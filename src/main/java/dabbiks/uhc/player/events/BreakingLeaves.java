package dabbiks.uhc.player.events;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.concurrent.ThreadLocalRandom;

public class BreakingLeaves implements Listener {

    @EventHandler
    public void onLeafBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Material type = block.getType();

        if (!isLeaf(type)) return;

        if (ThreadLocalRandom.current().nextInt(100) >= 3) return;

        ItemStack drop = getCustomDrop(type);
        if (drop != null) {
            block.getWorld().dropItemNaturally(block.getLocation(), drop);
        }
    }

    private boolean isLeaf(Material material) {
        return material == Material.OAK_LEAVES ||
                material == Material.JUNGLE_LEAVES ||
                material == Material.DARK_OAK_LEAVES ||
                material == Material.BIRCH_LEAVES;
    }

    private ItemStack getCustomDrop(Material leafType) {
        switch (leafType) {
            case OAK_LEAVES:
                return new ItemStack(Material.APPLE);
            case JUNGLE_LEAVES:
                return createCustomApple("Banan", 11);
            case DARK_OAK_LEAVES:
                return createCustomApple("Trufla", 12);
            case BIRCH_LEAVES:
                return createCustomApple("Kasztan", 13);
            default:
                return null;
        }
    }

    private ItemStack createCustomApple(String name, int cmd) {
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