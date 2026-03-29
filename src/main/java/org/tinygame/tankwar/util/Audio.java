package org.tinygame.tankwar.util;

import org.tinygame.tankwar.config.GameConfig;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 音频门面：业务层只表达“播放什么”，底层负责具体的播放策略与资源生命周期。
 */
public final class Audio {
    public static final SoundCue TANK_FIRE = SoundCue.of(GameConfig.CFG.audio.fire(), false);
    public static final SoundCue EXPLODE = SoundCue.of(GameConfig.CFG.audio.explode(), false);
    public static final SoundCue TANK_MOVE = SoundCue.of(GameConfig.CFG.audio.move(), false);
    public static final SoundCue BGM = SoundCue.of(GameConfig.CFG.audio.bgm(), true);

    private static final AudioEngine ENGINE = new AudioEngine();

    // JVM退出前清理循环播放Clip占用的底层音频资源，使用平台线程作为shutdown hook
    static {
        Runtime.getRuntime().addShutdownHook(
                Thread.ofPlatform().name("audio-shutdown").unstarted(ENGINE::shutdown)
        );
    }

    private Audio() {
    }

    public static void startBackgroundMusic() {
        ENGINE.play(BGM);
    }

    public static void shutdown() {
        ENGINE.shutdown();
    }

    public static void play(SoundCue cue) {
        ENGINE.play(cue);
    }

    public record SoundCue(String resourcePath, boolean looping) {
        public SoundCue {
            resourcePath = Objects.requireNonNull(resourcePath, "resourcePath");
        }

        public static SoundCue of(String resourcePath, boolean looping) {
            return new SoundCue(resourcePath, looping);
        }
    }

    // 限制实现类仅限内部两种，收口音频行为
    private sealed interface PlaybackStrategy permits LoopingPlayback, OneShotPlayback {
        static PlaybackStrategy of(boolean looping) {
            return switch (looping) {
                case true -> LoopingPlayback.INSTANCE;
                case false -> OneShotPlayback.INSTANCE;
            };
        }

        void play(String resourcePath, AudioEngine engine);
    }

    // 一次性音效策略：例如开火、爆炸，播放完就结束
    private enum OneShotPlayback implements PlaybackStrategy {
        INSTANCE;

        @Override
        public void play(String resourcePath, AudioEngine engine) {
            // 用虚拟线程异步播放短音效，避免阻塞游戏主流程
            Thread.ofVirtual()
                    .name("audio-sfx-", 0)
                    .start(() -> engine.playOneShot(resourcePath));
        }
    }

    // 循环音效策略：目前主要给背景音乐使用
    private enum LoopingPlayback implements PlaybackStrategy {
        INSTANCE;

        @Override
        public void play(String resourcePath, AudioEngine engine) {
            engine.playLoop(resourcePath);
        }
    }

    // Audio暴露简洁API，AudioEngine负责内部状态与资源管理
    private static final class AudioEngine {
        // 只缓存循环播放的音频；一次性音效播完即释放，不需要常驻
        private final Map<SoundCue, ClipHandle> loopingClips = new ConcurrentHashMap<>();

        void play(SoundCue cue) {
            PlaybackStrategy.of(cue.looping()).play(cue.resourcePath(), this);
        }

        void playOneShot(String resourcePath) {
            ClipHandle.open(resourcePath, false).ifPresent(ClipHandle::start);
        }

        @SuppressWarnings("resource")
        void playLoop(String resourcePath) {
            var cue = SoundCue.of(resourcePath, true);
            loopingClips.compute(cue, (_, existing) -> {
                if (existing == null || existing.isClosed()) {
                    return ClipHandle.open(resourcePath, true)
                            .map(handle -> {
                                handle.start();
                                return handle;
                            })
                            .orElse(null);
                }
                existing.start();
                return existing;
            });
        }

        void shutdown() {
            loopingClips.values().forEach(ClipHandle::close);
            loopingClips.clear();
        }
    }

    private record ClipHandle(Clip clip, boolean looping) implements AutoCloseable {
        private ClipHandle(Clip clip, boolean looping) {
            this.clip = clip;
            this.looping = looping;

            if (!looping) {
                // 一次性音效播放结束后自动关闭，避免积累底层音频句柄
                clip.addLineListener(this::closeWhenFinished);
            }
        }

        static Optional<ClipHandle> open(String resourcePath, boolean looping) {
            try (InputStream raw = Audio.class.getClassLoader().getResourceAsStream(resourcePath)) {
                Objects.requireNonNull(raw, () -> "Audio resource not found: " + resourcePath);

                try (AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(raw))) {
                    var clip = AudioSystem.getClip();
                    clip.open(stream);
                    return Optional.of(new ClipHandle(clip, looping));
                }
            } catch (Exception exception) {
                System.err.printf("Audio playback failed: %s - %s%n", resourcePath, exception.getMessage());
                return Optional.empty();
            }
        }

        synchronized void start() {
            if (looping) {
                if (clip.isRunning()) {
                    return;
                }
                clip.setFramePosition(0);
                clip.loop(Clip.LOOP_CONTINUOUSLY);
                clip.start();
                return;
            }
            clip.setFramePosition(0);
            clip.start();
        }

        synchronized boolean isClosed() {
            return !clip.isOpen();
        }

        private void closeWhenFinished(LineEvent event) {
            if (event.getType() == LineEvent.Type.STOP && clip.getFramePosition() >= clip.getFrameLength()) {
                close();
            }
        }

        @Override
        public synchronized void close() {
            if (!clip.isOpen()) {
                return;
            }
            clip.stop();
            clip.close();
        }
    }
}
