package org.asdanjer.slotcrafter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SlotLimitCommand implements CommandExecutor {
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

        int minSlots = plugin.getConfig().getInt("minSlots");
        if (newLimit < minSlots) {
            sender.sendMessage("The new limit cannot be less than the minimum limit (" + minSlots + ").");
            return false;
        }

        plugin.setManualCap(newLimit);
        sender.sendMessage("Slot limit has been set to " + newLimit);
        return true;
    }
}