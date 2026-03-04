package dabbiks.uhc.menu;

import dabbiks.uhc.game.gameplay.items.ItemBuilder;
import dabbiks.uhc.game.gameplay.items.recipes.data.RecipeIngredient;
import dabbiks.uhc.game.gameplay.items.recipes.data.RecipeInstance;
import dabbiks.uhc.game.gameplay.items.recipes.loader.RecipeManager;
import dabbiks.uhc.game.gameplay.items.recipes.loader.RecipeType;
import fr.mrmicky.fastinv.FastInv;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static dabbiks.uhc.Main.soundU;
import static dabbiks.uhc.Main.symbolU;

public class RecipeMenu extends FastInv {

    private final RecipeManager recipeManager;
    private final Player player;

    private RecipeType currentCategory;
    private int page = 0;
    private RecipeInstance selectedRecipe;
    private List<RecipeInstance> currentRecipes;

    private static final int[] CATEGORY_SLOTS = {0, 9, 18, 27, 36};
    private static final int[] RECIPE_SLOTS = {
            1, 2, 3, 4,
            10, 11, 12, 13,
            19, 20, 21, 22,
            28, 29, 30, 31,
            37, 38, 39, 40
    };
    private static final int[] GRID_SLOTS = {15, 16, 17, 24, 25, 26, 33, 34, 35};
    private static final int RESULT_SLOT = 43;
    private static final int PREV_PAGE_SLOT = 46;
    private static final int NEXT_PAGE_SLOT = 49;

    public RecipeMenu(Player player, RecipeManager recipeManager) {
        super(54, "\uF808" + symbolU.RECIPE_MENU);
        this.player = player;
        this.recipeManager = recipeManager;

        this.currentCategory = RecipeType.WEAPON;
        loadRecipes();

        refresh();
        Bukkit.getScheduler().runTaskLater(dabbiks.uhc.Main.plugin, this::refresh, 1L);
    }

    private void loadRecipes() {
        this.currentRecipes = recipeManager.getRecipesFromCategory(currentCategory);
        this.page = 0;
        this.selectedRecipe = null;
        if (!currentRecipes.isEmpty()) {
            this.selectedRecipe = currentRecipes.get(0);
        }
    }

    private void refresh() {
        for (int i = 0; i < getInventory().getSize(); i++) {
            removeItem(i);
        }

        renderCategories();
        renderRecipeList();
        renderCraftingGrid();
    }

    private void renderCategories() {
        int index = 0;
        for (RecipeType type : RecipeType.values()) {
            if (index >= CATEGORY_SLOTS.length) break;

            ItemStack icon = new ItemStack(Material.PAPER);
            ItemMeta meta = icon.getItemMeta();
            meta.setDisplayName(symbolU.MOUSE_LEFT + " §fPrzeglądaj " + type.getName());
            meta.setCustomModelData(10);

            if (type == currentCategory) {
                meta.setDisplayName(symbolU.MOUSE_LEFT + " §aWybrane");
                meta.setCustomModelData(getCategoryModel(type));
            }

            icon.setItemMeta(meta);

            setItem(CATEGORY_SLOTS[index], icon, e -> {
                if (currentCategory != type) {
                    currentCategory = type;
                    loadRecipes();
                    refresh();
                    soundU.playSoundToPlayer(player, Sound.ITEM_BOOK_PAGE_TURN, 1, 1);
                }
            });
            index++;
        }
    }

