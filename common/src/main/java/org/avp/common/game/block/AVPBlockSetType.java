package org.avp.common.game.block;

import net.minecraft.world.level.block.state.properties.BlockSetType;

public class AVPBlockSetType {

    public static final BlockSetType LARGE = new BlockSetType("large");

    public static final BlockSetType MEDIUM = new BlockSetType("medium");

    public static final BlockSetType SMALL = new BlockSetType("small");

    private AVPBlockSetType() {
        throw new UnsupportedOperationException();
    }
}
