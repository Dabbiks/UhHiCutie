package dabbiks.uhc.player.events.drop;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Biome;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public abstract class DropItem {

    public abstract Material getMaterial();
    public abstract Material getSmeltedMaterial();
    public abstract int getCustomModelData();
    public abstract int getMinAmount();
    public abstract int getMaxAmount();
    public abstract String getMessage();
    public abstract Sound getSound();
    public abstract String getDisplayName();
    public abstract List<String> getLore();

    public abstract double getChance(Material pickaxe, Material block, Biome biome);

    public ItemStack generateItem(int fortuneLevel, boolean isSmelted) {
        int amount = ThreadLocalRandom.current().nextInt(getMinAmount(), getMaxAmount() + 1);

        if (fortuneLevel > 0) {
            amount *= ThreadLocalRandom.current().nextInt(1, 3);
            if (ThreadLocalRandom.current().nextDouble() <= (0.3 * fortuneLevel)) {
                amount *= 2;
            }
        }

        Material finalMaterial = isSmelted && getSmeltedMaterial() != null ? getSmeltedMaterial() : getMaterial();
        ItemStack item = new ItemStack(finalMaterial, amount);

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            int cmd = getCustomModelData();
            if (cmd > 0) {
                meta.setCustomModelData(cmd);
            }
            if (getDisplayName() != null) {
                meta.setDisplayName(getDisplayName());
            }
            if (getLore() != null) {
                meta.setLore(getLore());
            }
            item.setItemMeta(meta);
        }

        return item;
    }
}