package com.avp.common.entity.machine;

import com.avp.AVP;
import com.avp.common.block.AVPBlocks;
import com.avp.common.block.entity.AmmoChestBlockEntity;
import com.avp.common.damage.AVPDamageTypes;
import com.avp.common.sound.AVPSoundEvents;
import com.avp.common.util.GravityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * TODO:
 * - Fix a missing block appearing for a second when placed.
 * - Right click turret as owner and have it drop as item.
 * - Have it go back to powered/unpowered state when target is dead.
 * - Have it placed and facing whatever horizontal direction the player is looking at.
 * - Turn turret towards target when firing.
 */
public class SentryTurret extends Entity implements TraceableEntity {
    @Nullable
    private UUID ownerUUID;

    @Nullable
    private Entity cachedOwner;

    private Monster targetedMonster;

    private int fireCooldown = 0;

    public static int range = AVP.config.blockConfigs.TURRET_RANGE;

    public static float damage = AVP.config.blockConfigs.TURRET_DAMAGE;

    protected static int ammoChestRange = AVP.config.blockConfigs.TURRET_AMMOCHEST_SEARCH_RANGE;

    private UUID targetedMonsterUUID;

    protected final SentryTurretAnimDispatcher animDispatcher;

    public SentryTurret(EntityType<?> entityType, Level level) {
        super(entityType, level);
        animDispatcher = new SentryTurretAnimDispatcher(this);
    }

    public void setOwner(@Nullable Entity owner) {
        if (owner != null) {
            this.ownerUUID = owner.getUUID();
            this.cachedOwner = owner;
        }
    }

    @Nullable
    @Override
    public Entity getOwner() {
        if (this.cachedOwner != null && !this.cachedOwner.isRemoved()) {
            return this.cachedOwner;
        } else if (this.ownerUUID != null && this.level() instanceof ServerLevel serverLevel) {
            this.cachedOwner = serverLevel.getEntity(this.ownerUUID);
            return this.cachedOwner;
        } else {
            return null;
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (tickCount % 20 == 0) {
            return;
        }

        if (!this.level().isClientSide) {
            if (this.targetedMonsterUUID != null && this.targetedMonster == null) {
                this.targetedMonster = (Monster) ((ServerLevel) this.level()).getEntity(this.targetedMonsterUUID);
            }

            if (!horizontalCollision) {
                GravityUtil.apply(this);
            }

            if (fireCooldown > 0) {
                fireCooldown--;
                return;
            }

            if (!isPoweredByRedstone()) {
                animDispatcher.unpowered();
                return;
            }

            var ammoChestBlockEntity = findNearbyAmmoChest(this, blockPosition());
            if (ammoChestBlockEntity == null || !ammoChestBlockEntity.hasAmmo()) {
                setTargetedMonster(null);
                animDispatcher.idle();
                return;
            }

            if (getTargetedMonster() != null && !getTargetedMonster().isAlive()) {
                setTargetedMonster(null);
            }

            targetAndFire();
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) { /* NONE */}

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        if (compound.hasUUID("Owner")) {
            this.ownerUUID = compound.getUUID("Owner");
            this.cachedOwner = null;
        }
        if (compound.hasUUID("Target")) {
            this.targetedMonsterUUID = compound.getUUID("Target");
            this.targetedMonster = null;
        }
        this.fireCooldown = compound.getInt("FireCooldown");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        if (this.ownerUUID != null) {
            compound.putUUID("Owner", this.ownerUUID);
        }
        if (this.targetedMonster != null) {
            compound.putUUID("Target", this.targetedMonster.getUUID());
        }
        compound.putInt("FireCooldown", this.fireCooldown);

    }

    @Override
    public @Nullable ItemStack getPickResult() {
        return AVPBlocks.SENTRY_TURRET.asItem().getDefaultInstance();
    }

    @Override
    public @NotNull InteractionResult interactAt(Player player, Vec3 vec, InteractionHand hand) {
        if (!this.level().isClientSide && player.getUUID().equals(this.ownerUUID)) {
            this.dropTurretItem();
            this.discard();
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    private void dropTurretItem() {
        var turretItem = this.getPickResult();
        if (turretItem != null) {
            this.spawnAtLocation(turretItem, 0.5f);
        }
    }

    private boolean isPoweredByRedstone() {
        return this.level().hasNeighborSignal(this.blockPosition());
    }

    private void targetAndFire() {
        if (this.getTargetedMonster() == null) {
            findTarget();
        }

        if (this.getTargetedMonster() != null) {
            fireAtTarget();
        }
    }

    private void findTarget() {
        var monsters = this.level().getEntitiesOfClass(
                Monster.class,
                this.getBoundingBox().inflate(range),
                this::canTargetMonster
        );

        if (!monsters.isEmpty()) {
            this.setTargetedMonster(monsters.getFirst());
        }
    }

    private void fireAtTarget() {
        var target = getTargetedMonster();
        if (target != null && target.isAlive()) {
            animDispatcher.firing();
            level().playSound(null, blockPosition(), AVPSoundEvents.WEAPON_GENERIC_SHOOT, SoundSource.BLOCKS, 1.0F, 1.0F);
            target.hurt(this.damageSources().source(AVPDamageTypes.BULLET, this.getOwner()), damage);
            fireCooldown = 20;
        } else {
            setTargetedMonster(null);
        }
    }

    public Monster getTargetedMonster() {
        return this.targetedMonster;
    }

    public void setTargetedMonster(@Nullable Monster monster) {
        this.targetedMonster = monster;
    }

    private boolean canTargetMonster(Monster monster) {
        return monster.isAlive() && monster.distanceTo(this) <= range;
    }

    @Nullable
    private static AmmoChestBlockEntity findNearbyAmmoChest(SentryTurret sentryTurret, BlockPos pos) {
        var level = sentryTurret.level();

        for (
                var searchRadius : BlockPos.betweenClosed(
                pos.offset(-ammoChestRange, -ammoChestRange, -ammoChestRange),
                pos.offset(ammoChestRange, ammoChestRange, ammoChestRange)
        )
        ) {
            var ammoEntity = level.getBlockEntity(searchRadius);
            if (ammoEntity instanceof AmmoChestBlockEntity ammoChestBlockEntity) {
                return ammoChestBlockEntity;
            }
        }

        return null;
    }

}
