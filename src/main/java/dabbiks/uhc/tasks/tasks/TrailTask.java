package dabbiks.uhc.tasks.tasks;

import dabbiks.uhc.cosmetics.ParticleTrail;
import dabbiks.uhc.player.data.persistent.PersistentData;
import dabbiks.uhc.player.data.persistent.PersistentDataManager;
import dabbiks.uhc.tasks.Task;
import dabbiks.uhc.utils.ParticleUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class TrailTask extends Task {

    @Override
    protected long getPeriod() {
        return 2;
    }

    @Override
    protected void tick() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.isGliding()) continue;

            PersistentData data = PersistentDataManager.getData(player.getUniqueId());
            if (data == null) continue;

            ParticleTrail trail = data.getTrail();
            if (trail == null || trail.getParticles().isEmpty()) continue;

            Location loc = player.getLocation();

            for (Particle particle : trail.getParticles()) {
                ParticleUtils.spawn(loc, particle, 2, 0.2, 0.01);
            }
        }
    }
}