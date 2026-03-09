package dabbiks.uhc.game.world.pathfinder;

import dabbiks.uhc.game.configs.WorldConfig;
import org.bukkit.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class Pathfinder {

    private static final int SCAN_MIN_Y = 20;
    private static final int SCAN_MAX_Y = 180;
    private static final int MIN_CLEARANCE = 8;
    private static final int SCAN_RADIUS = 2;

    public static CompletableFuture<HashMap<Integer, Location>> generatePathAsync(int minR, int maxR, int hAtMin, int hAtMax) {
        World world = Bukkit.getWorld(WorldConfig.worldName);
        if (world == null) return CompletableFuture.completedFuture(new HashMap<>());

        int mapMargin = 64;
        int boundsMinX = -maxR - mapMargin;
        int boundsMaxX = maxR + mapMargin;
        int boundsMinZ = -maxR - mapMargin;
        int boundsMaxZ = maxR + mapMargin;

        Map<Long, ChunkSnapshot> snapshots = new HashMap<>();
        try {
            int minChunkX = boundsMinX >> 4;
            int maxChunkX = boundsMaxX >> 4;
            int minChunkZ = boundsMinZ >> 4;
            int maxChunkZ = boundsMaxZ >> 4;

            for (int x = minChunkX; x <= maxChunkX; x++) {
                for (int z = minChunkZ; z <= maxChunkZ; z++) {
                    snapshots.put(asLong(x, z), world.getChunkAt(x, z).getChunkSnapshot(false, false, false));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return CompletableFuture.completedFuture(new HashMap<>());
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                List<Point> waypoints = new ArrayList<>();
                double avgR = (minR + maxR) / 2.0;
                int segments = 40;

                for (int i = 0; i < segments; i++) {
                    double angle = Math.toRadians(i * (360.0 / segments));
                    waypoints.add(new Point((int) (avgR * Math.cos(angle)), (int) (avgR * Math.sin(angle))));
                }
                waypoints.add(waypoints.get(0));

                List<Point> coarsePath = new ArrayList<>();
                for (int i = 0; i < waypoints.size() - 1; i++) {
                    coarsePath.addAll(generateLine(waypoints.get(i), waypoints.get(i + 1)));
                }

                List<Point2D> densePath = interpolateCatmullRom2D(coarsePath, 20);
                int denseSize = densePath.size();
                double[] targetHeights = new double[denseSize];

                for (int i = 0; i < denseSize; i++) {
                    Point2D p = densePath.get(i);
                    int px = (int) Math.round(p.x);
                    int pz = (int) Math.round(p.z);

                    int localMaxY = SCAN_MIN_Y;
                    for (int dx = -SCAN_RADIUS; dx <= SCAN_RADIUS; dx++) {
                        for (int dz = -SCAN_RADIUS; dz <= SCAN_RADIUS; dz++) {
                            int ty = getHighestSolidY(px + dx, pz + dz, snapshots);
                            if (ty > localMaxY) localMaxY = ty;
                        }
                    }

                    double r = Math.sqrt(p.x * p.x + p.z * p.z);
                    double idealY = calculateIdealHeight(r, minR, maxR, hAtMin, hAtMax);
                    targetHeights[i] = Math.max(idealY, localMaxY + MIN_CLEARANCE);
                }

                double[] smoothedY = smoothHeights(targetHeights, denseSize);

                HashMap<Integer, Location> resultMap = new HashMap<>();
                for (int i = 0; i < denseSize; i++) {
                    Point2D p = densePath.get(i);
                    resultMap.put(i, new Location(world, p.x, smoothedY[i], p.z));
                }

                return resultMap;

            } catch (Exception e) {
                e.printStackTrace();
                return new HashMap<>();
            }
        });
    }

    private static double[] smoothHeights(double[] targetHeights, int size) {
        double[] smoothed = targetHeights.clone();
        double MAX_SLOPE = 0.05;

        for (int i = 1; i < size * 2; i++) {
            int idx = i % size;
            int prev = (i - 1 + size) % size;
            if (smoothed[prev] - MAX_SLOPE > smoothed[idx]) {
                smoothed[idx] = smoothed[prev] - MAX_SLOPE;
            }
        }

        for (int i = size * 2; i >= 0; i--) {
            int idx = i % size;
            int next = (i + 1) % size;
            if (smoothed[next] - MAX_SLOPE > smoothed[idx]) {
                smoothed[idx] = smoothed[next] - MAX_SLOPE;
            }
        }

        for (int pass = 0; pass < 15; pass++) {
            double[] buffer = new double[size];
            for (int i = 0; i < size; i++) {
                double sum = 0;
                for (int w = -4; w <= 4; w++) {
                    sum += smoothed[(i + w + size) % size];
                }
                buffer[i] = sum / 9.0;
            }
            smoothed = buffer;
            for (int i = 0; i < size; i++) {
                if (smoothed[i] < targetHeights[i]) {
                    smoothed[i] = targetHeights[i];
                }
            }
        }
        return smoothed;
    }

    private static int getHighestSolidY(int x, int z, Map<Long, ChunkSnapshot> snapshots) {
        long key = asLong(x >> 4, z >> 4);
        ChunkSnapshot snap = snapshots.get(key);
        if (snap == null) return SCAN_MIN_Y;

        for (int y = SCAN_MAX_Y; y >= SCAN_MIN_Y; y--) {
            Material type = snap.getBlockType(x & 15, y, z & 15);
            if (!type.isAir() && type != Material.WATER && type != Material.LAVA) {
                return y;
            }
        }
        return SCAN_MIN_Y;
    }

    private static List<Point> generateLine(Point start, Point end) {
        List<Point> line = new ArrayList<>();
        double dist = start.dist(end);
        if (dist == 0) {
            line.add(start);
            return line;
        }
        double dx = (end.x - start.x) / dist;
        double dz = (end.z - start.z) / dist;
        for (double d = 0; d <= dist; d += 1.0) {
            line.add(new Point((int) (start.x + dx * d), (int) (start.z + dz * d)));
        }
        return line;
    }

    private static List<Point2D> interpolateCatmullRom2D(List<Point> coarse, int pointsPerSegment) {
        List<Point2D> dense = new ArrayList<>();
        int size = coarse.size();
        for (int i = 0; i < size; i++) {
            Point p0 = coarse.get((i - 1 + size) % size);
            Point p1 = coarse.get(i);
            Point p2 = coarse.get((i + 1) % size);
            Point p3 = coarse.get((i + 2) % size);

            for (int t = 0; t < pointsPerSegment; t++) {
                double time = t / (double) pointsPerSegment;
                double t2 = time * time;
                double t3 = t2 * time;

                double x = 0.5 * ((2 * p1.x) + (-p0.x + p2.x) * time +
                        (2 * p0.x - 5 * p1.x + 4 * p2.x - p3.x) * t2 +
                        (-p0.x + 3 * p1.x - 3 * p2.x + p3.x) * t3);

                double z = 0.5 * ((2 * p1.z) + (-p0.z + p2.z) * time +
                        (2 * p0.z - 5 * p1.z + 4 * p2.z - p3.z) * t2 +
                        (-p0.z + 3 * p1.z - 3 * p2.z + p3.z) * t3);

                dense.add(new Point2D(x, z));
            }
        }
        return dense;
    }

    private static double calculateIdealHeight(double r, int minR, int maxR, int hMin, int hMax) {
        double ratio = (r - minR) / (double) (maxR - minR);
        ratio = Math.max(0, Math.min(1, ratio));
        return hMin + (ratio * (hMax - hMin));
    }

    private static long asLong(int x, int z) {
        return (long) x & 0xffffffffL | ((long) z & 0xffffffffL) << 32;
    }

    private static class Point {
        int x, z;
        Point(int x, int z) { this.x = x; this.z = z; }
        double dist(Point o) { return Math.sqrt(Math.pow(x - o.x, 2) + Math.pow(z - o.z, 2)); }
    }

    private static class Point2D {
        double x, z;
        Point2D(double x, double z) { this.x = x; this.z = z; }
    }
}