package dabbiks.uhc.game.gameplay.items.data.enchants;

import org.bukkit.enchantments.Enchantment;

public enum EnchantType {
    SHARPNESS("Ostrość", 5, EnchantTier.COMMON, EnchantSlot.SWORD, null),
    KNOCKBACK("Odrzut", 3, EnchantTier.COMMON, EnchantSlot.SWORD, Enchantment.KNOCKBACK),
    LOOTING("Grabież", 2, EnchantTier.RARE, EnchantSlot.SWORD, Enchantment.LOOTING),
    SUNDER("Mocne ciosy", 3, EnchantTier.RARE, EnchantSlot.SWORD, null),
    LETHALITY("Przebicie pancerza", 10, EnchantTier.RARE, EnchantSlot.SWORD, null),
    HASTE("Zręczność", 2, EnchantTier.EPIC, EnchantSlot.SWORD, null),
    SLUDGE("Lepka maź", 1, EnchantTier.EPIC, EnchantSlot.SWORD, null),
    POISON("Zatrucie", 1, EnchantTier.MYTHIC, EnchantSlot.SWORD, null),
    IGNITE("Zaklęty ogień", 3, EnchantTier.MYTHIC, EnchantSlot.SWORD, Enchantment.FIRE_ASPECT),
    SHATTER("Strzaskanie", 2, EnchantTier.LEGENDARY, EnchantSlot.SWORD, null),

    EFFICIENCY("Wydajność", 5, EnchantTier.COMMON, EnchantSlot.TOOL, Enchantment.EFFICIENCY),
    UNBREAKING("Niezniszczalność", 3, EnchantTier.COMMON, EnchantSlot.TOOL, Enchantment.UNBREAKING),
    FORTUNE("Fortuna", 3, EnchantTier.EPIC, EnchantSlot.PICKAXE, null),
    SMELTING("Przepalanie", 1, EnchantTier.EPIC, EnchantSlot.PICKAXE, null),

    PROTECTION("Ochrona", 3, EnchantTier.COMMON, EnchantSlot.ARMOR, null),
    STONE_SKIN("Kamienna skóra", 3, EnchantTier.RARE, EnchantSlot.ARMOR, null),
    SWIFTNESS("Swoboda", 1, EnchantTier.EPIC, EnchantSlot.BOOTS, null),
    INSULATION("Izolacja", 3, EnchantTier.EPIC, EnchantSlot.LEGGINGS, null),
    THORNS("Ciernie", 3, EnchantTier.EPIC, EnchantSlot.CHESTPLATE, null),
    INVULNERABILITY("Nietykalność", 3, EnchantTier.LEGENDARY, EnchantSlot.HELMET, null),

    POWER("Moc", 3, EnchantTier.COMMON, EnchantSlot.BOW, null),
    GLOWING("Odblask", 1, EnchantTier.RARE, EnchantSlot.BOW, null),
    FLAME("Płomień", 1, EnchantTier.EPIC, EnchantSlot.BOW, Enchantment.FLAME),
    MAGIC_ARROW("Magiczna strzała", 3, EnchantTier.MYTHIC, EnchantSlot.BOW, null),
    INFINITY("Nieskończoność", 1, EnchantTier.LEGENDARY, EnchantSlot.BOW, Enchantment.INFINITY),

    QUICK_CHARGE("Szybkie ładowanie", 3, EnchantTier.COMMON, EnchantSlot.CROSSBOW, Enchantment.QUICK_CHARGE),
    PYROTECHNICS("Pirotechnika", 2, EnchantTier.EPIC, EnchantSlot.CROSSBOW, null),

    LOYALTY("Lojalność", 5, EnchantTier.COMMON, EnchantSlot.TRIDENT, Enchantment.LOYALTY),
    GROUNDING("Uziemienie", 1, EnchantTier.EPIC, EnchantSlot.TRIDENT, null),
    CHANNELING("Piorunotwórca", 1, EnchantTier.MYTHIC, EnchantSlot.TRIDENT, null),

    IRON_FEET("Stalowe stopy", 3, EnchantTier.COMMON, EnchantSlot.MACE, null),
    LEAPING("Wyskok", 3, EnchantTier.RARE, EnchantSlot.MACE, null),
    UNSTABLE_CORE("Niestabilny rdzeń", 1, EnchantTier.MYTHIC, EnchantSlot.MACE, null),

    LUNGE("Szarża", 3, EnchantTier.COMMON, EnchantSlot.SPEAR, Enchantment.LUNGE),

    LUCK_OF_THE_SEA("Morska fortuna", 5, EnchantTier.COMMON, EnchantSlot.FISHING_ROD, Enchantment.LUCK_OF_THE_SEA),
    LURE("Przynęta", 5, EnchantTier.RARE, EnchantSlot.FISHING_ROD, Enchantment.LURE);

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
