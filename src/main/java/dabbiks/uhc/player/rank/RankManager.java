package dabbiks.uhc.player.rank;

import dabbiks.uhc.player.data.persistent.PersistentData;
import dabbiks.uhc.player.data.persistent.PersistentDataManager;
import dabbiks.uhc.player.data.persistent.PersistentStats;
import dabbiks.uhc.player.data.session.SessionData;
import dabbiks.uhc.player.data.session.SessionDataManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static dabbiks.uhc.Main.*;

public class RankManager {

    public static void calculatePlayerModifier(Player player) {
        PersistentData persistentData = PersistentDataManager.getData(player.getUniqueId());
        SessionData sessionData = SessionDataManager.getData(player.getUniqueId());

        // Statystyki gracza
        double kills = persistentData.getStats().getOrDefault(PersistentStats.SEASONKILLS, 0);
        double assists = persistentData.getStats().getOrDefault(PersistentStats.SEASONASSISTS, 0);
        double wins = persistentData.getStats().getOrDefault(PersistentStats.SEASONWINS, 0);
        double perfectWins = persistentData.getStats().getOrDefault(PersistentStats.SEASONPERFECTWINS, 0);
        double played = persistentData.getStats().getOrDefault(PersistentStats.SEASONPLAYED, 0);

        if (played < 5) {
            sessionData.setRankPRModifier(100); // neutralny dla nowych
            return;
        }

        // --- PERFORMANCE SCORE (0–40) ---
        double deaths = played - wins; // UHC = każda przegrana = śmierć
        double kda = (deaths <= 0) ? (kills + assists) : (kills + assists) / deaths;
        double winRate = (wins + perfectWins * 0.5) / played; // perfect win = 1.5x value

        // Skalowanie
        double kdaScore = Math.min(kda / 1.5, 1.0) * 20;    // max 20 przy 1.5 KDA
        double winScore = Math.min(winRate / 0.12, 1.0) * 20; // max 20 przy 12% winrate

        double performanceScore = kdaScore + winScore; // 0–40

        // --- OPPONENT STRENGTH (bazowe 60) ---
        int playerRank = persistentData.getStats().getOrDefault(PersistentStats.RANKPR, 800);

        int players = 0;
        int totalRank = 0;
        for (Player p : playerListU.getAllPlayers()) { // zakładam że tu masz wszystkich uczestników
            if (p.equals(player)) continue;
            PersistentData pd = PersistentDataManager.getData(p.getUniqueId());
            totalRank += pd.getStats().getOrDefault(PersistentStats.RANKPR, 800);
            players++;
        }

        double avgOpponentRank = (players > 0) ? (double) totalRank / players : 800;
        double rankDiff = avgOpponentRank - playerRank;
        double opponentScore = 60 + (rankDiff / 20.0); // każde 20 różnicy = 1 punkt

        // --- FINAL SCORE ---
        double totalScore = performanceScore + opponentScore; // teoretycznie 0–100+
        int modifier = (int) Math.max(60, Math.min(140, totalScore));

        sessionData.setRankPRModifier(modifier);
    }

    public static void modifyPlayerRankPR(Player player, int rankPR) {
        PersistentData persistentData = PersistentDataManager.getData(player.getUniqueId());
        SessionData sessionData = SessionDataManager.getData(player.getUniqueId());

        if (persistentData.getStats().getOrDefault(PersistentStats.SEASONPLAYED, 0) < 5) return;

        persistentData.setStats(PersistentStats.PREVIOUSRANKPR, persistentData.getStats().getOrDefault(PersistentStats.RANKPR, 0));
        persistentData.addStats(PersistentStats.RANKPR, rankPR);
        if (persistentData.getStats().getOrDefault(PersistentStats.RANKPR, 0) < 1) {
            persistentData.setStats(PersistentStats.RANKPR, 1);
            persistentData.setStats(PersistentStats.PREVIOUSRANKPR, 1);
        }
        updatePlayerRank(player);
    }

