# Troubleshooting Guide

## Chunks Disappearing After /setbiome

**Fixed in latest version (1.0.0-reobf.jar)**

The issue was that chunks weren't being properly marked for saving and refreshing after biome modification.

### Solution Applied:
1. Added `setUnsaved(true)` call (with reflection fallback)
2. Added `world.refreshChunk()` to send updates to clients
3. Properly handle chunk modifications at NMS level

### How to Test:
1. Use `/setbiome neon_cyberpunk 2` in a small area first
2. Walk around the area - chunks should remain solid
3. Relog to verify chunks persist

---

## WorldEdit /biomelist Not Showing Custom Biomes

**Status: Investigating**

WorldEdit queries Bukkit's biome registry, which may not automatically include NMS-registered biomes.

### Possible Solutions:

#### Option 1: Check if biomes are actually registered
Run this command in-game:
```
/listbiomes
```
This uses our plugin's registry. If biomes show here but not in WorldEdit, it's a WorldEdit integration issue.

#### Option 2: Verify NMS registration
Enable debug mode in config.yml:
```yaml
settings:
  debug: true
```

Then check console for:
- "Unfreezing biome registry..."
- "Registered custom biome: vice_city_sunset"
- "Successfully registered X / 8 custom biomes"

#### Option 3: WorldEdit Compatibility
WorldEdit may cache biomes or use a different registry accessor. Try:
1. Restart server with plugin installed
2. Run `/we reload` if WorldEdit supports it
3. Check WorldEdit version compatibility

### Manual Biome Setting
Even if WorldEdit doesn't list them, you can still use:
```
/setbiome vice_city_sunset 5
```

This directly sets biomes using our plugin's registry.

---

## Biomes Not Registering on Startup

### Symptoms:
- No "Unfreezing biome registry" message in console
- No "Successfully registered" messages
- `/listbiomes` shows no biomes

### Solutions:

1. **Check auto_register setting:**
```yaml
settings:
  auto_register: true  # Must be true
```

2. **Enable debug logging:**
```yaml
settings:
  debug: true
```

3. **Check for errors in console:**
Look for exceptions during plugin load. Common issues:
- Wrong Paper version (need 1.21.8)
- Missing dependencies
- Permissions issues

4. **Verify config.yml syntax:**
- Use a YAML validator
- Check indentation (must be spaces, not tabs)
- Ensure hex colors are 6 characters (no # symbol)

---

## Colors Not Appearing Correctly

### Issue: Biomes load but colors don't match config

1. **Check hex color format:**
   - Must be exactly 6 characters
   - No # symbol
   - Valid hex (0-9, A-F)
   - Example: `"FF6B35"` not `"#FF6B35"`

2. **Empty colors use defaults:**
   - Empty string `""` uses Minecraft's default
   - This is intentional behavior

3. **Relog to see changes:**
   - Client caches biome colors
   - F3+T reloads resources (faster than relog)
   - Or move away and back

---

## Server Version Mismatch

### Error: `ClassNotFoundException` or `NoClassDefFoundError`

This means the plugin was compiled for a different Minecraft version.

**Current version:** 1.21.8

To rebuild for your version:
1. Edit `build.gradle`:
```gradle
paperweight.paperDevBundle('YOUR_VERSION-R0.1-SNAPSHOT')
```

2. Rebuild:
```bash
./gradlew clean reobfJar
```

3. Use the `-reobf.jar` file

---

## Performance Issues

### Large radius setbiome commands lag the server

The `/setbiome` command modifies chunks in real-time which can be intensive.

**Recommendations:**
- Use small radius (0-5) for testing
- Larger areas (10+) may cause lag spikes
- Consider using WorldEdit for very large areas once integration works
- Run during low-traffic times

**Optimization tips:**
- Reduce max radius in code if needed
- Add cooldown between commands
- Use async chunk loading (future feature)

---

## Need More Help?

1. **Enable debug mode** in config.yml
2. **Check console logs** for errors
3. **Verify Paper version** matches build version
4. **Test with small radius** first (radius 1-2)
5. **Share error logs** if reporting issues

### Useful Commands for Debugging:
```
/listbiomes          - Show all loaded biomes
/setbiome ID 0       - Test single chunk
/version CustomBiomes - Check plugin version
```
