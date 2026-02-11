package dabbiks.uhc.player.data.session;

import dabbiks.uhc.game.configs.SegmentConfig;
import dabbiks.uhc.player.data.persistent.PersistentData;
import dabbiks.uhc.player.data.persistent.PersistentDataManager;
import dabbiks.uhc.player.rank.RankType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static dabbiks.uhc.Main.tabManager;

public class SessionData {

    public boolean useAdvancedLore = true;

    public void setUseAdvancedLore(boolean useAdvancedLore) {
        this.useAdvancedLore = useAdvancedLore;
    }

    public boolean getUseAdvancedLore() {
        return useAdvancedLore;
    }

    // === DAMAGE ===

    public Player lastDamager = null;
    public int lastDamagerSegment = 0;
    public Map<Player, Integer> previousDamager = new HashMap<>();

    public void setLastDamager(Player victim, Player attacker) {
        if (lastDamager != null && lastDamager != attacker) {
            previousDamager.put(lastDamager, lastDamagerSegment);
            lastDamagerSegment = SegmentConfig.actualSegment;
            lastDamager = attacker;
            previousDamager.remove(attacker);
        } else {
            lastDamager = attacker;
            lastDamagerSegment = SegmentConfig.actualSegment;
        }
    }

    public Player getLastDamager() {
        return lastDamager;
    }

    public int getLastDamagerSegment() {
        return lastDamagerSegment;
    }

    public List<Player> getLastAssists() {
        List<Player> lastAssists = new ArrayList<>();
        for (Player player : previousDamager.keySet()) {
            int segment = previousDamager.get(player);
            if (SegmentConfig.actualSegment - segment <= 1) {
                lastAssists.add(player);
            }
        }
        return lastAssists;
    }

    public int lastDamageTime = 0;

    public void setLastDamageTime(int time) {
        lastDamageTime = time;
    }

    public int getLastDamageTime() {
        return lastDamageTime;
    }

    // === RANK ===

    public int rankPRModifier = 0;
    private Map<SessionStats, Integer> sessionStatsMap = new HashMap<>();

    public void setRankPRModifier(int modifier) {
        rankPRModifier = modifier;
    }

    public int getRankPRModifier() {
        return rankPRModifier;
    }

    public void addGameSessionStats(SessionStats sessionStats, Integer value) {
        sessionStatsMap.put(sessionStats, sessionStatsMap.getOrDefault(sessionStats, 0) + value);
    }

    public int getGameSessionStats(SessionStats sessionStats) {
        return sessionStatsMap.getOrDefault(sessionStats, 0);
    }

    // === CHAT ===

    public String teamIcon = "";
    public String rankIcon = "";

    public void setTeamIcon(String teamIcon) {
        this.teamIcon = teamIcon;
    }

    public String getTeamIcon() {
        return teamIcon;
    }

    public void updatePlayerPrefix(Player player) {
        PersistentData persistentData = PersistentDataManager.getData(player.getUniqueId());
        if (persistentData == null) return;
        if (persistentData.getPlayerRank() == null) {
            persistentData.setPlayerRank(RankType.UNRANKED);
        }
        rankIcon = persistentData.getPlayerRank().getIcon();
        String prefix = "";
        if (!rankIcon.isEmpty()) prefix = prefix + rankIcon + " ";
        if (!teamIcon.isEmpty()) prefix = prefix + teamIcon + " ";
        if (persistentData.getIsManager()) prefix = prefix + "\uE088" + " ";
        if (prefix.isEmpty()) // ! WIAD ERROR

        tabManager.setPlayerTabPrefix(player, prefix);
    }

    // === TAGI ===

    private EnumSet<SessionTags> activeTags = EnumSet.noneOf(SessionTags.class);
    private Map<SessionTags, Integer> tagTasks = new HashMap<>();

    public void addTag(SessionTags tag) {
        activeTags.add(tag);
    }

    public void removeTag(SessionTags tag) {
        activeTags.remove(tag);
        Integer taskId = tagTasks.remove(tag);
        if (taskId != null) Bukkit.getScheduler().cancelTask(taskId);
    }

    public boolean hasTag(SessionTags tag) {
        return activeTags.contains(tag);
    }

    public void addTagFor(Player player, SessionTags tag, long ticks) {
        addTag(tag);
        int taskId = Bukkit.getScheduler().runTaskLater(
                Bukkit.getPluginManager().getPlugin("uHC"),
                () -> {
                    SessionData data = SessionDataManager.getData(player.getUniqueId());
                    data.removeTag(tag);
                },
                ticks
        ).getTaskId();

        Integer oldTask = tagTasks.put(tag, taskId);
        if (oldTask != null) Bukkit.getScheduler().cancelTask(oldTask);
    }

    public void clearTags() {
        for (int taskId : tagTasks.values()) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
        activeTags.clear();
        tagTasks.clear();
    }

    // === MUZYK ===

    private List<ItemStack> discQueue = new ArrayList<>();

    public boolean hasSlotsInDiscQueue() {
        if (discQueue.size() < 3) {
            return true;
        }
        return false;
    }

    public int slotsInDiscQueue() {
        return discQueue.size();
    }

    public void addToDiscQueue(ItemStack itemStack) {
        discQueue.add(itemStack);
    }

    public void removeFromDiscQueue(ItemStack itemStack) {
        discQueue.remove(itemStack);
    }

    public ItemStack rotateDiscQueue() {
        if (discQueue.size() != 2 && discQueue.size() != 3) { return discQueue.getFirst(); }
        ItemStack item = discQueue.removeLast();
        discQueue.addFirst(item);

        return discQueue.getFirst();
    }

    // === OSWOJONE PSY ===

    private final List<Wolf> tamedWolves = new ArrayList<>();

    public void addTamedWolf(Wolf wolf) {
        if (!tamedWolves.contains(wolf)) tamedWolves.add(wolf);
    }

    public void removeTamedWolf(Wolf wolf) {
        tamedWolves.remove(wolf);
    }

    public List<Wolf> getTamedWolves() {
        return tamedWolves;
    }

    public int getTamedWolfCount() {
        return tamedWolves.size();
    }

    // === ZEGARMISTRZ ===

    private final List<Location> lastLocations = new ArrayList<>();
    private boolean isRewinding;
    private int segmentDamageBuff = 0;
    private int timeJumpCooldown = 0;

    public void manageLastLocations(Player player) {
        lastLocations.add(player.getLocation());
        if (lastLocations.size() > 20) lastLocations.removeFirst();
    }

    public List<Location> getLastLocations() {
        return lastLocations;
    }

    public void setIsRewinding(boolean bool) {
        isRewinding = bool;
    }

    public boolean getIsRewinding() {
        return isRewinding;
    }

    public void setSegmentDamageBuff(int i) {
        segmentDamageBuff = i;
    }

    public int getSegmentDamageBuff() {
        return segmentDamageBuff;
    }

    public void setTimeJumpCooldown(int i) {
        timeJumpCooldown = i;
    }

    public int getTimeJumpCooldown() {
        return timeJumpCooldown;
    }

}
