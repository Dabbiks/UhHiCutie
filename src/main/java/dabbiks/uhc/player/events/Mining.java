package dabbiks.uhc.player.events;

import dabbiks.uhc.game.gameplay.items.ItemBuilder;
import dabbiks.uhc.game.gameplay.items.ItemDeconstructor;
import dabbiks.uhc.game.gameplay.items.ItemInstance;
import dabbiks.uhc.player.data.persistent.PersistentData;
import dabbiks.uhc.player.data.persistent.PersistentDataManager;
import dabbiks.uhc.player.data.session.SessionData;
import dabbiks.uhc.player.data.session.SessionDataManager;
import dabbiks.uhc.player.data.session.SessionTags;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadableItemNBT;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

import static dabbiks.uhc.Main.soundU;

public class Mining implements Listener {

    private final Random random = new Random();

    private final Set<Material> ORES = EnumSet.of(
            Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE,
            Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE,
            Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE,
            Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE,
            Material.REDSTONE_ORE, Material.DEEPSLATE_REDSTONE_ORE,
            Material.EMERALD_ORE, Material.DEEPSLATE_EMERALD_ORE,
            Material.LAPIS_ORE, Material.DEEPSLATE_LAPIS_ORE,
            Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE,
            Material.NETHER_GOLD_ORE, Material.NETHER_QUARTZ_ORE,
            Material.ANCIENT_DEBRIS
    );

    private final Set<Material> STONE = EnumSet.of(
            Material.STONE, Material.DEEPSLATE, Material.COBBLESTONE,
            Material.COBBLED_DEEPSLATE, Material.ANDESITE, Material.DIORITE,
            Material.GRANITE, Material.BASALT
    );

    @EventHandler
    public void onMine(BlockBreakEvent event) {
        Player player = event.getPlayer();
        SessionData sessionData = SessionDataManager.getData(player.getUniqueId());
        PersistentData persistentData = PersistentDataManager.getData(player.getUniqueId());
        ItemStack handItem = player.getInventory().getItemInMainHand();
        ItemMeta handMeta = handItem.getItemMeta();
        Block block = event.getBlock();
        Random random = new Random();

        Material nextTier = getNextPickaxeTier(handItem.getType(), block.getType());
        if (nextTier != null) {
            ItemInstance instance = new ItemDeconstructor(handItem).deconstruct();
            instance.setMaterial(nextTier.name());
            player.getInventory().setItemInMainHand(new ItemBuilder(instance).build());
            soundU.playSoundToPlayer(player, Sound.BLOCK_SMITHING_TABLE_USE, 1.0f, 1.0f);
        }

        if (!ORES.contains(block.getType())) return;
        event.setDropItems(false);

        player.giveExp(5);

        Collection<ItemStack> drops = block.getDrops(handItem, player);

        int fortuneLevel = handMeta.getEnchantLevel(Enchantment.FORTUNE);

        for (ItemStack item : drops) {
            if (item.getType() == Material.COAL && random.nextDouble() > 0.5) sessionData.addElytraCharges(1);

            if (fortuneLevel > 0) {
                item.setAmount(item.getAmount() * random.nextInt(1, 3));

                if (random.nextDouble() <= (0.3 * fortuneLevel)) {
                    item.setAmount(item.getAmount() * 2);
                }
            }

            if (random.nextDouble() > 0.5) item.setAmount(item.getAmount()+1);

            Material smeltedMaterial = getSmeltedMaterial(item.getType());
            if (item.getType() != smeltedMaterial) {
                item.setType(smeltedMaterial);
            }

            if (sessionData.hasTag(SessionTags.MINER)) {
                int level = persistentData.getChampionLevel("miner");
                double chance = 0.02 * level;
                if (random.nextDouble() <= chance) {
                    item.setAmount(item.getAmount() * 2);
                }
            }

            block.getWorld().dropItemNaturally(block.getLocation().add(0.5, 0.5, 0.5), item);
        }
    }

    private Material getNextPickaxeTier(Material tool, Material block) {
        if (tool == Material.WOODEN_PICKAXE && STONE.contains(block)) return Material.STONE_PICKAXE;
        if (tool == Material.STONE_PICKAXE && (block == Material.COPPER_ORE || block == Material.DEEPSLATE_COPPER_ORE)) return Material.COPPER_PICKAXE;
        if (tool == Material.COPPER_PICKAXE && (block == Material.IRON_ORE || block == Material.DEEPSLATE_IRON_ORE)) return Material.IRON_PICKAXE;
        if (tool == Material.IRON_PICKAXE && (block == Material.DIAMOND_ORE || block == Material.DEEPSLATE_DIAMOND_ORE)) return Material.DIAMOND_PICKAXE;
        if (tool == Material.DIAMOND_PICKAXE && block == Material.ANCIENT_DEBRIS) return Material.NETHERITE_PICKAXE;
        return null;
    }

    private Material getSmeltedMaterial(Material material) {
        return switch (material) {
            case RAW_IRON, IRON_ORE, DEEPSLATE_IRON_ORE -> Material.IRON_INGOT;
            case RAW_GOLD, GOLD_ORE, DEEPSLATE_GOLD_ORE -> Material.GOLD_INGOT;
            case RAW_COPPER, COPPER_ORE, DEEPSLATE_COPPER_ORE -> Material.COPPER_INGOT;
            case ANCIENT_DEBRIS -> Material.NETHERITE_SCRAP;
            default -> material;
        };
    }
}