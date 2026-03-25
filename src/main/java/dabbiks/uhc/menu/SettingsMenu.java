package dabbiks.uhc.menu;

import dabbiks.uhc.game.gameplay.items.ItemTags;
import dabbiks.uhc.player.data.persistent.PersistentData;
import dabbiks.uhc.player.data.persistent.PersistentDataManager;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteItemNBT;
import fr.mrmicky.fastinv.FastInv;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.function.Consumer;

import static dabbiks.uhc.Main.symbolU;

public class SettingsMenu extends FastInv {

    private final Player player;
    private final PersistentData persistentData;

    public SettingsMenu(Player player, PersistentData persistentData) {
        super(27, "Ustawienia");
        this.player = player;
        this.persistentData = persistentData;

        render();
    }

    private void render() {
        String oreStatus = switch (persistentData.getOreMessageMode()) {
            case 0 -> "§aWSZYSTKIE";
            case 1 -> "§eTYLKO ISTOTNE";
            default -> "§cWYŁĄCZONE";
        };
        setItem(11, createIcon(Material.GOLDEN_PICKAXE, "§6Wiadomości dropu", oreStatus), e -> handleClick("ORE"));

        String gammaStatus = persistentData.getGamma() ? "§aWŁĄCZONE" : "§cWYŁĄCZONE";
        setItem(13, createIcon(Material.GOLDEN_CARROT, "§6Widzenie w ciemności", gammaStatus), e -> handleClick("GAMMA"));

        String censorStatus = persistentData.getCensor() ? "§aWŁĄCZONA" : "§cWYŁĄCZONA";
        setItem(15, createIcon(Material.PALE_OAK_SIGN, "§6Cenzura czatu", censorStatus), e -> handleClick("CENSOR"));
    }

    private ItemStack createIcon(Material material, String name, String status) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(name);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
            meta.setLore(Arrays.asList(
                    "§7Obecne ustawienie: " + status,
                    symbolU.MOUSE_LEFT + " Przełącz"
            ));
            item.setItemMeta(meta);
        }

        NBT.modify(item, (Consumer<ReadWriteItemNBT>) nbt -> nbt.setInteger(ItemTags.UHC_ITEM.name(), 1));

        return item;
    }

    private void handleClick(String type) {
        switch (type) {
            case "ORE" -> persistentData.setOreMessageMode((persistentData.getOreMessageMode() + 1) % 3);
            case "GAMMA" -> persistentData.setGamma(!persistentData.getGamma());
            case "CENSOR" -> persistentData.setCensor(!persistentData.getCensor());
        }

        PersistentDataManager.saveData(player.getUniqueId());
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
        render();
    }
}