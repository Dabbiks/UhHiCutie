package dabbiks.uhc.player.punishments;

import fr.mrmicky.fastinv.FastInv;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class PunishmentMenu {

    public static class MainMenu extends FastInv {
        public MainMenu(Player admin, OfflinePlayer target) {
            super(27, "§8Kary: " + target.getName());

            setItem(11, createItem(Material.DIAMOND_SWORD, "§cBan"), e -> new ReasonMenu(admin, target, PunishmentManager.Type.BAN).open(admin));
            setItem(12, createItem(Material.IRON_SWORD, "§6TempBan"), e -> new ReasonMenu(admin, target, PunishmentManager.Type.TEMPBAN).open(admin));
            setItem(13, createItem(Material.BARRIER, "§4Mute (Cały czat)"), e -> new ReasonMenu(admin, target, PunishmentManager.Type.MUTE_ALL).open(admin));
            setItem(14, createItem(Material.PAPER, "§eMute (Globalny)"), e -> new ReasonMenu(admin, target, PunishmentManager.Type.MUTE_GLOBAL).open(admin));
            setItem(15, createItem(Material.IRON_BOOTS, "§7Kick"), e -> new ReasonMenu(admin, target, PunishmentManager.Type.KICK).open(admin));
        }
    }

    public static class ReasonMenu extends FastInv {
        public ReasonMenu(Player admin, OfflinePlayer target, PunishmentManager.Type type) {
            super(27, "§8Powód: " + type.getName());

            List<String> reasons = (type == PunishmentManager.Type.BAN || type == PunishmentManager.Type.TEMPBAN)
                    ? Arrays.asList("Cheaty", "Reklama", "Bugowanie", "Niedozwolone modyfikacje")
                    : Arrays.asList("Spam", "Wulgaryzmy", "Obraza gracza/administracji", "Toksyczność");

            int slot = 10;
            for (String reason : reasons) {
                setItem(slot++, createItem(Material.NAME_TAG, "§e" + reason), e -> {
                    if (type == PunishmentManager.Type.BAN || type == PunishmentManager.Type.KICK) {
                        admin.closeInventory();
                        PunishmentManager.execute(admin, target, type, reason, -1);
                    } else {
                        new DurationMenu(admin, target, type, reason).open(admin);
                    }
                });
            }
        }
    }

    public static class DurationMenu extends FastInv {
        public DurationMenu(Player admin, OfflinePlayer target, PunishmentManager.Type type, String reason) {
            super(27, "§8Czas: " + type.getName());

            setItem(11, createItem(Material.CLOCK, "§a5 minut"), e -> punish(admin, target, type, reason, 5L * 60 * 1000));
            setItem(12, createItem(Material.CLOCK, "§e15 minut"), e -> punish(admin, target, type, reason, 15L * 60 * 1000));
            setItem(13, createItem(Material.CLOCK, "§61 godzina"), e -> punish(admin, target, type, reason, 60L * 60 * 1000));
            setItem(14, createItem(Material.CLOCK, "§c1 dzień"), e -> punish(admin, target, type, reason, 24L * 60 * 60 * 1000));
            setItem(15, createItem(Material.BEDROCK, "§4Permanentnie"), e -> punish(admin, target, type, reason, -1));
        }

        private void punish(Player admin, OfflinePlayer target, PunishmentManager.Type type, String reason, long time) {
            admin.closeInventory();
            PunishmentManager.execute(admin, target, type, reason, time);
        }
    }

    private static ItemStack createItem(Material mat, String name) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }
}