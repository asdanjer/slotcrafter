package org.asdanjer.slotcrafter;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class YeetCommand implements CommandExecutor, TabCompleter {
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
                if (args.length > 0) {
                    if(args[0].equalsIgnoreCase("help")){
                        sendhelpmessage(player,plugin.getConfig().getInt("kickmspt"));
                        return true;
                    }
                    //persitencycheck
                    if(args[0].equalsIgnoreCase("persistent") && plugin.dopersitency){
                        if(args.length==1){
                            player.sendMessage("Usage: /yeetme persistent <auto/off/mspt-value> ");
                            return true;
                        }
                        if(args[1].equalsIgnoreCase("off")){
                            persistency.togglePersitencyYeet(player,-1,false);
                            player.sendMessage("Disabled Persistent Yeet");
                            yeetablePlayers.remove(player.getUniqueId());
                            customMsptThresholds.remove(player.getUniqueId());
                        }else if(args[1].equalsIgnoreCase("auto")){
                            persistency.togglePersitencyYeet(player,-1,true);
                            player.sendMessage("Enabled Persistent Yeet");
                            yeetablePlayers.add(player.getUniqueId());
                        }else{
                            try{
                                int msptvalue=Integer.parseInt(args[1]);
                                if(msptvalue>MAXMSPT || msptvalue<MINMSPT){
                                    player.sendMessage("Invalid MSPT value. Using default.");
                                    msptvalue=plugin.getConfig().getInt("kickmspt");
                                }
                                persistency.togglePersitencyYeet(player,msptvalue,true);
                                yeetablePlayers.add(player.getUniqueId());
                                customMsptThresholds.put(player.getUniqueId(), msptvalue);
                                player.sendMessage("Enabled Persistent Yeet");
                            } catch (NumberFormatException e) {
                                player.sendMessage("Usage: /yeetme persistent <auto/off/mspt-value> ");
                            }

                        }
                        return true;
                    }
                    yeetablePlayers.add(player.getUniqueId());
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

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        try {
            if(command.getName().equalsIgnoreCase("yeetme")) {
                List<String> completions = new ArrayList<>();
                String kickmspt = String.valueOf(plugin.getConfig().getInt("kickmspt", 1));
                if (args.length == 1) {
                    if (plugin.dopersitency) completions.add("persistent");
                    completions.add(kickmspt);
                    completions.add("help");
                } else if (args.length == 2 && args[0].equalsIgnoreCase("persistent") && plugin.dopersitency) {
                    completions.add("auto");
                    completions.add("off");
                    completions.add(kickmspt);

                }
                return completions;
            }else return new ArrayList<>();
        }catch (Exception e){
            return new ArrayList<>();
        }
    }
    public void sendhelpmessage(Player player, int mspt){
        // Help message for /yeetme command
        player.sendMessage("§e/yeetme§f - Makes you automatically kickable when the server is lagging(MSPT at "+mspt+").");
        player.sendMessage("§e/yeetme <mspt>§f - Sets a custom MSPT threshold for automatic kicking.");
        if (plugin.dopersitency) player.sendMessage("§e/yeetme persistent <auto/off/mspt-value>§f - Enables it automatically on login:");
        if (plugin.dopersitency) player.sendMessage("   §f'auto' - at the default mspt value, 'off' - disables it, or enter a number for a custom MSPT value");
        player.sendMessage("§e/yeetme help§f - very secret help message");
        player.sendMessage("§cNote:§f To stop being automatically kickable, use §e/yeetme§f again.");
}
}
