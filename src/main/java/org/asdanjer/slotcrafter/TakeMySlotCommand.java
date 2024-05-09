package org.asdanjer.slotcrafter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class TakeMySlotCommand implements CommandExecutor, TabCompleter {
    private final Slotcrafter plugin;
    private final HashMap<UUID, Long> slotOfferedPlayers;
    private FileConfiguration config;
    private final Persistency persistency;


    public TakeMySlotCommand(Slotcrafter plugin, Persistency persistency) {
        this.plugin = plugin;
        this.slotOfferedPlayers = new HashMap<>();
        config = plugin.getConfig();
        this.persistency = persistency;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by a player.");
            return true;
        }
        if (!(sender.hasPermission("slotcrafter.takemyslot"))) {
            sender.sendMessage("Invalid Permission.");
            return true;
        }


        Player player = (Player) sender;
        UUID playerId = player.getUniqueId();
        int hours = config.getInt("defaultTakeMySlotTime", 1);
        long time = System.currentTimeMillis();
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("help")) {
                sendhelpmessage(player, hours);
                return true;
            }
            //persitencycheck
            if (args[0].equalsIgnoreCase("persistent") && plugin.dopersitency) {
                if (args.length == 1) {
                    player.sendMessage("Usage: /takemyslot persistent <auto/off/delay> ");
                    return true;
                }
                if (args[1].equalsIgnoreCase("off")) {
                    persistency.togglePersitencyTakeslot(player, -1, false);
                    player.sendMessage("Disabled Persistent Yeet");
                    slotOfferedPlayers.remove(playerId);
                } else if (args[1].equalsIgnoreCase("auto")) {
                    persistency.togglePersitencyTakeslot(player, -1, true);
                    player.sendMessage("Enabled Persistent Yeet");
                    slotOfferedPlayers.put(playerId, ((long) hours * 3600000) + time);
                } else {
                    try {
                        int delayTime = Integer.parseInt(args[1]);
                        if (delayTime > 24 || delayTime < 0) {
                            player.sendMessage("Invalid delay value. Using default.");
                            delayTime = plugin.getConfig().getInt("defaultTakeMySlotTime");
                        }
                        persistency.togglePersitencyTakeslot(player, delayTime, true);
                        slotOfferedPlayers.put(playerId, ((long) delayTime * 3600000) + time);
                        player.sendMessage("Enabled Persistent Take Slot");
                    } catch (NumberFormatException e) {
                        player.sendMessage("Usage: /takemyslot persistent <auto/off/delay> ");
                    }

                }
                return true;
            }
            try {
                hours = Integer.parseInt(args[0]);
                if (hours > 24) {
                    sender.sendMessage("Can't offer slot after more than 24 hours. Using default time!");
                    hours = config.getInt("defaultTakeMySlotTime", 1);
                } else {
                    sender.sendMessage("you offer your slot starting in " + hours + " hours.");
                }
            } catch (NumberFormatException e) {
                sender.sendMessage("Wrong value Using default time!");
            }
            slotOfferedPlayers.put(playerId, ((long) hours * 3600000) + time);
        } else {
            if (slotOfferedPlayers.containsKey(playerId)) {
                sender.sendMessage("You do no longer offer your slot.");
                slotOfferedPlayers.remove(playerId);
            } else {
                sender.sendMessage("you offer your slot starting in " + hours + " hours.");
                slotOfferedPlayers.put(playerId, ((long) hours * 3600000) + time);
            }
        }

        return true;
    }

    public HashMap<UUID, Long> getSlotOfferedPlayers() {
        return slotOfferedPlayers;
    }

    public void removePLayer(UUID playerId) {
        slotOfferedPlayers.remove(playerId);
    }

    public void addPlayer(UUID playerId, long time) {
        slotOfferedPlayers.put(playerId, time);
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        try {
            if(command.getName().equalsIgnoreCase("takemyslot")) {
            List<String> completions = new ArrayList<>();
            String defaultTime = String.valueOf(plugin.getConfig().getInt("defaultTakeMySlotTime", 1));
            if (args.length == 1) {
                if (plugin.dopersitency) completions.add("persistent");
                completions.add(defaultTime);
                completions.add("help");
            } else if (args.length == 2 && args[0].equalsIgnoreCase("persistent") && plugin.dopersitency) {
                completions.add("auto");
                completions.add("off");
                completions.add(defaultTime);

            }
            return completions;
            }else {
                return new ArrayList<>();
            }
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    public void sendhelpmessage(Player player, int hours){
        // Help message for /takemyslot command
        player.sendMessage("§e/takemyslot§f - Offers your slot after" + hours + " hours.");
        player.sendMessage("§e/takemyslot <hours>§f - Offers your slot after the specified number of hours.");
        if (plugin.dopersitency) player.sendMessage("§e/takemyslot persistent <auto/off/delay>§f - Enables it automatically on login:");
        if (plugin.dopersitency) player.sendMessage("   §f'auto' - enables it automatically "+hours+" hours after login, 'off' - disables it, or enter a number for a custom delay");
        player.sendMessage("§e/takemyslot help§f - very secret help message");
        player.sendMessage("§cNote:§f To stop offering your slot, use §e/takemyslot§f again.");    }
}