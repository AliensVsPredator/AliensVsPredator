package com.blib.internal.mixin;

import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

/**
 * Exposes {@link EntityRenderDispatcher#renderers} so we can look up a renderer by {@link EntityType} without an entity
 * instance. Used by the dismemberment system to fetch the source mob's model when rendering a vanilla limb fragment.
 */
@Mixin(EntityRenderDispatcher.class)
public interface MixinEntityRenderDispatcher_Accessor {

    @Accessor(value = "renderers")
    Map<EntityType<?>, EntityRenderer<?>> blib$getRenderers();
}
