package dabbiks.uhc.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;

public class SoundUtils {

    public void playSoundToPlayer(Player player, Sound sound, float volume, float pitch) {
        player.playSound(player, sound, volume, pitch);
    }

    public void playSoundToPlayers(List<Player> players, Sound sound, float volume, float pitch) {
        for (Player player : players) {
            player.playSound(player, sound, volume, pitch);
        }
    }

    public void playSoundAtPlayer(Player player, Sound sound, float volume, float pitch) {
        player.getWorld().playSound(player.getLocation(), sound, volume, pitch);
    }

    public void playSoundAtPlayers(List<Player> players, Sound sound, float volume, float pitch) {
        for (Player player : players) {
            player.getWorld().playSound(player.getLocation(), sound, volume, pitch);
        }
    }

    public void playSoundAtLocation(Location location, Sound sound, float volume, float pitch) {
        location.getWorld().playSound(location, sound, volume, pitch);
    }

}
