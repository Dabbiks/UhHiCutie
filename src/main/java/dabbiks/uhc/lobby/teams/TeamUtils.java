package dabbiks.uhc.lobby.teams;

import dabbiks.uhc.game.configs.LobbyConfig;
import dabbiks.uhc.game.configs.WorldConfig;
import dabbiks.uhc.player.PlayerState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.*;

import static dabbiks.uhc.Main.*;

public class TeamUtils {

    public static ScoreboardManager manager = Bukkit.getScoreboardManager();
    public static Scoreboard scoreboard = manager.getMainScoreboard();

    public static void createTeam(String teamName) {
        scoreboard.registerNewTeam(teamName);
    }

    public static void removeTeam(String teamName) {
        Team team = scoreboard.getTeam(teamName);
        if (team == null) return;
        team.unregister();
    }

    public static void removeAllTeams() {
        for (Team team : scoreboard.getTeams()) {
            team.unregister();
        }
    }

    public static int getPlayerCountInTeam(String teamName) {
        Team team = scoreboard.getTeam(teamName);
        if (team == null) return 0;
        return team.getSize();
    }

    public static boolean isPlayerInTeam(Player player, String teamName) {
        Team team = scoreboard.getTeam(teamName);
        if (team == null) return false;
        return team.hasEntry(player.getName());
    }

    public static Team getPlayerTeam(Player player) {
        return scoreboard.getEntryTeam(player.getName());
    }

    public static boolean isTargetAlly(Player player, Player target) {
        Team playerTeam = getPlayerTeam(player);
        Team targetTeam = getPlayerTeam(target);
        if (playerTeam == null || targetTeam == null) return false;
        return playerTeam.getName().equals(targetTeam.getName());
    }

    public static void addPlayerToTeam(Player player, String teamName) {
        Team team = scoreboard.getTeam(teamName);
        if (team == null) return;
        team.addEntry(player.getName());
    }

    public static void removePlayerFromTeam(Player player, String teamName) {
        Team team = scoreboard.getTeam(teamName);
        if (team == null) return;
        team.removeEntry(player.getName());
    }

    public static int getTeamsCount() {
        return scoreboard.getTeams().size();
    }

    public static Map<Player, Team> alivePlayersData = new HashMap<>();

    public static void fillAlivePlayersData() {
        alivePlayersData.clear();
        for (Player player : playerListU.getAllPlayers()) {
            Team team = getPlayerTeam(player);
            if (team != null) {
                alivePlayersData.put(player, team);
            }
        }
    }

    public static void removeFromAlivePlayersData(Player player) {
        alivePlayersData.remove(player);
    }

    public static Team getLastAliveTeam() {
        Team lastTeam = null;
        int aliveTeams = 0;

        for (Team team : scoreboard.getTeams()) {
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

    public static void balanceTeams() {
        List<Player> playersWithoutTeam = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (getPlayerTeam(player) == null) {
                playersWithoutTeam.add(player);
            }
        }

        if (!playersWithoutTeam.isEmpty()) {
            outer:
            for (Player player : playersWithoutTeam) {
                for (TeamData teamData : TeamLoader.getTeams()) {
                    Team team = scoreboard.getTeam(teamData.name);
                    if (team == null) continue;
                    if (team.getSize() < LobbyConfig.teamSize) {
                        team.addEntry(player.getName());
                        TeamDisplay.processTeamJoin(player, team.getName());
                        continue outer;
                    }
                }
            }
        }

        List<Team> teamsList = new ArrayList<>();
        for (TeamData td : TeamLoader.getTeams()) {
            Team t = scoreboard.getTeam(td.name);
            if (t != null) teamsList.add(t);
        }

        for (Team team : teamsList) {
            if (team.getSize() > 0 && team.getSize() < LobbyConfig.teamSize) {
                for (Team targetTeam : teamsList) {
                    if (targetTeam.equals(team)) continue;
                    if (targetTeam.getSize() >= LobbyConfig.teamSize) continue;

                    Set<String> entries = new HashSet<>(team.getEntries());
                    for (String playerName : entries) {
                        Player player = Bukkit.getPlayer(playerName);
                        if (player == null) continue;

                        team.removeEntry(playerName);
                        TeamDisplay.processTeamQuit(player, team.getName());

                        targetTeam.addEntry(playerName);
                        TeamDisplay.processTeamJoin(player, targetTeam.getName());

                        if (targetTeam.getSize() >= LobbyConfig.teamSize) break;
                        if (team.getSize() == 0) break;
                    }
                    if (team.getSize() == 0) break;
                }
            }
        }
    }

    public static void teleportTeamsRandomly(int maxX, int maxZ) {
        World world = Bukkit.getWorld(WorldConfig.worldName);
        if (world == null) return;

        Random random = new Random();
        Map<Team, Location> teamLocations = new HashMap<>();

        // Losujemy pozycje dla każdej drużyny w zakresie ±100
        for (Team team : scoreboard.getTeams()) {
            int x = random.nextInt(maxX * 2 + 1) - maxX; // losowe od -maxX do maxX
            int z = random.nextInt(maxZ * 2 + 1) - maxZ; // losowe od -maxZ do maxZ

            // Pobieramy najwyższy blok nie będący powietrzem
            int y = world.getHighestBlockYAt(x, z);

            Location loc = new Location(world, x + 0.5, y+1, z + 0.5); // +0.5 żeby środkiem bloku
            teamLocations.put(team, loc);
        }

        // Zbieramy wszystkich graczy w kolejności po drużynach
        List<Player> playersToTeleport = new ArrayList<>();
        List<Location> locationsByPlayer = new ArrayList<>();

        for (Map.Entry<Team, Location> entry : teamLocations.entrySet()) {
            Team team = entry.getKey();
            Location loc = entry.getValue();

            for (String entryName : team.getEntries()) {
                Player player = Bukkit.getPlayer(entryName);
                if (player != null && player.isOnline()) {
                    playersToTeleport.add(player);
                    locationsByPlayer.add(loc);
                }
            }
        }

        new BukkitRunnable() {
            int index = 0;

            @Override
            public void run() {
                if (index >= playersToTeleport.size()) {
                    cancel();
                    return;
                }
                Player player = playersToTeleport.get(index);
                Location loc = locationsByPlayer.get(index);
//                playerU.cleanseState(player);
                player.teleport(loc);
                index++;
            }
        }.runTaskTimer(plugin, 0L, 5);
    }
}
