#!/usr/bin/env python3
"""Generate reserved and classic tank-battle assets.

This script keeps the future-content asset generation logic in one place so the
art direction can be reproduced later without relying on shell history.
"""

from pathlib import Path
import struct
import zlib


OUT = Path("src/main/resources/images")


def chunk(tag: bytes, data: bytes) -> bytes:
    return (
        struct.pack(">I", len(data))
        + tag
        + data
        + struct.pack(">I", zlib.crc32(tag + data) & 0xFFFFFFFF)
    )


def write_png(path: Path, img: list[list[tuple[int, int, int, int]]]) -> None:
    height = len(img)
    width = len(img[0])
    raw = bytearray()
    for row in img:
        raw.append(0)
        for r, g, b, a in row:
            raw.extend((r, g, b, a))
    data = bytearray(b"\x89PNG\r\n\x1a\n")
    data += chunk(b"IHDR", struct.pack(">IIBBBBB", width, height, 8, 6, 0, 0, 0))
    data += chunk(b"IDAT", zlib.compress(bytes(raw), 9))
    data += chunk(b"IEND", b"")
    path.write_bytes(data)


class Canvas:
    def __init__(self, width: int, height: int) -> None:
        self.width = width
        self.height = height
        self.img = [[(0, 0, 0, 0) for _ in range(width)] for _ in range(height)]

    def set(self, x: int, y: int, color: tuple[int, int, int, int]) -> None:
        if 0 <= x < self.width and 0 <= y < self.height:
            self.img[y][x] = color

    def rect(self, x0: int, y0: int, x1: int, y1: int, color: tuple[int, int, int, int]) -> None:
        for y in range(y0, y1):
            for x in range(x0, x1):
                self.set(x, y, color)

    def circle(self, cx: int, cy: int, radius: int, color: tuple[int, int, int, int]) -> None:
        rr = radius * radius
        for y in range(cy - radius, cy + radius + 1):
            for x in range(cx - radius, cx + radius + 1):
                if 0 <= x < self.width and 0 <= y < self.height:
                    if (x - cx) * (x - cx) + (y - cy) * (y - cy) <= rr:
                        self.set(x, y, color)

    def ellipse(
        self, cx: int, cy: int, rx: int, ry: int, color: tuple[int, int, int, int]
    ) -> None:
        for y in range(cy - ry, cy + ry + 1):
            for x in range(cx - rx, cx + rx + 1):
                if 0 <= x < self.width and 0 <= y < self.height:
                    if ((x - cx) ** 2) / (rx * rx + 1e-9) + ((y - cy) ** 2) / (
                        ry * ry + 1e-9
                    ) <= 1:
                        self.set(x, y, color)

    def diamond(
        self, cx: int, cy: int, rx: int, ry: int, color: tuple[int, int, int, int]
    ) -> None:
        for y in range(cy - ry, cy + ry + 1):
            span = int(rx * (1 - abs(y - cy) / max(1, ry)))
            for x in range(cx - span, cx + span + 1):
                self.set(x, y, color)

    def outline(
        self, x0: int, y0: int, x1: int, y1: int, color: tuple[int, int, int, int], thickness: int = 1
    ) -> None:
        self.rect(x0, y0, x1, y0 + thickness, color)
        self.rect(x0, y1 - thickness, x1, y1, color)
        self.rect(x0, y0, x0 + thickness, y1, color)
        self.rect(x1 - thickness, y0, x1, y1, color)


