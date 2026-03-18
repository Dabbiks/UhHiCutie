package dabbiks.uhc.game.gameplay.champions;

import dabbiks.uhc.menu.Discount;
import dabbiks.uhc.menu.DiscountType;
import dabbiks.uhc.player.data.persistent.PersistentData;
import dabbiks.uhc.player.data.persistent.PersistentStats;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;

import static dabbiks.uhc.Main.symbolU;

public abstract class Champion {

    public abstract String getId();
    public abstract String getName();
    public abstract int getCost();
    public abstract Material getIcon();
    public abstract int getMaxLevel();

    public abstract void onStart(Player player, int level);

    protected abstract List<String> getClassDescription();
    protected abstract List<String> getLevelDescription(int level);

    protected List<String> getClickDescription(PersistentData persistentData, int level) {
        List<String> desc = new ArrayList<>();
        int playerCoins = persistentData.getStats().getOrDefault(PersistentStats.COINS, 0);

        final double championPriceMultiplier = Discount.getDiscounts().getOrDefault(DiscountType.CHAMPION, 1.0);
        final double upgradePriceMultiplier = Discount.getDiscounts().getOrDefault(DiscountType.CHAMPION_UPGRADE, 1.0);

        if (!persistentData.hasUnlockedChampion(getId())) {
            int originalCost = getCost();
            int discountedCost = (int) (originalCost * championPriceMultiplier);

            if (playerCoins >= discountedCost) {
                desc.add(symbolU.MOUSE_RIGHT + " §aKliknij, aby zakupić klasę!");
            } else {
                desc.add(symbolU.MOUSE_RIGHT + " §7Potrzebujesz jeszcze §c" + (discountedCost - playerCoins) + " monet");
                desc.add("§7lub §c" + (10 - persistentData.getChampionShards(getId())) + " odłamków §7żeby odblokować tę klasę!");
            }

            if (championPriceMultiplier != 1.0) {
                desc.add(" §8• §7Koszt: §6§m" + originalCost + "§r §4" + discountedCost + " monet");
            } else {
                desc.add(" §8• §7Koszt: §6" + originalCost + " monet");
            }

        } else if (persistentData.getChampionLevel(getId()) < getMaxLevel()) {
            int originalUpgradeCost = getUpgradeCost(getCost(), level);
            int discountedUpgradeCost = (int) (originalUpgradeCost * upgradePriceMultiplier);

            if (playerCoins >= discountedUpgradeCost) {
                desc.add(symbolU.MOUSE_RIGHT + " §eUlepsz na poziom " + (level + 1));
            } else {
                desc.add(symbolU.MOUSE_RIGHT + " §7Potrzebujesz jeszcze §c" + (discountedUpgradeCost - playerCoins) + " monet");
                desc.add(" §7żeby odblokować kolejny poziom tej klasy!");
            }

            if (upgradePriceMultiplier != 1.0) {
                desc.add(" §8• §7Koszt: §6§m" + originalUpgradeCost + "§r §4" + discountedUpgradeCost + " monet");
            } else {
                desc.add(" §8• §7Koszt: §6" + originalUpgradeCost + " monet");
            }

        } else {
            desc.add("§6§lMAESTRIA");
            desc.add(" §8• §7Punkty: §e" + persistentData.getChampionMastery(getId()));
            desc.add("");
            desc.add("§8" + symbolU.MOUSE_RIGHT + " §7Osiągnięto maksymalny poziom.");
        }

        desc.add("");
        if (!persistentData.getChampion().equals(getId())) {
            desc.add(symbolU.MOUSE_LEFT + " §eKliknij, aby wybrać tę klasę");
        } else {
            desc.add(symbolU.MOUSE_LEFT + " §aKlasa jest aktualnie wybrana");
        }
        return desc;
    }

    public List<String> getLore(PersistentData persistentData, int currentLevel) {
        List<String> lore = new ArrayList<>(getClassDescription());
        lore.add("");
        lore.addAll(getLevelDescription(currentLevel));
        lore.add("");
        lore.addAll(getClickDescription(persistentData, currentLevel));
        return lore;
    }

    public List<String> getPreviewLore() {
        List<String> lore = new ArrayList<>(getClassDescription());
        lore.add("");
        lore.addAll(getLevelDescription(getMaxLevel()));
        return lore;
    }

    public int getUpgradeCost(int cost, int level) {
        if (level + 1 == 5 || level + 1 == 10) {
            return cost;
        }
        return cost / 5;
    }
}