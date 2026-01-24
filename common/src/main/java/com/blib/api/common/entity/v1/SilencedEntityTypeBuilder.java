package com.blib.api.common.entity.v1;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public interface SilencedEntityTypeBuilder {

    <T extends Entity> EntityType<T> blib$buildWithoutDataFixerCheck();
}
