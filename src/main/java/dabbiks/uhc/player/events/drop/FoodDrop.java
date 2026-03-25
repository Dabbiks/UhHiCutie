package dabbiks.uhc.player.events.drop;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class FoodDrop implements Listener {

    private static final Set<Biome> TRUFFLE_BIOMES = Set.of(
            Biome.DARK_FOREST,
            Biome.TAIGA,
            Biome.SNOWY_TAIGA,
            Biome.OLD_GROWTH_SPRUCE_TAIGA,
            Biome.OLD_GROWTH_PINE_TAIGA,
            Biome.GROVE,
            Biome.PALE_GARDEN
    );

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Material type = block.getType();
        int roll = ThreadLocalRandom.current().nextInt(100);

        if (type == Material.GRASS_BLOCK || type == Material.DIRT) {
            if (TRUFFLE_BIOMES.contains(block.getBiome()) && roll < 10) {
                dropItem(block, createCustomItem("Trufla", 12));
            }
            return;
        }

        if (isLeaf(type)) {
            if (roll >= 3) return;

            ItemStack drop = getLeafDrop(type);
            if (drop != null) {
                dropItem(block, drop);
            }
        }
    }

    private boolean isLeaf(Material material) {
        return material == Material.OAK_LEAVES ||
                material == Material.JUNGLE_LEAVES ||
                material == Material.BIRCH_LEAVES;
    }

    private ItemStack getLeafDrop(Material leafType) {
        switch (leafType) {
            case OAK_LEAVES:
                return new ItemStack(Material.APPLE);
            case JUNGLE_LEAVES:
                return createCustomItem("Banan", 11);
            case BIRCH_LEAVES:
                return createCustomItem("Kasztan", 13);
            default:
                return null;
        }
    }

    private void dropItem(Block block, ItemStack item) {
        block.getWorld().dropItemNaturally(block.getLocation(), item);
    }

    private ItemStack createCustomItem(String name, int cmd) {
        ItemStack item = new ItemStack(Material.APPLE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§f" + name);
            meta.setCustomModelData(cmd);
            item.setItemMeta(meta);
        }
        return item;
    }
}