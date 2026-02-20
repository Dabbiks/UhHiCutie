package dabbiks.uhc.cosmetics.chest.rewards;

import dabbiks.uhc.player.data.persistent.PersistentData;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public abstract class Reward {

    public abstract String getType();

    public abstract void addReward(PersistentData persistentData);

    public abstract String getName();

    public abstract ItemStack getItem();

    public abstract void spawnEffect(Location location);
}
