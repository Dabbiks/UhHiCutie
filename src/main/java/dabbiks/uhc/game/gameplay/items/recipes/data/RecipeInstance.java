package dabbiks.uhc.game.gameplay.items.recipes.data;

import dabbiks.uhc.game.gameplay.items.ItemBuilder;
import dabbiks.uhc.game.gameplay.items.ItemInstance;
import dabbiks.uhc.player.data.session.SessionTags;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class RecipeInstance {

    private String id;
    private String type;
    private List<String> shape;
    private Map<Character, RecipeIngredient> ingredients;
    private ItemInstance result;
    private int maxCraftsPerPlayer;
    private List<String> categories;
    private boolean showInRecipeBook;
    private SessionTags requiredTag;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public boolean isShaped() { return "shaped".equalsIgnoreCase(type); }

    public List<String> getShape() { return shape; }
    public void setShape(List<String> shape) { this.shape = shape; }

    public Map<Character, RecipeIngredient> getIngredients() { return ingredients; }
    public void setIngredients(Map<Character, RecipeIngredient> ingredients) { this.ingredients = ingredients; }

    public ItemInstance getResult() { return result; }
    public void setResult(ItemInstance result) { this.result = result; }

    public int getMaxCraftsPerPlayer() { return maxCraftsPerPlayer; }
    public void setMaxCraftsPerPlayer(int maxCraftsPerPlayer) { this.maxCraftsPerPlayer = maxCraftsPerPlayer; }

    public List<String> getCategories() { return categories; }
    public void setCategories(List<String> categories) { this.categories = categories; }

    public boolean showInRecipeBook() { return showInRecipeBook; }
    public void setShowInRecipeBook(boolean showInRecipeBook) { this.showInRecipeBook = showInRecipeBook; }

    public SessionTags getRequiredTag() { return requiredTag; }
    public void setRequiredTag(SessionTags requiredTag) { this.requiredTag = requiredTag; }

    public ItemStack buildResult() {
        return new ItemBuilder(result).build();
    }

}