package org.avp.api.entity.data;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import org.avp.api.Holder;

import java.util.List;
import java.util.Optional;

public abstract class EntityData<T extends Entity> {
    public abstract Holder<EntityType<T>> getHolder();
    public abstract Optional<AttributeSupplier> getAttributeSupplier();
    public abstract List<TagKey<EntityType<?>>> getTags();
}
