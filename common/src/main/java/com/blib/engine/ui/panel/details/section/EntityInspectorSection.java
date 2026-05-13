package com.blib.engine.ui.panel.details.section;

import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.domain.selection.picking.EntitySelectable;
import com.blib.engine.ui.EngineFont;
import com.blib.engine.ui.panel.base.InspectorSection;
import com.blib.engine.ui.panel.details.DetailsPanel;

/**
 * Inspector section for {@link EntitySelectable}. Phase-1 adapter: delegates to {@link DetailsPanel}'s existing
 * {@code internalRenderEntityView}. Phase-2 work absorbs the entity-specific render + click + state fields out of
 * {@code DetailsPanel} and into this class — the architectural goal of the inspector decomposition.
 * <p>
 * Registering this section is what wires entity inspection into the {@code DetailsPanel} dispatch loop. Adding a new
 * inspector view for a different selectable type now means dropping a sibling section and registering it, with no
 * changes to the panel's own dispatch.
 */
@ApiStatus.Internal
public final class EntityInspectorSection implements InspectorSection<EntitySelectable> {

    private final DetailsPanel panel;

    public EntityInspectorSection(DetailsPanel panel) {
        this.panel = panel;
    }

    @Override
    public String id() {
        return "entity";
    }

    @Override
    public Class<EntitySelectable> selectableType() {
        return EntitySelectable.class;
    }

    @Override
    public int render(GuiGraphics graphics, int x, int y, int width, EntitySelectable target, int mouseX, int mouseY) {
        panel.internalRenderEntityView(graphics, EngineFont.get(), x, y, width, mouseX, mouseY, target);
        // Returns 0 because the legacy view takes ownership of the entire below-header region; sections aren't yet
        // stacked. When state is migrated in, the section will return the consumed y so siblings can follow.
        return 0;
    }
}
