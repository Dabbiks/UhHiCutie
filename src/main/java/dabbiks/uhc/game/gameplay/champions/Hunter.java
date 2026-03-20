package dabbiks.uhc.game.gameplay.champions;

import dabbiks.uhc.Main;
import dabbiks.uhc.game.gameplay.items.ItemBuilder;
import dabbiks.uhc.game.gameplay.items.ItemInstance;
import dabbiks.uhc.game.gameplay.items.conversion.ItemConverter;
import dabbiks.uhc.game.gameplay.items.data.enchants.EnchantData;
import dabbiks.uhc.game.gameplay.items.data.enchants.EnchantType;
import dabbiks.uhc.game.gameplay.items.recipes.data.RecipeInstance;
import dabbiks.uhc.game.gameplay.items.recipes.loader.RecipeManager;
import dabbiks.uhc.game.gameplay.items.recipes.loader.RecipeType;
import dabbiks.uhc.player.data.session.SessionData;
import dabbiks.uhc.player.data.session.SessionDataManager;
import dabbiks.uhc.player.data.session.SessionTags;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Hunter extends Champion {

    @Override
    public String getId() {
        return "hunter";
    }

    @Override
    public String getName() {
        return "Łowca";
    }

    @Override
    public int getCost() {
        return 10000;
    }

    @Override
    public Material getIcon() {
        return Material.WOODEN_SWORD;
    }

    @Override
    public int getMaxLevel() {
        return 10;
    }

    @Override
    public void onStart(Player player, int level) {
        SessionData sessionData = SessionDataManager.getData(player.getUniqueId());

        sessionData.addTag(SessionTags.HUNTER);

        String recipeId = "boomerang";
        Optional<RecipeInstance> optional = RecipeManager.getRecipeById(recipeId);

        if (optional.isEmpty()) return;

        RecipeInstance recipe = optional.get();
        ItemInstance instance = recipe.getResult().clone();

        switch (level) {
            case 1, 2 -> {
                player.getInventory().addItem(new ItemBuilder(instance).build());
                player.getInventory().addItem(new ItemStack(Material.DRIED_KELP, 8));
            }
            case 3, 4 -> {
                player.getInventory().addItem(new ItemBuilder(instance).build());
                player.getInventory().addItem(new ItemStack(Material.DRIED_KELP, 8));
                sessionData.addTag(SessionTags.RECIPE_EXPLOSIVE_BOOMERANG);
            }
            case 5, 6, 7, 8, 9 -> {
                List<EnchantData> enchants = new ArrayList<>();
                enchants.add(new EnchantData(EnchantType.BOOMERANG_DURABILITY, 2));
                instance.setEnchants(enchants);

                player.getInventory().addItem(new ItemBuilder(instance).build());
                player.getInventory().addItem(new ItemStack(Material.DRIED_KELP, 16));
                sessionData.addTag(SessionTags.BOOMERANG_LOOTING);
                sessionData.addTag(SessionTags.RECIPE_EXPLOSIVE_BOOMERANG);
            }
            case 10 -> {
                List<EnchantData> enchants = new ArrayList<>();
                enchants.add(new EnchantData(EnchantType.BOOMERANG_DURABILITY, 4));
                instance.setEnchants(enchants);

                player.getInventory().addItem(new ItemBuilder(instance).build());
                player.getInventory().addItem(new ItemStack(Material.DRIED_KELP, 16));
                sessionData.addTag(SessionTags.BOOMERANG_LOOTING);
                sessionData.addTag(SessionTags.BOOMERANG_EXECUTE);
                sessionData.addTag(SessionTags.RECIPE_EXPLOSIVE_BOOMERANG);
            }
        }
    }

    @Override
    protected List<String> getClassDescription() {
        List<String> desc = new ArrayList<>();
        desc.add("§7Klasa oparta na walce zasięgowej");
        desc.add("§7i dezorganizacji przeciwnika.");
        return desc;
    }

    @Override
    protected List<String> getLevelDescription(int level) {
        List<String> desc = new ArrayList<>();
        double stealChance = 2 + ((double) level / 5);

        desc.add("§6Ekwipunek startowy:");
        if (level <= 4) {
            desc.add(" §8■ §fBumerang");
            desc.add(" §8■ §fSuszone wodorosty §7(8 szt.)");
        } else if (level <= 9) {
            desc.add(" §8■ §fBumerang §e(Trwałość II)");
            desc.add(" §8■ §fSuszone wodorosty §7(16 szt.)");
        } else {
            desc.add(" §8■ §fBumerang §e(Trwałość IV)");
            desc.add(" §8■ §fSuszone wodorosty §7(24 szt.)");
        }

        desc.add("");
        desc.add("§6Bonusy pasywne:");
        desc.add(" §e» §fZadając obrażenia bumerangiem masz §c" + stealChance + "% §fna kradzież");
        desc.add("   §flosowego przedmiotu przeciwnika z poza głównego paska");

        if (level >= 5 && level <= 9) {
            desc.add(" §e» §fZabicie moba bumerangiem podwaja jego drop");
        } else if (level == 10) {
            desc.add(" §e» §fZabicie moba bumerangiem podwaja jego drop");
            desc.add(" §e» §fBumerangi §cwykańczają §fgraczy poniżej §c2 serc");
        }

        if (level >= 3) {
            desc.add("");
            desc.add("§6Przepisy:");
            desc.add(" §8■ §fBomberang");
        }

        return desc;
    }
}