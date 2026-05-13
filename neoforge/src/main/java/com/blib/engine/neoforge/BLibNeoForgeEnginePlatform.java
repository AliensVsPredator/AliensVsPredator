package com.blib.engine.neoforge;

import net.minecraft.client.KeyMapping;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.platform.EnginePlatform;
import com.blib.engine.platform.HudRenderer;

/**
 * NeoForge adapter for {@link EnginePlatform}. KeyMapping registration runs on the mod event bus (NeoForge's required
 * pattern), so this platform takes that bus in its constructor; the other hooks subscribe to the global event bus.
 */
@ApiStatus.Internal
public final class BLibNeoForgeEnginePlatform implements EnginePlatform {

    private final IEventBus modEventBus;

    public BLibNeoForgeEnginePlatform(IEventBus modEventBus) {
        this.modEventBus = modEventBus;
    }

    @Override
    public void registerClientTickEnd(Runnable handler) {
        NeoForge.EVENT_BUS.<ClientTickEvent.Post>addListener(event -> handler.run());
    }

    @Override
    public void registerKeyBinding(KeyMapping keyMapping) {
        modEventBus.<RegisterKeyMappingsEvent>addListener(event -> event.register(keyMapping));
    }

    @Override
    public void registerHudRenderer(HudRenderer renderer) {
        NeoForge.EVENT_BUS.<RenderGuiEvent.Post>addListener(
            event -> renderer.render(event.getGuiGraphics(), event.getPartialTick().getRealtimeDeltaTicks())
        );
    }
}
