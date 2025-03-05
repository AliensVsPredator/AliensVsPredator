package com.avp.core.common.manager;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.GameEventTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.level.gameevent.EntityPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// TODO: Refactor and PR to AzureLib.
public class VibrationUser implements VibrationSystem.User {

    protected final Mob mob;

    protected final int range;

    protected final PositionSource positionSource;

    public VibrationUser(Mob entity, int range) {
        this.positionSource = new EntityPositionSource(entity, entity.getEyeHeight());
        this.mob = entity;
        this.range = range;
    }

    @Override
    public int getListenerRadius() {
        return range;
    }

    @Override
    public @NotNull PositionSource getPositionSource() {
        return this.positionSource;
    }

    @Override
    public @NotNull TagKey<GameEvent> getListenableEvents() {
        return GameEventTags.WARDEN_CAN_LISTEN;
    }

    @Override
    public boolean canTriggerAvoidVibration() {
        return true;
    }

    @Override
    public boolean isValidVibration(Holder<GameEvent> gameEvent, @NotNull Context context) {
        if (!gameEvent.is(getListenableEvents())) {
            return false;
        }

        var entity = context.sourceEntity();

        if (
            entity != null
                && (entity.isSpectator()
                    || (entity.isSteppingCarefully() && gameEvent.is(GameEventTags.IGNORE_VIBRATIONS_SNEAKING))
                    || entity.dampensVibrations())
        ) {
            return false;
        }

        if (context.affectedState() != null) {
            return !context.affectedState().is(BlockTags.DAMPENS_VIBRATIONS);
        }

        return true;
    }

    @Override
    public boolean canReceiveVibration(
        @NotNull ServerLevel serverLevel,
        @NotNull BlockPos blockPos,
        @NotNull Holder<GameEvent> gameEvent,
        GameEvent.@NotNull Context context
    ) {
        if (
            mob.isNoAi()
                || mob.isDeadOrDying()
                || !mob.level().getWorldBorder().isWithinBounds(blockPos)
                || mob.isRemoved()
        ) {
            return false;
        }

        return !(context.sourceEntity() instanceof LivingEntity livingEntity) || canTargetEntity(livingEntity);
    }

    @Override
    public void onReceiveVibration(
        @NotNull ServerLevel serverLevel,
        @NotNull BlockPos blockPos,
        @NotNull Holder<GameEvent> gameEvent,
        @Nullable Entity entity,
        @Nullable Entity entity2,
        float f
    ) {
        // Do nothing.
    }

    public boolean canTargetEntity(@Nullable Entity entity) {
        return entity instanceof LivingEntity livingEntity
            && mob.level() == entity.level()
            && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(entity)
            && !mob.isVehicle()
            && !mob.isAlliedTo(entity)
            && livingEntity.getType() != EntityType.ARMOR_STAND
            && livingEntity.getType() != EntityType.WARDEN
            && !(livingEntity instanceof Bat)
            && !livingEntity.isInvulnerable()
            && !livingEntity.isDeadOrDying()
            && mob.level().getWorldBorder().isWithinBounds(livingEntity.getBoundingBox());
    }
}
