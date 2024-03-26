package org.asdanjer.slotcrafter;

import org.bukkit.command.*;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SlotcrafterCommand implements CommandExecutor, TabCompleter {
    List<String> allowedsettingsList = Arrays.asList("minSlots", "maxSlots", "lowerMSPTThreshold", "upperMSPTThreshold", "updateInterval", "averageMSPTInterval", "autoMode", "kickmspt");
    private Slotcrafter plugin;
    private FileConfiguration config;

    public SlotcrafterCommand(Slotcrafter plugin) {
        this.plugin = plugin;
        config = plugin.getConfig();
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("Usage: /slotcrafter <update/info> <config setting> <value>");
            return false;
        }

        if (args[0].equalsIgnoreCase("update")) {
            if (args.length != 3) {
                sender.sendMessage("Usage: /slotcrafter update <config setting> <value>");
                return false;
            }
            if (sender.hasPermission("slotcrafter.admin")) {
                String setting = args[1];
                String value = args[2];

                try {
                    if (allowedsettingsList.contains(setting)) {
                        plugin.updateConfigValue(setting, value);
                        sender.sendMessage("Config setting updated successfully.");
                    }
                } catch (IllegalArgumentException e) {
                    sender.sendMessage("Invalid setting or value. Please check and try again.");
                }
            } else {
                sender.sendMessage("You do not have permission to use this command.");
            }
        } else if (args[0].equalsIgnoreCase("info")) {
            if (sender.hasPermission("slotcrafter.admin")) {
                sender.sendMessage(plugin.getInfo());
            } else {
                sender.sendMessage("You do not have permission to use this command.");
            }
        }

        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("update");
            completions.add("info");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("update")){
            completions.addAll(allowedsettingsList);

        }else if (args.length == 3 && args[0].equalsIgnoreCase("update")) {
            String setting = args[1];
            if (config.isSet(setting)) {
                completions.add(String.valueOf(config.get(setting)));
            }
        }
        return completions;
    }
}