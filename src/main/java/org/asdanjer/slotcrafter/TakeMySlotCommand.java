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

        if (args.length > 0) {
            try {
                hours= Integer.parseInt(args[0]);
                slotOfferedPlayers.put(playerId, (long)hours*3600000);
            } catch (NumberFormatException e) {
                sender.sendMessage("Wrong value Using default time!");
                slotOfferedPlayers.put(playerId, (long)hours*3600000);
            }
        } else {
            if (slotOfferedPlayers.containsKey(playerId)) {
                slotOfferedPlayers.remove(playerId);
                sender.sendMessage("You do no longer offer your slot.");
            } else {
                slotOfferedPlayers.put(playerId, (long)hours*3600000);
                sender.sendMessage("you offer your slot starting in " + hours + " hours.");
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