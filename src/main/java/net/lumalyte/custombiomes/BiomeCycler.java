package net.lumalyte.custombiomes;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Handles automatic biome cycling for hub worlds
 * Smoothly transitions between configured biomes on a timer
 */
public class BiomeCycler {

    private final JavaPlugin plugin;
    private final Logger logger;
    private final BiomeRegistry biomeRegistry;
    private final CustomBiomeHandler biomeHandler;

    private BukkitTask cycleTask;
    private int currentBiomeIndex = 0;
    private List<String> cycleList;
    private String targetWorldName;
    private int intervalMinutes;
    private boolean enabled;

    public BiomeCycler(JavaPlugin plugin, BiomeRegistry biomeRegistry, CustomBiomeHandler biomeHandler) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.biomeRegistry = biomeRegistry;
        this.biomeHandler = biomeHandler;
        this.cycleList = new ArrayList<>();
    }

    /**
     * Loads configuration and starts cycling if enabled
     */
    public void loadConfig() {
        enabled = plugin.getConfig().getBoolean("biome_cycling.enabled", false);
        targetWorldName = plugin.getConfig().getString("biome_cycling.world", "world");
        intervalMinutes = plugin.getConfig().getInt("biome_cycling.interval_minutes", 10);

        // Load cycle list
        cycleList = plugin.getConfig().getStringList("biome_cycling.biome_cycle");

        // Validate biomes in cycle list
        List<String> validBiomes = new ArrayList<>();
        for (String biomeId : cycleList) {
            if (biomeRegistry.hasBiome(biomeId)) {
                validBiomes.add(biomeId);
            } else {
                logger.warning("Biome '" + biomeId + "' in cycle list not found, skipping");
            }
        }
        cycleList = validBiomes;

        if (enabled) {
            if (cycleList.isEmpty()) {
                logger.warning("Biome cycling enabled but no valid biomes in cycle list!");
                enabled = false;
            } else if (cycleList.size() < 2) {
                logger.warning("Biome cycling needs at least 2 biomes in cycle list!");
                enabled = false;
            } else {
                start();
            }
        }
    }

    /**
     * Starts the biome cycling task
     */
    public void start() {
        if (cycleTask != null) {
            cycleTask.cancel();
        }

        if (!enabled || cycleList.size() < 2) {
            return;
        }

        long intervalTicks = intervalMinutes * 60 * 20L; // Convert minutes to ticks

        logger.info("Starting biome cycling in world '" + targetWorldName + "'");
        logger.info("Cycling through " + cycleList.size() + " biomes every " + intervalMinutes + " minutes");
        logger.info("Cycle: " + String.join(" → ", cycleList));

        // Initial biome set
        applyCurrentBiome();

        // Schedule repeating task
        cycleTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            // Move to next biome
            currentBiomeIndex = (currentBiomeIndex + 1) % cycleList.size();
            applyCurrentBiome();
        }, intervalTicks, intervalTicks);
    }

    /**
     * Stops the biome cycling task
     */
    public void stop() {
        if (cycleTask != null) {
            cycleTask.cancel();
            cycleTask = null;
            logger.info("Biome cycling stopped");
        }
    }

    /**
     * Applies the current biome to the entire world
     */
    private void applyCurrentBiome() {
        String biomeId = cycleList.get(currentBiomeIndex);
        World world = Bukkit.getWorld(targetWorldName);

        if (world == null) {
            logger.warning("World '" + targetWorldName + "' not found, cannot apply biome cycle");
            return;
        }

        CustomBiomeData biomeData = biomeRegistry.getBiome(biomeId);
        if (biomeData == null) {
            logger.warning("Biome '" + biomeId + "' not found, skipping cycle step");
            return;
        }

        try {
            // Apply biome to all loaded chunks
            int chunksChanged = BiomeCycleApplicator.applyBiomeToWorld(
                world,
                biomeId,
                biomeRegistry,
                plugin.getConfig().getBoolean("biome_cycling.only_loaded_chunks", true)
            );

            String nextBiome = cycleList.get((currentBiomeIndex + 1) % cycleList.size());

            logger.info("Biome cycle: Applied '" + biomeData.getName() + "' to " + chunksChanged + " chunks");
            logger.info("Next biome: '" + biomeRegistry.getBiome(nextBiome).getName() + "' in " + intervalMinutes + " minutes");

            // Broadcast to players in that world
            if (plugin.getConfig().getBoolean("biome_cycling.announce_changes", true)) {
                String message = plugin.getConfig().getString(
                    "biome_cycling.change_message",
                    "§7The world atmosphere shifts to §e{biome}§7..."
                ).replace("{biome}", biomeData.getName());

                world.getPlayers().forEach(player -> player.sendMessage(message));
            }

        } catch (Exception e) {
            logger.warning("Failed to apply biome cycle: " + e.getMessage());
            if (plugin.getConfig().getBoolean("settings.debug", false)) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Manually skip to the next biome in the cycle
     */
    public void skipToNext() {
        if (!enabled) {
            return;
        }
        currentBiomeIndex = (currentBiomeIndex + 1) % cycleList.size();
        applyCurrentBiome();
    }

    /**
     * Get current cycle status
     */
    public String getStatus() {
        if (!enabled) {
            return "§cBiome cycling is disabled";
        }

        String current = cycleList.get(currentBiomeIndex);
        String next = cycleList.get((currentBiomeIndex + 1) % cycleList.size());

        CustomBiomeData currentData = biomeRegistry.getBiome(current);
        CustomBiomeData nextData = biomeRegistry.getBiome(next);

        return "§aCurrent: §e" + currentData.getName() +
               " §7| §aNext: §e" + nextData.getName() +
               " §7in " + intervalMinutes + " minutes";
    }

    public boolean isEnabled() {
        return enabled;
    }

    public List<String> getCycleList() {
        return new ArrayList<>(cycleList);
    }

    public String getTargetWorld() {
        return targetWorldName;
    }
}
