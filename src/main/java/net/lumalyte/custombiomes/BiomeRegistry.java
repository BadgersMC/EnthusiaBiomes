package net.lumalyte.custombiomes;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.logging.Logger;

/**
 * Registry that manages all custom biomes loaded from configuration
 * Handles loading, validation, and storage of biome data
 */
public class BiomeRegistry {
    private final JavaPlugin plugin;
    private final Logger logger;
    private final Map<String, CustomBiomeData> biomes;

    public BiomeRegistry(JavaPlugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.biomes = new LinkedHashMap<>();
    }

    /**
     * Loads all biomes from the plugin's config.yml
     */
    public void loadBiomesFromConfig() {
        biomes.clear();

        FileConfiguration config = plugin.getConfig();
        ConfigurationSection biomesSection = config.getConfigurationSection("biomes");

        if (biomesSection == null) {
            logger.warning("No biomes section found in config.yml!");
            return;
        }

        Set<String> biomeIds = biomesSection.getKeys(false);
        logger.info("Loading " + biomeIds.size() + " custom biomes from configuration...");

        for (String biomeId : biomeIds) {
            try {
                CustomBiomeData biomeData = loadBiomeData(biomeId, biomesSection.getConfigurationSection(biomeId));
                if (biomeData != null) {
                    biomes.put(biomeId, biomeData);
                    if (config.getBoolean("settings.debug", false)) {
                        logger.info("Loaded biome: " + biomeData);
                    }
                }
            } catch (Exception e) {
                logger.warning("Failed to load biome '" + biomeId + "': " + e.getMessage());
                if (config.getBoolean("settings.debug", false)) {
                    e.printStackTrace();
                }
            }
        }

        logger.info("Successfully loaded " + biomes.size() + " custom biomes");
    }

    /**
     * Loads a single biome's data from a configuration section
     */
    private CustomBiomeData loadBiomeData(String id, ConfigurationSection section) {
        if (section == null) {
            logger.warning("Biome '" + id + "' has no configuration section");
            return null;
        }

        String name = section.getString("name", id);
        String fogColor = section.getString("fog_color", "");
        String waterColor = section.getString("water_color", "");
        String waterFogColor = section.getString("water_fog_color", "");
        String skyColor = section.getString("sky_color", "");
        String foliageColor = section.getString("foliage_color", "");
        String grassColor = section.getString("grass_color", "");

        float temperature = (float) section.getDouble("temperature", 0.8);
        float rainfall = (float) section.getDouble("rainfall", 0.5);

        // Validate temperature and rainfall ranges
        temperature = Math.max(-2.0f, Math.min(2.0f, temperature));
        rainfall = Math.max(0.0f, Math.min(1.0f, rainfall));

        return new CustomBiomeData(
            id, name, fogColor, waterColor, waterFogColor,
            skyColor, foliageColor, grassColor, temperature, rainfall
        );
    }

    /**
     * Get a biome by its ID
     */
    public CustomBiomeData getBiome(String id) {
        return biomes.get(id);
    }

    /**
     * Get all registered biome IDs
     */
    public Set<String> getBiomeIds() {
        return new LinkedHashSet<>(biomes.keySet());
    }

    /**
     * Get all registered biomes
     */
    public Collection<CustomBiomeData> getAllBiomes() {
        return new ArrayList<>(biomes.values());
    }

    /**
     * Check if a biome is registered
     */
    public boolean hasBiome(String id) {
        return biomes.containsKey(id);
    }

    /**
     * Get the number of registered biomes
     */
    public int size() {
        return biomes.size();
    }

    /**
     * Clear all registered biomes
     */
    public void clear() {
        biomes.clear();
    }

    /**
     * Get the configured namespace for biomes
     */
    public String getNamespace() {
        String namespace = plugin.getConfig().getString("settings.namespace", "lumalyte");

        // Validate and sanitize namespace
        if (namespace == null || namespace.isEmpty()) {
            logger.warning("Invalid namespace in config, using default 'lumalyte'");
            return "lumalyte";
        }

        // Convert to lowercase and replace spaces with underscores
        namespace = namespace.toLowerCase().trim().replace(" ", "_");

        // Ensure it only contains valid characters (a-z, 0-9, _, -)
        if (!namespace.matches("^[a-z0-9_-]+$")) {
            logger.warning("Namespace '" + namespace + "' contains invalid characters, using default 'lumalyte'");
            return "lumalyte";
        }

        return namespace;
    }
}
