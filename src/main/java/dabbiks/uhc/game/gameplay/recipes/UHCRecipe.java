package dabbiks.uhc.game.gameplay.recipes;

import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class UHCRecipe {

    private String id;
    private String type;
    private List<String> shape; // tylko dla shaped
    private Map<Character, RecipeIngredient> ingredients;
    private RecipeResult result;
    private int maxCraftsPerPlayer;
    private List<String> categories;

    private boolean showInRecipeBook;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public boolean isShaped() { return "shaped".equalsIgnoreCase(type); }

    public List<String> getShape() { return shape; }
    public void setShape(List<String> shape) { this.shape = shape; }

    public Map<Character, RecipeIngredient> getIngredients() { return ingredients; }
    public void setIngredients(Map<Character, RecipeIngredient> ingredients) { this.ingredients = ingredients; }

    public RecipeResult getResult() { return result; }
    public void setResult(RecipeResult result) { this.result = result; }

    public int getMaxCraftsPerPlayer() { return maxCraftsPerPlayer; }
    public void setMaxCraftsPerPlayer(int maxCraftsPerPlayer) { this.maxCraftsPerPlayer = maxCraftsPerPlayer; }

    public List<String> getCategories() { return categories; }
    public void setCategories(List<String> categories) { this.categories = categories; }

    public boolean isShowInRecipeBook() { return showInRecipeBook; }
    public void setShowInRecipeBook(boolean showInRecipeBook) { this.showInRecipeBook = showInRecipeBook; }

    public ItemStack buildResult() {
        return result.buildItem(id);
    }

    public ItemStack buildShowcaseResult() {
        return result.buildShowcaseItem(id);
    }
}
