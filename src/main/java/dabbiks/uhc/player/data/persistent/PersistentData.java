package dabbiks.uhc.player.data.persistent;

import com.google.gson.annotations.Expose;
import dabbiks.uhc.Main;
import dabbiks.uhc.cosmetics.KillSound;
import dabbiks.uhc.cosmetics.PvpSword;
import dabbiks.uhc.cosmetics.particletrail.TrailData;
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

    @Expose
    private Map<String, Integer> championShards = new HashMap<>();

    // * COSMETICS

    @Expose
    private String killSound;
    @Expose
    private List<String> unlockedKillSounds = new ArrayList<>();

    @Expose
    private String pvpSword;
    @Expose
    private List<String> unlockedPvpSwords = new ArrayList<>();

    @Expose
    private String trail;
    @Expose
    private List<String> unlockedTrails = new ArrayList<>();

    // * CHESTS

    @Expose
    private int[] chests = new int[5];

    @Expose
    private int[] keys = new int[5];

    @Expose
    private int[] keyFragments = new int[5];

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

    public boolean isManager() {
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

    public void addUnlockedChampion(String champion) { unlockedChampions.add(champion);
        championLevel.put(champion, 1);
        championMastery.put(champion, 0);
    }

    public void addChampionShards(String champion, int amount) {
        championShards.put(champion, Math.min(championShards.getOrDefault(champion, 0)+amount, 10));
    }

    public int getChampionShards(String champion) {
        return championShards.getOrDefault(champion, 0);
    }

    // ? COSMETICS

    public KillSound getKillSound() {
        if (killSound == null) return null;
        return KillSound.valueOf(killSound);
    }
    public void setKillSound(KillSound killSound) { this.killSound = killSound.name(); }

    public void unlockKillSound(KillSound killSound) { unlockedKillSounds.add(killSound.name()); }
    public boolean hasKillSound(KillSound killSound) { return unlockedKillSounds.contains(killSound.name()); }

    public PvpSword getPvpSword() {
        if (pvpSword == null) return null;
        return PvpSword.valueOf(pvpSword);
    }
    public void setPvpSword(PvpSword pvpSword) { this.pvpSword = pvpSword.name(); }

    public void unlockPvpSword(PvpSword pvpSword) { unlockedPvpSwords.add(pvpSword.name()); }
    public boolean hasPvpSword(PvpSword pvpSword) { return unlockedPvpSwords.contains(pvpSword.name()); }

    public TrailData getTrail() {
        if (trail == null) return null;
        return Main.INSTANCE.getTrailManager().getTrailData(trail);
    }
    public void setTrail(TrailData trail) { this.trail = trail.getId(); }

    public void unlockTrail(TrailData trail) { unlockedKillSounds.add(trail.getId()); }
    public boolean hasTrail(TrailData trail) { return unlockedKillSounds.contains(trail.getId()); }

    // ? CHESTS

    public int getChests(int index) { return chests[index]; }
    public void setChests(int index, int amount) { chests[index] = amount; }
    public void addChests(int index, int amount) { chests[index] += amount; }

    public int getKeys(int index) { return keys[index]; }
    public void setKeys(int index, int amount) { keys[index] = amount; }
    public void addKeys(int index, int amount) { keys[index] += amount; }

    public int getKeyFragments(int index) { return keyFragments[index]; }
    public void setKeyFragments(int index, int amount) { keyFragments[index] = amount; }
    public void addKeyFragments(int index, int amount) { keyFragments[index] += amount; }

    /*-----------------------------------------------------------------------------------------------------------------*/

}