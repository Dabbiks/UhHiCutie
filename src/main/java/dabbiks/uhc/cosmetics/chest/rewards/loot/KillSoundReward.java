package dabbiks.uhc.cosmetics.chest.rewards.loot;

import dabbiks.uhc.cosmetics.CosmeticTier;
import dabbiks.uhc.cosmetics.KillSound;
import dabbiks.uhc.cosmetics.chest.rewards.Reward;
import dabbiks.uhc.player.data.persistent.PersistentData;
import dabbiks.uhc.player.data.persistent.PersistentStats;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static dabbiks.uhc.Main.*;

public class KillSoundReward extends Reward {

    private final KillSound killSound;
    private final CosmeticTier tier;

    public KillSoundReward(CosmeticTier tier) {
        this.tier = tier;
        List<KillSound> availableSounds = Arrays.stream(KillSound.values())
                .filter(s -> s.getTier() == tier)
                .toList();

        if (availableSounds.isEmpty()) {
            throw new IllegalArgumentException("No sounds for tier: " + tier);
        }

        this.killSound = availableSounds.get(ThreadLocalRandom.current().nextInt(availableSounds.size()));
    }

    @Override
    public String getType() {
        String color = switch (tier) {
            case COMMON -> "§7";
            case RARE -> "§b";
            case EPIC -> "§d";
            case MYTHIC -> "§c";
            case LEGENDARY -> "§6";
            case PRESTIGE -> "§4BŁĄD";
            case EASTER -> "§e";
        };
        return color + "§lDŹWIĘK";
    }

    @Override
    public void addReward(PersistentData persistentData) {
        if (persistentData.hasKillSound(killSound)) {
            persistentData.addStats(PersistentStats.POWDER, killSound.getPowderCost() / 4);

            Player player = Bukkit.getPlayer(persistentData.getName());
            if (player == null) return;
            player.sendMessage("§7Otrzymujesz §f" + killSound.getPowderCost() / 4 + symbolU.SCOREBOARD_POWDER + "§7 za §e" + killSound.getName());
        } else {
            persistentData.unlockKillSound(killSound);
        }
    }

    @Override
    public String getName() {
        return killSound.getName();
    }

    @Override
    public ItemStack getItem() {
        ItemStack item = new ItemStack(killSound.getMaterial());
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            item.setItemMeta(meta);
        }
        return item;
    }

    @Override
    public void spawnEffect(Location loc) {
        switch (tier) {
            case COMMON -> fireworkU.spawnBurst(loc, Color.WHITE);
            case RARE -> fireworkU.spawnBurst(loc, Color.AQUA);
            case EPIC -> fireworkU.spawnBurst(loc, Color.LIME);
            case MYTHIC -> {
                fireworkU.spawnBurst(loc, Color.FUCHSIA);
                fireworkU.instantExplode(loc, Color.WHITE);
            }
            case LEGENDARY -> {
                for (int i = 0; i <= 3; i++) {
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        fireworkU.spawnBurst(loc, Color.PURPLE);
                        fireworkU.instantExplode(loc, Color.WHITE);
                    }, i * 5L);
                }
            }
        }
    }
}