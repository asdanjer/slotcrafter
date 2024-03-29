package org.asdanjer.slotcrafter;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SlotLimitCommand implements CommandExecutor, TabCompleter {
    private Slotcrafter plugin;

    public SlotLimitCommand(Slotcrafter plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.hasPermission("slotcrafter.setslotlimit")) {
                player.sendMessage("You do not have permission to use this command.");
                return true;
            }
        }

        if (args.length != 1) {
            sender.sendMessage("Please specify the new slot limit or 'auto'.");
            return false;
        }

        if (args[0].equalsIgnoreCase("auto")) {
            plugin.fullAuto(true);
            sender.sendMessage("Automatic slot limit adjustment has been enabled.");
            return true;
        }

        int newLimit;
        try {
            newLimit = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            sender.sendMessage("Invalid number format. Please enter a valid integer or 'auto'.");
            return false;
        }

        plugin.setManualCap(newLimit);
        sender.sendMessage("Slot limit has been set to " + newLimit);
        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            int currentLimit = plugin.getRealplayercap();
            completions.add(String.valueOf(currentLimit + 1));
            completions.add("auto");
        }
        return completions;
    }
}