package com.alien.common.gameplay.entity.living.alien.ovomorph;

import com.alien.common.data.AlienVariantTypes;
import com.alien.common.gameplay.entity.living.alien.Alien;
import com.alien.common.gameplay.entity.living.alien.ovomorph.ai.OvomorphGOAP;
import com.alien.common.model.alien.HatchState;
import com.alien.common.model.alien.variant.AlienVariant;
import com.alien.common.registry.init.AlienEntityTypes;
import com.alien.common.registry.init.AlienItems;
import com.bvanseg.just.functional.option.Option;
import com.lib.common.gameplay.entity.manager.VibrationSystemManager;
import com.lib.common.network.SyncedDataAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.avp.AVP;
import com.avp.common.network.sync.AVPSyncedDataKey;
import com.avp.common.registry.init.AVPSoundEvents;
import com.avp.common.util.AVPPredicates;

public class Ovomorph extends Alien implements Shearable {

    public static final HatchState DEFAULT_HATCH_STATE = HatchState.SLEEPING;

    private static final String NBT_HATCH_STATE = "hatchState";

    private static final String NBT_MAXIMUM_SPAWN_COUNT = "maximumSpawnCount";

    private static final String NBT_IS_ROOTED = "isRooted";

    public static AttributeSupplier.Builder createOvomorphAttributes() {
        return applyFrom(AVP.config.statsConfigs.OVOMORPH_STATS, Monster.createMonsterAttributes());
    }

    public final SyncedDataAccessor<Byte> hatchStateId = getSyncedDataContainer().define(
        new AVPSyncedDataKey<>("hatch_state", ByteBufCodecs.BYTE),
        (byte) DEFAULT_HATCH_STATE.getId()
    );

    public final SyncedDataAccessor<Byte> maxSpawnCount = getSyncedDataContainer().define(
        new AVPSyncedDataKey<>("max_spawn_count", ByteBufCodecs.BYTE),
        (byte) 1
    );

    public final SyncedDataAccessor<Boolean> isRooted = getSyncedDataContainer().define(
        new AVPSyncedDataKey<>("is_rooted", ByteBufCodecs.BOOL),
        true
    );

    private final OvomorphAnimationDispatcher animationDispatcher;

    private final OvomorphGOAP goap;

    private final HatchManager hatchManager;

    public Ovomorph(EntityType<? extends Ovomorph> entityType, Level level) {
        super(entityType, level);
        this.animationDispatcher = new OvomorphAnimationDispatcher(this);
        this.goap = new OvomorphGOAP(this);
        this.hatchManager = new HatchManager(this, 3 * 20, 3 * 20);
        this.config = AVP.config.statsConfigs.OVOMORPH_STATS;
    }

    @Override
    public @Nullable EntityType<? extends Alien> getTypeForVariant(AlienVariant alienVariant) {
        return getType(alienVariant, isRoyal());
    }

    @Override
    protected VibrationSystemManager createVibrationSystemManager() {
        return new VibrationSystemManager(this, 2.5F, 8);
    }

    @Override
    public void tick() {
        super.tick();
        hatchManager.tick();

        if (!level().isClientSide) {
            goap.update(this);
        }
    }

    public void tryHatch() {
        if (
            !level().isClientSide
                && !hatchManager.isHatching()
                && !hatchManager.isHatched()
                && !isIrradiated()
        ) {
            hatchManager.hatch();
        }
    }

