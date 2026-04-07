package dabbiks.uhc.game.gameplay.setpieces;

import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import dabbiks.uhc.Main;
import dabbiks.uhc.game.gameplay.items.ItemBuilder;
import dabbiks.uhc.game.gameplay.items.ItemInstance;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.*;

public class SetPieceGraveLoot {
    Random random = new Random();
    Map<String,Map<String,List<ItemStack>>> setPieces = new HashMap<>();
    public static Map<String, Integer> rarityWeights = Map.of("common",10, "uncommon", 5, "rare",1);
    List<String> rarities = new ArrayList<>();
    private static final Gson gson = new Gson();
    public SetPieceGraveLoot() {
        for (String rarity : rarityWeights.keySet()) {
            for (int i = 0; i < rarityWeights.get(rarity); i++) rarities.add(rarity);
        }
        for (String rarity : rarityWeights.keySet()) {
            setPieces.put(rarity,new HashMap<>());
        }
        File folder = new File(Main.INSTANCE.getDataFolder(), "setpieces");
        if (!folder.exists()) folder.mkdirs();
        for (String rarity : rarityWeights.keySet()) {
            File rarityFolder = new File(folder,rarity);
            if (!rarityFolder.exists()) rarityFolder.mkdirs();
            File[] setPieceFiles = rarityFolder.listFiles();
            if (setPieceFiles == null) continue;
            for (File file : setPieceFiles) {
                if (!file.getName().endsWith(".json")) continue;
                String setPieceName = file.getName().substring(0,file.getName().length()-5);
                try (Reader reader = new FileReader(file)) {
                    List<ItemInstance> itemInstances = gson.fromJson(reader,new TypeToken<List<ItemInstance>>(){}.getType());
                    List<ItemStack> items = new ArrayList<>();
                    for (ItemInstance instance : itemInstances) {
                        items.add(new ItemBuilder(instance).build());
                    }
                    Map<String,List<ItemStack>> raritySetPieceMap = setPieces.get(rarity);
                    raritySetPieceMap.put(setPieceName,items);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public List<ItemStack> getRandomItems() {
        String rarity = rarities.get(random.nextInt(rarities.size()));
        Map<String, List<ItemStack>> setPiecesInRarity = setPieces.get(rarity);
        if (setPiecesInRarity.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> names = new ArrayList<>(setPiecesInRarity.keySet());
        String name = names.get(random.nextInt(names.size()));
        List<ItemStack> items = new ArrayList<>(setPiecesInRarity.get(name));
        setPiecesInRarity.remove(name);
        setPieces.put(rarity,setPiecesInRarity);
        return items;
    }
}