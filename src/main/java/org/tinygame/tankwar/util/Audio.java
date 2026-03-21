package org.tinygame.tankwar.util;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Objects;

/**
 * 音频工具类
 */
public final class Audio {
    public static final String TANK_FIRE = "audio/tank_fire.wav";
    public static final String EXPLODE = "audio/explode.wav";
    public static final String TANK_MOVE = "audio/tank_move.wav";
    public static final String BGM = "audio/bgm.wav";

    private static Clip bgmClip;
    private static boolean bgmPlaying;

    private Audio() {
    }

    public static void toggleBgm() {
        if (bgmClip == null) bgmClip = playClip(BGM, true);
        else if (bgmPlaying) bgmClip.stop();
        else                 bgmClip.start();
        bgmPlaying = !bgmPlaying;
    }

    public static void stopBgm() {
        if (bgmClip == null) return;
        bgmClip.stop();
        bgmClip.close();
        bgmClip = null;
    }

    // 异步播放一次性短音效（开火、爆炸等）
    public static void play(String resourcePath) {
        Thread.ofVirtual()
                .name("audio-sfx-", 0)
                .start(() -> playClip(resourcePath, false));
    }

    private static Clip playClip(String resourcePath, boolean loop) {
        try {
            InputStream raw = Audio.class.getClassLoader().getResourceAsStream(resourcePath);
            Objects.requireNonNull(raw, "Audio resource not found: " + resourcePath);

            AudioInputStream ais = AudioSystem.getAudioInputStream(new BufferedInputStream(raw));

            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            ais.close();

            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP && !loop) {
                    clip.close();
                }
            });

            if (loop) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            }
            clip.start();
            return clip;

        } catch (Exception e) {
            System.err.println("Audio playback failed: " + resourcePath + " - " + e.getMessage());
            return null;
        }
    }
}
