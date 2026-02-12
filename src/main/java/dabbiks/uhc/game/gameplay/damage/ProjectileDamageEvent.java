package dabbiks.uhc.game.gameplay.damage;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class ProjectileDamageEvent implements Listener {

    @EventHandler
    public void onProjectileDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Projectile projectile)) return;
        if (!(projectile.getShooter() instanceof Entity shooter)) return;

        if (event.getEntity() instanceof Player) {
            processProjectileToPlayer(event, (Player) event.getEntity(), shooter);
        } else if (shooter instanceof Player) {
            processProjectileToMonster(event, shooter, event.getEntity());
        }
    }

    public void processProjectileToPlayer(EntityDamageByEntityEvent event, Player victim, Entity shooter) {
        if (shooter instanceof Player) {
        } else {
        }
    }

    public void processProjectileToMonster(EntityDamageByEntityEvent event, Entity shooter, Entity victim) {
        // Logika gdy mob obrywa strzałą od gracza
    }
}