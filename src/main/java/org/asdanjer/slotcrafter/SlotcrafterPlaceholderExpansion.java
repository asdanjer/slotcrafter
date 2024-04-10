package org.asdanjer.slotcrafter;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.asdanjer.slotcrafter.TakeMySlotCommand;
import org.asdanjer.slotcrafter.YeetCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

import static java.awt.SystemColor.info;

public class SlotcrafterPlaceholderExpansion extends PlaceholderExpansion {

    private  JavaPlugin plugin;
    private YeetCommand yeetCommand;
    private TakeMySlotCommand takeMySlotCommand;
    private Info info;

    public SlotcrafterPlaceholderExpansion(JavaPlugin plugin, YeetCommand yeetCommand, TakeMySlotCommand takeMySlotCommand, Info info) {

        this.plugin = plugin;
        this.yeetCommand = yeetCommand;
        this.takeMySlotCommand = takeMySlotCommand;
        this.info = info;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public String getIdentifier() {
        return plugin.getDescription().getName();
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {

        // %slotcrafter_yeetable%
        if (identifier.equals("yeetable") && player != null) {

            return Boolean.toString(yeetCommand.getYeetablePlayers().contains(player.getUniqueId()));
        }

        // %slotcrafter_slotdonor%
        if (identifier.equals("slotdonor") && player != null) {
            HashMap<UUID, Long> offeringplayers = takeMySlotCommand.getSlotOfferedPlayers();
            if (!offeringplayers.isEmpty()){
            int hours = (int)(((offeringplayers.get(player.getUniqueId()) - System.currentTimeMillis())/3600000)+1);
            Bukkit.getLogger().info("Hours: " + hours);
            if (offeringplayers.containsKey(player.getUniqueId())) {
                return String.valueOf(hours);
            }else{
                return "65000";
            }
            }else return "65000";
        }
        // %slotcrafter_calculated_rolling_avg_mspt%
        if(identifier.equals("calulcated_rolling_avg_mspt")){
            return String.valueOf(info.getCurrentlyUsedMSPT());
        }

        return null;
    }
}