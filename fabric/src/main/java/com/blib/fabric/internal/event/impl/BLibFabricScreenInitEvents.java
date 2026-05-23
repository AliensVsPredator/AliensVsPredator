package com.blib.fabric.internal.event.impl;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

import com.blib.api.BLibAPI;
import com.blib.api.client.event.v1.BLibScreenInitContext;
import com.blib.api.client.event.v1.BLibScreenInitEvent;
import com.blib.api.common.event.v1.handle.BLibEventHandle;
import com.blib.api.common.mod.v1.BLibMod;
import com.blib.api.common.mod.v1.model.DistributionEnvironmentType;

/**
 * Fabric bridge for {@link BLibScreenInitEvent}: routes {@code ScreenEvents.AFTER_INIT} into the BLib event dispatcher.
 * Dedicated-server builds skip the registration entirely (no screens to fire against).
 */
@ApiStatus.Internal
public final class BLibFabricScreenInitEvents {

    public static final Function<BLibMod, BLibEventHandle<BLibScreenInitEvent>> POST_FACTORY = mod -> new BLibEventHandle<>(mod) {

        private final BLibScreenInitEvent dispatcher = ctx -> {
            // Fabric fires ScreenEvents.AFTER_INIT itself — there's no need (or way) to synthesize one. The dispatcher
            // is only meaningful for events that BLib mods might want to fire themselves; for screen init that's
            // not a supported use case.
        };

        @Override
        public BLibScreenInitEvent dispatcher() {
            return dispatcher;
        }

        @Override
        public void onRegister(BLibScreenInitEvent event) {
            if (BLibAPI.getDistributionType() != DistributionEnvironmentType.CLIENT) {
                return;
            }
            ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
                event.invoke(makeContext(screen));
            });
        }
    };

    private BLibFabricScreenInitEvents() {}

    private static BLibScreenInitContext makeContext(Screen screen) {
        return new BLibScreenInitContext() {

            @Override
            public Screen screen() {
                return screen;
            }

            @Override
            public <W extends AbstractWidget> W addWidget(W widget) {
                Screens.getButtons(screen).add(widget);
                return widget;
            }
        };
    }
}
