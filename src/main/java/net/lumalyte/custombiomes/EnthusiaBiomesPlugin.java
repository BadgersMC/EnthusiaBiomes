package net.lumalyte.custombiomes;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main plugin class for EnthusiaBiomes
 * Allows users to create unlimited custom biomes via YAML configuration
 *
 * @author Lumalyte Network
 * @version 1.0.0
 */
public class EnthusiaBiomesPlugin extends JavaPlugin {

    private BiomeRegistry biomeRegistry;
    private CustomBiomeHandler biomeHandler;
    private BiomeCycler biomeCycler;

    @Override
    public void onEnable() {
        // Save default config if it doesn't exist
        saveDefaultConfig();

        getLogger().info("╔═══════════════════════════════════════╗");
        getLogger().info("║   EnthusiaBiomes - Enthusia Network  ║");
        getLogger().info("║   Loading configuration...            ║");
        getLogger().info("╚═══════════════════════════════════════╝");

        // Initialize biome registry
        biomeRegistry = new BiomeRegistry(this);
        biomeRegistry.loadBiomesFromConfig();

        // Initialize biome handler
        biomeHandler = new CustomBiomeHandler(this, biomeRegistry);

        // Register biomes if auto-register is enabled
        // Note: This MUST happen during onEnable, before worlds load
        if (getConfig().getBoolean("settings.auto_register", true)) {
            try {
                biomeHandler.registerAllBiomes();
                getLogger().info("Custom biomes registered successfully!");
                getLogger().info("Use /setbiome <biome_id> to apply biomes to chunks");
            } catch (Exception e) {
                getLogger().severe("Failed to register custom biomes!");
                getLogger().severe("Error: " + e.getMessage());
                e.printStackTrace();
            }
        }

        // Register commands
        BiomeCommand biomeCommand = new BiomeCommand(this, biomeRegistry, biomeHandler);
        getCommand("setbiome").setExecutor(biomeCommand);
        getCommand("listbiomes").setExecutor(biomeCommand);
        getCommand("reloadbiomes").setExecutor(biomeCommand);

        // Debug command to check registry
        RegistryCheckCommand registryCheckCommand = new RegistryCheckCommand(biomeRegistry);
        getCommand("checkbiomes").setExecutor(registryCheckCommand);

        // Initialize biome cycler
        biomeCycler = new BiomeCycler(this, biomeRegistry, biomeHandler);
        biomeCycler.loadConfig();

        // Biome cycling command
        BiomeCycleCommand biomeCycleCommand = new BiomeCycleCommand(this, biomeCycler);
        getCommand("biomecycle").setExecutor(biomeCycleCommand);
        getCommand("biomecycle").setTabCompleter(biomeCycleCommand);

        getLogger().info("Custom Biomes Plugin enabled successfully!");
    }

    @Override
    public void onDisable() {
        // Stop biome cycling
        if (biomeCycler != null) {
            biomeCycler.stop();
        }

        if (biomeRegistry != null) {
            biomeRegistry.clear();
        }

        getLogger().info("Custom Biomes Plugin disabled");
    }

    public BiomeRegistry getBiomeRegistry() {
        return biomeRegistry;
    }

    public CustomBiomeHandler getBiomeHandler() {
        return biomeHandler;
    }

    /**
     * Reloads the plugin configuration and re-registers biomes
     */
    public void reloadPlugin() {
        reloadConfig();
        biomeRegistry.loadBiomesFromConfig();

        if (getConfig().getBoolean("settings.auto_register", true)) {
            biomeHandler.registerAllBiomes();
        }

        // Reload biome cycler
        if (biomeCycler != null) {
            biomeCycler.stop();
            biomeCycler.loadConfig();
        }

        getLogger().info("Plugin reloaded successfully!");
    }

    public BiomeCycler getBiomeCycler() {
        return biomeCycler;
    }
}
