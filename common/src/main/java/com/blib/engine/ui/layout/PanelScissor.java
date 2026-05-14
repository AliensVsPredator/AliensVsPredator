package com.blib.engine.ui.layout;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector3f;

/**
 * Raw GL scissor helper for docked engine panels. The workspace renders through a scaled pose stack, so vanilla
 * {@link GuiGraphics#enableScissor} can clip in the wrong coordinate space for nested panel content.
 */
@ApiStatus.Internal
public final class PanelScissor {

    private PanelScissor() {}

    public static void enable(GuiGraphics graphics, UiRect rect) {
        enable(graphics, rect.x(), rect.y(), rect.width(), rect.height());
    }

    public static void enable(GuiGraphics graphics, int x, int y, int width, int height) {
        if (width <= 0 || height <= 0) {
            RenderSystem.disableScissor();
            return;
        }
        graphics.flush();

        var matrix = graphics.pose().last().pose();
        var topLeft = matrix.transformPosition((float) x, (float) y, 0f, new Vector3f());
        var bottomRight = matrix.transformPosition((float) (x + width), (float) (y + height), 0f, new Vector3f());

        var window = Minecraft.getInstance().getWindow();
        var winHeight = window.getHeight();
        var guiScale = window.getGuiScale();
        var leftRaw = (int) ((double) topLeft.x * guiScale);
        var bottomRaw = (int) ((double) winHeight - (double) bottomRight.y * guiScale);
        var widthRaw = Math.max(0, (int) ((double) (bottomRight.x - topLeft.x) * guiScale));
        var heightRaw = Math.max(0, (int) ((double) (bottomRight.y - topLeft.y) * guiScale));
        RenderSystem.enableScissor(leftRaw, bottomRaw, widthRaw, heightRaw);
    }

    public static void disable(GuiGraphics graphics) {
        graphics.flush();
        RenderSystem.disableScissor();
    }
}
