package dabbiks.uhc.game.gameplay.elytra;

import org.bukkit.inventory.ItemStack;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChestplateManager {

    private final Map<UUID, ItemStack> savedChestplates = new HashMap<>();

    public void saveChestplate(UUID uuid, ItemStack item) {
        savedChestplates.put(uuid, item);
    }

    public ItemStack getAndRemoveChestplate(UUID uuid) {
        return savedChestplates.remove(uuid);
    }

    public boolean hasSavedChestplate(UUID uuid) {
        return savedChestplates.containsKey(uuid);
    }
}