package dabbiks.uhc.menu.cosmetics;

import dabbiks.uhc.cosmetics.Mount;
import dabbiks.uhc.cosmetics.MountManager;
import dabbiks.uhc.game.gameplay.items.ItemTags;
import dabbiks.uhc.menu.Discount;
import dabbiks.uhc.menu.DiscountType;
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
import java.util.List;

import static dabbiks.uhc.Main.*;

public class MountMenu extends FastInv {

    private final Player player;
    private final PersistentData persistentData;
    private final Mount[] mounts;
    private final double priceMultiplier = Discount.getDiscounts().getOrDefault(DiscountType.MOUNT, 1.0);

    public MountMenu(Player player, PersistentData persistentData) {
        super(36, "Mounty");
        this.player = player;
        this.persistentData = persistentData;
        this.mounts = Mount.values();

        render();
    }

    private void render() {
        int[] slots = {
                10, 11, 12, 13, 14, 15, 16,
                20, 21, 22, 23, 24, 25
        };

        int index = 0;
        for (Mount mount : mounts) {
            if (index >= slots.length) break;

            int slot = slots[index++];
            setItem(slot, createIcon(mount), e -> {
                boolean unlocked = persistentData.hasMount(mount);
                if (unlocked) {
                    handleSelect(mount);
                } else {
                    if (e.getClick().isLeftClick() && !e.getClick().isShiftClick()) {
                        handleBuy(mount, true);
                    } else if (e.getClick().isRightClick()) {
                        handleBuy(mount, false);
                    }
                }
            });
        }
    }

    private ItemStack createIcon(Mount mount) {
        ItemStack item = new ItemStack(mount.getMaterial());
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(mount.getTier().getIcon() + "§f" + mount.getName());
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);

            List<String> lore = new ArrayList<>();

            boolean unlocked = persistentData.hasMount(mount);
            boolean selected = MountManager.getInstance().isPlayerMount(player, mount);

            if (selected) {
                lore.add(symbolU.MOUSE_LEFT + "§a Wybrany mount");
                lore.add(symbolU.MOUSE_LEFT + "§7 Kliknij, aby schować");
            } else if (unlocked) {
                lore.add(symbolU.MOUSE_LEFT + "§e Wybierz mount");
            } else {
                int coins = persistentData.getStats().getOrDefault(PersistentStats.COINS, 0);
                int powder = persistentData.getStats().getOrDefault(PersistentStats.POWDER, 0);

                int originalCoins = mount.getCoinsCost();
                int discountedCoins = (int) (originalCoins * priceMultiplier);

                int originalPowder = mount.getPowderCost();
                int discountedPowder = (int) (originalPowder * priceMultiplier);

                boolean hasCoins = coins >= discountedCoins;
                boolean hasPowder = powder >= discountedPowder;

                if (hasCoins) {
                    if (priceMultiplier != 1.0) {
                        lore.add(symbolU.MOUSE_LEFT + "§7 Kup za §a§m" + originalCoins + "§r §4" + discountedCoins + "§f" + symbolU.SCOREBOARD_COIN);
                    } else {
                        lore.add(symbolU.MOUSE_LEFT + "§7 Kup za §a" + originalCoins + "§f" + symbolU.SCOREBOARD_COIN);
                    }
                } else {
                    lore.add(symbolU.MOUSE_LEFT + "§7 Brakuje §c" + (discountedCoins - coins) + "§f" + symbolU.SCOREBOARD_COIN);
                }

                if (hasPowder) {
                    if (priceMultiplier != 1.0) {
                        lore.add(symbolU.MOUSE_RIGHT + "§7 Kup za §a§m" + originalPowder + "§r §4" + discountedPowder + "§f" + symbolU.SCOREBOARD_POWDER);
                    } else {
                        lore.add(symbolU.MOUSE_RIGHT + "§7 Kup za §a" + originalPowder + "§f" + symbolU.SCOREBOARD_POWDER);
                    }
                } else {
                    lore.add(symbolU.MOUSE_RIGHT + "§7 Brakuje §c" + (discountedPowder - powder) + "§f" + symbolU.SCOREBOARD_POWDER);
                }
            }

            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setInteger(ItemTags.UHC_ITEM.name(), 1);

        return nbtItem.getItem();
    }

    private ItemStack createBackIcon(String name, Material material) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        return item;
    }

    private void handleBuy(Mount mount, boolean useCoins) {
        int cost = useCoins ? (int) (mount.getCoinsCost() * priceMultiplier) : (int) (mount.getPowderCost() * priceMultiplier);
        PersistentStats currency = useCoins ? PersistentStats.COINS : PersistentStats.POWDER;
        int playerCurrency = persistentData.getStats().getOrDefault(currency, 0);

        if (playerCurrency < cost) {
            soundU.playSoundToPlayer(player, Sound.ENTITY_VILLAGER_NO, 1, 1);
            return;
        }

        persistentData.removeStats(currency, cost);
        persistentData.unlockMount(mount);
        PersistentDataManager.saveData(player.getUniqueId());
        PurchaseMessage.send(player, "§7mount §c" + mount.getName().toUpperCase(), cost, useCoins);
        soundU.playSoundToPlayers(playerListU.getAllPlayers(), Sound.BLOCK_NOTE_BLOCK_BIT, 0.3f, 2);
        player.sendMessage("§aZakupiono mount " + mount.getName() + "!");

        render();
    }

    private void handleSelect(Mount mount) {
        if (MountManager.getInstance().isPlayerMount(player, mount)) {
            persistentData.setMount(null);
            PersistentDataManager.saveData(player.getUniqueId());
            MountManager.getInstance().removeMount(player);
            player.sendMessage("§cSchowano mount: " + mount.getName());
        } else {
            persistentData.setMount(mount);
            PersistentDataManager.saveData(player.getUniqueId());
            MountManager.getInstance().spawnMount(player, mount, persistentData);
            player.sendMessage("§aWybrano mount: " + mount.getName());
        }
        soundU.playSoundToPlayer(player, Sound.UI_BUTTON_CLICK, 1, 1);
        render();
    }
}