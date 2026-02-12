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

import static dabbiks.uhc.Main.*;

public class SessionData {

    /*-----------------------------------------------------------------------------------------------------------------*/

    // * DAMAGER

    public Player damager;
    public int damagerTime;
    public Map<Player, Integer> assists = new HashMap<>();

    // * RANK

    public int rankPRModifier;

    // * STATS

    private final Map<SessionStats, Integer> sessionStatsMap = new HashMap<>();

    // * TAGS

    private final EnumSet<SessionTags> activeTags = EnumSet.noneOf(SessionTags.class);
    private final Map<SessionTags, Integer> tagTasks = new HashMap<>();

    // * CHAT

    public String teamIcon = "";
    public String rankIcon = "";

    /*-----------------------------------------------------------------------------------------------------------------*/
    /*-----------------------------------------------------------------------------------------------------------------*/

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
        List<Player> players = new ArrayList<>();
        for (Map.Entry<Player, Integer> entry : assists.entrySet()) {
            if (timeU.getTime() - entry.getValue() > 60) assists.remove(entry.getKey());
            else players.add(entry.getKey());
        }

        return players;
    }

    // ? RANK

    public void setModifier(int modifier) {
        rankPRModifier = modifier;
    }

    public int getModifier() {
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

//    public void updatePlayerPrefix(Player player) {
//        PersistentData persistentData = PersistentDataManager.getData(player.getUniqueId());
//        if (persistentData == null) return;
//        if (persistentData.getPlayerRank() == null) {
//            persistentData.setPlayerRank(RankType.UNRANKED);
//        }
//        rankIcon = persistentData.getPlayerRank().getIcon();
//        String prefix = "";
//        if (!rankIcon.isEmpty()) prefix = prefix + rankIcon + " ";
//        if (!teamIcon.isEmpty()) prefix = prefix + teamIcon + " ";
//        if (persistentData.getIsManager()) prefix = prefix + "\uE088" + " ";
//        if (prefix.isEmpty()) // ! WIAD ERROR
//
//        tabManager.setPlayerTabPrefix(player, prefix);
//    }

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
