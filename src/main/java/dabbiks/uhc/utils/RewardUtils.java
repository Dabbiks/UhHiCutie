package dabbiks.uhc.utils;

import dabbiks.uhc.player.data.persistent.PersistentData;
import dabbiks.uhc.player.data.persistent.PersistentDataManager;
import dabbiks.uhc.player.data.persistent.PersistentStats;
import dabbiks.uhc.player.data.session.SessionData;
import dabbiks.uhc.player.data.session.SessionDataManager;
import dabbiks.uhc.player.data.session.SessionStats;
import dabbiks.uhc.player.rank.RankManager;
import org.bukkit.entity.Player;

public class RewardUtils {

    private final int WIN_COINS = 750;
    private final int KILL_COINS = 100;
    private final int ASSIST_COINS = 25;

    private final int WIN_RANK_POINTS = 50;
    private final int KILL_RANK_POINTS = 20;
    private final int ASSIST_RANK_POINTS = 5;
    private final int DEATH_RANK_POINTS = -20;

    public double multiplier;

    public void win(Player player) {
        PersistentData persistentData = PersistentDataManager.getData(player.getUniqueId());
        SessionData sessionData = SessionDataManager.getData(player.getUniqueId());

        assert persistentData != null;
        persistentData.addStats(PersistentStats.COINS, (int) (WIN_COINS * multiplier));
        persistentData.addStats(PersistentStats.TOTALCOINS, (int) (WIN_COINS * multiplier));
        persistentData.addStats(PersistentStats.PLAYED, 1);
        persistentData.addStats(PersistentStats.SEASONPLAYED, 1);
        sessionData.addStats(SessionStats.WINCOINS, (int) (WIN_COINS * multiplier));

        double rankModifier = sessionData.getModifier();

        int pointsToChange = (int) (WIN_RANK_POINTS * rankModifier);

        RankManager.modifyRankPoints(player, pointsToChange);
        sessionData.addStats(SessionStats.RANKING, pointsToChange);

        PersistentDataManager.saveData(player.getUniqueId());
    }

    public void kill(Player player) {
        PersistentData persistentData = PersistentDataManager.getData(player.getUniqueId());
        SessionData sessionData = SessionDataManager.getData(player.getUniqueId());

        assert persistentData != null;
        persistentData.addStats(PersistentStats.COINS, (int) (KILL_COINS * multiplier));
        persistentData.addStats(PersistentStats.TOTALCOINS, (int) (KILL_COINS * multiplier));
        sessionData.addStats(SessionStats.KILLCOINS, (int) (KILL_COINS * multiplier));

        sessionData.addStats(SessionStats.KILLS, 1);

        double rankModifier = sessionData.getModifier();

        int pointsToChange = (int) (KILL_RANK_POINTS * rankModifier);

        RankManager.modifyRankPoints(player, pointsToChange);
        sessionData.addStats(SessionStats.RANKING, pointsToChange);

        PersistentDataManager.saveData(player.getUniqueId());
    }

    public void assist(Player player) {
        PersistentData persistentData = PersistentDataManager.getData(player.getUniqueId());
        SessionData sessionData = SessionDataManager.getData(player.getUniqueId());

        assert persistentData != null;
        persistentData.addStats(PersistentStats.COINS, (int) (ASSIST_COINS * multiplier));
        persistentData.addStats(PersistentStats.TOTALCOINS, (int) (ASSIST_COINS * multiplier));
        sessionData.addStats(SessionStats.KILLCOINS, (int) (ASSIST_COINS * multiplier));

        double rankModifier = sessionData.getModifier();

        int pointsToChange = (int) (ASSIST_RANK_POINTS * rankModifier);

        RankManager.modifyRankPoints(player, pointsToChange);
        sessionData.addStats(SessionStats.RANKING, pointsToChange);

        PersistentDataManager.saveData(player.getUniqueId());
    }

    public void death(Player player) {
        PersistentData persistentData = PersistentDataManager.getData(player.getUniqueId());
        SessionData sessionData = SessionDataManager.getData(player.getUniqueId());

        assert persistentData != null;
        persistentData.addStats(PersistentStats.PLAYED, 1);
        persistentData.addStats(PersistentStats.SEASONPLAYED, 1);

        if (persistentData.getStats().getOrDefault(PersistentStats.SEASONPLAYED, 0) < RankManager.MIN_GAMES_FOR_RANKED) return;

        double rankModifier = sessionData.getModifier();
        double deathModifier = 2.0 - rankModifier;

        int pointsToChange = (int) (DEATH_RANK_POINTS * deathModifier);

        RankManager.modifyRankPoints(player, pointsToChange);
        sessionData.addStats(SessionStats.RANKING, pointsToChange);

        PersistentDataManager.saveData(player.getUniqueId());
    }
}
