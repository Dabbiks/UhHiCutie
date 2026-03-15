package dabbiks.uhc.cosmetics;

import org.bukkit.Material;

public enum PvpSword {
    WOODEN_SWORD("Drewniany Miecz", Material.WOODEN_SWORD, 0, CosmeticTier.COMMON, 10000, 2000),
    STONE_SWORD("Kamienny Miecz", Material.STONE_SWORD, 0, CosmeticTier.COMMON, 10000, 2000),
    COPPER_SWORD("Miedziany Miecz", Material.COPPER_SWORD, 0, CosmeticTier.RARE, 25000, 5000),
    IRON_SWORD("Żelazny Miecz", Material.IRON_SWORD, 0, CosmeticTier.RARE, 25000, 5000),
    GOLDEN_SWORD("Złoty Miecz", Material.GOLDEN_SWORD, 0, CosmeticTier.EPIC, 50000, 10000),
    DIAMOND_SWORD("Diamentowy Miecz", Material.DIAMOND_SWORD, 0, CosmeticTier.MYTHIC, 100000, 20000),
    NETHERITE_SWORD("Netherytowy Miecz", Material.NETHERITE_SWORD, 0, CosmeticTier.LEGENDARY, 250000, 50000),

    GOLDEN_PICKAXE("Złoty Kilof", Material.GOLDEN_PICKAXE, 0, CosmeticTier.EPIC, 50000, 10000),
    NETHERITE_PICKAXE("Netherytowy Kilof", Material.NETHERITE_PICKAXE, 0, CosmeticTier.LEGENDARY, 250000, 50000),
    GOLDEN_HOE("Złota Motyka", Material.GOLDEN_HOE, 0, CosmeticTier.EPIC, 50000, 10000),
    NETHERITE_HOE("Netherytowa Motyka", Material.NETHERITE_HOE, 0, CosmeticTier.LEGENDARY, 250000, 50000),
    GOLDEN_AXE("Złoty Topór", Material.GOLDEN_AXE, 0, CosmeticTier.EPIC, 50000, 10000),
    NETHERITE_AXE("Netherytowy Topór", Material.NETHERITE_AXE, 0, CosmeticTier.LEGENDARY, 250000, 50000),
    GOLDEN_SHOVEL("Złota Łopata", Material.GOLDEN_SHOVEL, 0, CosmeticTier.EPIC, 50000, 10000),

    NETHERITE_SHOVEL("Netherytowa Łopata", Material.NETHERITE_SHOVEL, 0, CosmeticTier.LEGENDARY, 250000, 50000),
    TRIDENT("Trójząb", Material.TRIDENT, 0, CosmeticTier.MYTHIC, 100000, 20000),
    MACE("Buława", Material.MACE, 0, CosmeticTier.LEGENDARY, 250000, 50000),
    FISHING_ROD("Wędka", Material.FISHING_ROD, 0, CosmeticTier.RARE, 25000, 5000),
    STICK("Patyk", Material.STICK, 0, CosmeticTier.COMMON, 10000, 2000),
    BONE("Kość", Material.BONE, 0, CosmeticTier.COMMON, 10000, 2000),
    BAMBOO("Bambus", Material.BAMBOO, 0, CosmeticTier.COMMON, 10000, 2000),

    BREEZE_ROD("Wietrzna Różdżka", Material.BREEZE_ROD, 0, CosmeticTier.EPIC, 50000, 10000),
    AMETHYST_SHARD("Odłamek Amethystu", Material.AMETHYST_SHARD, 0, CosmeticTier.RARE, 25000, 5000),
    ECHO_SHARD("Odłamek Echa", Material.ECHO_SHARD, 0, CosmeticTier.MYTHIC, 100000, 20000),
    RAW_COD("Surowy Dorsz", Material.COD, 0, CosmeticTier.COMMON, 10000, 2000),
    RAW_SALMON("Surowy Łosoś", Material.SALMON, 0, CosmeticTier.COMMON, 10000, 2000),
    TROPICAL_FISH("Ryba Tropikalna", Material.TROPICAL_FISH, 0, CosmeticTier.RARE, 25000, 5000),
    SPYGLASS("Luneta", Material.SPYGLASS, 0, CosmeticTier.LEGENDARY, 1000000, 200000),

    PRESTIGE_SWORD("Miecz z 24-karatowego złota", Material.GOLDEN_SWORD, 6700, CosmeticTier.PRESTIGE, 1000000, 200000),
    PRESTIGE_FISHING_ROD("Wędka z 24-karatowego złota", Material.FISHING_ROD, 6700, CosmeticTier.PRESTIGE, 1000000, 200000),
    PRESTIGE_MACE("Buzdygan z 24-karatowego złota", Material.MACE, 6700, CosmeticTier.PRESTIGE, 1000000, 200000);

    private final String name;
    private final Material material;
    private final int customModelData;
    private final CosmeticTier tier;
    private final int coinsCost, powderCost;

    PvpSword(String name, Material material, int customModelData, CosmeticTier tier, int coins, int powder) {
        this.name = name;
        this.material = material;
        this.customModelData = customModelData;
        this.tier = tier;
        this.coinsCost = coins;
        this.powderCost = powder;
    }

    public String getName() { return name; }
    public Material getMaterial() { return material; }
    public int getCustomModelData() { return customModelData; }
    public CosmeticTier getTier() { return tier; }
    public int getCoinsCost() { return coinsCost; }
    public int getPowderCost() { return powderCost; }
}