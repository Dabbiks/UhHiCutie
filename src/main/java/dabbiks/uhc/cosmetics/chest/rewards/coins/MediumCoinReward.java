package dabbiks.uhc.cosmetics.chest.rewards.coins;

import dabbiks.uhc.cosmetics.chest.rewards.Reward;
import dabbiks.uhc.player.data.persistent.PersistentData;
import dabbiks.uhc.player.data.persistent.PersistentDataManager;
import dabbiks.uhc.player.data.persistent.PersistentStats;
import dabbiks.uhc.utils.ParticleUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Random;

import static dabbiks.uhc.Main.*;

public class MediumCoinReward extends Reward {

    private final String name;
    private final Random random = new Random();
    private final int amount;

    private final Material material = Material.SUNFLOWER;
    private final int model = 0;

    public MediumCoinReward() {
        amount = random.nextInt(1000, 2500);
        name = amount + symbolU.SCOREBOARD_COIN;

    }

    @Override
    public String getType() {
        return "§e§lWALUTA";
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

    @Override
    public void spawnEffect(Location location) {
        fireworkU.spawnBurst(location, Color.YELLOW);
    }
}
