package com.blib.engine.ui.panel.details.section;

import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.domain.selection.picking.BlockVolumeSelectable;
import com.blib.engine.ui.EngineFont;
import com.blib.engine.ui.panel.base.InspectorSection;
import com.blib.engine.ui.panel.details.DetailsPanel;

/**
 * Inspector section for {@link BlockVolumeSelectable}. Phase-1 adapter: delegates to {@link DetailsPanel}'s existing
 * {@code internalRenderBlockVolumeView}.
 */
@ApiStatus.Internal
public final class BlockVolumeInspectorSection implements InspectorSection<BlockVolumeSelectable> {

    private final DetailsPanel panel;

    public BlockVolumeInspectorSection(DetailsPanel panel) {
        this.panel = panel;
    }

    @Override
    public String id() {
        return "block_volume";
    }

    @Override
    public Class<BlockVolumeSelectable> selectableType() {
        return BlockVolumeSelectable.class;
    }

    @Override
    public int render(GuiGraphics graphics, int x, int y, int width, BlockVolumeSelectable target, int mouseX, int mouseY) {
        panel.internalRenderBlockVolumeView(graphics, EngineFont.get(), x, y, width, mouseX, mouseY);
        return 0;
    }
}
