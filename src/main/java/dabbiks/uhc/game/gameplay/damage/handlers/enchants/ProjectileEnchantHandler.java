package dabbiks.uhc.game.gameplay.damage.handlers.enchants;

import dabbiks.uhc.game.gameplay.items.data.enchants.EnchantType;
import de.tr7zw.nbtapi.NBTEntity;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Trident;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ProjectileEnchantHandler {

    public double handle(Projectile projectile, LivingEntity victim, double damage) {
        NBTEntity nbt = new NBTEntity(projectile);
        double baseDamage = damage;

        // Obsługa Arrow
        if (projectile instanceof Arrow) {
            if (nbt.hasTag(EnchantType.POWER.getName())) {
                damage += power(nbt.getInteger(EnchantType.POWER.getName()));
            }
            if (nbt.hasTag(EnchantType.GLOWING.getName())) {
                glowing(nbt.getInteger(EnchantType.GLOWING.getName()), victim);
            }
            if (nbt.hasTag(EnchantType.MAGIC_ARROW.getName())) {
                magic_arrow(nbt.getInteger(EnchantType.MAGIC_ARROW.getName()), victim);
            }
            if (nbt.hasTag(EnchantType.PYROTECHNICS.getName())) {
                pyrotechnics(nbt.getInteger(EnchantType.PYROTECHNICS.getName()), victim);
            }
        }

        else if (projectile instanceof Trident) {
            if (nbt.hasTag(EnchantType.GROUNDING.getName())) {
                grounding(nbt.getInteger(EnchantType.GROUNDING.getName()), victim);
            }
            if (nbt.hasTag(EnchantType.CHANNELING.getName())) {
                channeling(nbt.getInteger(EnchantType.CHANNELING.getName()), victim);
            }
        }

        return damage;
    }

    private double power(int level) { return level * 1.25; }

    private void glowing(int level, LivingEntity victim) {
        victim.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 20 * level * 2, 0, false, true));
    }

    private void magic_arrow(int level, LivingEntity victim) {
        victim.damage(level * 1.5);
    }

    private void pyrotechnics(int level, LivingEntity victim) {
        victim.setFireTicks(40 * level);
    }

    private void grounding(int level, LivingEntity victim) {
        // Logika ściągania w dół (np. przerwanie lotu Elytrą)
        if (victim.isGliding()) {
            victim.setGliding(false);
        }
        victim.setVelocity(victim.getVelocity().setY(-0.5 * level));
    }

    private void channeling(int level, LivingEntity victim) {
        // Logika pioruna (standardowy Channeling działa tylko w burzy, tu można wymusić)
        victim.getWorld().strikeLightning(victim.getLocation());
    }
}