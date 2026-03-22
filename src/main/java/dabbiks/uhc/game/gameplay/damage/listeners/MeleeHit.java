package dabbiks.uhc.game.gameplay.damage.listeners;

import dabbiks.uhc.game.GameData;
import dabbiks.uhc.game.configs.SegmentConfig;
import dabbiks.uhc.game.configs.WorldConfig;
import dabbiks.uhc.game.gameplay.damage.handlers.*;
import dabbiks.uhc.game.gameplay.damage.handlers.enchants.ArmorEnchantHandler;
import dabbiks.uhc.game.gameplay.damage.handlers.enchants.MeleeEnchantHandler;
import dabbiks.uhc.game.gameplay.items.data.attributes.AttributeType;
import dabbiks.uhc.game.gameplay.items.data.enchants.EnchantType;
import dabbiks.uhc.game.teams.TeamUtils;
import dabbiks.uhc.player.PlayerState;
import dabbiks.uhc.player.data.session.SessionData;
import dabbiks.uhc.player.data.session.SessionDataManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

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

    private double applyPotionModifiers(LivingEntity damager, LivingEntity victim, double currentDamage) {
        double multiplier = 1.0;

        if (damager != null) {
            if (damager.hasPotionEffect(PotionEffectType.STRENGTH)) {
                int amp = damager.getPotionEffect(PotionEffectType.STRENGTH).getAmplifier();
                multiplier += (amp + 1) * 0.15;
            }
            if (damager.hasPotionEffect(PotionEffectType.WEAKNESS)) {
                int amp = damager.getPotionEffect(PotionEffectType.WEAKNESS).getAmplifier();
                multiplier -= (amp + 1) * 0.15;
            }
        }

        if (victim != null) {
            if (victim.hasPotionEffect(PotionEffectType.RESISTANCE)) {
                int amp = victim.getPotionEffect(PotionEffectType.RESISTANCE).getAmplifier();
                multiplier -= (amp + 1) * 0.15;
            }
        }

        return Math.max(0.0, currentDamage * multiplier);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!event.getEntity().getWorld().getName().equals(WorldConfig.worldName)) return;
        if (SegmentConfig.actualSegment == 1) return;
        if (GameData.isEnding) { event.setCancelled(true); return; }

        if (!(event instanceof EntityDamageByEntityEvent) && event.getEntity() instanceof Player) {
            processEnvironmentDamage(event);
            return;
        }

        if (!(event instanceof EntityDamageByEntityEvent entityEvent)) {
            return;
        }

        if (entityEvent.getDamager() instanceof Projectile) return;

        if (entityEvent.getEntity() instanceof Player) {
            if (entityEvent.getDamager() instanceof Player) {
                processPlayerDamage(entityEvent);
            } else if (entityEvent.getDamager() instanceof LivingEntity) {
                processDamageByMonster(entityEvent);
            } else {
                processEnvironmentDamage(entityEvent);
            }
        } else if (entityEvent.getDamager() instanceof Player) {
            processDamageToMonster(entityEvent);
        }
    }

    public void processEnvironmentDamage(EntityDamageEvent event) {
        Player victim = (Player) event.getEntity();
        if (SegmentConfig.actualSegment == 1) {
            event.setCancelled(true); return;
        }
        if (stateU.getPlayerState(victim) != PlayerState.ALIVE) return;

        double baseDamage = event.getDamage();
        double damage = baseDamage;

        damage += tagHandler.handle(victim, null, baseDamage);
        damage += meleeEnchantHandler.handle(victim, victim, damage, null, EnchantType.IRON_FEET);

        damage = applyPotionModifiers(null, victim, damage);

        double totalDamage = damage;
        double absorption = victim.getAbsorptionAmount();

        if (totalDamage >= victim.getHealth() + absorption) {
            event.setCancelled(true);
            deathHandler.handle(victim);
            return;
        }

        if (absorption > 0) {
            double absorbed = Math.min(totalDamage, absorption);
            victim.setAbsorptionAmount(absorption - absorbed);
            damage -= absorbed;
        }

        event.setDamage(damage);
        if (event.isApplicable(EntityDamageEvent.DamageModifier.ARMOR)) event.setDamage(EntityDamageEvent.DamageModifier.ARMOR, 0);
        if (event.isApplicable(EntityDamageEvent.DamageModifier.ABSORPTION)) event.setDamage(EntityDamageEvent.DamageModifier.ABSORPTION, 0);

        indicatorManager.spawnDamageIndicator(victim, totalDamage, false);
    }

    public void processDamageByMonster(EntityDamageByEntityEvent event) {
        Player victim = (Player) event.getEntity();
        if (stateU.getPlayerState(victim) != PlayerState.ALIVE) return;
        Entity damager = event.getDamager();

        if (!(damager instanceof LivingEntity)) return;

        double baseDamage = event.getDamage();
        if (event.isCritical()) baseDamage /= 1.5;

        double damage = baseDamage;

        if (parryingHandler.handle(victim, event)) return;
        damage += tagHandler.handle(damager, victim, baseDamage);

        damage = applyPotionModifiers((LivingEntity) damager, victim, damage);
        damage = armorHandler.handle(null, victim, damage);

        double totalDamage = damage;
        double absorption = victim.getAbsorptionAmount();

        if (totalDamage >= victim.getHealth() + absorption) {
            event.setCancelled(true);
            deathHandler.handle(victim);
            return;
        }

        if (absorption > 0) {
            double absorbed = Math.min(totalDamage, absorption);
            victim.setAbsorptionAmount(absorption - absorbed);
            damage -= absorbed;
        }

        event.setDamage(damage);
        if (event.isApplicable(EntityDamageEvent.DamageModifier.ARMOR)) event.setDamage(EntityDamageEvent.DamageModifier.ARMOR, 0);
        if (event.isApplicable(EntityDamageEvent.DamageModifier.ABSORPTION)) event.setDamage(EntityDamageEvent.DamageModifier.ABSORPTION, 0);

        indicatorManager.spawnDamageIndicator(victim, totalDamage, event.isCritical());
    }

    public void processDamageToMonster(EntityDamageByEntityEvent event) {
        Player damager = (Player) event.getDamager();
        Entity victim = event.getEntity();

        if (!(victim instanceof LivingEntity)) return;

        double baseDamage = event.getDamage();
        if (event.isCritical()) baseDamage /= 1.5;

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

        damage = applyPotionModifiers(damager, (LivingEntity) victim, damage);
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

        double baseDamage = event.getDamage();
        if (event.isCritical()) baseDamage /= 1.5;

        double damage = baseDamage;

        if (parryingHandler.handle(victim, event)) return;
        if (TeamUtils.isPlayerAlly(damager, victim)) {
            event.setCancelled(true);
            return;
        }

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

        damage = applyPotionModifiers(damager, victim, damage);
        damage = armorHandler.handle(damager, victim, damage);
        attributeHandler.handle(damager, victim, damage, AttributeType.LIFE_STEAL);

        double totalDamage = damage;
        double absorption = victim.getAbsorptionAmount();

        if (totalDamage >= victim.getHealth() + absorption) {
            event.setCancelled(true);
            deathHandler.handle(victim);
            return;
        }

        if (absorption > 0) {
            double absorbed = Math.min(totalDamage, absorption);
            victim.setAbsorptionAmount(absorption - absorbed);
            damage -= absorbed;
        }

        event.setDamage(damage);
        if (event.isApplicable(EntityDamageEvent.DamageModifier.ARMOR)) event.setDamage(EntityDamageEvent.DamageModifier.ARMOR, 0);
        if (event.isApplicable(EntityDamageEvent.DamageModifier.ABSORPTION)) event.setDamage(EntityDamageEvent.DamageModifier.ABSORPTION, 0);

        indicatorManager.spawnDamageIndicator(victim, totalDamage, event.isCritical());
    }
}