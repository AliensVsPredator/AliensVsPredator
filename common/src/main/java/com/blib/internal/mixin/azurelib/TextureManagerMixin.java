package com.blib.internal.mixin.azurelib;

import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.blib.api.client.texture.v1.AnimatableTexture;

@Mixin(value = TextureManager.class, priority = 2010)
public abstract class TextureManagerMixin {

    @Shadow
    public abstract void register(ResourceLocation resourceLocation, AbstractTexture abstractTexture);

    @Inject(
        method = "getTexture(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/texture/AbstractTexture;",
        at = @At("RETURN"),
        cancellable = true,
        require = 0
    )
    private void blib$replaceAnimatableTexture(
        ResourceLocation location,
        CallbackInfoReturnable<AbstractTexture> cir
    ) {
        AbstractTexture currentTexture = cir.getReturnValue();

        if (currentTexture == null || currentTexture.getClass() != SimpleTexture.class) {
            return;
        }

        AnimatableTexture animatableTexture = new AnimatableTexture(location);

        if (!animatableTexture.isAnimated()) {
            return;
        }

        this.register(location, animatableTexture);
        cir.setReturnValue(animatableTexture);
    }
}
