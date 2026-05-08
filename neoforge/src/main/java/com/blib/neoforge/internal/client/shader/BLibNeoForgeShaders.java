package com.blib.neoforge.internal.client.shader;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;

import com.blib.internal.client.posteffect.BLibPostEffectRegistry;
import com.blib.internal.client.shader.BLibShaders;

/**
 * NeoForge-side glue that registers every {@link BLibShaders#ALL} entry plus every {@link BLibPostEffectRegistry#ALL}
 * entry against {@link RegisterShadersEvent}. Fired on the mod event bus during the resource-load lifecycle.
 */
@ApiStatus.Internal
public final class BLibNeoForgeShaders {

    public static void register(IEventBus modEventBus) {
        modEventBus.<RegisterShadersEvent>addListener(event -> {
            var resources = event.getResourceProvider();

            for (var shader : BLibShaders.ALL) {
                try {
                    event.registerShader(
                        new ShaderInstance(resources, shader.id().toString(), shader.vertexFormat()),
                        shader::setInstance
                    );
                } catch (IOException e) {
                    throw new RuntimeException("Failed to load BLib shader " + shader.id(), e);
                }
            }

            for (var effect : BLibPostEffectRegistry.ALL) {
                try {
                    event.registerShader(
                        new ShaderInstance(resources, effect.spec().fragmentShader().toString(), DefaultVertexFormat.BLIT_SCREEN),
                        effect::setShaderInstance
                    );
                } catch (IOException e) {
                    throw new RuntimeException("Failed to load BLib post-effect " + effect.id(), e);
                }
            }
        });
    }

    private BLibNeoForgeShaders() {
        throw new UnsupportedOperationException();
    }
}
