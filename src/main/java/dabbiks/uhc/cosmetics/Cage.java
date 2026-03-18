package dabbiks.uhc.cosmetics;

import org.bukkit.Material;

public enum Cage {
    DEFAULT("Szklana Klatka", Material.GLASS, CosmeticTier.COMMON, 0, 0),

    OAK("Dębowa Klatka", Material.OAK_PLANKS, CosmeticTier.COMMON, 10000, 2000),
    STONE("Kamienna Klatka", Material.STONE, CosmeticTier.COMMON, 10000, 2000),
    DIRT("Ziemista Klatka", Material.DIRT, CosmeticTier.COMMON, 10000, 2000),
    COBBLESTONE("Brukowa Klatka", Material.COBBLESTONE, CosmeticTier.COMMON, 10000, 2000),
    SAND("Piaskowa Klatka", Material.SANDSTONE, CosmeticTier.COMMON, 10000, 2000),
    GRAVEL("Żwirowa Klatka", Material.TUFF, CosmeticTier.COMMON, 10000, 2000),

    IRON("Żelazna Klatka", Material.IRON_BLOCK, CosmeticTier.RARE, 25000, 5000),
    GOLD("Złota Klatka", Material.GOLD_BLOCK, CosmeticTier.RARE, 25000, 5000),
    COPPER("Miedziana Klatka", Material.COPPER_BLOCK, CosmeticTier.RARE, 25000, 5000),
    BRICK("Ceglana Klatka", Material.BRICKS, CosmeticTier.RARE, 25000, 5000),
    BOOKSHELF("Biblioteczna Klatka", Material.BOOKSHELF, CosmeticTier.RARE, 25000, 5000),
    SPONGE("Gąbkowa Klatka", Material.SPONGE, CosmeticTier.RARE, 25000, 5000),

    DIAMOND("Diamentowa Klatka", Material.DIAMOND_BLOCK, CosmeticTier.EPIC, 50000, 10000),
    EMERALD("Szmaragdowa Klatka", Material.EMERALD_BLOCK, CosmeticTier.EPIC, 50000, 10000),
    OBSIDIAN("Obsydianowa Klatka", Material.OBSIDIAN, CosmeticTier.EPIC, 50000, 10000),
    SLIME("Szlamowa Klatka", Material.SLIME_BLOCK, CosmeticTier.EPIC, 50000, 10000),
    HONEY("Miodowa Klatka", Material.HONEY_BLOCK, CosmeticTier.EPIC, 50000, 10000),

    NETHERITE("Netherytowa Klatka", Material.NETHERITE_BLOCK, CosmeticTier.MYTHIC, 100000, 20000),
    AMETHYST("Ametystowa Klatka", Material.AMETHYST_BLOCK, CosmeticTier.MYTHIC, 100000, 20000),
    CRYING_OBSIDIAN("Płacząca Klatka", Material.CRYING_OBSIDIAN, CosmeticTier.MYTHIC, 100000, 20000),
    PRISMARINE("Pryzmarynowa Klatka", Material.PRISMARINE, CosmeticTier.MYTHIC, 100000, 20000),
    REDSTONE("Redstonowa Klatka", Material.REDSTONE_BLOCK, CosmeticTier.MYTHIC, 100000, 20000),

    BEACON("Klatka Magicznej Latarni", Material.BEACON, CosmeticTier.LEGENDARY, 250000, 50000),
    END_PORTAL("Klatka Portalu Kresu", Material.END_PORTAL_FRAME, CosmeticTier.LEGENDARY, 250000, 50000),
    DRAGON_EGG("Klatka Smoczego Jaja", Material.OBSIDIAN, CosmeticTier.LEGENDARY, 250000, 50000),
    LODESTONE("Magnetyczna Klatka", Material.LODESTONE, CosmeticTier.LEGENDARY, 250000, 50000),
    BARRIER("Niewidzialna Klatka", Material.BARRIER, CosmeticTier.LEGENDARY, 250000, 50000);

    private final String name;
    private final Material material;
    private final CosmeticTier tier;
    private final int coinsCost, powderCost;

    Cage(String name, Material material, CosmeticTier tier, int coins, int powder) {
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