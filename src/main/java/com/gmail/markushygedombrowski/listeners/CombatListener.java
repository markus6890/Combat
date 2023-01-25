package com.gmail.markushygedombrowski.listeners;


import com.gmail.markushygedombrowski.Settings;
import com.gmail.markushygedombrowski.combat.CombatList;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CombatListener implements Listener {
    private Settings settings;
    private CombatList combatList;

    public CombatListener(Settings settings, CombatList combatList) {
        this.settings = settings;
        this.combatList = combatList;
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player)) return;
        Player defender = (Player) entity;
        Player attacker = getAttacker(event);
        addToCombat(defender, attacker);

    }

    private void addToCombat(Player defender, Player attacker) {
        if(!defender.hasPermission("vagt.slag")) {
            combatList.addPlayerToCombat(defender,settings.getTime());
        }
        if(!attacker.hasPermission("vagt.slag")) {
            combatList.addPlayerToCombat(attacker,settings.getTime());
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        combatList.removePlayer(player);

    }
    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if(!combatList.isPlayerInCombat(player)) {
            return;
        }
        player.setHealth(0);
        combatList.removePlayer(player);
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
