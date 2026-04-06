package dabbiks.uhc.game.gameplay.items.data.enchants;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class EnchantCalculator {

    private final Random random = new Random();
    private final EnchantManager enchantManager = new EnchantManager();

    public int convertVanillaToPower(ItemMeta meta) {
        if (meta == null) {
            return 1;
        }

        Map<org.bukkit.enchantments.Enchantment, Integer> enchants;
        if (meta instanceof org.bukkit.inventory.meta.EnchantmentStorageMeta) {
            enchants = ((org.bukkit.inventory.meta.EnchantmentStorageMeta) meta).getStoredEnchants();
        } else {
            enchants = meta.getEnchants();
        }

        if (enchants.isEmpty()) {
            return 1;
        }

        int totalPower = 1;

        for (Map.Entry<org.bukkit.enchantments.Enchantment, Integer> entry : enchants.entrySet()) {
            org.bukkit.enchantments.Enchantment enchant = entry.getKey();
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

    public List<EnchantData> calculateEnchants(int power, EnchantSlot itemSlot) {
        return calculateEnchants(power, itemSlot, false);
    }

    public List<EnchantData> calculateEnchants(int power, EnchantSlot itemSlot, boolean bonusEnchant) {
        List<EnchantData> result = new ArrayList<>();
        int modifiedLevel = power + random.nextInt(Math.max(1, power / 3)) + 2;

        List<EnchantType> allCompatible = getAllCompatibleEnchants(itemSlot);

        if (allCompatible.isEmpty()) {
            return result;
        }

        List<EnchantType> candidates = getCandidatesForPower(allCompatible, modifiedLevel, power);

        if (candidates.isEmpty()) {
            candidates = new ArrayList<>(allCompatible);
        }

        EnchantType firstEnchant = pickWeightedEnchant(candidates);

        if (firstEnchant == null && !candidates.isEmpty()) {
            firstEnchant = candidates.get(random.nextInt(candidates.size()));
        }

        if (firstEnchant != null) {
            result.add(createEnchantData(firstEnchant, modifiedLevel, bonusEnchant));
            EnchantType finalFirstEnchant = firstEnchant;
            allCompatible.removeIf(e -> e == finalFirstEnchant);
            bonusEnchant = false;
        }

        int currentChance = modifiedLevel * 2;

        while (!allCompatible.isEmpty() && random.nextInt(100) < currentChance) {
            EnchantType extraEnchant = pickWeightedEnchant(allCompatible);

            if (extraEnchant != null) {
                result.add(createEnchantData(extraEnchant, modifiedLevel, false));
                EnchantType finalExtra = extraEnchant;
                allCompatible.removeIf(e -> e == finalExtra);
            }

            currentChance /= 2;
        }

        return result;
    }

    private List<EnchantType> getAllCompatibleEnchants(EnchantSlot itemSlot) {
        List<EnchantType> valid = new ArrayList<>();
        for (EnchantType type : EnchantType.values()) {
            if (enchantManager.isCompatible(itemSlot, type.getSlot())) {
                valid.add(type);
            }
        }
        return valid;
    }

    private List<EnchantType> getCandidatesForPower(List<EnchantType> source, int modifiedLevel, int originalPower) {
        List<EnchantType> filtered = new ArrayList<>();

        for (EnchantType type : source) {
            int minPower = getMinPowerForTier(type.getTier());

            if (originalPower > 20 && type.getTier() == EnchantTier.COMMON) {
                continue;
            }

            if (modifiedLevel >= minPower) {
                filtered.add(type);
            }
        }
        return filtered;
    }

    private EnchantType pickWeightedEnchant(List<EnchantType> candidates) {
        if (candidates.isEmpty()) return null;

        int totalWeight = candidates.stream().mapToInt(e -> getWeight(e.getTier())).sum();
        if (totalWeight <= 0) return candidates.get(0);

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

    private EnchantData createEnchantData(EnchantType type, int modifiedLevel, boolean bonus) {
        double percentage = (modifiedLevel + random.nextInt(10)) / 45.0;
        int calculatedLevel = (int) Math.ceil(percentage * type.getMaxLevel());
        calculatedLevel = Math.max(1, Math.min(calculatedLevel, type.getMaxLevel()));

        if (bonus) {
            calculatedLevel++;
        }

        return new EnchantData(type, calculatedLevel);
    }

    private int getWeight(EnchantTier tier) {
        switch (tier) {
            case COMMON: return 10;
            case RARE: return 5;
            case EPIC: return 2;
            case MYTHIC: return 1;
            case LEGENDARY: return 1;
            default: return 1;
        }
    }

    private int getMinPowerForTier(EnchantTier tier) {
        switch (tier) {
            case COMMON: return 1;
            case RARE: return 10;
            case EPIC: return 20;
            case MYTHIC: return 25;
            case LEGENDARY: return 30;
            default: return 1;
        }
    }
}