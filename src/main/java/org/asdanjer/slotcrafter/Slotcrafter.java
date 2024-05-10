package org.asdanjer.slotcrafter;

import me.lucko.spark.api.Spark;
import me.lucko.spark.api.SparkProvider;
import me.lucko.spark.api.statistic.StatisticWindow;
import me.lucko.spark.api.statistic.misc.DoubleAverageInfo;
import me.lucko.spark.api.statistic.types.GenericStatistic;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;
import de.myzelyam.api.vanish.VanishAPI;


public final class Slotcrafter extends JavaPlugin implements Listener {

    LinkedList<MsptValue> msptValues = new LinkedList<>();
    boolean mode = getConfig().getBoolean("autoMode");
    int manualCap = 1;
    Logger logger = Bukkit.getLogger();
    private YeetCommand yeetCommand;
    private BukkitTask task;
    Info info = new Info(this);
    HashMap<UUID, Long> recentLeavers = new HashMap<>();
    private boolean slotsoppen = true;
    private int realplayercap = 0;
    TakeMySlotCommand takeMySlotCommand;
    Persistency persistency;
    boolean dopersitency = true;
    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        dopersitency = getConfig().getBoolean("persistent");
        persistency = new Persistency(this);
        this.yeetCommand = new YeetCommand(this,persistency);
        takeMySlotCommand = new TakeMySlotCommand(this,persistency);
        persistency.setcommands(yeetCommand,takeMySlotCommand);
        SlotcrafterCommand slotcrafterCommand = new SlotcrafterCommand(this);
        SlotLimitCommand slotLimitCommand = new SlotLimitCommand(this);
        realplayercap=getConfig().getInt("minSlots");

        getCommand("slotcrafter").setExecutor(slotcrafterCommand);
        getCommand("slotcrafter").setTabCompleter(slotcrafterCommand);
        getCommand("yeetme").setExecutor(yeetCommand);
        getCommand("yeetme").setTabCompleter(yeetCommand);
        getCommand("yeetthem").setExecutor(yeetCommand);
        getCommand("setslots").setExecutor(slotLimitCommand);
        getCommand("setslots").setTabCompleter(slotLimitCommand);
        getCommand("takemyslot").setExecutor(takeMySlotCommand);
        getCommand("takemyslot").setTabCompleter(takeMySlotCommand);

