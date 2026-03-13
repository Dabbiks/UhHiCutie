package dabbiks.uhc.game.teams;

import dabbiks.uhc.game.configs.LobbyConfig;
import dabbiks.uhc.game.configs.WorldConfig;
import dabbiks.uhc.player.PlayerState;
import dabbiks.uhc.player.data.persistent.PersistentData;
import dabbiks.uhc.player.data.persistent.PersistentDataManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.*;
import java.util.stream.Collectors;

import static dabbiks.uhc.Main.*;

public class TeamUtils {

    private static final TeamManager teamManager = INSTANCE.getTeamManager();
    private static final List<Location> cageCenters = new ArrayList<>();
    private static boolean cagesExist = false;

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

    public static boolean hasCages() {
        return cagesExist;
    }

    public static void createCagesAndTeleport(int radius) {
        World world = Bukkit.getWorld(WorldConfig.worldName);
        if (world == null) return;
        cageCenters.clear();
        cagesExist = true;

        Random random = new Random();

        for (Team team : teamManager.getScoreboard().getTeams()) {
            if (team.getSize() == 0) continue;

            int x = random.nextInt(radius * 2 + 1) - radius;
            int z = random.nextInt(radius * 2 + 1) - radius;
            int y = Bukkit.getWorld(WorldConfig.worldName).getHighestBlockYAt(x, z) + 50;
            Location center = new Location(world, x, y, z);
            cageCenters.add(center);

            Material cageMat = Material.GLASS;
            List<String> entries = new ArrayList<>(team.getEntries());
            if (!entries.isEmpty()) {
                String randomPlayer = entries.get(random.nextInt(entries.size()));
                Player p = Bukkit.getPlayer(randomPlayer);
                if (p != null) {
                    PersistentData pd = PersistentDataManager.getData(p.getUniqueId());
                    if (pd != null && pd.getCage() != null) {
                        try {
                            cageMat = pd.getCage().getMaterial();
                        } catch (Exception ignored) {}
                    }
                }
            }

            for (int dx = -3; dx <= 3; dx++) {
                for (int dy = 0; dy <= 6; dy++) {
                    for (int dz = -3; dz <= 3; dz++) {
                        Location loc = center.clone().add(dx, dy, dz);
                        boolean isEdgeX = (dx == -3 || dx == 3);
                        boolean isEdgeY = (dy == 0 || dy == 6);
                        boolean isEdgeZ = (dz == -3 || dz == 3);
                        int edgeCount = (isEdgeX ? 1 : 0) + (isEdgeY ? 1 : 0) + (isEdgeZ ? 1 : 0);

                        if (edgeCount >= 2) {
                            loc.getBlock().setType(cageMat);
                        } else if (edgeCount == 1) {
                            loc.getBlock().setType(Material.BARRIER);
                        } else {
                            loc.getBlock().setType(Material.AIR);
                        }
                    }
                }
            }

            for (String entryName : team.getEntries()) {
                Player player = Bukkit.getPlayer(entryName);
                if (player != null && player.isOnline()) {
                    player.teleport(center.clone().add(0.5, 1, 0.5));
                }
            }
        }
    }

    public static void removeCages() {
        World world = Bukkit.getWorld(WorldConfig.worldName);
        if (world == null) return;
        cagesExist = false;

        for (Location center : cageCenters) {
            for (int dx = -3; dx <= 3; dx++) {
                for (int dy = 0; dy <= 6; dy++) {
                    for (int dz = -3; dz <= 3; dz++) {
                        Location loc = center.clone().add(dx, dy, dz);
                        loc.getBlock().setType(Material.AIR);
                    }
                }
            }
        }
        cageCenters.clear();
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