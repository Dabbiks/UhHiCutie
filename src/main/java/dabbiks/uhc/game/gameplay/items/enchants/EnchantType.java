package dabbiks.uhc.game.gameplay.items.enchants;

import org.bukkit.enchantments.Enchantment;

public enum EnchantType {
    SHARPNESS("Ostrość", 5, EnchantTier.COMMON, EnchantSlot.MELEE, null),
    KNOCKBACK("Odrzut", 3, EnchantTier.COMMON, EnchantSlot.SWORD, Enchantment.KNOCKBACK),
    LOOTING("Grabież", 2, EnchantTier.RARE, EnchantSlot.SWORD, Enchantment.LOOTING),
    SUNDER("Mocne ciosy", 3, EnchantTier.RARE, EnchantSlot.MELEE, null),
    LETHALITY("Przebicie pancerza", 10, EnchantTier.RARE, EnchantSlot.MELEE, null),
    HASTE("Zręczność", 2, EnchantTier.EPIC, EnchantSlot.SWORD, null),
    SLUDGE("Lepka maź", 1, EnchantTier.EPIC, EnchantSlot.SWORD, null),
    POISON("Zatrucie", 1, EnchantTier.MYTHIC, EnchantSlot.SWORD, null),
    IGNITE("Zaklęty ogień", 3, EnchantTier.MYTHIC, EnchantSlot.SWORD, Enchantment.FIRE_ASPECT),
    SHATTER("Strzaskanie", 2, EnchantTier.LEGENDARY, EnchantSlot.MELEE, null);

    private String name;
    private int maxLevel;
    private EnchantTier tier;
    private EnchantSlot slot;
    private Enchantment enchantment;

    EnchantType(String name, int maxLevel, EnchantTier tier, EnchantSlot slot, Enchantment enchantment) {
        this.name = name;
        this.maxLevel = maxLevel;
        this.tier = tier;
        this.slot = slot;
        this.enchantment = enchantment;
    }

    public String getName() {
        return name;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public EnchantTier getTier() {
        return tier;
    }

    public EnchantSlot getSlot() {
        return slot;
    }

    public Enchantment getEnchantment() {
        return enchantment;
    }
}