    public static void updatePlayerRank(Player player) {
        PersistentData persistentData = PersistentDataManager.getData(player.getUniqueId());
        RankTypes previousThresholdRank = null;
        RankTypes actualThresholdRank = null;
        int previousRankPR = persistentData.getStats().getOrDefault(PersistentStats.PREVIOUSRANKPR, 0);
        int rankPR = persistentData.getStats().getOrDefault(PersistentStats.RANKPR, 0);

        for (RankTypes rankTypes : RankTypes.values()) {
            if (previousRankPR >= rankTypes.getMinThreshold() && previousRankPR <= rankTypes.getMaxThreshold()) {
                previousThresholdRank = rankTypes;
            }
            if (rankPR >= rankTypes.getMinThreshold() && rankPR <= rankTypes.getMaxThreshold()) {
                actualThresholdRank = rankTypes;
            }
            if (actualThresholdRank == null || previousThresholdRank == null) { continue; }
            if (actualThresholdRank == previousThresholdRank) { return;}
            if (actualThresholdRank.getMaxThreshold() > previousThresholdRank.getMaxThreshold()) {
                processPromotion(player, previousThresholdRank, actualThresholdRank);
                break;
            }
            if (actualThresholdRank.getMaxThreshold() < previousThresholdRank.getMaxThreshold()) {
                processDemotion(player, previousThresholdRank, actualThresholdRank);
                break;
            }
        }
    }

    public static void processPromotion(Player player, RankTypes lastRank, RankTypes actualRank) {
        if (lastRank.getIcon().equals(actualRank.getIcon())) {
            messageU.sendMessageToPlayers(playerListU.getAllPlayers(), "§a" + player.getName() + "§f podnosi dywizję: " + actualRank.getIcon());
        } else {
            messageU.sendMessageToPlayers(playerListU.getAllPlayers(), "§a" + player.getName() + "§f awansuje: " + lastRank.getIcon() + " §a>>§f " + actualRank.getIcon());
        }
        PersistentData persistentData = PersistentDataManager.getData(player.getUniqueId());
        persistentData.setRank(actualRank);
        SessionData sessionData = SessionDataManager.getData(player.getUniqueId());
        sessionData.updatePlayerPrefix(player);
    }

    public static void processDemotion(Player player, RankTypes lastRank, RankTypes actualRank) {
        if (lastRank.getIcon().equals(actualRank.getIcon())) {
            messageU.sendMessageToPlayers(playerListU.getAllPlayers(), "§c" + player.getName() + "§f spada z dywizji: " + actualRank.getIcon());
        } else {
            messageU.sendMessageToPlayers(playerListU.getAllPlayers(), "§c" + player.getName() + "§f degraduje: " + lastRank.getIcon() + " §a>>§f " + actualRank.getIcon());
        }        PersistentData persistentData = PersistentDataManager.getData(player.getUniqueId());
        persistentData.setRank(actualRank);
        SessionData sessionData = SessionDataManager.getData(player.getUniqueId());
        sessionData.updatePlayerPrefix(player);
    }

    public static void processPlacements(Player player) {
        PersistentData persistentData = PersistentDataManager.getData(player.getUniqueId());

        int rank = 30 * persistentData.getStats().getOrDefault(PersistentStats.SEASONKILLS, 0);
        rank = rank + 10 * persistentData.getStats().getOrDefault(PersistentStats.SEASONASSISTS, 0);
        rank = rank + 75 * persistentData.getStats().getOrDefault(PersistentStats.SEASONWINS, 0);
        rank = rank + 75 * persistentData.getStats().getOrDefault(PersistentStats.SEASONPERFECTWINS, 0);
        if (rank < 1) rank = 1;
        SessionData sessionData = SessionDataManager.getData(player.getUniqueId());

        for (RankTypes rankTypes : RankTypes.values()) {
            if (rankTypes.getMinThreshold() > rank) continue;
            if (rankTypes.getMaxThreshold() < rank) continue;
            persistentData.setStats(PersistentStats.RANKPR, rank);
            persistentData.setRank(rankTypes);
            PersistentDataManager.saveData(player.getUniqueId());

            messageU.sendMessageToPlayers(playerListU.getAllPlayers(), "§e" + player.getName() + "§f ukończył gry rozstawiające: " + rankTypes.getIcon());

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (player.isOnline()) {
                    sessionData.updatePlayerPrefix(player);
                }
            }, 30L);

        }
    }
}