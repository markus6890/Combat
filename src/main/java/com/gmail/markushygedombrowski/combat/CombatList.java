package com.gmail.markushygedombrowski.combat;

import com.gmail.markushygedombrowski.CombatMain;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class CombatList {
    private Map<Player, Integer> combatMap = new HashMap<>();
    private CombatMain plugin;
    private HotBarMessage hotBarMessage;
    public CombatList(CombatMain plugin, HotBarMessage hotBarMessage) {
        this.plugin = plugin;
        this.hotBarMessage = hotBarMessage;
    }

    public void clearMap() {
        combatMap.clear();
    }

    public boolean isPlayerInCombat(Player player) {
        return combatMap.containsKey(player);
    }

    public int getTime(Player player) {
        return (combatMap.get(player) / 20);
    }

    public void addPlayerToCombat(Player player, int time) {
        combatMap.put(player, time);
    }

    public void removePlayer(Player player) {
        if (player == null) return;
        if (!isPlayerInCombat(player)) return;
        combatMap.remove(player);
    }

    public CompletableFuture<Void> combatCoolDown() {
        return CompletableFuture.runAsync(() -> countDown());
    }

    private void countDown() {
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                timer();
            }
        },1L,1L);
    }

    private void timer() {
        if (combatMap.isEmpty()) return;
        new HashSet<>(combatMap.entrySet()).forEach(entry -> {
            Player p = entry.getKey();
            if (p == null) {
                return;
            }
            if (!(entry.getValue() <= 0)) {
                Player player = entry.getKey();
                int timeInSeconds = (entry.getValue() / 20) + 1;
                hotBarMessage.actionBarMessage(player,timeInSeconds);
                entry.setValue(entry.getValue() - 1);
                return;
            }
            combatMap.remove(entry.getKey());

        });
    }


}
