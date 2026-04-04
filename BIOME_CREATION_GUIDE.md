# Biome Creation Guide

This guide will help you create stunning custom biomes for your Minecraft server!

## Quick Start

1. Open `plugins/CustomBiomes/config.yml`
2. Add a new biome under the `biomes:` section
3. Run `/reloadbiomes` in-game
4. Use `/setbiome your_biome_id` to apply it!

## Biome Template

Copy and paste this template for each new biome:

```yaml
  your_biome_id:                # Must be unique, lowercase, use underscores
    name: "Your Biome Name"     # Display name (can have spaces)
    fog_color: ""               # 6-digit hex color (no #)
    water_color: ""
    water_fog_color: ""
    sky_color: ""
    foliage_color: ""
    grass_color: ""
    temperature: 0.8            # -2.0 to 2.0
    rainfall: 0.5               # 0.0 to 1.0
```

## Understanding Colors

### Fog Color
- The color of atmospheric fog in the distance
- Creates the overall "feel" of the biome
- **Tip**: Match this to your sky for cohesive atmosphere

### Water Color
- The color of water blocks
- Very visible in oceans, rivers, and lakes
- **Tip**: Tropical = bright cyan, Swamp = dark murky green

### Water Fog Color
- The color you see when underwater
- Affects underwater visibility atmosphere
- **Tip**: Usually slightly darker than water_color

### Sky Color
- The color of the sky above
- Most visible at horizon
- **Tip**: This is KEY for sunset/sunrise vibes!

### Foliage Color
- Color of leaves, vines, and some plants
- Overrides natural biome tinting
- **Tip**: Make it different from grass for variety

### Grass Color
- Color of grass blocks
- Very visible in plains and hills
- **Tip**: Complement your foliage color

## Color Palette Ideas

### Tropical Paradise
```yaml
fog_color: "87CEEB"        # Sky blue
water_color: "00CED1"      # Dark turquoise
water_fog_color: "008B8B"  # Dark cyan
sky_color: "87CEEB"        # Sky blue
foliage_color: "32CD32"    # Lime green
grass_color: "7CFC00"      # Lawn green
```

### Spooky Haunted
```yaml
fog_color: "2F4F4F"        # Dark slate gray
water_color: "556B2F"      # Dark olive green
water_fog_color: "3C3C3C"  # Very dark gray
sky_color: "483D8B"        # Dark slate blue
foliage_color: "8B4513"    # Saddle brown
grass_color: "6B8E23"      # Olive drab
```

### Alien Planet
```yaml
fog_color: "00FF00"        # Lime
water_color: "FF00FF"      # Magenta
water_fog_color: "8B008B"  # Dark magenta
sky_color: "00FFFF"        # Cyan
foliage_color: "FF1493"    # Deep pink
grass_color: "00FF7F"      # Spring green
```

### Sunset Beach
```yaml
fog_color: "FF6347"        # Tomato red
water_color: "4682B4"      # Steel blue
water_fog_color: "5F9EA0"  # Cadet blue
sky_color: "FF69B4"        # Hot pink
foliage_color: "228B22"    # Forest green
grass_color: "90EE90"      # Light green
```

### Winter Wonderland
```yaml
fog_color: "F0F8FF"        # Alice blue
water_color: "B0E0E6"      # Powder blue
water_fog_color: "ADD8E6"  # Light blue
sky_color: "E0FFFF"        # Light cyan
foliage_color: "98FB98"    # Pale green
grass_color: "F0FFF0"      # Honeydew
temperature: -0.5          # Snow instead of rain!
```

## Temperature & Rainfall

### Temperature
- **Above 0.95**: Very hot, no rain (desert-like)
- **0.5 to 0.95**: Warm, normal weather
- **0.15 to 0.5**: Cool, temperate
- **Below 0.15**: Cold, snow instead of rain!
- **Below 0.0**: Freezing, ice forms on water

### Rainfall
- **0.0**: No precipitation at all
- **0.1-0.3**: Dry, rare rain
- **0.4-0.6**: Moderate precipitation
- **0.7-0.9**: Wet, frequent rain
- **1.0**: Constant rain (monsoon-like)

## Pro Tips

### 1. Start with a Theme
Think of a concept first:
- "Candy Land" → bright pinks, purples, pastels
- "Apocalyptic Wasteland" → grays, browns, dark reds
- "Underwater City" → blues, teals, cyan
- "Magic Forest" → purples, glowing colors

### 2. Use Color Theory
- **Complementary**: Orange sky + blue water = sunset
- **Analogous**: Purple + pink + blue = magical
- **Monochromatic**: Different shades of same color = cohesive

### 3. Test in Different Times
- Check your biome at dawn, noon, dusk, and night
- Colors look different in different lighting!

### 4. Keep Some Defaults
- You don't need to color everything
- Leave some colors empty ("") for vanilla feel
- Mix custom and vanilla for subtle changes

### 5. Find Hex Colors
Use these websites to pick perfect colors:
- https://htmlcolorcodes.com/color-picker/
- https://www.color-hex.com/
- https://coolors.co/ (generates palettes!)

### 6. Copy from Real Life
- Search "sunset hex colors"
- Look at tropical beach photos
- Find cyberpunk aesthetic palettes
- Screenshot video games you like

## Common Color Combinations

### Neon/Cyberpunk
- **Primary**: FF00FF, 00FFFF, FF0080
- **Sky**: 7209B7, 560BAD
- **Water**: 06FFF0, 4CC9F0
- **Plants**: F72585, FF006E

### Tropical
- **Primary**: 00CED1, 20B2AA, 48D1CC
- **Sky**: 87CEEB, ADD8E6
- **Water**: 00BFFF, 1E90FF
- **Plants**: 32CD32, 00FA9A, 7CFC00

### Fantasy/Magical
- **Primary**: 9370DB, 8B7AB8, BA55D3
- **Sky**: DDA0DD, EE82EE
- **Water**: 9966CC, 8A2BE2
- **Plants**: DA70D6, FF00FF

### Desert/Arid
- **Primary**: DEB887, D2B48C, F4A460
- **Sky**: FFD700, FFA500
- **Water**: CD853F, DAA520
- **Plants**: BDB76B, BC8F8F

## Example: Creating a "Lava Planet" Biome

```yaml
biomes:
  lava_planet:
    name: "Lava Planet"
    fog_color: "FF4500"        # Orange red fog
    water_color: "DC143C"      # Crimson lava-like water
    water_fog_color: "8B0000"  # Dark red underwater
    sky_color: "FF6347"        # Tomato red sky
    foliage_color: "B22222"    # Fire brick plants
    grass_color: "CD5C5C"      # Indian red grass
    temperature: 2.0           # Maximum heat!
    rainfall: 0.0              # No rain on a lava planet
```

## Troubleshooting

### Biome doesn't appear
- Check for YAML formatting errors (proper indentation)
- Make sure biome_id is unique
- Run `/reloadbiomes` after editing
- Check console for error messages

### Colors look wrong
- Remove the # symbol from hex codes
- Use exactly 6 characters (000000 to FFFFFF)
- Make sure they're under the right property name

### Can't see changes
- You need to relog or move away and back
- Use F3+F4 or relog to refresh chunks
- Make sure you're in the right chunk

## Share Your Creations!

Found an amazing color combo? Share it with the community!

## Need More Ideas?

Check out:
- https://colorhunt.co/ - Color palette inspiration
- https://www.design-seeds.com/ - Colors from photos
- Real Minecraft biome mods for ideas
- Pinterest "color palette" searches

Happy biome creating! 🎨
