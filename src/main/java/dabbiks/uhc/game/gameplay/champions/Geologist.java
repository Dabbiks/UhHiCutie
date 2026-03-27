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

public class Geologist extends Champion {

    @Override
    public String getId() {
        return "geologist";
    }

    @Override
    public String getName() {
        return "Geolog";
    }

    @Override
    public int getCost() {
        return 10000;
    }

    @Override
    public Material getIcon() {
        return Material.AMETHYST_SHARD;
    }

    @Override
    public int getMaxLevel() {
        return 10;
    }

    @Override
    public void onStart(Player player, int level) {
        ItemConverter itemConverter = new ItemConverter();
        SessionData sessionData = SessionDataManager.getData(player.getUniqueId());

        sessionData.addTag(SessionTags.GEOLOGIST);

        switch (level) {
            case 1, 2, 3, 4 -> {
                player.getInventory().addItem(new ItemStack(Material.GRINDSTONE, 1));
                player.getInventory().addItem(new ItemStack(Material.QUARTZ, 2));
                player.getInventory().addItem(new ItemStack(Material.COOKIE, 5));
                sessionData.addTag(SessionTags.OBSIDIAN_DROP);
            }
            case 5, 6, 7, 8, 9 -> {
                player.getInventory().addItem(new ItemStack(Material.GRINDSTONE, 1));
                player.getInventory().addItem(new ItemStack(Material.QUARTZ, 4));
                player.getInventory().addItem(new ItemStack(Material.COOKIE, 5));
                sessionData.addTag(SessionTags.OBSIDIAN_DROP);
                sessionData.addTag(SessionTags.MINE_BUDDING_AMETHYST);
            }
            case 10 -> {
                player.getInventory().addItem(new ItemStack(Material.GRINDSTONE, 1));
                player.getInventory().addItem(new ItemStack(Material.BRUSH, 1));
                player.getInventory().addItem(new ItemStack(Material.QUARTZ, 4));
                player.getInventory().addItem(new ItemStack(Material.COOKIE, 10));
                sessionData.addTag(SessionTags.OBSIDIAN_DROP);
                sessionData.addTag(SessionTags.MINE_BUDDING_AMETHYST);
                sessionData.addTag(SessionTags.DIAMOND_HASTE);
            }
        }
    }

    @Override
    protected List<String> getClassDescription() {
        List<String> desc = new ArrayList<>();
        desc.add("§7Klasa zorientowana wokół pozyskiwania");
        desc.add("§7dodatkowych surowców mniejszym kosztem.");
        return desc;
    }

    @Override
    protected List<String> getLevelDescription(int level) {
        List<String> desc = new ArrayList<>();
        double obsidianChance = 0.3 + level * 0.05;

        desc.add("§6Ekwipunek startowy:");
        if (level <= 4) {
            desc.add(" §8■ §fKamień szlifierski");
            desc.add(" §8■ §fKwarc §7(2 szt.)");
            desc.add(" §8■ §fCiastko §7(5 szt.)");
        } else if (level <= 9) {
            desc.add(" §8■ §fKamień szlifierski");
            desc.add(" §8■ §fKwarc §7(4 szt.)");
            desc.add(" §8■ §fCiastko §7(5 szt.)");
        } else {
            desc.add(" §8■ §fKamień szlifierski");
            desc.add(" §8■ §fPędzel");
            desc.add(" §8■ §fKwarc §7(4 szt.)");
            desc.add(" §8■ §fCiastko §7(10 szt.)");
        }

        desc.add("");
        desc.add("§6Bonusy pasywne:");
        desc.add(" §e» §fPrzy wykopaniu kamienia ma §c" + obsidianChance + "% §fszansy na obsydian");

        if (level >= 5 && level <= 9) {
            desc.add(" §e» §fPotrafi wydobyć ametyst z niewyrośniętych zarodków");
        } else if (level == 10) {
            desc.add(" §e» §fPotrafi wydobyć ametyst z niewyrośniętych zarodków");
            desc.add(" §e» §fOtrzymuje pośpiech II na 12 sekund po wydobyciu diamentu");
        }

        if (level >= 3) {
            desc.add("");
            desc.add("§6Przepisy:");
            desc.add(" §8■ §fNapierśnik dekady");
            desc.add(" §8■ §fDynamit");
        }

        return desc;
    }
}