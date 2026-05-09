package com.blib.engine.selection;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Immutable snapshot of what the workspace has selected. Built by {@link SelectionManager} and read by the inspector +
 * selection-highlight renderer. Single-item and empty selections are the common cases; multi-item selections are
 * reserved for the future shift-click feature (deferred from phase 7 MVP).
 */
@ApiStatus.Internal
public record Selection(List<Selectable> items) {

    private static final Selection EMPTY = new Selection(List.of());

    public Selection {
        items = List.copyOf(items);
    }

    public static Selection empty() {
        return EMPTY;
    }

    public static Selection single(Selectable s) {
        return new Selection(List.of(s));
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public boolean isMultiple() {
        return items.size() > 1;
    }

    /**
     * The single selected item if exactly one is selected, otherwise {@code null}. Convenience for the common
     * single-selection inspector path.
     */
    public @Nullable Selectable single() {
        return items.size() == 1 ? items.get(0) : null;
    }

    /**
     * Set of types present in this selection. Empty for empty selections; one element for single-type selections;
     * multiple for mixed-type selections (reserved for future use).
     */
    public Set<SelectableType> types() {
        if (items.isEmpty()) {
            return Collections.emptySet();
        }
        var set = EnumSet.noneOf(SelectableType.class);
        for (var item : items) {
            set.add(item.type());
        }
        return set;
    }
}
