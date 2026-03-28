#!/usr/bin/env python3
"""Generate arcade-style sound effects and music for Tank War."""

from __future__ import annotations

import math
import random
import struct
import wave
from pathlib import Path


SAMPLE_RATE = 44100
OUT = Path("src/main/resources/audio")


def clamp(value: float, lo: float = -1.0, hi: float = 1.0) -> float:
    return max(lo, min(hi, value))


def envelope(time_s: float, attack: float, decay: float, sustain: float, release: float, total: float) -> float:
    if time_s < 0 or time_s > total:
        return 0.0
    if time_s < attack:
        return time_s / max(attack, 1e-9)
    time_s -= attack
    if time_s < decay:
        start = 1.0
        end = sustain
        return start + (end - start) * (time_s / max(decay, 1e-9))
    if time_s < total - attack - decay - release:
        return sustain
    tail = total - attack - decay - time_s
    return sustain * max(tail, 0.0) / max(release, 1e-9)


def mix(parts: list[list[float]]) -> list[float]:
    length = max((len(p) for p in parts), default=0)
    out = [0.0] * length
    for part in parts:
        for i, sample in enumerate(part):
            out[i] += sample
    peak = max((abs(v) for v in out), default=1.0)
    scale = 0.92 / peak if peak > 0.92 else 1.0
    return [clamp(v * scale) for v in out]


def low_pass(samples: list[float], amount: float = 0.18) -> list[float]:
    if not samples:
        return []
    out = []
    last = samples[0]
    for sample in samples:
        last = last + (sample - last) * amount
        out.append(last)
    return out


def echo(samples: list[float], delay: float, decay: float, repeats: int = 2) -> list[float]:
    delay_n = int(delay * SAMPLE_RATE)
    out = samples[:]
    target_len = len(samples) + delay_n * repeats
    if len(out) < target_len:
        out.extend([0.0] * (target_len - len(out)))
    for rep in range(1, repeats + 1):
        gain = decay ** rep
        start = delay_n * rep
        for i, sample in enumerate(samples):
            out[start + i] += sample * gain
    peak = max((abs(v) for v in out), default=1.0)
    scale = 0.92 / peak if peak > 0.92 else 1.0
    return [clamp(v * scale) for v in out]


def tremolo(samples: list[float], hz: float, depth: float) -> list[float]:
    out: list[float] = []
    for i, sample in enumerate(samples):
        t = i / SAMPLE_RATE
        mod = 1.0 - depth + depth * ((math.sin(2.0 * math.pi * hz * t) + 1.0) * 0.5)
        out.append(sample * mod)
    return out


def write_wav(path: Path, samples: list[float]) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    with wave.open(str(path), "wb") as wf:
        wf.setnchannels(1)
        wf.setsampwidth(2)
        wf.setframerate(SAMPLE_RATE)
        frames = bytearray()
        for sample in samples:
            frames.extend(struct.pack("<h", int(clamp(sample) * 32767)))
        wf.writeframes(bytes(frames))


def tone(
    freq_start: float,
    duration: float,
    volume: float,
    wave_type: str = "square",
    freq_end: float | None = None,
    vibrato_hz: float = 0.0,
    vibrato_depth: float = 0.0,
    attack: float = 0.004,
    decay: float = 0.08,
    sustain: float = 0.4,
    release: float = 0.05,
) -> list[float]:
    count = int(SAMPLE_RATE * duration)
    freq_end = freq_start if freq_end is None else freq_end
    samples: list[float] = []
    phase = 0.0
    for i in range(count):
        t = i / SAMPLE_RATE
        progress = t / max(duration, 1e-9)
        freq = freq_start + (freq_end - freq_start) * progress
        if vibrato_hz and vibrato_depth:
            freq *= 1.0 + math.sin(2.0 * math.pi * vibrato_hz * t) * vibrato_depth
        phase += 2.0 * math.pi * freq / SAMPLE_RATE
        if wave_type == "square":
            raw = 1.0 if math.sin(phase) >= 0 else -1.0
        elif wave_type == "triangle":
            raw = 2.0 * abs(2.0 * ((phase / (2.0 * math.pi)) % 1.0) - 1.0) - 1.0
        elif wave_type == "sine":
            raw = math.sin(phase)
        else:
            raw = 2.0 * ((phase / (2.0 * math.pi)) % 1.0) - 1.0
        env = envelope(t, attack, decay, sustain, release, duration)
        samples.append(raw * env * volume)
    return samples


def noise(duration: float, volume: float, attack: float = 0.001, decay: float = 0.08, sustain: float = 0.15,
          release: float = 0.1, color: float = 0.55) -> list[float]:
    count = int(SAMPLE_RATE * duration)
    samples: list[float] = []
    last = 0.0
    for i in range(count):
        t = i / SAMPLE_RATE
        raw = random.uniform(-1.0, 1.0)
        last = last * color + raw * (1.0 - color)
        env = envelope(t, attack, decay, sustain, release, duration)
        samples.append(last * env * volume)
    return samples


def silence(duration: float) -> list[float]:
    return [0.0] * int(SAMPLE_RATE * duration)


