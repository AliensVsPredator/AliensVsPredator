package com.blib.engine.layout;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.regex.Pattern;

/**
 * Top-level on-disk representation of a single named layout. One file per layout under
 * {@code <gameDir>/blib/engine/layouts/<id>.json}; the {@code id} field matches the filename so external file rename
 * detection (filename-id divergence) is straightforward.
 * <p>
 * {@code templateBase} records which built-in template (if any) the layout was originally cloned from — null for "from
 * scratch" layouts. The template ids correspond to {@link LayoutTemplate#id()} values. This drives the "Reset to
 * Template" menu item: if {@code templateBase != null}, Reset rebuilds from {@code LayoutTemplate.byId(templateBase)};
 * if a layout's id is itself a template id, Reset rebuilds from that template.
 * <p>
 * {@code version} is bumped when the schema changes so future BLib releases can migrate older layouts in place. The
 * loader rejects unknown versions rather than silently coercing.
 */
@ApiStatus.Internal
public record LayoutDoc(
    int version,
    String id,
    String displayName,
    @Nullable String templateBase,
    String createdAt,
    String modifiedAt,
    BodyNode body
) {

    public static final int CURRENT_VERSION = 1;

    /**
     * Layout id pattern. Mirrors the project-name pattern from {@code EngineProjectIO} so users get the same
     * lowercase-alphanumeric-plus-`_-` rule across every named entity in the engine. 1–32 chars keeps menu rendering
     * predictable.
     */
    public static final Pattern ID_PATTERN = Pattern.compile("^[a-z0-9_-]{1,32}$");

    public static final int MAX_DISPLAY_NAME_LENGTH = 48;

    public static void validateId(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Layout id is empty");
        }
        if (!ID_PATTERN.matcher(id).matches()) {
            throw new IllegalArgumentException("Layout id must be 1–32 lowercase letters, digits, '_' or '-'");
        }
    }

    public static void validateDisplayName(String displayName) {
        if (displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException("Display name is empty");
        }
        if (displayName.length() > MAX_DISPLAY_NAME_LENGTH) {
            throw new IllegalArgumentException("Display name must be ≤ " + MAX_DISPLAY_NAME_LENGTH + " chars");
        }
    }

    public LayoutDoc withTouchedModifiedAt() {
        return new LayoutDoc(version, id, displayName, templateBase, createdAt, Instant.now().toString(), body);
    }

    public LayoutDoc withDisplayName(String newDisplayName) {
        return new LayoutDoc(version, id, newDisplayName, templateBase, createdAt, modifiedAt, body);
    }

    public LayoutDoc withId(String newId) {
        return new LayoutDoc(version, newId, displayName, templateBase, createdAt, modifiedAt, body);
    }

    public LayoutDoc withBody(BodyNode newBody) {
        return new LayoutDoc(version, id, displayName, templateBase, createdAt, modifiedAt, newBody);
    }
}
