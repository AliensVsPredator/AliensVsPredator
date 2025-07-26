package com.alien.common.util;

import java.util.List;

public class AzAlienAnimationUtil {

    public static final String BODY_CONTROLLER_NAME = "body";

    public static final String HEAD_CONTROLLER_NAME = "head";

    public static final String LEFT_ARM_CONTROLLER_NAME = "leftarm";

    public static final String LEFT_LEG_CONTROLLER_NAME = "leftleg";

    public static final String LEFT_TITTY_ARM_CONTROLLER_NAME = "lefttittyarm";

    public static final String RIGHT_ARM_CONTROLLER_NAME = "rightarm";

    public static final String RIGHT_LEG_CONTROLLER_NAME = "rightleg";

    public static final String RIGHT_TITTY_ARM_CONTROLLER_NAME = "righttittyarm";

    public static final String TAIL_CONTROLLER_NAME = "tail";

    public static final List<String> XENO_LIMB_NAMES = List.of(
        BODY_CONTROLLER_NAME,
        HEAD_CONTROLLER_NAME,
        LEFT_ARM_CONTROLLER_NAME,
        LEFT_LEG_CONTROLLER_NAME,
        RIGHT_ARM_CONTROLLER_NAME,
        RIGHT_LEG_CONTROLLER_NAME,
        TAIL_CONTROLLER_NAME
    );

    public static final List<String> XENO_QUEEN_LIMB_NAMES = List.of(
        BODY_CONTROLLER_NAME,
        HEAD_CONTROLLER_NAME,
        LEFT_ARM_CONTROLLER_NAME,
        LEFT_LEG_CONTROLLER_NAME,
        LEFT_TITTY_ARM_CONTROLLER_NAME,
        RIGHT_ARM_CONTROLLER_NAME,
        RIGHT_LEG_CONTROLLER_NAME,
        RIGHT_TITTY_ARM_CONTROLLER_NAME,
        TAIL_CONTROLLER_NAME
    );
}
