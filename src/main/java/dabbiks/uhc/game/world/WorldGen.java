package dabbiks.uhc.game.world;

import dabbiks.uhc.game.configs.WorldConfig;
import dabbiks.uhc.game.gameplay.items.conversion.ItemConverter;
import dabbiks.uhc.game.gameplay.setpieces.SetPieceFileManager;
import dabbiks.uhc.game.gameplay.setpieces.SetPieceGraveLoot;
import org.bukkit.*;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.popcraft.chunky.api.ChunkyAPI;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static dabbiks.uhc.Main.INSTANCE;
import static dabbiks.uhc.Main.plugin;
import static org.bukkit.Bukkit.getLogger;

public class WorldGen {

    public static void createWorld() {
        if (Bukkit.getWorld(WorldConfig.worldName) != null) {
            Bukkit.unloadWorld(Bukkit.getWorld(WorldConfig.worldName), false);
        }

        File worldFile = new File(Bukkit.getWorldContainer(), WorldConfig.worldName);
        deleteFolder(worldFile);

        WorldCreator creator = new WorldCreator(WorldConfig.worldName);
        creator.createWorld();

        World world = Bukkit.getWorld(WorldConfig.worldName);

        if (world == null) {
            getLogger().severe("Failed while creating new " + WorldConfig.worldName);
            return;
        }

        world.setSpawnLocation(0, 100, 0);
        world.setPVP(false);
        world.setDifficulty(Difficulty.EASY);

        setGameRule(world, "show_advancement_messages", false);
        setGameRule(world, "spectators_generate_chunks", false);
        setGameRule(world, "advance_weather", false);
        setGameRule(world, "immediate_respawn", true);
        setGameRule(world, "random_tick_speed", 0);
        setGameRule(world, "reduced_debug_info", false);
        setGameRule(world, "natural_health_regeneration", false);
        setGameRule(world, "show_death_messages", false);
        setGameRule(world, "advance_time", false);
        setGameRule(world, "locator_bar", false);
        setGameRule(world, "spawn_patrols", false);
        setGameRule(world, "spawn_wandering_traders", false);
        setGameRule(world, "fire_spread_radius_around_player", 0);
        setGameRule(world, "spawn_phantoms", false);

        getLogger().info("Hello Mr. Dabbiks!");

        ChunkyAPI chunky = Bukkit.getServer().getServicesManager().load(ChunkyAPI.class);

        chunky.startTask(WorldConfig.worldName, "square", 0, 0, 400, 400, "region");
        chunky.onGenerationComplete(event -> {
            WorldConfig.isWorldGenerated = true;
            Bukkit.getScheduler().runTask(plugin, () -> {
                generateInvertedPyramid(world);
                generateGraves(world);
            });
        });
    }

    private static void generateInvertedPyramid(World world) {
        int baseY = calculateBaseY(world);
        int maxSteps = 100;
        Random random = new Random();
        int totalRadius = 7 + (maxSteps * 2);

        boolean[][] decorateMap = new boolean[totalRadius * 2 + 1][totalRadius * 2 + 1];
        int[][] targetYMap = new int[totalRadius * 2 + 1][totalRadius * 2 + 1];

        for (int x = -totalRadius; x <= totalRadius; x++) {
            for (int z = -totalRadius; z <= totalRadius; z++) {
                int maxDist = Math.max(Math.abs(x), Math.abs(z));
                int step = (maxDist <= 7) ? 0 : (maxDist - 8) / 2 + 1;

                if (step > maxSteps) continue;

                int targetY = baseY + step;
                int mapX = x + totalRadius;
                int mapZ = z + totalRadius;
                targetYMap[mapX][mapZ] = targetY;

                boolean shouldDecorate = false;
                Material blockBelow = world.getBlockAt(x, targetY - 1, z).getType();

                if (blockBelow != Material.AIR && !org.bukkit.Tag.LEAVES.isTagged(blockBelow)) {
                    shouldDecorate = true;
                } else {
                    for (int y = targetY; y <= 319; y++) {
                        Material type = world.getBlockAt(x, y, z).getType();
                        if (type != Material.AIR && type.isSolid() && !org.bukkit.Tag.LEAVES.isTagged(type) && !org.bukkit.Tag.LOGS.isTagged(type)) {
                            shouldDecorate = true;
                            break;
                        }
                    }
                }

                decorateMap[mapX][mapZ] = shouldDecorate;

                for (int y = 319; y >= targetY; y--) {
                    world.getBlockAt(x, y, z).setType(Material.AIR, false);
                }

                if (shouldDecorate) {
                    setTerrainSurface(world, x, targetY - 1, z);
                }
            }
        }

        for (int x = -totalRadius; x <= totalRadius; x++) {
            for (int z = -totalRadius; z <= totalRadius; z++) {
                int mapX = x + totalRadius;
                int mapZ = z + totalRadius;
                if (decorateMap[mapX][mapZ]) {
                    decorateSurface(world, x, targetYMap[mapX][mapZ] - 1, z, random);
                }
            }
        }
    }

