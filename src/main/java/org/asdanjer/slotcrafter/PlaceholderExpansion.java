package org.asdanjer.slotcrafter;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

class SlotcrafterPlaceholderExpansion extends PlaceholderExpansion {

    private final JavaPlugin plugin;

    public SlotcrafterPlaceholderExpansion(JavaPlugin plugin) {
        this.plugin = plugin;
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
        if (player == null) {
            return "";
        }

        // %slotcrafter_yeetable%
        if (identifier.equals("yeetable")) {
            return Boolean.toString(player.hasPermission("slotcrafter.yeetme"));
        }

        // %slotcrafter_slotdonor%
        if (identifier.equals("slotdonor")) {
            return Boolean.toString(player.hasPermission("slotcrafter.takemyslot"));
        }

        return null;
    }
}