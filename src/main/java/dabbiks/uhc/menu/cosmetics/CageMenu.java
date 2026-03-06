package dabbiks.uhc.menu.cosmetics;

import dabbiks.uhc.cosmetics.Cage;
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

public class CageMenu extends FastInv {

    private final Player player;
    private final PersistentData persistentData;
    private final Cage[] cages;

    public CageMenu(Player player, PersistentData persistentData) {
        super(54, "Klatki startowe");
        this.player = player;
        this.persistentData = persistentData;
        this.cages = Cage.values();

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
        for (Cage cage : cages) {
            if (index >= slots.length) break;

            int slot = slots[index++];
            setItem(slot, createIcon(cage), e -> {
                boolean unlocked = persistentData.hasCage(cage);
                if (unlocked) {
                    handleSelect(cage);
                } else {
                    if (e.getClick().isLeftClick()) {
                        handleBuy(cage, true);
                    } else if (e.getClick().isRightClick()) {
                        handleBuy(cage, false);
                    }
                }
            });
        }
    }

    private ItemStack createIcon(Cage cage) {
        ItemStack item = new ItemStack(cage.getMaterial());
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(cage.getTier().getIcon() + "§f" + cage.getName());
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);

            List<String> lore = new ArrayList<>();

            boolean unlocked = persistentData.hasCage(cage);
            boolean selected = persistentData.getCage() == cage;

            if (selected) {
                lore.add(symbolU.MOUSE_LEFT + "§a Wybrana klatka");
            } else if (unlocked) {
                lore.add(symbolU.MOUSE_LEFT + "§e Wybierz klatkę");
            } else {
                int coins = persistentData.getStats().getOrDefault(PersistentStats.COINS, 0);
                int powder = persistentData.getStats().getOrDefault(PersistentStats.POWDER, 0);

                boolean hasCoins = coins >= cage.getCoinsCost();
                boolean hasPowder = powder >= cage.getPowderCost();

                lore.add((hasCoins ? symbolU.MOUSE_LEFT + "§7 Kup za §a" + cage.getCoinsCost() + "§f" + symbolU.SCOREBOARD_COIN
                        : symbolU.MOUSE_LEFT + "§7 Brakuje §c" + (cage.getCoinsCost() - coins) + "§f" + symbolU.SCOREBOARD_COIN));
                lore.add((hasPowder ? symbolU.MOUSE_RIGHT + "§7 Kup za §a" + cage.getPowderCost() + "§f" + symbolU.SCOREBOARD_POWDER
                        : symbolU.MOUSE_RIGHT + "§7 Brakuje §c" + (cage.getPowderCost() - powder) + "§f" + symbolU.SCOREBOARD_POWDER));
            }

            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setInteger(ItemTags.UHC_ITEM.name(), 1);

        return nbtItem.getItem();
    }

    private void handleBuy(Cage cage, boolean useCoins) {
        int cost = useCoins ? cage.getCoinsCost() : cage.getPowderCost();
        PersistentStats currency = useCoins ? PersistentStats.COINS : PersistentStats.POWDER;
        int playerCurrency = persistentData.getStats().getOrDefault(currency, 0);

        if (playerCurrency < cost) {
            soundU.playSoundToPlayer(player, Sound.ENTITY_VILLAGER_NO, 1, 1);
            return;
        }

        persistentData.removeStats(currency, cost);
        persistentData.unlockCage(cage);
        PersistentDataManager.saveData(player.getUniqueId());
        PurchaseMessage.send(player, "§7klatka startowa §c" + cage.getName().toUpperCase(), cost, useCoins);
        soundU.playSoundToPlayers(playerListU.getAllPlayers(), Sound.BLOCK_NOTE_BLOCK_BIT, 0.3f, 2);
        player.sendMessage("§aZakupiono klatkę " + cage.getName() + "!");

        render();
    }

    private void handleSelect(Cage cage) {
        player.sendMessage("§aWybrano klatkę: " + cage.getName());
        persistentData.setCage(cage);
        soundU.playSoundToPlayer(player, Sound.UI_BUTTON_CLICK, 1, 1);
        render();
    }
}