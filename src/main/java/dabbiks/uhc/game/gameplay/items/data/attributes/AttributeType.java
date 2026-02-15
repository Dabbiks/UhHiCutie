package dabbiks.uhc.game.gameplay.items.data.attributes;

import org.bukkit.attribute.Attribute;

import static dabbiks.uhc.Main.symbolU;

public enum AttributeType {
    SIZE(                symbolU.SIZE,                 "Rozmiar",               AttributeOperation.INCREASE, 0.3,  Attribute.SCALE, false),
    SIZE_PERCENT(        symbolU.SIZE,                 "Rozmiar",               AttributeOperation.INCREASE, 0.3,  Attribute.SCALE, true),

    ARMOR(               symbolU.ARMOR,                "Pancerz",               AttributeOperation.INCREASE, 0.2,  Attribute.ARMOR, false),
    ARMOR_PERCENT(       symbolU.ARMOR,                "Pancerz",               AttributeOperation.INCREASE, 0.2,  Attribute.ARMOR, true),

    LIFE_STEAL(          symbolU.LIFE_STEAL,           "Wampiryzm",             AttributeOperation.INCREASE, 0.05, null, false),
    LIFE_STEAL_PERCENT(  symbolU.LIFE_STEAL,           "Wampiryzm",             AttributeOperation.INCREASE, 0.05, null, true),

    REGENERATION(        symbolU.REGENERATION,         "Regeneracja",           AttributeOperation.INCREASE, 0.5,  null, false),
    REGENERATION_PERCENT(symbolU.REGENERATION,         "Regeneracja",           AttributeOperation.INCREASE, 0.5,  null, true),

    ARROW_DAMAGE(        symbolU.ARROW_DAMAGE,         "Obrażenia",             AttributeOperation.INCREASE, 0.2,  null, false),
    ARROW_DAMAGE_PERCENT(symbolU.ARROW_DAMAGE,         "Obrażenia",             AttributeOperation.INCREASE, 0.2,  null, true),

    GRAVITY(             symbolU.GRAVITY,              "Grawitacja",            AttributeOperation.DECREASE, 0.5,  Attribute.GRAVITY, false),
    GRAVITY_PERCENT(     symbolU.GRAVITY,              "Grawitacja",            AttributeOperation.DECREASE, 0.5,  Attribute.GRAVITY, true),

    SNEAK_SPEED(         symbolU.SNEAK_SPEED,          "Skradanie",             AttributeOperation.INCREASE, 0.1,  Attribute.SNEAKING_SPEED, false),
    SNEAK_SPEED_PERCENT( symbolU.SNEAK_SPEED,          "Skradanie",             AttributeOperation.INCREASE, 0.1,  Attribute.SNEAKING_SPEED, true),

    HEALTH_BOOST(        symbolU.HEALTH_BOOST,         "Zdrowie",               AttributeOperation.INCREASE, 2.0,  Attribute.MAX_HEALTH, false),
    HEALTH_BOOST_PERCENT(symbolU.HEALTH_BOOST,         "Zdrowie",               AttributeOperation.INCREASE, 2.0,  Attribute.MAX_HEALTH, true),

    ATTACK_DAMAGE(       symbolU.ATTACK_DAMAGE,        "Obrażenia",             AttributeOperation.INCREASE, 0.15, Attribute.ATTACK_DAMAGE, false),
    ATTACK_DAMAGE_PERCENT(symbolU.ATTACK_DAMAGE,       "Obrażenia",             AttributeOperation.INCREASE, 0.15, Attribute.ATTACK_DAMAGE, true),

    ARMOR_PENETRATION(   symbolU.ARMOR_PENETRATION,    "Przebicie pancerza",    AttributeOperation.INCREASE, 0.1,  null, false),
    ARMOR_PENETRATION_PERCENT(symbolU.ARMOR_PENETRATION, "Przebicie pancerza",  AttributeOperation.INCREASE, 0.1,  null, true),

    CRIT_DAMAGE(         symbolU.CRITICAL_DAMAGE,      "Obrażenia krytyczne",   AttributeOperation.INCREASE, 0.25, null, false),
    CRIT_DAMAGE_PERCENT( symbolU.CRITICAL_DAMAGE,      "Obrażenia krytyczne",   AttributeOperation.INCREASE, 0.25, null, true),

