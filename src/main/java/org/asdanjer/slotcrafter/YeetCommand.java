package org.asdanjer.slotcrafter;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.UUID;

public class YeetCommand implements CommandExecutor {
    private Slotcrafter plugin;
    private HashSet<UUID> yeetablePlayers;

    public YeetCommand(Slotcrafter plugin) {
        this.plugin = plugin;
        this.yeetablePlayers = new HashSet<>();
    }

@Override
public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (command.getName().equalsIgnoreCase("yeetme")) {
        if (command.getName().equalsIgnoreCase("yeetme")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("This command can only be used by a player.");
                return true;
            }
            if (!sender.hasPermission("slotcrafter.yeetme")) {
                sender.sendMessage("You do not have permission to use this command.");
                return true;
            }
            Player player = (Player) sender;
            if (yeetablePlayers.contains(player.getUniqueId())) {
                yeetablePlayers.remove(player.getUniqueId());
                player.sendMessage("You will not be automatically kicked anymore.");
            } else {
                yeetablePlayers.add(player.getUniqueId());
                player.sendMessage("You will be automatically kicked when MSPT gets too high.");
            }
        }
        return true;
    } else if (command.getName().equalsIgnoreCase("yeetthem")) {
        if (!sender.hasPermission("slotcrafter.yeetthem")) {
            sender.sendMessage("You do not have permission to use this command.");
            return true;
        }
        int yeetlist=yeetPlayers();
        sender.sendMessage(yeetlist+ " Players have been yeeted!");
        return true;
    }
    return false;
}
    public void removeyeeter(UUID player) {
        if (yeetablePlayers.contains(player)) {
            yeetablePlayers.remove(player);
        }
    }
    public void checkyeetability() {
        double mspt = plugin.getMspt();
        double kickmspt = plugin.getConfig().getDouble("kickmspt");

        if (mspt > kickmspt) {
            yeetPlayers();
        }
    }

    private int yeetPlayers() {
        int yeetlist=yeetablePlayers.size();
        for (UUID playerId : yeetablePlayers) {
            Player yeetablePlayer = Bukkit.getPlayer(playerId);
            if (yeetablePlayer != null) {
                yeetablePlayer.kickPlayer("MSPT is too high!");

            }
            yeetablePlayers.remove( playerId);
        }
        return yeetlist;
    }
}