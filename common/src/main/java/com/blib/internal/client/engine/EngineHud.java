package com.blib.internal.client.engine;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;

/**
 * On-screen overlay for engine mode. Shows current navigation mode, freecam pose, current selection, and (in orbit
 * mode) the pivot position. Future phases extend this with tool name, modifier hints, and live offset/rotation values.
 */
@ApiStatus.Internal
public final class EngineHud {

    private static final int TEXT_COLOR = 0xFFFFAA00;

    private static final int SELECTION_COLOR = 0xFFFFD060;

    private static final int LINE_HEIGHT = 10;

    private static final int MARGIN = 6;

    private EngineHud() {}

    public static void render(GuiGraphics graphics) {
        var session = EngineMode.get().session();

        if (session == null) {
            return;
        }

        var font = Minecraft.getInstance().font;
        var pos = session.cameraPosition();
        var pivot = session.pivot();
        var lines = new ArrayList<Component>();

        switch (session.mode()) {
            case FLY -> {
                lines.add(Component.literal("[ENGINE: FLY]"));
                lines.add(Component.literal("pos: %.2f / %.2f / %.2f".formatted(pos.x, pos.y, pos.z)));
                lines.add(Component.literal("yaw: %.1f° pitch: %.1f°".formatted(session.yaw(), session.pitch())));
                lines.add(Component.literal("WASD + mouse | /blib engine orbit | /blib engine to exit"));
            }
            case ORBIT -> {
                lines.add(Component.literal("[ENGINE: ORBIT]"));
                lines.add(Component.literal("pos: %.2f / %.2f / %.2f".formatted(pos.x, pos.y, pos.z)));
                lines.add(Component.literal("pivot: %.2f / %.2f / %.2f".formatted(pivot.x, pivot.y, pivot.z)));
                lines.add(Component.literal("yaw: %.1f° pitch: %.1f°".formatted(session.yaw(), session.pitch())));
                lines.add(Component.literal("LMB-click select | LMB-drag orbit | RMB-drag pan | wheel zoom"));
            }
        }

        var firstLine = 0;

        for (var i = 0; i < lines.size(); i++) {
            graphics.drawString(font, lines.get(i), MARGIN, MARGIN + (firstLine + i) * LINE_HEIGHT, TEXT_COLOR, true);
        }

        var selected = session.selectedEntity();

        if (selected != null) {
            var label = "selected: %s (%s)".formatted(
                selected.getName().getString(),
                selected.getUUID().toString().substring(0, 8)
            );
            graphics.drawString(
                font,
                Component.literal(label),
                MARGIN,
                MARGIN + (firstLine + lines.size()) * LINE_HEIGHT,
                SELECTION_COLOR,
                true
            );
        }
    }
}
