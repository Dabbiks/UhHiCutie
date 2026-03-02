package dabbiks.uhc.game.gameplay.damage.handlers;

import dabbiks.uhc.game.gameplay.items.data.attributes.AttributeType;
import dabbiks.uhc.game.gameplay.items.data.enchants.EnchantType;
import dabbiks.uhc.game.teams.TeamUtils;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadableItemNBT;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.function.Function;

import static dabbiks.uhc.Main.indicatorManager;

public class MagicArrowHandler {

    private static final ArmorHandler armorHandler = new ArmorHandler();

    public static void launch(Player shooter, Arrow originalArrow, ItemStack weapon) {
        double force = Math.min(originalArrow.getVelocity().length() / 3.0, 1.0);

        int magicLevel = NBT.get(weapon, nbt -> nbt.hasTag(EnchantType.MAGIC_ARROW.name()) ? nbt.getInteger(EnchantType.MAGIC_ARROW.name()) : 0);
        int powerLevel = NBT.get(weapon, nbt -> nbt.hasTag(EnchantType.POWER.name()) ? nbt.getInteger(EnchantType.POWER.name()) : 0);
        boolean hasFlame = Boolean.TRUE.equals(NBT.get(weapon, (Function<ReadableItemNBT, Object>) nbt -> nbt.hasTag("FLAME")));
        double baseDamage = NBT.get(weapon, nbt -> nbt.hasTag(AttributeType.RANGED_DAMAGE.name()) ? nbt.getDouble(AttributeType.RANGED_DAMAGE.name()) : 2.0);

        double damage = (baseDamage * force) + (powerLevel * 1.25);
        double speed = 2.5 + (magicLevel * 0.4);

        Location loc = shooter.getEyeLocation();
        Vector dir = loc.getDirection().normalize();
        JavaPlugin plugin = JavaPlugin.getProvidingPlugin(MagicArrowHandler.class);

        originalArrow.remove();

        new BukkitRunnable() {
            int distanceLimit = 100;
            double currentDistance = 0;

            @Override
            public void run() {
                if (currentDistance >= distanceLimit) {
                    this.cancel();
                    return;
                }

                for (double step = 0; step < speed; step += 0.2) {
                    loc.add(dir.clone().multiply(0.2));
                    currentDistance += 0.2;

                    if (!loc.getBlock().isPassable()) {
                        this.cancel();
                        return;
                    }

                    loc.getWorld().spawnParticle(hasFlame ? Particle.FLAME : Particle.SNOWFLAKE, loc, 1, 0, 0, 0, 0.01, null, true);

                    for (Entity entity : loc.getWorld().getNearbyEntities(loc, 0.3, 0.3, 0.3)) {
                        if (entity.equals(shooter)) continue;

                        if (entity instanceof Player player && TeamUtils.isPlayerAlly(player, shooter)) {
                            this.cancel();
                            return;
                        }

                        if (entity instanceof LivingEntity victim) {
                            handleHit(shooter, victim, damage, hasFlame, plugin);
                            this.cancel();
                            return;
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private static void handleHit(Player shooter, LivingEntity victim, double damage, boolean flame, JavaPlugin plugin) {
        double finalDamage = damage;
        if (victim instanceof Player victimPlayer) {
            finalDamage = armorHandler.handle(shooter, victimPlayer, damage);
        }

        victim.damage(finalDamage, shooter);
        indicatorManager.spawnDamageIndicator(victim, finalDamage, false);

        if (flame) {
            victim.setFireTicks(60);
        } else {
            new BukkitRunnable() {
                int t = 0;
                @Override
                public void run() {
                    if (t >= 80 || victim.isDead()) {
                        this.cancel();
                        return;
                    }
                    victim.setFreezeTicks(60);
                    t++;
                }
            }.runTaskTimer(plugin, 0L, 1L);
        }
    }
}

