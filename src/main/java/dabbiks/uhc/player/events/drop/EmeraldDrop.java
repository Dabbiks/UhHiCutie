package dabbiks.uhc.player.events.drop;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Biome;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class EmeraldDrop extends DropItem {

    private final Set<Material> TARGET_BLOCKS = EnumSet.of(
            Material.STONE, Material.DIORITE, Material.ANDESITE,
            Material.GRANITE, Material.BASALT, Material.DEEPSLATE,
            Material.DRIPSTONE_BLOCK, Material.TUFF
    );

    @Override public Material getMaterial() { return Material.PAPER; }
    @Override public Material getSmeltedMaterial() { return Material.PAPER; }
    @Override public int getCustomModelData() { return 21; }
    @Override public int getMinAmount() { return 1; }
    @Override public int getMaxAmount() { return 1; }
    @Override public String getMessage() { return "§6+ §a§lSZMARAGD"; }
    @Override public Sound getSound() { return null; }
    @Override public String getDisplayName() { return "§fŁupek szmaragdu"; }
    @Override public List<String> getLore() { return null; }

    @Override
    public double getChance(Material pickaxe, Material block, Biome biome) {
        if (!TARGET_BLOCKS.contains(block)) return 0.0;

        double base = switch (pickaxe) {
            case IRON_PICKAXE -> 0.01495;
            case GOLDEN_PICKAXE -> 0.010465;
            case DIAMOND_PICKAXE -> 0.0299;
            case NETHERITE_PICKAXE -> 0.0299;
            default -> 0.0;
        };

        if (BiomeGroup.MOUNTAINS.contains(biome)) base *= 1.75;
        if (BiomeGroup.JUNGLES_AND_SAVANNAS.contains(biome)) base *= 1.25;
        return base;
    }
}