        // Schedule repeating task to check MSPT and adjust player cap and yeet people
        manageTaskRunner();
        setPlayerCap(getConfig().getInt("minSlots"));
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new SlotcrafterPlaceholderExpansion(this,yeetCommand,takeMySlotCommand,info).register();
        }
        if(getConfig().getInt("ReminderInterval")>0||getConfig().getInt("HighMSPTWarningInterval")>0){
            ReminderActionBar reminderActionBar = new ReminderActionBar(this,yeetCommand,takeMySlotCommand,info);
            reminderActionBar.start();
        }
        logger.info("Slotcrafter has been loaded!");

    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        if (slotsoppen) {
            event.allow();
            //logger.info("Slots are open");
        } else if (!slotsoppen && event.getResult() == PlayerLoginEvent.Result.KICK_FULL && (player.hasPermission("slotcrafter.ignorecap") || isPlayerOnList(playerUUID))) {
            event.allow();
            //logger.info("Player is on list");
        } else if (kickRandomWillingPlayer()) {
            event.allow();
            //logger.info("Kicked random willing player");
        } else {
            try {
                event.disallow(PlayerLoginEvent.Result.KICK_FULL,getConfig().getString("Server_Full_MSG") );
            } catch (Exception e) {
                event.disallow(PlayerLoginEvent.Result.KICK_FULL,"Server is full.Please try again later.");
            }

            //logger.info("Server is full");
        }
    }
    private boolean kickRandomWillingPlayer() {
        long currentTime = System.currentTimeMillis();
        List<Map.Entry<UUID, Long>> validEntries = takeMySlotCommand.getSlotOfferedPlayers().entrySet().stream()
                .filter(entry -> entry.getValue() <= currentTime)
                .collect(Collectors.toList());
        if (!validEntries.isEmpty()) {
            // Create a Random object
            Random random = new Random();

            // Get a random entry from the validEntries list
            Map.Entry<UUID, Long> randomEntry = validEntries.get(random.nextInt(validEntries.size()));

            // Now you have a random entry (player) who has offered their slot and their offer time is before the current time
            UUID randomPlayerId = randomEntry.getKey();
            try {
                Bukkit.getPlayer(randomPlayerId).kickPlayer("Your slot has been taken.");
                return true;
            }catch (Exception e){
                logger.warning("Coudn't kick player on list");
                return false;
            }
        }
        return false;
    }
    private boolean isPlayerOnList(UUID playerUUID) {
        Long leaveTime = recentLeavers.get(playerUUID);
        if (leaveTime != null) {
            long currentTime = System.currentTimeMillis();
            int delayInSeconds = getConfig().getInt("rejoinDelay");
            if (currentTime - leaveTime <= delayInSeconds * 1000L) {
                recentLeavers.remove(playerUUID);
                return true;
            }
        }
        return false;
    }
    private void clearRecentLeavers() {
        long currentTime = System.currentTimeMillis();
        recentLeavers.entrySet().removeIf(entry -> currentTime - entry.getValue() > getConfig().getInt("rejoinDelay") * 1000L);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        adjustPlayerCap(false);
        persistency.loadData(event.getPlayer());
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();
        recentLeavers.put(playerUUID, System.currentTimeMillis());
        yeetCommand.removeyeeter(event.getPlayer().getUniqueId());
        adjustPlayerCap(true);
        takeMySlotCommand.removePLayer(playerUUID);
    }
    public void manageTaskRunner() {
        int updateInterval = getConfig().getInt("updateInterval");

        if (task != null) {
            task.cancel();
        }
        if (updateInterval <= 0) {
            logger.warning("Config value for updateInterval is not a positive integer. Using default value: 60");
            updateInterval = 60;
        }

        task = Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
                adjustPlayerCap(false);
                yeetCommand.checkyeetability();
                info.setYeetablePlayers(yeetCommand.getYeetablePlayers());
                info.setTakeMySlotList(takeMySlotCommand.getSlotOfferedPlayers());
                clearRecentLeavers();
            }
        }, 0L, 20L * updateInterval);
    }

    private void adjustPlayerCap(boolean isQuitEvent) {
        int newPlayerCap;
        info.setMode(mode);
        info.setManualCap(manualCap);
        int currentPlayers = 0;
        double currentMSPT = getMspt();
        currentPlayers = Bukkit.getOnlinePlayers().size();
        if (isQuitEvent) {
            currentPlayers--;
        }
        if (Bukkit.getPluginManager().isPluginEnabled("SuperVanish") || Bukkit.getPluginManager().isPluginEnabled("PremiumVanish")) {
            currentPlayers -= VanishAPI.getInvisiblePlayers().size();
            //logger.info(" " + VanishAPI.getInvisiblePlayers().size());
        }
        if (mode) {
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
                newPlayerCap = realplayercap;
            }


        } else {
            newPlayerCap = manualCap;

        }
        if (newPlayerCap <= 0) {
            newPlayerCap = 1;
        }
        //logger.info("New player cap: " + newPlayerCap + " Current players: " + currentPlayers);
        slotsoppen= newPlayerCap> currentPlayers;
        realplayercap = newPlayerCap;
        Bukkit.setMaxPlayers(realplayercap);
    }
    private void setPlayerCap(int newPlayerCap) {
        newPlayerCap = Math.max(newPlayerCap, getConfig().getInt("minSlots"));
        try {
            Bukkit.setMaxPlayers(newPlayerCap);
        } catch (Exception e) {
            logger.severe("Failed to adjust player cap: " + e.getMessage());
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
                info.setAverageMode(false);
                info.setCurentMspt((int) currentMspt);
                return currentMspt;
            }
            info.setAverageMode(true);
        return calculateMspt(currentMspt);


        }
    }
    private double calculateMspt(double currentMspt) {

        // Remove MSPT values that are older than the desired timeframe
        long timeframeMillis = (long)getConfig().getInt("averageMSPTInterval") * 1000;
        while (!msptValues.isEmpty() && msptValues.getFirst().timestamp < System.currentTimeMillis() - timeframeMillis) {
            msptValues.removeFirst();
        }
        msptValues.add(new MsptValue(System.currentTimeMillis(), currentMspt));

        // Calculate the average MSPT over the remaining values
        double sum = 0;
        for (MsptValue value : msptValues) {
            sum += value.mspt;
        }
        double avaragemspt= sum / msptValues.size();
        info.setAverageMspt((int) avaragemspt);
        info.setMsptcount(msptValues.size());
        return avaragemspt;

    }
    public void fullAuto(boolean mode) {
        this.mode = mode;
        adjustPlayerCap(false);
    }
    public void setManualCap(int manualCap) {
        this.manualCap = manualCap;
        mode = false;
        adjustPlayerCap(false);
    }
    public void updateConfigValue(String setting, String value) {
        logger.info("Updating config setting: " + setting + " to " + value);
        switch (setting) {
            case "upperMSPTThreshold":
            case "lowerMSPTThreshold":
            case "kickmspt":
            case "minSlots":
            case "maxSlots":
            case "updateInterval":
            case "averageMSPTInterval":
                getConfig().set(setting, Integer.parseInt(value));
                logger.info("Setting int  " + setting + " to " + value);
                break;
            case "autoMode":
                getConfig().set(setting, Boolean.parseBoolean(value));
                break;
            default:
                throw new IllegalArgumentException("Invalid config setting.");
        }

        this.saveConfig();
        manageTaskRunner();
    }
    public String getInfo() {

        return info.getfulldebugstring();
    }

    public int getRealplayercap() {
        return realplayercap;
    }
}