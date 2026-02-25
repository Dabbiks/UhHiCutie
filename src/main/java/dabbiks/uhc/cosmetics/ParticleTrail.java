package dabbiks.uhc.cosmetics;

import org.bukkit.Material;
import org.bukkit.Particle;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum ParticleTrail {
    DEFAULT("Podstawowa smuga", Material.SNOWBALL, CosmeticTier.COMMON, 0, 0, Collections.emptyList()),
    SNOWY("Śnieżny Szlak", Material.SNOW_BLOCK, CosmeticTier.COMMON, 10000, 2000, List.of(Particle.SNOWFLAKE)),
    CLOUDY("Chmura", Material.WHITE_WOOL, CosmeticTier.COMMON, 10000, 2000, List.of(Particle.CLOUD)),
    SMOKY("Dymek", Material.CAMPFIRE, CosmeticTier.COMMON, 10000, 2000, List.of(Particle.SMOKE)),
    CRITICAL("Cios Krytyczny", Material.IRON_SWORD, CosmeticTier.COMMON, 10000, 2000, List.of(Particle.CRIT)),
    SLIMY("Glucik", Material.SLIME_BALL, CosmeticTier.COMMON, 10000, 2000, List.of(Particle.ITEM_SLIME)),
    RAINY("Krople Deszczu", Material.WATER_BUCKET, CosmeticTier.COMMON, 10000, 2000, List.of(Particle.DRIPPING_WATER)),
    HAPPY("Radosny Wieśniak", Material.EMERALD, CosmeticTier.COMMON, 10000, 2000, List.of(Particle.HAPPY_VILLAGER)),
    ANGRY("Zły Wieśniak", Material.REDSTONE, CosmeticTier.COMMON, 10000, 2000, List.of(Particle.ANGRY_VILLAGER)),
    MUSICAL("Nuty", Material.NOTE_BLOCK, CosmeticTier.COMMON, 10000, 2000, List.of(Particle.NOTE)),

    FLAME("Ognisty Szlak", Material.BLAZE_POWDER, CosmeticTier.RARE, 15000, 3000, Arrays.asList(Particle.FLAME, Particle.LAVA)),
    LAVA_DRIP("Kapiąca Lawa", Material.LAVA_BUCKET, CosmeticTier.RARE, 15000, 3000, List.of(Particle.DRIPPING_LAVA)),
    PORTAL("Eteryczny Portal", Material.ENDER_PEARL, CosmeticTier.RARE, 15000, 3000, List.of(Particle.PORTAL)),
    ENCHANTED("Zaklęcie", Material.BOOK, CosmeticTier.RARE, 15000, 3000, List.of(Particle.ENCHANT)),
    INKY("Atrament", Material.INK_SAC, CosmeticTier.RARE, 15000, 3000, List.of(Particle.SQUID_INK)),
    GLOWING("Blask", Material.GLOW_INK_SAC, CosmeticTier.RARE, 15000, 3000, List.of(Particle.GLOW_SQUID_INK)),
    BUBBLY("Bąbelki", Material.GLASS_BOTTLE, CosmeticTier.RARE, 15000, 3000, List.of(Particle.BUBBLE_POP)),
    FIREWORK_SPARK("Iskra", Material.FIREWORK_ROCKET, CosmeticTier.RARE, 15000, 3000, List.of(Particle.ELECTRIC_SPARK)),

    MAGIC("Magiczna Aura", Material.ENCHANTED_BOOK, CosmeticTier.EPIC, 20000, 4000, Arrays.asList(Particle.WITCH, Particle.ENCHANT)),
    DRAGONIC("Oddech Smoka", Material.DRAGON_BREATH, CosmeticTier.EPIC, 20000, 4000, List.of(Particle.DRAGON_BREATH)),
    TOTEM_SHARDS("Okruchy Totemu", Material.TOTEM_OF_UNDYING, CosmeticTier.EPIC, 20000, 4000, List.of(Particle.TOTEM_OF_UNDYING)),
    CHERRY_LEAVES("Płatki Wiśni", Material.CHERRY_SAPLING, CosmeticTier.EPIC, 20000, 4000, List.of(Particle.CHERRY_LEAVES)),
    BREEZY("Podmuch", Material.BREEZE_ROD, CosmeticTier.EPIC, 20000, 4000, List.of(Particle.GUST)),
    HONEY_DRIP("Miodowa Osłona", Material.HONEY_BOTTLE, CosmeticTier.EPIC, 20000, 4000, List.of(Particle.DRIPPING_HONEY)),
    END_ROD_SPARK("Gwiazda Endu", Material.END_ROD, CosmeticTier.EPIC, 20000, 4000, List.of(Particle.END_ROD)),
    WAX_ON("Woskowanie", Material.HONEYCOMB, CosmeticTier.EPIC, 20000, 4000, List.of(Particle.WAX_ON)),

    HEART("Miłosny Szlak", Material.HEART_OF_THE_SEA, CosmeticTier.MYTHIC, 25000, 5000, List.of(Particle.HEART)),
    SOUL_FIRE("Ogień Dusz", Material.SOUL_SAND, CosmeticTier.MYTHIC, 25000, 5000, Arrays.asList(Particle.SOUL, Particle.SOUL_FIRE_FLAME)),
    SCULK_CHARGE("Dotyk Sculku", Material.SCULK, CosmeticTier.MYTHIC, 25000, 5000, List.of(Particle.SCULK_CHARGE)),
    SONIC_BOOM("Fala Dźwiękowa", Material.ECHO_SHARD, CosmeticTier.MYTHIC, 25000, 5000, List.of(Particle.SONIC_BOOM)),
    WITCH_MAGIC("Klątwa Wiedźmy", Material.BREWING_STAND, CosmeticTier.MYTHIC, 25000, 5000, Arrays.asList(Particle.WITCH, Particle.INSTANT_EFFECT)),
    DAMAGE_RED("Krwawe Ślady", Material.NETHER_WART, CosmeticTier.MYTHIC, 25000, 5000, List.of(Particle.DAMAGE_INDICATOR)),

    DIVINE_LIGHT("Boskie Światło", Material.NETHER_STAR, CosmeticTier.LEGENDARY, 30000, 6000, Arrays.asList(Particle.FLASH, Particle.GLOW)),
    VOID_WALKER("Krocząc w Pustce", Material.DRAGON_EGG, CosmeticTier.LEGENDARY, 30000, 6000, Arrays.asList(Particle.REVERSE_PORTAL, Particle.DRAGON_BREATH)),
    COSMIC_DUST("Kosmiczny Pył", Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE, CosmeticTier.LEGENDARY, 30000, 6000, Arrays.asList(Particle.WHITE_ASH, Particle.ENCHANT)),
    TRIAL_HERO("Bohater Prób", Material.TRIAL_KEY, CosmeticTier.LEGENDARY, 30000, 6000, Arrays.asList(Particle.TRIAL_SPAWNER_DETECTION, Particle.SCRAPE)),
    ANCIENT_AURA("Pradawna Aura", Material.ANCIENT_DEBRIS, CosmeticTier.LEGENDARY, 30000, 6000, Arrays.asList(Particle.DRIPPING_OBSIDIAN_TEAR, Particle.LANDING_OBSIDIAN_TEAR));

    private final String displayName;
    private final Material material;
    private final CosmeticTier tier;
    private final int coinsCost;
    private final int powderCost;
    private final List<Particle> particles;

    ParticleTrail(String displayName, Material material, CosmeticTier tier, int coinsCost, int powderCost, List<Particle> particles) {
        this.displayName = displayName;
        this.material = material;
        this.tier = tier;
        this.coinsCost = coinsCost;
        this.powderCost = powderCost;
        this.particles = particles;
    }

    public String getDisplayName() { return displayName; }
    public Material getMaterial() { return material; }
    public CosmeticTier getTier() { return tier; }
    public int getCoinsCost() { return coinsCost; }
    public int getPowderCost() { return powderCost; }
    public List<Particle> getParticles() { return particles; }
}