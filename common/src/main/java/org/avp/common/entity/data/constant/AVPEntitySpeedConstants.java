package org.avp.common.entity.data.constant;

public class AVPEntitySpeedConstants {

    // Basic measurements
    public static final double PLAYER_WALK_SPEED = 0.31000001192092896;
    public static final double PLAYER_SPRINT_SPEED = PLAYER_WALK_SPEED * 1.3;
    public static final double PLAYER_SPRINT_JUMP_SPEED = PLAYER_WALK_SPEED * 1.65;

    public static final double BASE_SPEED = PLAYER_SPRINT_SPEED * 0.9;

    public static final double BELUGAMORPH_SPEED = PLAYER_SPRINT_SPEED;
    public static final double CHESTBURSTER_SPEED = PLAYER_SPRINT_SPEED * 1.05;
    public static final double CRUSHER_SPEED = PLAYER_SPRINT_SPEED * 1.15;
    public static final double DEACON_ADULT_ENGINEER_SPEED = PLAYER_SPRINT_SPEED * 1.2;
    public static final double DEACON_ADULT_SPEED = PLAYER_SPRINT_SPEED * 1.1;
    public static final double DRACOMORPH_SPEED = PLAYER_SPRINT_SPEED * 0.95;
    public static final double DRONE_SPEED = PLAYER_SPRINT_SPEED * 0.9;
    public static final double DRONE_RUNNER_SPEED = PLAYER_SPRINT_JUMP_SPEED * 1.05;
    public static final double ENGINEER_SPEED = PLAYER_SPRINT_JUMP_SPEED;
    public static final double FACEHUGGER_SPEED = PLAYER_SPRINT_SPEED * 1.1;
    public static final double FACEHUGGER_ROYAL_SPEED = PLAYER_SPRINT_SPEED * 1.2;
    public static final double OCTOHUGGER_SPEED = PLAYER_WALK_SPEED * 0.95;
    public static final double PRAETORIAN_SPEED = PLAYER_SPRINT_SPEED * 0.95;
    public static final double QUEEN_SPEED = PLAYER_SPRINT_SPEED * 0.9;
    public static final double TRILOBITE_SPEED = PLAYER_SPRINT_SPEED * 1.05;
    public static final double TRILOBITE_BABY_SPEED = PLAYER_WALK_SPEED * 0.95;
    public static final double ULTRAMORPH_SPEED = PLAYER_SPRINT_SPEED * 1.15;
    public static final double WARRIOR_SPEED = PLAYER_SPRINT_SPEED;
    public static final double WARRIOR_RUNNER_SPEED = PLAYER_SPRINT_JUMP_SPEED * 1.1;
    public static final double YAUTJA_SPEED = PLAYER_SPRINT_JUMP_SPEED;

    private AVPEntitySpeedConstants() {
        throw new UnsupportedOperationException();
    }
}
