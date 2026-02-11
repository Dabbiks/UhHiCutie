package dabbiks.uhc.game.gameplay.champions;

import dabbiks.uhc.game.gameplay.champions.Champion;
import dabbiks.uhc.game.gameplay.items.conversion.ItemConverter;
import dabbiks.uhc.player.data.persistent.PersistentData;
import dabbiks.uhc.player.data.persistent.PersistentStats;
import dabbiks.uhc.player.data.session.SessionData;
import dabbiks.uhc.player.data.session.SessionDataManager;
import dabbiks.uhc.player.data.session.SessionTags;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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
        desc.add("§7Klasa specjalizująca się w");
        desc.add("§7szybkim pozyskiwaniu surowców.");
        return desc;
    }

    @Override
    protected List<String> getLevelDescription(int level) {
        List<String> desc = new ArrayList<>();
        switch (level) {
            case 1, 2, 3, 4 -> {
                desc.add("§e» §fKamienny kilof");
                desc.add("§e» §fChleb §8(5)");
                desc.add("§b» §f" + level * 2 + "% na podwojenie surowców z rudy");
            }
            case 5, 6, 7, 8, 9 -> {
                desc.add("§e» §fŻelazny kilof");
                desc.add("§e» §fChleb §8(5)");
                desc.add("§b» §f" + level * 2 + "% na podwojenie surowców z rudy");
                desc.add("§b» §fUżycie kowadeł tańsze o 30%");
            }
            case 10 -> {
                desc.add("§e» §fŻelazny kilof");
                desc.add("§e» §fChleb §8(10)");
                desc.add("§b» §f" + level * 2 + "% na podwojenie surowców z rudy");
                desc.add("§b» §fUżycie kowadeł jest tańsze o 50%");
                desc.add("§b» §fPasek doświadczenia działa jednorazowo jak totem");
            }
        }
        return desc;
    }

    @Override
    protected List<String> getClickDescription(PersistentData persistentData, int level) {
        List<String> desc = new ArrayList<>();
        if (!persistentData.hasUnlockedChampion(getId())) {
            if (persistentData.getStats().getOrDefault(PersistentStats.COINS, 0) >= getCost()) {
                desc.add(symbolU.mouseLeft + " §fZakup górnika za " + getCost() + " monet!");
            } else {
                desc.add(symbolU.mouseLeft + " §cDo zakupu potrzebujesz jeszcze " + (getUpgradeCost(getCost(), level) - persistentData.getStats().getOrDefault(PersistentStats.COINS, 0)) + " monet!");
            }
            return desc;
        }

        if (persistentData.getChampionLevel(getId()) < 10) {
            if (persistentData.getStats().getOrDefault(PersistentStats.COINS, 0) >= getCost()) {
                desc.add(symbolU.mouseLeft + " §fUlepsz do poziomu " + (level + 1) + " za " + getUpgradeCost(getCost(), level) + " monet!");
            } else {
                desc.add(symbolU.mouseLeft + " §fDo ulepszenia potrzebujesz jeszcze " + (getUpgradeCost(getCost(), level) - persistentData.getStats().getOrDefault(PersistentStats.COINS, 0)) + " monet!");
            }
        } else {
            desc.add("§fZdobyta maestria: §e" + persistentData.getChampionMastery(getId()) + " pkt.");
            desc.add(symbolU.mouseLeft + " §fOsiągnąłeś najwyższy poziom górnika!");
        }

        if (!persistentData.getChampion().equals(getId())) {
            desc.add(symbolU.mouseRight + " §cKLIKNIJ, ŻEBY WYBRAĆ");
        } else {
            desc.add(symbolU.mouseRight + " §aWYBRANA KLASA");
        }
        return desc;
    }
}
