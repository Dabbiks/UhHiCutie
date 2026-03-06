package dabbiks.uhc.player.data.session;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

import static dabbiks.uhc.Main.*;

import dabbiks.uhc.game.gameplay.items.recipes.data.RecipeInstance;
import dabbiks.uhc.game.gameplay.items.recipes.loader.RecipeType;

public class SessionData {

    /*-----------------------------------------------------------------------------------------------------------------*/

    // * DAMAGER
    private Player damager;
    private int damagerTime;
    private Map<Player, Integer> assists = new HashMap<>();

    // * RANK
    private double rankPRModifier;

    // * STATS
    private final Map<SessionStats, Integer> sessionStatsMap = new HashMap<>();

    // * TAGS
    private final EnumSet<SessionTags> activeTags = EnumSet.noneOf(SessionTags.class);
    private final Map<SessionTags, Integer> tagTasks = new HashMap<>();

    // * CHAT
    private String teamIcon = "";
    private String rankIcon = "";

    // * ELYTRA
    private int elytraCharges = 10;

    // * RECIPES
    private RecipeType lastRecipeCategory = RecipeType.WEAPON;
    private RecipeInstance lastSelectedRecipe = null;

    // * STREAK
    private long lastKillTime = 0;
    private int killStreak = 0;

    /*-----------------------------------------------------------------------------------------------------------------*/
    /*-----------------------------------------------------------------------------------------------------------------*/

    public RecipeType getLastRecipeCategory() {
        return lastRecipeCategory;
    }

    public void setLastRecipeCategory(RecipeType lastRecipeCategory) {
        this.lastRecipeCategory = lastRecipeCategory;
    }

    public RecipeInstance getLastSelectedRecipe() {
        return lastSelectedRecipe;
    }

    public void setLastSelectedRecipe(RecipeInstance lastSelectedRecipe) {
        this.lastSelectedRecipe = lastSelectedRecipe;
    }

    public long getLastKillTime() {
        return lastKillTime;
    }

    public void setLastKillTime(long lastKillTime) {
        this.lastKillTime = lastKillTime;
    }

    public int getKillStreak() {
        return killStreak;
    }

    public void setKillStreak(int killStreak) {
        this.killStreak = killStreak;
    }

    // ? ELYTRA

    public int getMaxElytraCharges() {
        return hasTag(SessionTags.MORE_FUEL) ? 12 : 10;
    }

    public int getElytraCharges() {
        return Math.min(elytraCharges, getMaxElytraCharges());
    }

    public void addElytraCharges(int amount) {
        this.elytraCharges = Math.min(this.elytraCharges + amount, getMaxElytraCharges());
    }

    public void removeElytraCharges(int amount) {
        this.elytraCharges = Math.max(this.elytraCharges - amount, 0);
    }

    public boolean consumeElytraCharge() {
        if (this.elytraCharges > 0) {
            this.elytraCharges--;
            return true;
        }
        return false;
    }

    // ? DAMAGER
    public void setDamager(Player victim, Player attacker) {
        if (damager != null && damager != attacker) {
            assists.put(damager, damagerTime);
            damagerTime = (int) timeU.getTime();
            damager = attacker;
            assists.remove(attacker);
        } else {
            damager = attacker;
            damagerTime = (int) timeU.getTime();
        }
    }

    public Player getDamager() {
        return damager;
    }

    public int getDamagerTime() {
        return damagerTime;
    }

    public List<Player> getAssists() {
        long currentTime = timeU.getTime();

        assists.entrySet().removeIf(entry ->
                (currentTime - entry.getValue() > 60) || !entry.getKey().isOnline()
        );

        return new ArrayList<>(assists.keySet());
    }

    // ? RANK
    public void setModifier(double modifier) {
        rankPRModifier = modifier;
    }

    public double getModifier() {
        return rankPRModifier;
    }

    // ? STATS
    public void addStats(SessionStats sessionStats, Integer value) {
        sessionStatsMap.put(sessionStats, sessionStatsMap.getOrDefault(sessionStats, 0) + value);
    }

    public int getStats(SessionStats sessionStats) {
        return sessionStatsMap.getOrDefault(sessionStats, 0);
    }

    // ? CHAT
    public void setTeamIcon(String teamIcon) {
        this.teamIcon = teamIcon;
    }

    public String getTeamIcon() {
        return teamIcon;
    }

    // ? TAGS
    public void addTag(SessionTags tag) {
        activeTags.add(tag);
        Integer taskId = tagTasks.remove(tag);
        if (taskId != null) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
    }

    public void removeTag(SessionTags tag) {
        activeTags.remove(tag);
        Integer taskId = tagTasks.remove(tag);
        if (taskId != null) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
    }

    public boolean hasTag(SessionTags tag) {
        return activeTags.contains(tag);
    }

    public void addTagFor(Player player, SessionTags tag, long ticks) {
        activeTags.add(tag);
        Integer existingTask = tagTasks.remove(tag);
        if (existingTask != null) {
            Bukkit.getScheduler().cancelTask(existingTask);
        }

        int taskId = Bukkit.getScheduler().runTaskLater(INSTANCE, () -> removeTag(tag), ticks).getTaskId();
        tagTasks.put(tag, taskId);
    }

    public void clearTags() {
        for (int taskId : tagTasks.values()) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
        tagTasks.clear();
        activeTags.clear();
    }
}