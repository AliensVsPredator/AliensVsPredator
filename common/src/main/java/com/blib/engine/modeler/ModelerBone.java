package com.blib.engine.modeler;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Mutable bone draft inside the modeler. Mirrors the data shape of {@code AzBone} (the runtime geo bone) but holds
 * editable values rather than baked matrices. Walked top-down by the renderer to apply transforms (translate to bone →
 * translate to pivot → rotate → scale → translate away from pivot) per
 * {@code com.blib.api.client.render.v1.RenderUtil#prepMatrixForBone}.
 */
@ApiStatus.Internal
public final class ModelerBone {

    public String name;

    /** Bone-space translation applied before pivot manipulation. */
    public Vec3 position;

    /** Authored Euler rotation in degrees applied around {@link #pivot}, Z-Y-X order. */
    public Vec3 rotation;

    /** Uniform-ish scale; defaults to (1,1,1). */
    public Vec3 scale;

    /** Pivot point in bone-local pixel coordinates — the centre of rotation. */
    public Vec3 pivot;

    public final List<ModelerBone> children = new ArrayList<>();

    public final List<ModelerCube> cubes = new ArrayList<>();

    /** Back-reference to the parent bone, or {@code null} for the implicit root. */
    public @Nullable ModelerBone parent;

    public ModelerBone(String name) {
        this(name, Vec3.ZERO, Vec3.ZERO, new Vec3(1, 1, 1), Vec3.ZERO);
    }

    public ModelerBone(String name, Vec3 position, Vec3 rotation, Vec3 scale, Vec3 pivot) {
        this.name = name;
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
        this.pivot = pivot;
    }

    public void addChild(ModelerBone child) {
        child.parent = this;
        children.add(child);
    }
}
