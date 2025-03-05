package com.avp.common.entity.constant;

public class HealthConstants {

    // Basic measurements (in terms of half-hearts).
    public static final double PLAYER_HEALTH = 20;

    // Alien measurements (in terms of half-hearts).
    public static final double CHESTBURSTER_HEALTH = PLAYER_HEALTH * 0.25;

    public static final double DRONE_HEALTH = PLAYER_HEALTH * 2;

    public static final double FACEHUGGER_HEALTH = PLAYER_HEALTH * 0.15;

    public static final double OVAMORPH_HEALTH = PLAYER_HEALTH * 1.5;

    public static final double PRAETORIAN_HEALTH = PLAYER_HEALTH * 5;

    public static final double QUEEN_HEALTH = PLAYER_HEALTH * 10;

    public static final double WARRIOR_HEALTH = PLAYER_HEALTH * 3;

    public static final double YAUTJA_HEALTH = PLAYER_HEALTH * 10;

    private HealthConstants() {
        throw new UnsupportedOperationException();
    }
}
