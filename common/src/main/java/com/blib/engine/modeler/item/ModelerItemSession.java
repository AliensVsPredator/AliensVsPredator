package com.blib.engine.modeler.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.api.client.render.v1.item.BLibItemTransformMode;
import com.blib.engine.modeler.ModelerBone;

/**
 * Modeler-side state for "I'm authoring item transforms for {@link #itemId} right now". Attached to
 * {@link com.blib.engine.modeler.ModelerScene} as a nullable field — when null, the Modeler is in its default
 * entity-edit mode; when non-null, the Inspector exposes the item-config section and the Viewport gains a "Preview as"
 * picker.
 * <p>
 * No transform data lives here — edits write straight into {@link com.blib.engine.gizmo.BLibItemTransformOverrides} so
 * the live game world and the Modeler viewport read from the same source on the next frame. This object only tracks
 * which fields the Inspector is currently editing and which context the Viewport is currently previewing.
 */
@ApiStatus.Internal
public final class ModelerItemSession {

    /**
     * Item being authored — must exist in {@link com.blib.engine.gizmo.BLibItemTransformOverrides#registeredItemIds()}.
     */
    public final ResourceLocation itemId;

    /** Which transform set the Inspector vec3 fields target. Toggled via the Inspector mode button. */
    public BLibItemTransformMode mode = BLibItemTransformMode.IDLE;

    /** Which {@link ItemDisplayContext} the Inspector vec3 fields target. */
    public ItemDisplayContext editingContext = ItemDisplayContext.GUI;

    /**
     * When true, the Inspector edits the wall-fixed slot via
     * {@link com.blib.engine.gizmo.BLibItemTransformOverrides#setWallFixed} / {@code getEffectiveWallFixed} instead of
     * the regular {@link ItemDisplayContext#FIXED} slot. Only meaningful while {@link #editingContext} is
     * {@code FIXED}.
     */
    public boolean wallFixedActive;

    /**
     * What the Viewport renders. {@code null} means "edit mode" — show the geo with cubes/gizmos/UV editing. Non-null
     * means "preview mode" — invoke vanilla's actual {@code ItemRenderer} path for this context so the preview is
     * pixel-identical to the in-game render. The wall-FIXED variant is selected by combining {@code FIXED} with
     * {@link #previewWallFixed}.
     */
    public @Nullable ItemDisplayContext previewContext;

    /** Pair with {@link #previewContext == FIXED} to render the wall-mounted variant. */
    public boolean previewWallFixed;

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
