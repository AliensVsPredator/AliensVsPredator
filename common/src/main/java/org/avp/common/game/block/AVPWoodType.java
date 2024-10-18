package org.avp.common.game.block;

import net.minecraft.world.level.block.state.properties.WoodType;

public class AVPWoodType {

    public static final WoodType LARGE = new WoodType("large", AVPBlockSetType.LARGE);

    public static final WoodType MEDIUM = new WoodType("medium", AVPBlockSetType.MEDIUM);

    public static final WoodType SMALL = new WoodType("small", AVPBlockSetType.SMALL);

    private AVPWoodType() {
        throw new UnsupportedOperationException();
    }
}
