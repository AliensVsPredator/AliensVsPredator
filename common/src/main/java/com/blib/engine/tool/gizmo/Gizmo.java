package com.blib.engine.tool.gizmo;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.engine.session.EngineSession;

/**
 * Polymorphic contract over the engine's selection gizmos. Prior to this interface every gizmo
 * ({@code BlockSelectionScaleGizmo}, {@code BlockSelectionTranslateGizmo}, {@code MoveBlocksGizmo},
 * {@code EntityTranslateGizmo}, {@code EntityScaleGizmo}) exposed identical-shape static methods with no shared type,
 * so the viewport dispatcher used hardcoded {@code instanceof} checks to route input. Implementations of this interface
 * wrap the static classes, allowing the dispatcher to iterate a registry and pick the closest hit by ray-distance.
 * <p>
 * Subtype {@code H} is the gizmo's hit handle — {@code FaceHit}, {@code AxisHit}, etc. The dispatcher only needs the
 * generic {@link GizmoHit#t() distance}, but begin/update/commit-time the concrete gizmo needs the handle, so the type
 * is preserved at the interface level.
 */
@ApiStatus.Internal
public interface Gizmo<H extends GizmoHit> {

    /** Stable id used for registry lookup and serialization. Lowercase snake_case. */
    String id();

    /** True while an interactive drag is in progress for this gizmo. */
    boolean isDragging();

    /** Probe the gizmo for a hit under the cursor ray; returns null when nothing is hit. */
    @Nullable
    H hitTest(EngineSession session);

    /** Begin a drag against the given handle. The session provides the camera + cursor ray for plane-pick setup. */
    void beginDrag(H hit, EngineSession session);

    /** Update an in-progress drag given the latest cursor ray. */
    void updateDrag(EngineSession session, Vec3 cursorRayDir);

    /** End the current drag. Implementations route the final value through their target / command bus. */
    void endDrag();

    /** Force-clear hover and drag state (e.g. on workspace close or ESC). */
    void clear();

    /** Update hover state from the most recent {@link #hitTest hitTest}, decoupling picking from rendering. */
    void setHovered(@Nullable H hit);
}
