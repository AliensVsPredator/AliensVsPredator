package com.blib.engine.ui.panel.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayDeque;
import java.util.Objects;

import com.blib.engine.modeler.ModelerScene;
import com.blib.engine.modeler.history.ModelerAction;
import com.blib.engine.modeler.history.ModelerActionHistory;
import com.blib.engine.modeler.texture.LoadedTexture;
import com.blib.engine.texture.TextureEditorState;
import com.blib.engine.texture.TextureTool;
import com.blib.engine.ui.EngineFont;
import com.blib.engine.ui.dock.Panel;

@ApiStatus.Internal
public final class TextureViewportPanel implements Panel {

    private static final int BG_COLOR = 0xFF101014;

    private static final int HEADER_BG_COLOR = 0xFF18181C;

    private static final int HEADER_TEXT_COLOR = 0xFFD8D8E0;

    private static final int MUTED_TEXT_COLOR = 0xFF808088;

    private static final int CHECKER_DARK = 0xFF7E7E86;

    private static final int CHECKER_LIGHT = 0xFFAFAFB8;

    private static final int IMAGE_BORDER_COLOR = 0xFFE0E0E8;

    private static final int GRID_COLOR = 0x40202024;

    private static final int SELECTION_FILL_COLOR = 0x304F8FFF;

    private static final int SELECTION_OUTLINE_COLOR = 0xFF4F8FFF;

    private static final int HEADER_HEIGHT = 16;

    private static final int VIEW_PADDING = 24;

    private static final double MAX_ZOOM = 48.0;

    private int panelX, panelY, panelWidth, panelHeight;

    private int canvasX, canvasY, canvasW, canvasH;

    private double zoom = 1.0;

    private double panX;

    private double panY;

    private @Nullable LoadedTexture viewedTexture;

    private int viewedWidth;

    private int viewedHeight;

    private DragMode dragMode = DragMode.NONE;

    private double lastMouseX;

    private double lastMouseY;

    private int selectStartX;

    private int selectStartY;

    private int lastPaintX;

    private int lastPaintY;

    private @Nullable LoadedTexture paintTexture;

    private @Nullable ModelerAction.TexturePixelsMemento paintBefore;

    private @Nullable TextureEditorState.Selection selectionBefore;

    private enum DragMode {
        NONE,
        PAN,
        SELECT,
        PAINT
    }

    @Override
    public String title() {
        return "Texture Viewport";
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
        panelX = x;
        panelY = y;
        panelWidth = width;
        panelHeight = height;
        canvasX = x;
        canvasY = y + HEADER_HEIGHT;
        canvasW = width;
        canvasH = Math.max(0, height - HEADER_HEIGHT);

        graphics.fill(x, y, x + width, y + height, BG_COLOR);
        renderHeader(graphics, x, y, width);

        var active = ModelerScene.get().activeTexture;
        var pixels = active == null ? null : active.texture().getPixels();
        if (active == null || pixels == null) {
            viewedTexture = active;
            TextureEditorState.clearSelection();
            renderEmpty(graphics, x, y, width, height, active == null ? "No texture selected" : "Texture pixels unavailable");
            return;
        }

        ensureView(active, pixels);
        renderCanvas(graphics, pixels);
    }

