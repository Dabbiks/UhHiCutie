package dabbiks.uhc.tasks.tasks;

import dabbiks.uhc.game.GameState;
import dabbiks.uhc.game.configs.SegmentConfig;
import dabbiks.uhc.game.world.events.WorldBorder;
import dabbiks.uhc.player.PlayerState;
import dabbiks.uhc.player.data.persistent.PersistentData;
import dabbiks.uhc.player.data.persistent.PersistentDataManager;
import dabbiks.uhc.player.data.persistent.PersistentStats;
import dabbiks.uhc.player.data.session.SessionData;
import dabbiks.uhc.player.data.session.SessionDataManager;
import dabbiks.uhc.player.data.session.SessionStats;
import dabbiks.uhc.player.rank.RankType;
import dabbiks.uhc.tasks.Task;
import fr.mrmicky.fastboard.FastBoard;
import org.bukkit.entity.Player;

import static dabbiks.uhc.Main.*;
import static dabbiks.uhc.player.traffic.JoinEvent.boards;

public class ScoreboardTask extends Task {

    int spectators = 0;
    int alive = 0;

    protected long getPeriod() {
        return 20;
    }

    protected void tick() {

        spectators = playerListU.getSpectatingPlayers().size();
        alive = playerListU.getPlayingPlayers().size();

        for (Player player : playerListU.getAllPlayers()) {

            if (!player.isOnline()) {
                continue;
            }

            FastBoard board = boards.get(player);
            if (board == null) {
                continue;
            }

            PersistentData persistentData = PersistentDataManager.getData(player.getUniqueId());
            SessionData sessionData = SessionDataManager.getData(player.getUniqueId());

            if (stateU.getGameState() == GameState.WAITING || stateU.getGameState() == GameState.STARTING) {
                int pr = persistentData.getStats().getOrDefault(PersistentStats.RANK_PR, 0);
                if (pr < RankType.AMETHYST.getMinThreshold()) pr = pr % 100;
                else { pr -= RankType.AMETHYST.getMinThreshold(); }

                board.updateLines(
                        "",
                        "  §cTwój ranking",
                        "  §f" + persistentData.getRank().getIcon() + " §7" + persistentData.getRank().getName()
                                + " §8(" + pr + "PR§8)",
                        "",
                        "  §f" + symbolU.SCOREBOARD_COIN + " ᴍᴏɴᴇᴛʏ: §e" + persistentData.getStats().getOrDefault(PersistentStats.COINS, 0),
                        "  §f" + symbolU.SCOREBOARD_POWDER + " ᴘʀᴏsᴢᴇᴋ: §e" + persistentData.getStats().getOrDefault(PersistentStats.POWDER, 0),
                        "  §f" + symbolU.SCOREBOARD_SWORD + " ᴇʟɪᴍɪɴᴀᴄᴊᴇ: §e" + persistentData.getStats().getOrDefault(PersistentStats.SEASON_KILLS, 0) +
                                " §8(" + persistentData.getStats().getOrDefault(PersistentStats.SEASON_ASSISTS, 0) + ")",
                        "  §f" + symbolU.SCOREBOARD_CROWN + " ᴡʏɢʀᴀɴᴇ: §e" + (persistentData.getStats().getOrDefault(PersistentStats.SEASON_WINS, 0)) +
                        " §8(" + persistentData.getStats().getOrDefault(PersistentStats.SEASON_PLAYED, 0) + ")",
                        "",
                        "§8  s ᴜ ᴘ ᴇ ʀ ɢ ʟ ɪ  .  ᴅ ᴇ"
                );
            }
            if (stateU.getGameState() == GameState.IN_GAME && stateU.getPlayerState(player).equals(PlayerState.ALIVE)) {

                board.updateLines(
                        "",
                        "  §f" + symbolU.SCOREBOARD_COIN + " ᴍᴏɴᴇᴛʏ: §e" + persistentData.getStats().getOrDefault(PersistentStats.COINS, 0),
                        "  §f" + symbolU.SCOREBOARD_CLOCK + " ꜱᴇɢᴍᴇɴᴛ: §e" + SegmentConfig.actualSegment,
                        "  §f" + symbolU.SCOREBOARD_BORDER + " ɢʀᴀɴɪᴄᴀ: §e" + (int) WorldBorder.borderSize / 2,
                        "",
                        "  §f" + symbolU.SCOREBOARD_SWORD + " ᴇʟɪᴍɪɴᴀᴄᴊᴇ: §e" + sessionData.getStats(SessionStats.KILLS),
                        "  §f" + symbolU.SCOREBOARD_ALIVE + " ɢʀᴀᴄᴢᴇ: §e" + alive,
                        "  §f" + symbolU.SCOREBOARD_SPECTATOR + " ᴏʙꜱᴇʀᴡᴜᴊᴀᴄʏ: §e" + spectators,
                        "",
                        "§8  s ᴜ ᴘ ᴇ ʀ ɢ ʟ ɪ  .  ᴅ ᴇ"
                );
            }
            if (stateU.getGameState() == GameState.IN_GAME && !stateU.getPlayerState(player).equals(PlayerState.ALIVE)) {

                board.updateLines(
                        "",
                        "  §f" + symbolU.SCOREBOARD_COIN + " ᴍᴏɴᴇᴛʏ: §e" + persistentData.getStats().getOrDefault(PersistentStats.COINS, 0),
                        "  §f" + symbolU.SCOREBOARD_CLOCK + " ꜱᴇɢᴍᴇɴᴛ: §e" + SegmentConfig.actualSegment,
                        "  §f" + symbolU.SCOREBOARD_BORDER + " ɢʀᴀɴɪᴄᴀ: §e" + (int) WorldBorder.borderSize / 2,
                        "",
                        "  §f" + symbolU.SCOREBOARD_ALIVE + " ɢʀᴀᴄᴢᴇ: §e" + alive,
                        "  §f" + symbolU.SCOREBOARD_SPECTATOR + " ᴏʙꜱᴇʀᴡᴜᴊᴀᴄʏ: §e" + spectators,
                        "",
                        "§8  s ᴜ ᴘ ᴇ ʀ ɢ ʟ ɪ  .  ᴅ ᴇ"
                );
            }
        }
    }
}
