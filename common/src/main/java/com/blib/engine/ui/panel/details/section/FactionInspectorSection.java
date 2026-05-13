package com.blib.engine.ui.panel.details.section;

import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.domain.selection.picking.FactionSelectable;
import com.blib.engine.ui.EngineFont;
import com.blib.engine.ui.panel.base.InspectorSection;
import com.blib.engine.ui.panel.details.DetailsPanel;

/**
 * Inspector section for {@link FactionSelectable}. Phase-1 adapter: delegates to {@link DetailsPanel}'s existing
 * {@code internalRenderFactionView}.
 */
@ApiStatus.Internal
public final class FactionInspectorSection implements InspectorSection<FactionSelectable> {

    private final DetailsPanel panel;

    public FactionInspectorSection(DetailsPanel panel) {
        this.panel = panel;
    }

    @Override
    public String id() {
        return "faction";
    }

    @Override
    public Class<FactionSelectable> selectableType() {
        return FactionSelectable.class;
    }

    @Override
    public int render(GuiGraphics graphics, int x, int y, int width, FactionSelectable target, int mouseX, int mouseY) {
        panel.internalRenderFactionView(graphics, EngineFont.get(), x, y, width, mouseX, mouseY, target);
        return 0;
    }
}
