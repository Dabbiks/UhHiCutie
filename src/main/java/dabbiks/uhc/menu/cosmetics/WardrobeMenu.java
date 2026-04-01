package dabbiks.uhc.menu.cosmetics;

import dabbiks.uhc.cosmetics.Wardrobe;
import dabbiks.uhc.game.gameplay.items.ItemTags;
import dabbiks.uhc.player.data.persistent.PersistentData;
import de.tr7zw.nbtapi.NBTItem;
import fr.mrmicky.fastinv.FastInv;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import static dabbiks.uhc.Main.*;

public class WardrobeMenu extends FastInv {

    private final Player player;
    private final PersistentData persistentData;
    private final Wardrobe[] wardrobes;
    private int page = 0;

    public WardrobeMenu(Player player, PersistentData persistentData) {
        super(54, "\uF808" + symbolU.WARDROBE_MENU);
        this.player = player;
        this.persistentData = persistentData;
        this.wardrobes = Wardrobe.values();

        render();
    }

    private void render() {
        for (int i = 0; i < getInventory().getSize(); i++) {
            removeItem(i);
        }

        int maxPages = (int) Math.ceil((double) wardrobes.length / 5);

        for (int i = 0; i < 5; i++) {
            int index = page * 5 + i;
            if (index >= wardrobes.length) break;

            Wardrobe w = wardrobes[index];

            ItemStack helmet = getHelmet(w);
            if (helmet.getType() != Material.AIR) {
                setItem(12 + i, createIcon(w, helmet, persistentData.hasWardrobeHelmet(w), persistentData.getWardrobeHelmet() == w, w.getHelmetName()), e -> handleClick(w, persistentData.hasWardrobeHelmet(w), 0));
            }

            ItemStack chestplate = getArmor(Material.LEATHER_CHESTPLATE, w.getChestplateColor());
            if (chestplate.getType() != Material.AIR) {
                setItem(21 + i, createIcon(w, chestplate, persistentData.hasWardrobeChestplate(w), persistentData.getWardrobeChestplate() == w, w.getChestplateName()), e -> handleClick(w, persistentData.hasWardrobeChestplate(w), 1));
            }

            ItemStack leggings = getArmor(Material.LEATHER_LEGGINGS, w.getLeggingsColor());
            if (leggings.getType() != Material.AIR) {
                setItem(30 + i, createIcon(w, leggings, persistentData.hasWardrobeLeggings(w), persistentData.getWardrobeLeggings() == w, w.getLeggingsName()), e -> handleClick(w, persistentData.hasWardrobeLeggings(w), 2));
            }

            ItemStack boots = getArmor(Material.LEATHER_BOOTS, w.getBootsColor());
            if (boots.getType() != Material.AIR) {
                setItem(39 + i, createIcon(w, boots, persistentData.hasWardrobeBoots(w), persistentData.getWardrobeBoots() == w, w.getBootsName()), e -> handleClick(w, persistentData.hasWardrobeBoots(w), 3));
            }
        }

        if (page > 0) {
            setItem(38, createNav("§ePoprzednia strona", true), e -> {
                page--;
                render();
            });
        }

        if (page < maxPages - 1) {
            setItem(44, createNav("§eNastępna strona", false), e -> {
                page++;
                render();
            });
        }

        setItem(10, createPreview(persistentData.getWardrobeHelmet(), "Czapka", 0), e -> handleClear(0));
        setItem(19, createPreview(persistentData.getWardrobeChestplate(), "Napierśnik", 1), e -> handleClear(1));
        setItem(28, createPreview(persistentData.getWardrobeLeggings(), "Spodnie", 2), e -> handleClear(2));
        setItem(37, createPreview(persistentData.getWardrobeBoots(), "Buty", 3), e -> handleClear(3));
    }

    private void applyToPlayer(int type, Wardrobe w) {
        if (w == null) {
            if (type == 0) player.getInventory().setHelmet(null);
            else if (type == 1) player.getInventory().setChestplate(null);
            else if (type == 2) player.getInventory().setLeggings(null);
            else if (type == 3) player.getInventory().setBoots(null);
            return;
        }

        if (type == 0) player.getInventory().setHelmet(getHelmet(w));
        else if (type == 1) player.getInventory().setChestplate(getArmor(Material.LEATHER_CHESTPLATE, w.getChestplateColor()));
        else if (type == 2) player.getInventory().setLeggings(getArmor(Material.LEATHER_LEGGINGS, w.getLeggingsColor()));
        else if (type == 3) player.getInventory().setBoots(getArmor(Material.LEATHER_BOOTS, w.getBootsColor()));
    }

    private void handleClick(Wardrobe w, boolean unlocked, int type) {
        if (!unlocked) {
            soundU.playSoundToPlayer(player, Sound.ENTITY_VILLAGER_NO, 1, 1);
            return;
        }

        boolean changed = false;

        if (type == 0 && persistentData.getWardrobeHelmet() != w) {
            persistentData.setWardrobeHelmet(w);
            changed = true;
        } else if (type == 1 && persistentData.getWardrobeChestplate() != w) {
            persistentData.setWardrobeChestplate(w);
            changed = true;
        } else if (type == 2 && persistentData.getWardrobeLeggings() != w) {
            persistentData.setWardrobeLeggings(w);
            changed = true;
        } else if (type == 3 && persistentData.getWardrobeBoots() != w) {
            persistentData.setWardrobeBoots(w);
            changed = true;
        }

        if (changed) {
            applyToPlayer(type, w);
            player.sendMessage("§aZaktualizowano garderobę.");
            soundU.playSoundToPlayer(player, Sound.UI_BUTTON_CLICK, 1, 1);
            render();
        }
    }

