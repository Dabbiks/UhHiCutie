package dabbiks.uhc.utils;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

public class FireworkUtils {

    public void spawn(Location loc, FireworkEffect.Type type, Color color, Color fade, boolean flicker, boolean trail, int power) {
        Firework fw = loc.getWorld().spawn(loc, Firework.class);
        FireworkMeta meta = fw.getFireworkMeta();

        meta.addEffect(FireworkEffect.builder()
                .with(type)
                .withColor(color)
                .withFade(fade)
                .flicker(flicker)
                .trail(trail)
                .build());

        meta.setPower(power);
        fw.setFireworkMeta(meta);
    }

    public void spawnQuick(Location loc, Color color) {
        spawn(loc, FireworkEffect.Type.BALL, color, Color.WHITE, false, false, 1);
    }

    public void spawnStar(Location loc, Color color, Color fade) {
        spawn(loc, FireworkEffect.Type.STAR, color, fade, true, true, 1);
    }

    public void spawnCreeper(Location loc, Color color) {
        spawn(loc, FireworkEffect.Type.CREEPER, color, Color.BLACK, false, true, 1);
    }

    public void spawnBurst(Location loc, Color color) {
        spawn(loc, FireworkEffect.Type.BURST, color, Color.GRAY, true, false, 1);
    }

    public void instantExplode(Location loc, Color color) {
        Firework fw = loc.getWorld().spawn(loc, Firework.class);
        FireworkMeta meta = fw.getFireworkMeta();

        meta.addEffect(FireworkEffect.builder()
                .with(FireworkEffect.Type.BALL_LARGE)
                .withColor(color)
                .flicker(true)
                .build());

        fw.setFireworkMeta(meta);
        fw.detonate();
    }
}