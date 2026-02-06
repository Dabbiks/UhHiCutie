package dabbiks.uhc.utils.managers;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWorldBorder;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;

public class BorderManager {

    /**
     * Sends a complete world border initialization to player(s)
     * @param players Collection of players to send to
     * @param centerX Center X coordinate
     * @param centerZ Center Z coordinate
     * @param size Current border size (diameter)
     * @param warningBlocks Warning distance in blocks
     * @param warningTime Warning time in seconds
     */
    public static void sendWorldBorderInit(Collection<Player> players, double centerX, double centerZ,
                                           double size, int warningBlocks, int warningTime) {

        WrapperPlayServerWorldBorder packet = new WrapperPlayServerWorldBorder(
                centerX,           // center X
                centerZ,           // center Z
                size / 2,          // old radius
                size / 2,          // new radius
                0,                 // speed (not used for init)
                0,                 // portal teleport boundary
                warningTime,       // warning time
                warningBlocks      // warning blocks
        );

        for (Player player : players) {
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
        }
    }

    /**
     * Sends world border initialization to a single player
     */
    public static void sendWorldBorderInit(Player player, double centerX, double centerZ,
                                           double size, int warningBlocks, int warningTime) {
        sendWorldBorderInit(java.util.Collections.singletonList(player),
                centerX, centerZ, size, warningBlocks, warningTime);
    }

    /**
     * Sets the world border center for player(s)
     * @param players Collection of players
     * @param centerX Center X coordinate
     * @param centerZ Center Z coordinate
     */
    public static void setWorldBorderCenter(Collection<Player> players, double centerX, double centerZ) {
        WrapperPlayServerWorldBorder packet = new WrapperPlayServerWorldBorder(
                centerX,    // center X
                centerZ,    // center Z
                0,          // old radius (not used)
                0,          // new radius (not used)
                0,          // speed (not used)
                0,          // portal teleport boundary
                0,          // warning time (not used)
                0           // warning blocks (not used)
        );

        for (Player player : players) {
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
        }
    }

    /**
     * Sets world border center for a single player
     */
    public static void setWorldBorderCenter(Player player, double centerX, double centerZ) {
        setWorldBorderCenter(java.util.Collections.singletonList(player), centerX, centerZ);
    }

    /**
     * Sets world border center using Location
     */
    public static void setWorldBorderCenter(Player player, Location center) {
        setWorldBorderCenter(player, center.getX(), center.getZ());
    }

    /**
     * Sets the world border size instantly
     * @param players Collection of players
     * @param diameter New border diameter
     */
    public static void setWorldBorderSize(Collection<Player> players, double diameter) {
        WrapperPlayServerWorldBorder packet = new WrapperPlayServerWorldBorder(
                0,              // center X (not used)
                0,              // center Z (not used)
                diameter / 2,   // old radius (not used for SET_SIZE)
                diameter / 2,   // new radius
                0,              // speed (not used)
                0,              // portal teleport boundary
                0,              // warning time (not used)
                0               // warning blocks (not used)
        );

        for (Player player : players) {
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
        }
    }

    /**
     * Sets world border size for a single player
     */
    public static void setWorldBorderSize(Player player, double diameter) {
        setWorldBorderSize(java.util.Collections.singletonList(player), diameter);
    }

    /**
     * Animates world border size change over time
     * @param players Collection of players
     * @param oldDiameter Current border diameter
     * @param newDiameter Target border diameter
     * @param speed Speed of change (time in milliseconds)
     */
    public static void animateWorldBorderSize(Collection<Player> players, double oldDiameter,
                                              double newDiameter, long speed) {
        WrapperPlayServerWorldBorder packet = new WrapperPlayServerWorldBorder(
                0,                  // center X (not used)
                0,                  // center Z (not used)
                oldDiameter / 2,    // old radius
                newDiameter / 2,    // new radius
                speed,              // speed in milliseconds
                0,                  // portal teleport boundary
                0,                  // warning time (not used)
                0                   // warning blocks (not used)
        );

        for (Player player : players) {
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
        }
    }

    /**
     * Animates world border size for a single player
     */
    public static void animateWorldBorderSize(Player player, double oldDiameter,
                                              double newDiameter, long speed) {
        animateWorldBorderSize(java.util.Collections.singletonList(player),
                oldDiameter, newDiameter, speed);
    }

