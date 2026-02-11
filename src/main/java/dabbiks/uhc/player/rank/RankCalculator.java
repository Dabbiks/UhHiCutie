package dabbiks.uhc.player.rank;

import dabbiks.uhc.player.data.persistent.PersistentData;
import dabbiks.uhc.player.data.persistent.PersistentStats;

public class RankCalculator {

    private static final int MIN_MODIFIER = 60;
    private static final int MAX_MODIFIER = 140;
    private static final int BASE_OPPONENT_SCORE = 60;

    private static final int PLACE_KILL_POINTS = 30;
    private static final int PLACE_ASSIST_POINTS = 10;
    private static final int PLACE_WIN_POINTS = 75;

    public static int calculatePlacementRank(PersistentData data) {
        int kills = data.getStats().getOrDefault(PersistentStats.SEASONKILLS, 0);
        int assists = data.getStats().getOrDefault(PersistentStats.SEASONASSISTS, 0);
        int wins = data.getStats().getOrDefault(PersistentStats.SEASONWINS, 0);
        int perfectWins = data.getStats().getOrDefault(PersistentStats.SEASONPERFECTWINS, 0);

        int rank = (PLACE_KILL_POINTS * kills)
                + (PLACE_ASSIST_POINTS * assists)
                + (PLACE_WIN_POINTS * wins)
                + (PLACE_WIN_POINTS * perfectWins);

        return Math.max(1, rank);
    }

    public static int calculateMatchModifier(PersistentData data, double avgOpponentRank) {
        double played = data.getStats().getOrDefault(PersistentStats.SEASONPLAYED, 0);

        if (played < 5) return 100;

        double kills = data.getStats().getOrDefault(PersistentStats.SEASONKILLS, 0);
        double assists = data.getStats().getOrDefault(PersistentStats.SEASONASSISTS, 0);
        double wins = data.getStats().getOrDefault(PersistentStats.SEASONWINS, 0);
        double perfectWins = data.getStats().getOrDefault(PersistentStats.SEASONPERFECTWINS, 0);
        int currentRank = data.getStats().getOrDefault(PersistentStats.RANKPR, 0);

        double deaths = played - wins;
        double kda = (deaths <= 0) ? (kills + assists) : (kills + assists) / deaths;
        double winRate = (wins + (perfectWins * 0.5)) / played;

        double kdaScore = Math.min(kda / 1.5, 1.0) * 20;
        double winScore = Math.min(winRate / 0.12, 1.0) * 20;
        double performanceScore = kdaScore + winScore;

        double rankDiff = avgOpponentRank - currentRank;
        double opponentScore = BASE_OPPONENT_SCORE + (rankDiff / 20.0);

        double totalScore = performanceScore + opponentScore;
        return (int) Math.max(MIN_MODIFIER, Math.min(MAX_MODIFIER, totalScore));
    }
}