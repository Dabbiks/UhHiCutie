package dabbiks.uhc.game.gameplay.recipes;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CustomRecipeListener implements Listener {

    private final RecipeManager recipeManager;
    private final CraftLimitTracker tracker;

    public CustomRecipeListener(RecipeManager recipeManager, CraftLimitTracker tracker) {
        this.recipeManager = recipeManager;
        this.tracker = tracker;
    }

    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        if (event.getRecipe() == null) return;

        var inv = event.getInventory();
        var matrix = inv.getMatrix();

        boolean hasCustomData = false;
        for (ItemStack item : matrix) {
            if (item != null && item.hasItemMeta() && item.getItemMeta().hasCustomModelData()) {
                hasCustomData = true;
                break;
            }
        }
        if (hasCustomData) {
            if (!(event.getRecipe() instanceof Keyed keyed) || !"uhc".equals(keyed.getKey().getNamespace())) {
                inv.setResult(null);
                return;
            }
        }

        if (!(event.getRecipe() instanceof org.bukkit.inventory.ShapedRecipe shapedRecipe)) return;
        String recipeId = shapedRecipe.getKey().getKey();
        UHCRecipe recipe = recipeManager.getRecipeById(recipeId).orElse(null);
        if (recipe == null) return;

        if (!checkIngredientsMatch(matrix, recipe)) {
            inv.setResult(null);
        }
    }

    private boolean checkIngredientsMatch(ItemStack[] matrix, UHCRecipe recipe) {
        Map<Character, RecipeIngredient> ingredients = recipe.getIngredients();
        String[] shape = recipe.getShape().toArray(new String[0]);

        for (int row = 0; row < shape.length; row++) {
            String line = shape[row];
            for (int col = 0; col < line.length(); col++) {
                char keyChar = line.charAt(col);
                RecipeIngredient expected = ingredients.get(keyChar);

                int index = row * 3 + col;
                ItemStack actual = (index < matrix.length) ? matrix[index] : null;

                if (expected == null || keyChar == ' ') {
                    if (actual != null && actual.getType() != Material.AIR) {
                        return false;
                    }
                    continue;
                }

                if (!isItemMatchingRecipeIngredient(actual, expected)) {
                    return false;
                }
            }
        }
        return true;
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!(event.getRecipe() instanceof Keyed keyedRecipe)) return;
        if (!"uhc".equals(keyedRecipe.getKey().getNamespace())) return;
        if (event.getInventory().getResult() == null) return;
        if (event.getInventory().getResult().getType().equals(Material.SHIELD)) return;

        event.setCancelled(true);

        String recipeId = keyedRecipe.getKey().getKey();
        if (recipeId.endsWith("_reversed")) {
            recipeId = recipeId.substring(0, recipeId.length() - "_reversed".length());
        }
        Optional<UHCRecipe> optional = recipeManager.getRecipeById(recipeId);

        if (optional.isEmpty()) return;

        UHCRecipe uhcRecipe = optional.get();
        int maxAllowed = uhcRecipe.getMaxCraftsPerPlayer();
        int alreadyCrafted = tracker.getCraftCount(player, recipeId);
        int remaining = maxAllowed - alreadyCrafted;
        if (remaining <= 0) {
            player.sendMessage(ChatColor.RED + "Osiągnięto limit craftingu tego przedmiotu!");
            return;
        }

        ItemStack result = uhcRecipe.buildResult();
        if (result == null || result.getType() == Material.AIR) return;

        int singleCraftAmount = result.getAmount();

        ClickType click = event.getClick();
        boolean isShiftLeftClick = click == ClickType.SHIFT_LEFT;

        int craftCount;

        if (isShiftLeftClick && recipeId.equals("puszka_pandory")) {
            craftCount = 1;
            ItemStack[] matrix = event.getInventory().getMatrix();
            if (!removeIngredients(matrix, uhcRecipe, craftCount)) {
                player.sendMessage(ChatColor.RED + "Nie udało się usunąć składników.");
                return;
            }
            for (int i = 0; i < craftCount; i++) {
                tracker.increment(player, recipeId);
            }
            if (event.getClickedInventory() == null) { return; }
            if (event.getClickedInventory().getLocation() != null && event.getClickedInventory().getLocation().getBlock().getType().equals(Material.CRAFTING_TABLE)) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> event.getClickedInventory().getLocation().getBlock().setType(Material.CHEST), 1L);
                PuszkaPandory.addPandoraChest(event.getClickedInventory().getLocation());
            }
            return;
        } else if (!isShiftLeftClick && recipeId.equals("puszka_pandory")) {
            craftCount = 1;
            ItemStack[] matrix = event.getInventory().getMatrix();
            if (!removeIngredients(matrix, uhcRecipe, craftCount)) {
                player.sendMessage(ChatColor.RED + "Nie udało się usunąć składników.");
                return;
            }
            for (int i = 0; i < craftCount; i++) {
                tracker.increment(player, recipeId);
            }
            if (event.getClickedInventory() == null) { return; }
            if (event.getClickedInventory().getLocation() != null && event.getClickedInventory().getLocation().getBlock().getType().equals(Material.CRAFTING_TABLE)) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> event.getClickedInventory().getLocation().getBlock().setType(Material.CHEST), 1L);
                PuszkaPandory.addPandoraChest(event.getClickedInventory().getLocation());
            }
            return;
        } else if (isShiftLeftClick) {
            ItemStack[] matrix = event.getInventory().getMatrix();
            int maxPossible = getMaxCraftable(matrix, uhcRecipe);
            int maxFit = countFreeSpace(player, result) / singleCraftAmount;
            craftCount = Math.min(Math.min(maxPossible, maxFit), remaining);
        } else {
            craftCount = 1;
        }

        if (craftCount <= 0) {
            player.sendMessage(ChatColor.RED + "Brak miejsca w ekwipunku lub na kursorze.");
            return;
        }

        ItemStack[] matrix = event.getInventory().getMatrix();
        if (!removeIngredients(matrix, uhcRecipe, craftCount)) {
            player.sendMessage(ChatColor.RED + "Nie udało się usunąć składników.");
            return;
        }

        ItemStack totalResult = result.clone();
        totalResult.setAmount(craftCount * singleCraftAmount);

        boolean gaveItem = false;
        ItemStack cursor = player.getItemOnCursor();
        if (!isShiftLeftClick) {
            if (cursor == null || cursor.getType() == Material.AIR) {
                player.setItemOnCursor(totalResult);
                gaveItem = true;
            } else if (cursor.isSimilar(result) &&
                    cursor.getAmount() + totalResult.getAmount() <= result.getMaxStackSize()) {
                cursor.setAmount(cursor.getAmount() + totalResult.getAmount());
                gaveItem = true;
            }
        }

        if (!gaveItem) {
            HashMap<Integer, ItemStack> leftovers = player.getInventory().addItem(totalResult);
            if (!leftovers.isEmpty()) {
                player.sendMessage(ChatColor.RED + "Brak miejsca na przedmiot.");
                return;
            }
        }

        for (int i = 0; i < craftCount; i++) {
            tracker.increment(player, recipeId);
        }

        boolean canCraftMore = canCraftRecipe(event.getInventory().getMatrix(), uhcRecipe);
        if (!canCraftMore) {
            event.getInventory().setResult(null);
        }
    }

    private int getMaxCraftable(ItemStack[] matrix, UHCRecipe recipe) {
        int max = Integer.MAX_VALUE;
        Map<RecipeIngredient, Integer> required = new HashMap<>();

        if (recipe.isShaped()) {
            for (String row : recipe.getShape()) {
                for (char c : row.toCharArray()) {
                    if (c != ' ' && recipe.getIngredients().containsKey(c)) {
                        RecipeIngredient ing = recipe.getIngredients().get(c);
                        required.merge(ing, 1, Integer::sum);
                    }
                }
            }
        } else {
            for (RecipeIngredient ing : recipe.getIngredients().values()) {
                required.merge(ing, 1, Integer::sum);
            }
        }

        for (Map.Entry<RecipeIngredient, Integer> entry : required.entrySet()) {
            int available = countInMatrix(matrix, entry.getKey());
            max = Math.min(max, available / entry.getValue());
        }

        return max;
    }

    private int countFreeSpace(Player player, ItemStack result) {
        int free = 0;
        for (ItemStack item : player.getInventory().getStorageContents()) {
            if (item == null || item.getType() == Material.AIR) {
                free += result.getMaxStackSize();
            } else if (item.isSimilar(result)) {
                free += result.getMaxStackSize() - item.getAmount();
            }
        }
        return free;
    }

    private boolean removeIngredients(ItemStack[] matrix, UHCRecipe recipe, int times) {
        for (int t = 0; t < times; t++) {
            for (int i = 0; i < matrix.length; i++) {
                ItemStack item = matrix[i];
                if (item == null || item.getType() == Material.AIR) continue;

                for (RecipeIngredient ing : recipe.getIngredients().values()) {
                    if (isItemMatchingRecipeIngredient(item, ing)) {
                        item.setAmount(item.getAmount() - 1);
                        if (item.getAmount() <= 0) matrix[i] = null;
                        break;
                    }
                }
            }
        }
        return true;
    }

    private boolean isItemMatchingRecipeIngredient(ItemStack item, RecipeIngredient ing) {
        if (item == null || item.getType() == Material.AIR) return false;

        Material expected = ing.getMaterial();
        Material actual = item.getType();

        // 🔹 Obsługa wszystkich desek, gdy expected = OAK_PLANKS
        if (expected == Material.OAK_PLANKS) {
            if (!actual.name().endsWith("_PLANKS")) return false;
        } else {
            if (actual != expected) return false;
        }

        ItemMeta meta = item.getItemMeta();
        if (ing.getCustomModelData() != null) {
            if (meta == null || !meta.hasCustomModelData() || meta.getCustomModelData() != ing.getCustomModelData()) {
                return false;
            }
        }
        return true;
    }


    private int countInMatrix(ItemStack[] matrix, RecipeIngredient ing) {
        int count = 0;
        for (ItemStack item : matrix) {
            if (isItemMatchingRecipeIngredient(item, ing)) {
                count += item.getAmount();
            }
        }
        return count;
    }

    private boolean canCraftRecipe(ItemStack[] matrix, UHCRecipe recipe) {
        Map<RecipeIngredient, Integer> required = new HashMap<>();

        if (recipe.isShaped()) {
            for (String row : recipe.getShape()) {
                for (char c : row.toCharArray()) {
                    if (c == ' ') continue;
                    RecipeIngredient ing = recipe.getIngredients().get(c);
                    if (ing == null) continue;
                    required.merge(ing, 1, Integer::sum);
                }
            }
        } else {
            for (RecipeIngredient ing : recipe.getIngredients().values()) {
                required.merge(ing, 1, Integer::sum);
            }
        }

        Map<RecipeIngredient, Integer> available = new HashMap<>();
        for (ItemStack item : matrix) {
            if (item == null || item.getType() == Material.AIR) continue;

            for (RecipeIngredient ing : required.keySet()) {
                if (isItemMatchingRecipeIngredient(item, ing)) {
                    available.merge(ing, item.getAmount(), Integer::sum);
                }
            }
        }

        for (Map.Entry<RecipeIngredient, Integer> entry : required.entrySet()) {
            if (available.getOrDefault(entry.getKey(), 0) < entry.getValue()) {
                return false;
            }
        }

        return true;
    }
}
