package org.asdanjer.slotcrafter;

import org.bukkit.configuration.file.FileConfiguration;

public class Config {
    public double upper_mspt_threshold;
    public int min_slots;
    public int max_slots;
    public double lower_mspt_threshold;
    public int update_interval;
    public boolean auto_mode;
    public int average_mspt_interval;
    public double kick_mspt;
    private Slotcrafter plugin;
    private FileConfiguration config;
    public Config(Slotcrafter plugin) {
        this.plugin = plugin;
        config = plugin.getConfig();
        updateConfig();

    }
    public void updateConfigValue(String setting, String value) {

        switch (setting) {
            case "upper_mspt_threshold":
            case "lower_mspt_threshold":
            case "kick_mspt":
                config.set(setting, Double.parseDouble(value));
                break;
            case "min_slots":
            case "max_slots":
            case "update_interval":
            case "average_mspt_interval":
                config.set(setting, Integer.parseInt(value));
                break;
            case "auto_mode":
                config.set(setting, Boolean.parseBoolean(value));
                break;
            default:
                throw new IllegalArgumentException("Invalid config setting.");
        }

        plugin.saveConfig();
        plugin.reloadConfig();
        updateConfig();
    }
    private void  updateConfig() {
        upper_mspt_threshold = config.getDouble("upperMSPTThreshold");
        min_slots = config.getInt("minSlots");
        max_slots = config.getInt("maxSlots");
        lower_mspt_threshold = config.getDouble("lowerMSPTThreshold");
        update_interval = config.getInt("updateInterval");
        auto_mode = config.getBoolean("autoMode");
        average_mspt_interval = config.getInt("averageMSPTInterval");
        kick_mspt = config.getDouble("kickMSPT");
    }
}