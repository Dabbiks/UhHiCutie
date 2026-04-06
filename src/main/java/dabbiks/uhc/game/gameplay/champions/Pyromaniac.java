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

public class Pyromaniac extends Champion {

    @Override
    public String getId() {
        return "pyromaniac";
    }

    @Override
    public String getName() {
        return "Piroman";
    }

    @Override
    public int getCost() {
        return 10000;
    }

    @Override
    public Material getIcon() {
        return Material.FLINT_AND_STEEL;
    }

    @Override
    public int getMaxLevel() {
        return 10;
    }

    @Override
    public void onStart(Player player, int level) {
        ItemConverter itemConverter = new ItemConverter();
        SessionData sessionData = SessionDataManager.getData(player.getUniqueId());

        sessionData.addTag(SessionTags.PYROMANIAC);

        switch (level) {
            case 1, 2 -> {
                player.getInventory().addItem(new ItemStack(Material.FLINT_AND_STEEL));
                player.getInventory().addItem(itemConverter.convert(new ItemStack(Material.LEATHER_LEGGINGS)));
                player.getInventory().addItem(new ItemStack(Material.COOKED_RABBIT, 4));
            }
            case 3, 4 -> {
                player.getInventory().addItem(new ItemStack(Material.FLINT_AND_STEEL));
                player.getInventory().addItem(itemConverter.convert(new ItemStack(Material.LEATHER_LEGGINGS)));
                player.getInventory().addItem(new ItemStack(Material.COOKED_RABBIT, 4));
                sessionData.addTag(SessionTags.RECIPE_PYROMANIAC);
            }
            case 5, 6, 7, 8, 9 -> {
                player.getInventory().addItem(new ItemStack(Material.FLINT_AND_STEEL));
                player.getInventory().addItem(itemConverter.convert(new ItemStack(Material.LEATHER_LEGGINGS)));
                player.getInventory().addItem(new ItemStack(Material.COOKED_RABBIT, 6));
                sessionData.addTag(SessionTags.RECIPE_PYROMANIAC);
                sessionData.addTag(SessionTags.FIRE_IMMUNITY_TICKS);
                sessionData.addTag(SessionTags.SELF_BURN);
            }
            case 10 -> {
                player.getInventory().addItem(new ItemStack(Material.FLINT_AND_STEEL));
                player.getInventory().addItem(itemConverter.convert(new ItemStack(Material.LEATHER_LEGGINGS)));
                player.getInventory().addItem(new ItemStack(Material.COOKED_RABBIT, 8));
                sessionData.addTag(SessionTags.RECIPE_PYROMANIAC);
                sessionData.addTag(SessionTags.FIRE_IMMUNITY_TICKS);
                sessionData.addTag(SessionTags.FIRE_CHARGE_DISPENSER);
                sessionData.addTag(SessionTags.STRONG_SELF_BURN);
            }
        }
    }

    @Override
    protected List<String> getClassDescription() {
        List<String> desc = new ArrayList<>();
        desc.add("§7Klasa oparta na dynamicznej");
        desc.add("§7walce, czerpiąca siłę z ognia.");
        return desc;
    }

    @Override
    protected List<String> getLevelDescription(int level) {
        List<String> desc = new ArrayList<>();

        desc.add("§6Ekwipunek startowy:");
        if (level <= 4) {
            desc.add(" §8■ §fKrzesiwo");
            desc.add(" §8■ §fSkórzane spodnie");
            desc.add(" §8■ §fKrólik §7(4 szt.)");
        } else if (level <= 9) {
            desc.add(" §8■ §fKrzesiwo");
            desc.add(" §8■ §fMiedziane nogawice");
            desc.add(" §8■ §fKrólik §7(6 szt.)");
        } else {
            desc.add(" §8■ §fKrzesiwo");
            desc.add(" §8■ §fKolcze nogawice");
            desc.add(" §8■ §fKrólik §7(8 szt.)");
        }

        desc.add("");
        desc.add("§6Bonusy pasywne:");
        desc.add(" §e» §fGdy jesteś podpalony zadajesz dodatkowo " + level + "% obrażeń + 3% obrażeń za");
        desc.add("   §fkażde 5 pozostałych ticków płonięcia");

        if (level >= 5 && level <= 9) {
            desc.add(" §e» §fTrafienia krytyczne podpalają Cię. Kolejne trafienia zwiększają czas płonięcia");
            desc.add(" §e» §fObrażenia od podpalenia nie przerywają sprintowania");
        } else if (level == 10) {
            desc.add(" §e» §fTrafienia krytyczne podpalają Cię. Kolejne trafienia zwiększają czas płonięcia");
            desc.add(" §e» §fObrażenia od podpalenia nie przerywają sprintowania");
            desc.add(" §e» §fPotrafi rzucać kulami ognia");
        }

        if (level >= 3) {
            desc.add("");
            desc.add("§6Przepisy:");
            desc.add(" §8■ §fObsydianowa siekiera");
            desc.add(" §8■ §fOdwar z płomiennego proszku");
        }

        return desc;
    }
}