    private static int calculateBaseY(World world) {
        int y = world.getHighestBlockYAt(0, 0);

        while (y > world.getMinHeight()) {
            org.bukkit.block.Block block = world.getBlockAt(0, y, 0);
            Material type = block.getType();
            String typeName = type.name();

            if (type.isAir() || typeName.contains("LEAVES") || typeName.contains("LOG") || typeName.contains("WOOD")) {
                y--;
            } else {
                break;
            }
        }

        return y - 10;
    }

    private static void setTerrainSurface(World world, int x, int y, int z) {
        org.bukkit.block.Biome biome = world.getBiome(x, y, z);
        Material surfaceMaterial = Material.GRASS_BLOCK;
        String biomeName = biome.name();

        if (biomeName.contains("DESERT") || biomeName.contains("BADLANDS")) {
            surfaceMaterial = Material.SAND;
        } else if (biomeName.contains("SNOW") || biomeName.contains("ICE")) {
            surfaceMaterial = Material.SNOW_BLOCK;
        } else if (biomeName.contains("MUSHROOM")) {
            surfaceMaterial = Material.MYCELIUM;
        }

        world.getBlockAt(x, y, z).setType(surfaceMaterial, false);

        for (int i = 1; i <= 20; i++) {
            org.bukkit.block.Block under = world.getBlockAt(x, y - i, z);
            if (i > 3 && under.getType().isSolid() && !org.bukkit.Tag.LEAVES.isTagged(under.getType())) {
                break;
            }
            Material underMat = surfaceMaterial == Material.SAND ? Material.SANDSTONE : Material.DIRT;
            under.setType(underMat, false);
        }
    }

    private static void decorateSurface(World world, int x, int y, int z, Random random) {
        org.bukkit.block.Biome biome = world.getBiome(x, y, z);
        String biomeName = biome.name();
        Material surfaceMaterial = world.getBlockAt(x, y, z).getType();
        Material decorationMaterial = Material.AIR;

        if (surfaceMaterial == Material.SAND) {
            if (random.nextInt(10) == 0) decorationMaterial = Material.DEAD_BUSH;
        } else if (surfaceMaterial == Material.MYCELIUM) {
            if (random.nextInt(15) == 0) decorationMaterial = random.nextBoolean() ? Material.RED_MUSHROOM : Material.BROWN_MUSHROOM;
        } else if (biomeName.contains("JUNGLE")) {
            if (random.nextInt(10) == 0) decorationMaterial = Material.JUNGLE_SAPLING;
        } else if (surfaceMaterial == Material.GRASS_BLOCK) {
            if (random.nextInt(15) == 0) {
                decorationMaterial = random.nextBoolean() ? Material.DANDELION : Material.POPPY;
            } else if (random.nextInt(10) == 0) {
                decorationMaterial = Material.SHORT_GRASS;
            }
        }

        if (decorationMaterial != Material.AIR) {
            world.getBlockAt(x, y + 1, z).setType(decorationMaterial, false);
        } else if (random.nextInt(60) == 0) {
            if (surfaceMaterial == Material.SAND) {
                int height = 2 + random.nextInt(2);
                for (int h = 0; h < height; h++) {
                    world.getBlockAt(x, y + 1 + h, z).setType(Material.CACTUS, false);
                }
            } else if (surfaceMaterial == Material.GRASS_BLOCK) {
                TreeType treeType = TreeType.TREE;
                if (biomeName.contains("JUNGLE")) treeType = TreeType.JUNGLE;
                else if (biomeName.contains("BIRCH")) treeType = TreeType.BIRCH;
                else if (biomeName.contains("TAIGA") || biomeName.contains("SPRUCE")) treeType = TreeType.REDWOOD;
                else if (biomeName.contains("DARK_OAK")) treeType = TreeType.DARK_OAK;
                else if (biomeName.contains("SAVANNA")) treeType = TreeType.ACACIA;

                world.generateTree(new Location(world, x, y + 1, z), treeType);
            }
        }
    }

    private static <T> void setGameRule(World world, String ruleName, T value) {
        GameRule<T> rule = GameRule.getByName(ruleName);
        if (rule != null) {
            world.setGameRule(rule, value);
        } else {
            getLogger().warning("GameRule " + ruleName + " not found.");
        }
    }

