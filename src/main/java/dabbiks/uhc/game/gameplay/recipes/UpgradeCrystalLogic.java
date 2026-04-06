package dabbiks.uhc.game.gameplay.recipes;

import dabbiks.uhc.game.gameplay.items.ItemBuilder;
import dabbiks.uhc.game.gameplay.items.ItemDeconstructor;
import dabbiks.uhc.game.gameplay.items.ItemInstance;
import dabbiks.uhc.game.gameplay.items.data.enchants.EnchantData;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadableItemNBT;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Function;

public class UpgradeCrystalLogic implements Listener {

    @EventHandler
    public void onCrystalUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();

        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

        if (event.getClickedBlock() != null && event.getClickedBlock().getType().isInteractable() && !player.isSneaking()) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.END_CRYSTAL) return;

        Boolean hasPerk = NBT.get(item, (Function<ReadableItemNBT, Boolean>) nbt -> nbt.hasTag("UPGRADE_CRYSTAL"));

        if (hasPerk == null || !hasPerk) return;

        event.setCancelled(true);

        boolean upgradedAny = false;
        ItemStack[] armorContents = player.getInventory().getArmorContents();

        for (int i = 0; i < armorContents.length; i++) {
            ItemStack armorPiece = armorContents[i];
            if (armorPiece == null || armorPiece.getType() == Material.AIR) continue;

            ItemInstance instance = new ItemDeconstructor(armorPiece).deconstruct();
            if (instance == null || instance.getEnchants() == null) continue;

            boolean pieceUpgraded = false;
            List<EnchantData> enchants = instance.getEnchants();

            for (EnchantData enchant : enchants) {
                if (enchant.getLevel() < enchant.getType().getMaxLevel()) {
                    enchant.setLevel(enchant.getLevel() + 1);
                    pieceUpgraded = true;
                    upgradedAny = true;
                }
            }

            if (pieceUpgraded) {
                armorContents[i] = new ItemBuilder(instance).build();
            }
        }

        if (upgradedAny) {
            player.getInventory().setArmorContents(armorContents);
            item.setAmount(item.getAmount() - 1);
            player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0f, 1.0f);
            player.sendMessage("§aPomyślnie wzmocniono twoją zbroję!");
        } else {
            player.sendMessage("§cNie masz na sobie żadnej zbroi, którą można wzmocnić!");
        }
    }
}