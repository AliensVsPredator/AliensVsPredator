package com.blib.engine.ui.panel.details;

import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.ui.widget.SearchableSelect;

@ApiStatus.Internal
final class TagInspectorUi {

    static final String ADD_TAG_PLACEHOLDER = "Select tag to add...";

    private TagInspectorUi() {}

    static void configureAddTagPicker(SearchableSelect<?> select) {
        select.setPlaceholderText(ADD_TAG_PLACEHOLDER);
    }

    static int renderAddTagPicker(
        GuiGraphics graphics,
        int x,
        int y,
        int width,
        SearchableSelect<?> select,
        int mouseX,
        int mouseY
    ) {
        var pickerX = x + InspectorStyle.CONTENT_PADDING;
        var pickerW = Math.max(0, width - 2 * InspectorStyle.CONTENT_PADDING);
        select.render(graphics, pickerX, y, pickerW, mouseX, mouseY);
        return y + SearchableSelect.HEIGHT + InspectorStyle.ROW_GAP;
    }
}
