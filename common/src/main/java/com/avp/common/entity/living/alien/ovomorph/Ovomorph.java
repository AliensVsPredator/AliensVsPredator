package com.avp.common.entity.living.alien.ovomorph;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
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
import com.avp.common.entity.living.alien.Alien;
import com.avp.common.entity.living.alien.util.AlienVariantUtil;
import com.avp.common.entity.type.AVPEntityTypes;
import com.avp.common.item.AVPItems;
import com.avp.common.sound.AVPSoundEvents;
import com.avp.common.util.AVPPredicates;

public class Ovomorph extends Alien implements Shearable {

    private static final EntityDataAccessor<Byte> HATCH_STATE = SynchedEntityData.defineId(Ovomorph.class, EntityDataSerializers.BYTE);

    private static final EntityDataAccessor<Byte> MAX_SPAWN_COUNT = SynchedEntityData.defineId(Ovomorph.class, EntityDataSerializers.BYTE);

    private static final EntityDataAccessor<Boolean> ROOTED = SynchedEntityData.defineId(Ovomorph.class, EntityDataSerializers.BOOLEAN);

    private static final String IS_ROOTED_KEY = "isRooted";

    public static AttributeSupplier.Builder createOvomorphAttributes() {
        return applyFrom(AVP.config.statsConfigs.OVAMORPH_STATS, Monster.createMonsterAttributes());
    }

    private final OvomorphAnimationDispatcher animationDispatcher;

    private final HatchManager hatchManager;

    public Ovomorph(EntityType<? extends Ovomorph> entityType, Level level) {
        super(entityType, level);
        this.animationDispatcher = new OvomorphAnimationDispatcher(this);
        this.hatchManager = new HatchManager(this, HATCH_STATE, MAX_SPAWN_COUNT, 3 * 20, 3 * 20);
        this.config = AVP.config.statsConfigs.OVAMORPH_STATS;
    }

    @Override
    public @Nullable EntityType<? extends Alien> getAberrantType() {
        return isRoyal() ? AVPEntityTypes.ROYAL_ABERRANT_OVOMORPH.get() : AVPEntityTypes.ABERRANT_OVOMORPH.get();
    }

    @Override
    public @Nullable EntityType<? extends Alien> getIrradiatedType() {
        return null;
    }

    @Override
    public @Nullable EntityType<? extends Alien> getNetherType() {
        return isRoyal() ? AVPEntityTypes.ROYAL_NETHER_OVOMORPH.get() : AVPEntityTypes.NETHER_OVOMORPH.get();
    }

    @Override
    public @Nullable EntityType<? extends Alien> getDefaultType() {
        return isRoyal() ? AVPEntityTypes.ROYAL_OVOMORPH.get() : AVPEntityTypes.OVOMORPH.get();
    }

    @Override
    protected void defineSynchedData(@NotNull SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HATCH_STATE, (byte) HatchState.SLEEPING.getId());
        builder.define(MAX_SPAWN_COUNT, (byte) 1);
        builder.define(ROOTED, true);
    }

    @Override
    public void tick() {
        super.tick();
        hatchManager.tick();
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
        var resinBallItem = AlienVariantUtil.getResinBallFor(this);

        if (itemStack.is(AVPItems.RAW_ROYAL_JELLY.get())) {
            if (hatchManager.isHatching() || hatchManager().isHatched()) {
                level().playSound(null, this, SoundEvents.HONEY_BLOCK_PLACE, SoundSource.PLAYERS, 1.0F, 1.0F);
                hatchManager.restore();
                itemStack.consume(1, player);

                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.CONSUME;
            }
        } else if (isRooted() && itemStack.is(Items.SHEARS)) {
            shear(SoundSource.PLAYERS);
            gameEvent(GameEvent.SHEAR, player);
            itemStack.hurtAndBreak(1, player, getSlotForHand(interactionHand));

            return InteractionResult.SUCCESS;
        } else if (!isRooted() && itemStack.is(resinBallItem)) {
            level().playSound(null, this, AVPSoundEvents.ENTITY_OVOMORPH_ROOT.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
            setRooted(true);
            itemStack.consume(1, player);
        }

        return super.mobInteract(player, interactionHand);
    }

    @Override
    public void shear(@NotNull SoundSource soundSource) {
        setRooted(false);
        level().playSound(null, this, SoundEvents.SHEEP_SHEAR, soundSource, 1.0F, 1.0F);
        level().playSound(null, this, AVPSoundEvents.ENTITY_OVOMORPH_SHEAR.get(), soundSource, 1.0F, 1.0F);
        var resinBallItem = AlienVariantUtil.getResinBallFor(this);

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
        return isRooted();
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
        return !isRooted();
    }

    @Override
    public boolean isPushable() {
        return !isRooted();
    }

    @Override
    public boolean isPersistenceRequired() {
        return super.isPersistenceRequired() || !isRooted();
    }

    @Override
    protected boolean canHeal() {
        return !hatchManager.isHatching()
            && !hatchManager.isHatched()
            && super.canHeal();
    }

    @Override
    protected float getHealthRegenPerSecond() {
        return AVP.config.statsConfigs.OVAMORPH_STATS.healthRegenPerSecond;
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        hatchManager.load(compoundTag);

        if (compoundTag.contains(IS_ROOTED_KEY)) {
            setRooted(compoundTag.getBoolean(IS_ROOTED_KEY));
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        hatchManager.save(compoundTag);

        compoundTag.putBoolean(IS_ROOTED_KEY, isRooted());
    }

    public HatchManager hatchManager() {
        return hatchManager;
    }

    public boolean isRooted() {
        return entityData.get(ROOTED);
    }

    public void setRooted(boolean isRooted) {
        entityData.set(ROOTED, isRooted);
    }

    public OvomorphAnimationDispatcher getAnimationDispatcher() {
        return animationDispatcher;
    }
}