    private void handleClear(int type) {
        boolean changed = false;

        if (type == 0 && persistentData.getWardrobeHelmet() != null) {
            persistentData.setWardrobeHelmet(null);
            changed = true;
        } else if (type == 1 && persistentData.getWardrobeChestplate() != null) {
            persistentData.setWardrobeChestplate(null);
            changed = true;
        } else if (type == 2 && persistentData.getWardrobeLeggings() != null) {
            persistentData.setWardrobeLeggings(null);
            changed = true;
        } else if (type == 3 && persistentData.getWardrobeBoots() != null) {
            persistentData.setWardrobeBoots(null);
            changed = true;
        }

        if (changed) {
            applyToPlayer(type, null);
            player.sendMessage("§aZdjęto element garderoby.");
            soundU.playSoundToPlayer(player, Sound.UI_BUTTON_CLICK, 1, 1);
            render();
        }
    }

    private ItemStack getHelmet(Wardrobe w) {
        if (w.getHeadTexture() != null && !w.getHeadTexture().isEmpty()) {
            ItemStack item = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
            PlayerTextures textures = profile.getTextures();

            try {
                String decoded = new String(Base64.getDecoder().decode(w.getHeadTexture()));
                String url = decoded.split("\"url\":\"")[1].split("\"")[0];
                textures.setSkin(new URL(url));
                profile.setTextures(textures);
                meta.setOwnerProfile(profile);
            } catch (Exception ignored) {}

            item.setItemMeta(meta);
            return item;
        } else {
            return getArmor(Material.LEATHER_HELMET, w.getHelmetColor());
        }
    }

    private ItemStack getArmor(Material mat, Color color) {
        if (color == null) return new ItemStack(Material.AIR);
        ItemStack item = new ItemStack(mat);
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        if (meta != null) {
            meta.setColor(color);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createIcon(Wardrobe wardrobe, ItemStack baseItem, boolean unlocked, boolean selected, String displayName) {
        if (!unlocked) {
            ItemStack barrier = new ItemStack(Material.PAPER);
            ItemMeta meta = barrier.getItemMeta();
            if (meta != null) {
                meta.setDisplayName("§cNieodkryte");
                meta.setCustomModelData(18);
                barrier.setItemMeta(meta);
            }
            return barrier;
        }

        if (baseItem == null || baseItem.getType() == Material.AIR) {
            return new ItemStack(Material.AIR);
        }

        ItemStack item = baseItem.clone();
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(wardrobe.getTier().getIcon() + "§f" + displayName);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP, ItemFlag.HIDE_DYE);

            List<String> lore = new ArrayList<>();
            if (selected) {
                lore.add(symbolU.MOUSE_LEFT + "§a Wybrany element");
            } else {
                lore.add(symbolU.MOUSE_LEFT + "§e Wybierz element");
            }

            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setInteger(ItemTags.UHC_ITEM.name(), 1);
        return nbtItem.getItem();
    }

    private ItemStack createPreview(Wardrobe wardrobe, String type, int slotType) {
        if (wardrobe == null) {
            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName("§c" + type);
                meta.setCustomModelData(18);
                item.setItemMeta(meta);
            }
            return item;
        }

        ItemStack baseItem;
        String name;
        if (slotType == 0) {
            baseItem = getHelmet(wardrobe);
            name = wardrobe.getHelmetName();
        } else if (slotType == 1) {
            baseItem = getArmor(Material.LEATHER_CHESTPLATE, wardrobe.getChestplateColor());
            name = wardrobe.getChestplateName();
        } else if (slotType == 2) {
            baseItem = getArmor(Material.LEATHER_LEGGINGS, wardrobe.getLeggingsColor());
            name = wardrobe.getLeggingsName();
        } else {
            baseItem = getArmor(Material.LEATHER_BOOTS, wardrobe.getBootsColor());
            name = wardrobe.getBootsName();
        }

        if (baseItem.getType() == Material.AIR) {
            return new ItemStack(Material.AIR);
        }

        ItemStack item = baseItem.clone();
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(wardrobe.getTier().getIcon() + "§f" + name);
            List<String> lore = new ArrayList<>();
            lore.add("§7Obecnie założone");
            lore.add(symbolU.MOUSE_LEFT + " §cKliknij, aby zdjąć");
            meta.setLore(lore);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP, ItemFlag.HIDE_DYE);
            item.setItemMeta(meta);
        }

        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setInteger(ItemTags.UHC_ITEM.name(), 1);
        return nbtItem.getItem();
    }

    private ItemStack createNav(String name, boolean left) {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            if (left) meta.setCustomModelData(16);
            else meta.setCustomModelData(17);
            item.setItemMeta(meta);
        }
        return item;
    }
}