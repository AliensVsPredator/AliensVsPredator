package com.blib.api.client.engine.v1.inspector;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.blib.engine.ui.layout.UiRect;
import com.blib.engine.ui.layout.UiText;
import com.blib.engine.ui.panel.details.InspectorStyle;

/**
 * Reusable immediate-mode collapse state for groups inside an inspector section.
 * <p>
 * Call {@link #beginFrame()} once at the start of the owning inspector section's render pass, then draw each header
 * with {@link #drawHeader}. If the returned {@link Header#expanded()} is false, skip that group's body. Forward clicks
 * from the owning {@link InspectorSection#mouseClicked} implementation to {@link #mouseClicked}.
 */
public final class CollapsibleInspectorSections {

    private static final int CARET_WIDTH = 8;

    private static final int LABEL_GAP = 2;

    private static final int HOVER_HEADER_BG_COLOR = 0xFF303038;

    private final String ownerKey;

    private final Set<String> collapsed = new HashSet<>();

    private final List<HeaderHit> headerHits = new ArrayList<>();

    public CollapsibleInspectorSections(String ownerKey) {
        this.ownerKey = ownerKey == null || ownerKey.isBlank() ? "inspector" : ownerKey;
    }

    public void beginFrame() {
        headerHits.clear();
    }

    public Header drawHeader(
        GuiGraphics graphics,
        Font font,
        int x,
        int y,
        int width,
        String key,
        String label,
        int mouseX,
        int mouseY
    ) {
        var fullKey = fullKey(key);
        var expanded = !collapsed.contains(fullKey);
        var rect = UiRect.of(x, y, width, InspectorStyle.SECTION_HEADER_HEIGHT);
        var hovered = rect.contains(mouseX, mouseY);
        graphics.fill(
            rect.x(),
            rect.y(),
            rect.right(),
            rect.bottom(),
            hovered ? HOVER_HEADER_BG_COLOR : InspectorStyle.SECTION_HEADER_BG_COLOR
        );

        var textY = UiText.centeredY(font, rect);
        UiText.drawClipped(
            graphics,
            font,
            expanded ? "v" : ">",
            rect.x() + InspectorStyle.CONTENT_PADDING,
            textY,
            CARET_WIDTH,
            InspectorStyle.HEADER_TEXT_COLOR
        );

        var labelX = rect.x() + InspectorStyle.CONTENT_PADDING + CARET_WIDTH + LABEL_GAP;
        var labelW = Math.max(0, rect.right() - InspectorStyle.CONTENT_PADDING - labelX);
        UiText.drawClipped(graphics, font, label, labelX, textY, labelW, InspectorStyle.HEADER_TEXT_COLOR);

        headerHits.add(new HeaderHit(rect.x(), rect.y(), rect.width(), rect.height(), fullKey));
        return new Header(rect.bottom(), expanded);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return false;
        }
        for (var i = headerHits.size() - 1; i >= 0; i--) {
            var hit = headerHits.get(i);
            if (mouseX >= hit.x && mouseX < hit.x + hit.width && mouseY >= hit.y && mouseY < hit.y + hit.height) {
                toggle(hit.key);
                return true;
            }
        }
        return false;
    }

    public boolean isExpanded(String key) {
        return !collapsed.contains(fullKey(key));
    }

    public void setExpanded(String key, boolean expanded) {
        var fullKey = fullKey(key);
        if (expanded) {
            collapsed.remove(fullKey);
        } else {
            collapsed.add(fullKey);
        }
    }

    public void clear() {
        collapsed.clear();
        headerHits.clear();
    }

    private void toggle(String fullKey) {
        if (!collapsed.remove(fullKey)) {
            collapsed.add(fullKey);
        }
    }

    private String fullKey(String key) {
        return ownerKey + "/" + (key == null || key.isBlank() ? "section" : key);
    }

    public record Header(
        int nextY,
        boolean expanded
    ) {}

    private record HeaderHit(
        int x,
        int y,
        int width,
        int height,
        String key
    ) {}
}
