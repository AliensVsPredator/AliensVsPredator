package com.blib.engine.ui.panel.details.section;

import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.domain.selection.picking.PlacedJigsawPieceSelectable;
import com.blib.engine.ui.EngineFont;
import com.blib.engine.ui.panel.base.InspectorSection;
import com.blib.engine.ui.panel.details.DetailsPanel;

/**
 * Inspector section for {@link PlacedJigsawPieceSelectable}. Phase-1 adapter: delegates to {@link DetailsPanel}'s
 * existing {@code internalRenderPlacedJigsawPieceView}.
 */
@ApiStatus.Internal
public final class PlacedJigsawPieceInspectorSection implements InspectorSection<PlacedJigsawPieceSelectable> {

    private final DetailsPanel panel;

    public PlacedJigsawPieceInspectorSection(DetailsPanel panel) {
        this.panel = panel;
    }

    @Override
    public String id() {
        return "placed_jigsaw_piece";
    }

    @Override
    public Class<PlacedJigsawPieceSelectable> selectableType() {
        return PlacedJigsawPieceSelectable.class;
    }

    @Override
    public int render(GuiGraphics graphics, int x, int y, int width, PlacedJigsawPieceSelectable target, int mouseX, int mouseY) {
        panel.internalRenderPlacedJigsawPieceView(graphics, EngineFont.get(), x, y, width, mouseX, mouseY, target);
        return 0;
    }
}
