package dabbiks.uhc.game.gameplay.items.stations.table;

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

import static dabbiks.uhc.Main.soundU;

public class TableInstance {

    public final Location location;

    public Slot[] slots = new Slot[4];

    public TableInstance(Location location) {
        this.location = location;
        placeEnchantingTable();
    }

    public void placeEnchantingTable() {
        slots[0] = new Slot(location.clone().add(0.1, 0.77, 0.1), 0, 90, 0.8f);
        slots[1] = new Slot(location.clone().add(0.9, 0.77, 0.1), 90, 90, 1.0f);
        slots[2] = new Slot(location.clone().add(0.1, 0.77, 0.9), 270, 90, 1.2f);
        slots[3] = new Slot(location.clone().add(0.9, 0.77, 0.9), 180, 90, 1.4f);

    }

    public boolean fill() {
        if (slots[3].filled) {
            soundU.playSoundAtLocation(location, Sound.ENTITY_VILLAGER_NO, 0.6f, 1);
            return false;
        }

        for (Slot slot : slots) {
            if (!slot.filled) {
                fillSlot(slot);
                return true;
            }
        }
        return false;
    }

    public void reset() {
        soundU.playSoundAtLocation(location, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 0.6f, 1);
        for (Slot slot : slots) {
            slot.filled = false;
            if (slot.itemDisplay == null) continue;
            slot.itemDisplay.remove();
            slot.itemDisplay = null;
        }
    }

    public void destroy() {
        for (Slot slot : slots) {
            if (slot.filled) slot.location.getWorld().dropItemNaturally(slot.location, new ItemStack(Material.LAPIS_LAZULI));
            if (slot.itemDisplay == null) continue;
            slot.itemDisplay.remove();
            slot.itemDisplay = null;
        }
    }

    private void fillSlot(Slot slot) {
        int rotation1 = slot.rotation1;
        int rotation2 = slot.rotation2;
        float pitch = slot.pitch;

        slot.filled = true;
        soundU.playSoundAtLocation(location, Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 0.6f, pitch);
        slot.itemDisplay = (ItemDisplay) location.getWorld().spawnEntity(slot.location, EntityType.ITEM_DISPLAY);
        slot.itemDisplay.setItemStack(new ItemStack(Material.LAPIS_LAZULI));
        slot.itemDisplay.setRotation(rotation1, rotation2);
        slot.itemDisplay.setBillboard(Display.Billboard.FIXED);
        slot.itemDisplay.setTransformation(new Transformation(
                new Vector3f(0f, 0f, 0f),
                new Quaternionf(),
                new Vector3f(0.3f, 0.3f, 0.3f),
                new Quaternionf()
        ));
    }

    public class Slot {
        public Location location;
        public int rotation1, rotation2;
        public float pitch;
        public boolean filled;
        public ItemDisplay itemDisplay;

        public Slot(Location location, int rotation1, int rotation2, float pitch) {
            this.location = location;
            this.rotation1 = rotation1;
            this.rotation2 = rotation2;
            this.pitch = pitch;
        }
    }
}
