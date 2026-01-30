package com.blib.internal.client.model;

import net.minecraft.world.phys.Vec3;

public record GeoCube(
    GeoQuad[] quads,
    Vec3 pivot,
    Vec3 rotation,
    Vec3 size,
    double inflate,
    boolean mirror
) {}
