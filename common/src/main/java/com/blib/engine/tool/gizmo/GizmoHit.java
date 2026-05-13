package com.blib.engine.tool.gizmo;

import org.jetbrains.annotations.ApiStatus;

/**
 * A hit produced by {@link Gizmo#hitTest}. Carries the ray-distance so callers can compare across gizmos and pick the
 * closest. Specific gizmos return subtypes that also carry the hit handle (axis, face, etc.).
 */
@ApiStatus.Internal
public interface GizmoHit {

    /** Parametric distance along the cursor ray. Lower = closer to the camera. */
    double t();
}
