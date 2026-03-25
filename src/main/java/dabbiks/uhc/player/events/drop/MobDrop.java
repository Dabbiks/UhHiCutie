package dabbiks.uhc.player.events.drop;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class MobDrop implements Listener {

    private final Random random = new Random();

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        EntityType type = entity.getType();

        switch (type) {
            case SKELETON:
                event.getDrops().add(new ItemStack(Material.ARROW, 2 + random.nextInt(4)));
                break;

            case VILLAGER:
                event.getDrops().add(new ItemStack(Material.EMERALD, 3 + random.nextInt(6)));

                Villager villager = (Villager) entity;
                Villager.Profession profession = villager.getProfession();

                if (profession != Villager.Profession.NONE && profession != Villager.Profession.NITWIT) {
                    ItemStack professionItem = getProfessionItem(profession);
                    if (professionItem != null) {
                        event.getDrops().add(professionItem);
                    }
                }
                break;

            case CHICKEN:
                event.getDrops().add(new ItemStack(Material.FEATHER, 1));
                break;

            case CREEPER:
                event.getDrops().add(new ItemStack(Material.GUNPOWDER, 1));
                break;

            case HORSE:
                event.getDrops().add(new ItemStack(Material.LEATHER, 2));
                break;

            default:
                break;
        }
    }

    private ItemStack getProfessionItem(Villager.Profession profession) {
        int choice = random.nextInt(3);

        if (profession == Villager.Profession.ARMORER) {
            return choice == 0 ? new ItemStack(Material.IRON_CHESTPLATE) : (choice == 1 ? new ItemStack(Material.IRON_HELMET) : new ItemStack(Material.IRON_BOOTS));
        } else if (profession == Villager.Profession.BUTCHER) {
            return choice == 0 ? new ItemStack(Material.BEEF, 3) : (choice == 1 ? new ItemStack(Material.PORKCHOP, 3) : new ItemStack(Material.MUTTON, 3));
        } else if (profession == Villager.Profession.CARTOGRAPHER) {
            return choice == 0 ? new ItemStack(Material.PAPER, 5) : (choice == 1 ? new ItemStack(Material.MAP) : new ItemStack(Material.COMPASS));
        } else if (profession == Villager.Profession.CLERIC) {
            return choice == 0 ? new ItemStack(Material.REDSTONE, 4) : (choice == 1 ? new ItemStack(Material.GLOWSTONE_DUST, 3) : new ItemStack(Material.ENDER_PEARL));
        } else if (profession == Villager.Profession.FARMER) {
            return choice == 0 ? new ItemStack(Material.WHEAT, 6) : (choice == 1 ? new ItemStack(Material.CARROT, 4) : new ItemStack(Material.POTATO, 4));
        } else if (profession == Villager.Profession.FISHERMAN) {
            return choice == 0 ? new ItemStack(Material.COD, 3) : (choice == 1 ? new ItemStack(Material.SALMON, 3) : new ItemStack(Material.FISHING_ROD));
        } else if (profession == Villager.Profession.FLETCHER) {
            return choice == 0 ? new ItemStack(Material.ARROW, 5) : (choice == 1 ? new ItemStack(Material.BOW) : new ItemStack(Material.FLINT, 3));
        } else if (profession == Villager.Profession.LEATHERWORKER) {
            return choice == 0 ? new ItemStack(Material.LEATHER, 3) : (choice == 1 ? new ItemStack(Material.LEATHER_CHESTPLATE) : new ItemStack(Material.SADDLE));
        } else if (profession == Villager.Profession.LIBRARIAN) {
            return choice == 0 ? new ItemStack(Material.BOOK, 2) : (choice == 1 ? new ItemStack(Material.BOOKSHELF) : new ItemStack(Material.PAPER, 4));
        } else if (profession == Villager.Profession.MASON) {
            return choice == 0 ? new ItemStack(Material.BRICK, 4) : (choice == 1 ? new ItemStack(Material.STONE, 4) : new ItemStack(Material.CLAY_BALL, 4));
        } else if (profession == Villager.Profession.SHEPHERD) {
            return choice == 0 ? new ItemStack(Material.WHITE_WOOL, 3) : (choice == 1 ? new ItemStack(Material.SHEARS) : new ItemStack(Material.BLACK_WOOL, 3));
        } else if (profession == Villager.Profession.TOOLSMITH) {
            return choice == 0 ? new ItemStack(Material.IRON_PICKAXE) : (choice == 1 ? new ItemStack(Material.IRON_AXE) : new ItemStack(Material.IRON_SHOVEL));
        } else if (profession == Villager.Profession.WEAPONSMITH) {
            return choice == 0 ? new ItemStack(Material.IRON_SWORD) : (choice == 1 ? new ItemStack(Material.GOLDEN_SWORD) : new ItemStack(Material.DIAMOND_SWORD));
        }

        return null;
    }
}