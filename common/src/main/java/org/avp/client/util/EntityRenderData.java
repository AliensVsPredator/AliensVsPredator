package org.avp.client.util;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import org.avp.api.GameObject;

public record EntityRenderData<T extends Entity>(
    GameObject<EntityType<T>> entityTypeGameObject,
    EntityRendererProvider<T> entityRendererProvider
) {}
