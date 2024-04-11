package org.asdanjer.slotcrafter;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class ReminderActionBar {
    private final Slotcrafter plugin;
    private final YeetCommand yeetCommand;
    private final TakeMySlotCommand takeMySlotCommand;
    private final Info info;

    public ReminderActionBar(Slotcrafter plugin, YeetCommand yeetCommand, TakeMySlotCommand takeMySlotCommand, Info info) {
        this.plugin = plugin;
        this.yeetCommand = yeetCommand;
        this.takeMySlotCommand = takeMySlotCommand;
        this.info = info;
    }

    public void start() {
        if(plugin.getConfig().getLong("ReminderInterval")>0) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        UUID playerId = player.getUniqueId();
                        try {
                            if (yeetCommand.getYeetablePlayers().contains(playerId)) {
                                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(plugin.getConfig().getString("YeetmeMessage")));
                            }
                            if (takeMySlotCommand.getSlotOfferedPlayers().containsKey(playerId)) {
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(plugin.getConfig().getString("TakemyslotMessage")));
                                    }
                                }.runTaskLater(plugin, 40);
                            }
                        } catch (Exception e) {
                            Bukkit.getLogger().warning("Action bar config missung or wrong. Please check your config file.");
                            this.cancel();
                        }
                    }
                }
            }.runTaskTimer(plugin, 0, plugin.getConfig().getLong("ReminderInterval") * 20);
        }
        if(plugin.getConfig().getLong("HighMSPTWarningInterval")>0) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        try {
                            if (info.getCurrentlyUsedMSPT() > plugin.getConfig().getInt("HighMSPTThreshold")) {
                                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(plugin.getConfig().getString("HighMSPTWarningMessage")));
                            }
                        } catch (Exception e) {
                            Bukkit.getLogger().warning("Action bar config missing or wrong. Please check your config file.");
                            this.cancel();
                        }
                    }
                }
            }.runTaskTimer(plugin, 80, plugin.getConfig().getLong("HighMSPTWarningInterval") * 20);
        }
    }
}