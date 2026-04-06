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

public class Fisherman extends Champion {

    @Override
    public String getId() {
        return "fisherman";
    }

    @Override
    public String getName() {
        return "Wędkarz";
    }

    @Override
    public int getCost() {
        return 10000;
    }

    @Override
    public Material getIcon() {
        return Material.FISHING_ROD;
    }

    @Override
    public int getMaxLevel() {
        return 10;
    }

    @Override
    public void onStart(Player player, int level) {
        ItemConverter itemConverter = new ItemConverter();
        SessionData sessionData = SessionDataManager.getData(player.getUniqueId());

        sessionData.addTag(SessionTags.FISHERMAN);

        switch (level) {
            case 1, 2 -> {
                player.getInventory().addItem(itemConverter.convert(new ItemStack(Material.FISHING_ROD)));
                player.getInventory().addItem(itemConverter.convert(new ItemStack(Material.LEATHER_HELMET)));
                player.getInventory().addItem(new ItemStack(Material.COD, 3));
            }
            case 3, 4 -> {
                player.getInventory().addItem(itemConverter.convert(new ItemStack(Material.FISHING_ROD)));
                player.getInventory().addItem(itemConverter.convert(new ItemStack(Material.LEATHER_HELMET)));
                player.getInventory().addItem(new ItemStack(Material.COD, 3));
                sessionData.addTag(SessionTags.RECIPE_FISH_OIL);
            }
            case 5, 6, 7, 8, 9 -> {
                player.getInventory().addItem(itemConverter.convert(new ItemStack(Material.FISHING_ROD)));
                player.getInventory().addItem(new ItemStack(Material.WATER_BUCKET, 1));
                player.getInventory().addItem(itemConverter.convert(new ItemStack(Material.LEATHER_HELMET)));
                player.getInventory().addItem(itemConverter.convert(new ItemStack(Material.LEATHER_CHESTPLATE)));
                player.getInventory().addItem(new ItemStack(Material.COD, 3));
                sessionData.addTag(SessionTags.RECIPE_FISH_OIL);
                sessionData.addTag(SessionTags.SMALL_FISHING_ROD_KNOCKBACK);
            }
            case 10 -> {
                player.getInventory().addItem(itemConverter.convert(new ItemStack(Material.FISHING_ROD)));
                player.getInventory().addItem(new ItemStack(Material.WATER_BUCKET, 1));
                player.getInventory().addItem(itemConverter.convert(new ItemStack(Material.LEATHER_HELMET)));
                player.getInventory().addItem(itemConverter.convert(new ItemStack(Material.LEATHER_CHESTPLATE)));
                player.getInventory().addItem(itemConverter.convert(new ItemStack(Material.LEATHER_LEGGINGS)));
                player.getInventory().addItem(itemConverter.convert(new ItemStack(Material.LEATHER_BOOTS)));
                player.getInventory().addItem(new ItemStack(Material.COD, 3));
                sessionData.addTag(SessionTags.RECIPE_FISH_OIL);
                sessionData.addTag(SessionTags.BIG_FISHING_ROD_KNOCKBACK);
                sessionData.addTag(SessionTags.MORE_FISHING_DROPS);
                sessionData.addTag(SessionTags.RECIPE_FISH_OIL);
            }
        }
    }

    @Override
    protected List<String> getClassDescription() {
        List<String> desc = new ArrayList<>();
        desc.add("§7Klasa oparta na krótkich wymianach");
        desc.add("§7i zdobywaniu dodatkowych surowców.");
        return desc;
    }

    @Override
    protected List<String> getLevelDescription(int level) {
        List<String> desc = new ArrayList<>();
        int regenCooldown = 24 - level;

        desc.add("§6Ekwipunek startowy:");
        if (level <= 4) {
            desc.add(" §8■ §fWędka");
            desc.add(" §8■ §fSkórzana czapka");
            desc.add(" §8■ §fDorsz §7(3 szt.)");
        } else if (level <= 9) {
            desc.add(" §8■ §fWędka");
            desc.add(" §8■ §fWiadro wody");
            desc.add(" §8■ §fSkórzana czapka");
            desc.add(" §8■ §fSkórzana tunika");
            desc.add(" §8■ §fDorsz §7(3 szt.)");
        } else {
            desc.add(" §8■ §fWędka");
            desc.add(" §8■ §fWiadro wody");
            desc.add(" §8■ §fSkórzana czapka");
            desc.add(" §8■ §fSkórzana tunika");
            desc.add(" §8■ §fSkórzane spodnie");
            desc.add(" §8■ §fSkórzane buty");
            desc.add(" §8■ §fDorsz §7(3 szt.)");
        }

        desc.add("");
        desc.add("§6Bonusy pasywne:");
        desc.add(" §e» §fOtrzymujesz Regenerację II przy wejściu do wody §8(Odnowienie " + regenCooldown + "s)");

        if (level >= 5 && level <= 9) {
            desc.add(" §e» §7Przyciąganie wędką jest silniejsze o 20%");
        } else if (level == 10) {
            desc.add(" §e» §7Przyciąganie wędką jest silniejsze o 40%");
            desc.add(" §e» §fSzansa na wyłowienie użytecznych przedmiotów");
        }

        if (level >= 3) {
            desc.add("");
            desc.add("§6Przepisy:");
            desc.add(" §8■ §fTran");
        }

        return desc;
    }
}