def draw_super_tank(path: Path, palette: dict[str, tuple[int, int, int, int]], star: bool) -> None:
    c = Canvas(60, 60)
    shadow = (12, 14, 18, 255)
    c.ellipse(30, 38, 27, 21, (0, 0, 0, 42))

    c.rect(1, 4, 16, 59, shadow)
    c.rect(44, 4, 59, 59, shadow)
    c.rect(3, 6, 14, 57, palette["track"])
    c.rect(46, 6, 57, 57, palette["track"])
    for y in range(8, 56, 5):
        c.rect(4, y, 13, y + 2, palette["track_hi"])
        c.rect(47, y, 56, y + 2, palette["track_hi"])
    c.rect(13, 7, 20, 55, palette["side"])
    c.rect(40, 7, 47, 55, palette["side"])
    c.rect(14, 9, 19, 53, palette["side_hi"])
    c.rect(41, 9, 46, 53, palette["side_hi"])

    c.rect(14, 1, 46, 59, shadow)
    c.rect(15, 2, 45, 58, palette["body"])
    c.rect(17, 5, 43, 55, palette["body_hi"])
    c.rect(19, 10, 41, 51, palette["plate"])
    c.outline(15, 2, 45, 58, shadow)

    c.rect(17, 12, 24, 30, palette["armor"])
    c.rect(36, 12, 43, 30, palette["armor"])
    c.rect(18, 13, 23, 29, palette["armor_hi"])
    c.rect(37, 13, 42, 29, palette["armor_hi"])

    c.rect(23, 14, 37, 36, shadow)
    c.rect(24, 15, 36, 35, palette["core_shell"])
    c.rect(25, 16, 35, 34, palette["core_shell_hi"])
    c.circle(30, 25, 5, palette["core"])
    c.circle(30, 25, 2, palette["core_glow"])

    c.rect(24, 0, 36, 27, shadow)
    c.rect(25, 1, 35, 25, palette["barrel"])
    c.rect(21, 0, 39, 8, shadow)
    c.rect(22, 1, 38, 7, palette["barrel"])
    c.rect(22, 0, 38, 3, palette["muzzle"])
    c.rect(24, 0, 36, 1, palette["glint"])

    c.rect(21, 37, 39, 55, palette["body"])
    c.rect(23, 39, 37, 53, palette["accent"])
    c.rect(25, 41, 35, 51, palette["core"])

    for x in (21, 30, 39):
        c.circle(x, 45, 2, palette["muzzle"])

    if star:
        c.diamond(30, 46, 8, 5, palette["crest"])
        c.rect(29, 40, 31, 52, palette["crest_dark"])
        c.rect(24, 45, 36, 47, palette["crest_dark"])
    else:
        c.diamond(30, 46, 10, 5, palette["crest"])
        for x in (18, 22, 26, 34, 38, 42):
            c.rect(x, 50, x + 2, 55, palette["crest_dark"])

    c.rect(17, 1, 21, 10, shadow)
    c.rect(39, 1, 43, 10, shadow)
    c.rect(18, 2, 20, 9, palette["core"])
    c.rect(40, 2, 42, 9, palette["core"])

    c.rect(19, 10, 41, 11, (255, 255, 255, 85))
    c.rect(18, 33, 42, 34, palette["glint"])

    write_png(path, c.img)


def draw_nuke(path: Path) -> None:
    c = Canvas(30, 30)
    c.ellipse(15, 24, 5, 2, (0, 0, 0, 18))
    c.rect(11, 8, 19, 23, (48, 54, 66, 255))
    c.rect(12, 8, 18, 22, (104, 122, 150, 255))
    c.rect(13, 8, 17, 22, (214, 226, 242, 255))
    c.rect(10, 20, 20, 25, (44, 50, 58, 255))
    c.rect(11, 21, 19, 24, (126, 146, 176, 255))
    c.rect(9, 20, 11, 24, (72, 86, 102, 255))
    c.rect(19, 20, 21, 24, (72, 86, 102, 255))
    for y, span, col in [
        (3, 1, (255, 248, 210, 255)),
        (4, 2, (255, 216, 92, 255)),
        (5, 2, (255, 188, 48, 255)),
        (6, 3, (214, 126, 24, 255)),
        (7, 3, (214, 126, 24, 255)),
        (8, 3, (144, 84, 18, 255)),
    ]:
        c.rect(15 - span, y, 16 + span, y + 1, col)
    c.circle(15, 15, 4, (28, 32, 40, 255))
    c.circle(15, 15, 3, (230, 220, 86, 255))
    c.rect(14, 13, 16, 17, (28, 32, 40, 255))
    c.rect(12, 15, 18, 16, (28, 32, 40, 255))
    write_png(path, c.img)


def draw_repair_kit(path: Path) -> None:
    c = Canvas(48, 48)
    c.ellipse(24, 36, 17, 7, (0, 0, 0, 26))
    c.rect(10, 11, 38, 35, (42, 70, 46, 255))
    c.rect(12, 13, 36, 33, (92, 166, 98, 255))
    c.rect(18, 7, 30, 13, (42, 70, 46, 255))
    c.rect(19, 8, 29, 12, (146, 224, 156, 255))
    c.rect(21, 17, 27, 29, (238, 246, 240, 255))
    c.rect(17, 21, 31, 25, (238, 246, 240, 255))
    write_png(path, c.img)


