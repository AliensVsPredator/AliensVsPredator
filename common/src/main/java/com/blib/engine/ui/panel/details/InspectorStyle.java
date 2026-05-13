package com.blib.engine.ui.panel.details;

import org.jetbrains.annotations.ApiStatus;

/**
 * Shared visual + layout constants for {@code DetailsPanel} and its per-type {@code InspectorSection}s. Each section
 * was previously reading these from package-private fields on {@link DetailsPanel}, which broke once sections moved
 * into the {@code section} sub-package (Java packages are flat — sub-packages have no special access). Lifting the
 * constants here means every section imports the same palette and any future tweak lands in one place.
 */
@ApiStatus.Internal
public final class InspectorStyle {

    public static final int BACKGROUND_COLOR = 0xFF18181C;

    public static final int SECTION_HEADER_BG_COLOR = 0xFF26262C;

    public static final int HEADER_BAR_BG_COLOR = 0xFF1F1F26;

    public static final int LABEL_COLOR = 0xFF7C8088;

    public static final int VALUE_COLOR = 0xFFD8D8E0;

    public static final int HEADER_TEXT_COLOR = 0xFFB8C0D0;

    public static final int ACCENT_COLOR = 0xFFE6C26B;

    public static final int CONTENT_PADDING = 5;

    public static final int LINE_HEIGHT = 10;

    public static final int LABEL_COLUMN_WIDTH = 70;

    public static final int SECTION_HEADER_HEIGHT = 11;

    public static final int HEADER_BAR_HEIGHT = 14;

    /** Vertical gap between editable rows in the block view. */
    public static final int ROW_GAP = 2;

    /** Bounding-box side for the help-icon hit-test. The glyph itself is a single "?" rendered at this size. */
    public static final int HELP_ICON_SIZE = 7;

    /** Gap between the row label / section header text and the help icon. */
    public static final int HELP_ICON_GAP = 3;

    public static final int HELP_ICON_COLOR = 0xFF606068;

    public static final int HELP_ICON_HOVER_COLOR = 0xFFE6C26B;

    /** Warning palette — used for the Name label + warning icon when this jigsaw's Name is orphaned. */
    public static final int WARN_LABEL_COLOR = 0xFFE6A23C;

    public static final int WARN_ICON_COLOR = 0xFFE6A23C;

    public static final int WARN_ICON_HOVER_COLOR = 0xFFFFC766;

    private InspectorStyle() {}
}