    private void renderRecipeList() {
        List<RecipeInstance> visibleRecipes = currentRecipes.stream()
                .filter(RecipeInstance::showInRecipeBook)
                .toList();

        int recipesPerPage = RECIPE_SLOTS.length;
        int startIndex = page * recipesPerPage;
        int endIndex = Math.min(startIndex + recipesPerPage, visibleRecipes.size());

        for (int i = startIndex; i < endIndex; i++) {
            RecipeInstance recipe = visibleRecipes.get(i);
            int slotIndex = i - startIndex;

            ItemStack icon = new ItemBuilder(recipe.getResult()).build();

            ItemMeta meta = icon.getItemMeta();
            List<String> lore = meta.getLore();
            if (lore == null) lore = new ArrayList<>();
            lore.add("");
            if (selectedRecipe != null && selectedRecipe.getId().equals(recipe.getId())) {
                lore.add("§aWybrany przepis");
                meta.addEnchant(org.bukkit.enchantments.Enchantment.UNBREAKING, 1, true);
                meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
            } else {
                lore.add(symbolU.MOUSE_LEFT + " §eKliknij, aby zobaczyć przepis");
            }
            meta.setLore(lore);
            icon.setItemMeta(meta);

            setItem(RECIPE_SLOTS[slotIndex], icon, e -> {
                selectedRecipe = recipe;
                refresh();
                soundU.playSoundToPlayer(player, Sound.UI_BUTTON_CLICK, 1, 1);
            });
        }

        if (page > 0) {
            ItemStack prev = new ItemStack(Material.ARROW);
            ItemMeta meta = prev.getItemMeta();
            meta.setDisplayName("§e« Poprzednia strona");
            prev.setItemMeta(meta);
            setItem(PREV_PAGE_SLOT, prev, e -> {
                page--;
                refresh();
                soundU.playSoundToPlayer(player, Sound.UI_BUTTON_CLICK, 1, 1);
            });
        }

        if (endIndex < visibleRecipes.size()) {
            ItemStack next = new ItemStack(Material.ARROW);
            ItemMeta meta = next.getItemMeta();
            meta.setDisplayName("§eNastępna strona »");
            next.setItemMeta(meta);
            setItem(NEXT_PAGE_SLOT, next, e -> {
                page++;
                refresh();
                soundU.playSoundToPlayer(player, Sound.UI_BUTTON_CLICK, 1, 1);
            });
        }
    }

    private void renderCraftingGrid() {
        if (selectedRecipe == null || !selectedRecipe.showInRecipeBook()) return;

        ItemStack resultItem = new ItemBuilder(selectedRecipe.getResult()).build();
        setItem(RESULT_SLOT, resultItem, e -> e.setCancelled(true));

        Map<Character, RecipeIngredient> ingredients = selectedRecipe.getIngredients();

        if (!selectedRecipe.isShaped()) {
            renderShapelessGrid(ingredients);
            return;
        }

        List<String> shape = selectedRecipe.getShape();
        int height = shape.size();
        int width = shape.stream().mapToInt(String::length).max().orElse(0);

        int rowOffset = (3 - height) / 2;
        int colOffset = (3 - width) / 2;

        for (int row = 0; row < shape.size(); row++) {
            String rowStr = shape.get(row);
            for (int col = 0; col < rowStr.length(); col++) {
                char key = rowStr.charAt(col);
                RecipeIngredient ingredient = ingredients.get(key);

                if (key == ' ' || ingredient == null) continue;

                int targetRow = row + rowOffset;
                int targetCol = col + colOffset;
                int gridIndex = targetRow * 3 + targetCol;

                if (gridIndex >= 0 && gridIndex < GRID_SLOTS.length) {
                    setItem(GRID_SLOTS[gridIndex], ingredient.build(), e -> e.setCancelled(true));
                }
            }
        }
    }

    private void renderShapelessGrid(Map<Character, RecipeIngredient> ingredients) {
        List<RecipeIngredient> ingredientList = new ArrayList<>(ingredients.values());

        if (ingredientList.size() == 1) {
            setItem(GRID_SLOTS[4], ingredientList.get(0).build(), e -> e.setCancelled(true));
            return;
        }

        int slotIdx = 0;
        for (RecipeIngredient ingredient : ingredientList) {
            if (slotIdx >= GRID_SLOTS.length) break;
            setItem(GRID_SLOTS[slotIdx++], ingredient.build(), e -> e.setCancelled(true));
        }
    }

    private int getCategoryModel(RecipeType type) {
        int model = switch (type) {
            case WEAPON -> 12;
            case ARMOR -> 11;
            case TOOL -> 13;
            case CONSUMABLE -> 15;
            case USABLE -> 14;
            default -> 10;
        };
        return model;
    }
}