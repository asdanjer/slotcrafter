package org.asdanjer.slotcrafter;

import me.lucko.spark.api.Spark;
import me.lucko.spark.api.SparkProvider;
import me.lucko.spark.api.statistic.StatisticWindow;
import me.lucko.spark.api.statistic.misc.DoubleAverageInfo;
import me.lucko.spark.api.statistic.types.GenericStatistic;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.EventListener;
import java.util.logging.Logger;
import java.util.LinkedList;

public final class Slotcrafter extends JavaPlugin implements EventListener {
    private Config pluginConfig;
    LinkedList<MsptValue> msptValues = new LinkedList<>();
    boolean mode = getConfig().getBoolean("autoMode");
    int manualCap = 1;
    Logger logger = Bukkit.getLogger();
    private YeetCommand yeetCommand;

    @Override
    public void onEnable() {
        this.pluginConfig = new Config(this);
        this.saveDefaultConfig();
        int updateinterval = getConfig().getInt("updateInterval");

        this.yeetCommand = new YeetCommand(this);
        this.getCommand("yeetme").setExecutor(yeetCommand);
        this.getCommand("yeetthem").setExecutor(yeetCommand);
        this.getCommand("setslots").setExecutor(new SlotLimitCommand(this));

        // Schedule repeating task to check MSPT and adjust player cap and yeet people
        Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
                adjustPlayerCap();
                yeetCommand.checkyeetability();
            }
        }, 0L, 20L * updateinterval);
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

    private void adjustPlayerCap() {
        double currentMSPT = getMspt();
        int currentPlayers = Bukkit.getOnlinePlayers().size();
        int minSlots = getConfig().getInt("minSlots");
        System.out.printf(String.valueOf(minSlots));
        int maxSlots = getConfig().getInt("maxSlots");
        double lowerThreshold = getConfig().getDouble("lowerMSPTThreshold");
        double upperThreshold = getConfig().getDouble("upperMSPTThreshold");

        int newPlayerCap;
        if (mode) logger.info("so true");

        if (mode) {

            logger.info("Current MSPT: " + currentMSPT);
            logger.info("Lower threshold: " + lowerThreshold);
            if (currentMSPT < lowerThreshold) {
                newPlayerCap = Math.min(currentPlayers + 1, maxSlots);
                logger.info("ajusting low: " + newPlayerCap);
            } else if (currentMSPT > upperThreshold) {
                newPlayerCap = Math.max(currentPlayers - 1, minSlots);
                logger.info("ajusting high: " + newPlayerCap);
            } else {
                newPlayerCap = Bukkit.getMaxPlayers();
                logger.info("No threshold reached");
            }
            logger.info("Current player cap: " + Bukkit.getMaxPlayers());
            logger.info("New player cap: " + newPlayerCap);


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
            return sum / msptValues.size();
        }
    }

    public void fullAuto(boolean mode) {
        this.mode = mode;
    }

    public void setManualCap(int manualCap) {
        this.manualCap = manualCap;
        mode = false;
        adjustPlayerCap();
    }

    public Config getPluginConfig() {
        return pluginConfig;
    }
}