package dabbiks.uhc.cosmetics.chest.rewards.loot;

import dabbiks.uhc.game.gameplay.champions.Champion;
import dabbiks.uhc.game.gameplay.champions.ChampionManager;
import dabbiks.uhc.cosmetics.chest.rewards.Reward;
import dabbiks.uhc.player.data.persistent.PersistentData;
import dabbiks.uhc.player.data.persistent.PersistentStats;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static dabbiks.uhc.Main.*;

public class ChampionUpgradeReward extends Reward {

    private final Champion champion;
    private boolean isFallback = false;

    public ChampionUpgradeReward() {
        List<Champion> allChampions = new ChampionManager().getChampions();
        this.champion = allChampions.get(ThreadLocalRandom.current().nextInt(allChampions.size()));
    }

    @Override
    public String getType() {
        return "§6§lKLASA";
    }

    @Override
    public void addReward(PersistentData persistentData) {
        String id = champion.getId();
        boolean hasClass = persistentData.hasUnlockedChampion(id);
        int currentLevel = persistentData.getChampionLevel(id);

        if (hasClass && currentLevel < champion.getMaxLevel()) {
            persistentData.setChampionLevel(id, currentLevel + 1);
        } else {
            isFallback = true;
            if (!hasClass) {
                persistentData.addStats(PersistentStats.COINS, champion.getCost() / 8);

                Player player = Bukkit.getPlayer(persistentData.getName());
                if (player == null) return;
                player.sendMessage("§7Otrzymujesz §f" + champion.getCost() / 8 + symbolU.SCOREBOARD_COIN + "§7 za §eUlepszenie klasy");
            } else {
                persistentData.addStats(PersistentStats.POWDER, champion.getCost() / 20);

                Player player = Bukkit.getPlayer(persistentData.getName());
                if (player == null) return;
                player.sendMessage("§7Otrzymujesz §f" + champion.getCost() / 20 + symbolU.SCOREBOARD_POWDER + "§7 za §eUlepszenie klasy");
            }
        }
    }

    @Override
    public String getName() {
        return "§fUlepszenie " + champion.getName() + " ";
    }

    @Override
    public ItemStack getItem() {
        ItemStack item = new ItemStack(champion.getIcon());
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(getName());
            item.setItemMeta(meta);
        }
        return item;
    }

    @Override
    public void spawnEffect(Location loc) {
        if (!isFallback) {
            for (int i = 0; i <= 2; i++) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    fireworkU.spawnBurst(loc, Color.YELLOW);
                    fireworkU.instantExplode(loc, Color.WHITE);
                }, i * 4L);
            }
        } else {
            fireworkU.spawnBurst(loc, Color.YELLOW);
        }
    }
}