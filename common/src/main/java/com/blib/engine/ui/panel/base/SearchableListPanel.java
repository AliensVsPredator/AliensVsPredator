package com.blib.engine.ui.panel.base;

import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.ui.widget.TextInput;

/**
 * Extension of {@link ScrollableListPanel} with a built-in search/filter {@link TextInput}. The input lives above the
 * scrollable rows; subclasses query {@link #searchText()} when computing their displayed-row set. Standardises the
 * pattern that {@code OutlinerPanel}, {@code TagBrowserPanel}, {@code ContentBrowserPanel}, {@code PoolEditorPanel},
 * and others repeat individually.
 * <p>
 * Subclasses still own the actual filtering — this base only owns the input widget, its placeholder text, and the
 * shared scroll plumbing inherited from {@link ScrollableListPanel}.
 */
@ApiStatus.Internal
public abstract class SearchableListPanel extends ScrollableListPanel {

    protected final TextInput searchInput;

    protected SearchableListPanel(String placeholder) {
        this.searchInput = new TextInput(placeholder);
    }

    /** Current filter text. Empty string means "no filter active". */
    protected final String searchText() {
        return searchInput.content();
    }
}
