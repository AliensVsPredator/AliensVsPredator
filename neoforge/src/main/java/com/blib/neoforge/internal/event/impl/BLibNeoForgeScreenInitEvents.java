package com.blib.neoforge.internal.event.impl;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

import com.blib.api.BLibAPI;
import com.blib.api.client.event.v1.BLibScreenInitContext;
import com.blib.api.client.event.v1.BLibScreenInitEvent;
import com.blib.api.common.mod.v1.BLibMod;
import com.blib.api.common.mod.v1.model.DistributionEnvironmentType;
import com.blib.neoforge.event.BLibNeoForgeEventHandle;

/**
 * NeoForge bridge for {@link BLibScreenInitEvent}: routes {@code ScreenEvent.Init.Post} into the BLib event dispatcher
 * via the standard deferred-init pattern used by other NeoForge event handles. Dedicated-server builds skip the bus
 * registration entirely.
 */
@ApiStatus.Internal
public final class BLibNeoForgeScreenInitEvents {

    public static final Function<BLibMod, BLibNeoForgeEventHandle<BLibScreenInitEvent>> POST_FACTORY = mod -> new BLibNeoForgeEventHandle<>(
        mod
    ) {

        private final BLibScreenInitEvent dispatcher = ctx -> {
            // NeoForge fires ScreenEvent.Init.Post itself; we don't synthesize one here.
        };

        @Override
        public BLibScreenInitEvent dispatcher() {
            return dispatcher;
        }

        @Override
        public void initialize() {
            if (BLibAPI.getDistributionType() != DistributionEnvironmentType.CLIENT) {
                return;
            }
            NeoForge.EVENT_BUS.<ScreenEvent.Init.Post>addListener(event -> {
                var ctx = makeContext(event);
                listeners.forEach(listener -> listener.invoke(ctx));
            });
        }
    };

    private BLibNeoForgeScreenInitEvents() {}

    private static BLibScreenInitContext makeContext(ScreenEvent.Init.Post event) {
        return new BLibScreenInitContext() {

            @Override
            public Screen screen() {
                return event.getScreen();
            }

            @Override
            public <W extends AbstractWidget> W addWidget(W widget) {
                event.addListener(widget);
                return widget;
            }
        };
    }
}
