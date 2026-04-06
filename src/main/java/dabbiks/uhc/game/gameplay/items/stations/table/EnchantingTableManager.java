package dabbiks.uhc.game.gameplay.items.stations.table;

import dabbiks.uhc.ConsoleLogger;
import dabbiks.uhc.game.gameplay.items.ItemBuilder;
import dabbiks.uhc.game.gameplay.items.ItemDeconstructor;
import dabbiks.uhc.game.gameplay.items.ItemInstance;
import dabbiks.uhc.game.gameplay.items.ItemTags;
import dabbiks.uhc.game.gameplay.items.data.enchants.EnchantCalculator;
import dabbiks.uhc.game.gameplay.items.data.enchants.EnchantData;
import dabbiks.uhc.game.teams.TeamUtils;
import dabbiks.uhc.player.data.persistent.PersistentDataManager;
import dabbiks.uhc.player.data.session.SessionData;
import dabbiks.uhc.player.data.session.SessionDataManager;
import dabbiks.uhc.player.data.session.SessionTags;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadableItemNBT;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

import static dabbiks.uhc.Main.soundU;

public class EnchantingTableManager implements Listener {

    private final Map<Location, EnchantingTableInstance> tables = new HashMap<>();
    private final EnchantCalculator enchantCalculator = new EnchantCalculator();
    private final Random random = new Random();

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        if (block.getType() != Material.ENCHANTING_TABLE) return;
        tables.put(block.getLocation(), new EnchantingTableInstance(block.getLocation()));
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null || event.getClickedBlock().getType() != Material.ENCHANTING_TABLE) return;
        if (!event.getAction().isRightClick()) return;
        event.setCancelled(true);

        EnchantingTableInstance table = tables.get(event.getClickedBlock().getLocation());
        if (table == null) return;

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType() == Material.LAPIS_LAZULI) {
            if (table.fill()) item.setAmount(item.getAmount() - 1);
            return;
        }

        if (!table.slots[3].filled) {
            player.sendMessage("§cW stole brakuje lapisu");
            soundU.playSoundAtLocation(event.getClickedBlock().getLocation(), Sound.BLOCK_CHISELED_BOOKSHELF_INSERT_ENCHANTED, 0.6f, 1);
            return;
        }

        int power = countBookshelves(table.location) * 2;

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

        handleDuelistDamage(player);
        player.setLevel(player.getLevel() - 3);

        boolean bonusEnchant = hasBonusEnchant(player);
        List<EnchantData> enchants = enchantCalculator.calculateEnchants(power, itemInstance.getEnchantSlot(), bonusEnchant);

        if (enchants.isEmpty()) {
            ConsoleLogger.sendWarning(ConsoleLogger.LogType.ENCHANTS, "Error while calculating possible enchants");
            return;
        }

        itemInstance.setEnchants(enchants);
        itemInstance.setIsEnchanted(true);

        giveResultItem(player, item, new ItemBuilder(itemInstance).build());
        table.reset(getLapisKeepChance(player));
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() != Material.ENCHANTING_TABLE) return;
        EnchantingTableInstance table = tables.get(event.getBlock().getLocation());
        if (table != null) table.destroy();
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        for (Block block : event.blockList()) {
            if (block.getType() != Material.ENCHANTING_TABLE) continue;
            EnchantingTableInstance table = tables.get(block.getLocation());
            if (table != null) table.destroy();
        }
    }

    private void handleDuelistDamage(Player player) {
        SessionData sessionData = SessionDataManager.getData(player.getUniqueId());
        if (!sessionData.hasTag(SessionTags.ENCHANTED_DUELIST)) return;

        AttributeInstance attr = player.getAttribute(Attribute.MAX_HEALTH);
        if (attr == null) return;

        attr.setBaseValue(Math.max(2.0, attr.getBaseValue() - 1.0));
        if (player.getHealth() > attr.getBaseValue()) {
            player.setHealth(attr.getBaseValue());
        }
    }

    private boolean hasBonusEnchant(Player player) {
        Team team = TeamUtils.getPlayerTeam(player);
        if (team == null) return false;

        int maxEnchanterLevel = 0;
        for (String entry : team.getEntries()) {
            Player member = Bukkit.getPlayer(entry);
            if (member == null || !member.isOnline()) continue;

            SessionData memberSession = SessionDataManager.getData(member.getUniqueId());
            if (!memberSession.hasTag(SessionTags.ENCHANTER)) continue;

            int level = PersistentDataManager.getData(member.getUniqueId()).getChampionLevel("enchanter");
            maxEnchanterLevel = Math.max(maxEnchanterLevel, level);
        }

        if (maxEnchanterLevel == 0) return false;

        int chance = 5 + maxEnchanterLevel * 2;
        return random.nextInt(100) < chance;
    }

    private void giveResultItem(Player player, ItemStack originalHandItem, ItemStack resultItem) {
        if (originalHandItem.getAmount() > 1) {
            originalHandItem.setAmount(originalHandItem.getAmount() - 1);
            resultItem.setAmount(1);

            Map<Integer, ItemStack> leftOver = player.getInventory().addItem(resultItem);
            if (!leftOver.isEmpty()) {
                player.getWorld().dropItemNaturally(player.getLocation(), resultItem);
            }
        } else {
            player.getInventory().setItemInMainHand(resultItem);
        }
    }

    private double getLapisKeepChance(Player player) {
        SessionData sessionData = SessionDataManager.getData(player.getUniqueId());
        if (sessionData.hasTag(SessionTags.BIG_LAPIS_CHANCE)) return 0.25;
        if (sessionData.hasTag(SessionTags.SMALL_LAPIS_CHANCE)) return 0.15;
        return 0.0;
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
            }
        }

        return Math.min(count, 15);
    }

    private int checkBookshelf(World world, int x, int y, int z) {
        return world.getBlockAt(x, y, z).getType() == Material.BOOKSHELF ? 1 : 0;
    }
}