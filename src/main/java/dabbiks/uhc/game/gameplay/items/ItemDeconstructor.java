package dabbiks.uhc.game.gameplay.items;

import dabbiks.uhc.game.gameplay.items.data.attributes.AttributeData;
import dabbiks.uhc.game.gameplay.items.data.attributes.AttributeType;
import dabbiks.uhc.game.gameplay.items.data.enchants.EnchantData;
import dabbiks.uhc.game.gameplay.items.data.enchants.EnchantType;
import dabbiks.uhc.game.gameplay.items.data.perks.PerkType;
import de.tr7zw.nbtapi.NBTItem;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Equippable;
import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemDeconstructor {

    private final ItemStack itemStack;

    public ItemDeconstructor(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemInstance deconstruct() {
        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return null;
        }

        ItemInstance instance = new ItemInstance();

        instance.setMaterial(itemStack.getType().name());
        instance.setAmount(itemStack.getAmount());

        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            if (meta.hasDisplayName()) {
                instance.setName(meta.getDisplayName());
            }
            if (meta.hasCustomModelData()) {
                instance.setCustomModelData(meta.getCustomModelData());
            }
        }

        if (itemStack.hasData(DataComponentTypes.BLOCKS_ATTACKS)) {
            instance.setCanParry(true);
        }

        if (itemStack.hasData(DataComponentTypes.EQUIPPABLE)) {
            Equippable equippable = itemStack.getData(DataComponentTypes.EQUIPPABLE);
            if (equippable != null) {
                if (equippable.assetId() != null) {
                    instance.setArmorTexture(equippable.assetId().value());
                }

                EquipmentSlot slot = equippable.slot();
                String slotName = switch (slot) {
                    case HEAD -> "head";
                    case CHEST -> "chest";
                    case LEGS -> "legs";
                    case FEET -> "feet";
                    default -> "head";
                };
                instance.setArmorSlot(slotName);
            }
        }

        NBTItem nbtItem = new NBTItem(itemStack);

        List<EnchantData> enchants = new ArrayList<>();
        for (EnchantType type : EnchantType.values()) {
            if (nbtItem.hasKey(type.getName())) {
                enchants.add(new EnchantData(type, nbtItem.getInteger(type.getName())));
            }
        }
        if (!enchants.isEmpty()) {
            instance.setEnchants(enchants);
        }

        List<AttributeData> attributes = new ArrayList<>();
        for (AttributeType type : AttributeType.values()) {
            if (nbtItem.hasKey(type.getName())) {
                double value = nbtItem.getDouble(type.getName());

                EquipmentSlot slot = EquipmentSlot.HAND;
                if (nbtItem.hasKey(type.getName() + "_SLOT")) {
                    try {
                        slot = EquipmentSlot.valueOf(nbtItem.getString(type.getName() + "_SLOT"));
                    } catch (IllegalArgumentException ignored) {
                    }
                }

                boolean percent = false;
                if (nbtItem.hasKey(type.getName() + "_PERCENT")) {
                    percent = nbtItem.getBoolean(type.getName() + "_PERCENT");
                }

                attributes.add(new AttributeData(slot, type, value, percent));
            }
        }
        if (!attributes.isEmpty()) {
            instance.setAttributes(attributes);
        }

        List<PerkType> perks = new ArrayList<>();
        for (PerkType type : PerkType.values()) {
            if (nbtItem.hasKey(type.name())) {
                perks.add(type);
            }
        }
        if (!perks.isEmpty()) {
            instance.setPerks(perks);
        }

        if (nbtItem.hasKey("CAN_BE_FORGED")) {
            instance.setCanBeForged(true);
        }

        return instance;
    }
}