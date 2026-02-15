package dabbiks.uhc.game.gameplay.damage;

import dabbiks.uhc.game.gameplay.damage.handlers.ArmorHandler;
import dabbiks.uhc.game.gameplay.damage.handlers.AttributeHandler;
import dabbiks.uhc.game.gameplay.damage.handlers.TagHandler;
import dabbiks.uhc.game.gameplay.damage.handlers.enchants.ArmorEnchantHandler;
import dabbiks.uhc.game.gameplay.damage.handlers.enchants.ProjectileEnchantHandler;
import dabbiks.uhc.game.gameplay.items.data.attributes.AttributeType;
import dabbiks.uhc.game.gameplay.items.data.enchants.EnchantType;
import dabbiks.uhc.game.teams.TeamUtils;
import dabbiks.uhc.player.PlayerState;
import de.tr7zw.nbtapi.NBT;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import static dabbiks.uhc.Main.stateU;

public class ProjectileHit implements Listener {

    private final ProjectileEnchantHandler projectileEnchantHandler = new ProjectileEnchantHandler();
    private final ArmorEnchantHandler armorEnchantHandler = new ArmorEnchantHandler();
    private final ArmorHandler armorHandler = new ArmorHandler();
    private final TagHandler tagHandler = new TagHandler();

    @EventHandler
    public void onProjectileDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Projectile projectile)) return;
        if (!(projectile.getShooter() instanceof Entity shooter)) return;

        if (event.getEntity() instanceof Player victim) {
            processProjectileToPlayer(event, victim, projectile, shooter);
        } else if (shooter instanceof Player playerShooter) {
            processProjectileToMonster(event, playerShooter, projectile, event.getEntity());
        }
    }

    public void processProjectileToPlayer(EntityDamageByEntityEvent event, Player victim, Projectile projectile, Entity shooterEntity) {
        if (!(shooterEntity instanceof Player damager)) return;

        if (TeamUtils.isPlayerAlly(damager, victim)) {
            event.setCancelled(true);
            return;
        }
        if (stateU.getPlayerState(damager) != PlayerState.ALIVE
                || stateU.getPlayerState(victim) != PlayerState.ALIVE) {
            event.setCancelled(true);
            return;
        }

        final double baseDamage = event.getDamage();
        double damage = baseDamage;

        damage += getProjectileBonusDamage(projectile);
        damage += tagHandler.handle(damager, victim, baseDamage);
        damage = projectileEnchantHandler.handle(projectile, victim, damage);
        damage = armorEnchantHandler.handle(damager, victim, damage, event, EnchantType.INVULNERABILITY);
        damage = armorHandler.handle(damager, victim, damage);

        event.setDamage(damage);
    }

    public void processProjectileToMonster(EntityDamageByEntityEvent event, Player damager, Projectile projectile, Entity entityVictim) {
        if (!(entityVictim instanceof LivingEntity victim)) return;

        final double baseDamage = event.getDamage();
        double damage = baseDamage;

        damage += getProjectileBonusDamage(projectile);
        damage += tagHandler.handle(damager, victim, baseDamage);
        damage = projectileEnchantHandler.handle(projectile, victim, damage);

        damage = armorHandler.handle(damager, victim, damage);

        event.setDamage(damage);
    }

    private double getProjectileBonusDamage(Projectile projectile) {
        return NBT.getPersistentData(projectile, nbt -> {
            if (nbt.hasTag(AttributeType.ARROW_DAMAGE.getName())) {
                return nbt.getDouble(AttributeType.ARROW_DAMAGE.getName());
            }
            return 0.0;
        });
    }
}