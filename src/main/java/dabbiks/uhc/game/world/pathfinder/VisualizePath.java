package dabbiks.uhc.game.world.pathfinder;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VisualizePath {

    private static final List<Integer> activeTasks = new ArrayList<>();

    public static void visualizePath(HashMap<Integer, Location> pathMap, Color color) {
        if (pathMap == null || pathMap.isEmpty()) return;

        Particle.DustOptions dustOptions = new Particle.DustOptions(color, 1.0f);
        int size = pathMap.size();

        for (int i = 0; i < size; i += 4) {
            Location current = pathMap.get(i);
            if (current == null) continue;

            current.getWorld().spawnParticle(Particle.DUST, current, 1, 0, 0, 0, 0, dustOptions);
        }
    }

    public static void paths(JavaPlugin plugin) {
        stopAllVisualizations();

        generateAndVisualize(plugin, 125, 250, 70, 70, Color.RED);
    }

    public static void stopAllVisualizations() {
        for (int taskId : activeTasks) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
        activeTasks.clear();
    }

    private static void generateAndVisualize(JavaPlugin plugin, int minR, int maxR, int hMin, int hMax, Color color) {
        Pathfinder.generatePathAsync(minR, maxR, hMin, hMax).thenAccept(rawPathMap -> {

            Bukkit.getScheduler().runTask(plugin, () -> {
                if (rawPathMap != null && !rawPathMap.isEmpty()) {

                    HashMap<Integer, Location> smoothPath = PathSmoother.smoothPath(rawPathMap, 20);

                    BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                        visualizePath(smoothPath, color);
                    }, 0L, 2L);

                    activeTasks.add(task.getTaskId());
                }
            });

        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }
}