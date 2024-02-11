package org.asdanjer.slotcrafter;

import org.bukkit.command.*;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class SlotcrafterCommand implements CommandExecutor, TabCompleter {
    private Slotcrafter plugin;
    private FileConfiguration config;

    public SlotcrafterCommand(Slotcrafter plugin) {
        this.plugin = plugin;
        config = plugin.getConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length != 3 || !args[0].equalsIgnoreCase("update")) {
            sender.sendMessage("Usage: /slotcrafter update <config setting> <value>");
            return false;
        }
        if (sender.hasPermission("slotcrafter.admin")) {
            String setting = args[1];
            String value = args[2];

            try {
                plugin.updateConfigValue(setting, value);
                sender.sendMessage("Config setting updated successfully.");
            } catch (IllegalArgumentException e) {
                sender.sendMessage(e.getMessage());
            }
        } else {
            sender.sendMessage("You do not have permission to use this command.");
        }

        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("update");
            // add more subcommands here
        } else if (args.length == 2) {
            completions.add("minSlots");
            completions.add("maxSlots");
            completions.add("lowerMSPTThreshold");
            completions.add("upperMSPTThreshold");
            completions.add("updateInterval");
            completions.add("averageMSPTInterval");
            completions.add("autoMode");
            completions.add("kickmspt");
            // add more config settings here

        } else if (args.length == 3) {
            String setting = args[1];
            if (config.isSet(setting)) {
                completions.add(String.valueOf(config.get(setting)));
            }
        }
        return completions;
    }
}