package dabbiks.uhc.game.world.events;

import dabbiks.uhc.game.configs.WorldConfig;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.Random;

import static dabbiks.uhc.Main.*;

public class WeatherCycle {

    final static double sunnyToRainyChance = 0.05;
    final static double rainyToStormChance = 0.1;
    final static double stormToRainyChance = 0.3;
    final static double rainyToSunnyChance = 0.2;

    public static int sunnyDays = 0;

    public static String oldWeather = symbolU.WEATHER_SUNNY;
    public static String newWeather = symbolU.WEATHER_SUNNY;

    public static void rollWeather() {
        World world = Bukkit.getWorld(WorldConfig.worldName);
        Random random = new Random();
        if (world == null) return;

        double chance = random.nextDouble(0, 1);
        boolean didChange = false;

        if (world.isThundering() && chance <= stormToRainyChance) {
            world.setThundering(false);
            world.setStorm(true);
            didChange = true;
        }

        else if (world.hasStorm() && chance <= rainyToStormChance) {
            world.setThundering(true);
            didChange = true;
        }

        else if (world.hasStorm() && chance <= rainyToStormChance + rainyToSunnyChance) {
            world.setStorm(false);
            didChange = true;
        }

        else if (world.isClearWeather() && chance <= sunnyToRainyChance) {
            world.setStorm(true);
            didChange = true;
        }

        String currentWeather = getWeatherIcon();
        if (didChange && !oldWeather.equals(currentWeather)) {
            messageU.sendMessageToPlayers(playerListU.getAllPlayers(), "§f" + oldWeather + " ➜ " + currentWeather);
            oldWeather = currentWeather;
            newWeather = currentWeather;
        } else {
            newWeather = currentWeather;
        }
    }

    public static String getWeatherIcon() {
        World world = Bukkit.getWorld(WorldConfig.worldName);
        if (world == null) { return null; }

        if (world.isClearWeather()) {
            return symbolU.WEATHER_SUNNY;
        }
        if (world.isThundering()) {
            return symbolU.WEATHER_STORM;
        }
        return symbolU.WEATHER_RAINY;
    }

}
