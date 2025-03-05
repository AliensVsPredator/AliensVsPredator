package com.avp.common.entity.constant;

public class AttackDamageConstants {

    // Basic measurements (in terms of half-hearts).
    public static final double PLAYER_HEALTH = 20;

    // Alien measurements (in terms of half-hearts).
    public static final double CHESTBURSTER_ATTACK_DAMAGE = PLAYER_HEALTH * 0.1;

    public static final double DRONE_ATTACK_DAMAGE = PLAYER_HEALTH * 0.25;

    public static final double PRAETORIAN_ATTACK_DAMAGE = PLAYER_HEALTH * 0.75;

    public static final double QUEEN_ATTACK_DAMAGE = PLAYER_HEALTH * 2.5;

    public static final double WARRIOR_ATTACK_DAMAGE = PLAYER_HEALTH * 0.5;

    public static final double YAUTJA_ATTACK_DAMAGE = PLAYER_HEALTH * 0.75;

    private AttackDamageConstants() {
        throw new UnsupportedOperationException();
    }
}
