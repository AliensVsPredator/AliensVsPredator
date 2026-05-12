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

    public ModelerCube(String name, Vec3 origin, Vec3 size, Vec3 rotation, Vec3 pivot, double inflate) {
        this.name = name;
        this.origin = origin;
        this.size = size;
        this.rotation = rotation;
        this.pivot = pivot;
        this.inflate = inflate;
    }

    public static ModelerCube defaultCube(String name) {
        return new ModelerCube(name, new Vec3(0, 0, 0), new Vec3(8, 8, 8), Vec3.ZERO, new Vec3(0, 0, 0), 0.0);
    }
}
