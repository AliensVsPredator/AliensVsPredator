package com.blib.engine.ui.panel.details.section;

import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.domain.selection.picking.TagSelectable;
import com.blib.engine.ui.EngineFont;
import com.blib.engine.ui.panel.base.InspectorSection;
import com.blib.engine.ui.panel.details.DetailsPanel;

/**
 * Inspector section for {@link TagSelectable}. Phase-1 adapter: delegates to {@link DetailsPanel}'s existing
 * {@code internalRenderTagView}.
 */
@ApiStatus.Internal
public final class TagInspectorSection implements InspectorSection<TagSelectable> {

    private final DetailsPanel panel;

    public TagInspectorSection(DetailsPanel panel) {
        this.panel = panel;
    }

    @Override
    public String id() {
        return "tag";
    }

    @Override
    public Class<TagSelectable> selectableType() {
        return TagSelectable.class;
    }

    @Override
    public int render(GuiGraphics graphics, int x, int y, int width, TagSelectable target, int mouseX, int mouseY) {
        panel.internalRenderTagView(graphics, EngineFont.get(), x, y, width, mouseX, mouseY, target);
        return 0;
    }
}
