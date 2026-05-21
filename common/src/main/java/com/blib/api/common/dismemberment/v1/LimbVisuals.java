package com.blib.api.common.dismemberment.v1;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Client-only visual description of a detachable limb. The structural counterpart is {@link LimbDefinition} (server-
 * authoritative). The two halves are paired by {@link LimbDefinition#id()} — a typo on either side leaves the limb
 * either logically present with no visuals (renderer skips) or visually defined but never spawned (dead data).
 * <p>
 * Loaded from {@code assets/<ns>/blib_limb_visuals/<entity_path>.json} by {@code LimbVisualsLoader}. The Java
 * {@link LimbDefinition.Builder} also writes here for in-code limb registrations; JSON entries override Java entries by
 * limb id (tier-2 over tier-1) inside {@link LimbVisualsRegistry}.
 */
public record LimbVisuals(
    String rootBoneName,
    List<String> companionBoneNames,
    Vec3 renderOffset,
    Vec3 renderRotation,
    Vec3 renderScale,
    Vec3 renderPivot,
    boolean modelerTransform,
    List<LimbPose> poses
) {

    public static final Vec3 DEFAULT_SCALE = new Vec3(1.0, 1.0, 1.0);

    public static final Vec3 DEFAULT_PIVOT = Vec3.ZERO;

    public LimbVisuals(
        String rootBoneName,
        List<String> companionBoneNames,
        Vec3 renderOffset,
        Vec3 renderRotation,
        Vec3 renderScale
    ) {
        this(rootBoneName, companionBoneNames, renderOffset, renderRotation, renderScale, DEFAULT_PIVOT, false);
    }

    public LimbVisuals(
        String rootBoneName,
        List<String> companionBoneNames,
        Vec3 renderOffset,
        Vec3 renderRotation,
        Vec3 renderScale,
        Vec3 renderPivot,
        boolean modelerTransform
    ) {
        this(rootBoneName, companionBoneNames, renderOffset, renderRotation, renderScale, renderPivot, modelerTransform, List.of());
    }

    public LimbVisuals {
        Objects.requireNonNull(rootBoneName, "LimbVisuals rootBoneName must not be null");
        Objects.requireNonNull(companionBoneNames, "LimbVisuals companionBoneNames must not be null");
        Objects.requireNonNull(renderOffset, "LimbVisuals renderOffset must not be null");
        Objects.requireNonNull(renderRotation, "LimbVisuals renderRotation must not be null");
        Objects.requireNonNull(renderScale, "LimbVisuals renderScale must not be null");
        Objects.requireNonNull(renderPivot, "LimbVisuals renderPivot must not be null");
        Objects.requireNonNull(poses, "LimbVisuals poses must not be null");

        if (rootBoneName.isBlank()) {
            throw new IllegalArgumentException("LimbVisuals rootBoneName must not be blank");
        }

        companionBoneNames = List.copyOf(companionBoneNames);
        poses = List.copyOf(poses);

        Set<String> poseIds = new HashSet<>();
        for (var pose : poses) {
            Objects.requireNonNull(pose, "LimbVisuals pose must not be null");
            if (LimbPose.DEFAULT_ID.equals(pose.id())) {
                throw new IllegalArgumentException("LimbVisuals poses must not include the implicit default pose");
            }
            if (!poseIds.add(pose.id())) {
                throw new IllegalArgumentException("Duplicate LimbVisuals pose id: " + pose.id());
            }
        }
    }

    public LimbPose defaultPose() {
        return new LimbPose(
            LimbPose.DEFAULT_ID,
            LimbPose.DEFAULT_WEIGHT,
            renderOffset,
            renderRotation,
            renderScale,
            renderPivot,
            modelerTransform
        );
    }

    public List<LimbPose> allPoses() {
        var out = new ArrayList<LimbPose>(poses.size() + 1);
        out.add(defaultPose());
        out.addAll(poses);
        return List.copyOf(out);
    }

    public LimbPose poseOrDefault(String poseId) {
        if (poseId != null && !poseId.isBlank()) {
            if (LimbPose.DEFAULT_ID.equals(poseId)) {
                return defaultPose();
            }
            for (var pose : poses) {
                if (pose.id().equals(poseId)) {
                    return pose;
                }
            }
        }
        return defaultPose();
    }

    public static final Codec<LimbVisuals> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.STRING.fieldOf("root_bone").forGetter(LimbVisuals::rootBoneName),
            Codec.STRING.listOf().optionalFieldOf("companion_bones", List.of()).forGetter(LimbVisuals::companionBoneNames),
            Vec3.CODEC.optionalFieldOf("render_offset", Vec3.ZERO).forGetter(LimbVisuals::renderOffset),
            Vec3.CODEC.optionalFieldOf("render_rotation", Vec3.ZERO).forGetter(LimbVisuals::renderRotation),
            Vec3.CODEC.optionalFieldOf("render_scale", DEFAULT_SCALE).forGetter(LimbVisuals::renderScale),
            Vec3.CODEC
                .optionalFieldOf("render_pivot")
                .forGetter(visuals -> visuals.modelerTransform() ? Optional.of(visuals.renderPivot()) : Optional.empty()),
            LimbPose.CODEC.listOf().optionalFieldOf("poses", List.of()).forGetter(LimbVisuals::poses)
        )
            .apply(
                instance,
                (rootBoneName, companionBoneNames, renderOffset, renderRotation, renderScale, renderPivot, poses) -> new LimbVisuals(
                    rootBoneName,
                    companionBoneNames,
                    renderOffset,
                    renderRotation,
                    renderScale,
                    renderPivot.orElse(DEFAULT_PIVOT),
                    renderPivot.isPresent(),
                    poses
                )
            )
    );
}
