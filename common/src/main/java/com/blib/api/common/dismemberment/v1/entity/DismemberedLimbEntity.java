package com.blib.api.common.dismemberment.v1.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.blib.api.common.dismemberment.v1.LimbDefinition;
import com.blib.api.common.dismemberment.v1.LimbDefinitionRegistry;
import com.blib.api.common.dismemberment.v1.LimbInteractionRegistry;
import com.blib.api.common.dismemberment.v1.LimbPose;
import com.blib.api.common.dismemberment.v1.LimbVisuals;
import com.blib.api.common.dismemberment.v1.LimbVisualsRegistry;

/**
 * Generic, concrete entity representing a dismembered limb.
 * <p>
 * Carries a snapshot of the source entity's NBT (taken at dismemberment time) plus the source's {@link EntityType}, so
 * the renderer can reconstruct a transient "ghost" copy of the source mob and pull its texture/model directly from the
 * source's renderer. This means variant-specific textures, custom skins, and any other state encoded in the source
 * entity's data carry over to the limb fragment without each consumer maintaining a parallel mapping.
 * <p>
 * Visual fields (root bone, render offset / rotation / scale, companion bones) are <em>not</em> stored on the entity —
 * they live in {@link LimbVisualsRegistry} on the client, keyed by {@code (sourceEntityType, limbId)}. The client
 * resolves them at render time via {@link #resolveVisuals()}.
 * <p>
 * Lifetime, physics, and any side effects (e.g. acid bleeds) are intentionally minimal here — projects that need extra
 * behavior should compose this entity with their own systems rather than subclass it.
 */
public class DismemberedLimbEntity extends Entity {

    private static final String NBT_SOURCE_ENTITY_TYPE = "SourceEntityType";

    private static final String NBT_SOURCE_NBT = "SourceNbt";

    private static final String NBT_LIMB_ID = "LimbId";

    private static final String NBT_POSE_ID = "PoseId";

    private static final String NBT_LIFETIME_TICKS = "LifetimeTicks";

    private static final String NBT_AGE_TICKS = "AgeTicks";

    private static final EntityDataAccessor<String> SOURCE_ENTITY_TYPE = SynchedEntityData.defineId(
        DismemberedLimbEntity.class,
        EntityDataSerializers.STRING
    );

    private static final EntityDataAccessor<CompoundTag> SOURCE_NBT = SynchedEntityData.defineId(
        DismemberedLimbEntity.class,
        EntityDataSerializers.COMPOUND_TAG
    );

    private static final EntityDataAccessor<String> LIMB_ID = SynchedEntityData.defineId(
        DismemberedLimbEntity.class,
        EntityDataSerializers.STRING
    );

    private static final EntityDataAccessor<String> POSE_ID = SynchedEntityData.defineId(
        DismemberedLimbEntity.class,
        EntityDataSerializers.STRING
    );

    private static final int DEFAULT_LIFETIME_TICKS = 20 * 30;

    private static final float GRAVITY = 0.04F;

    private static final float DRAG = 0.98F;

    private static final double ITEM_POP_UPWARD_VELOCITY = 0.2;

    private static final double ITEM_POP_MAX_HORIZONTAL_VELOCITY = 0.5;

    private static final double ITEM_POP_INHERITED_VELOCITY_SCALE = 0.25;

    private int lifetimeTicks = DEFAULT_LIFETIME_TICKS;

    private int ageTicks = 0;

    /**
     * Strong reference to the resolved ghost entity, populated lazily on the client. Holding the strong ref here keeps
     * the ghost alive (against the cache's weak reference) for the limb's full lifetime, so even after every other limb
     * sharing the same source despawns, this limb still has a usable ghost for rendering.
     */
    private @Nullable LivingEntity cachedGhost;

    public DismemberedLimbEntity(EntityType<? extends DismemberedLimbEntity> entityType, Level level) {
        super(entityType, level);
        setNoGravity(false);
    }

    public void configure(EntityType<?> sourceEntityType, CompoundTag sourceNbt, ResourceLocation limbId, int lifetimeTicks) {
        configure(sourceEntityType, sourceNbt, limbId, lifetimeTicks, LimbPose.DEFAULT_ID);
    }

