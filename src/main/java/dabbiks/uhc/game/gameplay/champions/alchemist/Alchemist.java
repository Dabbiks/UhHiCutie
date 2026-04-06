package dabbiks.uhc.game.gameplay.champions.alchemist;

import dabbiks.uhc.game.gameplay.champions.Champion;
import dabbiks.uhc.game.gameplay.items.conversion.ItemConverter;
import dabbiks.uhc.player.data.session.SessionData;
import dabbiks.uhc.player.data.session.SessionDataManager;
import dabbiks.uhc.player.data.session.SessionTags;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Alchemist extends Champion {

    @Override
    public String getId() {
        return "alchemist";
    }

    @Override
    public String getName() {
        return "Alchemik";
    }

    @Override
    public int getCost() {
        return 10000;
    }

    @Override
    public Material getIcon() {
        return Material.BLAZE_POWDER;
    }

    @Override
    public int getMaxLevel() {
        return 10;
    }

    @Override
    public void onStart(Player player, int level) {
        ItemConverter itemConverter = new ItemConverter();
        SessionData sessionData = SessionDataManager.getData(player.getUniqueId());

        sessionData.addTag(SessionTags.ALCHEMIST);

        switch (level) {
            case 1, 2, 3, 4 -> {
                player.getInventory().addItem(new ItemStack(Material.BREWING_STAND, 1));
                player.getInventory().addItem(new ItemStack(Material.NETHER_WART, 1));
                player.getInventory().addItem(new ItemStack(Material.GOLDEN_CARROT, 1));
                sessionData.addTag(SessionTags.RECIPE_ALCHEMIST);
                sessionData.addTag(SessionTags.CAN_USE_BREWING_STAND);
            }
            case 5, 6, 7, 8, 9 -> {
                player.getInventory().addItem(new ItemStack(Material.BREWING_STAND, 1));
                player.getInventory().addItem(new ItemStack(Material.NETHER_WART, 2));
                player.getInventory().addItem(new ItemStack(Material.GOLDEN_CARROT, 2));
                sessionData.addTag(SessionTags.RECIPE_ALCHEMIST);
                sessionData.addTag(SessionTags.CAN_USE_BREWING_STAND);
                sessionData.addTag(SessionTags.POTION_SHARING);
            }
            case 10 -> {
                player.getInventory().addItem(new ItemStack(Material.BREWING_STAND, 1));
                player.getInventory().addItem(new ItemStack(Material.NETHER_WART, 3));
                player.getInventory().addItem(new ItemStack(Material.GOLDEN_CARROT, 3));
                sessionData.addTag(SessionTags.RECIPE_ALCHEMIST);
                sessionData.addTag(SessionTags.CAN_USE_BREWING_STAND);
                sessionData.addTag(SessionTags.POTION_SHARING);
                sessionData.addTag(SessionTags.ALCHEMIST_MAGICAL_HIT);
            }
        }
    }

    @Override
    protected List<String> getClassDescription() {
        List<String> desc = new ArrayList<>();
        desc.add("§7Klasa wspierająca, potrafi warzyć");
        desc.add("§7mikstury i dzielić się ich efektami.");
        return desc;
    }

    @Override
    protected List<String> getLevelDescription(int level) {
        List<String> desc = new ArrayList<>();
        int effectSharingRange = 3 + (level / 2);

        desc.add("§6Ekwipunek startowy:");
        if (level <= 4) {
            desc.add(" §8■ §fStatyw alchemiczny");
            desc.add(" §8■ §fNetherowa brodawka");
            desc.add(" §8■ §fZłota marchewka");
        } else if (level <= 9) {
            desc.add(" §8■ §fStatyw alchemiczny");
            desc.add(" §8■ §fNetherowa brodawka §7(2 szt.)");
            desc.add(" §8■ §fZłota marchewka §7(2 szt.)");
        } else {
            desc.add(" §8■ §fStatyw alchemiczny");
            desc.add(" §8■ §fNetherowa brodawka §7(3 szt.)");
            desc.add(" §8■ §fZłota marchewka §7(3 szt.)");
        }

        desc.add("");
        desc.add("§6Bonusy pasywne:");
        desc.add(" §e» §fPotrafi korzystać ze stołu alchemicznego od początku gry");

        if (level >= 5 && level <= 9) {
            desc.add(" §e» §fPrzy wypiciu mikstury duplikuje jej efekty");
            desc.add("   §fna sojuszników w zasięgu §c" + effectSharingRange + "§f kratek");
        } else if (level == 10) {
            desc.add(" §e» §fPrzy wypiciu mikstury duplikuje jej efekty");
            desc.add("   §fna sojuszników w zasięgu §c" + effectSharingRange + "§f kratek");
            desc.add(" §e» §f25% zadawanych obrażeń jest traktowane jak obrażenia magiczne");
        }

        desc.add("");
        desc.add("§6Przepisy:");
        desc.add(" §8■ §fStatyw alchemiczny");
        desc.add(" §8■ §fButelka z recyklingu");

        return desc;
    }
}