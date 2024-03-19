package org.asdanjer.slotcrafter;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class YeetCommand implements CommandExecutor {
    private Slotcrafter plugin;
    private HashSet<UUID> yeetablePlayers;
    private HashMap<UUID, Integer> customMsptThresholds;

    public YeetCommand(Slotcrafter plugin) {
        this.plugin = plugin;
        this.yeetablePlayers = new HashSet<>();
        this.customMsptThresholds = new HashMap<>();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
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
            if (yeetablePlayers.contains(player.getUniqueId())&&args.length==0) {
                yeetablePlayers.remove(player.getUniqueId());
                customMsptThresholds.remove(player.getUniqueId());
                player.sendMessage("You will not be automatically kicked anymore.");
            } else {
                yeetablePlayers.add(player.getUniqueId());
                if (args.length > 0) {
                    try {
                        int customMspt = Integer.parseInt(args[0]);
                        if(customMspt>200 || customMspt<1){
                            player.sendMessage("Invalid MSPT value. Using default.");
                        }else{
                            customMsptThresholds.put(player.getUniqueId(), customMspt);
                            player.sendMessage("You will be automatically kicked when MSPT gets to " + customMspt);
                        }
                    } catch (NumberFormatException e) {
                        player.sendMessage("Invalid MSPT value. Using default.");
                    }
                } else {
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
            customMsptThresholds.remove(player);
        }
    }

    public void checkyeetability() {
        double mspt = plugin.getMspt();
        Set<UUID> yeetablePlayersCopy = new HashSet<>(yeetablePlayers);
        for (UUID playerId : yeetablePlayersCopy) {
            int kickmspt = customMsptThresholds.getOrDefault(playerId, plugin.getConfig().getInt("kickmspt"));
            if (mspt > kickmspt) {
                yeetPlayer(playerId);
            }
        }
    }

    private void yeetPlayer(UUID playerId) {
        Player yeetablePlayer = Bukkit.getPlayer(playerId);
        if (yeetablePlayer != null) {
            List<String> commands = plugin.getConfig().getStringList("kickCommands");
            for (String command : commands) {
                command = command.replace("<player>", yeetablePlayer.getName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            }
            customMsptThresholds.remove(playerId);
        }
        yeetablePlayers.remove(playerId);
    }


    private int yeetPlayers() {
        int yeetedPlayers = 0;
        Set<UUID> yeetablePlayersCopy = new HashSet<>(yeetablePlayers);
        for (UUID playerId : yeetablePlayersCopy) {
            yeetPlayer(playerId);
            yeetedPlayers++;
        }
        return yeetedPlayers;
    }

    public HashSet<UUID> getYeetablePlayers() {
        return yeetablePlayers;
    }
}