package com.blib.engine.ui.panel.base;

import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.blib.engine.domain.selection.picking.Selectable;

/**
 * Holds the {@link InspectorSection}s known to the engine. Inspector panels query the registry for sections that match
 * the current selection's type, in render-order. Replaces the inline {@code switch (selection.type())} that previously
 * lived in {@code DetailsPanel.render} — adding a new inspector view now means dropping a new {@code InspectorSection}
 * implementation and registering it, with no changes to the inspector panel itself.
 */
@ApiStatus.Internal
public final class InspectorSectionRegistry {

    private static final List<InspectorSection<? extends Selectable>> SECTIONS = new ArrayList<>();

    private InspectorSectionRegistry() {}

    public static synchronized void register(InspectorSection<? extends Selectable> section) {
        SECTIONS.add(section);
        SECTIONS.sort(Comparator.comparingInt(InspectorSection::order));
    }

    public static synchronized List<InspectorSection<? extends Selectable>> all() {
        return Collections.unmodifiableList(new ArrayList<>(SECTIONS));
    }

    /**
     * Sections whose {@link InspectorSection#selectableType selectable type} is assignable from {@code selection}'s
     * runtime type. Sections that target {@link Selectable} itself match every selection and are conventionally used
     * for tool-state rows (gizmo mode, collision policy).
     */
    public static synchronized List<InspectorSection<? extends Selectable>> matching(Selectable selection) {
        var out = new ArrayList<InspectorSection<? extends Selectable>>();
        for (var s : SECTIONS) {
            if (s.selectableType().isInstance(selection)) {
                out.add(s);
            }
        }
        return out;
    }
}
