package dabbiks.uhc.cosmetics;

import dabbiks.uhc.cosmetics.Mount;
import dabbiks.uhc.player.data.persistent.PersistentData;
import dabbiks.uhc.player.data.persistent.PersistentDataManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static dabbiks.uhc.Main.plugin;

public class MountManager implements Listener {

    private static MountManager instance;
    private final NamespacedKey mountKey = new NamespacedKey(plugin, "is_mount");
    private final Map<UUID, Entity> activeMounts = new HashMap<>();
    private final Map<UUID, List<Entity>> activeSeats = new HashMap<>();
    private final Map<UUID, Mount> activeMountTypes = new HashMap<>();
    private final Map<UUID, PersistentData> activeData = new HashMap<>();
    private final Map<Location, BlockState> originalBlocks = new HashMap<>();
    private final Material[] rainbow = {Material.RED_WOOL, Material.ORANGE_WOOL, Material.YELLOW_WOOL, Material.LIME_WOOL, Material.LIGHT_BLUE_WOOL, Material.BLUE_WOOL, Material.PURPLE_WOOL};
    private int rainbowIndex = 0;

    public MountManager() {
        instance = this;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        startTasks();
    }

    public static MountManager getInstance() {
        if (instance == null) {
            instance = new MountManager();
        }
        return instance;
    }

    public boolean isPlayerMount(Player player, Mount mount) {
        return activeMountTypes.get(player.getUniqueId()) == mount;
    }

    public void spawnMount(Player player, Mount mountType, PersistentData data) {
        removeMount(player);
        Location loc = player.getLocation();
        Entity entity = loc.getWorld().spawnEntity(loc, mountType.getEntityType());
        entity.getPersistentDataContainer().set(mountKey, PersistentDataType.BYTE, (byte) 1);

        if (entity instanceof Tameable tameable) {
            tameable.setTamed(true);
            tameable.setOwner(player);
        }
        if (entity instanceof Steerable steerable) {
            steerable.setSaddle(true);
        }
        if (entity instanceof AbstractHorse horse) {
            horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
            horse.setTamed(true);
            horse.setOwner(player);
        }
        if (entity instanceof Ageable ageable) {
            ageable.setAdult();
        }

        switch (mountType) {
            case PIG -> {
                if (entity instanceof Pig pig) {
                    pig.setSaddle(true);
                    if (pig.getAttribute(Attribute.MOVEMENT_SPEED) != null) {
                        pig.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(pig.getAttribute(Attribute.MOVEMENT_SPEED).getBaseValue() * 1.20);
                    }
                    player.getInventory().setItem(6, new ItemStack(Material.CARROT_ON_A_STICK));
                }
            }
            case BROWN_HORSE -> {
                if (entity instanceof Horse horse) {
                    horse.setColor(Horse.Color.BROWN);
                    horse.setStyle(Horse.Style.WHITE_DOTS);
                }
            }
            case STRIDER -> {
                if (entity instanceof Strider strider) {
                    strider.setSaddle(true);
                    strider.setShivering(false);
                    if (strider.getAttribute(Attribute.MOVEMENT_SPEED) != null) {
                        strider.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(strider.getAttribute(Attribute.MOVEMENT_SPEED).getBaseValue() * 2.0);
                    }
                    player.getInventory().setItem(6, new ItemStack(Material.WARPED_FUNGUS_ON_A_STICK));
                }
            }
            case CAMEL -> {
                if (entity instanceof Camel camel) {
                    camel.getInventory().setSaddle(new ItemStack(Material.SADDLE));
                }
            }
            case BLACK_HORSE, WHITE_HORSE -> {
                if (entity instanceof Horse horse) {
                    horse.setColor(mountType == Mount.BLACK_HORSE ? Horse.Color.BLACK : Horse.Color.WHITE);
                    horse.setStyle(Horse.Style.NONE);
                    if (mountType == Mount.WHITE_HORSE) {
                        horse.getInventory().setArmor(new ItemStack(Material.GOLDEN_HORSE_ARMOR));
                    }
                    if (horse.getAttribute(Attribute.MOVEMENT_SPEED) != null) {
                        horse.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.3375 * 1.5);
                    }
                    try {
                        if (horse.getAttribute(Attribute.JUMP_STRENGTH) != null) {
                            horse.getAttribute(Attribute.JUMP_STRENGTH).setBaseValue(horse.getAttribute(Attribute.JUMP_STRENGTH).getBaseValue() * 1.5);
                        }
                    } catch (NoSuchFieldError e) {
                        if (horse.getAttribute(Attribute.valueOf("HORSE_JUMP_STRENGTH")) != null) {
                            horse.getAttribute(Attribute.valueOf("HORSE_JUMP_STRENGTH")).setBaseValue(horse.getAttribute(Attribute.valueOf("HORSE_JUMP_STRENGTH")).getBaseValue() * 1.5);
                        }
                    }
                }
            }
            case NAUTILUS -> {
                if (entity instanceof Dolphin dolphin) {
                    dolphin.setInvulnerable(true);
                }
            }
            case SNIFFER -> {
                player.getInventory().setItem(6, new ItemStack(Material.CARROT_ON_A_STICK));
            }
            case ENDER_DRAGON -> {
                if (entity instanceof EnderDragon dragon) {
                    dragon.setAI(false);
                    dragon.setPhase(EnderDragon.Phase.HOVER);
                }
                player.getInventory().setItem(6, new ItemStack(Material.CARROT_ON_A_STICK));
            }
        }

