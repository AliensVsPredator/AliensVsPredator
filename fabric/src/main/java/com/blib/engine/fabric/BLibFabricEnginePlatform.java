package com.blib.engine.fabric;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.KeyMapping;
import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.platform.EnginePlatform;
import com.blib.engine.platform.HudRenderer;

/**
 * Fabric adapter for {@link EnginePlatform}. Translates each generic registration call into the Fabric loader's actual
 * event-bus mechanics so the engine's common-side bootstrap doesn't have to know about
 * {@code ClientTickEvents}/{@code KeyBindingHelper}/{@code HudRenderCallback}.
 */
@ApiStatus.Internal
public final class BLibFabricEnginePlatform implements EnginePlatform {

    @Override
    public void registerClientTickEnd(Runnable handler) {
        ClientTickEvents.END_CLIENT_TICK.register(client -> handler.run());
    }

    @Override
    public void registerKeyBinding(KeyMapping keyMapping) {
        KeyBindingHelper.registerKeyBinding(keyMapping);
    }

    @Override
    public void registerHudRenderer(HudRenderer renderer) {
        HudRenderCallback.EVENT.register(
            (guiGraphics, deltaTracker) -> renderer.render(guiGraphics, deltaTracker.getRealtimeDeltaTicks())
        );
    }
}
