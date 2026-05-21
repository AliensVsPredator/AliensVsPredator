package com.blib.api.common.dismemberment.v1;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;
import java.util.Optional;

/**
 * One authorable visual transform for a detached limb.
 */
public record LimbPose(
    String id,
    int weight,
    Vec3 renderOffset,
    Vec3 renderRotation,
    Vec3 renderScale,
    Vec3 renderPivot,
    boolean modelerTransform
) {

    public static final String DEFAULT_ID = "default";

    public static final int DEFAULT_WEIGHT = 1;

    public LimbPose(String id, Vec3 renderOffset, Vec3 renderRotation, Vec3 renderScale, Vec3 renderPivot, boolean modelerTransform) {
        this(id, DEFAULT_WEIGHT, renderOffset, renderRotation, renderScale, renderPivot, modelerTransform);
    }

    public LimbPose {
        Objects.requireNonNull(id, "LimbPose id must not be null");
        Objects.requireNonNull(renderOffset, "LimbPose renderOffset must not be null");
        Objects.requireNonNull(renderRotation, "LimbPose renderRotation must not be null");
        Objects.requireNonNull(renderScale, "LimbPose renderScale must not be null");
        Objects.requireNonNull(renderPivot, "LimbPose renderPivot must not be null");
        if (id.isBlank()) {
            throw new IllegalArgumentException("LimbPose id must not be blank");
        }
        if (weight < 0) {
            throw new IllegalArgumentException("LimbPose weight must be non-negative");
        }
    }

    public boolean selectable() {
        return weight > 0;
    }

    public static final Codec<LimbPose> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.STRING.fieldOf("id").forGetter(LimbPose::id),
            Codec.INT.optionalFieldOf("weight", DEFAULT_WEIGHT).forGetter(LimbPose::weight),
            Vec3.CODEC.optionalFieldOf("render_offset", Vec3.ZERO).forGetter(LimbPose::renderOffset),
            Vec3.CODEC.optionalFieldOf("render_rotation", Vec3.ZERO).forGetter(LimbPose::renderRotation),
            Vec3.CODEC.optionalFieldOf("render_scale", LimbVisuals.DEFAULT_SCALE).forGetter(LimbPose::renderScale),
            Vec3.CODEC
                .optionalFieldOf("render_pivot")
                .forGetter(pose -> pose.modelerTransform() ? Optional.of(pose.renderPivot()) : Optional.empty())
        )
            .apply(
                instance,
                (id, weight, renderOffset, renderRotation, renderScale, renderPivot) -> new LimbPose(
                    id,
                    weight,
                    renderOffset,
                    renderRotation,
                    renderScale,
                    renderPivot.orElse(LimbVisuals.DEFAULT_PIVOT),
                    renderPivot.isPresent()
                )
            )
    );
}
