package com.blib.engine.modeler;

import org.jetbrains.annotations.ApiStatus;

/**
 * Sealed type representing what's currently selected in the modeler — either a {@link ModelerBone} or a
 * {@link ModelerCube}. Empty (no selection) is represented by {@code null} on the scene; this type is non-null when
 * something is selected.
 */
@ApiStatus.Internal
public sealed interface Selection {

    record BoneSelection(ModelerBone bone) implements Selection {}

    record CubeSelection(
        ModelerBone owner,
        ModelerCube cube
    ) implements Selection {}
}