    BURNING_TIME(        symbolU.BURNING_TIME,         "Czas płonięcia",        AttributeOperation.COMPARE,  1.0,  Attribute.BURNING_TIME, false),
    BURNING_TIME_PERCENT(symbolU.BURNING_TIME,         "Czas płonięcia",        AttributeOperation.COMPARE,  1.0,  Attribute.BURNING_TIME, true),

    ATTACK_SPEED(        symbolU.ATTACK_SPEED,         "Prędkość ataku",        AttributeOperation.COMPARE,  1.0,  Attribute.ATTACK_SPEED, false),
    ATTACK_SPEED_PERCENT(symbolU.ATTACK_SPEED,         "Prędkość ataku",        AttributeOperation.COMPARE,  1.0,  Attribute.ATTACK_SPEED, true),

    JUMP_STRENGTH(       symbolU.JUMP_STRENGTH,        "Siła skoku",            AttributeOperation.COMPARE,  1.0,  Attribute.JUMP_STRENGTH, false),
    JUMP_STRENGTH_PERCENT(symbolU.JUMP_STRENGTH,       "Siła skoku",            AttributeOperation.COMPARE,  1.0,  Attribute.JUMP_STRENGTH, true),

    ELECTRIC_DAMAGE(     symbolU.ELECTRIC_DAMAGE,      "Obrażenia elektryczne", AttributeOperation.INCREASE, 0.3,  null, false),
    ELECTRIC_DAMAGE_PERCENT(symbolU.ELECTRIC_DAMAGE,   "Obrażenia elektryczne", AttributeOperation.INCREASE, 0.3,  null, true),

    MOVEMENT_SPEED(      symbolU.MOVEMENT_SPEED,       "Prędkość ruchu",        AttributeOperation.INCREASE, 0.05, Attribute.MOVEMENT_SPEED, false),
    MOVEMENT_SPEED_PERCENT(symbolU.MOVEMENT_SPEED,     "Prędkość ruchu",        AttributeOperation.INCREASE, 0.05, Attribute.MOVEMENT_SPEED, true),

    KNOCKBACK_POWER(     symbolU.KNOCKBACK_POWER,      "Siła odrzutu",          AttributeOperation.INCREASE, 0.2,  Attribute.ATTACK_KNOCKBACK, false),
    KNOCKBACK_POWER_PERCENT(symbolU.KNOCKBACK_POWER,   "Siła odrzutu",          AttributeOperation.INCREASE, 0.2,  Attribute.ATTACK_KNOCKBACK, true),

    FALL_DAMAGE(         symbolU.FALL_DAMAGE,          "Obrażenia upadku",      AttributeOperation.INCREASE, -0.2, Attribute.FALL_DAMAGE_MULTIPLIER, false),
    FALL_DAMAGE_PERCENT( symbolU.FALL_DAMAGE,          "Obrażenia upadku",      AttributeOperation.INCREASE, -0.2, Attribute.FALL_DAMAGE_MULTIPLIER, true),

    ATTACK_RANGE(        symbolU.ATTACK_RANGE,         "Zasięg ataku",          AttributeOperation.COMPARE,  1.0,  Attribute.ENTITY_INTERACTION_RANGE, false),
    ATTACK_RANGE_PERCENT(symbolU.ATTACK_RANGE,         "Zasięg ataku",          AttributeOperation.COMPARE,  1.0,  Attribute.ENTITY_INTERACTION_RANGE, true),

    KNOCKBACK_RESISTANCE(symbolU.KNOCKBACK_RESISTANCE,  "Odporność na odrzut",   AttributeOperation.INCREASE, 0.1,  Attribute.KNOCKBACK_RESISTANCE, false),
    KNOCKBACK_RESISTANCE_PERCENT(symbolU.KNOCKBACK_RESISTANCE, "Odporność na odrzut", AttributeOperation.INCREASE, 0.1, Attribute.KNOCKBACK_RESISTANCE, true);

    private final String symbol;
    private final String name;
    private final AttributeOperation operation;
    private final double multiplier;
    private final Attribute attribute;
    private final boolean isPercentage;

    AttributeType(String symbol, String name, AttributeOperation operation, double multiplier, Attribute attribute, boolean isPercentage) {
        this.symbol = symbol;
        this.name = name;
        this.operation = operation;
        this.multiplier = multiplier;
        this.attribute = attribute;
        this.isPercentage = isPercentage;
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

    public boolean isPercentage() {
        return isPercentage;
    }
}