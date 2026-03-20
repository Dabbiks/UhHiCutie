package dabbiks.uhc.player.events;

import dabbiks.uhc.player.data.session.SessionData;
import dabbiks.uhc.player.data.session.SessionDataManager;
import dabbiks.uhc.player.data.session.SessionTags;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class Fishing implements Listener {

    private final Map<UUID, Long> regenCooldowns = new HashMap<>();
    private final Random random = new Random();
    private final Material[] customDrops = {
            Material.LEATHER,
            Material.RABBIT_HIDE,
            Material.BAMBOO,
            Material.FEATHER
    };

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
                event.getFrom().getBlockY() == event.getTo().getBlockY() &&
                event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        Player player = event.getPlayer();
        SessionData sessionData = SessionDataManager.getData(player.getUniqueId());

        if (sessionData == null || !sessionData.hasTag(SessionTags.FISHERMAN)) {
            return;
        }

        boolean wasInWater = event.getFrom().getBlock().getType() == Material.WATER;
        boolean isInWater = event.getTo().getBlock().getType() == Material.WATER;

        if (!wasInWater && isInWater) {
            long currentTime = System.currentTimeMillis();
            long lastUsed = regenCooldowns.getOrDefault(player.getUniqueId(), 0L);

            int level = 1;
            int cooldownSeconds = 24 - level;
            long cooldownMillis = cooldownSeconds * 1000L;

            if (currentTime - lastUsed >= cooldownMillis) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 3 * 20, 1));
                regenCooldowns.put(player.getUniqueId(), currentTime);
            }
        }
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
            return;
        }

        Player player = event.getPlayer();
        SessionData sessionData = SessionDataManager.getData(player.getUniqueId());

        if (sessionData != null && sessionData.hasTag(SessionTags.MORE_FISHING_DROPS)) {
            if (random.nextInt(100) < 20) {
                if (event.getCaught() instanceof Item) {
                    Item caughtEntity = (Item) event.getCaught();
                    Material rolledMaterial = customDrops[random.nextInt(customDrops.length)];
                    int amount = random.nextInt(3) + 1;
                    caughtEntity.setItemStack(new ItemStack(rolledMaterial, amount));
                }
            }
        }
    }
}