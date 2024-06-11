package org.avp.api.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;

public class GravityUtils {

    public static void apply(Entity entity) {
        entity.setDeltaMovement(0, entity.getDeltaMovement().y - 0.03999999910593033D, 0);
        entity.move(MoverType.SELF, entity.getDeltaMovement());
        entity.setDeltaMovement(0, entity.getDeltaMovement().y * 0.9800000190734863D, 0);
    }

    private GravityUtils() {
        throw new UnsupportedOperationException();
    }
}
