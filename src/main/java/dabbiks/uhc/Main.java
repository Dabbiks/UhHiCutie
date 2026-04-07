package dabbiks.uhc;

import dabbiks.uhc.commands.RequiredPlayersCommand;
import dabbiks.uhc.commands.SetPieceCommand;
import dabbiks.uhc.commands.TeamSizeCommand;
import dabbiks.uhc.cosmetics.chest.MysteryChestListener;
import dabbiks.uhc.game.gameplay.champions.listeners.AlchemistListener;
import dabbiks.uhc.game.gameplay.champions.listeners.BrewingListener;
import dabbiks.uhc.game.gameplay.champions.listeners.PyromaniacListener;
import dabbiks.uhc.game.gameplay.damage.listeners.*;
import dabbiks.uhc.game.gameplay.items.ItemUtils;
import dabbiks.uhc.game.gameplay.items.conversion.ConversionManager;
import dabbiks.uhc.game.gameplay.items.recipes.listener.RecipeLimitTracker;
import dabbiks.uhc.game.gameplay.items.recipes.listener.RecipeListener;
import dabbiks.uhc.game.gameplay.items.recipes.loader.RecipeLoader;
import dabbiks.uhc.game.gameplay.items.recipes.loader.RecipeManager;
import dabbiks.uhc.game.gameplay.items.stations.anvil.AnvilManager;
import dabbiks.uhc.game.gameplay.items.stations.grindstone.GrindstoneManager;
import dabbiks.uhc.game.gameplay.items.stations.smithingtable.SmithingTableManager;
import dabbiks.uhc.game.gameplay.items.stations.table.EnchantingTableManager;
import dabbiks.uhc.game.gameplay.mobs.NautilusHoverManager;
import dabbiks.uhc.game.gameplay.recipes.BurningAttackLogic;
import dabbiks.uhc.game.gameplay.recipes.FlareListener;
import dabbiks.uhc.game.gameplay.recipes.UpgradeCrystalLogic;
import dabbiks.uhc.game.gameplay.setpieces.SetPieceFileManager;
import dabbiks.uhc.game.gameplay.setpieces.SetPiecePickUpHandler;
import dabbiks.uhc.lobby.easter.EasterEggManager;
import dabbiks.uhc.lobby.easter.EasterLocationData;
import dabbiks.uhc.lobby.stock.StockData;
import dabbiks.uhc.lobby.stock.StockInteract;
import dabbiks.uhc.game.teams.*;
import dabbiks.uhc.game.world.WorldGen;
import dabbiks.uhc.game.world.events.CenterBuilderManager;
import dabbiks.uhc.game.world.events.Cobweb;
import dabbiks.uhc.game.world.events.LavaCollect;
import dabbiks.uhc.game.world.events.WorldBorder;
import dabbiks.uhc.lobby.LobbyItems;
import dabbiks.uhc.lobby.SpawnProtector;
import dabbiks.uhc.player.Chat;
import dabbiks.uhc.player.data.persistent.PersistentDataJson;
import dabbiks.uhc.player.events.Fishing;
import dabbiks.uhc.player.events.Mining;
import dabbiks.uhc.player.events.drop.FoodDrop;
import dabbiks.uhc.player.events.drop.MobDrop;
import dabbiks.uhc.player.traffic.JoinEvent;
import dabbiks.uhc.player.traffic.QuitEvent;
import dabbiks.uhc.tasks.TaskManager;
import dabbiks.uhc.utils.*;
import dabbiks.uhc.utils.managers.AttributeManager;
import dabbiks.uhc.utils.managers.IndicatorManager;
import dabbiks.uhc.utils.managers.PrefixManager;
import dabbiks.uhc.utils.managers.TabManager;
import fr.mrmicky.fastinv.FastInvManager;
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
    public static RewardUtils rewardU;
    public static FireworkUtils fireworkU;

    public static TabManager tabManager;
    public static AttributeManager attributeManager;
    public static IndicatorManager indicatorManager;

    public static StockData stockData;
    public static PersistentDataJson persistentDataJson;
    private RecipeManager recipeManager;
    private RecipeLimitTracker recipeLimitTracker;
    private WorldBorder worldBorder;
    private TeamManager teamManager;
    private PrefixManager prefixManager;
    public SetPieceFileManager gravesFileManager;
    public SetPiecePickUpHandler gravePickupHandler;

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
        rewardU = new RewardUtils();
        fireworkU = new FireworkUtils();

        tabManager = new TabManager();
        attributeManager = new AttributeManager();
        indicatorManager = new IndicatorManager();
        teamManager = new TeamManager();
        prefixManager = new PrefixManager();

        persistentDataJson = new PersistentDataJson();
        worldBorder = new WorldBorder();
        recipeManager = new RecipeManager();
        recipeLimitTracker = new RecipeLimitTracker();
        gravesFileManager = new SetPieceFileManager(getDataFolder());
        gravePickupHandler = new SetPiecePickUpHandler();

        new RecipeLoader(recipeManager).loadRecipes();

        new NautilusHoverManager(this);
        FastInvManager.register(this);
        stockData = new StockData(getDataFolder());
        Bukkit.getPluginManager().registerEvents(new StockInteract(), this);

        Bukkit.getPluginManager().registerEvents(new RecipeListener(recipeManager, recipeLimitTracker), this);
        Bukkit.getPluginManager().registerEvents(new AnvilManager(), this);
        Bukkit.getPluginManager().registerEvents(new EnchantingTableManager(), this);
        Bukkit.getPluginManager().registerEvents(new ConversionManager(), this);
        Bukkit.getPluginManager().registerEvents(new SmithingTableManager(), this);
        Bukkit.getPluginManager().registerEvents(new EasterEggManager(), this);
        Bukkit.getPluginManager().registerEvents(new GrindstoneManager(), this);
        Bukkit.getPluginManager().registerEvents(new AlchemistListener(), this);

        Bukkit.getPluginManager().registerEvents(new MeleeHit(), this);
        Bukkit.getPluginManager().registerEvents(new ProjectileHit(), this);
        Bukkit.getPluginManager().registerEvents(new ProjectileLaunch(), this);
        Bukkit.getPluginManager().registerEvents(new BoomerangLaunch(), this);

        Bukkit.getPluginManager().registerEvents(new TeamClick(), this);
        Bukkit.getPluginManager().registerEvents(new LobbyItems(), this);
        Bukkit.getPluginManager().registerEvents(new Cobweb(), this);
        Bukkit.getPluginManager().registerEvents(new LavaCollect(), this);
        Bukkit.getPluginManager().registerEvents(new ParryingBlocker(), this);
        Bukkit.getPluginManager().registerEvents(new SpawnProtector(), this);
        Bukkit.getPluginManager().registerEvents(new MysteryChestListener(), this);
        Bukkit.getPluginManager().registerEvents(new VolcanicMobDrop(), this);
        Bukkit.getPluginManager().registerEvents(new CenterBuilderManager(), this);
        Bukkit.getPluginManager().registerEvents(new MobDrop(), this);
        Bukkit.getPluginManager().registerEvents(new BrewingListener(), this);
        Bukkit.getPluginManager().registerEvents(new PyromaniacListener(), this);
        Bukkit.getPluginManager().registerEvents(new BurningAttackLogic(), this);
        Bukkit.getPluginManager().registerEvents(new FlareListener(), this);
        Bukkit.getPluginManager().registerEvents(new UpgradeCrystalLogic(), this);
        Bukkit.getPluginManager().registerEvents(gravePickupHandler, this);

        Bukkit.getPluginManager().registerEvents(new Mining(), this);
        Bukkit.getPluginManager().registerEvents(new FoodDrop(), this);
        Bukkit.getPluginManager().registerEvents(new Fishing(), this);

        Bukkit.getPluginManager().registerEvents(new JoinEvent(), this);
        Bukkit.getPluginManager().registerEvents(new QuitEvent(), this);
        Bukkit.getPluginManager().registerEvents(new Chat(), this);

        getCommand("setteamsize").setExecutor(new TeamSizeCommand());
        getCommand("setminplayers").setExecutor(new RequiredPlayersCommand());
        getCommand("kara").setExecutor(new dabbiks.uhc.commands.PunishmentCommand());
        getCommand("skrzynka").setExecutor(new dabbiks.uhc.commands.ChestCommand());
        getCommand("createsetpiece").setExecutor(new SetPieceCommand());

        dabbiks.uhc.player.punishments.PunishmentManager.init();

        EasterLocationData.load();
        EasterEggManager.clearAllEggs();
        EasterEggManager.spawnRandomEgg();

        Bukkit.getScheduler().runTaskLater(this, () -> {
            teamManager.deleteTeams();
            new TeamInitializer();
        }, 20L);

        new TaskManager().run();
        Bukkit.getScheduler().runTaskLater(this, WorldGen::createWorld, 10L);
    }

    @Override
    public void onDisable() {
        teamManager.deleteTeams();
        dabbiks.uhc.player.punishments.PunishmentManager.save();
    }

    public WorldBorder getWorldBorder() {
        return worldBorder;
    }
    public RecipeManager getRecipeManager() { return recipeManager; }
    public RecipeLimitTracker getRecipeLimitTracker() { return recipeLimitTracker; }
    public TeamManager getTeamManager() { return teamManager; }
    public PrefixManager getPrefixManager() { return prefixManager; }
}