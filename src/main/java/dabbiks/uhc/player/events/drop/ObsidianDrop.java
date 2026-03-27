package dabbiks.uhc.player.events.drop;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Biome;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class ObsidianDrop extends DropItem {

    private final Set<Material> TARGET_BLOCKS = EnumSet.of(
            Material.STONE, Material.DIORITE, Material.ANDESITE,
            Material.GRANITE, Material.BASALT, Material.DEEPSLATE,
            Material.DRIPSTONE_BLOCK, Material.TUFF
    );

    @Override public Material getMaterial() { return Material.OBSIDIAN; }
    @Override public Material getSmeltedMaterial() { return null; }
    @Override public int getCustomModelData() { return 0; }
    @Override public int getMinAmount() { return 1; }
    @Override public int getMaxAmount() { return 1; }
    @Override public String getMessage() { return "§6+ §5Obsydian"; }
    @Override public Sound getSound() { return Sound.ENTITY_EXPERIENCE_ORB_PICKUP; }
    @Override public String getDisplayName() { return null; }
    @Override public List<String> getLore() { return null; }

    @Override
    public double getChance(Material pickaxe, Material block, Biome biome) {
        if (!TARGET_BLOCKS.contains(block)) return 0.0;

        return 0.0;
    }
}