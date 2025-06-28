package com.alien.common.gameplay.entity.living.alien.parasite;

import com.alien.common.gameplay.entity.living.alien.Alien;
import com.alien.common.model.alien.FreeMob;
import com.lib.common.network.DataAccessor;
import com.mojang.serialization.Codec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import com.avp.common.util.AVPPredicates;

public abstract class Parasite extends Alien {

    public final DataAccessor<Boolean> isFertile = getDataContainer().<Boolean>builder("isFertile")
        .networkSynchronized(ByteBufCodecs.BOOL)
        .persistent(Codec.BOOL)
        .onLoad(this::handleFertilityChange)
        .onChange(this::handleFertilityChange)
        .build(true);

    protected final ParasiteAttachmentManager attachmentManager;

    protected Parasite(EntityType<? extends Parasite> entityType, Level level) {
        super(entityType, level);
        this.attachmentManager = new ParasiteAttachmentManager(this);
    }

    public void restoreAllGoals() {
        removeAllGoals(AVPPredicates.alwaysTrue());
        registerGoals();
    }

    @Override
    public void tick() {
        super.tick();
        attachmentManager.tick();

        if (!level().isClientSide) {
            var currentTarget = getTarget();

            if (currentTarget != null && !isValidHost(currentTarget)) {
                setTarget(null);
            }
        }
    }

    @Override
    public boolean doHurtTarget(@NotNull Entity entity) {
        if (canAttachToHost(entity)) {
            startRiding(entity, true);
        }

        return true;
    }

    protected boolean canAttachToHost(Entity entity) {
        return entity instanceof LivingEntity livingEntity &&
            isValidHost(livingEntity) &&
            !AVPPredicates.hasShield(entity) && !(this.isPassenger() || this.isVehicle());
    }

    protected boolean isValidHost(LivingEntity target) {
        return isFertile.get() && AVPPredicates.isFreeHost(this, target);
    }

    @Override
    public boolean startRiding(@NotNull Entity entity, boolean bl) {
        var isRiding = super.startRiding(entity, bl);

        if (isRiding) {
            // Will update the player's riding entities properly after the parasite detaches.
            tryUpdatePlayerRiding(entity);
        }

        return isRiding;
    }

    @Override
    public void stopRiding() {
        var host = attachmentManager.getHost();

        if (host instanceof Mob mob) {
            ((FreeMob) mob).restoreFreedom();
        }

        super.stopRiding();

        // Will update the player's riding entities properly after the parasite detaches.
        tryUpdatePlayerRiding(host);
    }

    @Override
    protected void doPush(@NotNull Entity entity) {
        super.doPush(entity);

        if (canAttachToHost(entity)) {
            startRiding(entity);
        }
    }

    private void tryUpdatePlayerRiding(Entity entity) {
        if (!level().isClientSide && entity instanceof ServerPlayer player) {
            player.connection.send(new ClientboundSetPassengersPacket(entity));
        }
    }

    @Override
    protected boolean canHeal() {
        return isFertile.get() && super.canHeal();
    }

    @Override
    protected boolean canBleedAcid() {
        return isFertile.get() || attachmentManager.isAttachedToHost();
    }

    @Override
    public boolean isPushable() {
        return isFertile.get();
    }

    public ParasiteAttachmentManager getAttachmentManager() {
        return attachmentManager;
    }

    private void handleFertilityChange(Boolean isFertile) {
        if (!isFertile) {
            ((FreeMob) this).removeFreedom();
            this.removeAllGoals(AVPPredicates.alwaysTrue());
        } else {
            ((FreeMob) this).restoreFreedom();
            this.restoreAllGoals();
        }
    }
}
