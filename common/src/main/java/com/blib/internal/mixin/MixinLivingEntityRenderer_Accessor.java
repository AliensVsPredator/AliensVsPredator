package com.blib.internal.mixin;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

/**
 * Exposes the protected {@code model} and {@code layers} fields on {@link LivingEntityRenderer} so the dismemberment
 * system can borrow the vanilla mob's {@code EntityModel} when rendering a limb fragment, and find specific layers
 * (e.g. armor) to re-run at the limb's pose.
 */
@Mixin(LivingEntityRenderer.class)
public interface MixinLivingEntityRenderer_Accessor {

    @Accessor(value = "model")
    EntityModel<?> blib$getModel();

    @Accessor(value = "layers")
    List<RenderLayer<?, ?>> blib$getLayers();
}
