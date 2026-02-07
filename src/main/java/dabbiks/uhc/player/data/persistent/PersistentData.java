package dabbiks.uhc.player.data.persistent;

import com.google.gson.annotations.Expose;
import dabbiks.uhc.player.rank.RankTypes;

import java.util.*;

public class PersistentData {

    /*-----------------------------------------------------------------------------------------------------------------*/

    @Expose
    private UUID playerId;

    @Expose
    private String playerName;

    @Expose
    private Map<PersistentStats, Integer> persistentStatsMap = new HashMap<>();

    @Expose
    private boolean isManager;

    //  RANGA

    @Expose
    private RankTypes playerRank;

    // WYBRANE OPCJE W PRZEPISACH

    @Expose
    private String playerLastRecipeCategoryType;

    @Expose
    private Integer playerLastCrockpotRecipeIndex;

    @Expose
    private UHCRecipe playerLastRecipeType;

    // ODBLOKOWANE

    @Expose
    private List<UHCRecipe> playerUnlockedRecipes = new ArrayList<>();

    // USTAWIENIA



    /*-----------------------------------------------------------------------------------------------------------------*/
    /*-----------------------------------------------------------------------------------------------------------------*/

    public UUID getPlayerId() {
        return playerId;
    }

    public void setPlayerId(UUID playerId) {
        this.playerId = playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    // RANGA

    public RankTypes getPlayerRank() {
        return playerRank;
    }

    public void setPlayerRank(RankTypes playerRank) {
        this.playerRank = playerRank;
    }

    // STATYSTYKI

    public Map<PersistentStats, Integer> getPersistentStats() {
        return persistentStatsMap;
    }

    public void addPersistentStats(PersistentStats persistentStats, int number) {
        persistentStatsMap.put(persistentStats, getPersistentStats().getOrDefault(persistentStats, 0) + number);
    }

    public void setPersistentStats(PersistentStats persistentStats, int number) {
        persistentStatsMap.put(persistentStats, number);
    }

    public void removePersistentStats(PersistentStats persistentStats, int number) {
        persistentStatsMap.put(persistentStats, getPersistentStats().getOrDefault(persistentStats, 0) - number);
    }

    // KLASY

    @Expose
    private String playerClassName;
    @Expose
    private List<String> playerUnlockedClassNames = new ArrayList<>();

    public String getPlayerClassName() {
        return playerClassName;
    }

    public void setPlayerClassName(String className) {
        playerClassName = className;
    }

    public List<String> getPlayerUnlockedClassNames() {
        return playerUnlockedClassNames;
    }

    public void addPlayerUnlockedClassName(String className) {
        if (!playerUnlockedClassNames.contains(className))
            playerUnlockedClassNames.add(className);
    }

    public void removePlayerUnlockedClassName(String className) {
        playerUnlockedClassNames.remove(className);
    }

    // PRZEPISY

    public String getPlayerLastRecipeCategoryType() {return playerLastRecipeCategoryType;}

    public void setPlayerLastRecipeCategoryType(String categoryType) {
        playerLastRecipeCategoryType = categoryType;
    }

    public UHCRecipe getPlayerLastRecipeType() {return playerLastRecipeType;}

    public void setPlayerLastRecipeType(UHCRecipe recipeType) {
        playerLastRecipeType = recipeType;
    }

    public List<UHCRecipe> getPlayerUnlockedRecipes() {return playerUnlockedRecipes;}

    public void addPlayerUnlockedRecipe(UHCRecipe recipeType) {
        playerUnlockedRecipes.add(recipeType);
    }

    public void removePlayerUnlockedRecipe(UHCRecipe recipeType) {
        playerUnlockedRecipes.remove(recipeType);
    }

    public Integer getPlayerLastCrockpotRecipeIndex() {
        return playerLastCrockpotRecipeIndex;
    }

    public void setPlayerLastCrockpotRecipeIndex(Integer playerLastCrockpotRecipe) {
        this.playerLastCrockpotRecipeIndex = playerLastCrockpotRecipe;
    }

    // DŹWIĘKI ZABÓJSTWA

    @Expose private String playerSelectedKillSound;
    @Expose private List<String> unlockedKillSounds = new ArrayList<>();

    public String getPlayerSelectedKillSound() { return playerSelectedKillSound; }
    public void setPlayerSelectedKillSound(String sound) { this.playerSelectedKillSound = sound; }

    public List<String> getUnlockedKillSounds() { return unlockedKillSounds; }
    public void addUnlockedKillSound(String sound) {
        if (!unlockedKillSounds.contains(sound)) unlockedKillSounds.add(sound);
    }

    /*-----------------------------------------------------------------------------------------------------------------*/

    @Expose private boolean inviteReward;

    public boolean getInviteReward() { return inviteReward; }
    public void setInviteReward(boolean bool) { inviteReward = bool; }

    /*-----------------------------------------------------------------------------------------------------------------*/

    public boolean getIsManager() {
        return isManager;
    }

    /*-----------------------------------------------------------------------------------------------------------------*/

    @Expose private int dropMessagesStyle;
    @Expose private boolean disableTips;
    @Expose private boolean enableGamma;

    public void setDropMessagesStyle(int i) {
        dropMessagesStyle = i;
    }
    public int getDropMessagesStyle() {
        return dropMessagesStyle;
    }

    public void setDisableTips(boolean bool) {
        disableTips = bool;
    }
    public boolean getDisableTips() {
        return disableTips;
    }
    public void setEnableGamma(boolean bool) {
        enableGamma = bool;
    }
    public boolean getEnableGamma() {
        return enableGamma;
    }

    /*-----------------------------------------------------------------------------------------------------------------*/

}
