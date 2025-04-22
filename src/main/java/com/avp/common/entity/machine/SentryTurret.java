package com.avp.common.entity.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import com.avp.AVP;
import com.avp.common.block.AVPBlocks;
import com.avp.common.block.entity.AmmoChestBlockEntity;
import com.avp.common.damage.AVPDamageTypes;
import com.avp.common.damage.AVPDamageTypesTags;
import com.avp.common.sound.AVPSoundEvents;

public class SentryTurret extends Mob implements TraceableEntity {

    public static float DAMAGE = AVP.config.blockConfigs.TURRET_DAMAGE;

    public static int RANGE = AVP.config.blockConfigs.TURRET_RANGE;

    protected static int AMMO_CHEST_RANGE = AVP.config.blockConfigs.TURRET_AMMOCHEST_SEARCH_RANGE;

    protected static int FOV = AVP.config.blockConfigs.TURRET_FOV;

    @Nullable
    private UUID ownerUUID;

    @Nullable
    private Entity cachedOwner;

    private Monster targetedMonster;

    private int fireCooldown = 0;

    protected final SentryTurretAnimDispatcher animDispatcher;

    public SentryTurret(EntityType<? extends Mob> entityType, Level level) {
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

    public static AttributeSupplier.Builder createSentryTurretAttributes() {
        return LivingEntity.createLivingAttributes()
            .add(Attributes.MAX_HEALTH, 16.0F)
            .add(Attributes.MOVEMENT_SPEED, 0.0F)
            .add(Attributes.KNOCKBACK_RESISTANCE, 100.0D)
            .add(Attributes.FOLLOW_RANGE, RANGE);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(1, new LookAtPlayerGoal(this, Monster.class, 8.0F));
    }

    @Override
    public float getPreciseBodyRotation(float partialTick) {
        return 0.0F;
    }

    @Override
    public void tick() {
        super.tick();

        if (fireCooldown > 0) {
            fireCooldown--;
            return;
        }

        if (!this.level().isClientSide) {
            if (!isPoweredByRedstone()) {
                animDispatcher.unpowered();
                return;
            } else if (getTargetedMonster() == null) {
                animDispatcher.idle();
            }

            var ammoChestBlockEntity = findNearbyAmmoChest(this, blockPosition());
            if (ammoChestBlockEntity == null || !ammoChestBlockEntity.hasAmmo()) {
                setTargetedMonster(null);
                animDispatcher.idle();
                return;
            }

            if (getTargetedMonster() != null && !getTargetedMonster().isAlive()) {
                setTargetedMonster(null);
                animDispatcher.idle();
                return;
            }

            if (ammoChestBlockEntity.hasAmmo() && tickCount % 10 == 0) {
                targetAndFire(ammoChestBlockEntity);
            }
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        if (compound.hasUUID("Owner")) {
            this.ownerUUID = compound.getUUID("Owner");
            this.cachedOwner = null;
        }
        if (compound.hasUUID("Target")) {
            this.targetedMonster = null;
        }
        this.fireCooldown = compound.getInt("FireCooldown");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
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
    public boolean displayFireAnimation() {
        return false;
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effectInstance) {
        // Sentry turrets aren't affected by any effects, even positive ones. It doesn't make sense for a turret
        // to have regeneration or absorption as much as it doesn't make sense for them to have nausea or wither.
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is((AVPDamageTypesTags.DOES_NOT_HURT_SENTRY_TURRETS))) {
            return false;
        }
        return super.hurt(source, amount);
    }

    @Override
    public boolean isPersistenceRequired() {
        return true;
    }

    @Override
    protected @NotNull InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.level().isClientSide && this.getOwner() != null && this.getOwner().is(player)) {
            this.dropTurretItem();
            this.discard();
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public boolean canBeCollidedWith() {
        return this.isAlive();
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
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

    private void targetAndFire(AmmoChestBlockEntity ammoChestBlockEntity) {
        if (this.targetedMonster != null && (!this.targetedMonster.isAlive() || this.targetedMonster.isRemoved())) {
            setTargetedMonster(null);
            this.fireCooldown = 0;
            this.animDispatcher.idle();
            return;
        }
        if (this.getTargetedMonster() == null) {
            findTarget();
        }

        if (this.getTargetedMonster() != null) {
            fireAtTarget(ammoChestBlockEntity);
        }
    }

    private void findTarget() {
        var monsters = this.level()
            .getEntitiesOfClass(
                Monster.class,
                this.getBoundingBox().inflate(RANGE),
                this::canTargetMonster
            );

        if (!monsters.isEmpty()) {
            this.setTargetedMonster(monsters.getFirst());
        }
    }

    private void fireAtTarget(AmmoChestBlockEntity ammoChestBlockEntity) {
        var target = getTargetedMonster();
        if (
            target != null && target.isAlive() && isFacingMonster(blockPosition(), getLookAngle(), target) && this.getSensing()
                .hasLineOfSight(target)
        ) {
            animDispatcher.firing();
            level().playSound(null, blockPosition(), AVPSoundEvents.WEAPON_GENERIC_SHOOT, SoundSource.BLOCKS, 1.0F, 1.0F);
            target.hurt(this.damageSources().source(AVPDamageTypes.BULLET, this), DAMAGE);
            target.setLastHurtMob(this);
            ammoChestBlockEntity.consumeAmmo(1);
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
        return monster.isAlive()
            && this.distanceTo(monster) <= RANGE
            && isFacingMonster(this.blockPosition(), this.getLookAngle(), monster)
            && this.getSensing().hasLineOfSight(monster);
    }

    private static boolean isFacingMonster(BlockPos turretPos, Vec3 facingVec, Monster monster) {
        var turretCenter = Vec3.atCenterOf(turretPos);
        var entityPos = Vec3.atCenterOf(monster.blockPosition());
        var directionToEntity = entityPos.subtract(turretCenter).normalize();

        var dotProduct = directionToEntity.dot(facingVec.normalize());
        return dotProduct > Math.cos(Math.toRadians(FOV));
    }

    @Nullable
    private static AmmoChestBlockEntity findNearbyAmmoChest(SentryTurret sentryTurret, BlockPos pos) {
        var level = sentryTurret.level();

        for (
            var searchRadius : BlockPos.betweenClosed(
                pos.offset(-AMMO_CHEST_RANGE, -AMMO_CHEST_RANGE, -AMMO_CHEST_RANGE),
                pos.offset(AMMO_CHEST_RANGE, AMMO_CHEST_RANGE, AMMO_CHEST_RANGE)
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
