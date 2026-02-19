package dabbiks.uhc.cosmetics;

import org.bukkit.Material;

public enum KillSound {
    BLASTX("BlastX", "blastx.ogg", Material.MUSIC_DISC_5, CosmeticTier.COMMON, 15000, 3000),
    CHAMPIONS2021("Champions 2021", "champions2021.ogg", Material.MUSIC_DISC_5, CosmeticTier.COMMON, 15000, 3000),
    CHAMPIONS2022("Champions 2022", "champions2022.ogg", Material.MUSIC_DISC_5, CosmeticTier.COMMON, 15000, 3000),
    CHAMPIONS2023("Champions 2023", "champions2023.ogg", Material.MUSIC_DISC_5, CosmeticTier.COMMON, 15000, 3000),
    CHAMPIONS2024("Champions 2024", "champions2024.ogg", Material.MUSIC_DISC_5, CosmeticTier.COMMON, 15000, 3000),
    CHRONOVOID("Chronovoid", "chronovoid.ogg", Material.MUSIC_DISC_5, CosmeticTier.COMMON, 15000, 3000),
    CRISIS("Crisis", "crisis.ogg", Material.MUSIC_DISC_5, CosmeticTier.COMMON, 15000, 3000),
    DREAMWINGS("Dreamwings", "dreamwings.ogg", Material.MUSIC_DISC_5, CosmeticTier.COMMON, 15000, 3000),
    ENDERFLAME("Enderflame", "enderflame.ogg", Material.MUSIC_DISC_5, CosmeticTier.COMMON, 15000, 3000),
    HELIX("Helix", "helix.ogg", Material.MUSIC_DISC_5, CosmeticTier.COMMON, 15000, 3000),
    ION("Ion", "ion.ogg", Material.MUSIC_DISC_5, CosmeticTier.COMMON, 15000, 3000),
    KURONAMI("Kuronami", "kuronami.ogg", Material.MUSIC_DISC_5, CosmeticTier.COMMON, 15000, 3000),
    MYSTBLOOM("Mystbloom", "mystbloom.ogg", Material.MUSIC_DISC_5, CosmeticTier.COMMON, 15000, 3000),
    NEPTUNE("Neptune", "neptune.ogg", Material.MUSIC_DISC_5, CosmeticTier.COMMON, 15000, 3000),
    OVERDRIVE("Overdrive", "overdrive.ogg", Material.MUSIC_DISC_5, CosmeticTier.COMMON, 15000, 3000),
    PRELUDE("Prelude", "prelude.ogg", Material.MUSIC_DISC_5, CosmeticTier.COMMON, 15000, 3000),
    PROTOCOL("Protocol", "protocol.ogg", Material.MUSIC_DISC_5, CosmeticTier.COMMON, 15000, 3000),
    REAVER("Reaver", "reaver.ogg", Material.MUSIC_DISC_5, CosmeticTier.COMMON, 15000, 3000),
    SPECTRUM("Spectrum", "spectrum.ogg", Material.MUSIC_DISC_5, CosmeticTier.COMMON, 15000, 3000);

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

    public String getName() {
        return name;
    }

    public String getSound() {
        return sound;
    }

    public Material getMaterial() {
        return material;
    }

    public CosmeticTier getTier() {
        return tier;
    }

    public int getCoinsCost() {
        return coinsCost;
    }

    public int getPowderCost() {
        return powderCost;
    }
}