    @Override
    public @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand interactionHand) {
        if (level().isClientSide) {
            return super.mobInteract(player, interactionHand);
        }

        var itemStack = player.getItemInHand(interactionHand);
        var resinBallItem = AlienVariantTypes.getFor(this).resinBall().get();

        if (itemStack.is(AlienItems.RAW_ROYAL_JELLY.get())) {
            if (hatchManager.isHatching() || getHatchManager().isHatched()) {
                level().playSound(null, this, SoundEvents.HONEY_BLOCK_PLACE, SoundSource.PLAYERS, 1.0F, 1.0F);
                hatchManager.restore();
                itemStack.consume(1, player);

                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.CONSUME;
            }
        } else if (isRooted.get() && itemStack.is(Items.SHEARS)) {
            shear(SoundSource.PLAYERS);
            gameEvent(GameEvent.SHEAR, player);
            itemStack.hurtAndBreak(1, player, getSlotForHand(interactionHand));

            return InteractionResult.SUCCESS;
        } else if (!isRooted.get() && itemStack.is(resinBallItem)) {
            level().playSound(null, this, AVPSoundEvents.ENTITY_OVOMORPH_ROOT.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
            isRooted.set(true);
            itemStack.consume(1, player);
        }

        return super.mobInteract(player, interactionHand);
    }

    @Override
    public void shear(@NotNull SoundSource soundSource) {
        isRooted.set(false);
        level().playSound(null, this, SoundEvents.SHEEP_SHEAR, soundSource, 1.0F, 1.0F);
        level().playSound(null, this, AVPSoundEvents.ENTITY_OVOMORPH_SHEAR.get(), soundSource, 1.0F, 1.0F);
        var resinBallItem = AlienVariantTypes.getFor(this).resinBall().get();

        var itemEntity = this.spawnAtLocation(resinBallItem, 1);

        if (itemEntity != null) {
            itemEntity.setDeltaMovement(
                itemEntity.getDeltaMovement()
                    .add(
                        (random.nextFloat() - random.nextFloat()) * 0.1F,
                        random.nextFloat() * 0.05F,
                        (random.nextFloat() - random.nextFloat()) * 0.1F
                    )
            );
        }
    }

    @Override
    public boolean readyForShearing() {
        return isRooted.get();
    }

    @Override
    public boolean hurt(@NotNull DamageSource damageSource, float damage) {
        var isHurt = super.hurt(damageSource, damage);

        if (!level().isClientSide && isHurt && damageSource.getEntity() != null) {
            tryHatch();
        }

        return isHurt;
    }

    @Override
    protected void doPush(@NotNull Entity entity) {
        if (entity instanceof Player player && (player.isCreative() || player.isSpectator())) {
            super.doPush(entity);
            return;
        }

        if (AVPPredicates.isFreeHost(this, entity)) {
            tryHatch();
        }

        super.doPush(entity);
    }

    @Override
    protected boolean canBleedAcid() {
        return !hatchManager.isHatching()
            && !hatchManager.isHatched();
    }

    @Override
    public boolean isPushedByFluid() {
        return !isRooted.get();
    }

    @Override
    public boolean isPushable() {
        return !isRooted.get();
    }

    @Override
    public boolean isPersistenceRequired() {
        if (hatchManager.isHatched()) {
            // If the ovomorph is hatched, then defer to super and no other factors.
            return super.isPersistenceRequired();
        }

        // Otherwise if super check passes or if ovomorph is not rooted, then persist the ovomorph.
        return super.isPersistenceRequired() || !isRooted.get();
    }

    @Override
    protected boolean canHeal() {
        return !hatchManager.isHatching()
            && !hatchManager.isHatched()
            && super.canHeal();
    }

    @Override
    protected boolean canAlienRideVehicle(@NotNull Entity vehicle) {
        return !isRooted.get();
    }

    @Override
    protected float getHealthRegenPerSecond() {
        return AVP.config.statsConfigs.OVOMORPH_STATS.healthRegenPerSecond;
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        hatchManager.load(compoundTag);

        if (compoundTag.contains(NBT_HATCH_STATE)) {
            // Assign the default hatch state in case we read an invalid hatch state ID.
            var hatchState = HatchState.ID_TO_HATCH_STATE_MAP.getOrDefault((int) compoundTag.getByte(NBT_HATCH_STATE), DEFAULT_HATCH_STATE);
            setHatchState(hatchState);
        }

        if (compoundTag.contains(NBT_IS_ROOTED)) {
            isRooted.set(compoundTag.getBoolean(NBT_IS_ROOTED));
        }

        if (compoundTag.contains(NBT_MAXIMUM_SPAWN_COUNT)) {
            maxSpawnCount.set(compoundTag.getByte(NBT_MAXIMUM_SPAWN_COUNT));
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        hatchManager.save(compoundTag);

        compoundTag.putByte(NBT_HATCH_STATE, (byte) getHatchState().unwrapOr(DEFAULT_HATCH_STATE).getId());
        compoundTag.putBoolean(NBT_IS_ROOTED, isRooted.get());
        compoundTag.putByte(NBT_MAXIMUM_SPAWN_COUNT, maxSpawnCount.get());
    }

    public HatchManager getHatchManager() {
        return hatchManager;
    }

    public Option<HatchState> getHatchState() {
        var id = (int) hatchStateId.get();
        return Option.ofNullable(HatchState.ID_TO_HATCH_STATE_MAP.get(id));
    }

    public void setHatchState(HatchState hatchState) {
        hatchStateId.set((byte) hatchState.getId());
    }

    public OvomorphAnimationDispatcher getAnimationDispatcher() {
        return animationDispatcher;
    }

    public static @Nullable EntityType<? extends Ovomorph> getType(AlienVariant alienVariant, boolean isRoyal) {
        if (isRoyal) {
            return switch (alienVariant) {
                case NORMAL -> AlienEntityTypes.ROYAL_OVOMORPH.get();
                case NETHER -> AlienEntityTypes.ROYAL_NETHER_OVOMORPH.get();
                case ABERRANT -> AlienEntityTypes.ROYAL_ABERRANT_OVOMORPH.get();
                case IRRADIATED -> null;
            };
        }

        return switch (alienVariant) {
            case NORMAL -> AlienEntityTypes.OVOMORPH.get();
            case NETHER -> AlienEntityTypes.NETHER_OVOMORPH.get();
            case ABERRANT -> AlienEntityTypes.ABERRANT_OVOMORPH.get();
            case IRRADIATED -> null;
        };
    }
}
