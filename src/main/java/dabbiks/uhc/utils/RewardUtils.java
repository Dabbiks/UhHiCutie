package dabbiks.uhc.utils;

import dabbiks.uhc.player.data.persistent.PersistentData;
import dabbiks.uhc.player.data.persistent.PersistentDataManager;
import dabbiks.uhc.player.data.persistent.PersistentStats;
import dabbiks.uhc.player.data.session.SessionData;
import dabbiks.uhc.player.data.session.SessionDataManager;
import dabbiks.uhc.player.data.session.SessionStats;
import dabbiks.uhc.player.rank.RankManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static dabbiks.uhc.Main.symbolU;

public class RewardUtils {

    private final int WIN_COINS = 1500;
    private final int KILL_COINS = 200;
    private final int ASSIST_COINS = 50;

    private final int WIN_RANK_POINTS = 50;
    private final int KILL_RANK_POINTS = 22;
    private final int ASSIST_RANK_POINTS = 5;
    private final int DEATH_RANK_POINTS = -18;

    public double multiplier = 1;

    public void win(Player player) {
        PersistentData persistentData = PersistentDataManager.getData(player.getUniqueId());
        SessionData sessionData = SessionDataManager.getData(player.getUniqueId());

        assert persistentData != null;
        persistentData.addStats(PersistentStats.COINS, (int) (WIN_COINS * multiplier));
        persistentData.addStats(PersistentStats.TOTAL_COINS, (int) (WIN_COINS * multiplier));
        persistentData.addStats(PersistentStats.PLAYED, 1);
        persistentData.addStats(PersistentStats.SEASON_PLAYED, 1);
        persistentData.addStats(PersistentStats.WINS, 1);
        persistentData.addStats(PersistentStats.SEASON_WINS, 1);
        sessionData.addStats(SessionStats.WINCOINS, (int) (WIN_COINS * multiplier));

        double rankModifier = sessionData.getModifier();

        int pointsToChange = (int) (WIN_RANK_POINTS * rankModifier);

        RankManager.modifyRankPoints(player, pointsToChange);
        sessionData.addStats(SessionStats.RANKING, pointsToChange);

        player.sendMessage("§f" + (WIN_COINS * multiplier) + symbolU.SCOREBOARD_COIN + " §8(Wygrana)");
        player.sendMessage("§6" + (int) (WIN_RANK_POINTS * rankModifier) + "PR §8(Wygrana)");

        PersistentDataManager.saveData(player.getUniqueId());
    }

    public void kill(Player player) {
        PersistentData persistentData = PersistentDataManager.getData(player.getUniqueId());
        SessionData sessionData = SessionDataManager.getData(player.getUniqueId());

        assert persistentData != null;
        persistentData.addStats(PersistentStats.COINS, (int) (KILL_COINS * multiplier));
        persistentData.addStats(PersistentStats.TOTAL_COINS, (int) (KILL_COINS * multiplier));
        persistentData.addStats(PersistentStats.KILLS, 1);
        persistentData.addStats(PersistentStats.SEASON_KILLS, 1);
        sessionData.addStats(SessionStats.KILLCOINS, (int) (KILL_COINS * multiplier));

        sessionData.addStats(SessionStats.KILLS, 1);

        double rankModifier = sessionData.getModifier();

        int pointsToChange = (int) (KILL_RANK_POINTS * rankModifier);

        RankManager.modifyRankPoints(player, pointsToChange);
        sessionData.addStats(SessionStats.RANKING, pointsToChange);

        player.sendMessage("§f" + (KILL_COINS * multiplier) + symbolU.SCOREBOARD_COIN + " §8(Zabójstwo)");
        player.sendMessage("§6" + (int) (KILL_RANK_POINTS * rankModifier) + "PR §8(Zabójstwo)");

        PersistentDataManager.saveData(player.getUniqueId());
    }

    public void assist(Player player) {
        PersistentData persistentData = PersistentDataManager.getData(player.getUniqueId());
        SessionData sessionData = SessionDataManager.getData(player.getUniqueId());

        assert persistentData != null;
        persistentData.addStats(PersistentStats.COINS, (int) (ASSIST_COINS * multiplier));
        persistentData.addStats(PersistentStats.TOTAL_COINS, (int) (ASSIST_COINS * multiplier));
        persistentData.addStats(PersistentStats.ASSISTS, 1);
        persistentData.addStats(PersistentStats.SEASON_ASSISTS, 1);
        sessionData.addStats(SessionStats.KILLCOINS, (int) (ASSIST_COINS * multiplier));

        double rankModifier = sessionData.getModifier();

        int pointsToChange = (int) (ASSIST_RANK_POINTS * rankModifier);

        RankManager.modifyRankPoints(player, pointsToChange);
        sessionData.addStats(SessionStats.RANKING, pointsToChange);

        player.sendMessage("§f" + (ASSIST_COINS * multiplier) + symbolU.SCOREBOARD_COIN + " §8(Asysta)");
        player.sendMessage("§6" + (int) (ASSIST_RANK_POINTS * rankModifier) + "PR §8(Asysta)");

        PersistentDataManager.saveData(player.getUniqueId());
    }

