package dabbiks.uhc.game.world;

import dabbiks.uhc.Main;
import dabbiks.uhc.game.configs.WorldConfig;
import org.bukkit.*;
import org.popcraft.chunky.api.ChunkyAPI;
import org.popcraft.chunky.api.event.task.GenerationCompleteEvent;

import java.io.File;

import static dabbiks.uhc.Main.plugin;
import static org.bukkit.Bukkit.broadcastMessage;
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
        } );
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