    public void configure(
        EntityType<?> sourceEntityType,
        CompoundTag sourceNbt,
        ResourceLocation limbId,
        int lifetimeTicks,
        String poseId
    ) {
        var sourceTypeId = BuiltInRegistries.ENTITY_TYPE.getKey(sourceEntityType);
        entityData.set(SOURCE_ENTITY_TYPE, sourceTypeId.toString());
        entityData.set(SOURCE_NBT, sourceNbt);
        entityData.set(LIMB_ID, limbId.toString());
        setPoseId(poseId);
        this.lifetimeTicks = Math.max(1, lifetimeTicks);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(SOURCE_ENTITY_TYPE, "");
        builder.define(SOURCE_NBT, new CompoundTag());
        builder.define(LIMB_ID, "");
        builder.define(POSE_ID, LimbPose.DEFAULT_ID);
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
        // Pickable so right-clicks can target the limb. We don't override attack(), so left-click damage no-ops on this
        // non-living entity, which is the desired behavior — the limb is for cosmetic + harvest interactions only.
        return true;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public @NotNull InteractionResult interact(@NotNull Player player, @NotNull InteractionHand hand) {
        if (level().isClientSide) {
            // Defer to the server's authoritative produce-and-discard. SUCCESS so the client plays the swing animation.
            return InteractionResult.sidedSuccess(true);
        }

        var stack = LimbInteractionRegistry.tryProduce(this);

        if (stack.isEmpty()) {
            return InteractionResult.PASS;
        }

        var itemEntity = new ItemEntity(level(), getX(), getY() + itemPopYOffset(), getZ(), stack);
        itemEntity.setDeltaMovement(createItemPopVelocity());
        itemEntity.setDefaultPickUpDelay();
        level().addFreshEntity(itemEntity);
        discard();

        return InteractionResult.SUCCESS;
    }

    private double itemPopYOffset() {
        return Math.min(0.5, Math.max(0.1, getBbHeight() * 0.5));
    }

    private Vec3 createItemPopVelocity() {
        var speed = random.nextDouble() * ITEM_POP_MAX_HORIZONTAL_VELOCITY;
        var angle = random.nextDouble() * Math.PI * 2.0;
        var pop = new Vec3(
            -Math.sin(angle) * speed,
            ITEM_POP_UPWARD_VELOCITY,
            Math.cos(angle) * speed
        );

        return pop.add(getDeltaMovement().scale(ITEM_POP_INHERITED_VELOCITY_SCALE));
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

    public CompoundTag getSourceNbt() {
        return entityData.get(SOURCE_NBT);
    }

    /**
     * Returns the strongly-referenced ghost entity for this limb, lazily resolved through {@link LimbGhostCache} so
     * sibling limbs from the same source share a single cached instance. Returns {@code null} until the source NBT and
     * entity type have been synced from the server.
     */
    public @Nullable LivingEntity getOrCreateGhost() {
        if (cachedGhost != null) {
            return cachedGhost;
        }

        var sourceType = getSourceEntityType();
        var nbt = getSourceNbt();

        if (sourceType == null || nbt.isEmpty()) {
            return null;
        }

        cachedGhost = LimbGhostCache.getOrCreate(sourceType, nbt, level());
        return cachedGhost;
    }

    /**
     * Identifier of the {@link LimbDefinition} this limb was spawned from, or {@code null} if the synced data has not
     * been populated yet (e.g. immediately after spawn before the entity-data packet arrives).
     */
    public @Nullable ResourceLocation getLimbId() {
        var raw = entityData.get(LIMB_ID);
        return raw.isEmpty() ? null : ResourceLocation.tryParse(raw);
    }

    public String getPoseId() {
        return normalizedPoseId(entityData.get(POSE_ID));
    }

    public void setPoseId(String poseId) {
        entityData.set(POSE_ID, normalizedPoseId(poseId));
    }

    /**
     * Returns the {@link LimbDefinition} this limb was spawned from via a direct registry lookup. Returns {@code null}
     * if the source type or limb id is missing/unknown.
     */
    public @Nullable LimbDefinition resolveLimbDefinition() {
        var sourceType = getSourceEntityType();
        var limbId = getLimbId();

        if (sourceType == null || limbId == null) {
            return null;
        }

        return LimbDefinitionRegistry.getDefinition(sourceType, limbId);
    }

    /**
     * Client-side helper: resolves the {@link LimbVisuals} record for this limb from {@link LimbVisualsRegistry}.
     * Returns {@code null} if the limb id / source type haven't synced yet, or if no visual record is registered for
     * this limb (e.g. a {@code /data} entry without a paired {@code /assets} entry — graceful failure path).
     */
    public @Nullable LimbVisuals resolveVisuals() {
        var sourceType = getSourceEntityType();
        var limbId = getLimbId();

        if (sourceType == null || limbId == null) {
            return null;
        }

        return LimbVisualsRegistry.get(sourceType, limbId);
    }

    /**
     * Client-side helper: resolves this limb's selected visual pose from {@link #resolveVisuals()} and the synced pose
     * id. The id is chosen once on the server when the limb spawns, so every client renders the same pose while still
     * allowing resource reloads to update the authored transform data.
     */
    public @Nullable LimbPose resolvePose() {
        var visuals = resolveVisuals();
        return visuals == null ? null : visuals.poseOrDefault(getPoseId());
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
        entityData.set(SOURCE_NBT, compoundTag.getCompound(NBT_SOURCE_NBT));
        entityData.set(LIMB_ID, compoundTag.getString(NBT_LIMB_ID));
        setPoseId(compoundTag.contains(NBT_POSE_ID) ? compoundTag.getString(NBT_POSE_ID) : LimbPose.DEFAULT_ID);
        this.lifetimeTicks = compoundTag.contains(NBT_LIFETIME_TICKS)
            ? compoundTag.getInt(NBT_LIFETIME_TICKS)
            : DEFAULT_LIFETIME_TICKS;
        this.ageTicks = compoundTag.getInt(NBT_AGE_TICKS);
        // World reload invalidates any prior client-side ghost reference.
        this.cachedGhost = null;
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        compoundTag.putString(NBT_SOURCE_ENTITY_TYPE, entityData.get(SOURCE_ENTITY_TYPE));
        compoundTag.put(NBT_SOURCE_NBT, entityData.get(SOURCE_NBT));
        compoundTag.putString(NBT_LIMB_ID, entityData.get(LIMB_ID));
        compoundTag.putString(NBT_POSE_ID, getPoseId());
        compoundTag.putInt(NBT_LIFETIME_TICKS, lifetimeTicks);
        compoundTag.putInt(NBT_AGE_TICKS, ageTicks);
    }

    private static String normalizedPoseId(@Nullable String poseId) {
        return poseId == null || poseId.isBlank() ? LimbPose.DEFAULT_ID : poseId.trim();
    }
}
