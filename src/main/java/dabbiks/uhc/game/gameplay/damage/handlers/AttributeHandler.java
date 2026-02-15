package dabbiks.uhc.game.gameplay.damage.handlers;

import dabbiks.uhc.game.gameplay.items.data.attributes.AttributeType;
import dabbiks.uhc.game.world.events.WeatherCycle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Objects;

import static dabbiks.uhc.Main.*;

public class AttributeHandler {

    public double handle(Player damager, LivingEntity victim, double damage, AttributeType attributeType) {
        if (attributeType == AttributeType.ELECTRIC_DAMAGE) return handleElectricDamage(damager, victim, damage);
        if (attributeType == AttributeType.LIFE_STEAL) return damage;
        return 0;
    }

    private double handleElectricDamage(Player damager, LivingEntity victim, double damage) {
        if (Objects.equals(WeatherCycle.getWeatherIcon(), symbolU.WEATHER_SUNNY)) return 0;
        double electricDamage = attributeManager.getAttributeValue(damager, AttributeType.ELECTRIC_DAMAGE);
        if (victim.getLocation().getBlockY() < victim.getWorld().getHighestBlockYAt(victim.getLocation().getBlockX(), victim.getLocation().getBlockZ())) return 0;
        return electricDamage;
    }

    private void handleLifeSteal(Player damager, LivingEntity victim, double damage) {
        double lifeSteal = attributeManager.getAttributeValue(damager, AttributeType.LIFE_STEAL);
        if (lifeSteal == 0) return;
        if (victim instanceof Player) {
            playerU.addHealth(damager, lifeSteal);
        } else {
            playerU.addHealth(damager, lifeSteal / 2);
        }
    }
}
