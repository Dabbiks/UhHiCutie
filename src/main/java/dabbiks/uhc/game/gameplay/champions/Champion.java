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
            if (playerCoins >= getCost()) {
                desc.add(symbolU.MOUSE_RIGHT + " §aKliknij, aby zakupić klasę!");
                if (championPriceMultiplier != 1) {
                    desc.add(" §8• §7Koszt: §6§m" + getCost() + "§r §c" + (int) (getCost() * championPriceMultiplier) + " monet");
                } else {
                    desc.add(" §8• §7Koszt: §6" + getCost() + " monet");
                }
            } else {
                desc.add(symbolU.MOUSE_RIGHT + " §7Potrzebujesz jeszcze §c" + (getCost() - playerCoins) + " monet");
                desc.add("§7lub §c" + (10 - persistentData.getChampionShards(getId())) + " odłamków §7żeby odblokować tę klasę!");
            }
            return desc;
        }

        if (persistentData.getChampionLevel(getId()) < getMaxLevel()) {
            int upgradeCost = getUpgradeCost(getCost(), level);
            if (playerCoins >= upgradeCost) {
                desc.add(symbolU.MOUSE_RIGHT + " §eUlepsz na poziom " + (level + 1));
                if (upgradePriceMultiplier != 1) {
                    desc.add(" §8• §7Koszt: §6§m" + upgradeCost + "§r §c" + (int) (upgradeCost * upgradePriceMultiplier) + " monet");
                } else {
                    desc.add(" §8• §7Koszt: §6" + upgradeCost + " monet");
                }
            } else {
                desc.add(symbolU.MOUSE_RIGHT + " §7Potrzebujesz jeszcze §c" + (upgradeCost - playerCoins) + " monet");
                desc.add(" §7żeby odblokować kolejny poziom tej klasy!");
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

    public int getUpgradeCost(int cost, int level) {
        if (level + 1 == 5 || level + 1 == 10) {
            return cost;
        }
        return cost / 5;
    }
}