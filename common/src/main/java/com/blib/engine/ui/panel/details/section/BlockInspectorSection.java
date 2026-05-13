package com.blib.engine.ui.panel.details.section;

import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.domain.selection.picking.BlockSelectable;
import com.blib.engine.ui.EngineFont;
import com.blib.engine.ui.panel.base.InspectorSection;
import com.blib.engine.ui.panel.details.DetailsPanel;

/**
 * Inspector section for {@link BlockSelectable}. Phase-1 adapter: delegates to {@link DetailsPanel}'s existing
 * {@code internalRenderGenericBlockView}.
 */
@ApiStatus.Internal
public final class BlockInspectorSection implements InspectorSection<BlockSelectable> {

    private final DetailsPanel panel;

    public BlockInspectorSection(DetailsPanel panel) {
        this.panel = panel;
    }

    @Override
    public String id() {
        return "block";
    }

    @Override
    public Class<BlockSelectable> selectableType() {
        return BlockSelectable.class;
    }

    @Override
    public int render(GuiGraphics graphics, int x, int y, int width, BlockSelectable target, int mouseX, int mouseY) {
        panel.internalRenderGenericBlockView(graphics, EngineFont.get(), x, y, width, mouseX, mouseY, target);
        return 0;
    }
}