    public void death(Player player) {
        PersistentData persistentData = PersistentDataManager.getData(player.getUniqueId());
        SessionData sessionData = SessionDataManager.getData(player.getUniqueId());

        assert persistentData != null;
        persistentData.addStats(PersistentStats.PLAYED, 1);
        persistentData.addStats(PersistentStats.SEASON_PLAYED, 1);

        if (persistentData.getStats().getOrDefault(PersistentStats.SEASON_PLAYED, 0) < RankManager.MIN_GAMES_FOR_RANKED) return;

        double rankModifier = sessionData.getModifier();
        double deathModifier = 2.0 - rankModifier;

        int pointsToChange = (int) (DEATH_RANK_POINTS * deathModifier);

        RankManager.modifyRankPoints(player, pointsToChange);
        sessionData.addStats(SessionStats.RANKING, pointsToChange);

        player.sendMessage("§c" + (int) (DEATH_RANK_POINTS * deathModifier) + "PR §8(Śmierć)");

        PersistentDataManager.saveData(player.getUniqueId());
    }

    public void playing(Player player) {
        PersistentData persistentData = PersistentDataManager.getData(player.getUniqueId());
        SessionData sessionData = SessionDataManager.getData(player.getUniqueId());

        assert persistentData != null;
        persistentData.addStats(PersistentStats.COINS, 2);
        persistentData.addStats(PersistentStats.TOTAL_COINS, 2);
        sessionData.addStats(SessionStats.TIMECOINS, 2);

        PersistentDataManager.saveData(player.getUniqueId());
    }

    public void summary(Player player) {
        SessionData sessionData = SessionDataManager.getData(player.getUniqueId());
        PersistentData persistentData = PersistentDataManager.getData(player.getUniqueId());

        List<String> summary = new ArrayList<>();
        summary.add("§8§m-------------------------");
        summary.add("");

        addCoinBreakdown(summary, sessionData);

        int totalCoins = sessionData.getStats(SessionStats.KILLCOINS) +
                sessionData.getStats(SessionStats.ASSISTCOINS) +
                sessionData.getStats(SessionStats.TIMECOINS) +
                sessionData.getStats(SessionStats.WINCOINS);
        summary.add("  §e+ " + totalCoins + " §7(Łącznie)");
        summary.add("");

        int gamesPlayed = persistentData.getStats().getOrDefault(PersistentStats.SEASON_PLAYED, 0);
        if (gamesPlayed <= 5) {
            summary.add("  §fGra rozstawiająca: §6" + gamesPlayed + "§f/§65");
        } else {
            int ranking = sessionData.getStats(SessionStats.RANKING);
            String sign = ranking >= 0 ? "§a+ " : "§c- ";
            summary.add(" " + sign + "§f" + Math.abs(ranking) + "PR");
        }

        summary.add("");
        summary.add("§8§m-------------------------");

        for (String string : summary) {
            player.sendMessage(string);
        }
    }

    private void addCoinBreakdown(List<String> summary, SessionData sessionData) {
        if (sessionData.getStats(SessionStats.KILLCOINS) > 0) {
            summary.add("  §e+ " + sessionData.getStats(SessionStats.KILLCOINS) + " §7(Zabójstwa)");
        }
        if (sessionData.getStats(SessionStats.ASSISTCOINS) > 0) {
            summary.add("  §e+ " + sessionData.getStats(SessionStats.ASSISTCOINS) + " §7(Asysty)");
        }
        if (sessionData.getStats(SessionStats.TIMECOINS) > 0) {
            summary.add("  §e+ " + sessionData.getStats(SessionStats.TIMECOINS) + " §7(Czas gry)");
        }
        if (sessionData.getStats(SessionStats.WINCOINS) > 0) {
            summary.add("  §e+ " + sessionData.getStats(SessionStats.WINCOINS) + " §7(Zwycięstwo)");
        }
    }
}
