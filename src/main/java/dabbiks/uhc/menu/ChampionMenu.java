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
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

import static dabbiks.uhc.Main.soundU;
import static dabbiks.uhc.Main.symbolU;

public class ChampionMenu extends FastInv {

    private final Player player;
    private final PersistentData data;
    private final List<Champion> champions;
    private final double championPriceMultiplier = Discount.getDiscounts().getOrDefault(DiscountType.CHAMPION, 1.0);
    private final double upgradePriceMultiplier = Discount.getDiscounts().getOrDefault(DiscountType.CHAMPION_UPGRADE, 1.0);

    private boolean previewMode = false;

    public ChampionMenu(Player player, PersistentData data) {
        super(54, "\uF808\uF805" + symbolU.CHAMPION_MENU);
        this.player = player;
        this.data = data;
        this.champions = new ChampionManager().getChampions();

        render();
    }

    private void render() {
        int[] slots = {10, 12, 14, 16, 20, 24, 28, 34};

        int index = 0;
        for (Champion champion : champions) {
            if (index >= slots.length) break;

            int slot = slots[index++];
            setItem(slot, createIcon(champion), e -> {
                if (previewMode) return;

                if (e.getClick().isRightClick()) {
                    handleUpgradeOrBuy(champion);
                } else if (e.getClick().isLeftClick()) {
                    handleSelect(champion);
                }
            });
        }

        ItemStack toggleBtn = new ItemStack(previewMode ? Material.ENDER_PEARL : Material.ENDER_EYE);
        ItemMeta toggleMeta = toggleBtn.getItemMeta();
        if (toggleMeta != null) {
            toggleMeta.setDisplayName(previewMode ? "§cPokaż swoje klasy" : "§ePodejrzyj wymaksowane klasy");
            toggleBtn.setItemMeta(toggleMeta);
        }

        setItem(49, toggleBtn, e -> {
            previewMode = !previewMode;
            render();
        });
    }

    private ItemStack createIcon(Champion champion) {
        int level = data.getChampionLevel(champion.getId());
        int displayLevel = (level == 0) ? 1 : level;

        ItemStack item = new ItemStack(champion.getIcon());
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            if (champion.getIconCustomModelData() > 0) {
                meta.setCustomModelData(champion.getIconCustomModelData());
            }

            if (previewMode) {
                meta.setDisplayName("§c§l" + champion.getName() + " §8(Poziom " + getRomanNumeral(champion.getMaxLevel()) + ")");
                meta.setLore(champion.getPreviewLore());
            } else {
                meta.setDisplayName("§c§l" + champion.getName() + " §8(Poziom " + getRomanNumeral(displayLevel) + ")");
                meta.setLore(champion.getLore(data, displayLevel));
            }

            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }

        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setInteger(ItemTags.UHC_ITEM.name(), 1);

        return nbtItem.getItem();
    }

    private String getRomanNumeral(int level) {
        String[] romans = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
        if (level >= 1 && level <= 10) return romans[level - 1];
        return String.valueOf(level);
    }

    private void handleUpgradeOrBuy(Champion champion) {
        boolean unlocked = data.hasUnlockedChampion(champion.getId());
        int currentLevel = data.getChampionLevel(champion.getId());
        int cost = unlocked ? (int) (champion.getUpgradeCost(champion.getCost(), currentLevel) * upgradePriceMultiplier)
                : (int) (champion.getCost() * championPriceMultiplier);
        int playerCoins = data.getStats().getOrDefault(PersistentStats.COINS, 0);

        if (unlocked && currentLevel >= champion.getMaxLevel()) {
            soundU.playSoundToPlayer(player, Sound.ENTITY_VILLAGER_NO, 1, 1);
            return;
        }

        if (playerCoins < cost) {
            soundU.playSoundToPlayer(player, Sound.ENTITY_VILLAGER_NO, 1, 1);
            return;
        }

        data.removeStats(PersistentStats.COINS, cost);

        if (!unlocked) {
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
        data.setChampion(champion.getId());
        player.sendMessage("§aWybrano klasę: " + champion.getName());
        soundU.playSoundToPlayer(player, Sound.UI_BUTTON_CLICK, 1, 1);

        render();
    }
}