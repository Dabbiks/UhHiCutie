package dabbiks.uhc.menu;

import dabbiks.uhc.game.gameplay.champions.Champion;
import dabbiks.uhc.game.gameplay.champions.ChampionManager;
import dabbiks.uhc.game.gameplay.items.ItemTags;
import dabbiks.uhc.player.data.persistent.PersistentData;
import dabbiks.uhc.player.data.persistent.PersistentStats;
import de.tr7zw.nbtapi.NBTItem;
import fr.mrmicky.fastinv.FastInv;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static dabbiks.uhc.Main.soundU;

public class ChampionMenu extends FastInv {

    private final Player player;
    private final PersistentData data;
    private final List<Champion> champions;

    public ChampionMenu(Player player, PersistentData data) {
        super(27, "Klasy postaci");
        this.player = player;
        this.data = data;
        this.champions = new ChampionManager().getChampions();

        render();
    }

    private void render() {

        int[] slots = {
                10, 12, 14, 16
        };

        int index = 0;
        for (Champion champion : champions) {
            if (index >= slots.length) break;

            int slot = slots[index++];
            setItem(slot, createIcon(champion), e -> {
                if (e.getClick().isRightClick()) {
                    handleUpgradeOrBuy(champion);
                } else if (e.getClick().isLeftClick()) {
                    handleSelect(champion);
                }
            });
        }
    }

    private ItemStack createIcon(Champion champion) {
        int level = data.getChampionLevel(champion.getId());
        int displayLevel = (level == 0) ? 1 : level;

        ItemStack item = new ItemStack(champion.getIcon());
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§c§l" + champion.getName());
            meta.setLore(champion.getLore(data, displayLevel));
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }

        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setInteger(ItemTags.UHC_ITEM.name(), 1);

        return nbtItem.getItem();
    }

    private void handleUpgradeOrBuy(Champion champion) {
        boolean unlocked = data.hasUnlockedChampion(champion.getId());
        int currentLevel = data.getChampionLevel(champion.getId());
        int cost = unlocked ? champion.getUpgradeCost(champion.getCost(), currentLevel) : champion.getCost();
        int playerCoins = data.getStats().getOrDefault(PersistentStats.COINS, 0);

        if (unlocked && currentLevel >= champion.getMaxLevel()) {
            soundU.playSoundToPlayer(player, Sound.ENTITY_VILLAGER_NO, 1, 1);
            return;
        }

        if (playerCoins < cost) {
            soundU.playSoundToPlayer(player, Sound.ENTITY_VILLAGER_NO, 1, 1);
            return;
        }

        if (!unlocked) {
            data.removeStats(PersistentStats.COINS, cost);
            data.addUnlockedChampion(champion.getId());
            player.sendMessage("§aZakupiono klasę " + champion.getName() + "!");
        } else {
            data.setChampionLevel(champion.getId(), currentLevel + 1);
            player.sendMessage("§aUlepszono klasę " + champion.getName() + "!");
        }

        soundU.playSoundToPlayer(player, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);

        render();
    }

    private void handleSelect(Champion champion) {
        if (!data.hasUnlockedChampion(champion.getId())) {
            soundU.playSoundToPlayer(player, Sound.ENTITY_VILLAGER_NO, 1, 1);
            return;
        }

        data.setChampion(champion.getId());
        player.sendMessage("§aWybrano klasę: " + champion.getName());
        soundU.playSoundToPlayer(player, Sound.UI_BUTTON_CLICK, 1, 1);

        render();
    }
}