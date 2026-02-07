package dabbiks.uhc.game.gameplay.items.attributes;

import org.bukkit.attribute.Attribute;

import static dabbiks.uhc.Main.symbolU;

public enum AttributeType {
    ATTACK_DAMAGE(symbolU.attack_damage, "Obrażenia", AttributeOperation.INCREASE, 0.15, Attribute.ATTACK_DAMAGE),
    HEALTH_BOOST(symbolU.health_boost, "Zdrowie", AttributeOperation.INCREASE, 2.0, Attribute.MAX_HEALTH),
    SNEAK_SPEED(symbolU.sneak_speed, "Skradanie", AttributeOperation.INCREASE, 0.1, Attribute.SNEAKING_SPEED),
    SIZE(symbolU.size, "Rozmiar", AttributeOperation.INCREASE, 0.3, Attribute.SCALE),
    GRAVITY(symbolU.gravity, "Grawitacja", AttributeOperation.DECREASE, 0.5, Attribute.GRAVITY),
    FALL_DAMAGE(symbolU.fall_damage, "Obrażenia upadku", AttributeOperation.INCREASE, -0.2, Attribute.FALL_DAMAGE_MULTIPLIER),
    JUMP_STRENGTH(symbolU.jump_strength, "Siła skoku", AttributeOperation.COMPARE, 1.0, Attribute.JUMP_STRENGTH),
    KNOCKBACK_POWER(symbolU.knockback_power, "Siła odrzutu", AttributeOperation.INCREASE, 0.2, Attribute.ATTACK_KNOCKBACK),
    KNOCKBACK_RESISTANCE(symbolU.knockback_resistance, "Odporność na odrzut", AttributeOperation.INCREASE, 0.1, Attribute.KNOCKBACK_RESISTANCE),
    BURNING_TIME(symbolU.burning_time, "Czas płonięcia", AttributeOperation.COMPARE, 1.0, Attribute.BURNING_TIME),
    LIFE_STEAL(symbolU.life_steal, "Wampiryzm", AttributeOperation.INCREASE, 0.05, null),
    ARMOR_PENETRATION(symbolU.armor_penetration, "Przebicie pancerza", AttributeOperation.INCREASE, 0.1, null),
    REGENERATION(symbolU.regeneration, "Regeneracja", AttributeOperation.INCREASE, 0.5, null),
    ATTACK_SPEED(symbolU.attack_speed, "Prędkość ataku", AttributeOperation.COMPARE, 1.0, Attribute.ATTACK_SPEED),
    CRIT_DAMAGE(symbolU.crit_damage, "Obrażenia krytyczne", AttributeOperation.INCREASE, 0.25, null),
    MOVEMENT_SPEED(symbolU.movement_speed, "Prędkość ruchu", AttributeOperation.INCREASE, 0.05, Attribute.MOVEMENT_SPEED),
    ARMOR(symbolU.armor, "Pancerz", AttributeOperation.INCREASE, 0.2, Attribute.ARMOR),
    ATTACK_RANGE(symbolU.attack_range, "Zasięg ataku", AttributeOperation.COMPARE, 1.0, Attribute.ENTITY_INTERACTION_RANGE),
    ELECTRIC_DAMAGE(symbolU.electric_damage, "Obrażenia elektryczne", AttributeOperation.INCREASE, 0.3, null);

    private final String symbol;
    private final String name;
    private final AttributeOperation operation;
    private final double multiplier;
    private final Attribute attribute;

    AttributeType(String symbol, String name, AttributeOperation operation, double multiplier, Attribute attribute) {
        this.symbol = symbol;
        this.name = name;
        this.operation = operation;
        this.multiplier = multiplier;
        this.attribute = attribute;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public AttributeOperation getOperation() {
        return operation;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public Attribute getAttribute() {
        return attribute;
    }

}