package dabbiks.uhc.game.gameplay.items.recipes.loader;

import com.google.gson.Gson;
import dabbiks.uhc.game.gameplay.items.recipes.data.RecipeInstance;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import static dabbiks.uhc.Main.plugin;

public class RecipeLoader {

    private RecipeManager manager;

    public RecipeLoader(RecipeManager manager) {
        this.manager = manager;
    }

    public void loadRecipes() {
        File folder = new File(plugin.getDataFolder(), "recipes");
        if (!folder.exists()) folder.mkdirs();

        Gson gson = new Gson();
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null) return;

        for (File file : files) {
            try (Reader reader = new FileReader(file)) {
                RecipeInstance recipe = gson.fromJson(reader, RecipeInstance.class);
                if (recipe == null || recipe.getId() == null || recipe.getResult() == null) continue;

                manager.registerRecipe(recipe);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                System.err.println("Błąd podczas ładowania przepisu z pliku: " + file.getName());
                e.printStackTrace();
            }
        }
    }

}
