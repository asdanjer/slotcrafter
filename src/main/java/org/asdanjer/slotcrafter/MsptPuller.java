package org.asdanjer.slotcrafter;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.LinkedList;
import java.util.List;




public class MsptPuller {
    private final List<Long> timeList = new LinkedList<>();
    private static final int MAX_ENTRIES = 20;
    public MsptPuller(Plugin plugin) {
        Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            @Override
            public void run() {
                timeList.add(System.currentTimeMillis());
                if (timeList.size() > MAX_ENTRIES) {
                    timeList.remove(0);
                }
            }
        }, 0L, 1L); // 0L initial delay, 1L period (1 tick)
    }
    double getmspt(){
        if (timeList.size() < 2) {
            return 0;
        }
        long lastTime = timeList.get(timeList.size() - 1);
        long firstTime = timeList.get(0);
        return (double) (lastTime - firstTime) / (timeList.size() - 1);
    }
}
