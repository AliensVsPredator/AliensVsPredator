package com.blib.mod.client.render.goap.panel;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;

import com.blib.mod.client.render.goap.constant.GOAPDebugHUDConstants;
import com.blib.mod.client.render.goap.model.GOAPAgentDebugData;
import com.blib.mod.common.gameplay.goap.GOAPDebugTracker;
import com.blib.mod.common.network.packet.S2CGOAPDebugPayload;

@ApiStatus.Internal
public final class GOAPDebugWorldStatePanel {

    public static final GOAPDebugWorldStatePanel INSTANCE = new GOAPDebugWorldStatePanel();

    private static final String SEPARATOR = " : ";

    private static final long PAGE_INTERVAL_MS = 5000;

    private int autoPage;

    private long lastAutoPageTime;

    private GOAPDebugWorldStatePanel() {}

    public void render(
        GuiGraphics graphics,
        Font font,
        GOAPAgentDebugData agent,
        S2CGOAPDebugPayload payload,
        int screenWidth
    ) {
        var keys = new ArrayList<>(agent.graphSensorKeys());
        keys.sort(String.CASE_INSENSITIVE_ORDER);
        var worldState = agent.worldState();
        var totalKeys = keys.size();
        var pageSize = GOAPDebugTracker.getWorldStatePageSize();
        var totalPages = Math.max(1, (totalKeys + pageSize - 1) / pageSize);

        var currentPage = resolveCurrentPage(payload, totalPages);
        var header = "World State (%d/%d)".formatted(currentPage + 1, totalPages);

        var maxKeyWidth = 0;

        for (var key : keys) {
            maxKeyWidth = Math.max(maxKeyWidth, font.width(key));
        }

        var separatorWidth = font.width(SEPARATOR);
        var valueWidth = font.width("0".repeat(32));
        var lineWidth = maxKeyWidth + separatorWidth + valueWidth;
        var totalWidth = Math.max(lineWidth, font.width(header)) + GOAPDebugHUDConstants.PADDING * 2;
        var lineCount = Math.min(pageSize, totalKeys - currentPage * pageSize) + 1;
        var totalHeight = lineCount * GOAPDebugHUDConstants.LINE_HEIGHT + GOAPDebugHUDConstants.PADDING * 2;

        var panelX = screenWidth - totalWidth - GOAPDebugHUDConstants.PADDING;

        graphics.fill(
            panelX,
            GOAPDebugHUDConstants.PADDING,
            panelX + totalWidth,
            GOAPDebugHUDConstants.PADDING + totalHeight,
            GOAPDebugHUDConstants.BACKGROUND_COLOR
        );

        var textX = panelX + GOAPDebugHUDConstants.PADDING;
        var y = GOAPDebugHUDConstants.PADDING + GOAPDebugHUDConstants.PADDING;

        graphics.drawString(font, header, textX, y, GOAPDebugHUDConstants.HEADER_COLOR, false);
        y += GOAPDebugHUDConstants.LINE_HEIGHT;

        var start = currentPage * pageSize;
        var end = Math.min(start + pageSize, totalKeys);
        var valueX = textX + maxKeyWidth + separatorWidth;

        for (var i = start; i < end; i++) {
            var key = keys.get(i);
            var value = worldState.getOrDefault(key, "");
            var hasValue = !value.isEmpty();
            var color = hasValue ? GOAPDebugHUDConstants.TEXT_COLOR : GOAPDebugHUDConstants.BLACKBOARD_COLOR;

            graphics.drawString(font, key, textX, y, color, false);
            graphics.drawString(font, SEPARATOR, textX + maxKeyWidth, y, color, false);

            if (hasValue) {
                graphics.drawString(font, value, valueX, y, color, false);
            }

            y += GOAPDebugHUDConstants.LINE_HEIGHT;
        }
    }

    public void clear() {
        this.autoPage = 0;
        this.lastAutoPageTime = 0;
    }

    private int resolveCurrentPage(S2CGOAPDebugPayload payload, int totalPages) {
        if (payload.worldStateAutoPage()) {
            var now = System.currentTimeMillis();

            if (now - lastAutoPageTime >= PAGE_INTERVAL_MS) {
                autoPage = (autoPage + 1) % totalPages;
                lastAutoPageTime = now;
            }

            if (autoPage >= totalPages) {
                autoPage = 0;
            }

            return autoPage;
        }

        return Math.clamp(payload.worldStatePage(), 0, totalPages - 1);
    }
}
