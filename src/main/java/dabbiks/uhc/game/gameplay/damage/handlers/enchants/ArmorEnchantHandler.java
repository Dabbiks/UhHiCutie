package dabbiks.uhc.game.gameplay.damage.handlers.enchants;

import dabbiks.uhc.game.gameplay.items.data.enchants.EnchantManager;
import dabbiks.uhc.game.gameplay.items.data.enchants.EnchantType;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ArmorEnchantHandler {

    private final EnchantManager enchantManager;

    public ArmorEnchantHandler() {
        this.enchantManager = new EnchantManager();
    }

    public double handle(Player damager, Player victim, double damage,
                         EntityDamageByEntityEvent event, EnchantType type) {

        double baseDamage = damage;
        damage = baseDamage;

        int level = enchantManager.getArmorLevel(victim, type);
        if (level == 0) return damage;

        switch (type) {
            case PROTECTION -> damage = protection(level, damage, event.isCritical());
            case STONE_SKIN -> damage = stone_skin(level, damage, event.isCritical());
            case SWIFTNESS -> swiftness(level, damager);
            case INSULATION -> damage = insulation(level, damage);
            case THORNS -> thorns(victim, damager);
            case INVULNERABILITY -> damage = invulnerability(level, damage);
        }

        damage -= baseDamage;
        return damage;
    }

    private double protection(int level, double damage, boolean isCritical) {
        return isCritical ? 0 : damage * (1 - 0.02 * level);
    }

    private double stone_skin(int level, double damage, boolean isCritical) {
        return isCritical ? damage * (1 - 0.02 * level) : 0;
    }

    private void swiftness(int level, Player damager) {
        if (damager.getHealth() > damager.getMaxHealth() / 3) return;
        damager.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * level, 0, false, true));
    }

    private double insulation(int level, double damage) {
        return damage / level;
    }

    private void thorns(Player victim, Player damager) {
        int level = enchantManager.getArmorLevel(victim, EnchantType.THORNS);
        if (level == 0) return;

        double percent = 0.015 * level;
        ItemStack[] armor = damager.getEquipment().getArmorContents();
        boolean update = false;

        for (int j = 0; j < armor.length; j++) {
            ItemStack item = armor[j];
            if (item == null || item.getType().isAir() || item.getType().getMaxDurability() == 0) continue;

            ItemMeta meta = item.getItemMeta();
            if (!(meta instanceof org.bukkit.inventory.meta.Damageable damageable) || meta.isUnbreakable()) continue;

            int max = item.getType().getMaxDurability();
            int damageToAdd = (int) (max * percent);
            int newDamage = damageable.getDamage() + damageToAdd;

            if (newDamage >= max) {
                armor[j] = null;
                damager.getWorld().playSound(damager.getLocation(), org.bukkit.Sound.ENTITY_ITEM_BREAK, 1f, 1f);
            } else {
                damageable.setDamage(newDamage);
                item.setItemMeta(meta);
            }
            update = true;
        }

        if (update) {
            damager.getEquipment().setArmorContents(armor);
        }
    }

    private double invulnerability(int level, double damage) {
        return Math.min((double) 10 / level + 4, damage);
    }

}
