package com.gmail.markushygedombrowski.combat;

import com.gmail.markushygedombrowski.CombatMain;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class CombatList {
    private Map<Player, Integer> combatMap = new HashMap<>();
    private Map<Player, Player> lastHit = new HashMap<>();
    private JavaPlugin plugin;
    private HotBarMessage hotBarMessage;

    public CombatList(JavaPlugin plugin, HotBarMessage hotBarMessage) {
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

    public void setTime(Player player, int time) {
        combatMap.replace(player, time);
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
        }, 1L, 1L);
    }

    private void timer() {
        if (combatMap.isEmpty()) return;
        new HashSet<>(combatMap.entrySet()).forEach(entry -> {
            Player p = entry.getKey();
            if (p == null) {
                return;
            }
            if(p.isDead()) {
                combatMap.remove(entry.getKey());
                lastHit.remove(entry.getKey());
                return;
            }
            if (!(entry.getValue() <= 0)) {
                Player player = entry.getKey();
                int timeInSeconds = (entry.getValue() / 20) + 1;
                hotBarMessage.actionBarMessage(player, timeInSeconds);
                entry.setValue(entry.getValue() - 1);
                return;
            }
            combatMap.remove(entry.getKey());
            lastHit.remove(entry.getKey());

        });
    }

    public void addLastHit(Player defender, Player attacker) {
        lastHit.put(defender, attacker);
    }

    public Player getLastHit(Player defender) {
        if(lastHit.get(defender) == null) return null;
        return lastHit.get(defender);
    }

    public void removeLastHit(Player player) {
        if (player == null) return;
        if (!isPlayerInCombat(player)) return;
        lastHit.remove(player);

    }

}
