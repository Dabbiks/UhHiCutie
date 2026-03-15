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

public class Archer extends Champion {

    @Override
    public String getId() {
        return "archer";
    }

    @Override
    public String getName() {
        return "Łucznik";
    }

    @Override
    public int getCost() {
        return 10000;
    }

    @Override
    public Material getIcon() {
        return Material.BOW;
    }

    @Override
    public int getMaxLevel() {
        return 10;
    }

    @Override
    public void onStart(Player player, int level) {
        ItemConverter itemConverter = new ItemConverter();
        SessionData sessionData = SessionDataManager.getData(player.getUniqueId());

        sessionData.addTag(SessionTags.ARCHER);

        switch (level) {
            case 1, 2, 3, 4 -> {
                player.getInventory().addItem(itemConverter.convert(new ItemStack(Material.BOW)));
                player.getInventory().addItem(new ItemStack(Material.ARROW, 5));
                player.getInventory().addItem(new ItemStack(Material.COOKED_CHICKEN, 5));
            }
            case 5, 6, 7, 8, 9 -> {
                player.getInventory().addItem(itemConverter.convert(new ItemStack(Material.BOW)));
                player.getInventory().addItem(new ItemStack(Material.ARROW, 15));
                player.getInventory().addItem(new ItemStack(Material.COOKED_CHICKEN, 5));
                sessionData.addTag(SessionTags.PROJECTILE_HIT_REGENERATION);
            }
            case 10 -> {
                player.getInventory().addItem(itemConverter.convert(new ItemStack(Material.BOW)));
                player.getInventory().addItem(new ItemStack(Material.ARROW, 35));
                player.getInventory().addItem(new ItemStack(Material.COOKED_CHICKEN, 10));
                sessionData.addTag(SessionTags.BIG_PROJECTILE_HIT_REGENERATION);
                sessionData.addTag(SessionTags.PROJECTILE_HIT_ARMOR_CORROSION);
            }
        }
    }

    @Override
    protected List<String> getClassDescription() {
        List<String> desc = new ArrayList<>();
        desc.add("§7Klasa zasięgowa ułatwiająca");
        desc.add("§7walkę za pomocą łuku i kuszy.");
        return desc;
    }

    @Override
    protected List<String> getLevelDescription(int level) {
        List<String> desc = new ArrayList<>();
        double arrowDropChance = 20 + level * 4;

        desc.add("§6Ekwipunek startowy:");
        if (level <= 4) {
            desc.add(" §8■ §fŁuk");
            desc.add(" §8■ §fStrzały §7(5 szt.)");
            desc.add(" §8■ §fKurczak §7(5 szt.)");
        } else if (level <= 9) {
            desc.add(" §8■ §fŁuk");
            desc.add(" §8■ §fStrzały §7(15 szt.)");
            desc.add(" §8■ §fKurczak §7(5 szt.)");
        } else {
            desc.add(" §8■ §fŁuk");
            desc.add(" §8■ §fStrzały §7(35 szt.)");
            desc.add(" §8■ §fKurczak §7(10 szt.)");
        }

        desc.add("");
        desc.add("§6Bonusy pasywne:");
        desc.add(" §e» §fMasz §c" + arrowDropChance + "% §fna strzały ze żwiru");

        if (level >= 5 && level <= 9) {
            desc.add(" §e» §fTrafienie gracza z łuku przywraca §cpół serca");
        } else if (level == 10) {
            desc.add(" §e» §fTrafienie gracza z łuku przywraca §cjedno serce");
            desc.add(" §e» §fTwoje strzały §ccniszczą pancerz §cfo §c3 + 3% §cfaktualnych użyć");
        }

        return desc;
    }
}