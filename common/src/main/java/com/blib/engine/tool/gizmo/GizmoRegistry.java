package com.blib.engine.tool.gizmo;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.blib.engine.session.EngineSession;

/**
 * Holds the currently-active gizmos and answers "which one is the cursor hovering?" without forcing the viewport panel
 * to {@code instanceof}-check selection types. The viewport calls {@link #pickClosest} each frame; the entry with the
 * smallest ray-distance wins. Implementations register themselves opportunistically — the registry is intentionally a
 * flat list rather than a typed map so the same instance can handle different selection types via the gizmo's own
 * applicability checks.
 * <p>
 * The registry does not own state; it's a thin coordination layer. Mode-switching (translate vs scale vs move-blocks)
 * still lives in the existing {@code BlockSelection.gizmoMode()} / {@code EntityGizmoMode} singletons that decide which
 * gizmo to register for the current selection.
 */
@ApiStatus.Internal
public final class GizmoRegistry {

    private static final List<Gizmo<?>> ACTIVE = new ArrayList<>();

    private GizmoRegistry() {}

    public static synchronized void register(Gizmo<?> gizmo) {
        if (!ACTIVE.contains(gizmo)) {
            ACTIVE.add(gizmo);
        }
    }

    public static synchronized void unregister(Gizmo<?> gizmo) {
        ACTIVE.remove(gizmo);
    }

    public static synchronized List<Gizmo<?>> active() {
        return Collections.unmodifiableList(new ArrayList<>(ACTIVE));
    }

    /** Returns the {@link Gizmo} whose hit-test reports the closest ray distance under the cursor. */
    public static synchronized @Nullable Hit pickClosest(EngineSession session) {
        Gizmo<?> bestGizmo = null;
        GizmoHit bestHit = null;
        var bestT = Double.POSITIVE_INFINITY;
        for (var g : ACTIVE) {
            var h = g.hitTest(session);
            if (h != null && h.t() > 0 && h.t() < bestT) {
                bestT = h.t();
                bestGizmo = g;
                bestHit = h;
            }
        }
        return bestGizmo == null ? null : new Hit(bestGizmo, bestHit);
    }

    /** Clear hover state on every registered gizmo. Called by callers that just resolved a non-gizmo hit. */
    public static synchronized void clearAllHover() {
        for (var g : ACTIVE) {
            clearHoverErased(g);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static void clearHoverErased(Gizmo<?> g) {
        ((Gizmo) g).setHovered(null);
    }

    /** Wraps a winning gizmo + its hit handle. The hit is type-erased; callers route via the gizmo's id. */
    public record Hit(
        Gizmo<?> gizmo,
        GizmoHit hit
    ) {}
}
