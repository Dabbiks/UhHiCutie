package dabbiks.uhc.game.gameplay.elytra;

import org.bukkit.inventory.ItemStack;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChestplateManager {

    private final Map<UUID, ItemStack> savedChestplates = new HashMap<>();
    private final Map<UUID, ItemStack> savedElytras = new HashMap<>();

    public void saveChestplate(UUID uuid, ItemStack item) {
        savedChestplates.put(uuid, item);
    }

    public ItemStack getAndRemoveChestplate(UUID uuid) {
        return savedChestplates.remove(uuid);
    }

    public boolean hasSavedChestplate(UUID uuid) {
        return savedChestplates.containsKey(uuid);
    }

    public void saveElytra(UUID uuid, ItemStack item) {
        savedElytras.put(uuid, item);
    }

    public ItemStack getElytra(UUID uuid) {
        return savedElytras.get(uuid);
    }

    public boolean hasSavedElytra(UUID uuid) {
        return savedElytras.containsKey(uuid);
    }
}