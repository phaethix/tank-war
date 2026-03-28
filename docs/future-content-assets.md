# Future Content Assets

## Purpose

This document records the reserved gameplay ideas, prepared art assets, and the source code used to generate them.

These features are not implemented yet. The goal is to make sure the ideas, filenames, and drawing approach are not lost.

Relevant source code:

- [generate_future_assets.py](/Users/huanyuli/github.com/tank-war/scripts/generate_future_assets.py)
- [generate_future_audio.py](/Users/huanyuli/github.com/tank-war/scripts/generate_future_audio.py)

## Reserved Gameplay Ideas

### Special weapons

- `src/main/resources/images/bullet/special/nuke_u.png`
  Use as a nuclear projectile or rare heavy shell.
  Possible rule: area explosion that destroys all tanks inside a radius.

### Heavy tanks

- `src/main/resources/images/tank/enemy/boss_tank_u.png`
  Enemy heavy tank.
  Possible rule: elite enemy that needs 3 hits to kill.

- `src/main/resources/images/tank/player/ally_boss_tank_u.png`
  Allied heavy tank.
  Possible rule: stronger player-side unit, summon unit, or late-game upgrade.

### Reward / punishment items

- `src/main/resources/images/items/positive/repair_kit.png`
  Possible rule: restore HP or grant one shield layer.

- `src/main/resources/images/items/negative/hazard_crate.png`
  Possible rule: trap pickup, damage on touch, slow debuff, or small explosion trigger.

### Terrain and battlefield

- `src/main/resources/images/terrain/ground/ground_tile.png`
  Base ground tile for the whole map.

- `src/main/resources/images/terrain/wall/brick_wall.png`
  Destructible wall.

- `src/main/resources/images/terrain/wall/steel_wall.png`
  Hard wall, resistant to normal bullets.

- `src/main/resources/images/terrain/ground/grass_tile.png`
  Decorative or semi-cover tile.

- `src/main/resources/images/terrain/ground/water_tile.png`
  Non-walkable area.

- `src/main/resources/images/terrain/ground/ice_tile.png`
  Slippery movement area.

- `src/main/resources/images/objectives/base.png`
  Classic protect-the-base objective.

### Support visuals / classic items

- `src/main/resources/images/effects/spawn/spawn_effect.png`
  Spawn animation for player or enemy entry.

- `src/main/resources/images/items/positive/shield_item.png`
  Temporary invulnerability item.

- `src/main/resources/images/items/special/bomb_item.png`
  Clear-screen or area-damage item.

- `src/main/resources/images/items/special/time_stop_item.png`
  Freeze enemy movement for a short duration.

- `src/main/resources/images/items/positive/life_item.png`
  Extra-life item.

### Reserved audio ideas

- `src/main/resources/audio/tank_fire.wav`
  Current player/default firing sound.

- `src/main/resources/audio/tank_move.wav`
  Current movement loop fragment.

- `src/main/resources/audio/explode.wav`
  Current normal explosion sound.

- `src/main/resources/audio/bgm.wav`
  Current main background music loop.

- `src/main/resources/audio/pickup.wav`
  Reward pickup sound.

- `src/main/resources/audio/boss_fire.wav`
  Heavy enemy firing sound for boss-style units.

- `src/main/resources/audio/giant_enemy_fire.wav`
  Dedicated giant enemy tank firing sound.
  Possible rule: use only for oversized or elite siege-class enemy tanks.

- `src/main/resources/audio/nuke_explode.wav`
  Dedicated nuclear explosion sound.
  Possible rule: use only for area-damage or map-shaking weapon events.

## Current Reserved Asset List

- `src/main/resources/images/bullet/special/nuke_u.png`
- `src/main/resources/images/tank/enemy/boss_tank_u.png`
- `src/main/resources/images/tank/player/ally_boss_tank_u.png`
- `src/main/resources/images/items/positive/repair_kit.png`
- `src/main/resources/images/items/negative/hazard_crate.png`
- `src/main/resources/images/terrain/ground/ground_tile.png`
- `src/main/resources/images/terrain/wall/brick_wall.png`
- `src/main/resources/images/terrain/wall/steel_wall.png`
- `src/main/resources/images/terrain/ground/grass_tile.png`
- `src/main/resources/images/terrain/ground/water_tile.png`
- `src/main/resources/images/terrain/ground/ice_tile.png`
- `src/main/resources/images/objectives/base.png`
- `src/main/resources/images/effects/spawn/spawn_effect.png`
- `src/main/resources/images/items/positive/shield_item.png`
- `src/main/resources/images/items/special/bomb_item.png`
- `src/main/resources/images/items/special/time_stop_item.png`
- `src/main/resources/images/items/positive/life_item.png`
- `src/main/resources/audio/tank_fire.wav`
- `src/main/resources/audio/tank_move.wav`
- `src/main/resources/audio/explode.wav`
- `src/main/resources/audio/bgm.wav`
- `src/main/resources/audio/pickup.wav`
- `src/main/resources/audio/boss_fire.wav`
- `src/main/resources/audio/giant_enemy_fire.wav`
- `src/main/resources/audio/nuke_explode.wav`