        entity.setCustomName(mountType.getName());
        entity.setCustomNameVisible(true);
        activeMounts.put(player.getUniqueId(), entity);
        activeMountTypes.put(player.getUniqueId(), mountType);
        activeData.put(player.getUniqueId(), data);

        if (mountType == Mount.ENDER_DRAGON) {
            ArmorStand seat = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
            seat.setVisible(false);
            seat.setGravity(false);
            seat.getPersistentDataContainer().set(mountKey, PersistentDataType.BYTE, (byte) 1);
            seat.addPassenger(player);
            activeSeats.put(player.getUniqueId(), Collections.singletonList(seat));
        } else {
            entity.addPassenger(player);
        }
    }

    public void removeMount(Player player) {
        UUID uuid = player.getUniqueId();
        Entity entity = activeMounts.remove(uuid);
        List<Entity> seats = activeSeats.remove(uuid);
        activeMountTypes.remove(uuid);
        activeData.remove(uuid);

        if (entity != null && entity.isValid()) {
            entity.remove();
        }

        if (seats != null) {
            for (Entity seat : seats) {
                if (seat != null && seat.isValid()) {
                    seat.remove();
                }
            }
        }

        ItemStack rod = player.getInventory().getItem(6);
        if (rod != null && (rod.getType() == Material.CARROT_ON_A_STICK || rod.getType() == Material.WARPED_FUNGUS_ON_A_STICK)) {
            player.getInventory().setItem(6, null);
        }
    }

    @EventHandler
    public void onDismount(EntityDismountEvent e) {
        if (e.getEntity() instanceof Player player) {
            Entity mount = e.getDismounted();
            if (mount.getPersistentDataContainer().has(mountKey, PersistentDataType.BYTE)) {
                PersistentData data = activeData.get(player.getUniqueId());
                if (data != null) {
                    data.setMount(null);
                    PersistentDataManager.saveData(player.getUniqueId());
                }
                removeMount(player);
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        removeMount(e.getPlayer());
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        removeMount(e.getPlayer());
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent e) {
        Entity clicked = e.getRightClicked();
        if (clicked instanceof Camel camel && camel.getPersistentDataContainer().has(mountKey, PersistentDataType.BYTE)) {
            if (!camel.getPassengers().isEmpty() && !camel.getPassengers().contains(e.getPlayer())) {
                if (camel.getPassengers().size() < 2) {
                    camel.addPassenger(e.getPlayer());
                }
            }
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        if (e.getEntity() != null && e.getEntity().getPersistentDataContainer().has(mountKey, PersistentDataType.BYTE)) {
            e.blockList().clear();
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent e) {
        if (e.getEntity() != null && e.getEntity().getPersistentDataContainer().has(mountKey, PersistentDataType.BYTE)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (e.getDamager().getPersistentDataContainer().has(mountKey, PersistentDataType.BYTE)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (e.getEntity().getPersistentDataContainer().has(mountKey, PersistentDataType.BYTE)) {
            e.setCancelled(true);
        }
    }

    private void startTasks() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<UUID, Entity> entry : activeMounts.entrySet()) {
                    UUID uuid = entry.getKey();
                    Entity mount = entry.getValue();
                    Player player = Bukkit.getPlayer(uuid);

                    if (player == null || mount == null || !mount.isValid()) continue;

                    Mount mountType = activeMountTypes.get(uuid);

                    if (mountType == Mount.ENDER_DRAGON) {
                        List<Entity> seats = activeSeats.get(uuid);
                        if (seats == null || seats.isEmpty()) continue;

                        ArmorStand seat = (ArmorStand) seats.get(0);
                        ItemStack mainHand = player.getInventory().getItemInMainHand();

                        if (mainHand.getType() == Material.CARROT_ON_A_STICK) {
                            Vector dir = player.getLocation().getDirection();
                            seat.setVelocity(dir.multiply(1.5));
                        } else {
                            seat.setVelocity(seat.getVelocity().multiply(0.8));
                        }

                        Location target = seat.getLocation().clone().subtract(0, 2.8, 0);
                        target.setYaw(player.getLocation().getYaw() + 180f);
                        target.setPitch(-player.getLocation().getPitch());
                        mount.teleport(target);

                    } else if (mountType == Mount.SNIFFER) {
                        mount.setRotation(player.getLocation().getYaw(), player.getLocation().getPitch());
                        ItemStack mainHand = player.getInventory().getItemInMainHand();

                        if (mainHand.getType() == Material.CARROT_ON_A_STICK) {
                            Vector dir = player.getLocation().getDirection();
                            dir.setY(0).normalize().multiply(0.4);
                            mount.setVelocity(new Vector(dir.getX(), mount.getVelocity().getY(), dir.getZ()));
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<UUID, Entity> entry : activeMounts.entrySet()) {
                    UUID uuid = entry.getKey();
                    Entity mount = entry.getValue();
                    if (mount == null || !mount.isValid()) continue;

                    Mount mountType = activeMountTypes.get(uuid);
                    if (mountType != Mount.BLACK_HORSE && mountType != Mount.WHITE_HORSE) continue;

                    Vector direction = mount.getLocation().getDirection().setY(0);
                    if (direction.lengthSquared() > 0) {
                        direction.normalize().multiply(2.0);
                    }

                    Location tailLoc = mount.getLocation().clone().subtract(direction);
                    Block centerBlock = tailLoc.getBlock().getRelative(0, -1, 0);

                    for (int x = -1; x <= 1; x++) {
                        for (int z = -1; z <= 1; z++) {
                            Block block = centerBlock.getRelative(x, 0, z);
                            Location loc = block.getLocation();

                            if (originalBlocks.containsKey(loc)) continue;

                            originalBlocks.put(loc, block.getState());

                            Material newMaterial;
                            if (mountType == Mount.BLACK_HORSE) {
                                newMaterial = Math.random() > 0.5 ? Material.COAL_BLOCK : Material.BLACK_WOOL;
                            } else {
                                newMaterial = rainbow[rainbowIndex];
                                rainbowIndex = (rainbowIndex + 1) % rainbow.length;
                            }

                            block.setBlockData(newMaterial.createBlockData(), false);

                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    BlockState state = originalBlocks.remove(loc);
                                    if (state != null) {
                                        state.update(true, false);
                                    }
                                }
                            }.runTaskLater(plugin, 60L);
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }
}