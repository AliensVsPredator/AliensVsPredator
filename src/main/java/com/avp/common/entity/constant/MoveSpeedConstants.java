package com.avp.common.entity.constant;

public class MoveSpeedConstants {

    // Basic measurements
    public static final double PLAYER_SPEED_MULTIPLIER = 10 * 0.315000001192092896;

    public static final double PLAYER_WALK_SPEED = 0.1 * PLAYER_SPEED_MULTIPLIER;

    public static final double PLAYER_SPRINT_SPEED = PLAYER_WALK_SPEED * 1.15;

    public static final double PLAYER_SPRINT_JUMP_SPEED = PLAYER_SPRINT_SPEED * 1.15;

    // Alien measurements
    public static final double CHESTBURSTER_SPEED = PLAYER_WALK_SPEED * 1.05;

    public static final double DRONE_SPEED = PLAYER_WALK_SPEED * 1;

    public static final double FACEHUGGER_SPEED = PLAYER_WALK_SPEED * 1.1;

    public static final double PRAETORIAN_SPEED = PLAYER_WALK_SPEED * 1.2;

    public static final double QUEEN_SPEED = PLAYER_WALK_SPEED * 0.9;

    public static final double WARRIOR_SPEED = PLAYER_WALK_SPEED * 1.1;

    public static final double YAUTJA_SPEED = PLAYER_WALK_SPEED * 1.2;

    private MoveSpeedConstants() {
        throw new UnsupportedOperationException();
    }
}
