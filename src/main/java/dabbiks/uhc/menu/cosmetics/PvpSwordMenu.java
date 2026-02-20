package dabbiks.uhc.menu.cosmetics;

import dabbiks.uhc.cosmetics.PvpSword;
import dabbiks.uhc.game.gameplay.items.ItemTags;
import dabbiks.uhc.player.data.persistent.PersistentData;
import dabbiks.uhc.player.data.persistent.PersistentDataManager;
import dabbiks.uhc.player.data.persistent.PersistentStats;
import de.tr7zw.nbtapi.NBTItem;
import fr.mrmicky.fastinv.FastInv;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static dabbiks.uhc.Main.*;

public class PvpSwordMenu extends FastInv {

    private final Player player;
    private final PersistentData persistentData;
    private final PvpSword[] pvpSwords;

    public PvpSwordMenu(Player player, PersistentData persistentData) {
        super(54, "Miecze PvP");
        this.player = player;
        this.persistentData = persistentData;
        this.pvpSwords = PvpSword.values();

        render();
    }

    private void render() {
        int[] slots = {
                10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                28, 29, 30, 31, 32, 33, 34,
                37, 38, 39, 40, 41, 42, 43
        };

        int index = 0;
        for (PvpSword pvpSword : pvpSwords) {
            if (index >= slots.length) break;

            int slot = slots[index++];
            setItem(slot, createIcon(pvpSword), e -> {
                boolean unlocked = persistentData.hasPvpSword(pvpSword);
                if (unlocked) {
                    handleSelect(pvpSword);
                } else {
                    if (e.getClick().isLeftClick()) {
                        handleBuy(pvpSword, true);
                    } else if (e.getClick().isRightClick()) {
                        handleBuy(pvpSword, false);
                    }
                }
            });
        }
    }

    private ItemStack createIcon(PvpSword pvpSword) {
        ItemStack item = new ItemStack(pvpSword.getMaterial());
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(pvpSword.getTier().getIcon() + "§f" + pvpSword.getName());
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

            List<String> lore = new ArrayList<>();

            boolean unlocked = persistentData.hasPvpSword(pvpSword);
            boolean selected = persistentData.getPvpSword() == pvpSword;

            if (selected) {
                lore.add(symbolU.MOUSE_LEFT + "§a Wybrany miecz");
            } else if (unlocked) {
                lore.add(symbolU.MOUSE_LEFT + "§e Wybierz miecz");
            } else {
                int coins = persistentData.getStats().getOrDefault(PersistentStats.COINS, 0);
                int powder = persistentData.getStats().getOrDefault(PersistentStats.POWDER, 0);

                boolean hasCoins = coins >= pvpSword.getCoinsCost();
                boolean hasPowder = powder >= pvpSword.getPowderCost();

                lore.add((hasCoins ? symbolU.MOUSE_LEFT + "§7 Kup za §a" + pvpSword.getCoinsCost() + "§f" + symbolU.SCOREBOARD_COIN
                        : symbolU.MOUSE_LEFT + "§7 Brakuje §c" + (pvpSword.getCoinsCost() - coins) + "§f" + symbolU.SCOREBOARD_COIN));
                lore.add((hasPowder ? symbolU.MOUSE_RIGHT + "§7 Kup za §a" + pvpSword.getPowderCost() + "§f" + symbolU.SCOREBOARD_POWDER
                        : symbolU.MOUSE_RIGHT + "§7 Brakuje §c" + (pvpSword.getPowderCost() - powder) + "§f" + symbolU.SCOREBOARD_POWDER));
            }

            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setInteger(ItemTags.UHC_ITEM.name(), 1);

        return nbtItem.getItem();
    }

    private void handleBuy(PvpSword pvpSword, boolean useCoins) {
        int cost = useCoins ? pvpSword.getCoinsCost() : pvpSword.getPowderCost();
        PersistentStats currency = useCoins ? PersistentStats.COINS : PersistentStats.POWDER;
        int playerCurrency = persistentData.getStats().getOrDefault(currency, 0);

        if (playerCurrency < cost) {
            soundU.playSoundToPlayer(player, Sound.ENTITY_VILLAGER_NO, 1, 1);
            return;
        }

        persistentData.removeStats(currency, cost);
        persistentData.unlockPvpSword(pvpSword);
        PersistentDataManager.saveData(player.getUniqueId());
        PurchaseMessage.send(player, "§7Miecz PVP §c" + pvpSword.getName().toUpperCase(), cost, useCoins);
        soundU.playSoundToPlayers(playerListU.getAllPlayers(), Sound.BLOCK_NOTE_BLOCK_BIT, 0.3f, 2);
        player.sendMessage("§aZakupiono przedmiot " + pvpSword.getName() + "!");

        render();
    }

    private void handleSelect(PvpSword pvpSword) {
        persistentData.setPvpSword(pvpSword);
        player.sendMessage("§aWybrano miecz: " + pvpSword.getName());
        soundU.playSoundToPlayer(player, Sound.UI_BUTTON_CLICK, 1, 1);
        render();
    }
}