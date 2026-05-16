package com.blib.engine.ui.layout;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector3f;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Raw GL scissor helper for docked engine panels. The workspace renders through a scaled pose stack, so vanilla
 * {@link GuiGraphics#enableScissor} can clip in the wrong coordinate space for nested panel content.
 */
@ApiStatus.Internal
public final class PanelScissor {

    private static final Deque<RawScissor> STACK = new ArrayDeque<>();

    private PanelScissor() {}

    public static void enable(GuiGraphics graphics, UiRect rect) {
        enable(graphics, rect.x(), rect.y(), rect.width(), rect.height());
    }

    public static void enable(GuiGraphics graphics, int x, int y, int width, int height) {
        graphics.flush();

        var raw = computeRaw(graphics, x, y, width, height);
        var clipped = STACK.isEmpty() ? raw : raw.intersect(STACK.peek());
        STACK.push(clipped);
        apply(clipped);
    }

    public static void disable(GuiGraphics graphics) {
        graphics.flush();
        if (STACK.isEmpty()) {
            RenderSystem.disableScissor();
            return;
        }
        STACK.pop();
        if (STACK.isEmpty()) {
            RenderSystem.disableScissor();
            return;
        }
        apply(STACK.peek());
    }

    private static RawScissor computeRaw(GuiGraphics graphics, int x, int y, int width, int height) {
        if (width <= 0 || height <= 0) {
            return new RawScissor(0, 0, 0, 0);
        }
        var matrix = graphics.pose().last().pose();
        var topLeft = matrix.transformPosition((float) x, (float) y, 0f, new Vector3f());
        var bottomRight = matrix.transformPosition((float) (x + width), (float) (y + height), 0f, new Vector3f());

        var window = Minecraft.getInstance().getWindow();
        var winWidth = window.getWidth();
        var winHeight = window.getHeight();
        var guiScale = window.getGuiScale();

        var minX = Math.min(topLeft.x, bottomRight.x) * guiScale;
        var maxX = Math.max(topLeft.x, bottomRight.x) * guiScale;
        var minY = Math.min(topLeft.y, bottomRight.y) * guiScale;
        var maxY = Math.max(topLeft.y, bottomRight.y) * guiScale;

        var leftRaw = Math.max(0, (int) Math.floor(minX));
        var rightRaw = Math.min(winWidth, (int) Math.ceil(maxX));
        var bottomRaw = Math.max(0, (int) Math.floor((double) winHeight - maxY));
        var topRaw = Math.min(winHeight, (int) Math.ceil((double) winHeight - minY));
        var widthRaw = Math.max(0, rightRaw - leftRaw);
        var heightRaw = Math.max(0, topRaw - bottomRaw);
        return new RawScissor(leftRaw, bottomRaw, widthRaw, heightRaw);
    }

    private static void apply(RawScissor scissor) {
        RenderSystem.enableScissor(scissor.left(), scissor.bottom(), scissor.width(), scissor.height());
    }

    private record RawScissor(
        int left,
        int bottom,
        int width,
        int height
    ) {

        RawScissor intersect(RawScissor other) {
            var right = Math.min(left + width, other.left + other.width);
            var top = Math.min(bottom + height, other.bottom + other.height);
            var nextLeft = Math.max(left, other.left);
            var nextBottom = Math.max(bottom, other.bottom);
            return new RawScissor(nextLeft, nextBottom, Math.max(0, right - nextLeft), Math.max(0, top - nextBottom));
        }
    }
}
