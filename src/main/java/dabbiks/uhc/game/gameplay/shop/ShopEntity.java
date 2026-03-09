package dabbiks.uhc.game.gameplay.shop;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import dabbiks.uhc.game.configs.WorldConfig;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static dabbiks.uhc.Main.plugin;

public class ShopEntity {

    public static final List<ShopEntity> shopEntities = new ArrayList<>();

    private HappyGhast happyGhast;
    private Mannequin mannequin;
    private TextDisplay textDisplay;
    private BukkitTask flightTask;
    private final World world;
    private final HashMap<Integer, Location> track;

    private int targetIndex = 1;
    private int boostedTicks;

    public ShopEntity(HashMap<Integer, Location> track, int targetIndex) {
        this.track = track;
        this.targetIndex = targetIndex <= 0 ? 1 : targetIndex;
        this.world = Bukkit.getWorld(WorldConfig.worldName);

        shopEntities.add(this);
    }

    public void spawn() {
        if (world == null || track.isEmpty()) {
            return;
        }

        Location exactLoc = track.getOrDefault(targetIndex, track.values().iterator().next()).clone();

        HappyGhast happyGhast = (HappyGhast) world.spawnEntity(exactLoc, EntityType.HAPPY_GHAST);
        happyGhast.setAI(false);
        happyGhast.setGravity(false);
        happyGhast.setSilent(true);
        happyGhast.setInvulnerable(true);
        happyGhast.setPersistent(true);
        happyGhast.setRemoveWhenFarAway(false);
        this.happyGhast = happyGhast;

        Mannequin mannequin = (Mannequin) world.spawnEntity(exactLoc, EntityType.MANNEQUIN);
        mannequin.setAI(false);
        mannequin.setGravity(false);
        mannequin.setSilent(true);
        mannequin.setInvulnerable(true);
        mannequin.setPersistent(true);
        mannequin.setRemoveWhenFarAway(false);
        this.mannequin = mannequin;

        happyGhast.addPassenger(mannequin);

        TextDisplay textDisplay = (TextDisplay) world.spawnEntity(exactLoc.clone().add(0, 3, 0), EntityType.TEXT_DISPLAY);
        textDisplay.text(net.kyori.adventure.text.Component.text("SKLEP").color(net.kyori.adventure.text.format.NamedTextColor.YELLOW).decorate(net.kyori.adventure.text.format.TextDecoration.BOLD));
        textDisplay.setBillboard(Display.Billboard.CENTER);
        textDisplay.setSeeThrough(true);
        textDisplay.setTeleportDuration(1);
        textDisplay.setPersistent(true);
        this.textDisplay = textDisplay;
    }

    public void applySkinToMannequin(String textureValue) {
        UUID uuid = UUID.nameUUIDFromBytes(textureValue.getBytes());
        PlayerProfile legacyProfile = Bukkit.createProfile(uuid, null);
        legacyProfile.setProperty(new ProfileProperty("textures", textureValue));

        mannequin.setProfile(ResolvableProfile.resolvableProfile(legacyProfile));
    }

    public void setHarness(Material material) {
        happyGhast.getEquipment().setItem(EquipmentSlot.BODY, new ItemStack(material));
    }

    public void startFlightTask() {
        flightTask = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    if (happyGhast == null || happyGhast.isDead()) {
                        if (textDisplay != null && !textDisplay.isDead()) textDisplay.remove();
                        flightTask.cancel();
                        return;
                    }

                    if (track.isEmpty()) return;

                    boolean isStopped = isPlayerClose() || isOtherShopOnTrack();
                    if (isStopped) {
                        boostedTicks++;
                    }

                    boolean isBoosted = boostedTicks > 0;
                    if (!isStopped) {
                        updatePosition(isBoosted);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void updatePosition(boolean isBoosted) {
        int speed = isBoosted ? 2 : 1;

        targetIndex += speed;
        if (targetIndex > track.size()) {
            targetIndex = targetIndex % track.size();
            if (targetIndex == 0) targetIndex = 1;
        }

        Location target = track.get(targetIndex);
        if (target != null) {
            happyGhast.teleport(target);

            if (textDisplay != null && !textDisplay.isDead()) {
                textDisplay.teleport(target.clone().add(0, 3, 0));
            }
        }
    }

    private boolean isPlayerClose() {
        for (Entity entity : happyGhast.getNearbyEntities(2.5, 4.0, 2.5)) {
            if (!(entity instanceof Player)) continue;
            if (entity.getLocation().getY() >= happyGhast.getLocation().getY() + 2) return true;
        }
        return false;
    }

    private boolean isOtherShopOnTrack() {
        if (track.isEmpty()) return false;

        for (ShopEntity other : shopEntities) {
            if (other == this) continue;

            int idxDiff = (other.targetIndex - this.targetIndex + track.size()) % track.size();

            if (idxDiff > 0 && idxDiff <= 30) {
                return true;
            }
        }
        return false;
    }

    public Location getPosition() {
        return track.getOrDefault(targetIndex, happyGhast != null ? happyGhast.getLocation() : null);
    }
}