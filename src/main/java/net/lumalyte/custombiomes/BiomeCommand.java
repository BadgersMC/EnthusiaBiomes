package net.lumalyte.custombiomes;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Command handler for biome-related commands
 */
public class BiomeCommand implements CommandExecutor, TabCompleter {

    private final EnthusiaBiomesPlugin plugin;
    private final BiomeRegistry biomeRegistry;
    private final CustomBiomeHandler biomeHandler;

    public BiomeCommand(EnthusiaBiomesPlugin plugin, BiomeRegistry biomeRegistry, CustomBiomeHandler biomeHandler) {
        this.plugin = plugin;
        this.biomeRegistry = biomeRegistry;
        this.biomeHandler = biomeHandler;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (command.getName().toLowerCase()) {
            case "setbiome":
                return handleSetBiome(sender, args);
            case "listbiomes":
                return handleListBiomes(sender);
            case "reloadbiomes":
                return handleReloadBiomes(sender);
            default:
                return false;
        }
    }

    /**
     * Sets the biome of the chunk the player is standing in
     */
    private boolean handleSetBiome(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return true;
        }

        if (!sender.hasPermission("custombiomes.setbiome")) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage("§cUsage: /setbiome <biome_id> [radius]");
            sender.sendMessage("§7Example: /setbiome vice_city_sunset 5");
            return true;
        }

        Player player = (Player) sender;
        String biomeId = args[0].toLowerCase();

        // Check if biome exists
        if (!biomeRegistry.hasBiome(biomeId)) {
            sender.sendMessage("§cBiome '" + biomeId + "' not found!");
            sender.sendMessage("§7Use /listbiomes to see available biomes");
            return true;
        }

        // Parse radius
        int radius = 0;
        if (args.length >= 2) {
            try {
                radius = Integer.parseInt(args[1]);
                if (radius < 0 || radius > 10) {
                    sender.sendMessage("§cRadius must be between 0 and 10!");
                    return true;
                }
            } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid radius! Must be a number.");
                return true;
            }
        }

        // Apply biome
        try {
            int chunksChanged = setBiomeInArea(player, biomeId, radius);
            CustomBiomeData biomeData = biomeRegistry.getBiome(biomeId);

            sender.sendMessage("§aSuccessfully set " + chunksChanged + " chunk(s) to biome: §e" + biomeData.getName());
            sender.sendMessage("§7Relog or move away and back to see the changes!");
        } catch (Exception e) {
            sender.sendMessage("§cFailed to set biome: " + e.getMessage());
            plugin.getLogger().warning("Failed to set biome for " + player.getName() + ": " + e.getMessage());
            if (plugin.getConfig().getBoolean("settings.debug", false)) {
                e.printStackTrace();
            }
        }

        return true;
    }

    /**
     * Lists all available custom biomes
     */
    private boolean handleListBiomes(CommandSender sender) {
        if (!sender.hasPermission("custombiomes.list")) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        sender.sendMessage("§6═══════════════════════════════════");
        sender.sendMessage("§e§lAvailable Custom Biomes");
        sender.sendMessage("§6═══════════════════════════════════");

        for (CustomBiomeData biome : biomeRegistry.getAllBiomes()) {
            sender.sendMessage("§a" + biome.getId() + " §7- §f" + biome.getName());
            sender.sendMessage("  §7Temperature: §e" + biome.getTemperature() +
                             " §7| Rainfall: §e" + biome.getRainfall());
        }

        sender.sendMessage("§6═══════════════════════════════════");
        sender.sendMessage("§7Total: §e" + biomeRegistry.size() + " §7biomes");
        sender.sendMessage("§7Use §e/setbiome <id> §7to apply a biome");

        return true;
    }

    /**
     * Reloads the plugin configuration
     */
    private boolean handleReloadBiomes(CommandSender sender) {
        if (!sender.hasPermission("custombiomes.reload")) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        sender.sendMessage("§7Reloading Custom Biomes Plugin...");
        plugin.reloadPlugin();
        sender.sendMessage("§aPlugin reloaded! §e" + biomeRegistry.size() + " §abiomes loaded.");

        return true;
    }

    /**
     * Sets biome in an area around the player
     */
    private int setBiomeInArea(Player player, String biomeId, int radius) throws Exception {
        World world = player.getWorld();
        Chunk centerChunk = player.getLocation().getChunk();

        CraftServer craftServer = (CraftServer) Bukkit.getServer();
        Registry<Biome> registry = craftServer.getServer()
            .registryAccess()
            .lookup(Registries.BIOME)
            .orElseThrow();

        // Get the custom biome holder using configured namespace
        String namespace = biomeRegistry.getNamespace();
        ResourceKey<Biome> biomeKey = ResourceKey.create(
            Registries.BIOME,
            Identifier.fromNamespaceAndPath(namespace, biomeId)
        );
        Holder<Biome> biomeHolder = registry.get(biomeKey)
            .orElseThrow(() -> new IllegalStateException("Biome not registered: " + biomeId));

        int chunksChanged = 0;

        // Set biome in all chunks within radius
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                Chunk chunk = world.getChunkAt(centerChunk.getX() + x, centerChunk.getZ() + z);

                // Set biome for entire chunk (all Y levels)
                CraftWorld craftWorld = (CraftWorld) world;
                LevelChunk nmsChunk = craftWorld.getHandle().getChunk(chunk.getX(), chunk.getZ());

                // Fill entire chunk with the biome
                // Biomes are stored in 4x4x4 sections, so we divide by 4
                for (int cx = 0; cx < 16; cx += 4) {
                    for (int cz = 0; cz < 16; cz += 4) {
                        for (int cy = world.getMinHeight(); cy < world.getMaxHeight(); cy += 4) {
                            nmsChunk.getSection(nmsChunk.getSectionIndex(cy))
                                    .setNoiseBiome(cx / 4, (cy / 4) & 3, cz / 4, biomeHolder);
                        }
                    }
                }

                // Paper 26.2 requires an explicit dirty marker for biome-only edits.
                // Save once after the batch, not once per chunk.
                nmsChunk.markUnsaved();

                // Send chunk update to all players
                world.refreshChunk(chunk.getX(), chunk.getZ());

                chunksChanged++;
            }
        }

        // Force a final world save to ensure everything persists
        try {
            world.save();
            player.sendMessage("§7Saved " + chunksChanged + " chunks to disk");
        } catch (Exception e) {
            player.sendMessage("§cWarning: Failed to save world, biomes may not persist on restart");
        }

        return chunksChanged;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (command.getName().equalsIgnoreCase("setbiome")) {
            if (args.length == 1) {
                // Tab complete biome IDs
                return biomeRegistry.getBiomeIds().stream()
                    .filter(id -> id.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
            } else if (args.length == 2) {
                // Tab complete radius
                return List.of("0", "1", "2", "3", "5", "10");
            }
        }

        return completions;
    }
}
