package dabbiks.uhc.cosmetics.chest;

import dabbiks.uhc.cosmetics.chest.rewards.Reward;
import dabbiks.uhc.player.data.persistent.PersistentData;
import dabbiks.uhc.player.data.persistent.PersistentDataManager;
import dabbiks.uhc.utils.managers.TextParticleManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Lidded;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.TextDisplay;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static dabbiks.uhc.Main.*;

public class MysteryChestSession {
    public static MysteryChestSession activeSession = null;

    private final UUID uuid;
    private final Location centerLocation = new Location(Bukkit.getWorld("world"), -44, 102, 3);
    private final Location timerLocation = new Location(Bukkit.getWorld("world"), -48, 104, 3.5);
    private final ChestType chestType;
    private int timer = 30;
    private int chestsLeft;
    private final Map<Location, BlockFace> chests = new HashMap<>();
    private final List<Location> openedChests = new ArrayList<>();
    private final List<TextDisplay> textDisplays = new ArrayList<>();
    private final List<ItemDisplay> itemDisplays = new ArrayList<>();
    private final List<String> rewards = new ArrayList<>();
    private TextDisplay timerDisplay;
    private boolean isEnding = false;
    private boolean rewardMessage = false;
    private final TextParticleManager particleEngine = new TextParticleManager();

    public MysteryChestSession(UUID uuid, ChestType chestType) {
        this.uuid = uuid;
        this.chestType = chestType;
        this.chestsLeft = chestType.getChests();

        if (activeSession != null) return;
        activeSession = this;

        PersistentData persistentData = PersistentDataManager.getData(uuid);
        persistentData.addChests(chestType.getIndex(), -1);
        persistentData.addKeys(chestType.getIndex(), -1);
        PersistentDataManager.saveData(uuid);

        startSession();
    }

