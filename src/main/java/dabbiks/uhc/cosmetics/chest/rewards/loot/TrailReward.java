package dabbiks.uhc.cosmetics.chest.rewards.loot;

import dabbiks.uhc.cosmetics.CosmeticTier;
import dabbiks.uhc.cosmetics.ParticleTrail;
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

public class TrailReward extends Reward {

    private final ParticleTrail trail;
    private final CosmeticTier tier;

    public TrailReward(CosmeticTier tier) {
        this.tier = tier;
        List<ParticleTrail> availableTrails = Arrays.stream(ParticleTrail.values())
                .filter(s -> s.getTier() == tier)
                .toList();

        if (availableTrails.isEmpty()) {
            throw new IllegalArgumentException("No trails for tier: " + tier);
        }

        this.trail = availableTrails.get(ThreadLocalRandom.current().nextInt(availableTrails.size()));
    }

    @Override
    public String getType() {
        String color = switch (tier) {
            case COMMON -> "§7";
            case RARE -> "§b";
            case EPIC -> "§d";
            case MYTHIC -> "§c";
            case LEGENDARY -> "§6";
        };
        return color + "§lSMUGA LOTU";
    }

    @Override
    public void addReward(PersistentData persistentData) {
        if (persistentData.hasTrail(trail)) {
            int compensation = trail.getPowderCost() / 4;
            persistentData.addStats(PersistentStats.POWDER, compensation);

            Player player = Bukkit.getPlayer(persistentData.getName());
            if (player != null) {
                player.sendMessage("§7Otrzymujesz §f" + compensation + symbolU.SCOREBOARD_POWDER + "§7 za §e" + trail.getDisplayName());
            }
        } else {
            persistentData.unlockTrail(trail);
        }
    }

    @Override
    public String getName() {
        return trail.getDisplayName();
    }

    @Override
    public ItemStack getItem() {
        ItemStack item = new ItemStack(trail.getMaterial());
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