    @Override
    public boolean mouseClickedCapture(double mouseX, double mouseY, int button) {
        return beginInteraction(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return beginInteraction(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        var active = ModelerScene.get().activeTexture;
        var pixels = active == null ? null : active.texture().getPixels();
        if (pixels == null) {
            dragMode = DragMode.NONE;
            return false;
        }

        switch (dragMode) {
            case PAN -> {
                panX += mouseX - lastMouseX;
                panY += mouseY - lastMouseY;
                lastMouseX = mouseX;
                lastMouseY = mouseY;
                return true;
            }
            case SELECT -> {
                var pixel = pixelAt(mouseX, mouseY, pixels, true);
                if (pixel != null) {
                    updateSelection(pixel.x(), pixel.y());
                }
                return true;
            }
            case PAINT -> {
                var pixel = pixelAt(mouseX, mouseY, pixels, false);
                if (pixel != null) {
                    paintLine(active, pixels, lastPaintX, lastPaintY, pixel.x(), pixel.y());
                    lastPaintX = pixel.x();
                    lastPaintY = pixel.y();
                }
                return true;
            }
            case NONE -> {
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (dragMode == DragMode.NONE) {
            return false;
        }
        if (dragMode == DragMode.SELECT) {
            pushSelectionAction(selectionBefore, TextureEditorState.selection());
            selectionBefore = null;
        } else if (dragMode == DragMode.PAINT) {
            pushPaintAction();
        }
        dragMode = DragMode.NONE;
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (!insideCanvas(mouseX, mouseY)) {
            return false;
        }
        var active = ModelerScene.get().activeTexture;
        var pixels = active == null ? null : active.texture().getPixels();
        if (pixels == null) {
            return false;
        }

        var imgX = (mouseX - imageX(pixels)) / zoom;
        var imgY = (mouseY - imageY(pixels)) / zoom;
        var nextZoom = clamp(zoom * Math.pow(1.18, scrollY), minZoom(pixels), MAX_ZOOM);
        if (nextZoom == zoom) {
            return true;
        }
        zoom = nextZoom;
        panX = mouseX - baseImageX(pixels) - imgX * zoom;
        panY = mouseY - baseImageY(pixels) - imgY * zoom;
        return true;
    }

    private boolean beginInteraction(double mouseX, double mouseY, int button) {
        if (!insideCanvas(mouseX, mouseY)) {
            return false;
        }
        var active = ModelerScene.get().activeTexture;
        var pixels = active == null ? null : active.texture().getPixels();
        if (pixels == null) {
            return true;
        }
        if (button == 1 || button == 2) {
            dragMode = DragMode.PAN;
            lastMouseX = mouseX;
            lastMouseY = mouseY;
            return true;
        }
        if (button != 0) {
            return false;
        }

        var tool = TextureEditorState.tool();
        if (tool == TextureTool.SELECT) {
            var pixel = pixelAt(mouseX, mouseY, pixels, false);
            if (pixel == null) {
                var before = TextureEditorState.selection();
                TextureEditorState.clearSelection();
                pushSelectionAction(before, null);
                return true;
            }
            dragMode = DragMode.SELECT;
            selectionBefore = TextureEditorState.selection();
            selectStartX = pixel.x();
            selectStartY = pixel.y();
            updateSelection(pixel.x(), pixel.y());
            return true;
        }
        var pixel = pixelAt(mouseX, mouseY, pixels, false);
        if (pixel == null) {
            return true;
        }
        if (tool == TextureTool.PENCIL) {
            dragMode = DragMode.PAINT;
            paintTexture = active;
            paintBefore = ModelerAction.TexturePixelsMemento.of(pixels);
            lastPaintX = pixel.x();
            lastPaintY = pixel.y();
            paintLine(active, pixels, pixel.x(), pixel.y(), pixel.x(), pixel.y());
            return true;
        }
        if (tool == TextureTool.BUCKET) {
            var before = ModelerAction.TexturePixelsMemento.of(pixels);
            if (bucketFill(active, pixels, pixel.x(), pixel.y())) {
                var after = ModelerAction.TexturePixelsMemento.of(pixels);
                pushTexturePixelsAction("texture_bucket", "Bucket Fill", active, before, after);
            }
            return true;
        }
        return false;
    }

    private void renderHeader(GuiGraphics graphics, int x, int y, int width) {
        var font = EngineFont.get();
        graphics.fill(x, y, x + width, y + HEADER_HEIGHT, HEADER_BG_COLOR);
        var active = ModelerScene.get().activeTexture;
        var label = active == null ? "No texture" : active.displayName();
        graphics.drawString(font, Component.literal(label), x + 5, y + 4, HEADER_TEXT_COLOR, false);

        var pixels = active == null ? null : active.texture().getPixels();
        if (pixels != null) {
            var detail = pixels.getWidth() + " x " + pixels.getHeight() + "  " + Math.round(zoom * 100.0) + "%";
            var detailW = font.width(detail);
            graphics.drawString(font, Component.literal(detail), x + width - detailW - 5, y + 4, MUTED_TEXT_COLOR, false);
        }
    }

    private void renderEmpty(GuiGraphics graphics, int x, int y, int width, int height, String message) {
        var font = EngineFont.get();
        var textW = font.width(message);
        graphics.drawString(
            font,
            Component.literal(message),
            x + (width - textW) / 2,
            y + (height - font.lineHeight) / 2,
            MUTED_TEXT_COLOR,
            false
        );
    }

    private void renderCanvas(GuiGraphics graphics, NativeImage pixels) {
        graphics.flush();
        applyRawScissor(graphics, canvasX, canvasY, canvasW, canvasH);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();

        var pose = graphics.pose().last().pose();
        var m00 = pose.m00();
        var m11 = pose.m11();
        var m30 = pose.m30();
        var m31 = pose.m31();

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        var buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        addQuad(buffer, m00, m11, m30, m31, canvasX, canvasY, canvasX + canvasW, canvasY + canvasH, BG_COLOR);
        addCheckerboard(buffer, m00, m11, m30, m31, pixels);
        BufferUploader.drawWithShader(buffer.buildOrThrow());

        renderTextureQuad(pixels, m00, m11, m30, m31);

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        var x0 = (int) Math.round(imageX(pixels));
        var y0 = (int) Math.round(imageY(pixels));
        var x1 = (int) Math.round(x0 + pixels.getWidth() * zoom);
        var y1 = (int) Math.round(y0 + pixels.getHeight() * zoom);
        addPixelGrid(buffer, m00, m11, m30, m31, pixels);
        addRectOutline(buffer, m00, m11, m30, m31, x0, y0, x1, y1, IMAGE_BORDER_COLOR);
        addSelection(buffer, m00, m11, m30, m31);
        BufferUploader.drawWithShader(buffer.buildOrThrow());

        RenderSystem.disableScissor();
    }

    private void renderTextureQuad(NativeImage pixels, float m00, float m11, float m30, float m31) {
        var active = ModelerScene.get().activeTexture;
        if (active == null) {
            return;
        }
        var x0 = (float) imageX(pixels);
        var y0 = (float) imageY(pixels);
        var x1 = (float) (x0 + pixels.getWidth() * zoom);
        var y1 = (float) (y0 + pixels.getHeight() * zoom);
        RenderSystem.setShaderTexture(0, active.textureId());
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        var texBuffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        texBuffer.addVertex(m00 * x0 + m30, m11 * y0 + m31, 0).setUv(0f, 0f).setColor(0xFFFFFFFF);
        texBuffer.addVertex(m00 * x0 + m30, m11 * y1 + m31, 0).setUv(0f, 1f).setColor(0xFFFFFFFF);
        texBuffer.addVertex(m00 * x1 + m30, m11 * y1 + m31, 0).setUv(1f, 1f).setColor(0xFFFFFFFF);
        texBuffer.addVertex(m00 * x1 + m30, m11 * y0 + m31, 0).setUv(1f, 0f).setColor(0xFFFFFFFF);
        BufferUploader.drawWithShader(texBuffer.buildOrThrow());
    }

    private void addCheckerboard(BufferBuilder buffer, float m00, float m11, float m30, float m31, NativeImage pixels) {
        var x0 = (int) Math.floor(imageX(pixels));
        var y0 = (int) Math.floor(imageY(pixels));
        var x1 = (int) Math.ceil(x0 + pixels.getWidth() * zoom);
        var y1 = (int) Math.ceil(y0 + pixels.getHeight() * zoom);
        var cell = 8;
        var startX = Math.max(canvasX, x0);
        var startY = Math.max(canvasY, y0);
        var endX = Math.min(canvasX + canvasW, x1);
        var endY = Math.min(canvasY + canvasH, y1);
        for (var y = startY; y < endY; y += cell) {
            for (var x = startX; x < endX; x += cell) {
                var parity = ((x - x0) / cell + (y - y0) / cell) & 1;
                addQuad(
                    buffer,
                    m00,
                    m11,
                    m30,
                    m31,
                    x,
                    y,
                    Math.min(x + cell, endX),
                    Math.min(y + cell, endY),
                    parity == 0 ? CHECKER_LIGHT : CHECKER_DARK
                );
            }
        }
    }

    private void addPixelGrid(BufferBuilder buffer, float m00, float m11, float m30, float m31, NativeImage pixels) {
        if (zoom < 8.0) {
            return;
        }
        var minX = Math.max(0, (int) Math.floor((canvasX - imageX(pixels)) / zoom));
        var maxX = Math.min(pixels.getWidth(), (int) Math.ceil((canvasX + canvasW - imageX(pixels)) / zoom));
        var minY = Math.max(0, (int) Math.floor((canvasY - imageY(pixels)) / zoom));
        var maxY = Math.min(pixels.getHeight(), (int) Math.ceil((canvasY + canvasH - imageY(pixels)) / zoom));
        for (var x = minX; x <= maxX; x++) {
            var sx = (int) Math.round(imageX(pixels) + x * zoom);
            addQuad(buffer, m00, m11, m30, m31, sx, canvasY, sx + 1, canvasY + canvasH, GRID_COLOR);
        }
        for (var y = minY; y <= maxY; y++) {
            var sy = (int) Math.round(imageY(pixels) + y * zoom);
            addQuad(buffer, m00, m11, m30, m31, canvasX, sy, canvasX + canvasW, sy + 1, GRID_COLOR);
        }
    }

    private void addSelection(BufferBuilder buffer, float m00, float m11, float m30, float m31) {
        var selection = TextureEditorState.selection();
        var active = ModelerScene.get().activeTexture;
        var pixels = active == null ? null : active.texture().getPixels();
        if (selection == null || pixels == null) {
            return;
        }
        var x0 = (int) Math.round(imageX(pixels) + selection.x0() * zoom);
        var y0 = (int) Math.round(imageY(pixels) + selection.y0() * zoom);
        var x1 = (int) Math.round(imageX(pixels) + selection.x1Exclusive() * zoom);
        var y1 = (int) Math.round(imageY(pixels) + selection.y1Exclusive() * zoom);
        addQuad(buffer, m00, m11, m30, m31, x0, y0, x1, y1, SELECTION_FILL_COLOR);
        addRectOutline(buffer, m00, m11, m30, m31, x0, y0, x1, y1, SELECTION_OUTLINE_COLOR);
    }

    private void ensureView(LoadedTexture active, NativeImage pixels) {
        if (active == viewedTexture && pixels.getWidth() == viewedWidth && pixels.getHeight() == viewedHeight) {
            return;
        }
        viewedTexture = active;
        viewedWidth = pixels.getWidth();
        viewedHeight = pixels.getHeight();
        zoom = Math.max(1.0, minZoom(pixels));
        zoom = Math.min(zoom, 8.0);
        panX = 0;
        panY = 0;
        dragMode = DragMode.NONE;
        paintTexture = null;
        paintBefore = null;
        selectionBefore = null;
        TextureEditorState.clearSelection();
    }

    private @Nullable Pixel pixelAt(double mouseX, double mouseY, NativeImage pixels, boolean clamp) {
        var px = (int) Math.floor((mouseX - imageX(pixels)) / zoom);
        var py = (int) Math.floor((mouseY - imageY(pixels)) / zoom);
        if (clamp) {
            px = Math.max(0, Math.min(pixels.getWidth() - 1, px));
            py = Math.max(0, Math.min(pixels.getHeight() - 1, py));
            return new Pixel(px, py);
        }
        if (px < 0 || px >= pixels.getWidth() || py < 0 || py >= pixels.getHeight()) {
            return null;
        }
        return new Pixel(px, py);
    }

    private void updateSelection(int pixelX, int pixelY) {
        var x0 = Math.min(selectStartX, pixelX);
        var y0 = Math.min(selectStartY, pixelY);
        var x1 = Math.max(selectStartX, pixelX) + 1;
        var y1 = Math.max(selectStartY, pixelY) + 1;
        TextureEditorState.setSelection(x0, y0, x1, y1);
    }

    private void paintLine(LoadedTexture texture, NativeImage pixels, int x0, int y0, int x1, int y1) {
        var nativeColor = TextureEditorState.argbToNative(TextureEditorState.primaryColor());
        var dx = Math.abs(x1 - x0);
        var dy = Math.abs(y1 - y0);
        var sx = x0 < x1 ? 1 : -1;
        var sy = y0 < y1 ? 1 : -1;
        var err = dx - dy;
        var x = x0;
        var y = y0;
        while (true) {
            if (x >= 0 && x < pixels.getWidth() && y >= 0 && y < pixels.getHeight() && TextureEditorState.containsSelectedPixel(x, y)) {
                pixels.setPixelRGBA(x, y, nativeColor);
            }
            if (x == x1 && y == y1) {
                break;
            }
            var e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x += sx;
            }
            if (e2 < dx) {
                err += dx;
                y += sy;
            }
        }
        texture.texture().upload();
    }

    private boolean bucketFill(LoadedTexture texture, NativeImage pixels, int startX, int startY) {
        if (!TextureEditorState.containsSelectedPixel(startX, startY)) {
            return false;
        }
        var replacement = TextureEditorState.argbToNative(TextureEditorState.primaryColor());
        var target = pixels.getPixelRGBA(startX, startY);
        if (target == replacement) {
            return false;
        }
        var changed = false;
        var width = pixels.getWidth();
        var height = pixels.getHeight();
        var visited = new boolean[width * height];
        var queue = new ArrayDeque<Integer>();
        queue.add(startY * width + startX);
        while (!queue.isEmpty()) {
            var packed = queue.removeFirst();
            var x = packed % width;
            var y = packed / width;
            var idx = y * width + x;
            if (visited[idx]) {
                continue;
            }
            visited[idx] = true;
            if (!TextureEditorState.containsSelectedPixel(x, y) || pixels.getPixelRGBA(x, y) != target) {
                continue;
            }
            pixels.setPixelRGBA(x, y, replacement);
            changed = true;
            if (x > 0) {
                queue.add(idx - 1);
            }
            if (x + 1 < width) {
                queue.add(idx + 1);
            }
            if (y > 0) {
                queue.add(idx - width);
            }
            if (y + 1 < height) {
                queue.add(idx + width);
            }
        }
        if (changed) {
            texture.texture().upload();
        }
        return changed;
    }

    private void pushPaintAction() {
        var texture = paintTexture;
        var before = paintBefore;
        paintTexture = null;
        paintBefore = null;
        if (texture == null || before == null) {
            return;
        }
        var pixels = texture.texture().getPixels();
        if (pixels == null) {
            return;
        }
        var after = ModelerAction.TexturePixelsMemento.of(pixels);
        pushTexturePixelsAction("texture_pencil", "Pencil Stroke", texture, before, after);
    }

    private static void pushTexturePixelsAction(
        String typeId,
        String description,
        LoadedTexture texture,
        ModelerAction.TexturePixelsMemento before,
        ModelerAction.TexturePixelsMemento after
    ) {
        if (!before.differsFrom(after)) {
            return;
        }
        ModelerActionHistory.push(
            new ModelerAction.TexturePixelsAction(
                typeId,
                description + " " + texture.displayName(),
                System.currentTimeMillis(),
                texture,
                before,
                after
            )
        );
    }

    private static void pushSelectionAction(
        @Nullable TextureEditorState.Selection before,
        @Nullable TextureEditorState.Selection after
    ) {
        if (Objects.equals(before, after)) {
            return;
        }
        var description = after == null ? "Clear Texture Selection" : "Select Texture Region";
        ModelerActionHistory.push(
            new ModelerAction.TextureSelectionAction(
                "texture_select",
                description,
                System.currentTimeMillis(),
                before,
                after
            )
        );
    }

    private boolean insidePanel(double mouseX, double mouseY) {
        return mouseX >= panelX && mouseX < panelX + panelWidth && mouseY >= panelY && mouseY < panelY + panelHeight;
    }

    private boolean insideCanvas(double mouseX, double mouseY) {
        return insidePanel(mouseX, mouseY) && mouseY >= canvasY && mouseY < canvasY + canvasH;
    }

    private double minZoom(NativeImage pixels) {
        var fitW = Math.max(0.1, (canvasW - 2.0 * VIEW_PADDING) / Math.max(1, pixels.getWidth()));
        var fitH = Math.max(0.1, (canvasH - 2.0 * VIEW_PADDING) / Math.max(1, pixels.getHeight()));
        return Math.max(0.1, Math.min(fitW, fitH));
    }

    private double baseImageX(NativeImage pixels) {
        return canvasX + (canvasW - pixels.getWidth() * zoom) * 0.5;
    }

    private double baseImageY(NativeImage pixels) {
        return canvasY + (canvasH - pixels.getHeight() * zoom) * 0.5;
    }

    private double imageX(NativeImage pixels) {
        return baseImageX(pixels) + panX;
    }

    private double imageY(NativeImage pixels) {
        return baseImageY(pixels) + panY;
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private static void addRectOutline(
        BufferBuilder buffer,
        float m00,
        float m11,
        float m30,
        float m31,
        int x0,
        int y0,
        int x1,
        int y1,
        int color
    ) {
        addQuad(buffer, m00, m11, m30, m31, x0, y0, x1, y0 + 1, color);
        addQuad(buffer, m00, m11, m30, m31, x0, y1 - 1, x1, y1, color);
        addQuad(buffer, m00, m11, m30, m31, x0, y0, x0 + 1, y1, color);
        addQuad(buffer, m00, m11, m30, m31, x1 - 1, y0, x1, y1, color);
    }

    private static void addQuad(
        BufferBuilder buffer,
        float m00,
        float m11,
        float m30,
        float m31,
        int x0,
        int y0,
        int x1,
        int y1,
        int color
    ) {
        if (x1 <= x0 || y1 <= y0) {
            return;
        }
        buffer.addVertex(m00 * x0 + m30, m11 * y0 + m31, 0).setColor(color);
        buffer.addVertex(m00 * x0 + m30, m11 * y1 + m31, 0).setColor(color);
        buffer.addVertex(m00 * x1 + m30, m11 * y1 + m31, 0).setColor(color);
        buffer.addVertex(m00 * x1 + m30, m11 * y0 + m31, 0).setColor(color);
    }

    private static void applyRawScissor(GuiGraphics graphics, int x, int y, int w, int h) {
        if (w <= 0 || h <= 0) {
            RenderSystem.disableScissor();
            return;
        }
        graphics.flush();
        var matrix = graphics.pose().last().pose();
        var topLeft = matrix.transformPosition((float) x, (float) y, 0f, new Vector3f());
        var bottomRight = matrix.transformPosition((float) (x + w), (float) (y + h), 0f, new Vector3f());

        var window = Minecraft.getInstance().getWindow();
        var winHeight = window.getHeight();
        var guiScale = window.getGuiScale();
        var leftRaw = (int) ((double) topLeft.x * guiScale);
        var bottomRaw = (int) ((double) winHeight - (double) bottomRight.y * guiScale);
        var widthRaw = Math.max(0, (int) ((double) (bottomRight.x - topLeft.x) * guiScale));
        var heightRaw = Math.max(0, (int) ((double) (bottomRight.y - topLeft.y) * guiScale));
        RenderSystem.enableScissor(leftRaw, bottomRaw, widthRaw, heightRaw);
    }

    private record Pixel(
        int x,
        int y
    ) {}
}
