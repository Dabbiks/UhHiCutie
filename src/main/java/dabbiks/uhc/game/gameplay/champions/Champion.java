package dabbiks.uhc.game.gameplay.champions;

import dabbiks.uhc.player.data.persistent.PersistentData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;

public abstract class Champion {

    public abstract String getId();
    public abstract String getName();
    public abstract int getCost();
    public abstract Material getIcon();
    public abstract int getMaxLevel();

    public abstract void onStart(Player player, int level);

    protected abstract List<String> getClassDescription();
    protected abstract List<String> getLevelDescription(int level);
    protected abstract List<String> getClickDescription(PersistentData persistentData, int level);

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