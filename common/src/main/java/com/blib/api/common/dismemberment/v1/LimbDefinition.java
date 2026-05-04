package com.blib.api.common.dismemberment.v1;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Server-authoritative description of one detachable limb on an entity.
 * <p>
 * {@code rootBoneName} is the bone in the entity's geo model whose subtree disappears when the limb detaches. Children
 * of the root are hidden via the existing {@code AzBone#setChildrenHidden} cascade, so callers describe one root and
 * the renderer hides the whole branch.
 * <p>
 * {@code companionBoneNames} extends that hide/draw set with sibling bones that aren't reachable from the root subtree
 * but conceptually belong with the limb (e.g. a chicken's beak/wattle, a villager's unified arms-vs-body split). They
 * are hidden on the source body alongside the root and are drawn alongside the root subtree on the spawned limb.
 * <p>
 * {@code renderOffset} is added to the bone-pivot translation when the detached limb is rendered, letting authors
 * fine-tune how the geometry sits inside the limb entity's hitbox (since bone positions aren't accessible server-side,
 * these offsets are the only way to nudge things).
 * <p>
 * {@code renderRotation} is an authored Euler rotation in degrees applied around the limb's anchor (the bone's pivot,
 * after it's been moved to the entity origin) so a limb that hung downward in bind pose can lie flat once detached.
 * Components are X/Y/Z; the renderer applies them in Z-Y-X order to match the geo bone convention.
 * <p>
 * {@code spawnOffsetProvider} produces a world-axis offset added to the source entity's position when the limb spawns.
 * Use it to anchor heads at eye height, tails behind the body, etc.
 * <p>
 * {@code fatal} marks this limb as one whose detachment kills the source entity. Useful for heads on most mobs
 * (decapitation = death) — set via {@link Builder#fatal()}.
 */
public record LimbDefinition(
    ResourceLocation id,
    String rootBoneName,
    LimbCategory category,
    List<String> companionBoneNames,
    Vec3 renderOffset,
    Vec3 renderRotation,
    Function<LivingEntity, Vec3> spawnOffsetProvider,
    boolean fatal
) {

    public LimbDefinition {
        Objects.requireNonNull(id, "LimbDefinition id must not be null");
        Objects.requireNonNull(rootBoneName, "LimbDefinition rootBoneName must not be null");
        Objects.requireNonNull(category, "LimbDefinition category must not be null");
        Objects.requireNonNull(companionBoneNames, "LimbDefinition companionBoneNames must not be null");
        Objects.requireNonNull(renderOffset, "LimbDefinition renderOffset must not be null");
        Objects.requireNonNull(renderRotation, "LimbDefinition renderRotation must not be null");
        Objects.requireNonNull(spawnOffsetProvider, "LimbDefinition spawnOffsetProvider must not be null");

        if (rootBoneName.isBlank()) {
            throw new IllegalArgumentException("LimbDefinition rootBoneName must not be blank");
        }

        companionBoneNames = List.copyOf(companionBoneNames);
    }

    public static Builder builder(ResourceLocation id, String rootBoneName, LimbCategory category) {
        return new Builder(id, rootBoneName, category);
    }

    public static final class Builder {

        private static final Function<LivingEntity, Vec3> DEFAULT_SPAWN_OFFSET =
            entity -> new Vec3(0.0, entity.getBbHeight() * 0.5, 0.0);

        private final ResourceLocation id;

        private final String rootBoneName;

        private final LimbCategory category;

        private List<String> companionBoneNames = List.of();

        private Vec3 renderOffset = Vec3.ZERO;

        private Vec3 renderRotation = Vec3.ZERO;

        private Function<LivingEntity, Vec3> spawnOffsetProvider = DEFAULT_SPAWN_OFFSET;

        private boolean fatal = false;

        private Builder(ResourceLocation id, String rootBoneName, LimbCategory category) {
            this.id = id;
            this.rootBoneName = rootBoneName;
            this.category = category;
        }

        /**
         * Translation applied (in blocks) on top of the bone-pivot compensation when the detached limb is rendered.
         * Positive Y lifts the geometry.
         */
        public Builder renderOffset(double x, double y, double z) {
            return renderOffset(new Vec3(x, y, z));
        }

        public Builder renderOffset(Vec3 renderOffset) {
            this.renderOffset = Objects.requireNonNull(renderOffset);
            return this;
        }

        /**
         * Authored Euler rotation in degrees, applied around the limb's anchor after the bone pivot has been moved to
         * the entity origin. Use this to orient limbs that would otherwise lie in their bind pose (e.g. arms pointing
         * down through the ground).
         */
        public Builder renderRotation(double pitch, double yaw, double roll) {
            return renderRotation(new Vec3(pitch, yaw, roll));
        }

        public Builder renderRotation(Vec3 renderRotation) {
            this.renderRotation = Objects.requireNonNull(renderRotation);
            return this;
        }

        /**
         * World-axis offset added to the source entity's position to determine where the limb entity spawns. Default is
         * the entity's vertical centre.
         */
        public Builder spawnOffset(double x, double y, double z) {
            var fixed = new Vec3(x, y, z);
            this.spawnOffsetProvider = $ -> fixed;
            return this;
        }

        public Builder spawnOffset(Function<LivingEntity, Vec3> spawnOffsetProvider) {
            this.spawnOffsetProvider = Objects.requireNonNull(spawnOffsetProvider);
            return this;
        }

        /**
         * Convenience: spawn at the source entity's eye height.
         */
        public Builder spawnAtEyeHeight() {
            this.spawnOffsetProvider = entity -> new Vec3(0.0, entity.getEyeHeight(), 0.0);
            return this;
        }

        /**
         * Companion bones are sibling parts that aren't children of {@code rootBoneName} but belong with the limb
         * conceptually — e.g. a chicken's {@code beak} / {@code red_thing} when detaching the {@code head}, or a
         * villager's unified {@code arms} when detaching the {@code body}. They're hidden on the source body alongside
         * the root and rendered alongside it on the spawned limb fragment, both for vanilla {@code ModelPart} and BLib
         * geo-bone render paths.
         */
        public Builder companions(String... companionBoneNames) {
            Objects.requireNonNull(companionBoneNames, "companionBoneNames");
            this.companionBoneNames = List.of(companionBoneNames);
            return this;
        }

        /**
         * Marks this limb as fatal: detaching it kills the source entity. Triggered through
         * {@link net.minecraft.world.entity.Entity#kill()}, so the entity dies the same way it would from any other
         * lethal damage — death event fires, loot table runs, advancements trigger, etc. Typical use is heads on most
         * mobs (decapitation = death).
         */
        public Builder fatal() {
            this.fatal = true;
            return this;
        }

        public LimbDefinition build() {
            return new LimbDefinition(
                id,
                rootBoneName,
                category,
                companionBoneNames,
                renderOffset,
                renderRotation,
                spawnOffsetProvider,
                fatal
            );
        }
    }
}
