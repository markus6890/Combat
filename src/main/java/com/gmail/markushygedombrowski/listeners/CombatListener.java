package com.gmail.markushygedombrowski.listeners;


import com.gmail.markushygedombrowski.CombatMain;
import com.gmail.markushygedombrowski.Settings;
import com.gmail.markushygedombrowski.combat.CombatList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class CombatListener implements Listener {
    private Settings settings;
    private CombatList combatList;
    private CombatMain plugin;
    public CombatListener(Settings settings, CombatList combatList, CombatMain plugin) {
        this.settings = settings;
        this.combatList = combatList;
        this.plugin = plugin;
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if(entity.hasMetadata("NPC")) {
            return;
        }
        if(event.getDamager().hasMetadata("NPC")) {
            return;
        }
         if (!(entity instanceof Player)) {
             return;
         }

        Player defender = (Player) entity;
        Player attacker = getAttacker(event);
        addToCombat(defender, attacker);
        addLastHitPlayer(defender,attacker);

    }

    private void addToCombat(Player defender, Player attacker) {


        if(combatList.isPlayerInCombat(defender)) {
            combatList.setTime(defender,settings.getTime());
        } else if (!defender.hasPermission("vagt.slag")) {
            combatList.addPlayerToCombat(defender,settings.getTime());
            defender.setMetadata("combat",new FixedMetadataValue(plugin,"true"));
        }

        if(combatList.isPlayerInCombat(attacker)) {
            combatList.setTime(attacker,settings.getTime());
        } else if(!attacker.hasPermission("vagt.slag")) {
            combatList.addPlayerToCombat(attacker,settings.getTime());
            attacker.setMetadata("combat",new FixedMetadataValue(plugin,"true"));
        }

    }
    private void addLastHitPlayer(Player defender,Player attacker) {
        if(!(combatList.getLastHit(defender) == attacker)) {
            combatList.removeLastHit(defender);
            combatList.addLastHit(defender,attacker);
        }

    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        combatList.removePlayer(player);
        combatList.removeLastHit(player);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if(!combatList.isPlayerInCombat(player)) {
            return;
        }
        if(player.hasPermission("vagt.slag")) {
            return;
        }
        player.setHealth(0);
        Bukkit.broadcastMessage("§c" + player.getName() + " §7logged ud i combat");
        combatList.removePlayer(player);
        combatList.removeLastHit(player);
    }




    private Player getAttacker(EntityDamageByEntityEvent event) {
        Player p;
        if (event.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) event.getDamager();
            p = (Player) projectile.getShooter();
            return p;
        }

        p = (Player) event.getDamager();
        return p;
    }
}
