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
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (yeetablePlayers.contains(player.getUniqueId())) {
                yeetablePlayers.remove(player.getUniqueId());
                player.sendMessage("You will not be automatically kicked.");
            } else {
                yeetablePlayers.add(player.getUniqueId());
                player.sendMessage("You will be automatically kicked when MSPT gets too high.");
            }
        } else {
            sender.sendMessage("This command can only be used by a player.");
        }
        return true;
    }     else if (command.getName().equalsIgnoreCase("yeetthem")) {
        yeetPlayers();
        sender.sendMessage("You been yeeted!");
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
        double upperMSPTThreshold = plugin.getConfig().getDouble("upperMSPTThreshold");

        if (mspt > upperMSPTThreshold) {
            for (UUID playerId : yeetablePlayers) {
                Player yeetablePlayer = Bukkit.getPlayer(playerId);
                if (yeetablePlayer != null) {
                    yeetablePlayers.remove( yeetablePlayer.getUniqueId());
                    yeetablePlayer.kickPlayer("MSPT is too high!");
                }

            }
        }
    }

    private void yeetPlayers() {
        for (UUID playerId : yeetablePlayers) {
            Player yeetablePlayer = Bukkit.getPlayer(playerId);
            if (yeetablePlayer != null) {
                yeetablePlayer.kickPlayer("MSPT is too high!");
            }
        }
    }
}