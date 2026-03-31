package dabbiks.uhc.menu;

import dabbiks.uhc.cosmetics.chest.ChestType;
import dabbiks.uhc.cosmetics.chest.KeyType;
import dabbiks.uhc.cosmetics.chest.MysteryChestSession;
import dabbiks.uhc.game.gameplay.items.ItemTags;
import dabbiks.uhc.player.data.persistent.PersistentData;
import dabbiks.uhc.player.data.persistent.PersistentDataManager;
import dabbiks.uhc.player.data.persistent.PersistentStats;
import de.tr7zw.nbtapi.NBT;
import fr.mrmicky.fastinv.FastInv;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static dabbiks.uhc.Main.*;

public class ChestMenu extends FastInv {

    private final Player player;
    private final PersistentData persistentData;
    private final double chestPriceMultiplier = Discount.getDiscounts().getOrDefault(DiscountType.CHEST, 1.0);
    private final double keyPriceMultiplier = Discount.getDiscounts().getOrDefault(DiscountType.KEY, 1.0);

    public ChestMenu(Player player, PersistentData persistentData) {
        super(36, "Magiczna skrzynia");
        this.player = player;
        this.persistentData = persistentData;

        render();
    }

    private void render() {
        int commonChest = 11;
        int rareChest = 12;
        int epicChest = 13;
        int mythicChest = 14;
        int legendaryChest = 15;
        int easterChest = 22;

        setItem(commonChest, createIcon(ChestType.COMMON), e -> {
            if (e.getClick().isShiftClick() && e.getClick().isLeftClick()) handleChestBuy(ChestType.COMMON);
            if (e.getClick().isShiftClick() && e.getClick().isRightClick()) handleKeyBuy(KeyType.COMMON);
            if (!e.getClick().isShiftClick() && e.getClick().isLeftClick()) openChest(ChestType.COMMON);
        });
        setItem(rareChest, createIcon(ChestType.RARE), e -> {
            if (e.getClick().isShiftClick() && e.getClick().isLeftClick()) handleChestBuy(ChestType.RARE);
            if (e.getClick().isShiftClick() && e.getClick().isRightClick()) handleKeyBuy(KeyType.RARE);
            if (!e.getClick().isShiftClick() && e.getClick().isLeftClick()) openChest(ChestType.RARE);
        });
        setItem(epicChest, createIcon(ChestType.EPIC), e -> {
            if (e.getClick().isShiftClick() && e.getClick().isLeftClick()) handleChestBuy(ChestType.EPIC);
            if (e.getClick().isShiftClick() && e.getClick().isRightClick()) handleKeyBuy(KeyType.EPIC);
            if (!e.getClick().isShiftClick() && e.getClick().isLeftClick()) openChest(ChestType.EPIC);
        });
        setItem(mythicChest, createIcon(ChestType.MYTHIC), e -> {
            if (e.getClick().isShiftClick() && e.getClick().isLeftClick()) handleChestBuy(ChestType.MYTHIC);
            if (e.getClick().isShiftClick() && e.getClick().isRightClick()) handleKeyBuy(KeyType.MYTHIC);
            if (!e.getClick().isShiftClick() && e.getClick().isLeftClick()) openChest(ChestType.MYTHIC);
        });
        setItem(legendaryChest, createIcon(ChestType.LEGENDARY), e -> {
            if (e.getClick().isShiftClick() && e.getClick().isLeftClick()) handleChestBuy(ChestType.LEGENDARY);
            if (e.getClick().isShiftClick() && e.getClick().isRightClick()) handleKeyBuy(KeyType.LEGENDARY);
            if (!e.getClick().isShiftClick() && e.getClick().isLeftClick()) openChest(ChestType.LEGENDARY);
        });
        setItem(easterChest, createIcon(ChestType.EASTER), e -> {
            if (e.getClick().isShiftClick() && e.getClick().isLeftClick()) handleChestBuy(ChestType.EASTER);
            if (e.getClick().isShiftClick() && e.getClick().isRightClick()) handleKeyBuy(KeyType.EASTER);
            if (!e.getClick().isShiftClick() && e.getClick().isLeftClick()) openChest(ChestType.EASTER);
        });
    }

