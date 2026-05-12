package com.blib.api.common.dismemberment.v1;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Server-authoritative description of one detachable limb. Logic-only fields:
 * <ul>
 * <li>{@code id} — unique identifier shared with the {@link LimbVisuals} entry on the client side.</li>
 * <li>{@code category} — grouping for category-based detach picks ({@code first-of-category}, etc.).</li>
 * <li>{@code spawnOffsetProvider} — server-side function computing the world-space offset where the detached limb
 * entity spawns; not serializable, registered separately in {@link SpawnFunctionRegistry}.</li>
 * <li>{@code fatal} — if true, detachment kills the source entity.</li>
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
    boolean fatal
) {

    public LimbDefinition {
        Objects.requireNonNull(id, "LimbDefinition id must not be null");
        Objects.requireNonNull(category, "LimbDefinition category must not be null");
        Objects.requireNonNull(spawnOffsetProvider, "LimbDefinition spawnOffsetProvider must not be null");
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
            Codec.BOOL.optionalFieldOf("fatal", false).forGetter(LimbDefinition::fatal)
        ).apply(instance, (id, category, fatal) -> new LimbDefinition(id, category, SpawnFunctionRegistry.get(id), fatal))
    );

    public static Builder builder(EntityType<?> entityType, ResourceLocation id, String rootBoneName, LimbCategory category) {
        return new Builder(entityType, id, rootBoneName, category);
    }

    public static final class Builder {

        private final EntityType<?> entityType;

        private final ResourceLocation id;

        private final String rootBoneName;

        private final LimbCategory category;

        private List<String> companionBoneNames = List.of();

        private Vec3 renderOffset = Vec3.ZERO;

        private Vec3 renderRotation = Vec3.ZERO;

        private Vec3 renderScale = LimbVisuals.DEFAULT_SCALE;

        private Function<LivingEntity, Vec3> spawnOffsetProvider = SpawnFunctionRegistry.DEFAULT_PROVIDER;

        private boolean fatal = false;

        private Builder(EntityType<?> entityType, ResourceLocation id, String rootBoneName, LimbCategory category) {
            this.entityType = Objects.requireNonNull(entityType, "entityType");
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
            var definition = new LimbDefinition(id, category, spawnOffsetProvider, fatal);
            var visuals = new LimbVisuals(rootBoneName, companionBoneNames, renderOffset, renderRotation, renderScale);
            var entityTypeId = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);

            LimbDefinitionRegistry.register(entityTypeId, definition);
            LimbVisualsRegistry.register(entityTypeId, id, visuals);
            SpawnFunctionRegistry.register(id, spawnOffsetProvider);

            return definition;
        }
    }
}
