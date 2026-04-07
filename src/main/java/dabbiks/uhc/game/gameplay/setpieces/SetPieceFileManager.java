package dabbiks.uhc.game.gameplay.setpieces;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dabbiks.uhc.game.gameplay.items.ItemBuilder;
import dabbiks.uhc.game.gameplay.items.ItemDeconstructor;
import dabbiks.uhc.game.gameplay.items.ItemInstance;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class SetPieceFileManager {
    public static List<Material> allowedTypes = List.of(Material.TRIDENT, Material.BOW, Material.CROSSBOW, Material.AMETHYST_SHARD, Material.RAW_GOLD, Material.RAW_IRON, Material.WATER_BUCKET, Material.LAVA_BUCKET, Material.BUCKET);
    public Map<UUID, List<ItemStack>> deadPlayersItems = new HashMap<>();
    File file;

    public SetPieceFileManager(File dataFolder) {
        this.file = new File(dataFolder, "died_players_items.json");
    }

    public void load() {
        if (!file.exists()) {
            save();
            deadPlayersItems = new HashMap<>();
            return;
        }
        deadPlayersItems = new HashMap<>();
        Random random = new Random();
        try (FileReader reader = new FileReader(file)) {
            Map<String, List<ItemInstance>> deadPlayersItemsInstances = new Gson().fromJson(reader, new TypeToken<Map<String, List<ItemInstance>>>() {
            }.getType());
            if (deadPlayersItemsInstances == null) return;
            for (String s : deadPlayersItemsInstances.keySet()) {
                List<ItemStack> items = new ArrayList<>();
                for (ItemInstance instance : deadPlayersItemsInstances.get(s)) {
                    items.add(new ItemBuilder(instance).build());
                }
                List<ItemStack> finalItems = new ArrayList<>();
                for (int i = 0; i < Math.min(3,items.size()); i++) {
                    finalItems.add(items.remove(random.nextInt(items.size())));
                }
                deadPlayersItems.put(UUID.fromString(s), finalItems);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void save() {
        Map<String, List<ItemInstance>> dataToSave = new HashMap<>();
        for (UUID uuid : deadPlayersItems.keySet()) {
            List<ItemInstance> instances = new ArrayList<>();
            for (ItemStack item : deadPlayersItems.get(uuid)) {
                ItemInstance itemInstance = new ItemDeconstructor(item).deconstruct();
                instances.add(itemInstance);
            }
            dataToSave.put(uuid.toString(), instances);
        }
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            new Gson().toJson(dataToSave, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void add(UUID uuid, ItemStack[] items) {
        List<ItemStack> itemList = new ArrayList<>();
        for (ItemStack item : items) {
            if (item == null) continue;
            Material type = item.getType();
            if (allowedTypes.contains(type) || Tag.ITEMS_ENCHANTABLE_WEAPON.isTagged(type) || Tag.ITEMS_TOOLS.isTagged(type) || Tag.ITEMS_ENCHANTABLE_ARMOR.isTagged(type) || Tag.ITEMS_TRIM_MATERIALS.isTagged(type)) {
                itemList.add(item);
            }
        }
        deadPlayersItems.put(uuid, itemList);
    }

    public static boolean shouldBeStuckInGround(Material type) {
        return Tag.ITEMS_SWORDS.isTagged(type)
                || Tag.ITEMS_TOOLS.isTagged(type)
                || type == Material.TRIDENT
                || type == Material.MACE;
    }
}