package dabbiks.uhc.game.gameplay.items;

import com.github.retrooper.packetevents.protocol.nbt.NBTInt;
import dabbiks.uhc.game.gameplay.items.attributes.AttributeData;
import dabbiks.uhc.game.gameplay.items.attributes.AttributeManager;
import dabbiks.uhc.game.gameplay.items.enchants.EnchantData;
import dabbiks.uhc.game.gameplay.items.enchants.EnchantManager;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

public class ItemBuilder {

    private final ItemInstance instance;

    public ItemBuilder(ItemInstance instance) {
        this.instance = instance;
    }

    public ItemStack build() {
        Material material = Material.matchMaterial(instance.getMaterial());
        if (material == null) material = Material.BARRIER;

        ItemStack item = new ItemStack(material, instance.getAmount() > 0 ? instance.getAmount() : 1);
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();

        if (meta == null) return item;

        if (instance.getName() != null) {
            meta.setDisplayName(instance.getName());
        }

        if (instance.getLore() != null) {
            meta.setLore(instance.getLore().stream().toList());
        }

        if (instance.getCustomModelData() != null) {
            meta.setCustomModelData(instance.getCustomModelData());
        }

        if (instance.getEnchants() != null) {
            lore = meta.getLore();
            lore.add("");
            for (EnchantData enchantData : instance.getEnchants()) {
                lore.add(EnchantManager.formatLoreLine(enchantData));
                if (enchantData.getType().getEnchantment() == null) continue;
                meta.addEnchant(enchantData.getType().getEnchantment(), enchantData.getLevel(), true);
            }

            meta.setLore(lore);
            item.setItemMeta(meta);
            NBTItem nbtItem = new NBTItem(item);

            for (EnchantData enchantData : instance.getEnchants()) {
                nbtItem.setInteger(enchantData.getType().getName(), enchantData.getLevel());
            }

            item = nbtItem.getItem();
        }

        meta = item.getItemMeta();
        if (instance.getAttributes() != null) {
            lore = meta.getLore();
            lore.add("");
            for (AttributeData attributeData : instance.getAttributes()) {
                lore.add(AttributeManager.formatLoreLine(attributeData));
                if (attributeData.getAttributeType().getAttribute() == null) continue;
                AttributeModifier.Operation operation = attributeData.isPercent() ? AttributeModifier.Operation.ADD_SCALAR : AttributeModifier.Operation.ADD_NUMBER;
                AttributeModifier modifier = new AttributeModifier(
                        UUID.randomUUID(),
                        attributeData.getAttributeType().getName(),
                        attributeData.getAttributeValue(),
                        operation,
                        attributeData.getEquipmentSlot()
                );
                meta.addAttributeModifier(attributeData.getAttributeType().getAttribute(), modifier);
            }

            meta.setLore(lore);
            item.setItemMeta(meta);
            NBTItem nbtItem = new NBTItem(item);

            for (AttributeData attributeData : instance.getAttributes()) {
                nbtItem.setDouble(attributeData.getAttributeType().getName(), attributeData.getAttributeValue());
            }

            item = nbtItem.getItem();
        }

        meta = item.getItemMeta();

        // !
        return item;
    }
}
