package dabbiks.uhc.game.gameplay.items.recipes.listener;

import dabbiks.uhc.Main;
import dabbiks.uhc.game.gameplay.items.recipes.data.RecipeIngredient;
import dabbiks.uhc.game.gameplay.items.recipes.data.RecipeInstance;
import dabbiks.uhc.game.gameplay.items.recipes.loader.RecipeManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static dabbiks.uhc.Main.plugin;

public class RecipeListener implements Listener {

    private final RecipeManager recipeManager;
    private final RecipeLimitTracker tracker;

    public RecipeListener(RecipeManager recipeManager, RecipeLimitTracker tracker) {
        this.recipeManager = recipeManager;
        this.tracker = tracker;
    }

    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        if (event.getRecipe() == null || !(event.getRecipe() instanceof Keyed keyed)) return;
        if (!"uhhicutie".equals(keyed.getKey().getNamespace())) return;

        Player player = (Player) event.getView().getPlayer();
        String recipeId = getRealRecipeId(keyed.getKey().getKey());
        Optional<RecipeInstance> recipeOpt = RecipeManager.getRecipeById(recipeId);

        if (recipeOpt.isEmpty()) return;
        RecipeInstance recipe = recipeOpt.get();

        if (!validateIngredients(event.getInventory().getMatrix(), recipe)) {
            event.getInventory().setResult(null);
            return;
        }

