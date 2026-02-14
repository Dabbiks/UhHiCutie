package dabbiks.uhc.game.teams;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class TeamData {
    private String name;
    private LocationData location;
    private int rotation;
    private String icon;
    private String banner;
    private String smallIcon;

    public Location getBukkitLocation() {
        return new Location(
                Bukkit.getWorld(location.world),
                location.x,
                location.y,
                location.z
        );
    }

    public static class LocationData {
        public String world;
        public double x, y, z;
    }

    public String getName() {
        return name;
    }
    public LocationData getLocation() { return location; }
    public int getRotation() { return rotation; }
    public String getIcon() {
        return icon;
    }
    public String getBanner() {
        return banner;
    }
    public String getSmallIcon() {
        return smallIcon;
    }
}
