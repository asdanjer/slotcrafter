package org.asdanjer.slotcrafter;

import org.bukkit.Bukkit;

import java.util.HashSet;
import java.util.UUID;

public class Info {
    private HashSet yeetlist;
    private Slotcrafter plugin;
    private  int currentLimit = Bukkit.getServer().getMaxPlayers();
    private  int manualCap;
    private  boolean mode;
    private boolean averageMode;
    private  int curentMspt;
    private  int averageMspt;
    private  int Msptcount;
    private HashSet<UUID> yeetablePlayers;
    private String output;


    public Info(Slotcrafter plugin) {
        this.plugin = plugin;
    }
    //getts all vlalues from this class as a nicly formatted string ready to be sent to the user
    public String getfulldebugstring(){
        String output = "";
        if(mode){
            if(averageMode){
                output += "Average MSPT: " + averageMspt + '\n';
                output += "MSPT caculated from: " + Msptcount + " Values\n";

            }else {
                output += "MSPT: " + curentMspt + "\n";
            }

        }
        else {
            output += "Manual Cap: " + manualCap + "\n";
        }
        if(yeetablePlayers== null){
            output += "No Yeetable Players\n";
        }
        else{
            if(yeetablePlayers.size()>0) {
                output += "Yeetable Players: " + yeetablePlayers.size() + "\n";
                for (UUID player : yeetablePlayers) {
                    output += Bukkit.getOfflinePlayer(player).getName() + "\n";
                }
            }
            else{
                output += "No Yeetable Players\n";
            }
        }

        return output;

    }

    public void setManualCap(int manualCap) {
        this.manualCap = manualCap;
    }

    public void setMode(boolean mode) {
        this.mode = mode;
    }

    public void setAverageMode(boolean averageMode) {
        this.averageMode = averageMode;
    }

    public void setCurentMspt(int curentMspt) {
        this.curentMspt = curentMspt;
    }

    public void setAverageMspt(int averageMspt) {
        this.averageMspt = averageMspt;
    }

    public void setMsptcount(int Msptcount) {
        this.Msptcount = Msptcount;
    }

    public void setYeetablePlayers(HashSet<UUID> yeetablePlayers) {
        this.yeetablePlayers = yeetablePlayers;
    }

}
