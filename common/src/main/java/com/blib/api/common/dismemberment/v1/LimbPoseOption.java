package com.blib.api.common.dismemberment.v1;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Objects;

/**
 * Server-side pose selection metadata for a detachable limb.
 * <p>
 * The visual transform for the pose lives in {@link LimbVisuals}; this record only tells the server which authored
 * pose ids are eligible for random selection and how heavily each one should be weighted.
 */
public record LimbPoseOption(
    String id,
    int weight
) {

    public LimbPoseOption {
        Objects.requireNonNull(id, "LimbPoseOption id must not be null");
        if (id.isBlank()) {
            throw new IllegalArgumentException("LimbPoseOption id must not be blank");
        }
        if (weight < 0) {
            throw new IllegalArgumentException("LimbPoseOption weight must be non-negative");
        }
    }

    public LimbPoseOption(String id) {
        this(id, LimbPose.DEFAULT_WEIGHT);
    }

    public boolean selectable() {
        return weight > 0;
    }

    public static LimbPoseOption fromPose(LimbPose pose) {
        Objects.requireNonNull(pose, "pose");
        return new LimbPoseOption(pose.id(), pose.weight());
    }

    public static final Codec<LimbPoseOption> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.STRING.fieldOf("id").forGetter(LimbPoseOption::id),
            Codec.INT.optionalFieldOf("weight", LimbPose.DEFAULT_WEIGHT).forGetter(LimbPoseOption::weight)
        ).apply(instance, LimbPoseOption::new)
    );
}
