package org.avp.api.entity;

import net.minecraft.world.entity.EntityType;

public interface Morphable {
    EntityType<?> getEntityTypeForPreviousForm();
}
