package dabbiks.uhc.cosmetics;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

public enum Mount {
    PIG("Świnia", EntityType.PIG, Material.PIG_SPAWN_EGG, CosmeticTier.COMMON, 100000, 60000),
    DONKEY("Osioł", EntityType.DONKEY, Material.DONKEY_SPAWN_EGG, CosmeticTier.RARE, 250000, 150000),
    MULE("Muł", EntityType.MULE, Material.MULE_SPAWN_EGG, CosmeticTier.RARE, 250000, 150000),
    BROWN_HORSE("Brązowy Koń", EntityType.HORSE, Material.HORSE_SPAWN_EGG, CosmeticTier.EPIC, 400000, 240000),
    SNIFFER("Sniffer", EntityType.SNIFFER, Material.SNIFFER_SPAWN_EGG, CosmeticTier.EPIC, 400000, 240000),
    STRIDER("Strider", EntityType.STRIDER, Material.STRIDER_SPAWN_EGG, CosmeticTier.MYTHIC, 600000, 375000),
    CAMEL("Wielbłąd", EntityType.CAMEL, Material.CAMEL_SPAWN_EGG, CosmeticTier.MYTHIC, 600000, 375000),
    SKELETON_HORSE("Szkieletowy Koń", EntityType.SKELETON_HORSE, Material.SKELETON_HORSE_SPAWN_EGG, CosmeticTier.MYTHIC, 600000, 375000),
    NAUTILUS("Nautilus", EntityType.DOLPHIN, Material.NAUTILUS_SHELL, CosmeticTier.LEGENDARY, 1000000, 600000),
    BLACK_HORSE("Czarny Koń", EntityType.HORSE, Material.BLACK_DYE, CosmeticTier.LEGENDARY, 1000000, 600000),
    WHITE_HORSE("Biały Koń", EntityType.HORSE, Material.WHITE_DYE, CosmeticTier.LEGENDARY, 1000000, 600000),
    ENDER_DRAGON("Smok Kresu", EntityType.ENDER_DRAGON, Material.ENDER_DRAGON_SPAWN_EGG, CosmeticTier.LEGENDARY, 10000000, 6000000);

    private final String name;
    private final EntityType entityType;
    private final Material material;
    private final CosmeticTier tier;
    private final int coinsCost, powderCost;

    Mount(String name, EntityType entityType, Material material, CosmeticTier tier, int coinsCost, int powderCost) {
        this.name = name;
        this.entityType = entityType;
        this.material = material;
        this.tier = tier;
        this.coinsCost = coinsCost;
        this.powderCost = powderCost;
    }

    public String getName() { return name; }
    public EntityType getEntityType() { return entityType; }
    public Material getMaterial() { return material; }
    public CosmeticTier getTier() { return tier; }
    public int getCoinsCost() { return coinsCost; }
    public int getPowderCost() { return powderCost; }
}