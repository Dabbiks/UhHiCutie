package dabbiks.uhc.player.events.drop;

import org.bukkit.block.Biome;
import java.util.Set;

public enum BiomeGroup {
    PLAINS_AND_FORESTS(Biome.PLAINS, Biome.SUNFLOWER_PLAINS, Biome.FOREST, Biome.FLOWER_FOREST, Biome.BIRCH_FOREST, Biome.OLD_GROWTH_BIRCH_FOREST, Biome.DARK_FOREST, Biome.CHERRY_GROVE),
    TAIGA_AND_COLD(Biome.TAIGA, Biome.SNOWY_TAIGA, Biome.OLD_GROWTH_PINE_TAIGA, Biome.OLD_GROWTH_SPRUCE_TAIGA, Biome.SNOWY_PLAINS, Biome.ICE_SPIKES),
    MOUNTAINS(Biome.MEADOW, Biome.GROVE, Biome.SNOWY_SLOPES, Biome.JAGGED_PEAKS, Biome.FROZEN_PEAKS, Biome.STONY_PEAKS),
    JUNGLES_AND_SAVANNAS(Biome.JUNGLE, Biome.SPARSE_JUNGLE, Biome.BAMBOO_JUNGLE, Biome.SAVANNA, Biome.SAVANNA_PLATEAU, Biome.WINDSWEPT_SAVANNA),
    DRY_AND_SWAMPS(Biome.DESERT, Biome.BADLANDS, Biome.ERODED_BADLANDS, Biome.WOODED_BADLANDS, Biome.SWAMP, Biome.MANGROVE_SWAMP),
    WATERS_AND_COASTS(Biome.RIVER, Biome.FROZEN_RIVER, Biome.BEACH, Biome.SNOWY_BEACH, Biome.STONY_SHORE, Biome.OCEAN, Biome.COLD_OCEAN, Biome.FROZEN_OCEAN, Biome.LUKEWARM_OCEAN, Biome.WARM_OCEAN, Biome.DEEP_OCEAN, Biome.DEEP_COLD_OCEAN, Biome.DEEP_FROZEN_OCEAN, Biome.DEEP_LUKEWARM_OCEAN, Biome.MUSHROOM_FIELDS),
    UNDERGROUND(Biome.LUSH_CAVES, Biome.DRIPSTONE_CAVES, Biome.DEEP_DARK),
    NETHER(Biome.NETHER_WASTES, Biome.CRIMSON_FOREST, Biome.WARPED_FOREST, Biome.SOUL_SAND_VALLEY, Biome.BASALT_DELTAS),
    THE_END(Biome.THE_END, Biome.SMALL_END_ISLANDS, Biome.END_MIDLANDS, Biome.END_HIGHLANDS, Biome.END_BARRENS);

    private final Set<Biome> biomes;

    BiomeGroup(Biome... biomesArray) {
        this.biomes = Set.of(biomesArray);
    }

    public boolean contains(Biome biome) {
        return this.biomes.contains(biome);
    }
}