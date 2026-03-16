package dabbiks.uhc.game.gameplay.damage.handlers;

import dabbiks.uhc.game.gameplay.items.data.attributes.AttributeType;
import dabbiks.uhc.game.gameplay.items.data.enchants.EnchantManager;
import dabbiks.uhc.game.gameplay.items.data.enchants.EnchantType;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadableItemNBT;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.function.Function;

import static dabbiks.uhc.Main.soundU;

public class BoomerangHandler {

    private final ArmorHandler armorHandler = new ArmorHandler();
    private final EnchantManager enchantManager = new EnchantManager();

    public boolean handleHit(Player thrower, LivingEntity victim, ItemStack boomerangItem, boolean returning) {
        Double baseDamage = NBT.get(boomerangItem, (Function<ReadableItemNBT, Double>) nbt -> nbt.getDouble(AttributeType.RANGED_DAMAGE.name()));
        if (baseDamage == null) baseDamage = 1.0;

        int sharpness = enchantManager.getItemLevel(boomerangItem, EnchantType.SHARPED);
        baseDamage += (sharpness * 0.1);

        if (returning) {
            int repercussion = enchantManager.getItemLevel(boomerangItem, EnchantType.REPERCUSSION);
            baseDamage += (repercussion * 0.3);
        }

        double finalDamage = armorHandler.handle(thrower, victim, baseDamage);

        victim.damage(finalDamage, thrower);
        victim.setNoDamageTicks(0);

        boolean killed = victim.isDead() || victim.getHealth() <= 0;

        int erosion = enchantManager.getItemLevel(boomerangItem, EnchantType.EROSION);
        if (erosion <= 0 || victim.getEquipment() == null) return killed;

        int armorDamage = erosion * 2;
        for (ItemStack armor : victim.getEquipment().getArmorContents()) {
            if (armor == null || armor.getType().isAir()) continue;

            ItemMeta meta = armor.getItemMeta();
            if (!(meta instanceof Damageable damageable)) continue;

            damageable.setDamage(damageable.getDamage() + armorDamage);
            if (damageable.getDamage() >= armor.getType().getMaxDurability()) {
                armor.setAmount(0);
                soundU.playSoundAtLocation(victim.getLocation(), Sound.ENTITY_ITEM_BREAK, 1f, 1f);
            } else {
                armor.setItemMeta(meta);
            }
        }

        return killed;
    }
}