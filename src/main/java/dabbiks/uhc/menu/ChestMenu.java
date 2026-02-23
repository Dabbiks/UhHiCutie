package dabbiks.uhc.menu;

import dabbiks.uhc.cosmetics.PvpSword;
import dabbiks.uhc.cosmetics.chest.ChestType;
import dabbiks.uhc.cosmetics.chest.KeyType;
import dabbiks.uhc.cosmetics.chest.MysteryChestSession;
import dabbiks.uhc.game.gameplay.items.ItemTags;
import dabbiks.uhc.player.data.persistent.PersistentData;
import dabbiks.uhc.player.data.persistent.PersistentDataManager;
import dabbiks.uhc.player.data.persistent.PersistentStats;
import de.tr7zw.nbtapi.NBTItem;
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

    public ChestMenu(Player player, PersistentData persistentData) {
        super(27, "Magiczna skrzynia");
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
    }

    private ItemStack createIcon(ChestType type) {
        ItemStack item = new ItemStack(Material.HONEYCOMB);
        ItemMeta meta = item.getItemMeta();

        KeyType keyType = Arrays.stream(KeyType.values()).filter(s -> s.getIndex() == type.getIndex()).findFirst().orElse(null);
        if (keyType == null) return new ItemStack(Material.BARRIER);

        if (meta != null) {
            meta.setDisplayName("");
            meta.setCustomModelData(type.getModel());
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

            List<String> lore = new ArrayList<>();

            lore.add(type.getName());
            lore.add("");
            lore.add("§7Skrzynie: §e" + persistentData.getChests(type.getIndex()));
            lore.add("§7Klucze: §e" + persistentData.getKeys(type.getIndex()));
            lore.add("§7Fragmenty kluczy §e" + persistentData.getKeyFragments(type.getIndex()) + "/3");
            lore.add("");

            int coins = persistentData.getStats().getOrDefault(PersistentStats.COINS, 0);

            boolean hasCoinsForChest = coins >= type.getPrice();
            boolean hasCoinsForKey = coins >= keyType.getPrice();
            boolean hasChestAndKey = persistentData.getChests(type.getIndex()) > 0 && persistentData.getKeys(type.getIndex()) > 0;

            lore.add(hasChestAndKey ? symbolU.MOUSE_LEFT + "§a Otwórz skrzynię!" : symbolU.MOUSE_LEFT + "§c Musisz posiadać skrzynię i klucz!");
            lore.add((hasCoinsForChest ? symbolU.MOUSE_LEFT + " + " + symbolU.SHIFT + "§7 Kup skrzynię za §a" + coins + "§f" + symbolU.SCOREBOARD_COIN
                    : symbolU.MOUSE_LEFT + " + " + symbolU.SHIFT + "§7 Brakuje §c" + (type.getPrice() - coins) + "§f" + symbolU.SCOREBOARD_COIN + "§7 do skrzyni"));
            lore.add((hasCoinsForKey ? symbolU.MOUSE_RIGHT + " + " + symbolU.SHIFT + "§7 Kup klucz za §a" + coins + "§f" + symbolU.SCOREBOARD_COIN
                    : symbolU.MOUSE_RIGHT + " + " + symbolU.SHIFT + "§7 Brakuje §c" + (keyType.getPrice() - coins) + "§f" + symbolU.SCOREBOARD_COIN + "§7 do klucza"));

            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setInteger(ItemTags.UHC_ITEM.name(), 1);

        return nbtItem.getItem();
    }

    private void handleChestBuy(ChestType chestType) {
        int cost = chestType.getPrice();
        int coins = persistentData.getStats().getOrDefault(PersistentStats.COINS, 0);

        if (coins < cost) {
            soundU.playSoundToPlayer(player, Sound.ENTITY_VILLAGER_NO, 1, 1);
            return;
        }

        persistentData.removeStats(PersistentStats.COINS, cost);
        persistentData.addChests(chestType.getIndex(), 1);
        PersistentDataManager.saveData(player.getUniqueId());
        player.sendMessage("§aZakupiono skrzynię!");

        render();
    }

    private void handleKeyBuy(KeyType keyType) {
        int cost = keyType.getPrice();
        int coins = persistentData.getStats().getOrDefault(PersistentStats.COINS, 0);

        if (coins < cost) {
            soundU.playSoundToPlayer(player, Sound.ENTITY_VILLAGER_NO, 1, 1);
            return;
        }

        persistentData.removeStats(PersistentStats.COINS, cost);
        persistentData.addKeys(keyType.getIndex(), 1);
        PersistentDataManager.saveData(player.getUniqueId());
        player.sendMessage("§aZakupiono skrzynię!");

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

        persistentData.addChests(chestType.getIndex(), -1);
        persistentData.addKeys(chestType.getIndex(), -1);
        PersistentDataManager.saveData(player.getUniqueId());

        new MysteryChestSession(player.getUniqueId(), chestType);

        render();
    }
}