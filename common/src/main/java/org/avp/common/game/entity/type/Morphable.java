package org.avp.common.game.entity.type;

import net.minecraft.world.entity.EntityType;

public interface Morphable {
    EntityType<?> getEntityTypeForPreviousForm();
}
