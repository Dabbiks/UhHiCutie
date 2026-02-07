package dabbiks.uhc.game.gameplay.items.enchants;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class EnchantCalculator {

    private static final Random random = new Random();

    public static int convertVanillaToPower(ItemMeta meta) {
        if (meta == null || !meta.hasEnchants()) {
            return 1;
        }

        int totalPower = 0;

        for (Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
            Enchantment enchant = entry.getKey();
            int level = entry.getValue();

            if (enchant.isCursed()) {
                continue;
            }

            int maxLevel = enchant.getMaxLevel();
            int enchantPower;

            if (maxLevel == 1) {
                enchantPower = 5;
            } else {
                double percentage = (double) level / maxLevel;
                enchantPower = (int) (percentage * 15.0);
            }

            totalPower += enchantPower;
        }

        return Math.min(30, Math.max(1, totalPower));
    }

    public static List<EnchantData> calculateEnchants(int power, EnchantSlot itemSlot) {
        List<EnchantData> result = new ArrayList<>();
        int modifiedLevel = power + random.nextInt(power / 4 + 1) + random.nextInt(4);
        int tierOffset = random.nextInt(5) - 2;

        List<EnchantType> candidates = getPossibleEnchants(itemSlot, modifiedLevel, tierOffset);

        if (candidates.isEmpty()) {
            return result;
        }

        EnchantType firstEnchant = pickWeightedEnchant(candidates);
        if (firstEnchant != null) {
            result.add(createEnchantData(firstEnchant, modifiedLevel));
            candidates.remove(firstEnchant);
        }

        int currentLevel = modifiedLevel / 2;

        while (!candidates.isEmpty() && random.nextInt(50) <= currentLevel) {
            EnchantType extraEnchant = pickWeightedEnchant(candidates);
            if (extraEnchant != null) {
                result.add(createEnchantData(extraEnchant, modifiedLevel));
                candidates.remove(extraEnchant);
            }
            currentLevel /= 2;
        }

        return result;
    }

    private static List<EnchantType> getPossibleEnchants(EnchantSlot itemSlot, int modifiedLevel, int tierOffset) {
        List<EnchantType> valid = new ArrayList<>();

        for (EnchantType type : EnchantType.values()) {
            if (!isSlotCompatible(itemSlot, type.getSlot())) {
                continue;
            }

            int minPower = getMinPowerForTier(type.getTier()) + tierOffset;
            if (modifiedLevel < minPower) {
                continue;
            }

            valid.add(type);
        }
        return valid;
    }

    private static boolean isSlotCompatible(EnchantSlot itemSlot, EnchantSlot enchantSlot) {
        if (itemSlot == enchantSlot) return true;

        // Obsługa ALL - pasuje do wszystkiego i przyjmuje wszystko
        if (itemSlot == EnchantSlot.ALL || enchantSlot == EnchantSlot.ALL) return true;

        if (enchantSlot == EnchantSlot.MELEE) {
            return itemSlot == EnchantSlot.SWORD || itemSlot == EnchantSlot.AXE ||
                    itemSlot == EnchantSlot.MACE || itemSlot == EnchantSlot.SPEAR;
        }

        if (enchantSlot == EnchantSlot.RANGED) {
            return itemSlot == EnchantSlot.BOW || itemSlot == EnchantSlot.CROSSBOW ||
                    itemSlot == EnchantSlot.TRIDENT;
        }

        if (enchantSlot == EnchantSlot.ARMOR) {
            return itemSlot == EnchantSlot.HELMET || itemSlot == EnchantSlot.CHESTPLATE ||
                    itemSlot == EnchantSlot.LEGGINGS || itemSlot == EnchantSlot.BOOTS;
        }

        return false;
    }

    private static EnchantType pickWeightedEnchant(List<EnchantType> candidates) {
        int totalWeight = candidates.stream().mapToInt(e -> getWeight(e.getTier())).sum();
        if (totalWeight <= 0) return null;

        int randomValue = random.nextInt(totalWeight);
        int currentSum = 0;

        for (EnchantType type : candidates) {
            currentSum += getWeight(type.getTier());
            if (randomValue < currentSum) {
                return type;
            }
        }
        return candidates.get(0);
    }

    private static EnchantData createEnchantData(EnchantType type, int modifiedLevel) {
        double percentage = Math.min(1.0, modifiedLevel / 40.0);
        int calculatedLevel = (int) Math.ceil(percentage * type.getMaxLevel());
        calculatedLevel = Math.max(1, Math.min(calculatedLevel, type.getMaxLevel()));

        return new EnchantData(type, calculatedLevel);
    }

    private static int getWeight(EnchantTier tier) {
        switch (tier) {
            case COMMON: return 6;
            case RARE, EPIC: return 3;
            case MYTHIC: return 1;
            case LEGENDARY: return 1;
            default: return 1;
        }
    }

    private static int getMinPowerForTier(EnchantTier tier) {
        switch (tier) {
            case COMMON: return 1;
            case RARE: return 8;
            case EPIC: return 16;
            case MYTHIC: return 22;
            case LEGENDARY: return 28;
            default: return 1;
        }
    }
}