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

public class Pikeman extends Champion {

    @Override
    public String getId() {
        return "pikeman";
    }

    @Override
    public String getName() {
        return "Pikinier";
    }

    @Override
    public int getCost() {
        return 10000;
    }

    @Override
    public Material getIcon() {
        return Material.IRON_SPEAR;
    }

    @Override
    public int getMaxLevel() {
        return 10;
    }

    @Override
    public void onStart(Player player, int level) {
        ItemConverter itemConverter = new ItemConverter();
        SessionData sessionData = SessionDataManager.getData(player.getUniqueId());

        sessionData.addTag(SessionTags.PIKEMAN);

        switch (level) {
            case 1, 2, 3, 4 -> {
                player.getInventory().addItem(itemConverter.convert(new ItemStack(Material.COPPER_SPEAR)));
                player.getInventory().addItem(new ItemStack(Material.BAKED_POTATO, 5));
            }
            case 5, 6, 7, 8, 9 -> {
                player.getInventory().addItem(itemConverter.convert(new ItemStack(Material.IRON_SPEAR)));
                player.getInventory().addItem(itemConverter.convert(new ItemStack(Material.LEATHER_LEGGINGS)));
                player.getInventory().addItem(new ItemStack(Material.BAKED_POTATO, 5));
                sessionData.addTag(SessionTags.ON_HIT_PROOF);
            }
            case 10 -> {
                player.getInventory().addItem(itemConverter.convert(new ItemStack(Material.DIAMOND_SPEAR)));
                player.getInventory().addItem(itemConverter.convert(new ItemStack(Material.CHAINMAIL_LEGGINGS)));
                player.getInventory().addItem(new ItemStack(Material.BAKED_POTATO, 10));
                sessionData.addTag(SessionTags.ON_HIT_PROOF);
                sessionData.addTag(SessionTags.STATUS_EFFECT_PROOF);
            }
        }
    }

    @Override
    protected List<String> getClassDescription() {
        List<String> desc = new ArrayList<>();
        desc.add("§7Klasa zorientowana wokół kontroli");
        desc.add("§7i odporności na efekty specjalne.");
        return desc;
    }

    @Override
    protected List<String> getLevelDescription(int level) {
        List<String> desc = new ArrayList<>();
        double onHitMovementBuff = 10 + level * 2;

        desc.add("§6Ekwipunek startowy:");
        if (level <= 4) {
            desc.add(" §8■ §fMiedziana włócznia");
            desc.add(" §8■ §fZiemniak §7(5 szt.)");
        } else if (level <= 9) {
            desc.add(" §8■ §fŻelazna włócznia");
            desc.add(" §8■ §fSkórzane spodnie");
            desc.add(" §8■ §fZiemniak §7(5 szt.)");
        } else {
            desc.add(" §8■ §fDiamentowa włócznia");
            desc.add(" §8■ §fKolcze nogawice");
            desc.add(" §8■ §fZiemniak §7(10 szt.)");
        }

        desc.add("");
        desc.add("§6Bonusy pasywne:");
        desc.add(" §e» §fTrafienie włócznią przyspiesza o §c" + onHitMovementBuff + "% §fna dwie sekundy");

        if (level >= 5 && level <= 9) {
            desc.add(" §e» §fWrogie efekty przy trafieniu nie mają na Ciebie wpływu");
        } else if (level == 10) {
            desc.add(" §e» §fWrogie efekty przy trafieniu nie mają na Ciebie wpływu");
            desc.add(" §e» §fNegatywne efekty mikstur nie mają na Ciebie wpływu");
        }

        return desc;
    }
}