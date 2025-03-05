package com.avp.core.common.entity.type;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public interface SilencedEntityTypeBuilder {

    <T extends Entity> EntityType<T> buildWithoutDataFixerCheck();
}
