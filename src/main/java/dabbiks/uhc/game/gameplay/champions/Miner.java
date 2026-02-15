package dabbiks.uhc.game.gameplay.champions;

import dabbiks.uhc.game.gameplay.items.conversion.ItemConverter;
import dabbiks.uhc.player.data.persistent.PersistentData;
import dabbiks.uhc.player.data.persistent.PersistentStats;
import dabbiks.uhc.player.data.session.SessionData;
import dabbiks.uhc.player.data.session.SessionDataManager;
import dabbiks.uhc.player.data.session.SessionTags;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static dabbiks.uhc.Main.symbolU;

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
                player.getInventory().addItem(new ItemStack(Material.BREAD, 5));
                sessionData.addTag(SessionTags.SMALL_ANVIL_DISCOUNT);
            }
            case 10 -> {
                player.getInventory().addItem(itemConverter.convert(new ItemStack(Material.IRON_PICKAXE)));
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
        int doubleOreChance = level * 2;

        desc.add("§6Ekwipunek startowy:");
        if (level <= 4) {
            desc.add(" §8■ §fKamienny kilof");
            desc.add(" §8■ §fChleb §7(5 szt.)");
        } else if (level <= 9) {
            desc.add(" §8■ §fŻelazny kilof");
            desc.add(" §8■ §fChleb §7(5 szt.)");
        } else {
            desc.add(" §8■ §fŻelazny kilof");
            desc.add(" §8■ §fChleb §7(10 szt.)");
        }

        desc.add("");
        desc.add("§6Bonusy pasywne:");
        desc.add(" §e» §fSzanse na podwójną rudę: §a" + doubleOreChance + "%");

        if (level >= 5 && level <= 9) {
            desc.add(" §e» §fZniżka na kowadła: §a30%");
        } else if (level == 10) {
            desc.add(" §e» §fZniżka na kowadła: §a50%");
            desc.add(" §e» §fOchrona: §bPasek EXP działa jak totem");
        }

        return desc;
    }

    @Override
    protected List<String> getClickDescription(PersistentData persistentData, int level) {
        List<String> desc = new ArrayList<>();
        int playerCoins = persistentData.getStats().getOrDefault(PersistentStats.COINS, 0);

        if (!persistentData.hasUnlockedChampion(getId())) {
            if (playerCoins >= getCost()) {
                desc.add(symbolU.MOUSE_LEFT + " §aKliknij, aby zakupić klasę!");
                desc.add(" §8• §7Koszt: §6" + getCost() + " monet");
            } else {
                desc.add(symbolU.MOUSE_LEFT + " §cBrakuje Ci §n" + (getCost() - playerCoins) + "§c monet!");
            }
            return desc;
        }

        if (persistentData.getChampionLevel(getId()) < 10) {
            int upgradeCost = getUpgradeCost(getCost(), level);
            if (playerCoins >= upgradeCost) {
                desc.add(symbolU.MOUSE_LEFT + " §eUlepsz na poziom " + (level + 1));
                desc.add(" §8• §7Koszt: §6" + upgradeCost + " monet");
            } else {
                desc.add(symbolU.MOUSE_LEFT + " §cPotrzebujesz jeszcze §n" + (upgradeCost - playerCoins) + "§c monet!");
            }
        } else {
            desc.add("§6§lMAESTRIA");
            desc.add(" §8• §7Punkty: §e" + persistentData.getChampionMastery(getId()));
            desc.add("");
            desc.add("§8" + symbolU.MOUSE_LEFT + " §7Osiągnięto maksymalny poziom.");
        }

        desc.add("");
        if (!persistentData.getChampion().equals(getId())) {
            desc.add(symbolU.MOUSE_RIGHT + " §eKliknij, aby wybrać tę klasę");
        } else {
            desc.add(symbolU.MOUSE_RIGHT + " §aKlasa jest aktualnie wybrana");
        }
        return desc;
    }
}