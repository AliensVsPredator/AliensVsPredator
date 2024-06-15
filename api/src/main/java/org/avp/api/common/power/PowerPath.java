package org.avp.api.common.power;

import net.minecraft.core.BlockPos;

public record PowerPath(
    BlockPos start,
    BlockPos end
) {

    public int getDistanceSquared() {
        var xComponent = (end.getX() - start.getX());
        var yComponent = (end.getY() - start.getY());
        var zComponent = (end.getZ() - start.getZ());
        return (xComponent * xComponent) + (yComponent * yComponent) + (zComponent * zComponent);
    }
}
