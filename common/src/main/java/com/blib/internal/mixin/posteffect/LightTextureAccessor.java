package com.blib.internal.mixin.posteffect;

import net.minecraft.client.renderer.LightTexture;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Exposes {@link LightTexture}'s private {@code lightTextureLocation} field so the post-effect pipeline can route the
 * lightmap as a sampler into effect shaders that request {@code BLibPostEffectInput.LIGHTMAP_TEXTURE}.
 */
@Mixin(LightTexture.class)
public interface LightTextureAccessor {

    @Accessor("lightTextureLocation")
    ResourceLocation blib$getLightTextureLocation();
}
