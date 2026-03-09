package dabbiks.uhc.game.gameplay.shop;

import dabbiks.uhc.game.world.pathfinder.Pathfinder;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static dabbiks.uhc.Main.plugin;

public class ShopManager {

    private HashMap<Integer, Location> path = new HashMap<>();

    public void spawnShops() {
        findPathForShops();
    }

    private void findPathForShops() {
        Pathfinder.generatePathAsync(130, 250, 0, 120).thenAccept(rawPathMap -> {
            Bukkit.getScheduler().runTask(plugin, () -> {
                if (rawPathMap != null && !rawPathMap.isEmpty()) {
                    path = applyStringPullingAndResample(rawPathMap);
                    new ItemShopEntity(path, 0);
                }
            });
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    private HashMap<Integer, Location> applyStringPullingAndResample(HashMap<Integer, Location> original) {
        if (original == null || original.isEmpty()) return new HashMap<>();

        List<Location> rawPoints = new ArrayList<>();
        World world = original.values().iterator().next().getWorld();

        for (int i = 1; i <= original.size(); i++) {
            Location loc = original.get(i);
            if (loc != null) {
                rawPoints.add(new Location(world, loc.getBlockX() + 0.5, loc.getY(), loc.getBlockZ() + 0.5));
            }
        }

        if (rawPoints.size() <= 2) return original;

        List<Location> corners = new ArrayList<>();
        corners.add(rawPoints.get(0));

        int currentIndex = 0;
        int size = rawPoints.size();

        while (currentIndex < size - 1) {
            int furthestIndex = currentIndex + 1;
            int maxLookahead = Math.min(size - 1, currentIndex + 15);

            for (int i = maxLookahead; i > currentIndex + 1; i--) {
                if (hasClearLineOfSight(rawPoints.get(currentIndex), rawPoints.get(i), world)) {
                    furthestIndex = i;
                    break;
                }
            }

            corners.add(rawPoints.get(furthestIndex));
            currentIndex = furthestIndex;
        }

        List<Location> resampled = new ArrayList<>();
        double spacing = 0.2;

        for (int i = 0; i < corners.size() - 1; i++) {
            Location p1 = corners.get(i);
            Location p2 = corners.get(i + 1);

            double dist = p1.distance(p2);
            int steps = Math.max(1, (int) Math.round(dist / spacing));

            for (int s = 0; s < steps; s++) {
                double fraction = (double) s / steps;
                double nx = p1.getX() + (p2.getX() - p1.getX()) * fraction;
                double ny = p1.getY() + (p2.getY() - p1.getY()) * fraction;
                double nz = p1.getZ() + (p2.getZ() - p1.getZ()) * fraction;
                resampled.add(new Location(world, nx, ny, nz));
            }
        }

        HashMap<Integer, Location> optimizedMap = new HashMap<>();
        int resampledSize = resampled.size();

        for (int i = 0; i < resampledSize; i++) {
            Location current = resampled.get(i).clone();
            int lookaheadIndex = (i + 15) % resampledSize;
            Location target = resampled.get(lookaheadIndex);

            Vector dir = target.toVector().subtract(current.toVector());
            if (dir.lengthSquared() > 0.001) {
                current.setDirection(dir);
            }

            optimizedMap.put(i + 1, current);
        }

        return optimizedMap;
    }

    private boolean hasClearLineOfSight(Location a, Location b, World world) {
        Location start = a.clone().add(0, 1.5, 0);
        Location end = b.clone().add(0, 1.5, 0);
        Vector dir = end.toVector().subtract(start.toVector());
        double dist = dir.length();

        if (dist <= 1.0) return true;

        RayTraceResult result = world.rayTraceBlocks(start, dir.normalize(), dist, FluidCollisionMode.NEVER, true);
        return result == null;
    }
}