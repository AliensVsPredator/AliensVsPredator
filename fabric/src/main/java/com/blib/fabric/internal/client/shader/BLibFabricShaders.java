package com.blib.fabric.internal.client.shader;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import org.jetbrains.annotations.ApiStatus;

import com.blib.internal.client.posteffect.BLibPostEffectRegistry;
import com.blib.internal.client.shader.BLibShaders;

/**
 * Fabric-side glue that registers every {@link BLibShaders#ALL} entry with MC's resource-loaded shader system, plus
 * every {@link BLibPostEffectRegistry#ALL} entry. Fired during the resource-load lifecycle whenever the client
 * (re)loads packs.
 */
@ApiStatus.Internal
public final class BLibFabricShaders {

    public static void register() {
        CoreShaderRegistrationCallback.EVENT.register(context -> {
            for (var shader : BLibShaders.ALL) {
                context.register(shader.id(), shader.vertexFormat(), shader::setInstance);
            }

            for (var effect : BLibPostEffectRegistry.ALL) {
                context.register(
                    effect.spec().fragmentShader(),
                    DefaultVertexFormat.BLIT_SCREEN,
                    effect::setShaderInstance
                );
            }
        });
    }

    private BLibFabricShaders() {
        throw new UnsupportedOperationException();
    }
}
