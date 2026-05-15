package com.blib.engine.modeler;

import org.jetbrains.annotations.ApiStatus;

import java.util.List;

/**
 * Sealed type representing what's currently selected in the modeler — a {@link ModelerBone}, a single
 * {@link ModelerCube}, a cube face, or a multi-cube selection (e.g. from the UV map's marquee). Empty (no selection)
 * is represented by {@code null} on the scene; this type is non-null when something is selected.
 */
@ApiStatus.Internal
public sealed interface Selection {

    record BoneSelection(ModelerBone bone) implements Selection {}

    record CubeSelection(
        ModelerBone owner,
        ModelerCube cube
    ) implements Selection {}

    /**
     * A single cube face selected from either the 3D viewport or UV map. Tools that do not have face-level behavior
     * should treat this as a cube selection for {@link #cube}; face-aware panels can use {@link #face} to show or edit
     * the exact UV island.
     */
    record FaceSelection(
        ModelerBone owner,
        ModelerCube cube,
        ModelerCube.Face face
    ) implements Selection {}

    /**
     * Two-or-more cube selection. The last entry in {@link #cubes} is the <em>primary</em> — the cube that single-cube
     * tools (the inspector, the gizmo) operate on. Renderers and the outliner iterate the full list so every selected
     * cube reads as selected. Single selections always use {@link CubeSelection}; this variant is reserved for
     * {@code cubes.size() >= 2}.
     */
    record MultiCubeSelection(List<CubeSelection> cubes) implements Selection {

        public MultiCubeSelection {
            if (cubes.size() < 2) {
                throw new IllegalArgumentException("MultiCubeSelection requires at least two cubes; use CubeSelection for one");
            }
            cubes = List.copyOf(cubes);
        }

        public CubeSelection primary() {
            return cubes.get(cubes.size() - 1);
        }

        public boolean contains(ModelerCube cube) {
            for (var cs : cubes) {
                if (cs.cube() == cube) {
                    return true;
                }
            }
            return false;
        }
    }
}
