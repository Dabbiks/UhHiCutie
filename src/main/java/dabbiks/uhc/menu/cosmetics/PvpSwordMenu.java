package dabbiks.uhc.menu.cosmetics;

import dabbiks.uhc.cosmetics.CosmeticTier;
import dabbiks.uhc.cosmetics.PvpSword;
import dabbiks.uhc.game.gameplay.items.ItemBuilder;
import dabbiks.uhc.game.gameplay.items.ItemInstance;
import dabbiks.uhc.game.gameplay.items.ItemTags;
import dabbiks.uhc.game.gameplay.items.data.attributes.AttributeData;
import dabbiks.uhc.game.gameplay.items.data.attributes.AttributeType;
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

public class PvpSwordMenu extends FastInv {

    private final Player player;
    private final PersistentData persistentData;
    private final PvpSword[] pvpSwords;
    private final double priceMultiplier = Discount.getDiscounts().getOrDefault(DiscountType.PVP_SWORD, 1.0);

    public PvpSwordMenu(Player player, PersistentData persistentData) {
        super(45, "Miecze PvP");
        this.player = player;
        this.persistentData = persistentData;
        this.pvpSwords = PvpSword.values();

        if (persistentData.getDonations() >= 20 && !persistentData.hasPvpSword(PvpSword.PRESTIGE_SWORD)) persistentData.unlockPvpSword(PvpSword.PRESTIGE_SWORD);
        if (persistentData.getDonations() >= 50 && !persistentData.hasPvpSword(PvpSword.PRESTIGE_FISHING_ROD)) persistentData.unlockPvpSword(PvpSword.PRESTIGE_FISHING_ROD);
        if (persistentData.getDonations() >= 100 && !persistentData.hasPvpSword(PvpSword.PRESTIGE_MACE)) persistentData.unlockPvpSword(PvpSword.PRESTIGE_MACE);

        render();
    }

    private void render() {
        int[] slots = {
                1, 2, 3, 4, 5, 6, 7,
                10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                28, 29, 30, 31, 32, 33, 34,
                39, 40, 41
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
            if (pvpSword.getCustomModelData() != 0) meta.setCustomModelData(pvpSword.getCustomModelData());

            List<String> lore = new ArrayList<>();

            boolean unlocked = persistentData.hasPvpSword(pvpSword);
            boolean selected = persistentData.getPvpSword() == pvpSword;

            if (selected) {
                lore.add(symbolU.MOUSE_LEFT + "§a Wybrany miecz");
            } else if (unlocked) {
                lore.add(symbolU.MOUSE_LEFT + "§e Wybierz miecz");
            } else if (pvpSword.getTier() == CosmeticTier.PRESTIGE) {
                if (pvpSword == PvpSword.PRESTIGE_SWORD) {
                    lore.add("§7Do odblokowania brakuje Ci ");
                    lore.add("§7jeszcze §a" + (20 - persistentData.getDonations()) + "zł §7wpłaty");
                } else if (pvpSword == PvpSword.PRESTIGE_FISHING_ROD) {
                    lore.add("§7Do odblokowania brakuje Ci ");
                    lore.add("§7jeszcze §a" + (50 - persistentData.getDonations()) + "zł §7wpłaty");
                } else if (pvpSword == PvpSword.PRESTIGE_MACE) {
                    lore.add("§7Do odblokowania brakuje Ci ");
                    lore.add("§7jeszcze §a" + (100 - persistentData.getDonations()) + "zł §7wpłaty");
                }
            } else {
                int coins = persistentData.getStats().getOrDefault(PersistentStats.COINS, 0);
                int powder = persistentData.getStats().getOrDefault(PersistentStats.POWDER, 0);

                int originalCoins = pvpSword.getCoinsCost();
                int discountedCoins = (int) (originalCoins * priceMultiplier);

                int originalPowder = pvpSword.getPowderCost();
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

    private void handleBuy(PvpSword pvpSword, boolean useCoins) {
        int cost = useCoins ? (int) (pvpSword.getCoinsCost() * priceMultiplier) : (int) (pvpSword.getPowderCost() * priceMultiplier);
        PersistentStats currency = useCoins ? PersistentStats.COINS : PersistentStats.POWDER;
        int playerCurrency = persistentData.getStats().getOrDefault(currency, 0);

        if (playerCurrency < cost) {
            soundU.playSoundToPlayer(player, Sound.ENTITY_VILLAGER_NO, 1, 1);
            return;
        }

        if (pvpSword.getTier() == CosmeticTier.PRESTIGE) {
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

        List<AttributeData> attrs = new ArrayList<>();
        attrs.add(new AttributeData(AttributeType.ATTACK_DAMAGE, 3));
        attrs.add(new AttributeData(AttributeType.ATTACK_SPEED, -2));
        attrs.add(new AttributeData(AttributeType.CRIT_DAMAGE_PERCENT, 25));

        ItemInstance itemInstance = new ItemInstance();
        itemInstance.setName(persistentData.getPvpSword().getName());
        itemInstance.setMaterial(persistentData.getPvpSword().getMaterial().name());
        itemInstance.setAttributes(attrs);
        if (pvpSword.getCustomModelData() != 0) itemInstance.setCustomModelData(pvpSword.getCustomModelData());

        player.getInventory().setItem(4, new ItemBuilder(itemInstance).build());

        render();
    }
}