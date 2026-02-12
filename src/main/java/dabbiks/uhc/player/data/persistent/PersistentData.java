package dabbiks.uhc.player.data.persistent;

import com.google.gson.annotations.Expose;
import dabbiks.uhc.game.gameplay.items.recipes.data.RecipeInstance;
import dabbiks.uhc.player.rank.RankType;

import java.util.*;

public class PersistentData {

    /*-----------------------------------------------------------------------------------------------------------------*/

    @Expose
    private UUID uuid;

    @Expose
    private String name;

    @Expose
    private Map<PersistentStats, Integer> stats = new HashMap<>();

    @Expose
    private boolean isManager;

    @Expose
    private RankType rank;


    // * RECIPES

    @Expose
    private String recipeCategory;

    @Expose
    private RecipeInstance recipe;

    // * CHAMPIONS

    @Expose
    private String champion;

    @Expose
    private List<String> unlockedChampions = new ArrayList<>();

    @Expose
    private Map<String, Integer> championMastery = new HashMap<>();

    @Expose
    private Map<String, Integer> championLevel = new HashMap<>();

    /*-----------------------------------------------------------------------------------------------------------------*/
    /*-----------------------------------------------------------------------------------------------------------------*/

    public UUID getUUID() {
        return uuid;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getIsManager() {
        return isManager;
    }

    public RankType getRank() {
        return rank;
    }

    public void setRank(RankType rank) {
        this.rank = rank;
    }

    public Map<PersistentStats, Integer> getStats() {
        return stats;
    }

    public void addStats(PersistentStats stats, int value) {
        this.stats.put(stats, getStats().getOrDefault(stats, 0) + value);
    }

    public void setStats(PersistentStats stats, int number) {
        this.stats.put(stats, number);
    }

    public void removeStats(PersistentStats stats, int number) {
        this.stats.put(stats, getStats().getOrDefault(stats, 0) - number);
    }

    // ? RECIPES

    public String getRecipeCategory() { return recipeCategory; }

    public void setRecipeCategory(String recipeCategory) {
        this.recipeCategory = recipeCategory;
    }

    public RecipeInstance getRecipe() { return recipe; }

    public void setRecipe(RecipeInstance recipe) { this.recipe = recipe; }

    // ? CHAMPIONS

    public String getChampion() { return champion; }

    public void setChampion(String champion) { this.champion = champion; }

    public int getChampionMastery(String champion) { return championMastery.getOrDefault(champion, 0); }

    public void addChampionMastery(String champion, int amount) { championMastery.put(champion, championMastery.getOrDefault(champion, 0) + amount); }

    public int getChampionLevel(String champion) { return championLevel.getOrDefault(champion, 0); }

    public void setChampionLevel(String champion, int level) { championLevel.put(champion, level); }

    public List<String> getUnlockedChampions() { return unlockedChampions; }

    public boolean hasUnlockedChampion(String champion) { return unlockedChampions.contains(champion); }

    public void addUnlockedChampion(String champion) { unlockedChampions.add(champion); }

    /*-----------------------------------------------------------------------------------------------------------------*/

}
