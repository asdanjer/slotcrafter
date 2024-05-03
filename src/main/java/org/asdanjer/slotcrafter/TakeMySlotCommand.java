package org.asdanjer.slotcrafter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import java.util.Random;

import java.util.HashMap;
import java.util.UUID;

public class TakeMySlotCommand implements CommandExecutor {
    private final Slotcrafter plugin;
    private final HashMap<UUID, Long> slotOfferedPlayers;
    private FileConfiguration config;


    public TakeMySlotCommand(Slotcrafter plugin) {
        this.plugin = plugin;
        this.slotOfferedPlayers = new HashMap<>();
        config = plugin.getConfig();
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
            try {
                hours= Integer.parseInt(args[0]);
                if (hours > 24) {
                    sender.sendMessage("Can't offer slot after more than 24 hours. Using default time!");
                    hours = config.getInt("defaultTakeMySlotTime", 1);
                }else{
                sender.sendMessage("you offer your slot starting in " + hours + " hours.");
                }
            } catch (NumberFormatException e) {
                sender.sendMessage("Wrong value Using default time!");
            }
            slotOfferedPlayers.put(playerId, ((long)hours*3600000)+time);
        } else {
            if (slotOfferedPlayers.containsKey(playerId)) {
                sender.sendMessage("You do no longer offer your slot.");
                slotOfferedPlayers.remove(playerId);
            } else {
                sender.sendMessage("you offer your slot starting in " + hours + " hours.");
                slotOfferedPlayers.put(playerId, ((long)hours*3600000)+time);
            }
        }

        return true;
    }
    public HashMap<UUID, Long> getSlotOfferedPlayers() {
        return slotOfferedPlayers;
    }
    public void removePLayer(UUID playerId){
        slotOfferedPlayers.remove(playerId);
    }
}