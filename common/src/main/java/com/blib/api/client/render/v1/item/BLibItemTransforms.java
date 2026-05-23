package com.blib.api.client.render.v1.item;

import com.mojang.serialization.Codec;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.blib.api.client.render.v1.BLibTransform;

/**
 * Collection of {@link BLibTransform}s keyed by {@link ItemDisplayContext}, used by a {@link BLibGeoBoneItemRenderer}
 * to position its rendered bone differently in each render state (GUI, first-person hands, third-person hands, ground
 * entity, head slot, etc.). Lookup falls back to {@link BLibTransform#IDENTITY} when a context isn't configured.
 */
public class BLibItemTransforms {

    /**
     * JSON shape matches the vanilla item-model {@code display} block (snake-case keys like
     * {@code thirdperson_righthand} — sourced from {@link ItemDisplayContext#getSerializedName()}). The
     * {@code fixed_wall} and {@code fixed_ground} keys are BLib extensions that route to {@link #fixedWall} and
     * {@link #fixedGround}. Empty maps decode to a builder-default (identity) transform set.
     */
    public static final Codec<BLibItemTransforms> CODEC = Codec.unboundedMap(Codec.STRING, BLibTransform.CODEC)
        .xmap(BLibItemTransforms::fromSerializedMap, BLibItemTransforms::toSerializedMap);

    private static final String FIXED_WALL_KEY = "fixed_wall";

    private static final String FIXED_GROUND_KEY = "fixed_ground";

    private final Map<ItemDisplayContext, BLibTransform> transforms;

    private final @Nullable BLibTransform fixedWall;

    private final @Nullable BLibTransform fixedGround;

    protected BLibItemTransforms(Map<ItemDisplayContext, BLibTransform> transforms) {
        this(transforms, null, null);
    }

    protected BLibItemTransforms(Map<ItemDisplayContext, BLibTransform> transforms, @Nullable BLibTransform fixedWall) {
        this(transforms, fixedWall, null);
    }

    protected BLibItemTransforms(
        Map<ItemDisplayContext, BLibTransform> transforms,
        @Nullable BLibTransform fixedWall,
        @Nullable BLibTransform fixedGround
    ) {
        this.transforms = transforms;
        this.fixedWall = fixedWall;
        this.fixedGround = fixedGround;
    }

    public BLibTransform get(ItemDisplayContext context) {
        return transforms.getOrDefault(context, BLibTransform.IDENTITY);
    }

    /**
     * Like {@link #get} but returns {@code null} when no transform was explicitly set for this context, so callers can
     * distinguish "user explicitly set identity" from "user did not set this context." Used by the cascade in
     * {@link BLibGeoBoneItemRenderer} that falls blocking transforms through to idle for any unset context.
     */
    public @Nullable BLibTransform getOrNull(ItemDisplayContext context) {
        return transforms.get(context);
    }

    /**
     * The transform to use when this item is being rendered as a wall-mounted block (e.g., a queen head placed on a
     * wall, as opposed to on the floor). Returns {@code null} if no wall-specific transform was set — callers should
     * fall back to the regular {@link ItemDisplayContext#FIXED} transform in that case.
     * <p>
     * Wall placement and floor placement of the same head produce visually distinct poses (head sitting upright vs head
     * hanging off the wall), and trying to derive one from the other via a single rotation always sweeps around the
     * bone pivot rather than the wall surface. A separate slot lets each pose be tuned independently with its own
     * translation/rotation/scale/pivot.
     */
    public @Nullable BLibTransform getFixedWallOrNull() {
        return fixedWall;
    }

    /**
     * The transform to use when this item is being rendered as a floor-placed fixed block. Returns {@code null} if no
     * ground-specific transform was set — callers should fall back to the regular {@link ItemDisplayContext#FIXED}
     * transform in that case.
     */
    public @Nullable BLibTransform getFixedGroundOrNull() {
        return fixedGround;
    }

