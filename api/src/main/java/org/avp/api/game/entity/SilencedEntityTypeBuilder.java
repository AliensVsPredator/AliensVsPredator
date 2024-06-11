package org.avp.api.game.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public interface SilencedEntityTypeBuilder {

    <T extends Entity> EntityType<T> buildWithoutDataFixerCheck();
}
