package dabbiks.uhc.cosmetics;

import org.bukkit.Material;

public enum KillSound {
    REAVER("Grabieżca", "reaver", Material.MUSIC_DISC_PIGSTEP, CosmeticTier.LEGENDARY, 35000, 7000),
    KURONAMI("Kuronami", "kuronami", Material.MUSIC_DISC_WAIT, CosmeticTier.LEGENDARY, 35000, 7000),
    PRELUDE("Preludium Chaosu", "prelude", Material.MUSIC_DISC_CAT, CosmeticTier.LEGENDARY, 35000, 7000),
    SPECTRUM("Spektrum", "spectrum", Material.MUSIC_DISC_RELIC, CosmeticTier.LEGENDARY, 35000, 7000),
    CHAMPIONS2021("Czempion 2021", "champions2021", Material.MUSIC_DISC_OTHERSIDE, CosmeticTier.LEGENDARY, 35000, 7000),

    ENDERFLAME("Smoczy Płomień", "enderflame", Material.MUSIC_DISC_CHIRP, CosmeticTier.MYTHIC, 27500, 5500),
    MYSTBLOOM("Mistyczny Rozkwit", "mystbloom", Material.MUSIC_DISC_WARD, CosmeticTier.MYTHIC, 27500, 5500),
    PROTOCOL("Protokół 781-A", "protocol", Material.MUSIC_DISC_PRECIPICE, CosmeticTier.MYTHIC, 27500, 5500),
    CHAMPIONS2024("Czempion 2024", "champions2024", Material.MUSIC_DISC_CREATOR, CosmeticTier.MYTHIC, 27500, 5500),

    ION("Jon", "ion", Material.MUSIC_DISC_FAR, CosmeticTier.EPIC, 22500, 4500),
    CHRONOVOID("Chronopustka", "chronovoid", Material.MUSIC_DISC_BLOCKS, CosmeticTier.EPIC, 22500, 4500),
    DREAMWINGS("Śniące Skrzydła", "dreamwings", Material.MUSIC_DISC_MELLOHI, CosmeticTier.EPIC, 22500, 4500),
    CHAMPIONS2023("Czempion 2023", "champions2023", Material.MUSIC_DISC_13, CosmeticTier.EPIC, 22500, 4500),

    NEPTUNE("Neptun", "neptune", Material.MUSIC_DISC_STAL, CosmeticTier.RARE, 17500, 3500),
    BLASTX("Wybuchowa Zabawka", "blastx", Material.MUSIC_DISC_11, CosmeticTier.RARE, 17500, 3500),
    OVERDRIVE("Przeciążenie", "overdrive", Material.MUSIC_DISC_STRAD, CosmeticTier.RARE, 17500, 3500),

    CRISIS("Komiksowy Cios", "crisis", Material.MUSIC_DISC_MALL, CosmeticTier.COMMON, 12500, 2500),
    HELIX("Heliks", "helix", Material.MUSIC_DISC_BLOCKS, CosmeticTier.COMMON, 12500, 2500);

    private final String name;
    private final String sound;
    private final Material material;
    private final CosmeticTier tier;
    private final int coinsCost;
    private final int powderCost;

    KillSound(String name, String sound, Material material, CosmeticTier tier, int coinsCost, int powderCost) {
        this.name = name;
        this.sound = sound;
        this.material = material;
        this.tier = tier;
        this.coinsCost = coinsCost;
        this.powderCost = powderCost;
    }

    public String getName() { return name; }
    public String getSound() { return sound; }
    public Material getMaterial() { return material; }
    public CosmeticTier getTier() { return tier; }
    public int getCoinsCost() { return coinsCost; }
    public int getPowderCost() { return powderCost; }
}