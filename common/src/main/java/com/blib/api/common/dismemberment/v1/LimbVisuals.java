package com.blib.api.common.dismemberment.v1;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Objects;

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
    Vec3 renderScale
) {

    public static final Vec3 DEFAULT_SCALE = new Vec3(1.0, 1.0, 1.0);

    public LimbVisuals {
        Objects.requireNonNull(rootBoneName, "LimbVisuals rootBoneName must not be null");
        Objects.requireNonNull(companionBoneNames, "LimbVisuals companionBoneNames must not be null");
        Objects.requireNonNull(renderOffset, "LimbVisuals renderOffset must not be null");
        Objects.requireNonNull(renderRotation, "LimbVisuals renderRotation must not be null");
        Objects.requireNonNull(renderScale, "LimbVisuals renderScale must not be null");

        if (rootBoneName.isBlank()) {
            throw new IllegalArgumentException("LimbVisuals rootBoneName must not be blank");
        }

        companionBoneNames = List.copyOf(companionBoneNames);
    }

    public static final Codec<LimbVisuals> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.STRING.fieldOf("root_bone").forGetter(LimbVisuals::rootBoneName),
            Codec.STRING.listOf().optionalFieldOf("companion_bones", List.of()).forGetter(LimbVisuals::companionBoneNames),
            Vec3.CODEC.optionalFieldOf("render_offset", Vec3.ZERO).forGetter(LimbVisuals::renderOffset),
            Vec3.CODEC.optionalFieldOf("render_rotation", Vec3.ZERO).forGetter(LimbVisuals::renderRotation),
            Vec3.CODEC.optionalFieldOf("render_scale", DEFAULT_SCALE).forGetter(LimbVisuals::renderScale)
        ).apply(instance, LimbVisuals::new)
    );
}
