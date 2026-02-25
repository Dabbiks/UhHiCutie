package dabbiks.uhc.game.teams;

import dabbiks.uhc.game.configs.LobbyConfig;
import dabbiks.uhc.game.configs.WorldConfig;
import dabbiks.uhc.player.PlayerState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import java.util.*;
import java.util.stream.Collectors;

import static dabbiks.uhc.Main.*;

public class TeamUtils {

    private static final TeamManager teamManager = INSTANCE.getTeamManager();

    public static boolean isPlayerInTeam(Player player, String teamName) {
        Team team = teamManager.getScoreboard().getTeam(teamName);
        if (team == null) return false;
        return team.hasEntry(player.getName());
    }

    public static boolean isPlayerAlly(Player player, Player target) {
        Team playerTeam = getPlayerTeam(player);
        Team targetTeam = getPlayerTeam(target);
        if (playerTeam == null || targetTeam == null) return false;
        return playerTeam.getName().equals(targetTeam.getName());
    }

    public static Team getPlayerTeam(Player player) {
        return teamManager.getScoreboard().getEntryTeam(player.getName());
    }

    public static void balanceTeams() {
        List<Player> playersWithoutTeam = Bukkit.getOnlinePlayers().stream()
                .filter(player -> getPlayerTeam(player) == null)
                .collect(Collectors.toList());

        for (Player player : playersWithoutTeam) {
            assignToFirstAvailableTeam(player);
        }

        List<Team> teamsList = teamManager.getScoreboard().getTeams().stream()
                .map(td -> teamManager.getScoreboard().getTeam(td.getName()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        for (Team team : teamsList) {
            if (team.getSize() == 0 || team.getSize() >= LobbyConfig.teamSize) continue;
            tryMovePlayersToTargetTeams(team, teamsList);
        }
    }

    private static void assignToFirstAvailableTeam(Player player) {
        for (Team team : teamManager.getScoreboard().getTeams()) {
            if (team == null || team.getSize() >= LobbyConfig.teamSize) continue;

            teamManager.addPlayer(player, team.getName());
            return;
        }
    }

    private static void tryMovePlayersToTargetTeams(Team team, List<Team> teamsList) {
        for (Team targetTeam : teamsList) {
            if (targetTeam.equals(team) || targetTeam.getSize() >= LobbyConfig.teamSize) continue;
            movePlayersBetweenTeams(team, targetTeam);
            if (team.getSize() == 0) return;
        }
    }

    private static void movePlayersBetweenTeams(Team source, Team target) {
        for (String playerName : new HashSet<>(source.getEntries())) {
            if (target.getSize() >= LobbyConfig.teamSize || source.getSize() == 0) return;

            Player player = Bukkit.getPlayer(playerName);
            if (player == null) continue;

            teamManager.removePlayer(player);
            teamManager.addPlayer(player, target.getName());
        }
    }

    public static void teleportTeamsRandomly(int maxX, int maxZ) {
        World world = Bukkit.getWorld(WorldConfig.worldName);
        if (world == null) return;

        Map<Team, Location> teamLocations = new HashMap<>();
        Random random = new Random();

        for (Team team : teamManager.getScoreboard().getTeams()) {
            int x = random.nextInt(maxX * 2 + 1) - maxX;
            int z = random.nextInt(maxZ * 2 + 1) - maxZ;
            int y = 500;
            teamLocations.put(team, new Location(world, x + 0.5, y, z + 0.5));
        }

        List<Player> playersToTeleport = new ArrayList<>();
        List<Location> locationsByPlayer = new ArrayList<>();

        teamLocations.forEach((team, loc) -> {
            for (String entryName : team.getEntries()) {
                addPlayerToTeleportList(entryName, loc, playersToTeleport, locationsByPlayer);
            }
        });

        scheduleTeleportTask(playersToTeleport, locationsByPlayer);
    }

    private static void addPlayerToTeleportList(String name, Location loc, List<Player> players, List<Location> locs) {
        Player player = Bukkit.getPlayer(name);
        if (player == null || !player.isOnline()) return;
        players.add(player);
        locs.add(loc);
    }

    private static void scheduleTeleportTask(List<Player> players, List<Location> locs) {
        new BukkitRunnable() {
            int index = 0;

            @Override
            public void run() {
                if (index >= players.size()) {
                    this.cancel();
                    return;
                }
                Player player = players.get(index);
                playerU.cleanseState(player);
                player.teleport(locs.get(index));
                index++;
            }
        }.runTaskTimer(plugin, 0L, 5);
    }

    public static Team getLastAliveTeam() {
        Team lastTeam = null;
        int aliveTeams = 0;

        for (Team team : teamManager.getScoreboard().getTeams()) {
            boolean hasAlive = false;
            for (String playerName : team.getEntries()) {
                Player player = Bukkit.getPlayer(playerName);
                if (player == null) continue;
                if (stateU.getPlayerState(player) == PlayerState.ALIVE) {
                    hasAlive = true;
                    break;
                }
            }
            if (hasAlive) {
                aliveTeams++;
                lastTeam = team;
            }
        }

        if (aliveTeams != 1) return null;
        return lastTeam;
    }

}
