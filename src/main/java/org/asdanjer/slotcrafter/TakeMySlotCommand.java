package org.asdanjer.slotcrafter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class TakeMySlotCommand implements CommandExecutor {
    private final Slotcrafter plugin;
    private final HashMap<UUID, Long> slotOfferedPlayers;

    public TakeMySlotCommand(Slotcrafter plugin) {
        this.plugin = plugin;
        this.slotOfferedPlayers = new HashMap<>();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 1) {
                try {
                    int hours = Integer.parseInt(args[0]);
                    long expiryTime = System.currentTimeMillis() + hours * 60 * 60 * 1000;
                    slotOfferedPlayers.put(player.getUniqueId(), expiryTime);
                    player.sendMessage("You have offered your slot for " + hours + " hours.");
                } catch (NumberFormatException e) {
                    player.sendMessage("Invalid time format. Please enter the time in hours.");
                }
            } else {
                player.sendMessage("Please specify the time in hours.");
            }
        } else {
            sender.sendMessage("This command can only be used by a player.");
        }
        return true;
    }

    public HashMap<UUID, Long> getSlotOfferedPlayers() {
        return slotOfferedPlayers;
    }
}