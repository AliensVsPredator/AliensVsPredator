package com.blib.engine.ui.panel.base;

import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.ApiStatus;

import com.blib.api.client.engine.v1.inspector.InspectorSection;
import com.blib.engine.domain.selection.picking.Selectable;

/**
 * Bridges a downstream-facing {@link com.blib.api.client.engine.v1.inspector.InspectorSection} (typed against a public
 * selection interface like {@link com.blib.api.client.engine.v1.selection.FactionSelection}) into the internal
 * {@link com.blib.engine.ui.panel.base.InspectorSection} (typed against {@link Selectable}). The adapter claims a
 * catch-all {@code Selectable} selectable-type so the internal registry routes every selection through it, then
 * filters per-render against the public section's declared selectable-type — that way an internal selectable like
 * {@code FactionSelectable} (which implements the public {@code FactionSelection}) flows through correctly without
 * the public API needing to know about internal selectable classes.
 */
@ApiStatus.Internal
public final class PublicInspectorSectionAdapter implements com.blib.engine.ui.panel.base.InspectorSection<Selectable> {

    private final InspectorSection<?> publicSection;

    private PublicInspectorSectionAdapter(InspectorSection<?> publicSection) {
        this.publicSection = publicSection;
    }

    public static PublicInspectorSectionAdapter wrap(InspectorSection<?> publicSection) {
        return new PublicInspectorSectionAdapter(publicSection);
    }

    @Override
    public String id() {
        return publicSection.id();
    }

    @Override
    public Class<Selectable> selectableType() {
        return Selectable.class;
    }

    @Override
    public int order() {
        return publicSection.order();
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public int render(GuiGraphics graphics, int x, int y, int width, Selectable target, int mouseX, int mouseY) {
        var publicType = publicSection.selectableType();
        if (!publicType.isInstance(target)) {
            return y;
        }
        return ((InspectorSection) publicSection).render(graphics, x, y, width, target, mouseX, mouseY);
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public boolean mouseClicked(double mouseX, double mouseY, int button, Selectable target) {
        var publicType = publicSection.selectableType();
        if (!publicType.isInstance(target)) {
            return false;
        }
        return ((InspectorSection) publicSection).mouseClicked(mouseX, mouseY, button, target);
    }
}
