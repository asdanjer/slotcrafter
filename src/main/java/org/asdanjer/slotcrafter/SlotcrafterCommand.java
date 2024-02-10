package org.asdanjer.slotcrafter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class SlotcrafterCommand implements CommandExecutor {
    private Slotcrafter plugin;
    private FileConfiguration config;
    private Config pluginconfig;

    public SlotcrafterCommand(Slotcrafter plugin) {
        this.plugin = plugin;
        config = plugin.getConfig();
        Config pluginconfig = plugin.getPluginConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length != 2) {
            sender.sendMessage("Usage: /slotcrafter <config setting> <value>");
            return false;
        }

        String setting = args[0];
        String value = args[1];

        try {
            pluginconfig.updateConfigValue(setting, value);
            sender.sendMessage("Config setting updated successfully.");
        } catch (IllegalArgumentException e) {
            sender.sendMessage(e.getMessage());
        }

        return true;
    }
}