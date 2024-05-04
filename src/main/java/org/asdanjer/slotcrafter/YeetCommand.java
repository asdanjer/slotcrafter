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
    private Persistency persistency;
    //resonable limits for custome mspt values
    private final int MAXMSPT = 200;
    private final int MINMSPT = 1;

    public YeetCommand(Slotcrafter plugin, Persistency persistency) {
        this.plugin = plugin;
        this.yeetablePlayers = new HashSet<>();
        this.customMsptThresholds = new HashMap<>();
        this.persistency = persistency;
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
                    if(args[0].equalsIgnoreCase("help")){
                        sender.sendMessage("Using this comand will kick you automatically when the server is at"+ plugin.getConfig().getInt("kickmspt") + "MSPT. You can change this value by using /yeetme <mspt>. To stop being yeetable use /yeetme again.");
                        return true;
                    }
                    //persitencycheck
                    if(args[0].equalsIgnoreCase("persistent")){
                        if(args.length==1){
                            player.sendMessage("Usage: /yeetme persistent <on/off/mspt-value> ");
                            return true;
                        }
                        if(args[1].equalsIgnoreCase("off")){
                            persistency.togglePersitencyYeet(player,-1,false);
                            player.sendMessage("Disabled Persistent Yeet");
                        }else if(args[1].equalsIgnoreCase("on")){
                            persistency.togglePersitencyYeet(player,plugin.getConfig().getInt("kickmspt"),true);
                            player.sendMessage("Enabled Persistent Yeet");
                        }else{
                            try{
                                int msptvalue=Integer.parseInt(args[1]);
                                if(msptvalue>MAXMSPT || msptvalue<MINMSPT){
                                    player.sendMessage("Invalid MSPT value. Using default.");
                                    msptvalue=plugin.getConfig().getInt("kickmspt");
                                }
                                persistency.togglePersitencyYeet(player,msptvalue,true);
                                player.sendMessage("Enabled Persistent Yeet");
                            } catch (NumberFormatException e) {
                                player.sendMessage("Usage: /yeetme persistent <on/off/mspt-value> ");
                            }

                        }
                        return true;
                    }

                    try {
                        int customMspt = Integer.parseInt(args[0]);
                        if(customMspt>MAXMSPT || customMspt<MINMSPT){
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
        yeetablePlayers.remove(player);
        customMsptThresholds.remove(player);

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

    public void addYeetablePlayer(UUID playerId) {
        yeetablePlayers.add(playerId);
    }

    public void addYeetablePlayer(UUID playerId, int customMspt) {
        yeetablePlayers.add(playerId);
        if(!(customMspt > MAXMSPT || customMspt < MINMSPT)){
            customMsptThresholds.put(playerId, customMspt);
        }
    }
}
