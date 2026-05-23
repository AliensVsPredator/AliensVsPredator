package com.blib.api.common.dismemberment.v1;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import com.blib.api.common.registry.v1.BLibHolder;

/**
 * Server-authoritative description of one detachable limb. Logic-only fields:
 * <ul>
 * <li>{@code id} — unique identifier shared with the {@link LimbVisuals} entry on the client side.</li>
 * <li>{@code category} — grouping for category-based detach picks ({@code first-of-category}, etc.).</li>
 * <li>{@code spawnOffsetProvider} — server-side function computing the world-space offset where the detached limb
 * entity spawns; not serializable, registered separately in {@link SpawnFunctionRegistry}.</li>
 * <li>{@code fatal} — if true, detachment kills the source entity.</li>
 * <li>{@code poses} — server-side pose ids and weights used when a limb is detached with a random pose.</li>
 * </ul>
 * Visual fields (root bone, companion bones, render offsets/rotations/scale) live on the client side in
 * {@link LimbVisuals}, paired by {@code id}. The Java {@link Builder} below configures both halves at once and writes
 * them into the appropriate registries on {@link Builder#build()}; JSON-loaded definitions ({@code
 * data/<ns>/blib_limbs/...}) bypass this builder entirely.
 */
public record LimbDefinition(
    ResourceLocation id,
    LimbCategory category,
    Function<LivingEntity, Vec3> spawnOffsetProvider,
    boolean fatal,
    List<LimbPoseOption> poses
) {

    public LimbDefinition(
        ResourceLocation id,
        LimbCategory category,
        Function<LivingEntity, Vec3> spawnOffsetProvider,
        boolean fatal
    ) {
        this(id, category, spawnOffsetProvider, fatal, List.of());
    }

    public LimbDefinition {
        Objects.requireNonNull(id, "LimbDefinition id must not be null");
        Objects.requireNonNull(category, "LimbDefinition category must not be null");
        Objects.requireNonNull(spawnOffsetProvider, "LimbDefinition spawnOffsetProvider must not be null");
        Objects.requireNonNull(poses, "LimbDefinition poses must not be null");

        Set<String> poseIds = new HashSet<>();
        var copied = new ArrayList<LimbPoseOption>(poses.size());
        for (var pose : poses) {
            Objects.requireNonNull(pose, "LimbDefinition pose must not be null");
            if (LimbPose.DEFAULT_ID.equals(pose.id())) {
                throw new IllegalArgumentException("LimbDefinition poses must not include the implicit default pose");
            }
            if (!poseIds.add(pose.id())) {
                throw new IllegalArgumentException("Duplicate LimbDefinition pose id: " + pose.id());
            }
            copied.add(pose);
        }
        poses = List.copyOf(copied);
    }

    /**
     * Codec for the JSON-serializable subset of {@link LimbDefinition} loaded from {@code data/<ns>/blib_limbs/}.
     * {@code spawnOffsetProvider} is not part of the codec; it's resolved from {@link SpawnFunctionRegistry} by
     * {@code id} after deserialization (falling back to {@link SpawnFunctionRegistry#DEFAULT_PROVIDER}).
     */
    public static final Codec<LimbDefinition> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(LimbDefinition::id),
            LimbCategory.CODEC.fieldOf("category").forGetter(LimbDefinition::category),
            Codec.BOOL.optionalFieldOf("fatal", false).forGetter(LimbDefinition::fatal),
            LimbPoseOption.CODEC.listOf().optionalFieldOf("poses", List.of()).forGetter(LimbDefinition::poses)
        ).apply(instance, (id, category, fatal, poses) -> new LimbDefinition(id, category, SpawnFunctionRegistry.get(id), fatal, poses))
    );

    public boolean hasPose(String poseId) {
        if (LimbPose.DEFAULT_ID.equals(poseId)) {
            return true;
        }
        for (var pose : poses) {
            if (pose.id().equals(poseId)) {
                return true;
            }
        }
        return false;
    }

    public List<LimbPoseOption> allPoseOptions() {
        var out = new ArrayList<LimbPoseOption>(poses.size() + 1);
        out.add(new LimbPoseOption(LimbPose.DEFAULT_ID, LimbPose.DEFAULT_WEIGHT));
        out.addAll(poses);
        return List.copyOf(out);
    }

    public String selectRandomPoseId(RandomSource random) {
        Objects.requireNonNull(random, "random");

        long totalWeight = LimbPose.DEFAULT_WEIGHT;
        for (var pose : poses) {
            if (pose.selectable()) {
                totalWeight += pose.weight();
            }
        }

        if (totalWeight <= 0) {
            return LimbPose.DEFAULT_ID;
        }

        var roll = randomRoll(random, totalWeight);
        if (roll < LimbPose.DEFAULT_WEIGHT) {
            return LimbPose.DEFAULT_ID;
        }
        roll -= LimbPose.DEFAULT_WEIGHT;

        for (var pose : poses) {
            if (!pose.selectable()) {
                continue;
            }
            if (roll < pose.weight()) {
                return pose.id();
            }
            roll -= pose.weight();
        }

        return LimbPose.DEFAULT_ID;
    }

    private static long randomRoll(RandomSource random, long bound) {
        if (bound <= Integer.MAX_VALUE) {
            return random.nextInt((int) bound);
        }
        return Math.floorMod(random.nextLong(), bound);
    }

    /**
     * Build a limb against an entity type registered by id. Pre-binding-safe — call freely at mod-init before
     * {@link BLibHolder}s have been bound, because the registries here are keyed by id rather than by live registry
     * entry.
     */
    public static Builder builder(ResourceLocation entityTypeId, ResourceLocation id, String rootBoneName, LimbCategory category) {
        return new Builder(entityTypeId, id, rootBoneName, category);
    }

    /**
     * Convenience overload that resolves the entity type's id via {@link BuiltInRegistries#ENTITY_TYPE}. Requires the
     * entity type to be registered already; for code paths that run during mod init before that point, use
     * {@link #builder(ResourceLocation, ResourceLocation, String, LimbCategory)} with the id directly (or with a
     * {@link BLibHolder#getResourceLocation()}).
     */
    public static Builder builder(EntityType<?> entityType, ResourceLocation id, String rootBoneName, LimbCategory category) {
        var entityTypeId = BuiltInRegistries.ENTITY_TYPE.getKey(Objects.requireNonNull(entityType, "entityType"));
        return new Builder(entityTypeId, id, rootBoneName, category);
    }

    /**
     * Convenience overload that extracts the entity type id from a {@link BLibHolder} without binding it. Lets callers
     * register limbs at mod-init time when the underlying registry entry isn't resolved yet.
     */
    public static Builder builder(
        BLibHolder<? extends EntityType<?>> entityTypeHolder,
        ResourceLocation id,
        String rootBoneName,
        LimbCategory category
    ) {
        return new Builder(Objects.requireNonNull(entityTypeHolder, "entityTypeHolder").getResourceLocation(), id, rootBoneName, category);
    }

    public static final class Builder {

        private final ResourceLocation entityTypeId;

        private final ResourceLocation id;

        private final String rootBoneName;

        private final LimbCategory category;

        private List<String> companionBoneNames = List.of();

        private Vec3 renderOffset = Vec3.ZERO;

        private Vec3 renderRotation = Vec3.ZERO;

        private Vec3 renderScale = LimbVisuals.DEFAULT_SCALE;

        private Vec3 renderPivot = LimbVisuals.DEFAULT_PIVOT;

        private boolean modelerTransform = false;

        private final List<LimbPose> poses = new ArrayList<>();

        private Function<LivingEntity, Vec3> spawnOffsetProvider = SpawnFunctionRegistry.DEFAULT_PROVIDER;

        private boolean fatal = false;

        private Builder(ResourceLocation entityTypeId, ResourceLocation id, String rootBoneName, LimbCategory category) {
            this.entityTypeId = Objects.requireNonNull(entityTypeId, "entityTypeId");
            this.id = Objects.requireNonNull(id, "id");
            this.rootBoneName = Objects.requireNonNull(rootBoneName, "rootBoneName");
            this.category = Objects.requireNonNull(category, "category");
            if (rootBoneName.isBlank()) {
                throw new IllegalArgumentException("rootBoneName must not be blank");
            }
        }

        public Builder renderOffset(double x, double y, double z) {
            return renderOffset(new Vec3(x, y, z));
        }

        public Builder renderOffset(Vec3 renderOffset) {
            this.renderOffset = Objects.requireNonNull(renderOffset);
            return this;
        }

        public Builder renderRotation(double pitch, double yaw, double roll) {
            return renderRotation(new Vec3(pitch, yaw, roll));
        }

        public Builder renderRotation(Vec3 renderRotation) {
            this.renderRotation = Objects.requireNonNull(renderRotation);
            return this;
        }

        public Builder renderScale(double x, double y, double z) {
            return renderScale(new Vec3(x, y, z));
        }

        public Builder renderScale(Vec3 renderScale) {
            this.renderScale = Objects.requireNonNull(renderScale);
            return this;
        }

        public Builder renderPivot(double x, double y, double z) {
            return renderPivot(new Vec3(x, y, z));
        }

        public Builder renderPivot(Vec3 renderPivot) {
            this.renderPivot = Objects.requireNonNull(renderPivot);
            this.modelerTransform = true;
            return this;
        }

        public Builder pose(LimbPose pose) {
            this.poses.add(Objects.requireNonNull(pose));
            return this;
        }

        public Builder poses(List<LimbPose> poses) {
            Objects.requireNonNull(poses, "poses");
            this.poses.clear();
            for (var pose : poses) {
                this.poses.add(Objects.requireNonNull(pose));
            }
            return this;
        }

        public Builder spawnOffset(double x, double y, double z) {
            var fixed = new Vec3(x, y, z);
            this.spawnOffsetProvider = $ -> fixed;
            return this;
        }

        public Builder spawnOffset(Function<LivingEntity, Vec3> spawnOffsetProvider) {
            this.spawnOffsetProvider = Objects.requireNonNull(spawnOffsetProvider);
            return this;
        }

        public Builder spawnAtEyeHeight() {
            this.spawnOffsetProvider = entity -> new Vec3(0.0, entity.getEyeHeight(), 0.0);
            return this;
        }

        public Builder companions(String... companionBoneNames) {
            Objects.requireNonNull(companionBoneNames, "companionBoneNames");
            this.companionBoneNames = List.of(companionBoneNames);
            return this;
        }

        public Builder fatal() {
            this.fatal = true;
            return this;
        }

        /**
         * Finalize the limb. Side-effects three registries by limb id:
         * <ul>
         * <li>{@link LimbDefinitionRegistry} ← the slimmer {@link LimbDefinition} record (tier-1).</li>
         * <li>{@link LimbVisualsRegistry} ← {@link LimbVisuals} built from the visual builder fields (tier-1).</li>
         * <li>{@link SpawnFunctionRegistry} ← the spawn function.</li>
         * </ul>
         * Returns the {@link LimbDefinition} for callers that want to hold a reference, but ignoring the return value
         * is fine — the registries are now populated.
         */
        public LimbDefinition build() {
            var poseOptions = poses.stream().map(LimbPoseOption::fromPose).toList();
            var definition = new LimbDefinition(id, category, spawnOffsetProvider, fatal, poseOptions);
            var visuals = new LimbVisuals(
                rootBoneName,
                companionBoneNames,
                renderOffset,
                renderRotation,
                renderScale,
                renderPivot,
                modelerTransform,
                poses
            );

            LimbDefinitionRegistry.register(entityTypeId, definition);
            LimbVisualsRegistry.register(entityTypeId, id, visuals);
            SpawnFunctionRegistry.register(id, spawnOffsetProvider);

            return definition;
        }
    }
}
