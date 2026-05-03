package com.blib.internal.mixin.azurelib;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

import com.blib.api.client.texture.v1.AnimatableTexture;

@Mixin(value = TextureManager.class, priority = 2015)
public abstract class TextureManagerMixin {

    @Unique
    private final Map<ResourceLocation, Boolean> blit$animationCache = new HashMap<>();

    @Unique
    private final Map<ResourceLocation, AnimatableTexture> blit$textureCache = new HashMap<>();

    @Shadow
    public abstract void register(ResourceLocation resourceLocation, AbstractTexture abstractTexture);

    @Shadow
    protected abstract AbstractTexture loadTexture(ResourceLocation path, AbstractTexture texture);

    @Inject(
        method = "getTexture(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/texture/AbstractTexture;",
        at = @At("RETURN"),
        cancellable = true,
        require = 0
    )
    private void blit$replaceAnimatableTexture(
        ResourceLocation location,
        CallbackInfoReturnable<AbstractTexture> cir
    ) {
        var currentTexture = cir.getReturnValue();

        if (currentTexture == null || currentTexture.getClass() != SimpleTexture.class) {
            return;
        }

        if (blit$textureCache.containsKey(location)) {
            cir.setReturnValue(blit$textureCache.get(location));
            return;
        }

        var cached = blit$animationCache.get(location);
        if (cached != null && !cached) {
            return;
        }

        if (!blit$hasAnimationMetadata(location)) {
            blit$animationCache.put(location, false);
            return;
        }

        var animatableTexture = new AnimatableTexture(location);

        try {
            loadTexture(location, animatableTexture);
        } catch (Exception e) {
            blit$animationCache.put(location, false);
            return;
        }

        if (!animatableTexture.isAnimated()) {
            blit$animationCache.put(location, false);
            return;
        }

        blit$animationCache.put(location, true);
        blit$textureCache.put(location, animatableTexture);

        this.register(location, animatableTexture);
        cir.setReturnValue(animatableTexture);
    }

    @Unique
    private boolean blit$hasAnimationMetadata(ResourceLocation texture) {
        var mcmeta = ResourceLocation.fromNamespaceAndPath(
            texture.getNamespace(),
            texture.getPath() + ".mcmeta"
        );

        try {
            return Minecraft.getInstance()
                .getResourceManager()
                .getResource(mcmeta)
                .isPresent();
        } catch (Exception e) {
            return false;
        }
    }
}
