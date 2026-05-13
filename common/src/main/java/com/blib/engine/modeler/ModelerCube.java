package com.blib.engine.modeler;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;

/**
 * Mutable cube draft inside the in-engine modeler. Field set mirrors {@code com.blib.internal.client.model.Cube} (the
 * JSON-side DTO) so {@code ModelerSceneLoader} can map 1:1 when importing existing models.
 * <p>
 * All measurements are in model-space pixels (Bedrock convention: 16 pixels = 1 block).
 */
@ApiStatus.Internal
public final class ModelerCube {

    public String name;

    /** Minimum corner of the cube in bone-local pixel coordinates. */
    public Vec3 origin;

    /** Width / height / depth in pixels. */
    public Vec3 size;

    /** Authored Euler rotation in degrees around {@link #pivot}, applied Z-Y-X. */
    public Vec3 rotation;

    /** Rotation pivot in bone-local pixel coordinates. */
    public Vec3 pivot;

    /** Outward padding in pixels. Inflates the rendered geometry symmetrically without changing origin/size. */
    public double inflate;

    /**
     * Box-UV origin U coordinate in texture pixels. The full six-face unwrap is derived from this point plus the cube's
     * size — see {@code AzBakedModelFactory.buildQuad} (Direction.WEST/EAST/...) for the canonical layout. Ignored when
     * the source cube authored per-face UVs (v1 only edits box UVs).
     */
    public double uvOriginU;

    /** Box-UV origin V coordinate in texture pixels. See {@link #uvOriginU}. */
    public double uvOriginV;

    /**
     * Set when the source model authored per-face UVs ({@code "uv": {"north": ..., "south": ...}}) instead of a single
     * box origin. Surfaces in the UV map panel as a "not editable in v1" hint — those cubes still load at UV (0, 0)
     * (since v1 only edits box UVs) and stack visibly at the texture origin; the flag lets us distinguish them from
     * cubes that legitimately use a (0, 0) box origin.
     */
    public boolean hasPerFaceUv;

    public ModelerCube(String name, Vec3 origin, Vec3 size, Vec3 rotation, Vec3 pivot, double inflate) {
        this.name = name;
        this.origin = origin;
        this.size = size;
        this.rotation = rotation;
        this.pivot = pivot;
        this.inflate = inflate;
        this.uvOriginU = 0.0;
        this.uvOriginV = 0.0;
        this.hasPerFaceUv = false;
    }

    public static ModelerCube defaultCube(String name) {
        return new ModelerCube(name, new Vec3(0, 0, 0), new Vec3(8, 8, 8), Vec3.ZERO, new Vec3(0, 0, 0), 0.0);
    }
}
