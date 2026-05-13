package com.blib.engine.platform;

import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.ApiStatus;

/**
 * Loader-agnostic HUD render hook. Adapts to Fabric's {@code HudRenderCallback} and NeoForge's {@code RenderGuiEvent} —
 * both deliver a {@link GuiGraphics} plus a partial-tick value; the engine's renderers only care about those two
 * inputs.
 */
@ApiStatus.Internal
@FunctionalInterface
public interface HudRenderer {

    void render(GuiGraphics graphics, float partialTick);
}
