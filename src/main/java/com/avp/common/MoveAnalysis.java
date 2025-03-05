package com.avp.common;

import net.minecraft.world.entity.Entity;
import org.joml.Vector3d;

public class MoveAnalysis {

    private final Entity entity;

    private final Vector3d deltaPosition;

    private final Vector3d lastPosition;

    private int lastTick;

    public MoveAnalysis(Entity entity) {
        this.entity = entity;
        this.lastPosition = new Vector3d(entity.position().toVector3f());
        this.deltaPosition = new Vector3d();
    }

    public void tick() {
        if (entity.tickCount == lastTick) {
            // Only update on tick differences.
            return;
        }

        var prevPos = lastPosition;
        var prevPosX = prevPos.x;
        var prevPosY = prevPos.y;
        var prevPosZ = prevPos.z;

        var pos = entity.position();
        var posX = pos.x;
        var posY = pos.y;
        var posZ = pos.z;

        deltaPosition.set(posX - prevPosX, posY - prevPosY, posZ - prevPosZ);
        lastPosition.set(pos.x, pos.y, pos.z);

        this.lastTick = entity.tickCount;
    }

    public boolean isMovingHorizontally() {
        return deltaPosition.x != 0 || deltaPosition.z != 0;
    }

    public boolean isMovingVertically() {
        return deltaPosition.y != 0;
    }

    public boolean isMoving() {
        return isMovingHorizontally() || isMovingVertically();
    }
}
