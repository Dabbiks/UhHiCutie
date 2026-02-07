package dabbiks.uhc.game.gameplay.items.fireworks;

import java.util.List;

public class FireworkData {

    private int flightDuration;
    private List<ExplosionData> explosions;

    public FireworkData(int flightDuration, List<ExplosionData> explosions) {
        this.flightDuration = flightDuration;
        this.explosions = explosions;
    }

    public int getFlightDuration() { return flightDuration; }
    public List<ExplosionData> getExplosions() { return explosions; }
}