package dabbiks.uhc.cosmetics.particletrail;

import org.bukkit.Location;
import org.bukkit.Particle;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrailCycleManager {
    private final Map<String, TrailData> trails = new HashMap<>();
    private final Map<String, Integer> currentIndices = new HashMap<>();

    public void setTrails(List<TrailData> loadedTrails) {
        this.trails.clear();
        this.currentIndices.clear();
        for (TrailData trail : loadedTrails) {
            this.trails.put(trail.getId(), trail);
            this.currentIndices.put(trail.getId(), 0);
        }
    }

    public void spawnParticle(String id, Location location) {
        TrailData data = trails.get(id);
        if (data == null || data.getParticles().isEmpty()) {
            return;
        }

        int index = currentIndices.getOrDefault(id, 0);
        Particle particle = data.getParticles().get(index);

        currentIndices.put(id, (index + 1) % data.getParticles().size());

        if (particle.getDataType() == Particle.DustOptions.class) {
            Particle.DustOptions options = new Particle.DustOptions(data.getColor(), data.getSize());
            location.getWorld().spawnParticle(particle, location, 1, 0, 0, 0, 0, options);
        } else {
            location.getWorld().spawnParticle(particle, location, 1, 0, 0, 0, 0);
        }
    }

    public TrailData getTrailData(String id) {
        return trails.get(id);
    }
}