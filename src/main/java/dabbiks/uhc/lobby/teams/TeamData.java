package dabbiks.uhc.lobby.teams;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class TeamData {
    public String name;
    public LocationData location;
    public int rotation;
    public String icon;
    public String banner;
    public String smallIcon;

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
