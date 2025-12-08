package com.blib.common.gameplay.model.spawning;

public record AdvancedStats(
    float health,
    float attackDamage,
    float healthRegenPerSecond,
    float knockbackResistance,
    float moveSpeed,
    float armor,
    float armorToughness,
    int nestTickRate,
    float followRange
) {}
