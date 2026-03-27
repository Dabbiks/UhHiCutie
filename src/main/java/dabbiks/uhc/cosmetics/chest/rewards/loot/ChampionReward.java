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

public class ChampionReward extends Reward {

    private final Champion champion;
    private final boolean isFullUnlock;
    private final int shardAmount;

    public ChampionReward() {
        List<Champion> availableChampions = new ChampionManager().getChampions();
        this.champion = availableChampions.get(ThreadLocalRandom.current().nextInt(availableChampions.size()));

        double roll = ThreadLocalRandom.current().nextDouble();
        if (roll < 0.15) {
            this.isFullUnlock = true;
            this.shardAmount = 0;
        } else {
            this.isFullUnlock = false;
            this.shardAmount = ThreadLocalRandom.current().nextInt(1, 4);
        }
    }

    @Override
    public String getType() {
        return "§6§lKLASA";
    }

    @Override
    public void addReward(PersistentData persistentData) {
        if (isFullUnlock) {
            if (persistentData.hasUnlockedChampion(champion.getId())) {
                persistentData.addStats(PersistentStats.COINS, champion.getCost() / 8);

                Player player = Bukkit.getPlayer(persistentData.getName());
                if (player == null) return;
                player.sendMessage("§7Otrzymujesz §f" + champion.getCost() / 8 + symbolU.SCOREBOARD_COIN + "§7 za §e" + champion.getName());
            } else {
                persistentData.addUnlockedChampion(champion.getId());
            }
        } else {
            persistentData.addChampionShards(champion.getId(), shardAmount);
        }
    }

    @Override
    public String getName() {
        if (isFullUnlock) {
            return "§f" + champion.getName();
        } else {
            return "§e" + shardAmount + "x §fOdłamek " + champion.getName();
        }
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
        if (isFullUnlock) {
            for (int i = 0; i <= 3; i++) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    fireworkU.spawnBurst(loc, Color.ORANGE);
                    fireworkU.instantExplode(loc, Color.YELLOW);
                }, i * 5L);
            }
        } else {
            fireworkU.spawnBurst(loc, Color.YELLOW);
        }
    }
}