# ✅ Custom Biomes Plugin - Build Successful!

## Build Information

**JAR Location:** `build/libs/CustomBiomes-1.0.0.jar`
**Size:** 23KB
**Version:** 1.0.0
**Minecraft Version:** 1.21.11 (Paper)
**Build System:** Gradle 9.1.0 + paperweight

## Installation

1. Copy `CustomBiomes-1.0.0.jar` to your server's `plugins/` folder
2. Start or restart your server
3. The plugin will generate `plugins/CustomBiomes/config.yml`
4. Customize biomes in the config and run `/reloadbiomes`

## Quick Start

### Commands
```
/setbiome vice_city_sunset       - Apply Vice City sunset to your chunk
/setbiome vice_city_sunset 5     - Apply to 5 chunk radius
/listbiomes                       - See all available biomes
/reloadbiomes                     - Reload config after changes
```

### Pre-loaded Biomes
1. **vice_city_sunset** - Miami/Florida sunset (orange/pink/teal)
2. **neon_cyberpunk** - Pink/cyan futuristic
3. **ocean_paradise** - Tropical turquoise waters
4. **mystic_forest** - Purple/blue magical
5. **desert_oasis** - Golden desert tones
6. **cherry_blossom** - Pink Japanese aesthetic
7. **volcanic_wasteland** - Dark crimson lava
8. **arctic_aurora** - Icy teal aurora

## Configuration

### Customize Namespace
Edit `config.yml`:
```yaml
settings:
  namespace: "myserver"  # Changes biome IDs from lumalyte:biome to myserver:biome
```

### Add New Biomes
```yaml
biomes:
  my_custom_biome:
    name: "My Custom Biome"
    fog_color: "FF6347"        # Hex colors (no # symbol)
    water_color: "1E90FF"
    water_fog_color: "4682B4"
    sky_color: "87CEEB"
    foliage_color: "32CD32"
    grass_color: "7CFC00"
    temperature: 0.8
    rainfall: 0.5
```

Then run `/reloadbiomes` - no code needed!

## Technical Details

- Uses paperweight 2.0.0-beta.19 for NMS access
- Based on TerraformGenerator's biome registration approach
- Supports 6 color properties per biome
- Dynamic loading from YAML configuration
- Reflection-based for handling obfuscated NMS fields
- Namespace validation and sanitization
- Full permission system

## Permissions

- `custombiomes.*` - All permissions (op)
- `custombiomes.setbiome` - Use /setbiome (op)
- `custombiomes.list` - Use /listbiomes (everyone)
- `custombiomes.reload` - Use /reloadbiomes (op)

## Documentation

- **README.md** - Full plugin documentation
- **BIOME_CREATION_GUIDE.md** - Tutorial for creating custom biomes

## Notes

- Players will see biomes as `namespace:biome_id` in F3 debug screen
- Colors use 6-digit hex format without # symbol
- Leave color fields empty ("") to use Minecraft defaults
- Temperature < 0.15 causes snow instead of rain
- Rainfall 0.0 = no precipitation

Enjoy creating unlimited custom biomes! 🎨
