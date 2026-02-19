package dabbiks.uhc.player.rank;

import dabbiks.uhc.player.data.persistent.PersistentData;
import dabbiks.uhc.player.data.persistent.PersistentDataManager;
import dabbiks.uhc.player.data.persistent.PersistentStats;
import dabbiks.uhc.player.data.session.SessionData;
import dabbiks.uhc.player.data.session.SessionDataManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

import static dabbiks.uhc.Main.*;

public class RankManager {

    public static final int MIN_GAMES_FOR_RANKED = 5;

    public static void calculatePlayerModifier(Player player) {
        PersistentData pData = PersistentDataManager.getData(player.getUniqueId());
        SessionData sData = SessionDataManager.getData(player.getUniqueId());

        double avgOpponentRank = calculateAverageOpponentRank(player);
        int modifier = RankCalculator.calculateMatchModifier(pData, avgOpponentRank);

        sData.setModifier(modifier);
    }

    public static void modifyRankPoints(Player player, int amount) {
        PersistentData data = PersistentDataManager.getData(player.getUniqueId());

        if (data.getStats().getOrDefault(PersistentStats.SEASON_PLAYED, 0) < MIN_GAMES_FOR_RANKED) {
            return;
        }

        int currentPoints = data.getStats().getOrDefault(PersistentStats.RANK_PR, 0);

        data.setStats(PersistentStats.PREVIOUS_RANK_PR, currentPoints);

        int newPoints = Math.max(1, currentPoints + amount);
        data.setStats(PersistentStats.RANK_PR, newPoints);

        checkRankUpdate(player, currentPoints, newPoints);
    }

    private static void checkRankUpdate(Player player, int oldPoints, int newPoints) {
        RankType oldRank = RankType.getByPoints(oldPoints);
        RankType newRank = RankType.getByPoints(newPoints);

        if (oldRank == newRank) return;

        boolean isPromotion = newRank.ordinal() > oldRank.ordinal();
        handleRankChange(player, oldRank, newRank, isPromotion);
    }

    private static void handleRankChange(Player player, RankType oldRank, RankType newRank, boolean isPromotion) {
        PersistentData data = PersistentDataManager.getData(player.getUniqueId());

        data.setRank(newRank);

        boolean isDivisionChange = !oldRank.getIcon().equals(newRank.getIcon());
        String arrow = isPromotion ? " §a>>§f " : " §c<<§f ";
        String action = isPromotion ? "awansuje" : "degraduje";
        String color = isPromotion ? "§a" : "§c";

        String message;
        if (!isDivisionChange) {
            String verb = isPromotion ? "podnosi" : "spada z";
            message = color + player.getName() + "§f " + verb + " dywizji: " + newRank.getIcon();
        } else {
            message = color + player.getName() + "§f " + action + ": " + oldRank.getIcon() + arrow + newRank.getIcon();
        }

        messageU.sendMessageToPlayers(playerListU.getAllPlayers(), message);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) {
                INSTANCE.getPrefixManager().update(player);
            }
        }, 5L);
    }

    public static void processPlacements(Player player) {
        PersistentData data = PersistentDataManager.getData(player.getUniqueId());

        if (data.getStats().getOrDefault(PersistentStats.SEASON_PLAYED, 0) < MIN_GAMES_FOR_RANKED) {
            return;
        }

        if (data.getRank() != RankType.UNRANKED) {
            return;
        }

        int calculatedPoints = RankCalculator.calculatePlacementRank(data);
        RankType resultRank = RankType.getByPoints(calculatedPoints);

        data.setStats(PersistentStats.RANK_PR, calculatedPoints);
        data.setRank(resultRank);
        PersistentDataManager.saveData(player.getUniqueId());

        messageU.sendMessageToPlayers(playerListU.getAllPlayers(),
                "§e" + player.getName() + "§f ukończył gry rozstawiające: " + resultRank.getIcon());

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) {
                INSTANCE.getPrefixManager().update(player);
            }
        }, 5L);
    }

    private static double calculateAverageOpponentRank(Player excludedPlayer) {
        List<Player> allPlayers = playerListU.getAllPlayers();

        if (allPlayers.size() <= 1) return 800.0;

        long totalRank = 0;
        int count = 0;

        for (Player p : allPlayers) {
            if (p.equals(excludedPlayer)) continue;

            PersistentData pd = PersistentDataManager.getData(p.getUniqueId());
            if (pd != null) {
                totalRank += pd.getStats().getOrDefault(PersistentStats.RANK_PR, 800);
                count++;
            }
        }

        return count > 0 ? (double) totalRank / count : 800.0;
    }
}