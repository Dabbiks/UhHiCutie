package dabbiks.uhc.game.gameplay.damage.handlers;

import dabbiks.uhc.game.gameplay.items.data.attributes.AttributeType;
import dabbiks.uhc.game.gameplay.items.data.enchants.EnchantManager;
import dabbiks.uhc.game.gameplay.items.data.enchants.EnchantType;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static dabbiks.uhc.Main.attributeManager;

public class ArmorHandler {

    private final EnchantManager enchantManager = new EnchantManager();

    public double handle(Player damager, LivingEntity victim, double damage) {
        int armor;

        if (victim instanceof Player) armor = handlePlayer((Player) victim);
        else armor = handleMob(victim);


        if (damager != null) {
            armor -= (int) attributeManager.getAttributeValue(damager, AttributeType.ARMOR_PENETRATION);

            ItemStack item = damager.getInventory().getItemInMainHand();
            if (item.isEmpty() || item.getType() == Material.AIR) {
                return damage;
            }
            NBTItem nbtItem = new NBTItem(item);

            int level = enchantManager.getItemLevel(nbtItem, EnchantType.LETHALITY);
            if (level > 0) armor -= level * 2;
        }
        if (armor < 0) armor = 0;
        double reduction = armor / (armor + 20.0);
        return damage * (1 - reduction);
    }

    private int handlePlayer(Player victim) {
        return (int) attributeManager.getAttributeValue(victim, AttributeType.ARMOR);
    }

    private int handleMob(LivingEntity victim) {
        if (victim.getEquipment() == null) return 0;

        int pieces = 0;
        for (ItemStack item : victim.getEquipment().getArmorContents()) {
            if (item != null && !item.getType().isAir()) {
                pieces++;
            }
        }
        return 10 + pieces * 5;
    }

}
