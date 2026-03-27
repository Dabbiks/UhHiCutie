package dabbiks.uhc.cosmetics.chest.rewards.loot;

import dabbiks.uhc.cosmetics.chest.rewards.Reward;
import dabbiks.uhc.player.data.persistent.PersistentData;
import dabbiks.uhc.player.data.persistent.PersistentStats;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Random;

import static dabbiks.uhc.Main.fireworkU;

public class StockReward extends Reward {

    public enum Tier {
        SMALL("§c§lAKCJE", 1, 4);

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

    public StockReward(Tier tier) {
        this.tier = tier;
        this.amount = random.nextInt(tier.min, tier.max);
        String name = "";
        if (amount == 1) name = amount + " Akcja";
        if (amount >= 2) name = amount + " Akcje";
        this.name = name;
    }

    @Override
    public String getType() {
        return tier.typePrefix;
    }

    @Override
    public void addReward(PersistentData persistentData) {
        persistentData.addStats(PersistentStats.STOCK, amount);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ItemStack getItem() {
        ItemStack item = new ItemStack(Material.PAPER);
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
            case SMALL -> fireworkU.spawnBurst(loc, Color.YELLOW);
        }
    }
}