package com.blib.engine.ui.panel.inspector.section;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.domain.selection.picking.Selectable;
import com.blib.engine.ui.EngineFont;
import com.blib.engine.ui.panel.base.InspectorSection;

/**
 * Inspector section shown when the user hasn't selected anything yet. Carved out of the {@code renderToolStateView}
 * fallback that used to live inline inside {@code DetailsPanel} as the demonstration that the migration path called out
 * in the architecture proposal works.
 * <p>
 * Wired into the registry via {@code InspectorSectionRegistry.register(new NoSelectionHintSection())}; the inspector
 * panel that consumes the registry is expected to call this section only when the current selection is null (since
 * matching by selectable type doesn't otherwise distinguish empty-selection from "anything matches"). A future cleanup
 * pass can introduce a dedicated {@code EmptySelectableMarker} to make this purely type-driven.
 */
@ApiStatus.Internal
public final class NoSelectionHintSection implements InspectorSection<Selectable> {

    private static final int CONTENT_PADDING = 5;

    private static final int LINE_HEIGHT = 11;

    private static final int LABEL_COLOR = 0xFF888894;

    private static final String HINT = "Pick an entity, block, faction, or tag to inspect its details.";

    @Override
    public String id() {
        return "tool_state_hint";
    }

    @Override
    public Class<Selectable> selectableType() {
        return Selectable.class;
    }

    @Override
    public int order() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int render(GuiGraphics graphics, int x, int y, int width, Selectable target, int mouseX, int mouseY) {
        var font = EngineFont.get();
        var hintWidth = font.width(HINT);
        var hintX = x + Math.max(CONTENT_PADDING, (width - hintWidth) / 2);
        var hintY = y + LINE_HEIGHT;
        graphics.drawString(font, Component.literal(HINT), hintX, hintY, LABEL_COLOR, false);
        return hintY + LINE_HEIGHT;
    }
}