def draw_hazard_crate(path: Path) -> None:
    c = Canvas(48, 48)
    c.ellipse(24, 36, 17, 7, (0, 0, 0, 26))
    c.rect(9, 10, 39, 38, (52, 42, 22, 255))
    c.rect(11, 12, 37, 36, (136, 92, 38, 255))
    for offset in range(-10, 11):
        x = 24 + offset
        y = 24 + offset
        for t in range(2):
            c.set(x + t, y, (250, 214, 78, 255))
            c.set(x + t, y + 1, (250, 214, 78, 255))
        y2 = 24 - offset
        for t in range(2):
            c.set(x + t, y2, (250, 214, 78, 255))
            c.set(x + t, y2 + 1, (250, 214, 78, 255))
    c.outline(11, 12, 37, 36, (74, 56, 26, 255))
    write_png(path, c.img)


def draw_steel_wall(path: Path) -> None:
    c = Canvas(60, 60)
    c.rect(0, 0, 60, 60, (36, 40, 46, 255))
    for y in range(0, 60, 20):
        c.rect(0, y, 60, y + 2, (86, 96, 108, 255))
    for x in range(0, 60, 20):
        c.rect(x, 0, x + 2, 60, (86, 96, 108, 255))
    for y in (10, 30, 50):
        for x in (10, 30, 50):
            c.circle(x, y, 2, (198, 206, 214, 255))
    write_png(path, c.img)


def draw_grass_tile(path: Path) -> None:
    c = Canvas(60, 60)
    c.rect(0, 0, 60, 60, (44, 92, 42, 255))
    blades = [
        (8, 50, 12, (126, 212, 98, 255)),
        (15, 44, 14, (90, 186, 72, 255)),
        (28, 52, 11, (126, 212, 98, 255)),
        (39, 47, 13, (90, 186, 72, 255)),
        (49, 54, 10, (126, 212, 98, 255)),
        (22, 36, 12, (110, 204, 88, 255)),
        (34, 24, 11, (126, 212, 98, 255)),
        (11, 21, 9, (110, 204, 88, 255)),
    ]
    for x, y, height, color in blades:
        for i in range(height):
            c.set(x, y - i, color)
            c.set(x + 1, y - i + (1 if i % 3 == 0 else 0), color)
            c.set(x - 1, y - i + (1 if i % 4 == 0 else 0), color)
    write_png(path, c.img)


