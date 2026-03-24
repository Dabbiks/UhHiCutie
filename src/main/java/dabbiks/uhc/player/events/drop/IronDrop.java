package dabbiks.uhc.player.events.drop;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Biome;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class IronDrop extends DropItem {

    private final Set<Material> TARGET_BLOCKS = EnumSet.of(
            Material.STONE, Material.DIORITE, Material.ANDESITE,
            Material.GRANITE, Material.BASALT, Material.DEEPSLATE,
            Material.DRIPSTONE_BLOCK, Material.TUFF
    );

    @Override public Material getMaterial() { return Material.RAW_IRON; }
    @Override public Material getSmeltedMaterial() { return Material.IRON_INGOT; }
    @Override public int getCustomModelData() { return 0; }
    @Override public int getMinAmount() { return 1; }
    @Override public int getMaxAmount() { return 1; }
    @Override public String getMessage() { return "§6+ §7§lŻELAZO"; }
    @Override public Sound getSound() { return null; }
    @Override public String getDisplayName() { return null; }
    @Override public List<String> getLore() { return null; }

    @Override
    public double getChance(Material pickaxe, Material block, Biome biome) {
        if (!TARGET_BLOCKS.contains(block)) return 0.0;

        double base = switch (pickaxe) {
            case STONE_PICKAXE -> 0.1296;
            case IRON_PICKAXE -> 0.06085;
            case DIAMOND_PICKAXE -> 0.0698;
            case NETHERITE_PICKAXE -> 0.0897;
            default -> 0.0;
        };

        if (base > 0.0) {
            if (block == Material.BASALT) base *= 1.2;
            if (block == Material.DIORITE) base *= 1.3;
        }
        return base;
    }
}