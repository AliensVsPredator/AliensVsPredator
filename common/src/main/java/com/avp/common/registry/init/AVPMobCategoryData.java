package com.avp.common.registry.init;

public class AVPMobCategoryData {

    public static final Data ALIEN = new Data("avp:alien", 75, false, false, 128);

    public static final Data OVOMORPH = new Data("avp:ovomorph", 60, false, false, 128);

    public static final Data PREDATOR = new Data("avp:predator", 75, false, false, 128);

    public record Data(
        String name,
        int max,
        boolean isFriendly,
        boolean isPersistent,
        int despawnDistance
    ) {}
}
