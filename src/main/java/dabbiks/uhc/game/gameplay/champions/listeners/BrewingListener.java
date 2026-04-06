package dabbiks.uhc.game.gameplay.champions.listeners;

import dabbiks.uhc.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class BrewingListener implements Listener {

    private final NamespacedKey CUSTOM_POTION_KEY = new NamespacedKey(Main.plugin, "custom_potion");

    @EventHandler
    public void onBrew(BrewEvent event) {
        BrewerInventory inv = event.getContents();

        boolean[] activeSlots = new boolean[3];
        for (int i = 0; i < 3; i++) {
            ItemStack item = inv.getItem(i);
            if (item != null && item.getType() != Material.AIR) {
                activeSlots[i] = true;
            }
        }

        Bukkit.getScheduler().runTaskLater(Main.plugin, () -> {
            for (int i = 0; i < 3; i++) {
                if (activeSlots[i]) {
                    ItemStack result = inv.getItem(i);
                    if (result != null && result.hasItemMeta() && result.getItemMeta() instanceof PotionMeta) {
                        modifyPotion(result);
                        inv.setItem(i, result);
                    }
                }
            }
        }, 1L);
    }

    private void modifyPotion(ItemStack item) {
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        PotionType type = meta.getBasePotionType();

        meta.clearCustomEffects();
        meta.getPersistentDataContainer().set(CUSTOM_POTION_KEY, PersistentDataType.BYTE, (byte) 1);

        int durationStart = 100;
        int durationLong = 300;

        int ampStart = 0;
        int ampStrong = 1;

        switch (type) {
            case AWKWARD -> apply(meta, null, 0, 0, "§fKlarowna Mikstura");

            case SWIFTNESS -> apply(meta, PotionEffectType.SPEED, durationStart, ampStart, "§bMikstura Szybkości");
            case LONG_SWIFTNESS -> apply(meta, PotionEffectType.SPEED, durationLong, ampStart, "§bWydłużona Mikstura Szybkości");
            case STRONG_SWIFTNESS -> apply(meta, PotionEffectType.SPEED, durationStart, ampStrong, "§bWzmocniona Mikstura Szybkości");

            case LEAPING -> apply(meta, PotionEffectType.JUMP_BOOST, durationStart, ampStart, "§aMikstura Skoku");
            case LONG_LEAPING -> apply(meta, PotionEffectType.JUMP_BOOST, durationLong, ampStart, "§aWydłużona Mikstura Skoku");
            case STRONG_LEAPING -> apply(meta, PotionEffectType.JUMP_BOOST, durationStart, ampStrong, "§aWzmocniona Mikstura Skoku");

            case STRENGTH -> apply(meta, PotionEffectType.STRENGTH, durationStart, ampStart, "§cMikstura Siły");
            case LONG_STRENGTH -> apply(meta, PotionEffectType.STRENGTH, durationLong, ampStart, "§cWydłużona Mikstura Siły");
            case STRONG_STRENGTH -> apply(meta, PotionEffectType.STRENGTH, durationStart, ampStrong, "§cWzmocniona Mikstura Siły");

            case HEALING -> apply(meta, PotionEffectType.INSTANT_HEALTH, 0, ampStart, "§cMikstura Leczenia");
            case STRONG_HEALING -> apply(meta, PotionEffectType.INSTANT_HEALTH, 0, ampStrong, "§cWzmocniona Mikstura Leczenia");

            case HARMING -> apply(meta, PotionEffectType.INSTANT_DAMAGE, 0, ampStart, "§4Mikstura Obrażeń");
            case STRONG_HARMING -> apply(meta, PotionEffectType.INSTANT_DAMAGE, 0, ampStrong, "§4Wzmocniona Mikstura Obrażeń");

            case POISON -> apply(meta, PotionEffectType.POISON, durationStart, ampStart, "§2Mikstura Trucizny");
            case LONG_POISON -> apply(meta, PotionEffectType.POISON, durationLong, ampStart, "§2Wydłużona Mikstura Trucizny");
            case STRONG_POISON -> apply(meta, PotionEffectType.POISON, durationStart, ampStrong, "§2Wzmocniona Mikstura Trucizny");

            case REGENERATION -> apply(meta, PotionEffectType.REGENERATION, durationStart, ampStart, "§dMikstura Regeneracji");
            case LONG_REGENERATION -> apply(meta, PotionEffectType.REGENERATION, durationLong, ampStart, "§dWydłużona Mikstura Regeneracji");
            case STRONG_REGENERATION -> apply(meta, PotionEffectType.REGENERATION, durationStart, ampStrong, "§dWzmocniona Mikstura Regeneracji");

            case FIRE_RESISTANCE -> apply(meta, PotionEffectType.FIRE_RESISTANCE, durationStart, ampStart, "§6Mikstura Odporności na Ogień");
            case LONG_FIRE_RESISTANCE -> apply(meta, PotionEffectType.FIRE_RESISTANCE, durationLong, ampStart, "§6Wydłużona Mikstura Odporności na Ogień");

            case WATER_BREATHING -> apply(meta, PotionEffectType.WATER_BREATHING, durationStart, ampStart, "§1Mikstura Oddychania pod Wodą");
            case LONG_WATER_BREATHING -> apply(meta, PotionEffectType.WATER_BREATHING, durationLong, ampStart, "§1Wydłużona Mikstura Oddychania pod Wodą");

            case NIGHT_VISION -> apply(meta, PotionEffectType.NIGHT_VISION, durationStart, ampStart, "§5Mikstura Widzenia w Ciemności");
            case LONG_NIGHT_VISION -> apply(meta, PotionEffectType.NIGHT_VISION, durationLong, ampStart, "§5Wydłużona Mikstura Widzenia w Ciemności");

            case INVISIBILITY -> apply(meta, PotionEffectType.INVISIBILITY, durationStart, ampStart, "§7Mikstura Niewidzialności");
            case LONG_INVISIBILITY -> apply(meta, PotionEffectType.INVISIBILITY, durationLong, ampStart, "§7Wydłużona Mikstura Niewidzialności");

            case WEAKNESS -> apply(meta, PotionEffectType.WEAKNESS, durationStart, ampStart, "§8Mikstura Osłabienia");
            case LONG_WEAKNESS -> apply(meta, PotionEffectType.WEAKNESS, durationLong, ampStart, "§8Wydłużona Mikstura Osłabienia");

            case SLOWNESS -> apply(meta, PotionEffectType.SLOWNESS, durationStart, ampStart, "§8Mikstura Spowolnienia");
            case LONG_SLOWNESS -> apply(meta, PotionEffectType.SLOWNESS, durationLong, ampStart, "§8Wydłużona Mikstura Spowolnienia");
            case STRONG_SLOWNESS -> apply(meta, PotionEffectType.SLOWNESS, durationStart, ampStrong, "§8Wzmocniona Mikstura Spowolnienia");

            case SLOW_FALLING -> apply(meta, PotionEffectType.SLOW_FALLING, durationStart, ampStart, "§fMikstura Powolnego Opadania");
            case LONG_SLOW_FALLING -> apply(meta, PotionEffectType.SLOW_FALLING, durationLong, ampStart, "§fWydłużona Mikstura Powolnego Opadania");

            case TURTLE_MASTER -> {
                apply(meta, PotionEffectType.SLOWNESS, durationStart, ampStart, "§5Mikstura Żółwiego Mistrza");
                meta.addCustomEffect(new PotionEffect(PotionEffectType.RESISTANCE, durationStart, ampStart), true);
            }
            case LONG_TURTLE_MASTER -> {
                apply(meta, PotionEffectType.SLOWNESS, durationLong, ampStart, "§5Wydłużona Mikstura Żółwiego Mistrza");
                meta.addCustomEffect(new PotionEffect(PotionEffectType.RESISTANCE, durationLong, ampStart), true);
            }
            case STRONG_TURTLE_MASTER -> {
                apply(meta, PotionEffectType.SLOWNESS, durationStart, ampStrong, "§5Wzmocniona Mikstura Żółwiego Mistrza");
                meta.addCustomEffect(new PotionEffect(PotionEffectType.RESISTANCE, durationStart, ampStrong), true);
            }
            default -> {}
        }
        item.setItemMeta(meta);
    }

    private void apply(PotionMeta meta, PotionEffectType effect, int duration, int amp, String name) {
        meta.setBasePotionType(PotionType.AWKWARD);

        if (effect != null) {
            meta.addCustomEffect(new PotionEffect(effect, duration, amp), true);
        }

        meta.setColor(null);
        meta.setDisplayName(name);
    }
}