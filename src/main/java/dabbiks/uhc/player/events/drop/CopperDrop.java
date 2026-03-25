package dabbiks.uhc.player.events.drop;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Biome;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class CopperDrop extends DropItem {

    private final Set<Material> TARGET_BLOCKS = EnumSet.of(
            Material.GRANITE
    );

    @Override public Material getMaterial() { return Material.COAL; }
    @Override public Material getSmeltedMaterial() { return Material.COAL; }
    @Override public int getCustomModelData() { return 0; }
    @Override public int getMinAmount() { return 1; }
    @Override public int getMaxAmount() { return 1; }
    @Override public String getMessage() { return "§6+ §8§lWĘGIEL"; }
    @Override public Sound getSound() { return null; }
    @Override public String getDisplayName() { return null; }
    @Override public List<String> getLore() { return null; }

    @Override
    public double getChance(Material pickaxe, Material block, Biome biome) {
        if (!TARGET_BLOCKS.contains(block)) return 0.0;

        double base = switch (pickaxe) {
            case STONE_PICKAXE -> 0.0897;
            case WOODEN_PICKAXE, IRON_PICKAXE, GOLDEN_PICKAXE, DIAMOND_PICKAXE, NETHERITE_PICKAXE -> 0.0495;
            default -> 0.0;
        };

        if (BiomeGroup.TAIGA_AND_COLD.contains(biome)) base *= 1.5;
        return base;
    }
}