package dabbiks.uhc.player.events.drop;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Biome;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class RedstoneDrop extends DropItem {

    private final Set<Material> TARGET_BLOCKS = EnumSet.of(
            Material.STONE, Material.DIORITE, Material.ANDESITE,
            Material.GRANITE, Material.BASALT, Material.DEEPSLATE,
            Material.DRIPSTONE_BLOCK, Material.TUFF
    );

    @Override public Material getMaterial() { return Material.REDSTONE; }
    @Override public Material getSmeltedMaterial() { return Material.REDSTONE; }
    @Override public int getCustomModelData() { return 0; }
    @Override public int getMinAmount() { return 1; }
    @Override public int getMaxAmount() { return 1; }
    @Override public String getMessage() { return "§6+ §c§lREDSTONE"; }
    @Override public Sound getSound() { return null; }
    @Override public String getDisplayName() { return null; }
    @Override public List<String> getLore() { return null; }

    @Override
    public double getChance(Material pickaxe, Material block, Biome biome) {
        if (!TARGET_BLOCKS.contains(block)) return 0.0;

        double base = switch (pickaxe) {
            case IRON_PICKAXE -> 0.0299;
            case GOLDEN_PICKAXE -> 0.04485;
            case DIAMOND_PICKAXE -> 0.07475;
            case NETHERITE_PICKAXE -> 0.0897;
            default -> 0.0;
        };

        if (base > 0.0 && block == Material.BASALT) base *= 1.2;
        return base;
    }
}