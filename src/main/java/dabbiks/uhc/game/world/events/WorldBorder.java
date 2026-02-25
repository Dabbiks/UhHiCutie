package dabbiks.uhc.game.world.events;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerInitializeWorldBorder;
import dabbiks.uhc.game.configs.SegmentConfig;
import dabbiks.uhc.game.configs.WorldConfig;
import dabbiks.uhc.utils.managers.BorderManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static dabbiks.uhc.Main.*;

public class WorldBorder {

    public static double borderSize = WorldConfig.worldBorderSize * 2;

    public void prepareWorldBorder() {
        Bukkit.getWorld(WorldConfig.worldName).getWorldBorder().setSize(WorldConfig.worldBorderSize * 2);
        Bukkit.getWorld(WorldConfig.worldName).getWorldBorder().setDamageAmount(0);
    }

    public boolean isBorderGrowing = false;
    public int timer = 0;

    public void setBorderSize(double amountReduced, long millis) {
        if (isBorderGrowing) return;

        double oldSize = borderSize;
        if (borderSize < 2) {
            borderSize = 0.1;
        } else {
            borderSize = borderSize - amountReduced;
        }

        List<Player> targetPlayers = new ArrayList<>();
        for (Player player : playerListU.getAllPlayers()) {
            if (player.getWorld().getName().equals(WorldConfig.worldName)) {
                targetPlayers.add(player);
            }
        }

        BorderManager.animateWorldBorderSize(targetPlayers, oldSize, borderSize, millis);

        for (Player player : playerListU.getPlayingPlayers()) {
            int x = player.getLocation().getBlockX();
            int z = player.getLocation().getBlockZ();
            if (x > borderSize / 2 || x < ((borderSize / 2) * -1) || z > borderSize / 2 || z < ((borderSize / 2) * -1)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 30, 3, false, false));
            }
        }
    }

    public static boolean isBorderClose(Player player) {
        int x = player.getLocation().getBlockX();
        int z = player.getLocation().getBlockZ();
        int higher = 0;
        if (Math.abs(x) > Math.abs(z)) {
            higher = Math.abs(x);
        }
        if (Math.abs(z) >= Math.abs(x)) {
            higher = Math.abs(z);
        }
        if (borderSize / 2 - higher < 10) {
            return true;
        }
        return false;
    }

    public void manageBorder() {
        int actualSegment = SegmentConfig.actualSegment;

        if (timeU.getTime() == (SegmentConfig.firstBorderStageSegment * (SegmentConfig.eachSegmentTime))) {
            titleU.sendTitleToPlayers(playerListU.getAllPlayers(), "§e⚠", "§fGranica ruszyła!", 60);
            soundU.playSoundToPlayers(playerListU.getAllPlayers(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.3F, 1);
        }

        if (timeU.getTime() == (SegmentConfig.secondBorderStageSegment * (SegmentConfig.eachSegmentTime))) {
            titleU.sendTitleToPlayers(playerListU.getAllPlayers(), "§a⚠", "§fGranica się zatrzymała!", 60);
            soundU.playSoundToPlayers(playerListU.getAllPlayers(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.3F, 1);
        }

        if (timeU.getTime() == (SegmentConfig.thirdBorderStageSegment * (SegmentConfig.eachSegmentTime))) {
            titleU.sendTitleToPlayers(playerListU.getAllPlayers(), "§e⚠", "§fGranica ruszyła!", 60);
            soundU.playSoundToPlayers(playerListU.getAllPlayers(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.3F, 1);
        }

        if (timer == 1) { timer = 0; return; }

        if (actualSegment > SegmentConfig.firstBorderStageSegment && actualSegment <= SegmentConfig.secondBorderStageSegment && !isBorderGrowing) {
            setBorderSize(1.6, 1000L);
        } else if (actualSegment > SegmentConfig.secondBorderStageSegment && actualSegment <= SegmentConfig.thirdBorderStageSegment && !isBorderGrowing) {
            return;
        } else if (actualSegment > SegmentConfig.thirdBorderStageSegment && !isBorderGrowing && borderSize > 6) {
            setBorderSize(1.6, 1000L);
        }

        for (Player player : playerListU.getAllPlayers()) {
            if (actualSegment >= SegmentConfig.firstBorderStageSegment && actualSegment <= 32 && isBorderClose(player)) {
                player.playSound(player, Sound.ENTITY_ENDERMAN_AMBIENT, 0.2F, 2);
            } else if (isBorderClose(player) && SegmentConfig.actualSegment > 32) {
                player.playSound(player, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.3F, 2);
                player.playSound(player, Sound.AMBIENT_CAVE, 0.3F, 2);
            }
        }

        timer++;
    }

}
