package com.blib.api.client.engine.v1.inspector;

import com.blib.engine.ui.panel.base.PublicInspectorSectionAdapter;

/**
 * Entry point for downstream mods to contribute {@link InspectorSection}s into the engine workspace's Inspector panel.
 * Sections registered here render after BLib's own built-in sections for the same selection type, in registration
 * order. Registration is process-wide and should happen during client init.
 * <p>
 * Example:
 * <pre>
 *   InspectorSectionRegistry.register(new MyHiveLocationInspectorSection());
 * </pre>
 */
public final class InspectorSectionRegistry {

    private InspectorSectionRegistry() {}

    /**
     * Register a section. The {@link InspectorSection#selectableType()} declares which selection class triggers
     * the section; the engine routes to all matching sections in registration order. Calling this with a section
     * targeting an unsupported selection type is allowed but the section will simply never render.
     */
    public static void register(InspectorSection<?> section) {
        com.blib.engine.ui.panel.base.InspectorSectionRegistry.register(PublicInspectorSectionAdapter.wrap(section));
    }
}