def concat(parts: list[list[float]]) -> list[float]:
    out: list[float] = []
    for part in parts:
        out.extend(part)
    return out


def tank_fire() -> list[float]:
    body = tone(1240, 0.085, 0.46, wave_type="square", freq_end=360, sustain=0.16, release=0.022)
    click = tone(2200, 0.024, 0.14, wave_type="square", freq_end=1600, attack=0.0, decay=0.01, sustain=0.08, release=0.01)
    snap = noise(0.038, 0.18, decay=0.02, sustain=0.04, release=0.018, color=0.08)
    tail = tone(220, 0.07, 0.18, wave_type="triangle", freq_end=120, attack=0.0, decay=0.025, sustain=0.08, release=0.02)
    return low_pass(mix([body, click, snap, tail]), 0.22)


def boss_fire() -> list[float]:
    body = tone(380, 0.24, 0.56, wave_type="square", freq_end=95, sustain=0.22, release=0.08)
    sub = tone(104, 0.28, 0.34, wave_type="sine", freq_end=58, attack=0.0, decay=0.08, sustain=0.24, release=0.08)
    metal = tone(760, 0.06, 0.12, wave_type="triangle", freq_end=260, attack=0.0, decay=0.03, sustain=0.08, release=0.02)
    snap = noise(0.07, 0.18, decay=0.035, sustain=0.05, release=0.03, color=0.22)
    return low_pass(echo(mix([body, sub, metal, snap]), 0.045, 0.35, repeats=1), 0.16)


def giant_enemy_fire() -> list[float]:
    warning = tone(210, 0.09, 0.08, wave_type="triangle", freq_end=170, vibrato_hz=9.0, vibrato_depth=0.04,
                   attack=0.0, decay=0.03, sustain=0.12, release=0.03)
    cannon = tone(260, 0.32, 0.62, wave_type="square", freq_end=72, attack=0.0, decay=0.09, sustain=0.24, release=0.1)
    sub = tone(82, 0.38, 0.38, wave_type="sine", freq_end=34, attack=0.0, decay=0.11, sustain=0.24, release=0.12)
    hiss = noise(0.11, 0.12, decay=0.04, sustain=0.06, release=0.04, color=0.3)
    debris = noise(0.18, 0.18, decay=0.06, sustain=0.08, release=0.05, color=0.12)
    tail = tone(140, 0.18, 0.14, wave_type="triangle", freq_end=58, attack=0.0, decay=0.06, sustain=0.12, release=0.05)
    return low_pass(echo(mix([warning, cannon, sub, hiss, debris, tail]), 0.06, 0.42, repeats=2), 0.14)


def pickup() -> list[float]:
    return low_pass(concat([
        tone(700, 0.08, 0.34, wave_type="triangle", freq_end=900, decay=0.03, sustain=0.18, release=0.03),
        tone(1100, 0.09, 0.38, wave_type="triangle", freq_end=1450, decay=0.03, sustain=0.18, release=0.04),
        tone(1600, 0.11, 0.34, wave_type="sine", freq_end=1850, decay=0.04, sustain=0.16, release=0.05),
    ]), 0.28)


def tank_move() -> list[float]:
    beat = mix([
        tone(124, 0.05, 0.16, wave_type="triangle", freq_end=96, attack=0.0, decay=0.02, sustain=0.1, release=0.018),
        noise(0.032, 0.04, decay=0.015, sustain=0.04, release=0.01, color=0.42),
    ])
    return low_pass(concat([beat, silence(0.032), beat, silence(0.038), beat]), 0.24)


def explode() -> list[float]:
    punch = tone(320, 0.08, 0.24, wave_type="square", freq_end=180, attack=0.0, decay=0.03, sustain=0.08, release=0.02)
    boom = tone(190, 0.24, 0.34, wave_type="triangle", freq_end=52, attack=0.0, decay=0.08, sustain=0.14, release=0.08)
    crack = noise(0.16, 0.42, decay=0.05, sustain=0.08, release=0.05, color=0.04)
    shards = tone(980, 0.09, 0.12, wave_type="triangle", freq_end=420, attack=0.0, decay=0.035, sustain=0.08, release=0.025)
    air = noise(0.24, 0.1, decay=0.1, sustain=0.08, release=0.06, color=0.65)
    return echo(mix([punch, boom, crack, shards, air]), 0.035, 0.2, repeats=1)


def nuke_explode() -> list[float]:
    flash = tone(880, 0.07, 0.12, wave_type="square", freq_end=260, attack=0.0, decay=0.025, sustain=0.06, release=0.015)
    main_blast = tone(150, 0.58, 0.42, wave_type="triangle", freq_end=24, attack=0.0, decay=0.18, sustain=0.22, release=0.18)
    sub = tone(56, 0.72, 0.26, wave_type="sine", freq_end=18, attack=0.0, decay=0.22, sustain=0.26, release=0.2)
    shock = noise(0.34, 0.3, decay=0.12, sustain=0.18, release=0.08, color=0.08)
    debris = noise(0.62, 0.14, decay=0.24, sustain=0.2, release=0.18, color=0.72)
    siren = tone(310, 0.36, 0.08, wave_type="triangle", freq_end=180, vibrato_hz=7.0, vibrato_depth=0.08,
                 attack=0.0, decay=0.08, sustain=0.2, release=0.08)
    return low_pass(echo(mix([flash, main_blast, sub, shock, debris, siren]), 0.09, 0.38, repeats=2), 0.18)


