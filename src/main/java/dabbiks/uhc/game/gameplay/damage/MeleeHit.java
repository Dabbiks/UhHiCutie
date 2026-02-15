package dabbiks.uhc.game.gameplay.damage;

import dabbiks.uhc.game.gameplay.damage.handlers.*;
import dabbiks.uhc.game.gameplay.damage.handlers.enchants.ArmorEnchantHandler;
import dabbiks.uhc.game.gameplay.damage.handlers.enchants.MeleeEnchantHandler;
import dabbiks.uhc.game.gameplay.items.data.attributes.AttributeType;
import dabbiks.uhc.game.gameplay.items.data.enchants.EnchantType;
import dabbiks.uhc.game.teams.TeamUtils;
import dabbiks.uhc.player.PlayerState;
import dabbiks.uhc.player.data.persistent.PersistentData;
import dabbiks.uhc.player.data.persistent.PersistentDataManager;
import dabbiks.uhc.player.data.session.SessionData;
import dabbiks.uhc.player.data.session.SessionDataManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import static dabbiks.uhc.Main.indicatorManager;
import static dabbiks.uhc.Main.stateU;

public class MeleeHit implements Listener {

    ParryingHandler parryingHandler = new ParryingHandler();
    CriticalHitHandler criticalHitHandler = new CriticalHitHandler();
    TagHandler tagHandler = new TagHandler();
    ArmorHandler armorHandler = new ArmorHandler();
    AttributeHandler attributeHandler = new AttributeHandler();
    DeathHandler deathHandler = new DeathHandler();

