package dabbiks.uhc.game.gameplay.damage.handlers.enchants;

import dabbiks.uhc.game.gameplay.items.data.enchants.EnchantType;
import de.tr7zw.nbtapi.NBT;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Trident;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class ProjectileEnchantHandler {

    public double handle(Projectile projectile, LivingEntity victim, double damage) {
        double enchantDamage = 0;

        if (projectile instanceof Arrow) {
            if (NBT.getPersistentData(projectile, nbt -> nbt.hasTag(EnchantType.POWER.name()))) {
                enchantDamage += power(NBT.getPersistentData(projectile, nbt -> nbt.getInteger(EnchantType.POWER.name())));
            }
            if (NBT.getPersistentData(projectile, nbt -> nbt.hasTag(EnchantType.GLOWING.name()))) {
                glowing(NBT.getPersistentData(projectile, nbt -> nbt.getInteger(EnchantType.GLOWING.name())), victim);
            }
            if (NBT.getPersistentData(projectile, nbt -> nbt.hasTag(EnchantType.PYROTECHNICS.name()))) {
                pyrotechnics(NBT.getPersistentData(projectile, nbt -> nbt.getInteger(EnchantType.PYROTECHNICS.name())), victim);
            }
        }

        else if (projectile instanceof Trident) {
            if (NBT.getPersistentData(projectile, nbt -> nbt.hasTag(EnchantType.GROUNDING.name()))) {
                grounding(NBT.getPersistentData(projectile, nbt -> nbt.getInteger(EnchantType.GROUNDING.name())), victim);
            }
            if (NBT.getPersistentData(projectile, nbt -> nbt.hasTag(EnchantType.CHANNELING.name()))) {
                channeling(NBT.getPersistentData(projectile, nbt -> nbt.getInteger(EnchantType.CHANNELING.name())), victim);
            }
        }

        return enchantDamage;
    }

    private double power(int level) { return level * 1.25; }

    private void glowing(int level, LivingEntity victim) {
        victim.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 40 * level, 0, false, true));
    }

    private void pyrotechnics(int level, LivingEntity victim) {
        Location loc = victim.getLocation();
        World world = loc.getWorld();
        if (world == null) return;

        world.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.2f);
        world.spawnParticle(Particle.EXPLOSION, loc, 1);

        world.getNearbyEntities(loc, 3.5, 3.5, 3.5).forEach(entity -> {
            if (entity instanceof LivingEntity target) {
                target.damage(5.0 * level);
                target.setFireTicks(40 * level);

                Vector push = target.getLocation().toVector().subtract(loc.toVector());

                if (push.lengthSquared() == 0.0) {
                    push = new Vector(0, 0.4, 0);
                } else {
                    push.normalize().multiply(0.8).setY(0.4);
                }

                target.setVelocity(push);
            }
        });
    }

    private void grounding(int level, LivingEntity victim) {
        if (victim.isGliding()) {
            victim.setGliding(false);
        }
        victim.setVelocity(victim.getVelocity().setY(-0.5 * level));
    }

    private void channeling(int level, LivingEntity victim) {
        Location loc = victim.getLocation();
        World world = loc.getWorld();
        if (world == null) return;

        world.strikeLightningEffect(loc);
        victim.damage(2.0 * level);

        world.spawnParticle(Particle.ELECTRIC_SPARK, loc, 15, 0.5, 1.0, 0.5, 0.1);
        world.playSound(loc, Sound.ITEM_TRIDENT_THUNDER, 1.0f, 1.0f);
    }
}