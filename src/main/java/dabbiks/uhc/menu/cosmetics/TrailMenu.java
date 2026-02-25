package dabbiks.uhc.menu.cosmetics;

import dabbiks.uhc.cosmetics.ParticleTrail;
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

public class TrailMenu extends FastInv {

    private final Player player;
    private final PersistentData persistentData;

    public TrailMenu(Player player, PersistentData persistentData) {
        super(54, "Smugi lotu");
        this.player = player;
        this.persistentData = persistentData;

        render();
    }

    private void render() {
        int[] slots = {
                4,
                10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                28, 29, 30, 31, 32, 33, 34,
                37, 38, 39, 40, 41, 42, 43
        };

        int index = 0;
        for (ParticleTrail trail : ParticleTrail.values()) {
            if (index >= slots.length) break;

            int slot = slots[index++];
            setItem(slot, createIcon(trail), e -> {
                boolean unlocked = persistentData.hasTrail(trail);
                if (unlocked) {
                    handleSelect(trail);
                } else {
                    if (e.getClick().isLeftClick()) {
                        handleBuy(trail, true);
                    } else if (e.getClick().isRightClick()) {
                        handleBuy(trail, false);
                    }
                }
            });
        }
    }

    private ItemStack createIcon(ParticleTrail trail) {
        ItemStack item = new ItemStack(trail.getMaterial());
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(trail.getTier().getIcon() + "§f" + trail.getDisplayName());
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

            List<String> lore = new ArrayList<>();

            boolean unlocked = persistentData.hasTrail(trail);
            boolean selected = trail.name().equals(persistentData.getTrail());

            if (selected) {
                lore.add(symbolU.MOUSE_LEFT + "§a Wybrana smuga");
            } else if (unlocked) {
                lore.add(symbolU.MOUSE_LEFT + "§e Wybierz smugę");
            } else {
                int coins = persistentData.getStats().getOrDefault(PersistentStats.COINS, 0);
                int powder = persistentData.getStats().getOrDefault(PersistentStats.POWDER, 0);

                boolean hasCoins = coins >= trail.getCoinsCost();
                boolean hasPowder = powder >= trail.getPowderCost();

                lore.add((hasCoins ? symbolU.MOUSE_LEFT + "§7 Kup za §a" + trail.getCoinsCost() + "§f" + symbolU.SCOREBOARD_COIN
                        : symbolU.MOUSE_LEFT + "§7 Brakuje §c" + (trail.getCoinsCost() - coins) + "§f" + symbolU.SCOREBOARD_COIN));
                lore.add((hasPowder ? symbolU.MOUSE_RIGHT + "§7 Kup za §a" + trail.getPowderCost() + "§f" + symbolU.SCOREBOARD_POWDER
                        : symbolU.MOUSE_RIGHT + "§7 Brakuje §c" + (trail.getPowderCost() - powder) + "§f" + symbolU.SCOREBOARD_POWDER));
            }

            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setInteger(ItemTags.UHC_ITEM.name(), 1);

        return nbtItem.getItem();
    }

    private void handleBuy(ParticleTrail trail, boolean useCoins) {
        int cost = useCoins ? trail.getCoinsCost() : trail.getPowderCost();
        PersistentStats currency = useCoins ? PersistentStats.COINS : PersistentStats.POWDER;
        int playerCurrency = persistentData.getStats().getOrDefault(currency, 0);

        if (playerCurrency < cost) {
            soundU.playSoundToPlayer(player, Sound.ENTITY_VILLAGER_NO, 1, 1);
            return;
        }

        persistentData.removeStats(currency, cost);
        persistentData.unlockTrail(trail);
        PersistentDataManager.saveData(player.getUniqueId());
        PurchaseMessage.send(player, "§7smugę §c" + trail.getDisplayName().toUpperCase(), cost, useCoins);
        soundU.playSoundToPlayers(playerListU.getAllPlayers(), Sound.BLOCK_NOTE_BLOCK_BIT, 0.3f, 2);
        player.sendMessage("§aZakupiono smugę " + trail.getDisplayName() + "!");

        render();
    }

    private void handleSelect(ParticleTrail trail) {
        persistentData.setTrail(trail);
        player.sendMessage("§aWybrano smugę: " + trail.getDisplayName());
        soundU.playSoundToPlayer(player, Sound.UI_BUTTON_CLICK, 1, 1);
        render();
    }
}