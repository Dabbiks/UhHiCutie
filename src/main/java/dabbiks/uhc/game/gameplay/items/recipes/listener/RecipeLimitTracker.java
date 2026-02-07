package dabbiks.uhc.game.gameplay.items.recipes.listener;

import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RecipeLimitTracker {

    private final Map<UUID, Map<String, Integer>> playerCrafts = new HashMap<>();

    public boolean canCraft(Player player, String recipeId, int limit) {
        return getCraftCount(player, recipeId) < limit;
    }

    public void increment(Player player, String recipeId) {
        playerCrafts
                .computeIfAbsent(player.getUniqueId(), k -> new HashMap<>())
                .merge(recipeId, 1, Integer::sum);
    }

    public int getCraftCount(Player player, String recipeId) {
        return playerCrafts
                .getOrDefault(player.getUniqueId(), Collections.emptyMap())
                .getOrDefault(recipeId, 0);
    }

}
