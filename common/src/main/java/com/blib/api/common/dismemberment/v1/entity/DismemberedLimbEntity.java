package com.blib.api.common.dismemberment.v1.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Generic, concrete entity representing a dismembered limb.
 * <p>
 * It carries enough metadata for the renderer to reuse the source entity's baked model and texture, rendering only the
 * bone subtree rooted at {@link #getRootBoneName()}. Lifetime, physics, and any side effects (e.g. acid bleeds) are
 * intentionally minimal here — projects that need extra behavior should compose this entity with their own systems
 * rather than subclass it.
 */
public class DismemberedLimbEntity extends Entity {

    private static final String NBT_SOURCE_ENTITY_TYPE = "SourceEntityType";

    private static final String NBT_MODEL_LOCATION = "ModelLocation";

    private static final String NBT_TEXTURE_LOCATION = "TextureLocation";

    private static final String NBT_ROOT_BONE_NAME = "RootBoneName";

    private static final String NBT_RENDER_OFFSET_X = "RenderOffsetX";

    private static final String NBT_RENDER_OFFSET_Y = "RenderOffsetY";

    private static final String NBT_RENDER_OFFSET_Z = "RenderOffsetZ";

    private static final String NBT_RENDER_ROTATION_X = "RenderRotationX";

    private static final String NBT_RENDER_ROTATION_Y = "RenderRotationY";

    private static final String NBT_RENDER_ROTATION_Z = "RenderRotationZ";

    private static final String NBT_LIFETIME_TICKS = "LifetimeTicks";

    private static final String NBT_AGE_TICKS = "AgeTicks";

    private static final EntityDataAccessor<String> SOURCE_ENTITY_TYPE = SynchedEntityData.defineId(
        DismemberedLimbEntity.class,
        EntityDataSerializers.STRING
    );

    private static final EntityDataAccessor<String> MODEL_LOCATION = SynchedEntityData.defineId(
        DismemberedLimbEntity.class,
        EntityDataSerializers.STRING
    );

    private static final EntityDataAccessor<String> TEXTURE_LOCATION = SynchedEntityData.defineId(
        DismemberedLimbEntity.class,
        EntityDataSerializers.STRING
    );

    private static final EntityDataAccessor<String> ROOT_BONE_NAME = SynchedEntityData.defineId(
        DismemberedLimbEntity.class,
        EntityDataSerializers.STRING
    );

    private static final EntityDataAccessor<Float> RENDER_OFFSET_X = SynchedEntityData.defineId(
        DismemberedLimbEntity.class,
        EntityDataSerializers.FLOAT
    );

    private static final EntityDataAccessor<Float> RENDER_OFFSET_Y = SynchedEntityData.defineId(
        DismemberedLimbEntity.class,
        EntityDataSerializers.FLOAT
    );

    private static final EntityDataAccessor<Float> RENDER_OFFSET_Z = SynchedEntityData.defineId(
        DismemberedLimbEntity.class,
        EntityDataSerializers.FLOAT
    );

    private static final EntityDataAccessor<Float> RENDER_ROTATION_X = SynchedEntityData.defineId(
        DismemberedLimbEntity.class,
        EntityDataSerializers.FLOAT
    );

    private static final EntityDataAccessor<Float> RENDER_ROTATION_Y = SynchedEntityData.defineId(
        DismemberedLimbEntity.class,
        EntityDataSerializers.FLOAT
    );

    private static final EntityDataAccessor<Float> RENDER_ROTATION_Z = SynchedEntityData.defineId(
        DismemberedLimbEntity.class,
        EntityDataSerializers.FLOAT
    );

    private static final int DEFAULT_LIFETIME_TICKS = 20 * 30;

    private static final float GRAVITY = 0.04F;

    private static final float DRAG = 0.98F;

    private int lifetimeTicks = DEFAULT_LIFETIME_TICKS;

    private int ageTicks = 0;

    public DismemberedLimbEntity(EntityType<? extends DismemberedLimbEntity> entityType, Level level) {
        super(entityType, level);
        setNoGravity(false);
    }

    public void configure(
        EntityType<?> sourceEntityType,
        ResourceLocation modelLocation,
        ResourceLocation textureLocation,
        String rootBoneName,
        Vec3 renderOffset,
        Vec3 renderRotation,
        int lifetimeTicks
    ) {
        var sourceTypeId = BuiltInRegistries.ENTITY_TYPE.getKey(sourceEntityType);
        entityData.set(SOURCE_ENTITY_TYPE, sourceTypeId.toString());
        entityData.set(MODEL_LOCATION, modelLocation.toString());
        entityData.set(TEXTURE_LOCATION, textureLocation.toString());
        entityData.set(ROOT_BONE_NAME, rootBoneName);
        entityData.set(RENDER_OFFSET_X, (float) renderOffset.x);
        entityData.set(RENDER_OFFSET_Y, (float) renderOffset.y);
        entityData.set(RENDER_OFFSET_Z, (float) renderOffset.z);
        entityData.set(RENDER_ROTATION_X, (float) renderRotation.x);
        entityData.set(RENDER_ROTATION_Y, (float) renderRotation.y);
        entityData.set(RENDER_ROTATION_Z, (float) renderRotation.z);
        this.lifetimeTicks = Math.max(1, lifetimeTicks);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(SOURCE_ENTITY_TYPE, "");
        builder.define(MODEL_LOCATION, "");
        builder.define(TEXTURE_LOCATION, "");
        builder.define(ROOT_BONE_NAME, "");
        builder.define(RENDER_OFFSET_X, 0F);
        builder.define(RENDER_OFFSET_Y, 0F);
        builder.define(RENDER_OFFSET_Z, 0F);
        builder.define(RENDER_ROTATION_X, 0F);
        builder.define(RENDER_ROTATION_Y, 0F);
        builder.define(RENDER_ROTATION_Z, 0F);
    }

    @Override
    public void tick() {
        super.tick();

        ageTicks++;

        // Run physics on both sides so the client doesn't visually stutter waiting
        // for server position packets. The server stays authoritative — its
        // position updates correct any client drift through the normal entity
        // sync path.
        applyPhysics();

        if (!level().isClientSide && ageTicks >= lifetimeTicks) {
            discard();
        }
    }

    private void applyPhysics() {
        if (!isNoGravity()) {
            setDeltaMovement(getDeltaMovement().add(0.0, -GRAVITY, 0.0));
        }

        move(MoverType.SELF, getDeltaMovement());

        var horizontalDrag = onGround() ? 0.6 : DRAG;
        setDeltaMovement(getDeltaMovement().multiply(horizontalDrag, DRAG, horizontalDrag));

        if (onGround() && getDeltaMovement().y < 0.0) {
            setDeltaMovement(getDeltaMovement().multiply(1.0, -0.5, 1.0));
        }
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    public @Nullable EntityType<?> getSourceEntityType() {
        var raw = entityData.get(SOURCE_ENTITY_TYPE);
        if (raw.isEmpty()) {
            return null;
        }

        var parsed = ResourceLocation.tryParse(raw);
        if (parsed == null) {
            return null;
        }

        return BuiltInRegistries.ENTITY_TYPE.getOptional(parsed).orElse(null);
    }

    public @Nullable ResourceLocation getModelLocation() {
        return parseOrNull(entityData.get(MODEL_LOCATION));
    }

    public @Nullable ResourceLocation getTextureLocation() {
        return parseOrNull(entityData.get(TEXTURE_LOCATION));
    }

    public String getRootBoneName() {
        return entityData.get(ROOT_BONE_NAME);
    }

    public Vec3 getLimbRenderOffset() {
        return new Vec3(
            entityData.get(RENDER_OFFSET_X),
            entityData.get(RENDER_OFFSET_Y),
            entityData.get(RENDER_OFFSET_Z)
        );
    }

    public Vec3 getLimbRenderRotation() {
        return new Vec3(
            entityData.get(RENDER_ROTATION_X),
            entityData.get(RENDER_ROTATION_Y),
            entityData.get(RENDER_ROTATION_Z)
        );
    }

    public int getLifetimeTicks() {
        return lifetimeTicks;
    }

    public int getAgeTicks() {
        return ageTicks;
    }

    public void launch(Vec3 velocity) {
        setDeltaMovement(velocity);
        hasImpulse = true;
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        entityData.set(SOURCE_ENTITY_TYPE, compoundTag.getString(NBT_SOURCE_ENTITY_TYPE));
        entityData.set(MODEL_LOCATION, compoundTag.getString(NBT_MODEL_LOCATION));
        entityData.set(TEXTURE_LOCATION, compoundTag.getString(NBT_TEXTURE_LOCATION));
        entityData.set(ROOT_BONE_NAME, compoundTag.getString(NBT_ROOT_BONE_NAME));
        entityData.set(RENDER_OFFSET_X, compoundTag.getFloat(NBT_RENDER_OFFSET_X));
        entityData.set(RENDER_OFFSET_Y, compoundTag.getFloat(NBT_RENDER_OFFSET_Y));
        entityData.set(RENDER_OFFSET_Z, compoundTag.getFloat(NBT_RENDER_OFFSET_Z));
        entityData.set(RENDER_ROTATION_X, compoundTag.getFloat(NBT_RENDER_ROTATION_X));
        entityData.set(RENDER_ROTATION_Y, compoundTag.getFloat(NBT_RENDER_ROTATION_Y));
        entityData.set(RENDER_ROTATION_Z, compoundTag.getFloat(NBT_RENDER_ROTATION_Z));
        this.lifetimeTicks = compoundTag.contains(NBT_LIFETIME_TICKS)
            ? compoundTag.getInt(NBT_LIFETIME_TICKS)
            : DEFAULT_LIFETIME_TICKS;
        this.ageTicks = compoundTag.getInt(NBT_AGE_TICKS);
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        compoundTag.putString(NBT_SOURCE_ENTITY_TYPE, entityData.get(SOURCE_ENTITY_TYPE));
        compoundTag.putString(NBT_MODEL_LOCATION, entityData.get(MODEL_LOCATION));
        compoundTag.putString(NBT_TEXTURE_LOCATION, entityData.get(TEXTURE_LOCATION));
        compoundTag.putString(NBT_ROOT_BONE_NAME, entityData.get(ROOT_BONE_NAME));
        compoundTag.putFloat(NBT_RENDER_OFFSET_X, entityData.get(RENDER_OFFSET_X));
        compoundTag.putFloat(NBT_RENDER_OFFSET_Y, entityData.get(RENDER_OFFSET_Y));
        compoundTag.putFloat(NBT_RENDER_OFFSET_Z, entityData.get(RENDER_OFFSET_Z));
        compoundTag.putFloat(NBT_RENDER_ROTATION_X, entityData.get(RENDER_ROTATION_X));
        compoundTag.putFloat(NBT_RENDER_ROTATION_Y, entityData.get(RENDER_ROTATION_Y));
        compoundTag.putFloat(NBT_RENDER_ROTATION_Z, entityData.get(RENDER_ROTATION_Z));
        compoundTag.putInt(NBT_LIFETIME_TICKS, lifetimeTicks);
        compoundTag.putInt(NBT_AGE_TICKS, ageTicks);
    }

    private static @Nullable ResourceLocation parseOrNull(String raw) {
        return raw.isEmpty() ? null : ResourceLocation.tryParse(raw);
    }
}
