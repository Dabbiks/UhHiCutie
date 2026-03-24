package dabbiks.uhc.game.gameplay.items.stations.smithingtable;

import dabbiks.uhc.game.gameplay.items.ItemBuilder;
import dabbiks.uhc.game.gameplay.items.ItemDeconstructor;
import dabbiks.uhc.game.gameplay.items.ItemInstance;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

import static dabbiks.uhc.Main.soundU;

public class SmithingTableManager implements Listener {

    private final Map<Location, SmithingTableInstance> tables = new HashMap<>();

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        if (block.getType() != Material.SMITHING_TABLE) return;
        tables.put(block.getLocation(), new SmithingTableInstance(block.getLocation()));
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        if (event.getClickedBlock().getType() != Material.SMITHING_TABLE) return;
        if (!tables.containsKey(event.getClickedBlock().getLocation())) {
            tables.put(event.getClickedBlock().getLocation(), new SmithingTableInstance(event.getClickedBlock().getLocation()));
        }
        if (!event.getAction().isRightClick()) return;
        event.setCancelled(true);

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        SmithingTableInstance table = tables.get(event.getClickedBlock().getLocation());
        if (table == null) return;

        boolean filled = false;
        if (item.getType() == Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE) filled = table.fill();
        if (filled) item.setAmount(item.getAmount()-1);
        if (item.getType() == Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE) return;

        if (!table.slot.filled) {
            player.sendMessage("§cPotrzebujesz szablonu ulepszenia!");
            soundU.playSoundAtLocation(event.getClickedBlock().getLocation(), Sound.BLOCK_ANVIL_LAND, 0.6f, 2);
            return;
        }

        if (item.isEmpty() || item.getType().equals(Material.AIR)) return;

        boolean isDiamond = item.getType().name().startsWith("DIAMOND");
        if (!isDiamond) {
            player.sendMessage("§cTego przedmiotu nie da się ulepszyć");
            return;
        }

        String netheriteMaterial = item.getType().name().replace("DIAMOND", "NETHERITE");

        if (Material.getMaterial(netheriteMaterial) == null) {
            player.sendMessage("§cTego przedmiotu nie da się ulepszyć");
            return;
        }



        ItemInstance itemInstance = new ItemDeconstructor(item).deconstruct();
        itemInstance.setMaterial(netheriteMaterial);
        ItemStack resultItem = new ItemBuilder(itemInstance).build();
        ItemStack handItem = player.getInventory().getItemInMainHand();

        if (handItem.getAmount() > 1) {
            handItem.setAmount(handItem.getAmount() - 1);
            resultItem.setAmount(1);

            HashMap<Integer, ItemStack> leftOver = player.getInventory().addItem(resultItem);
            if (!leftOver.isEmpty()) {
                player.getWorld().dropItemNaturally(player.getLocation(), resultItem);
            }
        } else {
            player.getInventory().setItemInMainHand(resultItem);
        }

        table.reset();
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.SMITHING_TABLE) {
            if (!tables.containsKey(event.getBlock().getLocation())) return;
            SmithingTableInstance table = tables.get(event.getBlock().getLocation());
            table.destroy();
        }
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        for (Block block : event.blockList()) {
            if (block.getType() == Material.SMITHING_TABLE) {
                if (!tables.containsKey(block.getLocation())) return;
                SmithingTableInstance table = tables.get(block.getLocation());
                table.destroy();
            }
        }
    }
}
