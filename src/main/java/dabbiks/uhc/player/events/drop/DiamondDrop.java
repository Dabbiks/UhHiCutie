package dabbiks.uhc.player.events.drop;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Biome;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class DiamondDrop extends DropItem {

    private final Set<Material> TARGET_BLOCKS = EnumSet.of(
            Material.STONE, Material.DIORITE, Material.ANDESITE,
            Material.GRANITE, Material.BASALT, Material.DEEPSLATE,
            Material.DRIPSTONE_BLOCK, Material.TUFF
    );

    @Override public Material getMaterial() { return Material.PAPER; }
    @Override public Material getSmeltedMaterial() { return Material.PAPER; }
    @Override public int getCustomModelData() { return 20; }
    @Override public int getMinAmount() { return 1; }
    @Override public int getMaxAmount() { return 1; }
    @Override public String getMessage() { return "§6+ §b§lDIAMENT"; }
    @Override public Sound getSound() { return Sound.ENTITY_GLOW_SQUID_AMBIENT; }
    @Override public String getDisplayName() { return "§fŁupek diamentu"; }
    @Override public List<String> getLore() { return null; }

    @Override
    public double getChance(Material pickaxe, Material block, Biome biome) {
        if (!TARGET_BLOCKS.contains(block)) return 0.0;

        double base = switch (pickaxe) {
            case IRON_PICKAXE -> 0.00974;
            case GOLDEN_PICKAXE -> 0.01495;
            case DIAMOND_PICKAXE -> 0.01265;
            case NETHERITE_PICKAXE -> 0.01564;
            default -> 0.0;
        };

        if (BiomeGroup.MOUNTAINS.contains(biome)) base *= 1.25;
        if (BiomeGroup.PLAINS_AND_FORESTS.contains(biome)) base *= 0.8;
        return base;
    }
}