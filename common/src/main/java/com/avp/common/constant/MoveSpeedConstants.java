package com.avp.common.constant;

public record MoveSpeedConstants() {

    // Basic measurements
    public static final float PLAYER_SPEED_MULTIPLIER = 10 * 0.315000001192092896F;

    public static final float PLAYER_WALK_SPEED = 0.1F * PLAYER_SPEED_MULTIPLIER;

    public static final float PLAYER_SPRINT_SPEED = PLAYER_WALK_SPEED * 1.15F;

    public static final float PLAYER_SPRINT_JUMP_SPEED = PLAYER_SPRINT_SPEED * 1.15F;

    // Alien measurements
    public static final float CHESTBURSTER_SPEED = PLAYER_WALK_SPEED * 1.05F;

    public static final float CRUSHER_SPEED = PLAYER_WALK_SPEED * 1.2F;

    public static final float DRONE_SPEED = PLAYER_WALK_SPEED * 1F;

    public static final float FACEHUGGER_SPEED = PLAYER_WALK_SPEED * 1.1F;

    public static final float PRAETORIAN_SPEED = PLAYER_WALK_SPEED * 1.2F;

    public static final float PROWLER_SPEED = PLAYER_WALK_SPEED * 1.1F;

    public static final float QUEEN_SPEED = PLAYER_WALK_SPEED * 0.9F;

    public static final float RUNNER_SPEED = PLAYER_WALK_SPEED * 1F;

    public static final float WARRIOR_SPEED = PLAYER_WALK_SPEED * 1.1F;

    public static final float YAUTJA_SPEED = PLAYER_WALK_SPEED * 1.2F;
}
