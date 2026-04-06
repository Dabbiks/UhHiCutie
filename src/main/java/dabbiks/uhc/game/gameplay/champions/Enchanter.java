package dabbiks.uhc.game.gameplay.champions;

import dabbiks.uhc.game.gameplay.items.conversion.ItemConverter;
import dabbiks.uhc.player.data.session.SessionData;
import dabbiks.uhc.player.data.session.SessionDataManager;
import dabbiks.uhc.player.data.session.SessionTags;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Enchanter extends Champion {

    @Override
    public String getId() {
        return "enchanter";
    }

    @Override
    public String getName() {
        return "Zaklinacz";
    }

    @Override
    public int getCost() {
        return 10000;
    }

    @Override
    public Material getIcon() {
        return Material.LAPIS_LAZULI;
    }

    @Override
    public int getMaxLevel() {
        return 10;
    }

    @Override
    public void onStart(Player player, int level) {
        ItemConverter itemConverter = new ItemConverter();
        SessionData sessionData = SessionDataManager.getData(player.getUniqueId());

        sessionData.addTag(SessionTags.ENCHANTER);

        switch (level) {
            case 1, 2 -> {
                player.getInventory().addItem(new ItemStack(Material.BOOK));
                player.getInventory().addItem(new ItemStack(Material.LAPIS_LAZULI));
                player.getInventory().addItem(new ItemStack(Material.CHORUS_FRUIT, 3));
            }
            case 3, 4 -> {
                player.getInventory().addItem(new ItemStack(Material.BOOK));
                player.getInventory().addItem(new ItemStack(Material.LAPIS_LAZULI));
                player.getInventory().addItem(new ItemStack(Material.CHORUS_FRUIT, 3));
                sessionData.addTag(SessionTags.RECIPE_ENCHANTER);
            }
            case 5, 6, 7, 8, 9 -> {
                player.getInventory().addItem(new ItemStack(Material.BOOK));
                player.getInventory().addItem(new ItemStack(Material.LAPIS_LAZULI));
                player.getInventory().addItem(new ItemStack(Material.CHORUS_FRUIT, 3));
                sessionData.addTag(SessionTags.SMALL_LAPIS_CHANCE);
                sessionData.addTag(SessionTags.RECIPE_ENCHANTER);
            }
            case 10 -> {
                player.getInventory().addItem(new ItemStack(Material.BOOK));
                player.getInventory().addItem(new ItemStack(Material.LAPIS_LAZULI));
                player.getInventory().addItem(new ItemStack(Material.CHORUS_FRUIT, 3));
                sessionData.addTag(SessionTags.BIG_LAPIS_CHANCE);
                sessionData.addTag(SessionTags.ENCHANTED_DUELIST);
                sessionData.addTag(SessionTags.RECIPE_ENCHANTER);
            }
        }
    }

    @Override
    protected List<String> getClassDescription() {
        List<String> desc = new ArrayList<>();
        desc.add("§7Klasa wspierająca redukująca zużycie");
        desc.add("§7zasobów i wzmacniająca siłę zaklęć.");
        return desc;
    }

    @Override
    protected List<String> getLevelDescription(int level) {
        List<String> desc = new ArrayList<>();
        int additionalEnchantChance = 5 + level * 2;

        desc.add("§6Ekwipunek startowy:");
        if (level <= 4) {
            desc.add(" §8■ §fKsiążka");
            desc.add(" §8■ §fOwoc chorusu §7(3 szt.)");
        } else if (level <= 9) {
            desc.add(" §8■ §fKsiążka §7(3 szt.)");
            desc.add(" §8■ §fLapis §7(3 szt.)");
            desc.add(" §8■ §fOwoc chorusu §7(5 szt.)");
        } else {
            desc.add(" §8■ §fKsiążka §7(5 szt.)");
            desc.add(" §8■ §fLapis §7(5 szt.)");
            desc.add(" §8■ §fOwoc chorusu §7(10 szt.)");
        }

        desc.add("");
        desc.add("§6Bonusy pasywne:");
        desc.add(" §e» §fTwoja drużyna ma " + additionalEnchantChance + "% na dodatkowe zaklęcie");
        desc.add("   §fz tieru wyżej niż maksymalny w danym momencie");

        if (level >= 5 && level <= 9) {
            desc.add(" §e» §7Po użyciu stołu każdy lapis ma 15% na pozostanie w stole");
        } else if (level == 10) {
            desc.add(" §e» §fPo użyciu stołu każdy lapis ma 25% na pozostanie w stole");
            desc.add(" §e» §fZaklinanie przedmiotów odbiera pół serca maksymalnego zdrowia, zabójstwa");
            desc.add("   §fregenerują i dodają maksymalnie zdrowie w zależności od ilości doświadczenia ofiary");
        }

        if (level >= 3) {
            desc.add("");
            desc.add("§6Przepisy:");
            desc.add(" §8■ §fWzmacniający kryształ");
        }

        return desc;
    }
}