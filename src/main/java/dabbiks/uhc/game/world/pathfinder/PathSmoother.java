package dabbiks.uhc.game.world.pathfinder;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import java.util.HashMap;

public class PathSmoother {

    public static HashMap<Integer, Location> smoothPath(HashMap<Integer, Location> originalPath, int pointsPerSegment) {
        if (originalPath == null || originalPath.isEmpty()) return new HashMap<>();

        HashMap<Integer, Location> finalMap = new HashMap<>();
        int totalPoints = originalPath.size();
        int lookAhead = Math.max(1, pointsPerSegment / 2);

        for (int i = 0; i < totalPoints; i++) {
            Location current = originalPath.get(i).clone();
            Location target = originalPath.get((i + lookAhead) % totalPoints);

            Vector dir = target.toVector().subtract(current.toVector());
            if (dir.lengthSquared() > 0.001) {
                current.setDirection(dir.normalize());
            }
            finalMap.put(i, current);
        }

        return finalMap;
    }
}