package dabbiks.uhc;

import dabbiks.uhc.game.gameplay.items.ItemUtils;
import dabbiks.uhc.game.gameplay.items.recipes.listener.RecipeLimitTracker;
import dabbiks.uhc.game.gameplay.items.recipes.listener.RecipeListener;
import dabbiks.uhc.game.gameplay.items.recipes.loader.RecipeLoader;
import dabbiks.uhc.game.gameplay.items.recipes.loader.RecipeManager;
import dabbiks.uhc.game.gameplay.items.stations.anvil.AnvilManager;
import dabbiks.uhc.game.gameplay.items.stations.table.TableManager;
import dabbiks.uhc.game.world.events.WorldBorder;
import dabbiks.uhc.player.data.persistent.PersistentDataJson;
import dabbiks.uhc.utils.*;
import dabbiks.uhc.utils.managers.AttributeManager;
import dabbiks.uhc.utils.managers.IndicatorManager;
import dabbiks.uhc.utils.managers.TabManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    public static Plugin plugin;
    public static Main INSTANCE;

    public static MessageUtils messageU;
    public static PlayerUtils playerU;
    public static SoundUtils soundU;
    public static StateUtils stateU;
    public static SymbolUtils symbolU;
    public static TimeUtils timeU;
    public static TitleUtils titleU;
    public static ItemUtils itemU;
    public static PlayerListUtils playerListU;

    public static TabManager tabManager;
    public static AttributeManager attributeManager;
    public static IndicatorManager indicatorManager;

    public static PersistentDataJson persistentDataJson;

    private RecipeManager recipeManager;
    private RecipeLimitTracker recipeLimitTracker;
    private WorldBorder worldBorder;

    @Override
    public void onEnable() {

        plugin = this;
        INSTANCE = this;

        messageU = new MessageUtils();
        playerU = new PlayerUtils();
        soundU = new SoundUtils();
        stateU = new StateUtils();
        symbolU = new SymbolUtils();
        timeU = new TimeUtils();
        titleU = new TitleUtils();
        itemU = new ItemUtils();
        playerListU = new PlayerListUtils();

        recipeManager = new RecipeManager();
        recipeLimitTracker = new RecipeLimitTracker();
        new RecipeLoader(recipeManager).loadRecipes();
        Bukkit.getPluginManager().registerEvents(new RecipeListener(recipeManager, recipeLimitTracker), this);
        Bukkit.getPluginManager().registerEvents(new AnvilManager(), this);
        Bukkit.getPluginManager().registerEvents(new TableManager(), this);

        worldBorder = new WorldBorder();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public WorldBorder getWorldBorder() {
        return worldBorder;
    }
    public RecipeManager getRecipeManager() { return recipeManager; }
}
