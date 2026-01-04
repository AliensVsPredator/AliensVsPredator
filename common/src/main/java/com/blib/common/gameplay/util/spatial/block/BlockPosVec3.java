package com.blib.common.gameplay.util.spatial.block;

import net.minecraft.core.BlockPos;

import com.blib.common.gameplay.util.spatial.Vec3Like;

public record BlockPosVec3(BlockPos pos) implements Vec3Like {

    @Override
    public double x() {
        return pos.getX() + 0.5;
    }

    @Override
    public double y() {
        return pos.getY() + 0.5;
    }

    @Override
    public double z() {
        return pos.getZ() + 0.5;
    }
}