    MeleeEnchantHandler meleeEnchantHandler = new MeleeEnchantHandler();
    ArmorEnchantHandler armorEnchantHandler = new ArmorEnchantHandler();

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event instanceof EntityDamageByEntityEvent) && event.getEntity() instanceof Player) {
            processEnvironmentDamage(event);
            return;
        }

        if (!(event instanceof EntityDamageByEntityEvent entityEvent)) {
            return;
        }

        if (entityEvent.getDamager() instanceof Projectile) return;

        if (!(entityEvent.getDamager() instanceof Player) && !(entityEvent.getEntity() instanceof Player)) return;

        if (entityEvent.getEntity() instanceof Player && !(entityEvent.getDamager() instanceof Player)) {
            processDamageByMonster(entityEvent);
        } else if (!(entityEvent.getEntity() instanceof Player) && entityEvent.getDamager() instanceof Player) {
            processDamageToMonster(entityEvent);
        } else {
            processPlayerDamage(entityEvent);
        }
    }

    public void processEnvironmentDamage(EntityDamageEvent event) {
        Player victim = (Player) event.getEntity();
        if (stateU.getPlayerState(victim) != PlayerState.ALIVE) return;

        final double baseDamage = event.getDamage();
        double damage = baseDamage;

        damage += tagHandler.handle(victim, null, baseDamage);

        damage = armorHandler.handle(null, victim, damage);

        event.setDamage(damage);
        event.setDamage(EntityDamageEvent.DamageModifier.ARMOR, 0);
        damage = event.getFinalDamage();
        indicatorManager.spawnDamageIndicator(victim, damage, false);

        if (damage >= victim.getHealth()) { event.setCancelled(true); deathHandler.handle(victim); }
    }

    public void processDamageByMonster(EntityDamageByEntityEvent event) {
        Player victim = (Player) event.getEntity();
        if (stateU.getPlayerState(victim) != PlayerState.ALIVE) return;
        Entity damager = event.getDamager();

        if (!(damager instanceof LivingEntity)) return;

        final double baseDamage = event.getDamage();
        double damage = baseDamage;

        if (parryingHandler.handle(victim, event)) return;
        damage += tagHandler.handle(damager, victim, baseDamage);

        damage = armorHandler.handle(null, victim, damage);

        event.setDamage(damage);
        event.setDamage(EntityDamageEvent.DamageModifier.ARMOR, 0);
        damage = event.getFinalDamage();
        indicatorManager.spawnDamageIndicator(victim, damage, event.isCritical());

        if (damage >= victim.getHealth()) { event.setCancelled(true); deathHandler.handle(victim); }
    }

    public void processDamageToMonster(EntityDamageByEntityEvent event) {
        Player damager = (Player) event.getDamager();
        Entity victim = event.getEntity();

        if (!(victim instanceof LivingEntity)) return;

        final double baseDamage = event.getDamage();
        double damage = baseDamage;

        damage += criticalHitHandler.handle(damager, baseDamage, event.isCritical());
        damage += tagHandler.handle(damager, victim, baseDamage);
        damage += attributeHandler.handle(damager, (LivingEntity) victim, damage, AttributeType.ELECTRIC_DAMAGE);

        damage += meleeEnchantHandler.handle(damager, (LivingEntity) victim, baseDamage, event, EnchantType.SHARPNESS);
        damage += meleeEnchantHandler.handle(damager, (LivingEntity) victim, baseDamage, event, EnchantType.SUNDER);
        damage += meleeEnchantHandler.handle(damager, (LivingEntity) victim, baseDamage, event, EnchantType.SLUDGE);
        damage += meleeEnchantHandler.handle(damager, (LivingEntity) victim, baseDamage, event, EnchantType.POISON);
        damage += meleeEnchantHandler.handle(damager, (LivingEntity) victim, baseDamage, event, EnchantType.IGNITE);
        damage += meleeEnchantHandler.handle(damager, (LivingEntity) victim, baseDamage, event, EnchantType.SHATTER);
        damage += meleeEnchantHandler.handle(damager, (LivingEntity) victim, baseDamage, event, EnchantType.UNSTABLE_CORE);

        damage = armorHandler.handle(damager, (LivingEntity) victim, damage);
        attributeHandler.handle(damager, (LivingEntity) victim, damage, AttributeType.LIFE_STEAL);

        event.setDamage(damage);
        event.setDamage(EntityDamageEvent.DamageModifier.ARMOR, 0);
        damage = event.getFinalDamage();
        indicatorManager.spawnDamageIndicator(victim, damage, event.isCritical());
    }

    public void processPlayerDamage(EntityDamageByEntityEvent event) {
        Player damager = (Player) event.getDamager();
        Player victim = (Player) event.getEntity();

        if (stateU.getPlayerState(damager) != PlayerState.ALIVE) return;
        if (stateU.getPlayerState(victim) != PlayerState.ALIVE) return;

        SessionData sessionData = SessionDataManager.getData(victim.getUniqueId());
        sessionData.setDamager(victim, damager);

        final double baseDamage = event.getDamage();
        double damage = baseDamage;

        if (parryingHandler.handle(victim, event)) return;
        if (TeamUtils.isPlayerAlly(damager, victim)) return;

        damage += criticalHitHandler.handle(damager, baseDamage, event.isCritical());
        damage += tagHandler.handle(damager, victim, baseDamage);
        damage += attributeHandler.handle(damager, victim, damage, AttributeType.ELECTRIC_DAMAGE);
        damage += meleeEnchantHandler.handle(damager, victim, baseDamage, event, EnchantType.SHARPNESS);
        damage += meleeEnchantHandler.handle(damager, victim, baseDamage, event, EnchantType.SUNDER);
        damage += meleeEnchantHandler.handle(damager, victim, baseDamage, event, EnchantType.HASTE);
        damage += meleeEnchantHandler.handle(damager, victim, baseDamage, event, EnchantType.SLUDGE);
        damage += meleeEnchantHandler.handle(damager, victim, baseDamage, event, EnchantType.POISON);
        damage += meleeEnchantHandler.handle(damager, victim, baseDamage, event, EnchantType.IGNITE);
        damage += meleeEnchantHandler.handle(damager, victim, baseDamage, event, EnchantType.SHATTER);
        damage += meleeEnchantHandler.handle(damager, victim, baseDamage, event, EnchantType.LEAPING);
        damage += meleeEnchantHandler.handle(damager, victim, baseDamage, event, EnchantType.UNSTABLE_CORE);

        damage += armorEnchantHandler.handle(damager, victim, baseDamage, event, EnchantType.PROTECTION);
        damage += armorEnchantHandler.handle(damager, victim, baseDamage, event, EnchantType.STONE_SKIN);
        damage += armorEnchantHandler.handle(damager, victim, baseDamage, event, EnchantType.SWIFTNESS);
        damage += armorEnchantHandler.handle(damager, victim, baseDamage, event, EnchantType.INSULATION);
        damage += armorEnchantHandler.handle(damager, victim, baseDamage, event, EnchantType.THORNS);
        damage += armorEnchantHandler.handle(damager, victim, damage, event, EnchantType.INVULNERABILITY);

        damage = armorHandler.handle(damager, victim, damage);
        attributeHandler.handle(damager, victim, damage, AttributeType.LIFE_STEAL);

        event.setDamage(damage);
        event.setDamage(EntityDamageEvent.DamageModifier.ARMOR, 0);
        damage = event.getFinalDamage();
        indicatorManager.spawnDamageIndicator(victim, damage, event.isCritical());

        if (damage >= victim.getHealth()) { event.setCancelled(true); deathHandler.handle(victim); }
    }
}