    private void startSession() {
        World world = Bukkit.getWorld("world");
        Bukkit.getPlayer(uuid).getInventory().close();

        if (chestType == ChestType.MYTHIC) soundU.playSoundAtLocation(centerLocation, Sound.ENTITY_WITHER_DEATH, 0.7f, 0.6f);
        if (chestType == ChestType.LEGENDARY) soundU.playSoundAtLocation(centerLocation, Sound.ENTITY_ENDER_DRAGON_DEATH, 0.7f, 0.6f);

        new ChestMessage().send(chestType, uuid, null);

        soundU.playSoundAtLocation(centerLocation, Sound.BLOCK_ANVIL_USE, 1, 1);
        chests.put(new Location(world, centerLocation.getX(), centerLocation.getY(), centerLocation.getZ() - 2), BlockFace.SOUTH);
        chests.put(new Location(world, centerLocation.getX() - 2, centerLocation.getY(), centerLocation.getZ() - 1), BlockFace.EAST);
        chests.put(new Location(world, centerLocation.getX() - 2, centerLocation.getY(), centerLocation.getZ() + 1), BlockFace.EAST);
        chests.put(new Location(world, centerLocation.getX(), centerLocation.getY(), centerLocation.getZ() + 2), BlockFace.NORTH);
        chests.put(new Location(world, centerLocation.getX() + 2, centerLocation.getY(), centerLocation.getZ() + 1), BlockFace.WEST);
        chests.put(new Location(world, centerLocation.getX() + 2, centerLocation.getY(), centerLocation.getZ() - 1), BlockFace.WEST);
        centerLocation.getBlock().setType(Material.AIR);

        timerDisplay = timerLocation.getWorld().spawn(timerLocation, TextDisplay.class, entity -> {
            entity.setRotation(-90, 0);
            entity.setText("§c" + timer);
            entity.setBillboard(Display.Billboard.FIXED);
            entity.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
        });
        textDisplays.add(timerDisplay);

        textDisplays.add(timerLocation.getWorld().spawn(timerLocation.clone().add(0, 0.3, 0), TextDisplay.class, entity -> {
            entity.setRotation(-90, 0);
            entity.setText(chestType.getName());
            entity.setBillboard(Display.Billboard.FIXED);
            entity.setShadowed(true);
            entity.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
        }));
        textDisplays.add(timerLocation.getWorld().spawn(timerLocation.clone().add(0, 0.6, 0), TextDisplay.class, entity -> {
            entity.setRotation(-90, 0);
            if (Bukkit.getPlayer(uuid) != null) entity.setText("§c" + Bukkit.getPlayer(uuid).getName() + " §fotwiera...");
            entity.setBillboard(Display.Billboard.FIXED);
            entity.setShadowed(true);
            entity.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
        }));

        int delay = 0;
        for (Map.Entry<Location, BlockFace> entry : chests.entrySet()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    spawnChest(centerLocation, entry.getKey(), entry.getValue());
                }
            }.runTaskLater(plugin, delay);
            delay += 3;
        }

        startTimer();
        startParticleEffects();
    }

    private void startParticleEffects() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (isEnding || activeSession == null) {
                    this.cancel();
                    return;
                }

                for (Location loc : chests.keySet()) {
                    if (!openedChests.contains(loc) && loc.getBlock().getType() == Material.CHEST) {
                        spawnQuestionParticle(loc);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 6L);
    }

    private void spawnQuestionParticle(Location chestLoc) {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        double offsetX = random.nextDouble(-0.1, 1.1);
        double offsetZ = random.nextDouble(-0.1, 1.1);
        Location spawnLoc = chestLoc.clone().add(offsetX, 0.6, offsetZ);

        Vector velocity = new Vector(0, 0.035, 0);
        int lifespan = 30;

        particleEngine.spawnParticle(spawnLoc, symbolU.QUESTION_MARK, velocity, lifespan);
    }

    private void startTimer() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (isEnding) {
                    this.cancel();
                    return;
                }
                if (timer <= 0 || chestsLeft <= 0) {
                    this.cancel();
                    autoOpenAndEnd();
                    return;
                }
                soundU.playSoundAtLocation(centerLocation, Sound.AMBIENT_CAVE, 0.4f, 1);
                timer--;
                if (timerDisplay != null && timerDisplay.isValid()) {
                    timerDisplay.setText("§c" + timer);
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    public void tryOpenChest(UUID playerUuid, Block block) {
        if (isEnding || !playerUuid.equals(uuid)) return;
        Location loc = block.getLocation();
        if (!chests.containsKey(loc) || openedChests.contains(loc)) return;

        if (chestsLeft <= 0) return;
        if (block.getState() instanceof Lidded lid) {
            lid.open();
            openedChests.add(loc);
            chestsLeft--;
            soundU.playSoundAtLocation(loc, Sound.BLOCK_PISTON_CONTRACT, 1, 0.6f);
            soundU.playSoundAtLocation(loc, Sound.BLOCK_CHEST_OPEN, 1, 1);
            giveReward(loc);
            if (chestsLeft == 0 && !rewardMessage) { new ChestMessage().send(chestType, uuid, rewards); rewardMessage = true; }
        }
    }

    private void giveReward(Location location) {
        Reward reward = ChestRewardManager.drawReward(chestType);
        if (reward == null) return;

        textDisplays.add(location.getWorld().spawn(location.clone().add(0.5, 1.75, 0.5), TextDisplay.class, entity -> {
            entity.setText(reward.getType());
            entity.setShadowed(true);
            entity.setBillboard(Display.Billboard.CENTER);
            entity.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
        }));
        textDisplays.add(location.getWorld().spawn(location.clone().add(0.5, 1.5, 0.5), TextDisplay.class, entity -> {
            entity.setText(reward.getName());
            entity.setShadowed(true);
            entity.setBillboard(Display.Billboard.CENTER);
            entity.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
        }));
        itemDisplays.add(location.getWorld().spawn(location.clone().add(0.5, 1, 0.5), ItemDisplay.class, entity -> {
            entity.setItemStack(reward.getItem());
            setHorizontalRotation(entity, centerLocation.clone().add(0.5, 0, 0.5));
            Transformation t = entity.getTransformation();
            t.getScale().set(0.8f, 0.8f, 0.8f);
            entity.setTransformation(t);
        }));
        reward.spawnEffect(location.clone().add(0.5, 1, 0.5));
        rewards.add(reward.getType() + " §f" + reward.getName());

        PersistentData data = PersistentDataManager.getData(uuid);
        if (data != null) {
            reward.addReward(data);
        }
    }

    private void autoOpenAndEnd() {
        if (isEnding) return;
        isEnding = true;

        int delay = 0;
        for (Location location : chests.keySet()) {
            if (!openedChests.contains(location)) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (chestsLeft <= 0) {
                            Block block = location.getBlock();
                            block.setType(Material.AIR);
                            soundU.playSoundAtLocation(location, Sound.ENTITY_ZOMBIE_DEATH, 0.8f, 1f);
                            soundU.playSoundAtLocation(location, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 0.5f, 1.6f);
                        } else {
                            Block block = location.getBlock();
                            if (block.getState() instanceof Lidded lid) {
                                lid.open();
                            }
                            giveReward(location);
                            chestsLeft--;
                            if (chestsLeft == 0 && !rewardMessage) { new ChestMessage().send(chestType, uuid, rewards); rewardMessage = true; }
                        }
                    }
                }.runTaskLater(plugin, delay);
                delay += 3;
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                int delay = 0;
                for (Location location : chests.keySet()) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            location.getBlock().setType(Material.AIR);
                            soundU.playSoundAtLocation(location, Sound.ENTITY_ZOMBIE_DEATH, 0.8f, 1f);
                            soundU.playSoundAtLocation(location, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 0.5f, 1.6f);
                        }
                    }.runTaskLater(plugin, delay);
                    delay += 3;
                }
                for (TextDisplay display : textDisplays) {
                    if (display != null && display.isValid()) display.remove();
                }
                for (ItemDisplay display : itemDisplays) {
                    if (display != null && display.isValid()) display.remove();
                }
                Block block = centerLocation.getBlock();
                block.setType(Material.ENDER_CHEST);
                if (block.getBlockData() instanceof org.bukkit.block.data.Directional directional) {
                    directional.setFacing(BlockFace.EAST);
                    block.setBlockData(directional);
                }
                activeSession = null;
            }
        }.runTaskLater(plugin, delay + 100L);
    }

    private void spawnChest(Location loc1, Location loc2, BlockFace face) {
        BlockDisplay display = loc1.getWorld().spawn(loc1, BlockDisplay.class, entity -> {
            entity.setBlock(Material.HOPPER.createBlockData());
        });

        new BukkitRunnable() {
            int ticks = 0;
            final int maxTicks = 24;

            @Override
            public void run() {
                if (ticks >= maxTicks) {
                    display.remove();
                    Block block = loc2.getBlock();
                    block.setType(Material.CHEST);
                    soundU.playSoundAtLocation(loc2, Sound.ENTITY_ZOMBIE_DEATH, 0.8f, 1f);
                    if (block.getBlockData() instanceof org.bukkit.block.data.Directional directional) {
                        directional.setFacing(face);
                        block.setBlockData(directional);
                    }
                    this.cancel();
                    return;
                }

                double progress = (double) ticks / maxTicks;
                Vector endV = loc2.toVector().subtract(loc1.toVector());
                Vector currentV = endV.multiply(progress);
                double height = 3.0 * Math.sin(Math.PI * progress);
                currentV.add(new Vector(0, height, 0));

                org.bukkit.util.Transformation trans = display.getTransformation();
                trans.getTranslation().set((float) currentV.getX(), (float) currentV.getY(), (float) currentV.getZ());

                display.setTransformation(trans);
                display.setInterpolationDuration(2);
                display.setInterpolationDelay(0);

                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void setHorizontalRotation(ItemDisplay display, Location target) {
        Location start = display.getLocation();
        double dx = target.getX() - start.getX();
        double dz = target.getZ() - start.getZ();

        if (dx == 0 && dz == 0) return;

        float yaw = (float) Math.atan2(dz, dx) - (float) Math.PI / 2;

        Transformation t = display.getTransformation();
        t.getLeftRotation().set(new org.joml.AxisAngle4f(-yaw, 0, 1, 0));
        display.setTransformation(t);
    }
}