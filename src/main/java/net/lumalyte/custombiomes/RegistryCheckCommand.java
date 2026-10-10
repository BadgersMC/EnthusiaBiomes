package net.lumalyte.custombiomes;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Debug command to check if biomes are in Bukkit's registry
 */
public class RegistryCheckCommand implements CommandExecutor {

    private final BiomeRegistry biomeRegistry;

    public RegistryCheckCommand(BiomeRegistry biomeRegistry) {
        this.biomeRegistry = biomeRegistry;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("§6═══════════════════════════════════");
        sender.sendMessage("§e§lBiome Registry Check");
        sender.sendMessage("§6═══════════════════════════════════");

        String namespace = biomeRegistry.getNamespace();
        sender.sendMessage("§7Namespace: §e" + namespace);
        sender.sendMessage("");

        int foundCount = 0;
        int totalCount = 0;

        sender.sendMessage("§eChecking custom biomes in Bukkit registry:");
        for (String biomeId : biomeRegistry.getBiomeIds()) {
            totalCount++;
            String fullKey = namespace + ":" + biomeId;

            boolean found = false;
            for (org.bukkit.block.Biome biome : org.bukkit.Registry.BIOME) {
                if (biome.getKey().toString().equals(fullKey)) {
                    found = true;
                    foundCount++;
                    break;
                }
            }

            if (found) {
                sender.sendMessage("  §a✓ " + biomeId + " §7(" + fullKey + ")");
            } else {
                sender.sendMessage("  §c✗ " + biomeId + " §7(" + fullKey + ") §c- NOT IN BUKKIT REGISTRY");
            }
        }

        sender.sendMessage("");
        sender.sendMessage("§7Result: §e" + foundCount + " / " + totalCount + " §7biomes found in Bukkit registry");

        if (foundCount == 0) {
            sender.sendMessage("");
            sender.sendMessage("§c⚠ No custom biomes found in Bukkit registry!");
            sender.sendMessage("§7This means WorldEdit cannot see them.");
            sender.sendMessage("§7Possible issues:");
            sender.sendMessage("  §7- Biomes not registered to NMS registry properly");
            sender.sendMessage("  §7- Server version mismatch");
            sender.sendMessage("  §7- Registration timing issue");
        } else if (foundCount < totalCount) {
            sender.sendMessage("");
            sender.sendMessage("§e⚠ Some biomes are missing from Bukkit registry");
            sender.sendMessage("§7Check server logs for registration errors");
        } else {
            sender.sendMessage("");
            sender.sendMessage("§a✓ All custom biomes are accessible!");
            sender.sendMessage("§7WorldEdit should be able to see these biomes");
        }

        sender.sendMessage("§6═══════════════════════════════════");
        return true;
    }
}
