package dabbiks.uhc.player.traffic;

import dabbiks.uhc.game.GameData;
import dabbiks.uhc.game.GameState;
import dabbiks.uhc.game.configs.LobbyConfig;
import dabbiks.uhc.game.gameplay.damage.handlers.DeathHandler;
import dabbiks.uhc.game.teams.TeamManager;
import dabbiks.uhc.player.PlayerState;
import dabbiks.uhc.player.data.persistent.PersistentDataManager;
import dabbiks.uhc.player.data.session.SessionData;
import dabbiks.uhc.player.data.session.SessionDataManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import static dabbiks.uhc.Main.*;
import static dabbiks.uhc.game.gameplay.bossbar.SegmentBossBar.mainBossBar;
import static dabbiks.uhc.player.traffic.JoinEvent.boards;

public class QuitEvent implements Listener {

    private TeamManager teamManager = INSTANCE.getTeamManager();

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        event.setQuitMessage("");

        SessionData sessionData = SessionDataManager.getData(player.getUniqueId());

        handleQuitLogic(player, sessionData);
        cleanupPlayerData(player);
    }

    private void cleanupPlayerData(Player player) {
        PersistentDataManager.saveData(player.getUniqueId());
        PersistentDataManager.delData(player.getUniqueId());

        boards.remove(player);
        mainBossBar.removePlayer(player);
    }

    private void handleQuitLogic(Player player, SessionData sessionData) {
        if (GameData.isEnding) return;

        GameState gameState = stateU.getGameState();

        if (gameState == GameState.WAITING || gameState == GameState.STARTING) {
            handleLobbyQuit(player, sessionData);
            return;
        }

        if (stateU.getPlayerState(player) != PlayerState.ALIVE) {
            handleSpectatorQuit(player, sessionData);
            return;
        }

        if (gameState == GameState.IN_GAME) {
            handleInGameQuit(player, sessionData);
        }
    }

    private void handleLobbyQuit(Player player, SessionData sessionData) {
        String msg = "§c" + player.getName() + " §7(§f" + (Bukkit.getOnlinePlayers().size() - 1) + "/" + LobbyConfig.maxPlayerCount + "§7)";
        messageU.sendMessageToPlayers(playerListU.getAllPlayers(), msg);

        stateU.removePlayerState(player);
        teamManager.removePlayer(player);
        sessionData.clearTags();
    }

    private void handleSpectatorQuit(Player player, SessionData sessionData) {
        messageU.sendMessageToPlayers(playerListU.getAllPlayers(), "§7" + player.getName() + " opuszcza widownię");
        stateU.removePlayerState(player);
        sessionData.clearTags();
    }

    private void handleInGameQuit(Player player, SessionData sessionData) {
        new DeathHandler().handle(player);
        sessionData.clearTags();
    }
}