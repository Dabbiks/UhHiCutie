package dabbiks.uhc.game.gameplay.items.recipes.loader;

import com.google.gson.Gson;
import dabbiks.uhc.game.gameplay.items.recipes.data.RecipeInstance;
import dabbiks.uhc.game.gameplay.items.recipes.remover.RecipeRemover;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import static dabbiks.uhc.Main.plugin;

public class RecipeLoader {

    private final RecipeManager manager;

    public RecipeLoader(RecipeManager manager) {
        this.manager = manager;
    }

    public void loadRecipes() {
        File folder = new File(plugin.getDataFolder(), "recipes");
        if (!folder.exists()) folder.mkdirs();

        new RecipeRemover();
        new RecipeGenerator(manager).registerAll();
        scanAndLoad(folder, new Gson());
    }

    private void scanAndLoad(File folder, Gson gson) {
        File[] files = folder.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                scanAndLoad(file, gson);
                continue;
            }

            if (!file.getName().endsWith(".json")) continue;

            try (FileReader reader = new FileReader(file)) {
                RecipeInstance recipe = gson.fromJson(reader, RecipeInstance.class);
                if (recipe != null && recipe.getId() != null && recipe.getResult() != null) {
                    manager.registerRecipe(recipe);
                }
            } catch (Exception e) {
                System.err.println("Błąd podczas ładowania: " + file.getAbsolutePath());
                e.printStackTrace();
            }
        }
    }

}
