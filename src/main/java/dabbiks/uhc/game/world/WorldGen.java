package dabbiks.uhc.game.world;

import dabbiks.uhc.game.configs.WorldConfig;
import org.bukkit.*;
import org.popcraft.chunky.api.ChunkyAPI;

import java.io.File;
import java.util.Random;

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
            Bukkit.getScheduler().runTask(plugin, () -> generateInvertedPyramid(world));
        });
    }

    private static void generateInvertedPyramid(World world) {
        int baseY = 45;
        int maxSteps = 100;
        Random random = new Random();

        int totalRadius = 7 + (maxSteps * 2);

        for (int x = -totalRadius; x <= totalRadius; x++) {
            for (int z = -totalRadius; z <= totalRadius; z++) {

                int maxDist = Math.max(Math.abs(x), Math.abs(z));
                int step;

                if (maxDist <= 7) {
                    step = 0;
                } else {
                    step = (maxDist - 8) / 2 + 1;
                }

                if (step > maxSteps) continue;

                int targetY = baseY + step;
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

                for (int y = 319; y >= targetY; y--) {
                    world.getBlockAt(x, y, z).setType(Material.AIR, false);
                }

                if (shouldDecorate) {
                    setBiomeSpecificBlock(world, x, targetY - 1, z, random);
                }
            }
        }
    }

    private static void setBiomeSpecificBlock(World world, int x, int y, int z, Random random) {
        org.bukkit.block.Block block = world.getBlockAt(x, y, z);
        org.bukkit.block.Biome biome = world.getBiome(x, y, z);

        Material surfaceMaterial = Material.GRASS_BLOCK;
        Material decorationMaterial = Material.AIR;
        String biomeName = biome.name();

        if (biomeName.contains("DESERT") || biomeName.contains("BADLANDS")) {
            surfaceMaterial = Material.SAND;
            if (random.nextInt(10) == 0) decorationMaterial = Material.DEAD_BUSH;
        } else if (biomeName.contains("SNOW") || biomeName.contains("ICE")) {
            surfaceMaterial = Material.SNOW_BLOCK;
        } else if (biomeName.contains("MUSHROOM")) {
            surfaceMaterial = Material.MYCELIUM;
            if (random.nextInt(15) == 0) decorationMaterial = random.nextBoolean() ? Material.RED_MUSHROOM : Material.BROWN_MUSHROOM;
        } else if (biomeName.contains("JUNGLE")) {
            surfaceMaterial = Material.GRASS_BLOCK;
            if (random.nextInt(10) == 0) decorationMaterial = Material.JUNGLE_SAPLING;
        } else {
            surfaceMaterial = Material.GRASS_BLOCK;
            if (random.nextInt(15) == 0) {
                decorationMaterial = random.nextBoolean() ? Material.DANDELION : Material.POPPY;
            } else if (random.nextInt(10) == 0) {
                decorationMaterial = Material.SHORT_GRASS;
            }
        }

        block.setType(surfaceMaterial, false);

        for (int i = 1; i <= 20; i++) {
            org.bukkit.block.Block under = world.getBlockAt(x, y - i, z);
            if (i > 3 && under.getType().isSolid() && !org.bukkit.Tag.LEAVES.isTagged(under.getType())) {
                break;
            }
            Material underMat = surfaceMaterial == Material.SAND ? Material.SANDSTONE : Material.DIRT;
            under.setType(underMat, false);
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
}