    private ItemStack createIcon(ChestType type) {
        ItemStack item = new ItemStack(Material.HONEYCOMB);
        ItemMeta meta = item.getItemMeta();

        KeyType keyType = Arrays.stream(KeyType.values()).filter(s -> s.getIndex() == type.getIndex()).findFirst().orElse(null);
        if (keyType == null) return new ItemStack(Material.BARRIER);

        if (meta != null) {
            meta.setDisplayName(type.getName());
            meta.setItemName(type.getName());
            meta.setCustomModelData(type.getModel());
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

            List<String> lore = new ArrayList<>();

            lore.add("");
            lore.add("§7Skrzynie: §e" + persistentData.getChests(type.getIndex()));
            lore.add("§7Klucze: §e" + persistentData.getKeys(type.getIndex()));
            lore.add("§7Fragmenty kluczy §e" + persistentData.getKeyFragments(type.getIndex()) + "/3");
            lore.add("");

            boolean isEaster = type == ChestType.EASTER;
            PersistentStats currencyStat = isEaster ? PersistentStats.POWDER : PersistentStats.COINS;
            String currencySymbol = isEaster ? symbolU.SCOREBOARD_POWDER : symbolU.SCOREBOARD_COIN;
            int balance = persistentData.getStats().getOrDefault(currencyStat, 0);

            int originalChestPrice = type.getPrice();
            int discountedChestPrice = (int) (originalChestPrice * chestPriceMultiplier);

            int originalKeyPrice = keyType.getPrice();
            int discountedKeyPrice = (int) (originalKeyPrice * keyPriceMultiplier);

            boolean hasCoinsForChest = balance >= discountedChestPrice;
            boolean hasCoinsForKey = balance >= discountedKeyPrice;
            boolean hasChestAndKey = persistentData.getChests(type.getIndex()) > 0 && persistentData.getKeys(type.getIndex()) > 0;

            lore.add(hasChestAndKey ? symbolU.MOUSE_LEFT + "§a Otwórz skrzynię!" : symbolU.MOUSE_LEFT + "§c Musisz posiadać skrzynię i klucz!");

            if (hasCoinsForChest) {
                if (chestPriceMultiplier != 1.0) {
                    lore.add(symbolU.MOUSE_LEFT + " + " + symbolU.SHIFT + "§7 Kup skrzynię za §a§m" + originalChestPrice + "§r §4" + discountedChestPrice + "§f" + currencySymbol);
                } else {
                    lore.add(symbolU.MOUSE_LEFT + " + " + symbolU.SHIFT + "§7 Kup skrzynię za §a" + originalChestPrice + "§f" + currencySymbol);
                }
            } else {
                lore.add(symbolU.MOUSE_LEFT + " + " + symbolU.SHIFT + "§7 Brakuje §c" + (discountedChestPrice - balance) + "§f" + currencySymbol + "§7 do skrzyni");
            }

            if (hasCoinsForKey) {
                if (keyPriceMultiplier != 1.0) {
                    lore.add(symbolU.MOUSE_RIGHT + " + " + symbolU.SHIFT + "§7 Kup klucz za §a§m" + originalKeyPrice + "§r §4" + discountedKeyPrice + "§f" + currencySymbol);
                } else {
                    lore.add(symbolU.MOUSE_RIGHT + " + " + symbolU.SHIFT + "§7 Kup klucz za §a" + originalKeyPrice + "§f" + currencySymbol);
                }
            } else {
                lore.add(symbolU.MOUSE_RIGHT + " + " + symbolU.SHIFT + "§7 Brakuje §c" + (discountedKeyPrice - balance) + "§f" + currencySymbol + "§7 do klucza");
            }

            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        NBT.modify(item, nbt -> {
            nbt.setInteger(ItemTags.UHC_ITEM.name(), 1);
        });

        return item;
    }

    private void handleChestBuy(ChestType chestType) {
        int cost = (int) (chestType.getPrice() * chestPriceMultiplier);
        PersistentStats currencyStat = (chestType == ChestType.EASTER) ? PersistentStats.POWDER : PersistentStats.COINS;
        int balance = persistentData.getStats().getOrDefault(currencyStat, 0);

        if (balance < cost) {
            soundU.playSoundToPlayer(player, Sound.ENTITY_VILLAGER_NO, 1, 1);
            return;
        }

        persistentData.removeStats(currencyStat, cost);
        persistentData.addChests(chestType.getIndex(), 1);
        PersistentDataManager.saveData(player.getUniqueId());
        player.sendMessage("§aZakupiono skrzynię!");

        render();
    }

    private void handleKeyBuy(KeyType keyType) {
        int cost = (int) (keyType.getPrice() * keyPriceMultiplier);
        PersistentStats currencyStat = (keyType == KeyType.EASTER) ? PersistentStats.POWDER : PersistentStats.COINS;
        int balance = persistentData.getStats().getOrDefault(currencyStat, 0);

        if (balance < cost) {
            soundU.playSoundToPlayer(player, Sound.ENTITY_VILLAGER_NO, 1, 1);
            return;
        }

        persistentData.removeStats(currencyStat, cost);
        persistentData.addKeys(keyType.getIndex(), 1);
        PersistentDataManager.saveData(player.getUniqueId());
        player.sendMessage("§aZakupiono klucz!");

        render();
    }

    private void openChest(ChestType chestType) {
        if (persistentData.getChests(chestType.getIndex()) <= 0) {
            soundU.playSoundToPlayer(player, Sound.ENTITY_VILLAGER_NO, 1, 1);
            return;
        }
        if (persistentData.getKeys(chestType.getIndex()) <= 0) {
            soundU.playSoundToPlayer(player, Sound.ENTITY_VILLAGER_NO, 1, 1);
            return;
        }

        new MysteryChestSession(player.getUniqueId(), chestType);

        render();
    }
}