package dabbiks.uhc.game.gameplay.items.data.enchants;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import static dabbiks.uhc.Main.symbolU;

public class EnchantManager {

    public int getItemLevel(NBTItem nbtItem, EnchantType type) {
        return nbtItem.getInteger(type.toString());
    }

    public int getArmorLevel(LivingEntity entity, EnchantType type) {
        int totalLevel = 0;

        if (entity.getEquipment() == null) {
            return 0;
        }

        ItemStack[] armorContents = entity.getEquipment().getArmorContents();

        for (ItemStack item : armorContents) {
            if (item == null || item.getType().isAir()) {
                continue;
            }

            NBTItem nbt = new NBTItem(item);
            String key = type.toString();

            if (nbt.hasKey(key)) {
                totalLevel += nbt.getInteger(key);
            }
        }

        return totalLevel;
    }

    public void combineLevel(EnchantData data1, EnchantData data2) {
        if (!data1.getType().equals(data2.getType())) return;

        int lvl1 = data1.getLevel();
        int lvl2 = data2.getLevel();
        int maxLvl = data1.getType().getMaxLevel();

        if (lvl2 > lvl1) {
            data1.setLevel(lvl2);
        } else if (lvl1 == lvl2 && lvl1 < maxLvl) {
            data1.setLevel(lvl1 + 1);
        }
    }

    public boolean isCompatible(EnchantSlot itemSlot, EnchantSlot enchantSlot) {
        if (itemSlot == enchantSlot) return true;
        if (enchantSlot == EnchantSlot.ALL) return true;

        if (enchantSlot == EnchantSlot.MELEE) {
            return itemSlot == EnchantSlot.SWORD || itemSlot == EnchantSlot.AXE ||
                    itemSlot == EnchantSlot.MACE || itemSlot == EnchantSlot.SPEAR ||
                    itemSlot == EnchantSlot.TRIDENT;
        }

        if (enchantSlot == EnchantSlot.ARMOR) {
            return itemSlot == EnchantSlot.HELMET || itemSlot == EnchantSlot.CHESTPLATE ||
                    itemSlot == EnchantSlot.LEGGINGS || itemSlot == EnchantSlot.BOOTS;
        }

        if (enchantSlot == EnchantSlot.TOOL) {
            return itemSlot == EnchantSlot.PICKAXE || itemSlot == EnchantSlot.AXE;
        }

        if (enchantSlot == EnchantSlot.RANGED) {
            return itemSlot == EnchantSlot.BOW || itemSlot == EnchantSlot.CROSSBOW ||
                    itemSlot == EnchantSlot.TRIDENT;
        }

        return false;
    }

    private static String numberToRoman(int number) {
        return switch (number) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            case 6 -> "VI";
            case 7 -> "VII";
            case 8 -> "VIII";
            case 9 -> "IX";
            case 10 -> "X";
            default -> "?";
        };
    }

    private static String tierToIcon(EnchantTier tier) {
        return switch (tier) {
            case COMMON -> symbolU.ENCHANT_COMMON;
            case RARE -> symbolU.ENCHANT_RARE;
            case EPIC -> symbolU.ENCHANT_EPIC;
            case MYTHIC -> symbolU.ENCHANT_MYTHIC;
            case LEGENDARY -> symbolU.ENCHANT_LEGENDARY;
        };
    }

    public static String formatLoreLine(EnchantData data) {
        return tierToIcon(data.getType().getTier()) + " " + data.getType().getName() + " " + numberToRoman(data.getLevel());
    }

}