def bgm_note(freq: float, duration: float, volume: float, wave_type: str = "square") -> list[float]:
    return tone(freq, duration, volume, wave_type=wave_type, attack=0.005, decay=0.03, sustain=0.65, release=0.04)


def bgm_track() -> list[float]:
    def render_phrase(notes: list[float], note_duration: float, gap: float, volume: float,
                      wave_type: str) -> list[float]:
        phrase: list[float] = []
        for freq in notes:
            if freq <= 0:
                phrase.extend(silence(note_duration + gap))
            else:
                phrase.extend(bgm_note(freq, note_duration, volume, wave_type=wave_type))
                phrase.extend(silence(gap))
        return phrase

    phrase_a = [
        392.0, 440.0, 493.88, 587.33, 523.25, 493.88, 440.0, 392.0,
        440.0, 493.88, 523.25, 659.25, 587.33, 523.25, 493.88, 440.0,
    ]
    phrase_b = [
        523.25, 587.33, 659.25, 783.99, 659.25, 587.33, 523.25, 493.88,
        440.0, 493.88, 523.25, 587.33, 523.25, 493.88, 440.0, 392.0,
    ]
    phrase_c = [
        392.0, 0.0, 392.0, 440.0, 493.88, 0.0, 523.25, 587.33,
        659.25, 587.33, 523.25, 493.88, 440.0, 392.0, 349.23, 392.0,
    ]

    lead = concat([
        render_phrase(phrase_a, 0.19, 0.018, 0.17, "square"),
        render_phrase(phrase_b, 0.19, 0.018, 0.17, "square"),
        render_phrase(phrase_c, 0.18, 0.022, 0.16, "triangle"),
        render_phrase(phrase_b, 0.19, 0.018, 0.18, "square"),
    ])

    bass_notes = [
        196.0, 196.0, 220.0, 220.0, 174.61, 174.61, 196.0, 196.0,
        261.63, 261.63, 220.0, 220.0, 174.61, 174.61, 164.81, 164.81,
    ] * 2
    bass = render_phrase(bass_notes, 0.42, 0.0, 0.105, "triangle")

    pad: list[float] = []
    chord_roots = [196.0, 220.0, 174.61, 196.0, 261.63, 220.0, 174.61, 164.81] * 2
    for root in chord_roots:
        chord = mix([
            tone(root, 0.86, 0.05, wave_type="sine", sustain=0.8, release=0.08),
            tone(root * 1.25, 0.86, 0.032, wave_type="sine", sustain=0.8, release=0.08),
            tone(root * 1.5, 0.86, 0.02, wave_type="sine", sustain=0.8, release=0.08),
        ])
        pad.extend(chord)

    counter_notes = [
        261.63, 293.66, 329.63, 293.66, 349.23, 392.0, 349.23, 293.66,
        329.63, 349.23, 392.0, 440.0, 392.0, 349.23, 329.63, 293.66,
    ] * 2
    counter = render_phrase(counter_notes, 0.12, 0.09, 0.05, "sine")

    drum_hit = mix([
        tone(95, 0.065, 0.095, wave_type="triangle", freq_end=52, attack=0.0, decay=0.028, sustain=0.05, release=0.02),
        noise(0.032, 0.052, decay=0.014, sustain=0.03, release=0.01, color=0.12),
    ])
    snare = mix([
        noise(0.05, 0.08, decay=0.022, sustain=0.03, release=0.014, color=0.08),
        tone(220, 0.03, 0.035, wave_type="triangle", freq_end=160, attack=0.0, decay=0.012, sustain=0.03, release=0.008),
    ])
    drums: list[float] = []
    for index in range(32):
        hit = drum_hit if index % 4 in (0, 2) else snare
        drums.extend(hit)
        drums.extend(silence(0.17 if index % 8 != 7 else 0.22))

    track = mix([lead, bass, pad, counter, drums])
    track = tremolo(track, 3.2, 0.08)
    return low_pass(track, 0.12)


def main() -> None:
    random.seed(42)
    OUT.mkdir(parents=True, exist_ok=True)
    write_wav(OUT / "tank_fire.wav", tank_fire())
    write_wav(OUT / "boss_fire.wav", boss_fire())
    write_wav(OUT / "giant_enemy_fire.wav", giant_enemy_fire())
    write_wav(OUT / "pickup.wav", pickup())
    write_wav(OUT / "tank_move.wav", tank_move())
    write_wav(OUT / "explode.wav", explode())
    write_wav(OUT / "nuke_explode.wav", nuke_explode())
    write_wav(OUT / "bgm.wav", bgm_track())


if __name__ == "__main__":
    main()
