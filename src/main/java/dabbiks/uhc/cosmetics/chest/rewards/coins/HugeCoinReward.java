package dabbiks.uhc.cosmetics.chest.rewards.coins;

import dabbiks.uhc.cosmetics.chest.rewards.Reward;
import dabbiks.uhc.player.data.persistent.PersistentData;
import dabbiks.uhc.player.data.persistent.PersistentDataManager;
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

public class HugeCoinReward extends Reward {

    private final String name;
    private final Random random = new Random();
    private final int amount;

    private final Material material = Material.SUNFLOWER;
    private final int model = 0;

    public HugeCoinReward() {
        amount = random.nextInt(7500, 15000);
        name = amount + symbolU.SCOREBOARD_COIN;

    }

    @Override
    public String getType() {
        return "§5§lWALUTA";
    }

    @Override
    public void addReward(PersistentData persistentData) {
        persistentData.addStats(PersistentStats.COINS, amount);
    }

    @Override
    public String getName() {
        return name;
    }

    public ItemStack getItem() {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setCustomModelData(model);
        item.setItemMeta(meta);
        return item;
    }

    private final Color[] colors = {
            Color.AQUA, Color.BLACK, Color.BLUE, Color.FUCHSIA, Color.GRAY,
            Color.GREEN, Color.LIME, Color.MAROON, Color.NAVY, Color.OLIVE,
            Color.ORANGE, Color.PURPLE, Color.RED, Color.SILVER, Color.TEAL,
            Color.WHITE, Color.YELLOW
    };

    @Override
    public void spawnEffect(Location location) {
        runEffect(location);

        for (int i = 1; i <= 3; i++) {
            long delay = i * 5L;
            Bukkit.getScheduler().runTaskLater(plugin, () -> runEffect(location), delay);
        }
    }

    private void runEffect(Location location) {
        Color randomColor1 = colors[ThreadLocalRandom.current().nextInt(colors.length)];
        Color randomColor2 = colors[ThreadLocalRandom.current().nextInt(colors.length)];

        fireworkU.spawnBurst(location, randomColor1);
        fireworkU.instantExplode(location, randomColor2);
    }
}
