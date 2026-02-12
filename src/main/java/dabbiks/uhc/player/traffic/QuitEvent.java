package dabbiks.uhc.player.traffic;

import dabbiks.uhc.game.GameState;
import dabbiks.uhc.game.configs.LobbyConfig;
import dabbiks.uhc.game.configs.SegmentConfig;
import dabbiks.uhc.game.teams.TeamDisplay;
import dabbiks.uhc.game.teams.TeamUtils;
import dabbiks.uhc.player.PlayerState;
import dabbiks.uhc.player.data.persistent.PersistentDataManager;
import dabbiks.uhc.player.data.session.SessionData;
import dabbiks.uhc.player.data.session.SessionDataManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Team;

import static dabbiks.uhc.Main.*;
import static dabbiks.uhc.game.gameplay.bossbar.SegmentBossBar.mainBossBar;
import static dabbiks.uhc.player.traffic.JoinEvent.boards;

public class QuitEvent implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        event.setQuitMessage("");

        SessionData sessionData = SessionDataManager.getData(player.getUniqueId());

        cleanupPlayerData(player);
        handleQuitLogic(player, sessionData);
    }

    private void cleanupPlayerData(Player player) {
        PersistentDataManager.saveData(player.getUniqueId());
        PersistentDataManager.delData(player.getUniqueId());

        boards.remove(player);
        mainBossBar.removePlayer(player);
    }

    private void handleQuitLogic(Player player, SessionData sessionData) {
        GameState gameState = stateU.getGameState();

        if (gameState == GameState.WAITING || gameState == GameState.STARTING) {
            handleLobbyQuit(player, sessionData);
            return;
        }

        if (stateU.getPlayerState(player) == PlayerState.SPECTATOR) {
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

        Team previousTeam = TeamUtils.getPlayerTeam(player);
        if (previousTeam != null) {
            String teamName = previousTeam.getName();
            TeamUtils.removePlayerFromTeam(player, teamName);
            TeamDisplay.processTeamQuit(player, teamName);
            sessionData.teamIcon = "";

            Bukkit.getScheduler().runTaskLater(plugin, () -> TeamDisplay.reloadTeamDisplay(previousTeam), 2L);
        }

        sessionData.clearTags();
    }

    private void handleSpectatorQuit(Player player, SessionData sessionData) {
        messageU.sendMessageToPlayers(playerListU.getAllPlayers(), "§7" + player.getName() + " opuszcza widownię");
        stateU.removePlayerState(player);
        sessionData.clearTags();
    }

    private void handleInGameQuit(Player player, SessionData sessionData) {
        Damage.processDeath(player);
        stateU.removePlayerState(player);
        sessionData.clearTags();
    }
}