## Visual Direction

The reserved assets follow the same stylized top-down pixel-art language as the current tank sprites:

- strong silhouette first
- high contrast highlights
- readable shapes at small size
- heavy shapes for tanks and walls
- clear faction separation for player vs enemy

Faction distinction for heavy tanks:

- `tank/enemy/boss_tank_u.png`
  darker red body, heavier front detail, more threatening silhouette

- `tank/player/ally_boss_tank_u.png`
  blue/cyan body, brighter core, stronger badge-like center mark

## Background Recommendation

The current pure black background is too empty for this game style.

The better direction is:

1. Use `terrain/ground/ground_tile.png` as the base layer and repeat it across the battlefield.
2. Place walls, water, ice, grass, and the base on top of that layer.
3. Keep the map readable with strong color separation:
   - ground: muted green-brown
   - walls: warm brick or cold steel
   - water: saturated blue
   - ice: pale cyan
   - grass: vivid green

This will look much better than a flat black background and will make the battlefield feel like a real map.

For audio presentation:

1. Keep ordinary actions short and readable.
2. Use dedicated sounds for oversized weapons and elite enemies.
3. Keep the BGM loop longer than one small phrase so repetition feels less obvious.
4. Make special-event sounds rare and clearly distinguishable from normal combat sounds.

## How The Assets Were Drawn

These assets were generated by code instead of external drawing software.

The general drawing method is:

1. Create a transparent raster canvas.
2. Draw primitive shapes:
   - rectangles
   - circles / ellipses
   - diamonds
3. Layer shadows first.
4. Add armor plates, cores, highlights, and faction-colored details.
5. Write the final image as PNG directly.

This makes the style reproducible and easy to iterate later.

## How The Audio Was Designed

The audio was also generated by code instead of external sound libraries.

The general approach is:

1. Build short tones with square, triangle, and sine waves.
2. Add filtered noise for impact, grit, or air.
3. Layer multiple sound parts together:
   - transient click
   - main body
   - low tail
   - noise burst
4. Apply light post-processing:
   - low-pass smoothing
   - echo
   - tremolo where appropriate
5. Export the final result as mono WAV.

This makes it easy to keep a consistent arcade-like sound style across all effects.

## Source Code

The full reusable generator source code is stored here:

- [generate_future_assets.py](/Users/huanyuli/github.com/tank-war/scripts/generate_future_assets.py)
- [generate_future_audio.py](/Users/huanyuli/github.com/tank-war/scripts/generate_future_audio.py)

That script currently generates:

- heavy tanks
- special projectile
- support items
- terrain tiles
- base asset
- spawn effect

The audio generator currently produces:

- `src/main/resources/audio/tank_fire.wav`
- `src/main/resources/audio/tank_move.wav`
- `src/main/resources/audio/explode.wav`
- `src/main/resources/audio/bgm.wav`
- `src/main/resources/audio/pickup.wav`
- `src/main/resources/audio/boss_fire.wav`
- `src/main/resources/audio/giant_enemy_fire.wav`
- `src/main/resources/audio/nuke_explode.wav`

Key generated outputs from the script:

- `tank/enemy/boss_tank_u.png`
- `tank/player/ally_boss_tank_u.png`
- `bullet/special/nuke_u.png`
- `items/positive/repair_kit.png`
- `items/negative/hazard_crate.png`
- `terrain/ground/ground_tile.png`
- `terrain/wall/brick_wall.png`
- `terrain/ground/water_tile.png`
- `terrain/ground/ice_tile.png`
- `objectives/base.png`
- `effects/spawn/spawn_effect.png`
- `items/positive/shield_item.png`
- `items/special/bomb_item.png`
- `items/special/time_stop_item.png`
- `items/positive/life_item.png`

## Future Implementation Notes

When these features are implemented later, the likely next steps are:

1. Add config entries for reserved asset paths.
2. Add entity types such as `BossTank`, `Pickup`, `Obstacle`, or `SpecialBullet`.
3. Add HP rules, area-damage rules, and item effects.
4. Add tile-based map data for terrain placement.
5. Replace the current plain black background with a tiled ground layer.
6. Bind special audio to special entities and events such as giant enemy fire and nuclear explosions.

## Notes

- These assets are intentionally prepared ahead of implementation.
- They are future-content placeholders, not active gameplay content yet.
- This document should be updated whenever a new reserved asset or gameplay idea is added.
