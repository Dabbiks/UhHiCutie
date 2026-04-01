package dabbiks.uhc.cosmetics.chest.rewards.loot;

import dabbiks.uhc.cosmetics.CosmeticTier;
import dabbiks.uhc.cosmetics.Wardrobe;
import dabbiks.uhc.cosmetics.chest.rewards.Reward;
import dabbiks.uhc.player.data.persistent.PersistentData;
import dabbiks.uhc.player.data.persistent.PersistentStats;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.URL;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static dabbiks.uhc.Main.*;

public class WardrobeReward extends Reward {

    private final Wardrobe wardrobe;
    private final CosmeticTier tier;
    private final int pieceType;

    public WardrobeReward(CosmeticTier tier) {
        this.tier = tier;
        List<Wardrobe> availableWardrobes = Arrays.stream(Wardrobe.values())
                .filter(w -> w.getTier() == tier)
                .toList();

        if (availableWardrobes.isEmpty()) {
            throw new IllegalArgumentException("No wardrobes for tier: " + tier);
        }

        this.wardrobe = availableWardrobes.get(ThreadLocalRandom.current().nextInt(availableWardrobes.size()));
        this.pieceType = ThreadLocalRandom.current().nextInt(4);
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
        return color + "§lGARDEROBA";
    }

    @Override
    public void addReward(PersistentData persistentData) {
        boolean hasPiece = false;
        switch (pieceType) {
            case 0 -> hasPiece = persistentData.hasWardrobeHelmet(wardrobe);
            case 1 -> hasPiece = persistentData.hasWardrobeChestplate(wardrobe);
            case 2 -> hasPiece = persistentData.hasWardrobeLeggings(wardrobe);
            case 3 -> hasPiece = persistentData.hasWardrobeBoots(wardrobe);
        }

        if (hasPiece) {
            int powder = switch (tier) {
                case COMMON -> 125;
                case RARE -> 312;
                case EPIC -> 625;
                case MYTHIC -> 1250;
                case LEGENDARY -> 3125;
                default -> 25;
            };

            persistentData.addStats(PersistentStats.POWDER, powder);

            Player player = Bukkit.getPlayer(persistentData.getName());
            if (player == null) return;
            player.sendMessage("§7Otrzymujesz §f" + powder + symbolU.SCOREBOARD_POWDER + "§7 za §e" + getName());
        } else {
            switch (pieceType) {
                case 0 -> persistentData.unlockWardrobeHelmet(wardrobe);
                case 1 -> persistentData.unlockWardrobeChestplate(wardrobe);
                case 2 -> persistentData.unlockWardrobeLeggings(wardrobe);
                case 3 -> persistentData.unlockWardrobeBoots(wardrobe);
            }
        }
    }

    @Override
    public String getName() {
        return switch (pieceType) {
            case 0 -> wardrobe.getHelmetName();
            case 1 -> wardrobe.getChestplateName();
            case 2 -> wardrobe.getLeggingsName();
            case 3 -> wardrobe.getBootsName();
            default -> wardrobe.getSetName();
        };
    }

    @Override
    public ItemStack getItem() {
        ItemStack item = new ItemStack(Material.AIR);
        switch (pieceType) {
            case 0 -> {
                if (wardrobe.getHeadTexture() != null && !wardrobe.getHeadTexture().isEmpty()) {
                    item = new ItemStack(Material.PLAYER_HEAD);
                    SkullMeta meta = (SkullMeta) item.getItemMeta();
                    PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
                    PlayerTextures textures = profile.getTextures();

                    try {
                        String decoded = new String(Base64.getDecoder().decode(wardrobe.getHeadTexture()));
                        String url = decoded.split("\"url\":\"")[1].split("\"")[0];
                        textures.setSkin(new URL(url));
                        profile.setTextures(textures);
                        meta.setOwnerProfile(profile);
                    } catch (Exception ignored) {}

                    item.setItemMeta(meta);
                } else if (wardrobe.getHelmetColor() != null) {
                    item = new ItemStack(Material.LEATHER_HELMET);
                    LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
                    meta.setColor(wardrobe.getHelmetColor());
                    item.setItemMeta(meta);
                }
            }
            case 1 -> {
                if (wardrobe.getChestplateColor() != null) {
                    item = new ItemStack(Material.LEATHER_CHESTPLATE);
                    LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
                    meta.setColor(wardrobe.getChestplateColor());
                    item.setItemMeta(meta);
                }
            }
            case 2 -> {
                if (wardrobe.getLeggingsColor() != null) {
                    item = new ItemStack(Material.LEATHER_LEGGINGS);
                    LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
                    meta.setColor(wardrobe.getLeggingsColor());
                    item.setItemMeta(meta);
                }
            }
            case 3 -> {
                if (wardrobe.getBootsColor() != null) {
                    item = new ItemStack(Material.LEATHER_BOOTS);
                    LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
                    meta.setColor(wardrobe.getBootsColor());
                    item.setItemMeta(meta);
                }
            }
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
            case EASTER -> fireworkU.spawnBurst(loc, Color.YELLOW);
        }
    }
}