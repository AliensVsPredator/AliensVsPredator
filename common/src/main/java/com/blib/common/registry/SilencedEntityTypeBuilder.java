package com.blib.common.registry;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public interface SilencedEntityTypeBuilder {

    <T extends Entity> EntityType<T> blib$buildWithoutDataFixerCheck();
}