def draw_ground_tile(path: Path) -> None:
    c = Canvas(60, 60)
    c.rect(0, 0, 60, 60, (66, 86, 58, 255))
    for y in range(0, 60, 6):
        for x in range(0, 60, 6):
            tone = (72 + ((x + y) // 6) % 2 * 8, 96 + (x // 6) % 2 * 6, 60 + (y // 6) % 2 * 6, 255)
            c.rect(x, y, x + 6, y + 6, tone)
    for x, y in [(8, 12), (19, 41), (33, 17), (46, 29), (50, 49), (12, 53), (27, 33)]:
        c.rect(x, y, x + 2, y + 1, (90, 108, 72, 255))
        c.rect(x + 1, y - 1, x + 2, y + 2, (58, 74, 50, 255))
    for x, y in [(14, 18), (38, 11), (25, 48), (43, 42)]:
        for i in range(6):
            c.set(x + i, y + i // 2, (98, 116, 78, 255))
            c.set(x + i, y + i // 2 + 1, (46, 60, 40, 255))
    write_png(path, c.img)


def draw_brick_wall(path: Path) -> None:
    c = Canvas(60, 60)
    c.rect(0, 0, 60, 60, (96, 50, 40, 255))
    brick_cols = [(170, 92, 66, 255), (154, 82, 60, 255), (188, 108, 76, 255)]
    for row in range(5):
        y = 2 + row * 11
        offset = 0 if row % 2 == 0 else 8
        x = -offset
        index = 0
        while x < 60:
            width = 16
            c.rect(max(0, x + 1), y, min(60, x + width - 1), y + 9, brick_cols[index % 3])
            c.outline(max(0, x + 1), y, min(60, x + width - 1), y + 9, (108, 54, 42, 255))
            x += 16
            index += 1
    for y in range(0, 60, 11):
        c.rect(0, y, 60, y + 2, (76, 40, 34, 255))
    for x, y in [(9, 8), (34, 14), (22, 35), (48, 43)]:
        c.rect(x, y, x + 3, y + 2, (220, 158, 122, 255))
    write_png(path, c.img)


def draw_water_tile(path: Path) -> None:
    c = Canvas(60, 60)
    c.rect(0, 0, 60, 60, (24, 78, 142, 255))
    for y in range(0, 60, 8):
        for x in range(0, 60, 12):
            c.rect(x + (y // 8) % 4, y + 2, x + 8 + (y // 8) % 4, y + 4, (116, 206, 255, 255))
            c.rect(x + 4 - ((y // 8) % 3), y + 5, x + 12 - ((y // 8) % 3), y + 6, (60, 164, 236, 255))
    for x, y in [(10, 13), (29, 24), (44, 11), (17, 42), (39, 47)]:
        c.circle(x, y, 1, (214, 246, 255, 255))
    write_png(path, c.img)


def draw_ice_tile(path: Path) -> None:
    c = Canvas(60, 60)
    c.rect(0, 0, 60, 60, (186, 234, 248, 255))
    for x in range(0, 60, 6):
        c.rect(x, 0, x + 1, 60, (220, 248, 255, 140))
    for y in range(0, 60, 6):
        c.rect(0, y, 60, y + 1, (220, 248, 255, 120))
    segments = [
        ((7, 15), (22, 7), (30, 14)),
        ((35, 16), (48, 10), (54, 18)),
        ((14, 38), (26, 30), (33, 42)),
        ((36, 46), (48, 35), (54, 49)),
    ]
    for points in segments:
        for (x1, y1), (x2, y2) in zip(points, points[1:]):
            steps = max(abs(x2 - x1), abs(y2 - y1)) + 1
            for i in range(steps):
                x = round(x1 + (x2 - x1) * i / (steps - 1))
                y = round(y1 + (y2 - y1) * i / (steps - 1))
                c.set(x, y, (132, 190, 220, 255))
                c.set(x + 1, y, (232, 250, 255, 180))
    write_png(path, c.img)


def draw_base(path: Path) -> None:
    c = Canvas(60, 60)
    c.rect(0, 0, 60, 60, (92, 66, 34, 255))
    c.rect(4, 4, 56, 56, (164, 124, 58, 255))
    c.outline(4, 4, 56, 56, (74, 52, 28, 255), 2)
    c.rect(12, 12, 48, 48, (116, 82, 40, 255))
    wing = (238, 206, 92, 255)
    body = (255, 230, 132, 255)
    beak = (242, 146, 44, 255)
    for y in range(20, 33):
        span = 12 - abs(26 - y)
        c.rect(18 - span, y, 24, y + 1, wing)
        c.rect(36, y, 42 + span, y + 1, wing)
    c.rect(26, 20, 34, 39, body)
    c.rect(24, 36, 36, 40, wing)
    c.rect(27, 18, 33, 22, body)
    c.rect(29, 15, 31, 18, beak)
    c.rect(20, 42, 40, 48, (86, 60, 30, 255))
    c.rect(22, 44, 38, 46, (210, 180, 92, 255))
    write_png(path, c.img)


def draw_spawn_effect(path: Path) -> None:
    c = Canvas(60, 60)
    for radius, color in [
        (24, (60, 220, 255, 60)),
        (18, (90, 236, 255, 90)),
        (12, (150, 248, 255, 120)),
        (6, (255, 255, 255, 180)),
    ]:
        c.circle(30, 30, radius, color)
    import math

    for angle in range(0, 360, 45):
        x2 = int(30 + math.cos(math.radians(angle)) * 24)
        y2 = int(30 + math.sin(math.radians(angle)) * 24)
        steps = max(abs(x2 - 30), abs(y2 - 30)) + 1
        for s in range(steps):
            x = round(30 + (x2 - 30) * s / (steps - 1))
            y = round(30 + (y2 - 30) * s / (steps - 1))
            c.set(x, y, (180, 250, 255, 180))
    write_png(path, c.img)


def draw_shield_item(path: Path) -> None:
    c = Canvas(48, 48)
    c.ellipse(24, 36, 16, 6, (0, 0, 0, 22))
    c.rect(12, 10, 36, 34, (38, 76, 130, 255))
    c.rect(14, 12, 34, 32, (96, 170, 238, 255))
    for y in range(12, 32):
        inset = abs(22 - y) // 4
        c.rect(14 + inset, y, 34 - inset, y + 1, (132, 214, 255, 255))
    c.circle(24, 22, 6, (236, 248, 255, 255))
    c.rect(22, 18, 26, 26, (78, 142, 220, 255))
    write_png(path, c.img)


def draw_bomb_item(path: Path) -> None:
    c = Canvas(48, 48)
    c.ellipse(24, 36, 15, 6, (0, 0, 0, 22))
    c.circle(24, 23, 12, (32, 36, 44, 255))
    c.circle(24, 23, 10, (76, 84, 96, 255))
    c.rect(21, 9, 27, 15, (126, 94, 44, 255))
    c.rect(22, 7, 26, 10, (238, 210, 102, 255))
    for x, y in [(16, 20), (30, 20), (20, 29), (28, 29)]:
        c.circle(x, y, 1, (214, 224, 232, 255))
    for i, (x, y) in enumerate([(28, 7), (31, 4), (34, 5), (36, 2)]):
        c.circle(x, y, 1, (255, 196 - 20 * i, 80, 255))
    write_png(path, c.img)


def draw_time_stop_item(path: Path) -> None:
    c = Canvas(48, 48)
    c.ellipse(24, 36, 16, 6, (0, 0, 0, 22))
    c.circle(24, 24, 12, (212, 190, 88, 255))
    c.circle(24, 24, 10, (248, 244, 226, 255))
    c.rect(21, 8, 27, 13, (212, 190, 88, 255))
    c.rect(19, 5, 29, 9, (120, 96, 44, 255))
    c.rect(23, 17, 25, 25, (72, 88, 112, 255))
    c.rect(24, 23, 30, 25, (72, 88, 112, 255))
    write_png(path, c.img)


def draw_life_item(path: Path) -> None:
    c = Canvas(48, 48)
    c.ellipse(24, 36, 16, 6, (0, 0, 0, 22))
    c.rect(10, 12, 38, 34, (22, 78, 104, 255))
    c.rect(12, 14, 36, 32, (82, 176, 212, 255))
    c.rect(21, 17, 27, 29, (255, 244, 246, 255))
    c.rect(17, 21, 31, 25, (255, 244, 246, 255))
    write_png(path, c.img)


def main() -> None:
    OUT.mkdir(parents=True, exist_ok=True)

    enemy_palette = {
        "track": (74, 44, 52, 255),
        "track_hi": (164, 98, 112, 255),
        "side": (84, 22, 34, 255),
        "side_hi": (136, 58, 76, 255),
        "body": (52, 18, 28, 255),
        "body_hi": (118, 44, 60, 255),
        "plate": (178, 84, 92, 255),
        "armor": (82, 18, 28, 255),
        "armor_hi": (210, 124, 118, 255),
        "core_shell": (92, 24, 34, 255),
        "core_shell_hi": (236, 176, 140, 255),
        "core": (255, 220, 190, 255),
        "core_glow": (255, 250, 238, 255),
        "barrel": (106, 98, 104, 255),
        "muzzle": (255, 188, 72, 255),
        "glint": (255, 226, 160, 255),
        "accent": (246, 150, 96, 255),
        "crest": (255, 136, 96, 255),
        "crest_dark": (88, 14, 18, 255),
    }

    ally_palette = {
        "track": (28, 68, 88, 255),
        "track_hi": (108, 194, 228, 255),
        "side": (16, 48, 66, 255),
        "side_hi": (56, 122, 154, 255),
        "body": (18, 30, 46, 255),
        "body_hi": (42, 92, 126, 255),
        "plate": (90, 184, 220, 255),
        "armor": (20, 42, 64, 255),
        "armor_hi": (122, 212, 236, 255),
        "core_shell": (22, 52, 76, 255),
        "core_shell_hi": (154, 226, 246, 255),
        "core": (214, 248, 255, 255),
        "core_glow": (255, 255, 255, 255),
        "barrel": (108, 122, 132, 255),
        "muzzle": (255, 196, 88, 255),
        "glint": (214, 248, 255, 255),
        "accent": (88, 236, 255, 255),
        "crest": (255, 226, 112, 255),
        "crest_dark": (28, 74, 96, 255),
    }

    draw_super_tank(OUT / "tank/enemy/boss_tank_u.png", enemy_palette, star=False)
    draw_super_tank(OUT / "ally_tank/enemy/boss_tank_u.png", ally_palette, star=True)
    draw_nuke(OUT / "bullet/special/nuke_u.png")
    draw_repair_kit(OUT / "items/positive/repair_kit.png")
    draw_hazard_crate(OUT / "items/negative/hazard_crate.png")
    draw_steel_wall(OUT / "steelWall.png")
    draw_grass_tile(OUT / "grassTile.png")

    draw_ground_tile(OUT / "terrain/ground/ground_tile.png")
    draw_brick_wall(OUT / "terrain/wall/brick_wall.png")
    draw_water_tile(OUT / "terrain/ground/water_tile.png")
    draw_ice_tile(OUT / "terrain/ground/ice_tile.png")
    draw_base(OUT / "objectives/base.png")
    draw_spawn_effect(OUT / "effects/spawn/spawn_effect.png")
    draw_shield_item(OUT / "items/positive/shield_item.png")
    draw_bomb_item(OUT / "items/special/bomb_item.png")
    draw_time_stop_item(OUT / "items/special/time_stop_item.png")
    draw_life_item(OUT / "items/positive/life_item.png")


if __name__ == "__main__":
    main()