    /**
     * Sets warning distance (red screen effect when close to border)
     * @param players Collection of players
     * @param blocks Distance in blocks when warning starts
     */
    public static void setWorldBorderWarningDistance(Collection<Player> players, int blocks) {
        WrapperPlayServerWorldBorder packet = new WrapperPlayServerWorldBorder(
                0,          // center X (not used)
                0,          // center Z (not used)
                0,          // old radius (not used)
                0,          // new radius (not used)
                0,          // speed (not used)
                0,          // portal teleport boundary
                0,          // warning time (not used)
                blocks      // warning blocks
        );

        for (Player player : players) {
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
        }
    }

    /**
     * Sets warning distance for a single player
     */
    public static void setWorldBorderWarningDistance(Player player, int blocks) {
        setWorldBorderWarningDistance(java.util.Collections.singletonList(player), blocks);
    }

    /**
     * Sets warning time (red screen effect before border shrinks)
     * @param players Collection of players
     * @param seconds Time in seconds when warning starts before border changes
     */
    public static void setWorldBorderWarningTime(Collection<Player> players, int seconds) {
        WrapperPlayServerWorldBorder packet = new WrapperPlayServerWorldBorder(
                0,          // center X (not used)
                0,          // center Z (not used)
                0,          // old radius (not used)
                0,          // new radius (not used)
                0,          // speed (not used)
                0,          // portal teleport boundary
                seconds,    // warning time
                0           // warning blocks (not used)
        );

        for (Player player : players) {
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
        }
    }

    /**
     * Sets warning time for a single player
     */
    public static void setWorldBorderWarningTime(Player player, int seconds) {
        setWorldBorderWarningTime(java.util.Collections.singletonList(player), seconds);
    }

    /**
     * Creates a custom world border for a player at their location
     * @param player Target player
     * @param radius Border radius around player
     * @param warningDistance Warning distance in blocks
     */
    public static void createPersonalBorder(Player player, double radius, int warningDistance) {
        Location loc = player.getLocation();
        sendWorldBorderInit(player, loc.getX(), loc.getZ(), radius * 2, warningDistance, 15);
    }

    /**
     * Removes world border by setting it to maximum size
     * @param players Collection of players
     */
    public static void removeWorldBorder(Collection<Player> players) {
        setWorldBorderSize(players, 59999968); // Maximum border size
        setWorldBorderWarningDistance(players, 0);
        setWorldBorderWarningTime(players, 0);
    }

    /**
     * Removes world border for a single player
     */
    public static void removeWorldBorder(Player player) {
        removeWorldBorder(java.util.Collections.singletonList(player));
    }

    /**
     * Creates a shrinking border effect (commonly used in battle royale games)
     * @param players Collection of players
     * @param centerX Center X coordinate
     * @param centerZ Center Z coordinate
     * @param startSize Starting border size
     * @param endSize Final border size
     * @param durationSeconds Duration of shrinking in seconds
     */
    public static void createShrinkingBorder(Collection<Player> players, double centerX, double centerZ,
                                             double startSize, double endSize, int durationSeconds) {
        // Initialize border
        sendWorldBorderInit(players, centerX, centerZ, startSize, 50, 15);

        // Start shrinking animation
        animateWorldBorderSize(players, startSize, endSize, durationSeconds * 1000L);
    }

    /**
     * Follows a player with world border (moving border effect)
     * @param player Target player to follow
     * @param radius Border radius
     */
    public static void followPlayerWithBorder(Player player, double radius) {
        Location loc = player.getLocation();
        setWorldBorderCenter(player, loc.getX(), loc.getZ());
        setWorldBorderSize(player, radius * 2);
    }

    /**
     * Creates a pulsing border effect
     * @param players Collection of players
     * @param centerX Center X
     * @param centerZ Center Z
     * @param minSize Minimum size
     * @param maxSize Maximum size
     * @param pulseTime Time for one pulse cycle in seconds
     */
    public static void createPulsingBorder(Collection<Player> players, double centerX, double centerZ,
                                           double minSize, double maxSize, int pulseTime) {
        // Initialize at min size
        sendWorldBorderInit(players, centerX, centerZ, minSize, 10, 5);

        // Animate to max size
        animateWorldBorderSize(players, minSize, maxSize, pulseTime * 500L);
    }

    /**
     * Checks if PacketEvents is properly loaded
     * @return true if PacketEvents is available
     */
    public static boolean isPacketEventsAvailable() {
        try {
            return PacketEvents.getAPI() != null && PacketEvents.getAPI().isLoaded();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Gets the server version for compatibility checks
     * @return ServerVersion enum
     */
    public static ServerVersion getServerVersion() {
        return PacketEvents.getAPI().getServerManager().getVersion();
    }
}
