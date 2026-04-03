package dabbiks.uhc.player.punishments;

import dabbiks.uhc.game.gameplay.items.ItemTags;
import de.tr7zw.nbtapi.NBTItem;
import fr.mrmicky.fastinv.FastInv;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

import static dabbiks.uhc.Main.symbolU;

public class PunishmentMenu {

    private static void fillBackground(FastInv inv) {
        ItemStack bg = createItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < inv.getInventory().getSize(); i++) {
            if (inv.getInventory().getItem(i) == null) {
                inv.setItem(i, bg);
            }
        }
    }

    public static class MainMenu extends FastInv {
        public MainMenu(Player admin, OfflinePlayer target) {
            super(36, "§8Kary: " + target.getName());

            setItem(11, createItem(Material.DIAMOND_SWORD, "§cBan", "§7Trwale wyklucza gracza z serwera.", "", symbolU.MOUSE_LEFT + " §7Wybierz"), e -> new ReasonMenu(admin, target, PunishmentManager.Type.BAN).open(admin));
            setItem(12, createItem(Material.IRON_SWORD, "§6TempBan", "§7Tymczasowo wyklucza gracza z serwera.", "", symbolU.MOUSE_LEFT + " §7Wybierz"), e -> new ReasonMenu(admin, target, PunishmentManager.Type.TEMPBAN).open(admin));
            setItem(13, createItem(Material.BARRIER, "§4Mute (Cały czat)", "§7Całkowicie blokuje pisanie na czacie.", "", symbolU.MOUSE_LEFT + " §7Wybierz"), e -> new ReasonMenu(admin, target, PunishmentManager.Type.MUTE_ALL).open(admin));
            setItem(14, createItem(Material.PAPER, "§eMute (Globalny)", "§7Blokuje czat globalny (pozwala na drużynowy).", "", symbolU.MOUSE_LEFT + " §7Wybierz"), e -> new ReasonMenu(admin, target, PunishmentManager.Type.MUTE_GLOBAL).open(admin));
            setItem(15, createItem(Material.IRON_BOOTS, "§7Kick", "§7Wyrzuca gracza z serwera.", "", symbolU.MOUSE_LEFT + " §7Wybierz"), e -> new ReasonMenu(admin, target, PunishmentManager.Type.KICK).open(admin));

            setItem(31, createItem(Material.MILK_BUCKET, "§aWyczyść wszystkie kary", "§7Usuwa bany i wyciszenia gracza.", "", symbolU.MOUSE_LEFT + " §7Wyczyść"), e -> {
                admin.closeInventory();
                PunishmentManager.clearAll(admin, target);
            });

            fillBackground(this);
        }
    }

    public static class ReasonMenu extends FastInv {
        public ReasonMenu(Player admin, OfflinePlayer target, PunishmentManager.Type type) {
            super(36, "§8Powód: " + type.getName());

            List<String> reasons = (type == PunishmentManager.Type.BAN || type == PunishmentManager.Type.TEMPBAN)
                    ? Arrays.asList("Cheaty", "Reklama", "Bugowanie", "Niedozwolone modyfikacje", "Niesportowe zachowanie")
                    : Arrays.asList("Spam", "Wulgaryzmy", "Obraza gracza/administracji", "Toksyczność", "Niesportowe zachowanie");

            int[] slots = {11, 12, 13, 14, 15};
            for (int i = 0; i < reasons.size(); i++) {
                String reason = reasons.get(i);
                setItem(slots[i], createItem(Material.NAME_TAG, "§e" + reason, "", symbolU.MOUSE_LEFT + " §7Wybierz powód"), e -> {
                    if (type == PunishmentManager.Type.BAN || type == PunishmentManager.Type.KICK) {
                        admin.closeInventory();
                        PunishmentManager.execute(admin, target, type, reason, -1);
                    } else {
                        new DurationMenu(admin, target, type, reason).open(admin);
                    }
                });
            }

            fillBackground(this);
        }
    }

    public static class DurationMenu extends FastInv {
        public DurationMenu(Player admin, OfflinePlayer target, PunishmentManager.Type type, String reason) {
            super(36, "§8Czas: " + type.getName());

            setItem(11, createItem(Material.CLOCK, "§a5 minut", "", symbolU.MOUSE_LEFT + " §7Wybierz czas"), e -> punish(admin, target, type, reason, 5L * 60 * 1000));
            setItem(12, createItem(Material.CLOCK, "§e15 minut", "", symbolU.MOUSE_LEFT + " §7Wybierz czas"), e -> punish(admin, target, type, reason, 15L * 60 * 1000));
            setItem(13, createItem(Material.CLOCK, "§61 godzina", "", symbolU.MOUSE_LEFT + " §7Wybierz czas"), e -> punish(admin, target, type, reason, 60L * 60 * 1000));
            setItem(14, createItem(Material.CLOCK, "§c1 dzień", "", symbolU.MOUSE_LEFT + " §7Wybierz czas"), e -> punish(admin, target, type, reason, 24L * 60 * 60 * 1000));
            setItem(15, createItem(Material.BEDROCK, "§4Permanentnie", "", symbolU.MOUSE_LEFT + " §7Wybierz czas"), e -> punish(admin, target, type, reason, -1));

            fillBackground(this);
        }

        private void punish(Player admin, OfflinePlayer target, PunishmentManager.Type type, String reason, long time) {
            admin.closeInventory();
            PunishmentManager.execute(admin, target, type, reason, time);
        }
    }

    private static ItemStack createItem(Material mat, String name, String... lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            if (lore.length > 0) {
                meta.setLore(Arrays.asList(lore));
            }
            item.setItemMeta(meta);
        }

        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setInteger(ItemTags.UHC_ITEM.name(), 1);
        return nbtItem.getItem();
    }
}