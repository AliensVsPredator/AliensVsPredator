package org.avp.client.util;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import org.avp.api.Holder;

public record EntityRenderData<T extends Entity>(
    Holder<EntityType<T>> entityTypeHolder,
    EntityRendererProvider<T> entityRendererProvider
) {}
