package dabbiks.uhc.game.gameplay.items.stations.smithingtable;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Random;

import static dabbiks.uhc.Main.soundU;

public class SmithingTableInstance {

    public final Location location;
    public SmithingTableSlot slot;

    public SmithingTableInstance(Location location) {
        this.location = location;
        placeSmithingTable();
    }

    public void placeSmithingTable() {
        slot = new SmithingTableSlot(location.clone().add(0.5, 1.05, 0.5), 30, 90);
    }

    public boolean fill() {
        if (slot.filled) {
            soundU.playSoundAtLocation(location, Sound.ENTITY_VILLAGER_NO, 0.6f, 1);
            return false;
        }

        fillSlot();
        return true;
    }

    public void reset() {
        soundU.playSoundAtLocation(location, Sound.BLOCK_SMITHING_TABLE_USE, 0.8f, 1);
        slot.filled = false;
        if (slot.itemDisplay == null) return;
        slot.itemDisplay.remove();
        slot.itemDisplay = null;
    }

    public void destroy() {
        if (slot.filled) slot.location.getWorld().dropItemNaturally(slot.location, new ItemStack(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE));
        if (slot.itemDisplay == null) return;
        slot.itemDisplay.remove();
        slot.itemDisplay = null;
    }

    private void fillSlot() {
        Random random = new Random();
        int rotation1 = slot.rotation1 + random.nextInt(-30, 30);
        int rotation2 = slot.rotation2;
        float pitch = 1;

        slot.filled = true;
        soundU.playSoundAtLocation(location, Sound.ITEM_AXE_SCRAPE, 0.8f, pitch);
        slot.itemDisplay = (ItemDisplay) location.getWorld().spawnEntity(slot.location, EntityType.ITEM_DISPLAY);
        slot.itemDisplay.setItemStack(new ItemStack(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE));
        slot.itemDisplay.setRotation(rotation1, rotation2);
        slot.itemDisplay.setBillboard(Display.Billboard.FIXED);
        slot.itemDisplay.setTransformation(new Transformation(
                new Vector3f(0f, 0f, 0f),
                new Quaternionf(),
                new Vector3f(1f, 1f, 1f),
                new Quaternionf()
        ));
    }

    public class SmithingTableSlot {
        public Location location;
        public int rotation1, rotation2;
        public boolean filled;
        public ItemDisplay itemDisplay;

        public SmithingTableSlot(Location location, int rotation1, int rotation2) {
            this.location = location;
            this.rotation1 = rotation1;
            this.rotation2 = rotation2;
        }
    }
}
