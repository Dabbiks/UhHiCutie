package dabbiks.uhc.game.gameplay.damage.handlers;

import dabbiks.uhc.game.gameplay.items.data.attributes.AttributeType;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class CriticalHitHandler {

    public double handle(Player player, double damage, boolean critical) {
        if (!critical) return 0.0;

        double originalDamage = damage;

        damage = applyBonus(player.getInventory().getItemInMainHand(), damage, EquipmentSlot.HAND);
        damage = applyBonus(player.getInventory().getItemInOffHand(), damage, EquipmentSlot.OFF_HAND);

        return damage - originalDamage;
    }

    private double applyBonus(ItemStack item, double damage, EquipmentSlot slot) {
        if (item == null || item.getType().isAir()) return damage;

        NBTItem nbtItem = new NBTItem(item);
        String key = AttributeType.CRIT_DAMAGE.name();

        if (!nbtItem.hasTag(key)) return damage;

        if (nbtItem.hasTag("SLOT")) {
            if (!EquipmentSlot.valueOf(nbtItem.getString("SLOT")).equals(slot)) return damage;
        }

        double value = nbtItem.getDouble(key);
        boolean isPercent = nbtItem.getBoolean(key + "_PERCENT");

        return isPercent ? damage * (1.0 + value) : damage + value;
    }
}
