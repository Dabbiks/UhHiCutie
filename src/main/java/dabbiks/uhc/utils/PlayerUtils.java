package dabbiks.uhc.utils;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Iterator;

import static dabbiks.uhc.Main.attributeManager;

public class PlayerUtils {

    public void cleanseState(Player player) {
        attributeManager.clearAllAttributes(player);
        resetMaxHealth(player);
        fullHeal(player);

        player.setFoodLevel(20);
        player.setSaturation(0);
        player.setExhaustion(0);

        player.setLevel(0);
        player.setExp(0);
        player.setTotalExperience(0);

        player.setNoDamageTicks(0);
        player.setFallDistance(0);
        player.setFireTicks(0);
        player.setArrowsInBody(0);
        player.setFreezeTicks(0);
        player.getInventory().setHeldItemSlot(0);

        player.setGlowing(false);
        player.setInvisible(false);
        player.setInvulnerable(false);
        player.setGravity(true);
        player.setCanPickupItems(true);

        player.setVisualFire(false);
        player.setInvisible(false);
        player.setInvulnerable(false);
        player.setHealthScaled(false);
        player.setSprinting(false);
        player.setSneaking(false);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setGlowing(false);
        player.setCollidable(true);
        player.setGravity(true);
        player.setCanPickupItems(true);

        player.closeInventory();
        clearInventory(player);

        player.setPlayerListName(player.getName());
        player.setDisplayName(player.getName());
        player.setCustomChatCompletions(Collections.emptyList());

        player.setGameMode(GameMode.SURVIVAL);
        player.setCompassTarget(player.getLocation());
        player.setLastDeathLocation(null);
        player.setRespawnLocation(null);

        player.resetCooldown();
        player.resetPlayerWeather();
        player.resetPlayerTime();
        player.resetTitle();
        player.resetIdleDuration();

        removeEffects(player);


        player.getPersistentDataContainer().getKeys().forEach(key -> {
            player.getPersistentDataContainer().remove(key);
        });

        player.eject();
        for (Entity entity : player.getPassengers()) {
            player.removePassenger(entity);
            if (!(entity instanceof Player)) entity.remove();
        }

        for (Iterator<KeyedBossBar> it = Bukkit.getBossBars(); it.hasNext(); ) {
            KeyedBossBar bossBar = it.next();
            if (!bossBar.getPlayers().contains(player)) continue;
            bossBar.removePlayer(player);
        }

    }

    public void resetMaxHealth(@NotNull Player player) {
        AttributeInstance healthAttribute = player.getAttribute(Attribute.MAX_HEALTH);
        if (healthAttribute != null) healthAttribute.setBaseValue(healthAttribute.getDefaultValue()*2);
    }

    public void fullHeal(@NotNull Player player) {
        AttributeInstance healthAttribute = player.getAttribute(Attribute.MAX_HEALTH);
        if (healthAttribute != null) player.setHealth(healthAttribute.getValue());
    }

    public void clearInventory(@NotNull Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.getEnderChest().clear();
    }

    public void removeEffects(@NotNull Player player) {
        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            player.removePotionEffect(potionEffect.getType());
        }
    }

    public void addHealth(@NotNull Player player, double health) {
        if (player.getHealth() + health >= player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
            return;
        }
        player.setHealth(player.getHealth()+health);
    }

    public void removeFood(@NotNull Player player, int food) {
        if (player.getFoodLevel() - food < 0) {
            player.setFoodLevel(0);
            return;
        }
        player.setFoodLevel(player.getFoodLevel()-food);
    }

    public void damage(Player victim, Player attacker, double health) {
        if (attacker == null && victim.getHealth() <= health) {
            victim.damage(health, DamageSource.builder(DamageType.FALL).build());
            return;
        }
        if (victim.getHealth() <= health) {
            victim.damage(health, attacker);
            return;
        }
        victim.setHealth(victim.getHealth()-health);
    }

}
