package org.asdanjer.slotcrafter;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

public class Persistency {
    private Slotcrafter plugin;
    private YeetCommand yeetCommand;
    private TakeMySlotCommand takeMySlotCommand;
    private final NamespacedKey yeetstorage;
    private final NamespacedKey takeslotdelay;
    //constructor
    public Persistency(Slotcrafter plugin, YeetCommand yeetCommand, TakeMySlotCommand takeMySlotCommand) {
        this.plugin = plugin;
        this.yeetCommand = yeetCommand;
        this.takeMySlotCommand = takeMySlotCommand;
        yeetstorage= new NamespacedKey(plugin,"yeetmspt");
        takeslotdelay= new NamespacedKey(plugin,"takeslotdelay");

    }
    public void loadData(Player player){
        PersistentDataContainer pdc= player.getPersistentDataContainer();
        if(pdc.has(yeetstorage,PersistentDataType.INTEGER)){
            new BukkitRunnable() {
                @Override
                public void run() {
                    try{
                        int yeetmspt=pdc.get(yeetstorage,PersistentDataType.INTEGER);
                        if (yeetmspt>-1){
                            yeetCommand.addYeetablePlayer(player.getUniqueId(),yeetmspt);
                        }else{
                            yeetCommand.addYeetablePlayer(player.getUniqueId());
                        }
                    }catch (Exception e){
                        yeetCommand.addYeetablePlayer(player.getUniqueId());
                    }
                }
            }.runTaskLater(plugin, 1200);
        }
        if(pdc.has(takeslotdelay,PersistentDataType.INTEGER)){
            try {
                int delayTime = pdc.get(takeslotdelay, PersistentDataType.INTEGER);
                if(delayTime<0) delayTime=plugin.getConfig().getInt("defaultTakeMySlotTime",1);
                long delayTimeEnd = System.currentTimeMillis() + (Math.max(delayTime * 3600000, 0));
                takeMySlotCommand.addPlayer(player.getUniqueId(),delayTimeEnd);
            }catch (Exception e){}
        }
    }
    public void togglePersitencyYeet(Player player, int value, boolean enable){
        PersistentDataContainer pdc= player.getPersistentDataContainer();
        if(!enable){
            if(pdc.has(yeetstorage,PersistentDataType.INTEGER)) pdc.remove(yeetstorage);
        }else{
            pdc.set(yeetstorage,PersistentDataType.INTEGER,value);
        }
    }
    public void togglePersitencyTakeslot(Player player, int value,boolean enable){
        PersistentDataContainer pdc= player.getPersistentDataContainer();
        if(!enable){
            if(pdc.has(takeslotdelay,PersistentDataType.INTEGER)) pdc.remove(takeslotdelay);
        }else{
            pdc.set(takeslotdelay,PersistentDataType.INTEGER,value);
        }
    }
}