    /**
     * Decode a vanilla-style display-block map (keys = {@link ItemDisplayContext#getSerializedName()} plus the BLib
     * {@code fixed_wall}/{@code fixed_ground} extensions) into a {@link BLibItemTransforms}. Unknown keys are ignored
     * — vanilla's display block is small but stable, and silently skipping unrecognized perspectives keeps forward
     * compatibility cheap.
     */
    private static BLibItemTransforms fromSerializedMap(Map<String, BLibTransform> map) {
        var builder = builder();

        for (var entry : map.entrySet()) {
            var key = entry.getKey();
            var transform = entry.getValue();

            if (FIXED_WALL_KEY.equals(key)) {
                builder.fixedWall(transform);
                continue;
            }

            if (FIXED_GROUND_KEY.equals(key)) {
                builder.fixedGround(transform);
                continue;
            }

            for (var ctx : ItemDisplayContext.values()) {
                if (ctx.getSerializedName().equals(key)) {
                    builder.set(ctx, transform);
                    break;
                }
            }
        }

        return builder.build();
    }

    /**
     * Inverse of {@link #fromSerializedMap}. Emits one entry per context with a non-null transform, plus optional wall.
     */
    private static Map<String, BLibTransform> toSerializedMap(BLibItemTransforms transforms) {
        var out = new LinkedHashMap<String, BLibTransform>();

        for (var ctx : ItemDisplayContext.values()) {
            var t = transforms.getOrNull(ctx);

            if (t != null) {
                out.put(ctx.getSerializedName(), t);
            }
        }

        if (transforms.fixedWall != null) {
            out.put(FIXED_WALL_KEY, transforms.fixedWall);
        }

        if (transforms.fixedGround != null) {
            out.put(FIXED_GROUND_KEY, transforms.fixedGround);
        }

        return out;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private final Map<ItemDisplayContext, BLibTransform> transforms = new EnumMap<>(ItemDisplayContext.class);

        private boolean mirrorFirstPerson = false;

        private boolean mirrorThirdPerson = false;

        private @Nullable BLibTransform fixedWall = null;

        private @Nullable BLibTransform fixedGround = null;

        private Builder() {}

        public Builder set(ItemDisplayContext context, BLibTransform transform) {
            transforms.put(context, transform);
            return this;
        }

        public Builder gui(BLibTransform transform) {
            return set(ItemDisplayContext.GUI, transform);
        }

        public Builder ground(BLibTransform transform) {
            return set(ItemDisplayContext.GROUND, transform);
        }

        public Builder fixed(BLibTransform transform) {
            return set(ItemDisplayContext.FIXED, transform);
        }

        /**
         * Set a separate transform for wall-block placement of this item. Used by callers (e.g., the AVP-Alien
         * queen-head block-entity renderer) that render the same item differently when it's a wall-mounted block vs a
         * floor block. If unset, callers fall back to {@link #fixed}. Tuned independently of the floor pose since the
         * two visual setups don't reduce to a single rotation.
         */
        public Builder fixedWall(BLibTransform transform) {
            this.fixedWall = transform;
            return this;
        }

        /**
         * Set a separate transform for floor-block placement of this item. This is distinct from vanilla
         * {@link ItemDisplayContext#FIXED} so item-frame and floor-block presentations can be tuned independently.
         */
        public Builder fixedGround(BLibTransform transform) {
            this.fixedGround = transform;
            return this;
        }

        public Builder head(BLibTransform transform) {
            return set(ItemDisplayContext.HEAD, transform);
        }

        public Builder firstPersonLeftHand(BLibTransform transform) {
            return set(ItemDisplayContext.FIRST_PERSON_LEFT_HAND, transform);
        }

        public Builder firstPersonRightHand(BLibTransform transform) {
            return set(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, transform);
        }

        public Builder thirdPersonLeftHand(BLibTransform transform) {
            return set(ItemDisplayContext.THIRD_PERSON_LEFT_HAND, transform);
        }

        public Builder thirdPersonRightHand(BLibTransform transform) {
            return set(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, transform);
        }

        /** Apply the same transform to both first-person contexts. */
        public Builder firstPerson(BLibTransform transform) {
            return firstPersonLeftHand(transform).firstPersonRightHand(transform);
        }

        /** Apply the same transform to both third-person contexts. */
        public Builder thirdPerson(BLibTransform transform) {
            return thirdPersonLeftHand(transform).thirdPersonRightHand(transform);
        }

        /**
         * On {@link #build()}, derive {@link ItemDisplayContext#FIRST_PERSON_LEFT_HAND} from
         * {@link ItemDisplayContext#FIRST_PERSON_RIGHT_HAND} via a YZ-plane reflection — i.e. negate
         * {@code translation.x}, {@code rotation.y}, {@code rotation.z}, and {@code pivot.x}; everything else stays the
         * same. Skipped if the left-hand context was set explicitly, or if the right-hand context wasn't set.
         * Resolution is deferred to build, so subsequent right-hand changes flow through to the mirror.
         */
        public Builder mirrorFirstPersonRightToLeft() {
            this.mirrorFirstPerson = true;
            return this;
        }

        /**
         * Same as {@link #mirrorFirstPersonRightToLeft} but for third-person hands.
         */
        public Builder mirrorThirdPersonRightToLeft() {
            this.mirrorThirdPerson = true;
            return this;
        }

        /**
         * Convenience shorthand for both {@link #mirrorFirstPersonRightToLeft} and
         * {@link #mirrorThirdPersonRightToLeft}.
         */
        public Builder mirrorRightToLeftHands() {
            return mirrorFirstPersonRightToLeft().mirrorThirdPersonRightToLeft();
        }

        public BLibItemTransforms build() {
            var resolved = new EnumMap<>(transforms);

            if (mirrorFirstPerson) {
                applyMirror(resolved, ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, ItemDisplayContext.FIRST_PERSON_LEFT_HAND);
            }

            if (mirrorThirdPerson) {
                applyMirror(resolved, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, ItemDisplayContext.THIRD_PERSON_LEFT_HAND);
            }

            return new BLibItemTransforms(resolved, fixedWall, fixedGround);
        }

        private static void applyMirror(Map<ItemDisplayContext, BLibTransform> map, ItemDisplayContext from, ItemDisplayContext to) {
            // Explicit set wins — `set(LEFT_HAND, ...)` after `mirrorRightToLeft()` keeps the explicit value.
            if (map.containsKey(to)) {
                return;
            }

            var source = map.get(from);

            if (source == null) {
                return;
            }

            map.put(to, mirrorAcrossYZ(source));
        }

        /**
         * Reflect a transform across the YZ plane — the geometric mirror that turns a right-hand pose into a
         * visually-symmetric left-hand pose. Negates the X component of translation and pivot (the side-of-body axis)
         * and the Y/Z components of rotation (the in-plane angles); leaves X rotation, scale, and Y/Z translation/pivot
         * alone.
         * <p>
         * Derivation: for the full transform {@code M = T(t)·T(p)·R·S·T(-p)}, conjugating by the YZ reflection
         * {@code F = diag(-1,1,1)} gives {@code F·M·F = T(F·t)·T(F·p)·(F·R·F)·S·T(-F·p)}. For {@code R = Rx·Ry·Rz},
         * {@code F·R·F = Rx·Ry(-y)·Rz(-z)} (X rotation is invariant because its axis lies in the reflection plane; Y
         * and Z negate because their axes flip).
         */
        private static BLibTransform mirrorAcrossYZ(BLibTransform t) {
            return new BLibTransform(
                new Vector3f(-t.translation().x, t.translation().y, t.translation().z),
                new Vector3f(t.rotation().x, -t.rotation().y, -t.rotation().z),
                new Vector3f(t.scale()),
                new Vector3f(-t.pivot().x, t.pivot().y, t.pivot().z)
            );
        }
    }
}
