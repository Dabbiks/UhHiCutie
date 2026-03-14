package dabbiks.uhc.player.events.drop;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Biome;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class GoldDrop extends DropItem {

    private final Set<Material> TARGET_BLOCKS = EnumSet.of(
            Material.STONE, Material.DIORITE, Material.ANDESITE,
            Material.GRANITE, Material.BASALT, Material.DEEPSLATE,
            Material.DRIPSTONE_BLOCK, Material.TUFF
    );

    @Override public Material getMaterial() { return Material.RAW_GOLD; }
    @Override public Material getSmeltedMaterial() { return Material.GOLD_INGOT; }
    @Override public int getCustomModelData() { return 0; }
    @Override public int getMinAmount() { return 1; }
    @Override public int getMaxAmount() { return 1; }
    @Override public String getMessage() { return "§6+ §e§lZŁOTO"; }
    @Override public Sound getSound() { return null; }
    @Override public String getDisplayName() { return null; }
    @Override public List<String> getLore() { return null; }

    @Override
    public double getChance(Material pickaxe, Material block, Biome biome) {
        if (!TARGET_BLOCKS.contains(block)) return 0.0;

        double base = switch (pickaxe) {
            case IRON_PICKAXE -> 0.0207;
            case DIAMOND_PICKAXE -> 0.0345;
            case NETHERITE_PICKAXE -> 0.07475;
            default -> 0.0;
        };

        if (base > 0.0) {
            if (block == Material.BASALT) base *= 1.2;
            if (biome == Biome.BADLANDS || biome == Biome.ERODED_BADLANDS || biome == Biome.WOODED_BADLANDS) base *= 2.0;
        }
        return base;
    }
}