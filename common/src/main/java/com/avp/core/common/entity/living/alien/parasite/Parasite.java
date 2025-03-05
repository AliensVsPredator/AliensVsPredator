package com.avp.core.common.entity.living.alien.parasite;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import com.avp.core.common.entity.living.FreeMob;
import com.avp.core.common.entity.living.alien.Alien;
import com.avp.core.common.item.AVPItems;
import com.avp.core.common.manager.ParasiteAttachmentManager;
import com.avp.core.common.util.AVPPredicates;

public abstract class Parasite extends Alien {

    private static final EntityDataAccessor<Boolean> IS_FERTILE = SynchedEntityData.defineId(
        Parasite.class,
        EntityDataSerializers.BOOLEAN
    );

    protected final ParasiteAttachmentManager attachmentManager;

    protected Parasite(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.attachmentManager = new ParasiteAttachmentManager(this, IS_FERTILE);
    }

    public void restoreAllGoals() {
        removeAllGoals(AVPPredicates.alwaysTrue());
        registerGoals();
    }

    @Override
    protected void defineSynchedData(@NotNull SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IS_FERTILE, true);
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
    public @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        if (itemStack.is(AVPItems.RAW_ROYAL_JELLY.get())) {
            if (!level().isClientSide && !attachmentManager.isFertile()) {
                attachmentManager.restore();
                player.getItemInHand(interactionHand).shrink(1);
                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.CONSUME;
            }
        } else {
            return super.mobInteract(player, interactionHand);
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
            !AVPPredicates.hasShield(entity);
    }

    protected boolean isValidHost(LivingEntity target) {
        return attachmentManager.isFertile() && AVPPredicates.isFreeHost(this, target);
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
        return attachmentManager.isFertile() && super.canHeal();
    }

    @Override
    protected boolean canBleedAcid() {
        return attachmentManager.isFertile() || attachmentManager.isAttachedToHost();
    }

    @Override
    public boolean isPushable() {
        return attachmentManager.isFertile();
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        attachmentManager.load(compoundTag);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        attachmentManager.save(compoundTag);
    }

    public ParasiteAttachmentManager attachmentManager() {
        return attachmentManager;
    }
}
