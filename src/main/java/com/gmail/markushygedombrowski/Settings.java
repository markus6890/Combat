package com.gmail.markushygedombrowski;

import org.bukkit.configuration.file.FileConfiguration;

public class Settings {


    private int time;


    public void load (FileConfiguration config) {
        this.time = (config.getInt("combat.time") * 20);

    }

    public int getTime() {
        return time;
    }
}
