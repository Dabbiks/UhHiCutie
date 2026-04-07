package dabbiks.uhc.commands;

import com.google.gson.Gson;
import dabbiks.uhc.Main;
import dabbiks.uhc.game.gameplay.items.ItemDeconstructor;
import dabbiks.uhc.game.gameplay.items.ItemInstance;
import dabbiks.uhc.game.gameplay.setpieces.SetPieceGraveLoot;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SetPieceCommand implements CommandExecutor, TabCompleter {
    private static final Gson gson = new Gson();
    private void saveSetpiece(List<ItemStack> items, String name, String rarity) {
        List<ItemInstance> instances = new ArrayList<>();
        for (ItemStack item : items) {
            instances.add(new ItemDeconstructor(item).deconstruct());
        }
        File mainFolder = Main.INSTANCE.getDataFolder();
        File folder = new File(mainFolder, "setpieces");
        if (!folder.exists()) folder.mkdirs();
        File[] rarityFolders = folder.listFiles();
        if (rarityFolders == null) return;
        for (File rarityFolder : rarityFolders) {
            if (!rarityFolder.getName().equals(rarity)) continue;
            File file = new File(rarityFolder,name+".json");
            try (FileWriter writer = new FileWriter(file)) {
                gson.toJson(instances, writer);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("uhc.admin")) {
            sender.sendMessage("§cBrak uprawnień.");
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Musisz być graczem");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage("Musisz wpisać nazwę setpiecu");
            return true;
        }

        String setPieceName = args[0];

        if (args.length < 2) {
            sender.sendMessage("Musisz wpisać rzadkość setpiecu");
            return true;
        }

        String setPieceRarity = args[1];

        if (!SetPieceGraveLoot.rarityWeights.containsKey(setPieceRarity)) {
            sender.sendMessage("Nie ma takiej rzadkości");
            return true;
        }

        List<ItemStack> items = new ArrayList<>();
        for (int slot = 0; slot < 9; slot++) {
            ItemStack item = player.getInventory().getItem(slot);
            if (item != null && !item.isEmpty()) {
                items.add(item);
            }
        }
        if (items.isEmpty()) {
            sender.sendMessage("Masz pusty hotbar");
            return true;
        }
        saveSetpiece(items,setPieceName,setPieceRarity);
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 2) {
            return SetPieceGraveLoot.rarityWeights.keySet().stream().toList();
        }
        return List.of();
    }
}