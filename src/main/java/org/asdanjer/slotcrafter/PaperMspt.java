package org.asdanjer.slotcrafter;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.LinkedList;
import java.util.List;

import static com.sun.tools.javac.jvm.PoolWriter.MAX_ENTRIES;


public class PaperMspt implements Listener {
    private final List<Double> timeList = new LinkedList<>();
    private static final int MAX_ENTRIES = 20;
    private Plugin plugin;
    PaperMspt(Plugin plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onServerTickEvent(ServerTickEndEvent e) {
        timeList.add(e.getTickDuration());
        if (timeList.size() > MAX_ENTRIES) {
            timeList.remove(0);
        }
    }

    public double getmspt() {
        if (timeList.size() < 2) {
            return 0;
        }
        double sum = 0;
        for (double time : timeList) {
            sum += time;
        }
        return sum / timeList.size();
    }
}