    private static void deleteFolder(File source) {
        try {
            if (!source.exists()) {
                return;
            }

            File[] files = source.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory() && file.getName().equals("datapacks")) {
                        continue;
                    } else if (file.isDirectory()) {
                        deleteFolder(file);
                    } else {
                        file.delete();
                    }
                }
            }
            source.delete();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }


    private static boolean canGenerateGrave(Location location) {
        World world = location.getWorld();
        if (!location.clone().add(0,-1,0).getBlock().isSolid()) return false;
        for (int x = location.getBlockX() - 2; x <= location.getBlockX() + 2; x++) {
            for (int z = location.getBlockZ() - 2; z <= location.getBlockZ() + 2; z++) {
                if (!world.getBlockAt(x,location.getBlockY()-1,z).isSolid()) return false;
                for (int y = location.getBlockY(); y <= location.getBlockY() + 1; y++) {
                    if (world.getBlockAt(x, y, z).getType().isSolid()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static void generateGraves(World world) {
        INSTANCE.gravesFileManager.load();
        List<List<ItemStack>> deadPlayersItems = new ArrayList<>(INSTANCE.gravesFileManager.deadPlayersItems.values());
        Random random = new Random();
        SetPieceGraveLoot randomGraveLoot = new SetPieceGraveLoot();
        for (int i = 0; i < random.nextInt(8, 15); i++) {
            deadPlayersItems.add(randomGraveLoot.getRandomItems());
        }
        int size = (int) WorldConfig.worldBorderSize;
        for (List<ItemStack> items : deadPlayersItems) {
            boolean[][] safeLocations = {
                    {true,true,true,true,true},
                    {true,true,true,true,true},
                    {true,true,false,true,true},
                    {true,true,true,true,true},
                    {true,true,true,true,true}
            };
            boolean accepted = false;
            int attempts = 0;
            while (!accepted && attempts < 50) {
                attempts++;
                int x = random.nextInt(-size, size);
                int z = random.nextInt(-size, size);
                int y = world.getHighestBlockYAt(x, z, HeightMap.MOTION_BLOCKING_NO_LEAVES) + 1;
                Location loc = new Location(world, x, y, z);
                if (!canGenerateGrave(loc)) continue;
                accepted = true;
                Bukkit.getLogger().info("Grob na: x "+x+", y "+y+", z "+z);
                ItemStack bone = new ItemConverter().convert(new ItemStack(Material.BONE));
                ItemStack rottenFlesh = new ItemConverter().convert(new ItemStack(Material.ROTTEN_FLESH));
                items.add(bone.clone());
                items.add(bone.clone());
                items.add(rottenFlesh.clone());
                if (random.nextDouble() > 0.30) items.add(rottenFlesh.clone());
                if (random.nextDouble() > 0.70) items.add(rottenFlesh.clone());
                for (ItemStack item : items) {
                    Location displayLoc = loc.clone();
                    boolean ac = false;
                    int tries = 0;
                    while (!ac && tries < 20) {
                        int[] offset = {random.nextInt(0,5),random.nextInt(0,5)};
                        if (!safeLocations[offset[0]][offset[1]]) {
                            tries++;
                            continue;
                        }
                        ac = true;
                        displayLoc.add(offset[0]-2,0,offset[1]-2);
                        safeLocations[offset[0]][offset[1]] = false;
                    }
                    if (tries >= 20) {
                        continue;
                    }
                    ItemDisplay itemDisplay = world.spawn(displayLoc, ItemDisplay.class);
                    itemDisplay.setItemStack(item);
                    if (SetPieceFileManager.shouldBeStuckInGround(item.getType())) {
                        itemDisplay.setTransformation(new Transformation(
                                new Vector3f(random.nextFloat(-0.2F,0.2F), 0.2F,
                                        random.nextFloat(-0.2F,0.2F)),
                                new Quaternionf(
                                        (float) random.nextInt(-60, 60) / 100,
                                        (float) random.nextInt(-60, 60) / 100,
                                        1,
                                        (float) random.nextInt(0, 60) / 100
                                ).normalize(),
                                new Vector3f(1, 1, 1),
                                new Quaternionf(0, 0, 0, 1)
                        ));
                    } else {
                        itemDisplay.setTransformation(new Transformation(
                                new Vector3f(random.nextFloat(-0.2F,0.2F), 0,
                                        random.nextFloat(-0.2F,0.2F)),
                                new Quaternionf(1, 0, 0, 1).normalize(),
                                new Vector3f(0.7F, 0.7F, 1.6F),
                                new Quaternionf(0, 0, (float) random.nextInt(-200, 200) / 100, 1).normalize()
                        ));
                    }
                    INSTANCE.gravePickupHandler.addItemDisplay(itemDisplay);
                }
                loc.getBlock().setType(Material.SKELETON_SKULL);
            }
        }
        INSTANCE.gravesFileManager.deadPlayersItems.clear();
    }
}