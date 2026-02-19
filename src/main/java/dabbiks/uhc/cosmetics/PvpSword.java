package dabbiks.uhc.cosmetics;

import org.bukkit.Material;

public enum PvpSword {
    WOODEN_SWORD("Drewniany Miecz", Material.WOODEN_SWORD, CosmeticTier.COMMON, 10000, 2000),
    STONE_SWORD("Kamienny Miecz", Material.STONE_SWORD, CosmeticTier.COMMON, 10000, 2000),
    COPPER_SWORD("Miedziany Miecz", Material.COPPER_SWORD, CosmeticTier.RARE, 25000, 5000),
    IRON_SWORD("Żelazny Miecz", Material.IRON_SWORD, CosmeticTier.RARE, 25000, 5000),
    GOLDEN_SWORD("Złoty Miecz", Material.GOLDEN_SWORD, CosmeticTier.EPIC, 50000, 10000),
    DIAMOND_SWORD("Diamentowy Miecz", Material.DIAMOND_SWORD, CosmeticTier.MYTHIC, 100000, 20000),
    NETHERITE_SWORD("Netherytowy Miecz", Material.NETHERITE_SWORD, CosmeticTier.LEGENDARY, 250000, 50000),

    GOLDEN_PICKAXE("Złoty Kilof", Material.GOLDEN_PICKAXE, CosmeticTier.EPIC, 50000, 10000),
    NETHERITE_PICKAXE("Netherytowy Kilof", Material.NETHERITE_PICKAXE, CosmeticTier.LEGENDARY, 250000, 50000),
    GOLDEN_HOE("Złota Motyka", Material.GOLDEN_HOE, CosmeticTier.EPIC, 50000, 10000),
    NETHERITE_HOE("Netherytowa Motyka", Material.NETHERITE_HOE, CosmeticTier.LEGENDARY, 250000, 50000),
    GOLDEN_AXE("Złoty Topór", Material.GOLDEN_AXE, CosmeticTier.EPIC, 50000, 10000),
    NETHERITE_AXE("Netherytowy Topór", Material.NETHERITE_AXE, CosmeticTier.LEGENDARY, 250000, 50000),
    GOLDEN_SHOVEL("Złota Łopata", Material.GOLDEN_SHOVEL, CosmeticTier.EPIC, 50000, 10000),

    NETHERITE_SHOVEL("Netherytowa Łopata", Material.NETHERITE_SHOVEL, CosmeticTier.LEGENDARY, 250000, 50000),
    TRIDENT("Trójząb", Material.TRIDENT, CosmeticTier.MYTHIC, 100000, 20000),
    MACE("Buława", Material.MACE, CosmeticTier.LEGENDARY, 250000, 50000),
    FISHING_ROD("Wędka", Material.FISHING_ROD, CosmeticTier.RARE, 25000, 5000),
    STICK("Patyk", Material.STICK, CosmeticTier.COMMON, 10000, 2000),
    BONE("Kość", Material.BONE, CosmeticTier.COMMON, 10000, 2000),
    BAMBOO("Bambus", Material.BAMBOO, CosmeticTier.COMMON, 10000, 2000),

    BREEZE_ROD("Wietrzna Różdżka", Material.BREEZE_ROD, CosmeticTier.EPIC, 50000, 10000),
    AMETHYST_SHARD("Odłamek Amethystu", Material.AMETHYST_SHARD, CosmeticTier.RARE, 25000, 5000),
    ECHO_SHARD("Odłamek Echa", Material.ECHO_SHARD, CosmeticTier.MYTHIC, 100000, 20000),
    RAW_COD("Surowy Dorsz", Material.COD, CosmeticTier.COMMON, 10000, 2000),
    RAW_SALMON("Surowy Łosoś", Material.SALMON, CosmeticTier.COMMON, 10000, 2000),
    TROPICAL_FISH("Ryba Tropikalna", Material.TROPICAL_FISH, CosmeticTier.RARE, 25000, 5000),
    SPYGLASS("Luneta", Material.SPYGLASS, CosmeticTier.LEGENDARY, 1000000, 200000);

    private final String name;
    private final Material material;
    private final CosmeticTier tier;
    private final int coinsCost, powderCost;

    PvpSword(String name, Material material, CosmeticTier tier, int coins, int powder) {
        this.name = name;
        this.material = material;
        this.tier = tier;
        this.coinsCost = coins;
        this.powderCost = powder;
    }

    public String getName() { return name; }
    public Material getMaterial() { return material; }
    public CosmeticTier getTier() { return tier; }
    public int getCoinsCost() { return coinsCost; }
    public int getPowderCost() { return powderCost; }
}