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

public class Miner extends Champion {

    @Override
    public String getId() {
        return "miner";
    }

    @Override
    public String getName() {
        return "Górnik";
    }

    @Override
    public int getCost() {
        return 10000;
    }

    @Override
    public Material getIcon() {
        return Material.IRON_PICKAXE;
    }

    @Override
    public int getMaxLevel() {
        return 10;
    }

    @Override
    public void onStart(Player player, int level) {
        ItemConverter itemConverter = new ItemConverter();
        SessionData sessionData = SessionDataManager.getData(player.getUniqueId());

        sessionData.addTag(SessionTags.MINER);

        switch (level) {
            case 1, 2, 3, 4 -> {
                player.getInventory().addItem(itemConverter.convert(new ItemStack(Material.STONE_PICKAXE)));
                player.getInventory().addItem(new ItemStack(Material.BREAD, 5));
            }
            case 5, 6, 7, 8, 9 -> {
                player.getInventory().addItem(itemConverter.convert(new ItemStack(Material.IRON_PICKAXE)));
                player.getInventory().addItem(itemConverter.convert(new ItemStack(Material.LEATHER_HELMET)));
                player.getInventory().addItem(new ItemStack(Material.BREAD, 5));
                sessionData.addTag(SessionTags.SMALL_ANVIL_DISCOUNT);
            }
            case 10 -> {
                player.getInventory().addItem(itemConverter.convert(new ItemStack(Material.IRON_PICKAXE)));
                player.getInventory().addItem(itemConverter.convert(new ItemStack(Material.IRON_HELMET)));
                player.getInventory().addItem(new ItemStack(Material.BREAD, 10));
                sessionData.addTag(SessionTags.BIG_ANVIL_DISCOUNT);
                sessionData.addTag(SessionTags.IMMORTAL_EXPERIENCE);
            }
        }
    }

    @Override
    protected List<String> getClassDescription() {
        List<String> desc = new ArrayList<>();
        desc.add("§7Klasa skupiona na efektywnym");
        desc.add("§7pozyskiwaniu surowców z mapy.");
        return desc;
    }

    @Override
    protected List<String> getLevelDescription(int level) {
        List<String> desc = new ArrayList<>();
        double doubleOreChance = level * 0.8;

        desc.add("§6Ekwipunek startowy:");
        if (level <= 4) {
            desc.add(" §8■ §fKamienny kilof");
            desc.add(" §8■ §fChleb §7(5 szt.)");
        } else if (level <= 9) {
            desc.add(" §8■ §fŻelazny kilof");
            desc.add(" §8■ §fSkórzana czapka");
            desc.add(" §8■ §fChleb §7(5 szt.)");
        } else {
            desc.add(" §8■ §fŻelazny kilof");
            desc.add(" §8■ §fŻelazny hełm");
            desc.add(" §8■ §fChleb §7(10 szt.)");
        }

        desc.add("");
        desc.add("§6Bonusy pasywne:");
        desc.add(" §e» §fMasz §c" + doubleOreChance + "% §fszansy na podwojenie dropu");

        if (level >= 5 && level <= 9) {
            desc.add(" §e» §fUżycie kowadła jest §ctańsze §fo §c30%");
        } else if (level == 10) {
            desc.add(" §e» §fUżycie kowadła jest §ctańsze §fo §c50%");
            desc.add(" §e» §fPasek XP działa jednorazowo jak totem");
        }

        if (level >= 3) {
            desc.add("");
            desc.add("§6Przepisy:");
            desc.add(" §8■ §fPiec hutniczy");
            desc.add(" §8■ §fTania sztabka netherytu");
        }

        return desc;
    }
}