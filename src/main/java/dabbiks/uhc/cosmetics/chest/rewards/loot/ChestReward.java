package dabbiks.uhc.cosmetics.chest.rewards.loot;

import dabbiks.uhc.cosmetics.chest.ChestType;
import dabbiks.uhc.cosmetics.chest.rewards.Reward;
import dabbiks.uhc.player.data.persistent.PersistentData;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.concurrent.ThreadLocalRandom;

import static dabbiks.uhc.Main.fireworkU;

public class ChestReward extends Reward {

    private final ChestType type;
    private final int amount;

    public ChestReward(ChestType type) {
        this.type = type;
        this.amount = ThreadLocalRandom.current().nextInt(1, 3);
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
        return color + "§lSKRZYNIA";
    }

    @Override
    public void addReward(PersistentData persistentData) {
        persistentData.addChests(type.getIndex(), amount);
    }

    @Override
    public String getName() {
        return "§e" + amount + "x " + "§fSkrzynia";
    }

    @Override
    public ItemStack getItem() {
        ItemStack item = new ItemStack(Material.HONEYCOMB);
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