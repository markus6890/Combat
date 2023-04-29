package com.gmail.markushygedombrowski;

import com.gmail.markushygedombrowski.combat.CombatList;
import com.gmail.markushygedombrowski.combat.HotBarMessage;
import com.gmail.markushygedombrowski.listeners.CombatListener;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CombatMain extends JavaPlugin {
    private CombatList combatList;
    private static CombatMain combatMain;
    @Override
    public void onEnable()  {
        combatMain = this;
        Settings settings = new Settings();
        FileConfiguration config = getConfig();
        settings.load(config);
        HotBarMessage hotBarMessage = new HotBarMessage();
        combatList = new CombatList(this,hotBarMessage);
        combatList.clearMap();

        CombatListener combatListener = new CombatListener(settings, combatList);
        Bukkit.getPluginManager().registerEvents(combatListener,this);

        CompletableFuture<Void> countDown = combatList.combatCoolDown();
        try {
            countDown.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void onDisable() {
    combatList.clearMap();
    }

    public static CombatMain getIninstance() {
        return combatMain;
    }
    public CombatList getCombatList() {
        return combatList;
    }
}
