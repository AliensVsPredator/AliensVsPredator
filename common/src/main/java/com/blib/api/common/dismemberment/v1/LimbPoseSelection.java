package com.blib.api.common.dismemberment.v1;

import net.minecraft.util.RandomSource;

import java.util.Objects;

/**
 * Caller-selected pose policy for a newly detached limb.
 */
public record LimbPoseSelection(
    Kind kind,
    String poseId
) {

    private static final LimbPoseSelection RANDOM = new LimbPoseSelection(Kind.RANDOM, LimbPose.DEFAULT_ID);

    private static final LimbPoseSelection DEFAULT = new LimbPoseSelection(Kind.DEFAULT, LimbPose.DEFAULT_ID);

    public enum Kind {
        RANDOM,
        DEFAULT,
        SPECIFIC
    }

    public LimbPoseSelection {
        Objects.requireNonNull(kind, "LimbPoseSelection kind must not be null");
        poseId = poseId == null ? "" : poseId.trim();
        if (kind == Kind.SPECIFIC && poseId.isBlank()) {
            throw new IllegalArgumentException("Specific limb pose id must not be blank");
        }
        if (kind != Kind.SPECIFIC) {
            poseId = LimbPose.DEFAULT_ID;
        }
    }

    public static LimbPoseSelection random() {
        return RANDOM;
    }

    public static LimbPoseSelection defaultPose() {
        return DEFAULT;
    }

    public static LimbPoseSelection specific(String poseId) {
        var normalized = Objects.requireNonNull(poseId, "poseId").trim();
        if (LimbPose.DEFAULT_ID.equals(normalized)) {
            return DEFAULT;
        }
        return new LimbPoseSelection(Kind.SPECIFIC, normalized);
    }

    public String resolve(LimbDefinition definition, RandomSource random) {
        return switch (kind) {
            case RANDOM -> definition.selectRandomPoseId(random);
            case DEFAULT -> LimbPose.DEFAULT_ID;
            case SPECIFIC -> poseId;
        };
    }
}
