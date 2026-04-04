# Biome Cycling Feature

The Custom Biomes Plugin now includes an automatic biome cycling system that smoothly transitions your hub world through different atmospheric themes on a configurable timer.

## Overview

Biome cycling allows your world to automatically change its visual atmosphere (sky colors, water colors, grass colors, etc.) by cycling through a list of configured biomes at regular intervals. This creates a dynamic, ever-changing environment perfect for hub worlds.

## Configuration

Add the following section to your `config.yml`:

```yaml
biome_cycling:
  # Enable automatic biome cycling
  enabled: false

  # Target world to apply cycling to
  world: "world"

  # Time between biome changes (in minutes)
  interval_minutes: 10

  # List of biomes to cycle through (must be valid biome IDs from your biomes section)
  biome_cycle:
    - vice_city_sunset
    - neon_cyberpunk
    - ocean_paradise
    - mystic_forest
    - desert_oasis
    - cherry_blossom
    - volcanic_wasteland
    - arctic_aurora

  # Only apply to loaded chunks (recommended for performance)
  only_loaded_chunks: true

  # Announce biome changes to players in the world
  announce_changes: true

  # Message sent to players when biome changes
  # Use {biome} placeholder for the biome name
  change_message: "§7The world atmosphere shifts to §e{biome}§7..."
```

## How It Works

1. **Automatic Transitions**: Every X minutes (configurable), the plugin automatically applies the next biome in the cycle list to your target world
2. **Instant Visual Updates**: Players see the color changes immediately on their screen - no relogging required
3. **Seamless Loop**: When the cycle reaches the last biome, it loops back to the first
4. **Performance Optimized**: By default, only applies to loaded chunks for optimal performance

## Commands

### `/biomecycle` or `/biomecycle status`
Shows the current cycling status, including:
- Current biome
- Next biome
- Time until next change
- Cycle list

**Permission**: `custombiomes.cycle`

### `/biomecycle start`
Manually starts the biome cycling if it's enabled in config but not currently running.

**Permission**: `custombiomes.cycle`

### `/biomecycle stop`
Stops the automatic biome cycling.

**Permission**: `custombiomes.cycle`

### `/biomecycle skip`
Immediately skips to the next biome in the cycle without waiting for the timer.

**Permission**: `custombiomes.cycle`

## Example Output

When biome cycling is active, you'll see:

```
[Server] Biome cycle: Applied 'Vice City Sunset' to 247 chunks
[Server] Next biome: 'Neon Cyberpunk' in 10 minutes
```

Players in the world will see:
```
§7The world atmosphere shifts to §eVice City Sunset§7...
```

## Setup Instructions

1. **Configure Your Biomes**: First, make sure you have multiple biomes defined in your `config.yml`

2. **Enable Cycling**: Set `biome_cycling.enabled: true` in your config

3. **Choose Your World**: Set `world:` to the name of your hub world (e.g., "world", "hub", "lobby")

4. **Set Interval**: Choose how many minutes between transitions (e.g., `interval_minutes: 10`)

5. **Build Your Cycle**: List the biome IDs you want to cycle through in the `biome_cycle:` section

6. **Reload or Restart**: Run `/reloadbiomes` or restart your server

The cycling will start automatically on server startup if enabled!

## Performance Considerations

- **Loaded Chunks Only**: By default (`only_loaded_chunks: true`), the plugin only modifies chunks that are currently loaded in memory. This is fast and safe.

- **Chunk Count**: The number of chunks modified depends on your server's view distance and how many players are online. A typical hub with 10-20 players might have 200-300 loaded chunks.

- **Processing Time**: Each cycle typically takes 50-150ms to complete - this is extremely lightweight and won't cause any server lag.

- **Visual Updates**: Colors update instantly for all players in the world, creating a stunning visual effect.

## Use Cases

### Dynamic Hub World
Create an ever-changing hub atmosphere:
```yaml
biome_cycle:
  - vice_city_sunset    # Sunset vibes
  - mystic_forest       # Magical purple
  - neon_cyberpunk      # Night city
  - arctic_aurora       # Cool morning
```

### Theme Park Worlds
Cycle through different themed areas every few minutes to create excitement.

### Event Servers
Build anticipation by having the world transition through increasingly intense biomes leading up to an event.

### Seasonal Rotation
Change the atmosphere throughout the day to match real-world time or in-game events.

## Technical Details

The biome cycling system uses the same instant-update NMS approach as the `/setbiome` command:

1. Retrieves the biome holder from the NMS registry
2. Applies the biome to all loaded chunks (or all chunks if configured)
3. Marks chunks as modified for persistence
4. Refreshes chunks for immediate visual feedback
5. Broadcasts custom message to players (optional)

This ensures:
- **Instant visual feedback** - players see changes immediately
- **Persistence** - biome changes survive server restarts
- **Performance** - minimal impact on server TPS

## Troubleshooting

**Biomes not cycling**:
- Check that `enabled: true` in config
- Verify the world name is correct
- Ensure at least 2 valid biomes are in the cycle list
- Check server console for error messages

**Some biomes missing from cycle**:
- Verify the biome IDs match exactly (case-sensitive)
- Check that all biomes are properly registered on startup
- Use `/listbiomes` to see available biomes

**Players not seeing changes**:
- This shouldn't happen - changes are instant
- If it does, check they're actually in the target world
- Verify `announce_changes: true` to confirm cycles are happening

**Performance issues**:
- Ensure `only_loaded_chunks: true`
- Increase `interval_minutes` for less frequent changes
- Reduce the number of biomes in the cycle

## Version Information

- **Added in**: CustomBiomes v1.0.0
- **Minecraft Version**: 1.21.8+
- **Dependencies**: None (built into the plugin)