        if (isLimitFullyReached(player, recipe)) {
            event.getInventory().setResult(null);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onCraft(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!(event.getRecipe() instanceof Keyed keyed)) return;
        if (!"uhhicutie".equals(keyed.getKey().getNamespace())) return;

        String recipeId = getRealRecipeId(keyed.getKey().getKey());
        Optional<RecipeInstance> recipeOpt = RecipeManager.getRecipeById(recipeId);
        if (recipeOpt.isEmpty()) return;

        RecipeInstance recipe = recipeOpt.get();

        if (!validateIngredients(event.getInventory().getMatrix(), recipe)) {
            event.setCancelled(true);
            player.updateInventory();
            return;
        }

        int maxAllowed = recipe.getMaxCraftsPerPlayer();
        int remainingLimit = Integer.MAX_VALUE;

        if (maxAllowed > 0) {
            int alreadyCrafted = tracker.getCraftCount(player, recipeId);
            remainingLimit = maxAllowed - alreadyCrafted;

            if (remainingLimit <= 0) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "Limit wyczerpany!");
                player.updateInventory();
                return;
            }
        }

        if (event.isShiftClick()) {
            event.setCancelled(true);

            int maxPossibleByMaterials = getMaxCraftTimes(event.getInventory());
            int amountToCraft = Math.min(maxPossibleByMaterials, remainingLimit);

            if (amountToCraft <= 0) return;

            reduceMatrix(event.getInventory(), amountToCraft);

            ItemStack resultItemTemplate = event.getRecipe().getResult().clone();
            List<ItemStack> itemsToGive = new ArrayList<>();
            for (int i = 0; i < amountToCraft; i++) {
                itemsToGive.add(resultItemTemplate.clone());
            }

            HashMap<Integer, ItemStack> leftovers = player.getInventory().addItem(itemsToGive.toArray(new ItemStack[0]));

            if (!leftovers.isEmpty()) {
                for (ItemStack item : leftovers.values()) {
                    player.getWorld().dropItem(player.getLocation(), item);
                }
                player.sendMessage(ChatColor.YELLOW + "Ekwipunek pełny! Przedmioty upadły na ziemię.");
            }

            if (maxAllowed > 0) {
                for (int i = 0; i < amountToCraft; i++) {
                    tracker.increment(player, recipeId);
                }
                if (amountToCraft == remainingLimit) {
                    player.sendMessage(ChatColor.YELLOW + "Wytworzono ostatnie możliwe sztuki (Limit).");
                }
            }

            event.getInventory().setResult(null);
            Bukkit.getScheduler().runTask(plugin, player::updateInventory);

        } else {
            if (maxAllowed > 0) {
                tracker.increment(player, recipeId);
                if (remainingLimit == 1) {
                    player.sendMessage(ChatColor.YELLOW + "Wytworzono ostatnie możliwe sztuki (Limit).");
                }
            }
        }
    }

    private boolean validateIngredients(ItemStack[] matrix, RecipeInstance recipe) {
        if (recipe.isShaped()) {
            return validateShaped(matrix, recipe);
        } else {
            return validateShapeless(matrix, recipe);
        }
    }

    private boolean validateShaped(ItemStack[] matrix, RecipeInstance recipe) {
        if (checkShapeMatrix(matrix, recipe.getShape(), recipe.getIngredients())) {
            return true;
        }

        List<String> reversedShape = recipe.getShape().stream()
                .map(row -> new StringBuilder(row).reverse().toString())
                .toList();

        if (!recipe.getShape().equals(reversedShape)) {
            return checkShapeMatrix(matrix, reversedShape, recipe.getIngredients());
        }

        return false;
    }

    private boolean checkShapeMatrix(ItemStack[] matrix, List<String> shape, Map<Character, RecipeIngredient> ingredients) {
        int minRow = 3, minCol = 3, maxRow = -1, maxCol = -1;
        for (int i = 0; i < matrix.length; i++) {
            if (matrix[i] != null && matrix[i].getType() != Material.AIR) {
                int row = i / 3;
                int col = i % 3;
                if (row < minRow) minRow = row;
                if (col < minCol) minCol = col;
                if (row > maxRow) maxRow = row;
                if (col > maxCol) maxCol = col;
            }
        }

        if (maxRow == -1) return false;

        int recipeHeight = shape.size();
        int recipeWidth = shape.get(0).length();

        if ((maxRow - minRow + 1) != recipeHeight || (maxCol - minCol + 1) != recipeWidth) {
            return false;
        }

        for (int r = 0; r < recipeHeight; r++) {
            String rowStr = shape.get(r);
            for (int c = 0; c < recipeWidth; c++) {
                char key = rowStr.charAt(c);
                int matrixIndex = (minRow + r) * 3 + (minCol + c);
                ItemStack item = matrix[matrixIndex];

                if (key == ' ') {
                    if (item != null && item.getType() != Material.AIR) return false;
                    continue;
                }

                RecipeIngredient required = ingredients.get(key);
                if (required == null) return false;
                if (!isItemMatchingIngredient(item, required)) return false;
            }
        }
        return true;
    }

    private boolean validateShapeless(ItemStack[] matrix, RecipeInstance recipe) {
        List<RecipeIngredient> required = new ArrayList<>(recipe.getIngredients().values());

        for (ItemStack item : matrix) {
            if (item == null || item.getType() == Material.AIR) continue;

            boolean matched = false;
            for (int i = 0; i < required.size(); i++) {
                if (isItemMatchingIngredient(item, required.get(i))) {
                    required.remove(i);
                    matched = true;
                    break;
                }
            }
            if (!matched) return false;
        }
        return required.isEmpty();
    }

    private boolean isItemMatchingIngredient(ItemStack item, RecipeIngredient ingredient) {
        if (item == null || item.getType() == Material.AIR) return false;

        Material requiredMat = ingredient.getMaterial();
        Material itemMat = item.getType();

        boolean materialMatches = false;

        if (Tag.PLANKS.isTagged(requiredMat)) {
            materialMatches = Tag.PLANKS.isTagged(itemMat);
        } else if (Tag.WOOL.isTagged(requiredMat)) {
            materialMatches = Tag.WOOL.isTagged(itemMat);
        } else if (Tag.LOGS.isTagged(requiredMat)) {
            materialMatches = Tag.LOGS.isTagged(itemMat);
        } else if (requiredMat == Material.COBBLESTONE || requiredMat == Material.COBBLED_DEEPSLATE) {
            materialMatches = (itemMat == Material.COBBLESTONE || itemMat == Material.COBBLED_DEEPSLATE);
        } else if (requiredMat == Material.COAL || requiredMat == Material.CHARCOAL) {
            materialMatches = (itemMat == Material.COAL || itemMat == Material.CHARCOAL);
        } else {
            materialMatches = (itemMat == requiredMat);
        }

        if (!materialMatches) return false;

        ItemMeta meta = item.getItemMeta();

        int requiredCMD = (ingredient.getCustomModelData() == null) ? 0 : ingredient.getCustomModelData();
        int itemCMD = (meta != null && meta.hasCustomModelData()) ? meta.getCustomModelData() : 0;

        return requiredCMD == itemCMD;
    }

    private void reduceMatrix(CraftingInventory inv, int timesToCraft) {
        ItemStack[] matrix = inv.getMatrix();
        for (int i = 0; i < matrix.length; i++) {
            if (matrix[i] != null && matrix[i].getType() != Material.AIR) {
                int currentAmount = matrix[i].getAmount();
                int newAmount = currentAmount - timesToCraft;
                if (newAmount > 0) {
                    matrix[i].setAmount(newAmount);
                } else {
                    matrix[i] = null;
                }
            }
        }
        inv.setMatrix(matrix);
    }

    private int getMaxCraftTimes(CraftingInventory inv) {
        ItemStack[] matrix = inv.getMatrix();
        int minMaterial = Integer.MAX_VALUE;
        boolean hasItems = false;
        for (ItemStack item : matrix) {
            if (item != null && item.getType() != Material.AIR) {
                if (item.getAmount() < minMaterial) {
                    minMaterial = item.getAmount();
                }
                hasItems = true;
            }
        }
        return hasItems ? minMaterial : 0;
    }

    private boolean isLimitFullyReached(Player player, RecipeInstance recipe) {
        if (recipe.getMaxCraftsPerPlayer() <= 0) return false;
        return tracker.getCraftCount(player, recipe.getId()) >= recipe.getMaxCraftsPerPlayer();
    }

    private String getRealRecipeId(String key) {
        if (key.endsWith("_reversed")) return key.substring(0, key.length() - "_reversed".length());
        return key;
    }
}