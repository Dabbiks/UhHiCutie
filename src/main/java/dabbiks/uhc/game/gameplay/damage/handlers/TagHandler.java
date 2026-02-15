package dabbiks.uhc.game.gameplay.damage.handlers;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class TagHandler {

    public double handle(Entity damager, Entity victim, double damage) {
        return 0;
    }

    private double playerToPlayerTags(Player damager, Player victim, double damage) {
        return damage;
    }

    private double playerToMobTags(Player damager, Entity victim, double damage) {
        return damage;
    }

    private double mobToPlayerTags(Entity damager, Player victim, double damage) {
        return damage;
    }

}
