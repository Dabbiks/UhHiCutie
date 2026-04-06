package dabbiks.uhc.game.gameplay.damage.handlers;

import dabbiks.uhc.game.gameplay.items.data.attributes.AttributeType;
import dabbiks.uhc.player.data.session.SessionData;
import dabbiks.uhc.player.data.session.SessionDataManager;
import dabbiks.uhc.player.data.session.SessionTags;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class CriticalHitHandler {

    public double handle(Player player, double damage, boolean critical) {
        if (!critical) return 0.0;

        SessionData sessionData = SessionDataManager.getData(player.getUniqueId());
        if (sessionData.hasTag(SessionTags.SELF_BURN) || sessionData.hasTag(SessionTags.STRONG_SELF_BURN)) {
            int addTicks = sessionData.hasTag(SessionTags.STRONG_SELF_BURN) ? 60 : 40;
            player.setFireTicks(player.getFireTicks() > 0 ? player.getFireTicks() + addTicks : addTicks);
        }

        double originalDamage = damage;

        damage = applyBonus(player.getInventory().getItemInMainHand(), damage, EquipmentSlot.HAND);
        damage = applyBonus(player.getInventory().getItemInOffHand(), damage, EquipmentSlot.OFF_HAND);

        return damage - originalDamage;
    }

    private double applyBonus(ItemStack item, double damage, EquipmentSlot slot) {
        if (item == null || item.getType().isAir()) return damage;

        NBTItem nbtItem = new NBTItem(item);

        if (nbtItem.hasTag("SLOT")) {
            if (!EquipmentSlot.valueOf(nbtItem.getString("SLOT")).equals(slot)) return damage;
        }

        double newDamage = damage;

        String flatKey = AttributeType.CRIT_DAMAGE.name();
        if (nbtItem.hasTag(flatKey)) {
            newDamage += nbtItem.getDouble(flatKey);
        }

        String percentKey = AttributeType.CRIT_DAMAGE_PERCENT.name();
        if (nbtItem.hasTag(percentKey)) {
            double percentValue = nbtItem.getDouble(percentKey);
            newDamage += damage * (percentValue / 100.0);
        }

        return newDamage;
    }
}