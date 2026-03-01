package dabbiks.uhc.player.events;

import dabbiks.uhc.player.data.persistent.PersistentData;
import dabbiks.uhc.player.data.persistent.PersistentDataManager;
import dabbiks.uhc.player.data.session.SessionData;
import dabbiks.uhc.player.data.session.SessionDataManager;
import dabbiks.uhc.player.data.session.SessionTags;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadableItemNBT;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

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

    @EventHandler
    public void onMine(BlockBreakEvent event) {
        Player player = event.getPlayer();
        SessionData sessionData = SessionDataManager.getData(player.getUniqueId());
        PersistentData persistentData = PersistentDataManager.getData(player.getUniqueId());
        Block block = event.getBlock();

        if (!ORES.contains(block.getType())) return;

        player.giveExp(5);

        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        Collection<ItemStack> drops = block.getDrops(itemInHand, player);

        for (ItemStack itemStack : drops) {
            block.getWorld().dropItemNaturally(block.getLocation(), itemStack);
        }

        boolean hasSmelting = itemInHand.getType() != Material.AIR &&
                Boolean.TRUE.equals(NBT.get(itemInHand, (Function<ReadableItemNBT, Boolean>) nbt -> nbt.hasTag("SMELTING")));

        if (hasSmelting) {
            event.setDropItems(false);
            for (ItemStack drop : drops) {
                drop.setType(getSmeltedMaterial(drop.getType()));
                block.getWorld().dropItemNaturally(block.getLocation(), drop);
            }
        }

        if (sessionData.hasTag(SessionTags.MINER)) {
            assert persistentData != null;
            int level = persistentData.getChampionLevel("miner");
            double chance = 0.02 * level;

            if (random.nextDouble() <= chance) {
                for (ItemStack drop : drops) {
                    ItemStack bonus = drop.clone();
                    if (hasSmelting) {
                        bonus.setType(getSmeltedMaterial(bonus.getType()));
                    }
                    block.getWorld().dropItemNaturally(block.getLocation(), bonus);
                }
            }
        }
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