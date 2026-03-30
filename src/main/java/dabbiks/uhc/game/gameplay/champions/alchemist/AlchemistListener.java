package dabbiks.uhc.game.gameplay.champions.alchemist;

import dabbiks.uhc.player.data.persistent.PersistentData;
import dabbiks.uhc.player.data.persistent.PersistentDataManager;
import dabbiks.uhc.player.data.session.SessionData;
import dabbiks.uhc.player.data.session.SessionDataManager;
import dabbiks.uhc.player.data.session.SessionTags;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Team;

import java.util.Collection;

import static dabbiks.uhc.Main.plugin;

public class AlchemistListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getView().getTopInventory().getType() != InventoryType.BREWING) return;

        SessionData sessionData = SessionDataManager.getData(player.getUniqueId());
        boolean canUseBrewingStand = sessionData != null && sessionData.hasTag(SessionTags.CAN_USE_BREWING_STAND);

        if (canUseBrewingStand) return;

        if (event.getClickedInventory() != null && event.getClickedInventory().getType() == InventoryType.BREWING) {
            if (event.getSlot() == 3 || event.getSlot() == 4) {
                event.setCancelled(true);
            }
        } else if (event.isShiftClick()) {
            ItemStack item = event.getCurrentItem();
            if (item != null) {
                Material type = item.getType();
                if (type != Material.POTION && type != Material.SPLASH_POTION && type != Material.LINGERING_POTION && type != Material.GLASS_BOTTLE) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getView().getTopInventory().getType() != InventoryType.BREWING) return;

        SessionData sessionData = SessionDataManager.getData(player.getUniqueId());
        boolean canUseBrewingStand = sessionData != null && sessionData.hasTag(SessionTags.CAN_USE_BREWING_STAND);

        if (canUseBrewingStand) return;

        for (int slot : event.getRawSlots()) {
            if (slot == 3 || slot == 4) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onPotionDrink(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item.getType() != Material.POTION) return;

        SessionData sessionData = SessionDataManager.getData(player.getUniqueId());
        if (sessionData == null || !sessionData.hasTag(SessionTags.POTION_SHARING)) return;

        PersistentData persistentData = PersistentDataManager.getData(player.getUniqueId());
        int level = persistentData != null ? persistentData.getChampionLevel("alchemist") : 1;
        int effectSharingRange = 3 + (level / 2);

        Collection<PotionEffect> before = player.getActivePotionEffects();

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!player.isOnline()) return;

            Collection<PotionEffect> after = player.getActivePotionEffects();
            Team team = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(player.getName());

            for (PotionEffect effect : after) {
                boolean isNewOrUpgraded = true;
                for (PotionEffect old : before) {
                    if (old.getType().equals(effect.getType())) {
                        if (old.getAmplifier() >= effect.getAmplifier() && old.getDuration() >= effect.getDuration()) {
                            isNewOrUpgraded = false;
                        }
                        break;
                    }
                }

                if (isNewOrUpgraded) {
                    for (Player target : player.getWorld().getPlayers()) {
                        if (target.equals(player)) continue;
                        if (target.getLocation().distance(player.getLocation()) > effectSharingRange) continue;

                        if (team != null && team.hasEntry(target.getName())) {
                            target.addPotionEffect(effect);
                        }
                    }
                }
            }
        }, 1L);
    }
}