package dabbiks.uhc.menu.cosmetics;

import dabbiks.uhc.cosmetics.KillSound;
import dabbiks.uhc.game.gameplay.items.ItemTags;
import dabbiks.uhc.menu.Discount;
import dabbiks.uhc.menu.DiscountType;
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

public class KillSoundMenu extends FastInv {

    private final Player player;
    private final PersistentData persistentData;
    private final KillSound[] killSounds;
    private final double priceMultiplier = Discount.getDiscounts().getOrDefault(DiscountType.KILL_SOUND, 1.0);

    public KillSoundMenu(Player player, PersistentData persistentData) {
        super(36, "Dźwięki zabójstwa");
        this.player = player;
        this.persistentData = persistentData;
        this.killSounds = KillSound.values();

        render();
    }

    private void render() {
        int[] slots = {
                10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                28, 29, 30, 31, 32, 33, 34
        };

        int index = 0;
        for (KillSound killSound : killSounds) {
            if (index >= slots.length) break;

            int slot = slots[index++];
            setItem(slot, createIcon(killSound), e -> {
                boolean unlocked = persistentData.hasKillSound(killSound);
                if (unlocked) {
                    handleSelect(killSound);
                } else {
                    if (e.getClick().isLeftClick()) {
                        handleBuy(killSound, true);
                    } else if (e.getClick().isRightClick()) {
                        handleBuy(killSound, false);
                    }
                }
            });
        }
    }

    private ItemStack createIcon(KillSound killSound) {
        ItemStack item = new ItemStack(killSound.getMaterial());
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(killSound.getTier().getIcon() + "§f" + killSound.getName());
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);

            List<String> lore = new ArrayList<>();

            boolean unlocked = persistentData.hasKillSound(killSound);
            boolean selected = persistentData.getKillSound() == killSound;

            if (selected) {
                lore.add(symbolU.MOUSE_LEFT + "§a Wybrany dźwięk");
            } else if (unlocked) {
                lore.add(symbolU.MOUSE_LEFT + "§e Wybierz dźwięk");
            } else {
                int coins = persistentData.getStats().getOrDefault(PersistentStats.COINS, 0);
                int powder = persistentData.getStats().getOrDefault(PersistentStats.POWDER, 0);

                int coinsCost = (int) (killSound.getCoinsCost() * priceMultiplier);
                int powderCost = (int) (killSound.getPowderCost() * priceMultiplier);

                boolean hasCoins = coins >= coinsCost;
                boolean hasPowder = powder >= powderCost;

                lore.add((hasCoins ? symbolU.MOUSE_LEFT + "§7 Kup za §a" + coinsCost + "§f" + symbolU.SCOREBOARD_COIN
                        : symbolU.MOUSE_LEFT + "§7 Brakuje §c" + (coinsCost - coins) + "§f" + symbolU.SCOREBOARD_COIN));
                lore.add((hasPowder ? symbolU.MOUSE_RIGHT + "§7 Kup za §a" + powderCost + "§f" + symbolU.SCOREBOARD_POWDER
                        : symbolU.MOUSE_RIGHT + "§7 Brakuje §c" + (powderCost - powder) + "§f" + symbolU.SCOREBOARD_POWDER));
            }

            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setInteger(ItemTags.UHC_ITEM.name(), 1);

        return nbtItem.getItem();
    }

    private void handleBuy(KillSound killSound, boolean useCoins) {
        int cost = useCoins ? (int) (killSound.getCoinsCost() * priceMultiplier) : (int) (killSound.getPowderCost() * priceMultiplier);
        PersistentStats currency = useCoins ? PersistentStats.COINS : PersistentStats.POWDER;
        int playerCurrency = persistentData.getStats().getOrDefault(currency, 0);

        if (playerCurrency < cost) {
            soundU.playSoundToPlayer(player, Sound.ENTITY_VILLAGER_NO, 1, 1);
            return;
        }

        persistentData.removeStats(currency, cost);
        persistentData.unlockKillSound(killSound);
        PersistentDataManager.saveData(player.getUniqueId());
        PurchaseMessage.send(player, "§7dźwięk zabójstwa §c" + killSound.getName().toUpperCase(), cost, useCoins);
        soundU.playSoundToPlayers(playerListU.getAllPlayers(), Sound.BLOCK_NOTE_BLOCK_BIT, 0.3f, 2);
        player.sendMessage("§aZakupiono dźwięk " + killSound.getName() + "!");

        render();
    }

    private void handleSelect(KillSound killSound) {
        player.sendMessage("§aWybrano dźwięk: " + killSound.getName());
        persistentData.setKillSound(killSound);
        soundU.playSoundToPlayer(player, Sound.UI_BUTTON_CLICK, 1, 1);
        render();
    }
}