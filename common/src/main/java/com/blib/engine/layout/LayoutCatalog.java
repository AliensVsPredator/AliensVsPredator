package com.blib.engine.layout;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Single facade the workspace screen talks to for layout discovery and persistence. Hides the {@link LayoutStorage} +
 * {@link LayoutTemplate} pair behind a unified API and ensures the layouts directory is seeded on first use.
 * <p>
 * Listing order: templates first (in declaration order), then user layouts alphabetically by display name. The active
 * layout's position in the menu is determined by the catalog's iteration order, not by recency, so the menu doesn't
 * reshuffle as the user switches between layouts.
 */
@ApiStatus.Internal
public final class LayoutCatalog {

    private static final Logger LOGGER = LoggerFactory.getLogger(LayoutCatalog.class);

    private LayoutCatalog() {}

    /**
     * One-shot init that seeds the three template files if missing. Safe to call repeatedly — second-and-later calls
     * find the files and do nothing.
     */
    public static void initialize() {
        try {
            LayoutStorage.seedTemplatesIfMissing();
        } catch (IOException e) {
            LOGGER.warn("[BLib] LayoutCatalog.initialize: seed failed", e);
        }
    }

    /**
     * Every layout that exists on disk, ordered: built-in templates in {@link LayoutTemplate} declaration order, then
     * user layouts alphabetically. Templates that have been customized (file edited since seed) appear in the template
     * slot, not the user-layout slot — they're identified by id.
     */
    public static List<LayoutDoc> listAll() {
        var all = LayoutStorage.list();
        var templates = new ArrayList<LayoutDoc>();
        var userLayouts = new ArrayList<LayoutDoc>();
        for (var doc : all) {
            if (LayoutTemplate.byId(doc.id()) != null) {
                templates.add(doc);
            } else {
                userLayouts.add(doc);
            }
        }
        // Sort templates by their canonical ordinal so DEFAULT comes before GOAP before JIGSAW even if filesystem
        // ordering returns them differently.
        templates.sort(Comparator.comparingInt(d -> {
            var t = LayoutTemplate.byId(d.id());
            return t == null ? Integer.MAX_VALUE : t.ordinal();
        }));
        userLayouts.sort(Comparator.comparing(LayoutDoc::displayName, String.CASE_INSENSITIVE_ORDER));
        var out = new ArrayList<LayoutDoc>(templates.size() + userLayouts.size());
        out.addAll(templates);
        out.addAll(userLayouts);
        return out;
    }

    /**
     * Read a single layout by id. Returns null if the file is missing or unparseable. Templates whose disk file has
     * been deleted return null too — call {@link LayoutTemplate#byId} and {@link LayoutTemplate#toDoc} for a
     * guaranteed- fresh template baseline.
     */
    public static @Nullable LayoutDoc get(String id) {
        return LayoutStorage.read(id).orElse(null);
    }

    /**
     * True iff {@code id} corresponds to a built-in template. Used to decide whether "Reset to Template" rebuilds from
     * code (template id) or simply reloads from disk (user layout). Independent of whether the file exists on disk.
     */
    public static boolean isTemplateId(String id) {
        return LayoutTemplate.byId(id) != null;
    }

    public static void save(LayoutDoc doc) throws IOException {
        LayoutStorage.write(doc);
    }

    public static boolean delete(String id) throws IOException {
        return LayoutStorage.delete(id);
    }

    /** True if {@code id} doesn't already exist on disk and isn't a built-in template id. */
    public static boolean idAvailable(String id) {
        try {
            LayoutDoc.validateId(id);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return !LayoutStorage.exists(id);
    }

    /**
     * Slugify {@code displayName} into a layout id ({@code [a-z0-9_-]} only, ≤32 chars), then deduplicate by appending
     * {@code _2}, {@code _3}, … until an unused id is found. The base slug is empty-checked: an unslugifiable name (all
     * punctuation, etc.) falls back to {@code "layout"}.
     */
    public static String suggestId(String displayName) {
        var base = slugify(displayName);
        if (base.isEmpty()) {
            base = "layout";
        }
        if (base.length() > 32) {
            base = base.substring(0, 32);
        }
        if (idAvailable(base)) {
            return base;
        }
        for (var i = 2; i < Integer.MAX_VALUE; i++) {
            var suffix = "_" + i;
            var maxBaseLen = Math.max(1, 32 - suffix.length());
            var trimmed = base.length() > maxBaseLen ? base.substring(0, maxBaseLen) : base;
            var candidate = trimmed + suffix;
            if (idAvailable(candidate)) {
                return candidate;
            }
        }
        // Should never reach here in practice — fall back to base + timestamp as a last resort.
        return (base + "_" + System.currentTimeMillis()).substring(0, Math.min(32, base.length() + 14));
    }

    private static String slugify(String s) {
        if (s == null) {
            return "";
        }
        var lower = s.toLowerCase(Locale.ROOT);
        var sb = new StringBuilder(lower.length());
        var lastWasUnderscore = false;
        for (var i = 0; i < lower.length(); i++) {
            var ch = lower.charAt(i);
            var ok = (ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9') || ch == '_' || ch == '-';
            if (ok) {
                sb.append(ch);
                lastWasUnderscore = (ch == '_');
            } else if (!lastWasUnderscore) {
                sb.append('_');
                lastWasUnderscore = true;
            }
        }
        // Trim leading/trailing underscores so we don't generate ids like "_foo_".
        var slug = sb.toString();
        var start = 0;
        while (start < slug.length() && (slug.charAt(start) == '_' || slug.charAt(start) == '-')) {
            start++;
        }
        var end = slug.length();
        while (end > start && (slug.charAt(end - 1) == '_' || slug.charAt(end - 1) == '-')) {
            end--;
        }
        return slug.substring(start, end);
    }
}
