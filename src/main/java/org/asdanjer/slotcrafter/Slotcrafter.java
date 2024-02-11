package org.asdanjer.slotcrafter;

import me.lucko.spark.api.Spark;
import me.lucko.spark.api.SparkProvider;
import me.lucko.spark.api.statistic.StatisticWindow;
import me.lucko.spark.api.statistic.misc.DoubleAverageInfo;
import me.lucko.spark.api.statistic.types.GenericStatistic;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.logging.Logger;
import java.util.LinkedList;

public final class Slotcrafter extends JavaPlugin implements Listener {

    LinkedList<MsptValue> msptValues = new LinkedList<>();
    boolean mode = getConfig().getBoolean("autoMode");
    int manualCap = 1;
    Logger logger = Bukkit.getLogger();
    private YeetCommand yeetCommand;
    private BukkitTask task;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        this.yeetCommand = new YeetCommand(this);
        SlotcrafterCommand slotcrafterCommand = new SlotcrafterCommand(this);
        getCommand("slotcrafter").setExecutor(slotcrafterCommand);
        getCommand("slotcrafter").setTabCompleter(slotcrafterCommand);
        getCommand("yeetme").setExecutor(yeetCommand);
        getCommand("yeetthem").setExecutor(yeetCommand);
        SlotLimitCommand slotLimitCommand = new SlotLimitCommand(this);
        getCommand("setslots").setExecutor(slotLimitCommand);
        getCommand("setslots").setTabCompleter(slotLimitCommand);


        // Schedule repeating task to check MSPT and adjust player cap and yeet people
        manageTaskRunner();
        setPlayerCap(getConfig().getInt("minSlots"));
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        adjustPlayerCap();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        yeetCommand.removeyeeter(event.getPlayer().getUniqueId());
        adjustPlayerCap();
    }
    public void manageTaskRunner() {
        int updateInterval = getConfig().getInt("updateInterval");

        if (task != null) {
            task.cancel();
        }

        task = Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
                adjustPlayerCap();
                yeetCommand.checkyeetability();
            }
        }, 0L, 20L * updateInterval);
    }

    private void adjustPlayerCap() {


        int newPlayerCap;
        if (mode) {
            double currentMSPT = getMspt();
            int currentPlayers = Bukkit.getOnlinePlayers().size();
            int minSlots = getConfig().getInt("minSlots");
            System.out.printf(String.valueOf(minSlots));
            int maxSlots = getConfig().getInt("maxSlots");
            double lowerThreshold = getConfig().getDouble("lowerMSPTThreshold");
            double upperThreshold = getConfig().getDouble("upperMSPTThreshold");
            if (currentMSPT < lowerThreshold) {
                newPlayerCap = Math.min(currentPlayers + 1, maxSlots);
            } else if (currentMSPT > upperThreshold) {
                newPlayerCap = Math.max(currentPlayers, minSlots);
            } else {
                newPlayerCap = Bukkit.getMaxPlayers();
            }


        } else {
            newPlayerCap = manualCap;

        }
        setPlayerCap(newPlayerCap);
    }

    private void setPlayerCap(int newPlayerCap) {
        newPlayerCap = Math.max(newPlayerCap, getConfig().getInt("minSlots"));
        logger.info("Player cap adjusted to: " + newPlayerCap);
        try {
            Bukkit.setMaxPlayers(newPlayerCap);
        } catch (Exception e) {
            logger.severe("Failed to adjust player cap: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public double getMspt() {
        Spark spark = SparkProvider.get();
        GenericStatistic<DoubleAverageInfo, StatisticWindow.MillisPerTick> mspt = spark.mspt();

        if (mspt == null) {
            logger.info("could not get mspt statistic, returning 1000 as a placeholder value. This is normal on startup.");
            return 1000;
        } else {
            // Get the MSPT value and add it to the list with the current timestamp
            double currentMspt = mspt.poll(StatisticWindow.MillisPerTick.MINUTES_1).mean();
            if(getConfig().getInt("averageMSPTInterval")<=0){
                logger.info("MSPT: " + currentMspt);
                return currentMspt;
            }
            else {
                return calculateMspt(currentMspt);
            }

        }
    }
    private double calculateMspt(double currentMspt) {
        msptValues.add(new MsptValue(System.currentTimeMillis(), currentMspt));

        // Remove MSPT values that are older than the desired timeframe
        long timeframeMillis = getConfig().getInt("averageMSPTInterval") * 1000;
        while (!msptValues.isEmpty() && msptValues.getFirst().timestamp < System.currentTimeMillis() - timeframeMillis) {
            msptValues.removeFirst();
        }

        // Calculate the average MSPT over the remaining values
        double sum = 0;
        for (MsptValue value : msptValues) {
            sum += value.mspt;
        }
        logger.info("MSPT: " + currentMspt + "Calulated from: " + msptValues.size() + " values");
        return sum / msptValues.size();

    }
    public void fullAuto(boolean mode) {
        this.mode = mode;
        adjustPlayerCap();
    }
    public void setManualCap(int manualCap) {
        this.manualCap = manualCap;
        mode = false;
        adjustPlayerCap();
    }
    public void updateConfigValue(String setting, String value) {

        switch (setting) {
            case "upperMSPTThreshold":
            case "lowerMSPTThreshold":
            case "kickmspt":
                getConfig().set(setting, Double.parseDouble(value));
                break;
            case "minSlots":
            case "maxSlots":
            case "updateInterval":
            case "averageMSPTInterval":
                getConfig().set(setting, Integer.parseInt(value));
                break;
            case "autoMode":
                getConfig().set(setting, Boolean.parseBoolean(value));
                break;
            default:
                throw new IllegalArgumentException("Invalid config setting.");
        }

        this.saveConfig();
        this.reloadConfig();
        manageTaskRunner();
    }
}