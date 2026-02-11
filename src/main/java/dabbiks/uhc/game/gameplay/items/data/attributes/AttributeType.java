package dabbiks.uhc.game.gameplay.items.data.attributes;

import org.bukkit.attribute.Attribute;

import static dabbiks.uhc.Main.symbolU;

public enum AttributeType {
    ATTACK_DAMAGE(symbolU.ATTACK_DAMAGE, "Obrażenia", AttributeOperation.INCREASE, 0.15, Attribute.ATTACK_DAMAGE),
    HEALTH_BOOST(symbolU.HEALTH_BOOST, "Zdrowie", AttributeOperation.INCREASE, 2.0, Attribute.MAX_HEALTH),
    SNEAK_SPEED(symbolU.SNEAK_SPEED, "Skradanie", AttributeOperation.INCREASE, 0.1, Attribute.SNEAKING_SPEED),
    SIZE(symbolU.SIZE, "Rozmiar", AttributeOperation.INCREASE, 0.3, Attribute.SCALE),
    GRAVITY(symbolU.GRAVITY, "Grawitacja", AttributeOperation.DECREASE, 0.5, Attribute.GRAVITY),
    FALL_DAMAGE(symbolU.FALL_DAMAGE, "Obrażenia upadku", AttributeOperation.INCREASE, -0.2, Attribute.FALL_DAMAGE_MULTIPLIER),
    JUMP_STRENGTH(symbolU.JUMP_STRENGTH, "Siła skoku", AttributeOperation.COMPARE, 1.0, Attribute.JUMP_STRENGTH),
    KNOCKBACK_POWER(symbolU.KNOCKBACK_POWER, "Siła odrzutu", AttributeOperation.INCREASE, 0.2, Attribute.ATTACK_KNOCKBACK),
    KNOCKBACK_RESISTANCE(symbolU.KNOCKBACK_RESISTANCE, "Odporność na odrzut", AttributeOperation.INCREASE, 0.1, Attribute.KNOCKBACK_RESISTANCE),
    BURNING_TIME(symbolU.BURNING_TIME, "Czas płonięcia", AttributeOperation.COMPARE, 1.0, Attribute.BURNING_TIME),
    LIFE_STEAL(symbolU.LIFE_STEAL, "Wampiryzm", AttributeOperation.INCREASE, 0.05, null),
    ARMOR_PENETRATION(symbolU.ARMOR_PENETRATION, "Przebicie pancerza", AttributeOperation.INCREASE, 0.1, null),
    REGENERATION(symbolU.REGENERATION, "Regeneracja", AttributeOperation.INCREASE, 0.5, null),
    ATTACK_SPEED(symbolU.ATTACK_SPEED, "Prędkość ataku", AttributeOperation.COMPARE, 1.0, Attribute.ATTACK_SPEED),
    CRIT_DAMAGE(symbolU.CRITICAL_DAMAGE, "Obrażenia krytyczne", AttributeOperation.INCREASE, 0.25, null),
    MOVEMENT_SPEED(symbolU.MOVEMENT_SPEED, "Prędkość ruchu", AttributeOperation.INCREASE, 0.05, Attribute.MOVEMENT_SPEED),
    ARMOR(symbolU.ARMOR, "Pancerz", AttributeOperation.INCREASE, 0.2, Attribute.ARMOR),
    ATTACK_RANGE(symbolU.ATTACK_RANGE, "Zasięg ataku", AttributeOperation.COMPARE, 1.0, Attribute.ENTITY_INTERACTION_RANGE),
    ELECTRIC_DAMAGE(symbolU.ELECTRIC_DAMAGE, "Obrażenia elektryczne", AttributeOperation.INCREASE, 0.3, null);

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