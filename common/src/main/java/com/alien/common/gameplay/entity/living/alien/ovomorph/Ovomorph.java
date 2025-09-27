package com.alien.common.gameplay.entity.living.alien.ovomorph;

import com.alien.common.data.AlienVariantTypes;
import com.alien.common.gameplay.entity.living.alien.Alien;
import com.alien.common.model.alien.HatchState;
import com.alien.common.model.alien.variant.AlienVariant;
import com.alien.common.registry.init.AlienEntityTypes;
import com.just.core.functional.option.Option;
import com.lib.common.gameplay.entity.manager.VibrationSystemManager;
import com.lib.common.network.DataAccessor;
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
import com.avp.common.registry.init.AVPDataKeys;
import com.avp.common.registry.init.AVPSoundEvents;
import com.avp.common.registry.tag.AVPEntityTypeTags;
import com.avp.common.util.AVPPredicates;

public class Ovomorph extends Alien implements Shearable {

    public static final HatchState DEFAULT_HATCH_STATE = HatchState.SLEEPING;

    public static AttributeSupplier.Builder createOvomorphAttributes() {
        return applyFrom(AVP.config.statsConfigs.OVOMORPH_STATS, Monster.createMonsterAttributes());
    }

    public final DataAccessor<Byte> hatchStateId;

    public final DataAccessor<Byte> maxSpawnCount;

    public final DataAccessor<Boolean> isRooted;

    private final OvomorphAnimationDispatcher animationDispatcher;

    private final HatchManager hatchManager;

    public boolean pickupRequestAcknowledged;

    public boolean wantsPickup;

    public Ovomorph(EntityType<? extends Ovomorph> entityType, Level level) {
        super(entityType, level);

        this.hatchStateId = new DataAccessor<>(this, AVPDataKeys.OVOMORPH_HATCH_STATE);
        this.maxSpawnCount = new DataAccessor<>(this, AVPDataKeys.OVOMORPH_MAXIMUM_SPAWN_COUNT);
        this.isRooted = new DataAccessor<>(this, AVPDataKeys.OVOMORPH_IS_ROOTED);

        this.animationDispatcher = new OvomorphAnimationDispatcher(this);
        this.hatchManager = new HatchManager(this, 3 * 20, 3 * 20);
        this.config = AVP.config.statsConfigs.OVOMORPH_STATS;
        this.wantsPickup = false;
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
//            goap.update(this);

            this.wantsPickup = canBePickedUp();

            if (!pickupRequestAcknowledged && wantsPickup && tickCount % 20 == 0) {
                var alienVariantType = AlienVariantTypes.getFor(this);
                var deferredHolder = alienVariantType.eggPickupRequestEvent();

                if (deferredHolder != null) {
                    gameEvent(deferredHolder.getHolder());
                }
            }

            if (isPassenger()) {
                this.pickupRequestAcknowledged = false;
            }
        }
    }

    public boolean canBeHeld() {
        return isAlive()
            && !isDeadOrDying()
            && !isRooted.get()
            && getHatchState().contains(HatchState.SLEEPING);
    }

    public boolean canBePickedUp() {
        return canBeHeld()
            && onGround()
            && !isPassenger();
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

        if (isRooted.get() && itemStack.is(Items.SHEARS)) {
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

        if (
            // Entity is not an alien...
            !entity.getType().is(AVPEntityTypeTags.ALIENS)
                // OR entity is an ovomorph.
                || entity.getType().is(AVPEntityTypeTags.OVOMORPHS)
        ) {
            super.doPush(entity);
        }
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
