package dabbiks.uhc.menu.cosmetics;

import dabbiks.uhc.game.gameplay.items.ItemTags;
import dabbiks.uhc.menu.Discount;
import dabbiks.uhc.menu.DiscountType;
import dabbiks.uhc.player.data.persistent.PersistentData;
import dabbiks.uhc.player.data.persistent.PersistentStats;
import de.tr7zw.nbtapi.NBTItem;
import fr.mrmicky.fastinv.FastInv;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static dabbiks.uhc.Main.symbolU;

public class CosmeticsMainMenu extends FastInv {

    private final Player player;
    private final PersistentData persistentData;

    public CosmeticsMainMenu(Player player, PersistentData persistentData) {
        super(27, "Dodatki");
        this.player = player;
        this.persistentData = persistentData;

        render();
    }

    private void render() {

        int trailSlot = 10;
        int killSoundSlot = 12;
        int pvpSwordSlot = 14;
        int cageSlot = 16;

        int coinSlot = 21;
        int discountSlot = 22;
        int powderSlot = 23;

        setItem(trailSlot, createIcon(symbolU.MOUSE_LEFT + " §fSmugi lotu", Material.FIREWORK_ROCKET, null), e -> {
            super.getInventory().close();
            new TrailMenu(player, persistentData).open(player);
        });
        setItem(killSoundSlot, createIcon(symbolU.MOUSE_LEFT + " §fDźwięki zabójstwa", Material.MUSIC_DISC_BLOCKS, null), e -> {
            super.getInventory().close();
            new KillSoundMenu(player, persistentData).open(player);
        });
        setItem(pvpSwordSlot, createIcon(symbolU.MOUSE_LEFT + " §fMiecze PvP", Material.GOLDEN_SWORD, null), e -> {
            super.getInventory().close();
            new PvpSwordMenu(player, persistentData).open(player);
        });
        setItem(cageSlot, createIcon(symbolU.MOUSE_LEFT + " §fKlatki startowe", Material.GLASS, null), e -> {
            super.getInventory().close();
            new CageMenu(player, persistentData).open(player);
        });

        List<String> discountLore = new ArrayList<>();
        Map<DiscountType, Double> discounts = Discount.getDiscounts();

        if (discounts.isEmpty()) {
            discountLore.add("§7Brak aktywnych promocji.");
        } else {
            for (Map.Entry<DiscountType, Double> entry : discounts.entrySet()) {
                if (entry.getValue() < 1.0) {
                    int percent = (int) ((1.0 - entry.getValue()) * 100);
                    discountLore.add("§8- §c-" + percent + "% §7" + entry.getKey().getName());
                }
            }
            if (discountLore.isEmpty()) discountLore.add("§7Brak aktywnych promocji.");
        }

        setItem(coinSlot, createIcon("§f" + persistentData.getStats().getOrDefault(PersistentStats.COINS, 0) +
                symbolU.SCOREBOARD_COIN, Material.SUNFLOWER, null));
        setItem(discountSlot, createIcon("§c% §fTrwające promocje", Material.RED_DYE, discountLore));
        setItem(powderSlot, createIcon("§f" + persistentData.getStats().getOrDefault(PersistentStats.POWDER, 0) +
                symbolU.SCOREBOARD_POWDER, Material.AMETHYST_SHARD, null));
    }

    private ItemStack createIcon(String name, Material material, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(name);
            if (lore != null) meta.setLore(lore);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }

        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setInteger(ItemTags.UHC_ITEM.name(), 1);

        return nbtItem.getItem();
    }
}