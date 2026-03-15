package dabbiks.uhc.player.events;

import dabbiks.uhc.game.gameplay.items.data.enchants.EnchantManager;
import dabbiks.uhc.game.gameplay.items.data.enchants.EnchantType;
import dabbiks.uhc.player.data.persistent.PersistentData;
import dabbiks.uhc.player.data.persistent.PersistentDataManager;
import dabbiks.uhc.player.data.session.SessionData;
import dabbiks.uhc.player.data.session.SessionDataManager;
import dabbiks.uhc.player.data.session.SessionTags;
import dabbiks.uhc.player.events.drop.*;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class Mining implements Listener {

    private final Set<DropItem> customDrops = new HashSet<>();
    private final EnchantManager enchantManager = new EnchantManager();

    private final Set<Material> STONE_TYPES = EnumSet.of(
            Material.STONE, Material.ANDESITE, Material.DIORITE,
            Material.GRANITE, Material.DEEPSLATE, Material.TUFF,
            Material.BASALT, Material.DRIPSTONE_BLOCK
    );

    private final Set<Material> COBBLESTONE_YIELDERS = EnumSet.of(
            Material.STONE, Material.ANDESITE, Material.DIORITE,
            Material.GRANITE, Material.DEEPSLATE, Material.TUFF
    );

    private final Set<Material> ORES_AND_RAW_BLOCKS = EnumSet.of(
            Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE,
            Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE, Material.RAW_IRON_BLOCK,
            Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE, Material.RAW_COPPER_BLOCK,
            Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE, Material.RAW_GOLD_BLOCK,
            Material.REDSTONE_ORE, Material.DEEPSLATE_REDSTONE_ORE,
            Material.EMERALD_ORE, Material.DEEPSLATE_EMERALD_ORE,
            Material.LAPIS_ORE, Material.DEEPSLATE_LAPIS_ORE,
            Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE,
            Material.NETHER_GOLD_ORE, Material.NETHER_QUARTZ_ORE,
            Material.ANCIENT_DEBRIS
    );

    public Mining() {
        customDrops.add(new CoalDrop());
        customDrops.add(new IronDrop());
        customDrops.add(new QuartzDrop());
        customDrops.add(new GoldDrop());
        customDrops.add(new RedstoneDrop());
        customDrops.add(new LapisDrop());
        customDrops.add(new EmeraldDrop());
        customDrops.add(new DiamondDrop());
    }

    @EventHandler
    public void onMine(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) return;

        Block block = event.getBlock();
        Material blockType = block.getType();

        if (blockType == Material.GRAVEL) {
            SessionData sessionData = SessionDataManager.getData(player.getUniqueId());
            if (sessionData != null && sessionData.hasTag(SessionTags.ARCHER)) {
                PersistentData pData = PersistentDataManager.getData(player.getUniqueId());
                if (pData != null) {
                    int level = pData.getChampionLevel("archer");
                    double chance = 0.20 + (level * 0.04);
                    if (ThreadLocalRandom.current().nextDouble() <= chance) {
                        block.getWorld().dropItemNaturally(block.getLocation().add(0.5, 0.5, 0.5), new ItemStack(Material.ARROW, 1));
                    }
                }
            }
        }

        if (ORES_AND_RAW_BLOCKS.contains(blockType)) {
            event.setDropItems(false);
            return;
        }

        ItemStack handItem = player.getInventory().getItemInMainHand();
        Material pickaxe = handItem.getType();
        Biome biome = block.getBiome();

        if (pickaxe == Material.AIR) return;

        if (STONE_TYPES.contains(blockType)) {
            player.giveExp(1);
        }

        if (COBBLESTONE_YIELDERS.contains(blockType)) {
            event.setDropItems(false);
            block.getWorld().dropItemNaturally(block.getLocation().add(0.5, 0.5, 0.5), new ItemStack(Material.COBBLESTONE, 1));
        }

        int smeltingLevel = enchantManager.getItemLevel(handItem, EnchantType.SMELTING);
        boolean isSmelted = smeltingLevel > 0;

        int fortuneLevel = enchantManager.getItemLevel(handItem, EnchantType.FORTUNE);
        if (fortuneLevel == 0 && handItem.hasItemMeta()) {
            fortuneLevel = handItem.getItemMeta().getEnchantLevel(Enchantment.FORTUNE);
        }

        boolean droppedAnything = false;

        for (DropItem dropItem : customDrops) {
            double chance = dropItem.getChance(pickaxe, blockType, biome);

            if (chance > 0 && ThreadLocalRandom.current().nextDouble() <= chance) {
                droppedAnything = true;

                ItemStack itemToDrop = dropItem.generateItem(fortuneLevel, isSmelted);
                block.getWorld().dropItemNaturally(block.getLocation().add(0.5, 0.5, 0.5), itemToDrop);

                if (dropItem.getSound() != null) {
                    player.playSound(player.getLocation(), dropItem.getSound(), 1.0f, 1.0f);
                }

                if (dropItem.getMessage() != null) {
                    player.sendMessage(dropItem.getMessage());
                }
            }
        }

        if (droppedAnything) {
            player.giveExp(5);
        }
    }
}