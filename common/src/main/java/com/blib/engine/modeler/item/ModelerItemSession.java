package com.blib.engine.modeler.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.ApiStatus;

import com.blib.api.client.render.v1.item.BLibItemTransformMode;
import com.blib.engine.modeler.ModelerBone;

/**
 * Modeler-side state for "I'm authoring item transforms for {@link #itemId} right now". Attached to
 * {@link com.blib.engine.modeler.ModelerScene} as a nullable field — when null, the Modeler is in its default
 * entity-edit mode; when non-null, the Inspector exposes the item-config section and the Viewport renders the item via
 * vanilla's {@code ItemRenderer} for the current {@link #editingContext}.
 * <p>
 * No transform data lives here — edits write straight into {@link com.blib.engine.gizmo.BLibItemTransformOverrides} so
 * the live game world and the Modeler viewport read from the same source on the next frame. This object only tracks
 * which fields the Inspector is currently editing (= which context the Viewport currently renders).
 */
@ApiStatus.Internal
public final class ModelerItemSession {

    /**
     * Item being authored — must exist in {@link com.blib.engine.gizmo.BLibItemTransformOverrides#registeredItemIds()}.
     */
    public final ResourceLocation itemId;

    /** Which transform set the Inspector vec3 fields target. Toggled via the Inspector mode button. */
    public BLibItemTransformMode mode = BLibItemTransformMode.IDLE;

    /**
     * Which {@link ItemDisplayContext} the Inspector vec3 fields target. Also drives what the Viewport renders — one
     * picker controls both the edit slot and the preview pose so the user can't end up dragging in a context they don't
     * see.
     */
    public ItemDisplayContext editingContext = ItemDisplayContext.GUI;

    /**
     * When true, the Inspector edits the wall-fixed slot via
     * {@link com.blib.engine.gizmo.BLibItemTransformOverrides#setWallFixed} / {@code getEffectiveWallFixed} instead of
     * the regular {@link ItemDisplayContext#FIXED} slot. Only meaningful while {@link #editingContext} is
     * {@code FIXED}.
     */
    public boolean wallFixedActive;

    /**
     * Synthetic {@link ModelerBone} whose mutable fields ({@code position} / {@code rotation} / {@code pivot} /
     * {@code scale}) mirror the currently-edited {@link com.blib.api.client.render.v1.BLibTransform}. The modeler's
     * existing bone-gizmo paths (TRANSLATE/ROTATE/PIVOT/SCALE) mutate these fields in place; a per-frame sync copies
     * them back into {@link com.blib.engine.gizmo.BLibItemTransformOverrides} so the live render reads the user's
     * edits. Lazily allocated so identity stays stable across frames (the gizmo's {@code DragState} captures a
     * reference; recreating per-frame would break in-flight drags).
     */
    public final ModelerBone gizmoShimBone = new ModelerBone("__item_transform_gizmo__");

    public ModelerItemSession(ResourceLocation itemId) {
        this.itemId = itemId;
    }
}
