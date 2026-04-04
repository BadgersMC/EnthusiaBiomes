package net.lumalyte.custombiomes;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Command to control biome cycling
 * /biomecycle [start|stop|skip|status]
 */
public class BiomeCycleCommand implements CommandExecutor, TabCompleter {

    private final EnthusiaBiomesPlugin plugin;
    private final BiomeCycler biomeCycler;

    public BiomeCycleCommand(EnthusiaBiomesPlugin plugin, BiomeCycler biomeCycler) {
        this.plugin = plugin;
        this.biomeCycler = biomeCycler;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            // Default to status
            sender.sendMessage(biomeCycler.getStatus());
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "start":
                if (!biomeCycler.isEnabled()) {
                    sender.sendMessage("§cBiome cycling is disabled in config.yml");
                    sender.sendMessage("§7Set 'biome_cycling.enabled: true' and reload");
                    return true;
                }

                biomeCycler.start();
                sender.sendMessage("§aBiome cycling started!");
                sender.sendMessage("§7World: §e" + biomeCycler.getTargetWorld());
                sender.sendMessage("§7Biomes: §e" + String.join(", ", biomeCycler.getCycleList()));
                return true;

            case "stop":
                biomeCycler.stop();
                sender.sendMessage("§cBiome cycling stopped");
                return true;

            case "skip":
                if (!biomeCycler.isEnabled()) {
                    sender.sendMessage("§cBiome cycling is disabled");
                    return true;
                }

                biomeCycler.skipToNext();
                sender.sendMessage("§aSkipped to next biome!");
                sender.sendMessage(biomeCycler.getStatus());
                return true;

            case "status":
                sender.sendMessage("§6═══════════════════════════════════");
                sender.sendMessage("§e§lBiome Cycling Status");
                sender.sendMessage("§6═══════════════════════════════════");
                sender.sendMessage(biomeCycler.getStatus());
                if (biomeCycler.isEnabled()) {
                    sender.sendMessage("§7World: §e" + biomeCycler.getTargetWorld());
                    sender.sendMessage("§7Cycle: §e" + String.join(" → ", biomeCycler.getCycleList()));
                }
                sender.sendMessage("§6═══════════════════════════════════");
                return true;

            default:
                sender.sendMessage("§cUsage: /biomecycle [start|stop|skip|status]");
                return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>(Arrays.asList("start", "stop", "skip", "status"));
            String input = args[0].toLowerCase();
            completions.removeIf(s -> !s.startsWith(input));
            return completions;
        }
        return new ArrayList<>();
    }
}
