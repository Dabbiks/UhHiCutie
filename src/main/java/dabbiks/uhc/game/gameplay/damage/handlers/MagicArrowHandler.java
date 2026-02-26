package dabbiks.uhc.game.gameplay.damage.handlers;

import dabbiks.uhc.game.gameplay.damage.handlers.ArmorHandler;
import dabbiks.uhc.game.gameplay.items.data.attributes.AttributeType;
import dabbiks.uhc.game.gameplay.items.data.enchants.EnchantType;
import dabbiks.uhc.game.teams.TeamUtils;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import static dabbiks.uhc.Main.indicatorManager;

public class MagicArrowHandler {

    private static final ArmorHandler armorHandler = new ArmorHandler();

    public static void launch(Player shooter, Arrow originalArrow, NBTItem nbt) {
        double force = Math.min(originalArrow.getVelocity().length() / 3.0, 1.0);
        int magicLevel = nbt.getInteger(EnchantType.MAGIC_ARROW.name());
        int powerLevel = nbt.hasTag(EnchantType.POWER.name()) ? nbt.getInteger(EnchantType.POWER.name()) : 0;
        boolean hasFlame = nbt.hasTag("FLAME");
        double baseDamage = nbt.hasTag(AttributeType.RANGED_DAMAGE.name()) ? nbt.getDouble(AttributeType.RANGED_DAMAGE.name()) : 2.0;

        double damage = (baseDamage * force) + (powerLevel * 1.25);
        double speed = 2.5 + (magicLevel * 0.4);

        Location loc = shooter.getEyeLocation();
        Vector dir = loc.getDirection().normalize().multiply(speed);
        JavaPlugin plugin = JavaPlugin.getProvidingPlugin(MagicArrowHandler.class);

        new BukkitRunnable() {
            int distanceTraveled = 0;

            @Override
            public void run() {
                if (distanceTraveled >= (100 / speed) || !loc.getBlock().isPassable()) {
                    this.cancel();
                    return;
                }

                loc.add(dir);
                loc.getWorld().spawnParticle(hasFlame ? Particle.FLAME : Particle.SNOWFLAKE, loc, 3, 0.05, 0.05, 0.05, 0.02, null, true);

                for (Entity entity : loc.getWorld().getNearbyEntities(loc, 0.5, 0.5, 0.5)) {
                    if (entity instanceof Player player && TeamUtils.isPlayerAlly(player, shooter)) { this.cancel(); return; }
                    if (entity instanceof LivingEntity victim && !entity.equals(shooter)) {
                        handleHit(shooter, victim, damage, hasFlame, plugin);
                        this.cancel();
                        return;
                    }
                }
                distanceTraveled++;
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
                    if (t >= 80 || victim.isDead()) { this.cancel(); return; }
                    victim.setFreezeTicks(60);
                    t++;
                }
            }.runTaskTimer(plugin, 0L, 1L);
        }
    }
}