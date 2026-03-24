package dabbiks.uhc.game.gameplay.items.stations.table;

import dabbiks.uhc.ConsoleLogger;
import dabbiks.uhc.game.gameplay.items.ItemBuilder;
import dabbiks.uhc.game.gameplay.items.ItemDeconstructor;
import dabbiks.uhc.game.gameplay.items.ItemInstance;
import dabbiks.uhc.game.gameplay.items.ItemTags;
import dabbiks.uhc.game.gameplay.items.data.enchants.EnchantCalculator;
import dabbiks.uhc.game.gameplay.items.data.enchants.EnchantData;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadableItemNBT;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
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
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static dabbiks.uhc.Main.soundU;

public class EnchantingTableManager implements Listener {

    private final Map<Location, EnchantingTableInstance> tables = new HashMap<>();
    private final EnchantCalculator enchantCalculator = new EnchantCalculator();

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        if (block.getType() != Material.ENCHANTING_TABLE) return;
        tables.put(block.getLocation(), new EnchantingTableInstance(block.getLocation()));
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        if (event.getClickedBlock().getType() != Material.ENCHANTING_TABLE) return;
        if (!event.getAction().isRightClick()) return;
        event.setCancelled(true);

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        EnchantingTableInstance table = tables.get(event.getClickedBlock().getLocation());
        if (table == null) return;

        boolean filled = false;
        if (item.getType() == Material.LAPIS_LAZULI) filled = table.fill();
        if (filled) item.setAmount(item.getAmount()-1);
        if (item.getType() == Material.LAPIS_LAZULI) return;

        if (!table.slots[3].filled) {
            player.sendMessage("§cW stole brakuje lapisu");
            soundU.playSoundAtLocation(event.getClickedBlock().getLocation(), Sound.BLOCK_CHISELED_BOOKSHELF_INSERT_ENCHANTED, 0.6f, 1);
            return;
        }

        int bookshelves = countBookshelves(table.location);
        if (bookshelves > 15) bookshelves = 15;
        int power = bookshelves * 2;

        if (player.getLevel() < 3) {
            player.sendMessage("§cMusisz mieć co najmniej 3. poziom");
            return;
        }
        if (player.getLevel() < power) {
            player.sendMessage("§cWymagany poziom " + power);
            return;
        }

        if (item.isEmpty() || item.getType().equals(Material.AIR)) return;

        boolean canBeEnchanted = NBT.get(item, (Function<ReadableItemNBT, Boolean>) nbt -> nbt.getBoolean(ItemTags.CAN_BE_ENCHANTED.name()));
        if (!canBeEnchanted) {
            player.sendMessage("§cTego przedmiotu nie da się zakląć");
            return;
        }

        ItemInstance itemInstance = new ItemDeconstructor(item).deconstruct();
        if (itemInstance.getEnchants() != null && !itemInstance.getEnchants().isEmpty()) {
            player.sendMessage("§cTen przedmiot jest już zaklęty");
            return;
        }

        player.setLevel(player.getLevel() - 3);
        List<EnchantData> enchants = enchantCalculator.calculateEnchants(power, itemInstance.getEnchantSlot());
        if (enchants.isEmpty()) {
            ConsoleLogger.sendWarning(ConsoleLogger.LogType.ENCHANTS, "Error while calculating possible enchants");
            return;
        }
        itemInstance.setEnchants(enchants);
        itemInstance.setIsEnchanted(true);

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
        if (event.getBlock().getType() == Material.ENCHANTING_TABLE) {
            if (!tables.containsKey(event.getBlock().getLocation())) return;
            EnchantingTableInstance table = tables.get(event.getBlock().getLocation());
            table.destroy();
        }
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        for (Block block : event.blockList()) {
            if (block.getType() == Material.ENCHANTING_TABLE) {
                if (!tables.containsKey(block.getLocation())) return;
                EnchantingTableInstance table = tables.get(block.getLocation());
                table.destroy();
            }
        }
    }

    private int countBookshelves(Location table) {
        if (table == null || table.getWorld() == null) return 0;

        World world = table.getWorld();
        int tx = table.getBlockX(), ty = table.getBlockY(), tz = table.getBlockZ();
        int count = 0;

        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                if (Math.abs(dx) < 2 && Math.abs(dz) < 2) continue;

                if (world.getBlockAt(tx + Integer.signum(dx), ty, tz + Integer.signum(dz)).getType() != Material.AIR) continue;

                count += checkBookshelf(world, tx + dx, ty, tz + dz);
                count += checkBookshelf(world, tx + dx, ty + 1, tz + dz);
            }}

        return Math.min(count, 15);
    }

    private int checkBookshelf(World world, int x, int y, int z) {
        return world.getBlockAt(x, y, z).getType() == Material.BOOKSHELF ? 1 : 0;
    }
}