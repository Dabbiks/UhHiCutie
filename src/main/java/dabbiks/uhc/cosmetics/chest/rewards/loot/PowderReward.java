package dabbiks.uhc.cosmetics.chest.rewards.loot;

import dabbiks.uhc.cosmetics.chest.rewards.Reward;
import dabbiks.uhc.player.data.persistent.PersistentData;
import dabbiks.uhc.player.data.persistent.PersistentStats;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static dabbiks.uhc.Main.*;

public class PowderReward extends Reward {

    public enum Tier {
        MINI("§4§lPYŁ", 10, 80),
        SMALL("§c§lPYŁ", 60, 200),
        MEDIUM("§e§lPYŁ", 200, 500),
        BIG("§e§lPYŁ", 400, 1500),
        HUGE("§5§lPYŁ", 1500, 3000);

        private final String typePrefix;
        private final int min, max;

        Tier(String typePrefix, int min, int max) {
            this.typePrefix = typePrefix;
            this.min = min;
            this.max = max;
        }
    }

    private final Tier tier;
    private final int amount;
    private final String name;
    private static final Random random = new Random();

    public PowderReward(Tier tier) {
        this.tier = tier;
        this.amount = random.nextInt(tier.min, tier.max);
        this.name = amount + symbolU.SCOREBOARD_POWDER;
    }

    @Override
    public String getType() {
        return tier.typePrefix;
    }

    @Override
    public void addReward(PersistentData persistentData) {
        persistentData.addStats(PersistentStats.POWDER, amount);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ItemStack getItem() {
        ItemStack item = new ItemStack(Material.AMETHYST_SHARD);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(0);
            item.setItemMeta(meta);
        }
        return item;
    }

    @Override
    public void spawnEffect(Location loc) {
        switch (tier) {
            case MEDIUM -> fireworkU.spawnBurst(loc, Color.YELLOW);
            case BIG -> {
                fireworkU.spawnBurst(loc, Color.YELLOW);
                fireworkU.instantExplode(loc, Color.ORANGE);
            }
            case HUGE -> {
                for (int i = 0; i <= 3; i++) {
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        Color c1 = getRandomColor();
                        Color c2 = getRandomColor();
                        fireworkU.spawnBurst(loc, c1);
                        fireworkU.instantExplode(loc, c2);
                    }, i * 5L);
                }
            }
        }
    }

    private Color getRandomColor() {
        Color[] colors = {Color.AQUA, Color.BLACK, Color.BLUE, Color.FUCHSIA, Color.GRAY, Color.GREEN, Color.LIME, Color.MAROON, Color.NAVY, Color.OLIVE, Color.ORANGE, Color.PURPLE, Color.RED, Color.SILVER, Color.TEAL, Color.WHITE, Color.YELLOW};
        return colors[ThreadLocalRandom.current().nextInt(colors.length)];
    }
}