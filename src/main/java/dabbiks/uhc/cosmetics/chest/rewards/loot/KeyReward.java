package dabbiks.uhc.cosmetics.chest.rewards.loot;

import dabbiks.uhc.cosmetics.chest.KeyType;
import dabbiks.uhc.cosmetics.chest.rewards.Reward;
import dabbiks.uhc.player.data.persistent.PersistentData;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.concurrent.ThreadLocalRandom;

import static dabbiks.uhc.Main.fireworkU;

public class KeyReward extends Reward {

    private final KeyType type;
    private final boolean isFullKey;
    private final int amount;

    public KeyReward(KeyType type) {
        this.type = type;
        double roll = ThreadLocalRandom.current().nextDouble();
        if (roll < 0.20) {
            this.isFullKey = true;
            this.amount = ThreadLocalRandom.current().nextInt(1, 3);
        } else {
            this.isFullKey = false;
            this.amount = ThreadLocalRandom.current().nextInt(1, 4);
        }
    }

    @Override
    public String getType() {
        String color = switch (type) {
            case COMMON -> "§7";
            case RARE -> "§b";
            case EPIC -> "§a";
            case MYTHIC -> "§d";
            case LEGENDARY -> "§5";
            case EASTER -> "§e";
        };
        return color + "§lKLUCZ";
    }

    @Override
    public void addReward(PersistentData persistentData) {
        int index = type.getIndex();
        if (isFullKey) {
            persistentData.addKeys(index, amount);
        } else {
            persistentData.addKeyFragments(index, amount);

            int totalFragments = persistentData.getKeyFragments(index);
            if (totalFragments >= 3) {
                int newKeys = totalFragments / 3;
                int remainingFragments = totalFragments % 3;

                persistentData.addKeys(index, newKeys);
                persistentData.setKeyFragments(index, remainingFragments);
            }
        }
    }

    @Override
    public String getName() {
        return "§e" + amount + "x " + (isFullKey ? "§fKlucz" : "§fOdłamek klucza");
    }

    @Override
    public ItemStack getItem() {
        ItemStack item = new ItemStack(isFullKey ? Material.TRIPWIRE_HOOK : Material.PRISMARINE_SHARD);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(type.getModel());
            item.setItemMeta(meta);
        }
        return item;
    }

    @Override
    public void spawnEffect(Location loc) {
        Color color = switch (type) {
            case COMMON -> Color.WHITE;
            case RARE -> Color.AQUA;
            case EPIC -> Color.GREEN;
            case MYTHIC -> Color.FUCHSIA;
            case LEGENDARY -> Color.PURPLE;
            case EASTER -> Color.YELLOW;
        };
        fireworkU.spawnBurst(loc, color);
    }
}