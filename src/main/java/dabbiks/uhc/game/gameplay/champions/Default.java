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

public class Default extends Champion {

    @Override
    public String getId() {
        return "default";
    }

    @Override
    public String getName() {
        return "Cywil";
    }

    @Override
    public int getCost() {
        return 10000;
    }

    @Override
    public Material getIcon() {
        return Material.BREAD;
    }

    @Override
    public int getMaxLevel() {
        return 10;
    }

    @Override
    public void onStart(Player player, int level) {
        ItemConverter itemConverter = new ItemConverter();
        SessionData sessionData = SessionDataManager.getData(player.getUniqueId());

        sessionData.addTag(SessionTags.DEFAULT);

        switch (level) {
            case 1, 2, 3, 4 -> {
                player.getInventory().addItem(itemConverter.convert(new ItemStack(Material.STONE_SWORD)));
                player.getInventory().addItem(new ItemStack(Material.BOOK, 1));
                player.getInventory().addItem(new ItemStack(Material.CARROT, 5));
            }
            case 5, 6, 7, 8, 9 -> {
                player.getInventory().addItem(itemConverter.convert(new ItemStack(Material.IRON_SWORD)));
                player.getInventory().addItem(new ItemStack(Material.BOOK, 1));
                player.getInventory().addItem(new ItemStack(Material.CARROT, 5));
                sessionData.addTag(SessionTags.SMALL_PARRY_COOLDOWN);
            }
            case 10 -> {
                player.getInventory().addItem(itemConverter.convert(new ItemStack(Material.IRON_SWORD)));
                player.getInventory().addItem(new ItemStack(Material.BOOK, 1));
                player.getInventory().addItem(new ItemStack(Material.CARROT, 10));
                sessionData.addTag(SessionTags.BIG_PARRY_COOLDOWN);
                sessionData.addTag(SessionTags.PARRY_REGENERATION);
            }
        }
    }

    @Override
    protected List<String> getClassDescription() {
        List<String> desc = new ArrayList<>();
        desc.add("§7Podstawowa klasa wzmacniająca");
        desc.add("§7walkę wręcz i parowanie.");
        return desc;
    }

    @Override
    protected List<String> getLevelDescription(int level) {
        List<String> desc = new ArrayList<>();
        int bonusSwordDamage = level * 2;

        desc.add("§6Ekwipunek startowy:");
        if (level <= 4) {
            desc.add(" §8■ §fKamienny miecz");
            desc.add(" §8■ §fMarchew §7(5 szt.)");
        } else if (level <= 9) {
            desc.add(" §8■ §fŻelazny miecz");
            desc.add(" §8■ §fMarchew §7(5 szt.)");
        } else {
            desc.add(" §8■ §fŻelazny miecz");
            desc.add(" §8■ §fMarchew §7(10 szt.)");
        }

        desc.add("");
        desc.add("§6Bonusy pasywne:");
        desc.add(" §e» §fDodatkowe obrażenia z miecza: §a" + bonusSwordDamage + "%");

        if (level >= 5 && level <= 9) {
            desc.add(" §e» §fParowanie ma zmniejszony cooldown o 15%");
        } else if (level == 10) {
            desc.add(" §e» §fParowanie ma zmniejszony cooldown o 30%");
            desc.add(" §e» §fParowanie ciosów krytycznych leczy o §c2 serca");
        }

        return desc;
    }
}