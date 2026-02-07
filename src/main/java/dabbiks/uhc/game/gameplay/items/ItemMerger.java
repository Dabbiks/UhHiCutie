package dabbiks.uhc.game.gameplay.items;

import dabbiks.uhc.game.gameplay.items.attributes.AttributeData;
import dabbiks.uhc.game.gameplay.items.attributes.AttributeManager;
import dabbiks.uhc.game.gameplay.items.enchants.EnchantData;
import dabbiks.uhc.game.gameplay.items.enchants.EnchantManager;
import dabbiks.uhc.game.gameplay.items.enchants.EnchantType;
import dabbiks.uhc.game.gameplay.items.perks.PerkType;
import org.bukkit.Material;

import java.util.*;

public class ItemMerger {

    private final ItemInstance firstItem;
    private final ItemInstance secondItem;

    public ItemMerger(ItemInstance firstItem, ItemInstance secondItem) {
        this.firstItem = firstItem;
        this.secondItem = secondItem;
    }

    public ItemInstance merge() {
        if (firstItem == null || secondItem == null) return null;
        if (!firstItem.getMaterial().equals(secondItem.getMaterial()) && !secondItem.getMaterial().equals(Material.ENCHANTED_BOOK)) return null;

        ItemInstance instance = new ItemInstance();

        if (firstItem.getName() != null) {
            instance.setName(firstItem.getName());
        }

        if (firstItem.getCustomModelData() != null) {
            instance.setCustomModelData(firstItem.getCustomModelData());
        } else if (secondItem.getCustomModelData() != null) {
            instance.setCustomModelData(secondItem.getCustomModelData());
        }

        if (!mergeEnchants().isEmpty()) instance.setEnchants(mergeEnchants());
        if (!mergeAttributes().isEmpty()) instance.setAttributes(mergeAttributes());
        if (!mergePerks().isEmpty()) instance.setPerks(mergePerks());

        if (firstItem.getArmorTexture() != null && firstItem.getArmorSlot() != null) {
            instance.setArmorTexture(firstItem.getArmorTexture());
            instance.setArmorSlot(firstItem.getArmorSlot());
        } else if (secondItem.getArmorTexture() != null && secondItem.getArmorSlot() != null) {
            instance.setArmorTexture(secondItem.getArmorTexture());
            instance.setArmorSlot(secondItem.getArmorSlot());
        }

        if (firstItem.canParry()) {
            instance.setCanParry(true);
        }

        if (firstItem.canBeForged()) {
            instance.setCanParry(true);
        }

        return instance;
    }

    private List<EnchantData> mergeEnchants() {
        Map<EnchantType, EnchantData> mergedMap = new LinkedHashMap<>();

        if (firstItem.getEnchants() != null) {
            for (EnchantData data : firstItem.getEnchants()) {
                mergedMap.put(data.getType(), new EnchantData(data.getType(), data.getLevel()));
            }
        }

        if (secondItem.getEnchants() != null) {
            for (EnchantData secondData : secondItem.getEnchants()) {
                EnchantType type = secondData.getType();
                if (mergedMap.containsKey(type)) {
                    EnchantManager.combineLevel(mergedMap.get(type), secondData);
                } else {
                    mergedMap.put(type, new EnchantData(type, secondData.getLevel()));
                }
            }
        }

        return new ArrayList<>(mergedMap.values());
    }

    private List<AttributeData> mergeAttributes() {
        Map<String, AttributeData> mergedMap = new LinkedHashMap<>();

        if (firstItem.getAttributes() != null) {
            for (AttributeData data : firstItem.getAttributes()) {
                String key = data.getAttributeType().name() + ":" + data.getEquipmentSlot().name();
                mergedMap.put(key, new AttributeData(
                        data.getEquipmentSlot(),
                        data.getAttributeType(),
                        data.getAttributeValue(),
                        data.isPercent()
                ));
            }
        }

        if (secondItem.getAttributes() != null) {
            for (AttributeData secondData : secondItem.getAttributes()) {
                String key = secondData.getAttributeType().name() + ":" + secondData.getEquipmentSlot().name();

                if (mergedMap.containsKey(key)) {
                    AttributeManager.combineValue(mergedMap.get(key), secondData);
                } else {
                    mergedMap.put(key, new AttributeData(
                            secondData.getEquipmentSlot(),
                            secondData.getAttributeType(),
                            secondData.getAttributeValue(),
                            secondData.isPercent()
                    ));
                }
            }
        }

        return new ArrayList<>(mergedMap.values());
    }

    private List<PerkType> mergePerks() {
        Set<PerkType> mergedSet = new LinkedHashSet<>();

        if (firstItem.getPerks() != null) {
            mergedSet.addAll(firstItem.getPerks());
        }

        if (secondItem.getPerks() != null) {
            mergedSet.addAll(secondItem.getPerks());
        }

        return new ArrayList<>(mergedSet);
    }
}
