package com.avp.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.*;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import com.avp.AVP;
import com.avp.common.block.AVPBlockTags;
import com.avp.common.block.SentryTurretBlock;
import com.avp.common.damage.AVPDamageTypes;
import com.avp.common.sound.AVPSoundEvents;
import com.avp.server.BlockBreakProgressManager;

public class SentryTurretBE extends BlockEntity {

    public Monster targetedMonster;

    private UUID targetedMonsterUUID;

    protected final SentryTurretAnimDispatcher animDispatcher;

    protected int fireCooldown = 0;

    public SentryTurretBE(BlockPos pos, BlockState blockState) {
        super(BlockEntityTypes.SENTRY_TURRET_BE, pos, blockState);
        animDispatcher = new SentryTurretAnimDispatcher();
    }

    public UUID getTargetedMonsterUUID() {
        return targetedMonsterUUID;
    }

    public Monster getTargetedMonster() {
        if (this.targetedMonster == null && this.targetedMonsterUUID != null) {
            var entities = level.getEntitiesOfClass(Monster.class, new AABB(this.worldPosition).inflate(32)); // Adjust
                                                                                                              // range
                                                                                                              // if
                                                                                                              // needed
            for (var entity : entities) {
                if (entity.getUUID().equals(this.targetedMonsterUUID)) {
                    this.targetedMonster = entity;
                    break;
                }
            }
        }
        return this.targetedMonster;
    }

    public void setTargetedMonster(Monster monster) {
        if (this.targetedMonster == monster) {
            return;
        }

        this.targetedMonster = monster;
        this.targetedMonsterUUID = (monster != null) ? monster.getUUID() : null;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, SentryTurretBE blockEntity) {
        if (level.isClientSide) {
            return;
        }

        if (!(level.hasNeighborSignal(pos) || level.hasNeighborSignal(pos.above()))) {
            blockEntity.animDispatcher.unpowered(blockEntity);
            blockEntity.setTargetedMonster(null);
            return;
        }

        if (blockEntity.fireCooldown > 0) {
            blockEntity.fireCooldown--;
            return;
        }

        var range = 32;
        var facing = state.getValue(BlockStateProperties.FACING).getNormal();
        var facingVec = new Vec3(facing.getX(), facing.getY(), facing.getZ());
        var monsters = level.getEntitiesOfClass(Monster.class, new AABB(pos).inflate(range), Entity::isAlive);

        var currentTarget = blockEntity.getTargetedMonster();
        if (currentTarget != null) {
            boolean validTarget = currentTarget.isAlive() &&
                currentTarget.blockPosition().closerThan(pos, range) &&
                isFacingMonster(level, pos, facingVec, currentTarget);

            if (validTarget) {
                if (blockEntity.fireCooldown == 0) {
                    if (canTargetMonster(level, pos, facingVec, currentTarget, range, blockEntity)) {
                        blockEntity.setTargetedMonster(currentTarget);
                        blockEntity.animDispatcher.firing(blockEntity);
                        blockEntity.fireCooldown = 2;
                        return;
                    }
                    blockEntity.fireCooldown = 2;
                    blockEntity.animDispatcher.firing(blockEntity);
                }
                return;
            } else {
                blockEntity.setTargetedMonster(null);
            }
        }

        if (currentTarget != null) {
            boolean validTarget = currentTarget.isAlive() &&
                currentTarget.blockPosition().closerThan(pos, range) &&
                isFacingMonster(level, pos, facingVec, currentTarget);
            if (validTarget) {
                if (blockEntity.fireCooldown == 0) {
                    if (canTargetMonster(level, pos, facingVec, currentTarget, range, blockEntity)) {
                        blockEntity.setTargetedMonster(currentTarget);
                        blockEntity.animDispatcher.firing(blockEntity);
                        blockEntity.fireCooldown = 2;
                        return;
                    }
                    blockEntity.fireCooldown = 2;
                    blockEntity.animDispatcher.firing(blockEntity);
                }
                return;
            } else {
                blockEntity.setTargetedMonster(null);
            }
        }

        if (blockEntity.getTargetedMonster() == null) {
            for (var monster : monsters) {
                if (canTargetMonster(level, pos, facingVec, monster, range, blockEntity)) {
                    blockEntity.setTargetedMonster(monster);
                    blockEntity.animDispatcher.firing(blockEntity);
                    blockEntity.fireCooldown = 2;
                    return;
                }
            }
        }

        blockEntity.animDispatcher.idle(blockEntity);
    }

    private static boolean isFacingMonster(Level level, BlockPos turretPos, Vec3 facingVec, Monster monster) {
        var turretCenter = Vec3.atCenterOf(turretPos);
        var entityPos = Vec3.atCenterOf(monster.blockPosition());
        var directionToEntity = entityPos.subtract(turretCenter).normalize();

        var dotProduct = directionToEntity.dot(facingVec.normalize());
        return dotProduct > Math.cos(Math.toRadians(75));
    }

    private static boolean canTargetMonster(
        Level level,
        BlockPos turretPos,
        Vec3 facingVec,
        Monster monster,
        double maxRange,
        SentryTurretBE blockEntity
    ) {
        var facing = blockEntity.getBlockState().getValue(SentryTurretBlock.FACING);
        var offsetPosition = turretPos.relative(facing);
        var turretCenter = Vec3.atCenterOf(offsetPosition);
        var entityPos = Vec3.atCenterOf(monster.blockPosition());
        var distance = entityPos.distanceTo(turretCenter);
        var turretToMonsterVec = entityPos.subtract(turretCenter);
        if (turretToMonsterVec.lengthSqr() == 0) {
            return false;
        }
        var directionToEntity = turretToMonsterVec.normalize();

        if (distance > maxRange) {
            return false;
        }

        var dotProduct = directionToEntity.dot(facingVec.normalize());
        if (dotProduct <= Math.cos(Math.toRadians(75))) {
            return false;
        }

        var result = level.clip(
            new ClipContext(
                turretCenter,
                entityPos,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                monster
            )
        );

        if (result.getType() == HitResult.Type.BLOCK) {
            var blockPos = result.getBlockPos();
            if (isMonsterBehindBlock(level, turretPos, monster, blockPos)) {
                onBlockHit(level, blockPos, blockEntity);
                blockEntity.fireCooldown = 2;
            }
            return true;
        }

        onEntityHit(level, monster, monster.blockPosition());
        return true;
    }

    private static boolean isMonsterBehindBlock(Level level, BlockPos turretPos, Monster monster, BlockPos blockPos) {
        var turretCenter = Vec3.atCenterOf(turretPos);
        var blockCenter = Vec3.atCenterOf(blockPos);
        var monsterPos = monster.position();

        return blockCenter.distanceTo(turretCenter) < monsterPos.distanceTo(turretCenter);
    }

    private static void onBlockHit(@NotNull Level level, @NotNull BlockPos blockPos, @NotNull SentryTurretBE blockEntity) {
        if (!AVP.config.weaponConfigs.BULLETS_DAMAGE_BLOCKS_ENABLED) {
            return;
        }

        if (!level.getGameRules().getBoolean(GameRules.RULE_PROJECTILESCANBREAKBLOCKS)) {
            return;
        }

        var blockState = level.getBlockState(blockPos);

        if (blockState.is(AVPBlockTags.SHOULD_NOT_BE_DESTROYED)) {
            return;
        }

        blockEntity.animDispatcher.firing(blockEntity);
        level.playSound(null, blockPos, AVPSoundEvents.WEAPON_GENERIC_SHOOT, SoundSource.BLOCKS, 1.0F, 1.0F);
        BlockBreakProgressManager.damage(level, blockPos, 1F);
    }

    private static void onEntityHit(@NotNull Level level, Entity hitEntity, @NotNull BlockPos blockPos) {
        var registry = hitEntity.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);
        var damageSource = new DamageSource(registry.getHolderOrThrow(AVPDamageTypes.BULLET), hitEntity);

        hitEntity.hurt(damageSource, 1F);
        level.playSound(null, blockPos, AVPSoundEvents.WEAPON_GENERIC_SHOOT, SoundSource.BLOCKS, 1.0F, 1.0F);
        if (hitEntity instanceof LivingEntity livingEntity) {
            livingEntity.invulnerableTime = 0;
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (this.targetedMonsterUUID != null) {
            tag.putUUID("TargetedMonster", this.targetedMonsterUUID);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        if (tag.hasUUID("TargetedMonster")) {
            this.targetedMonsterUUID = tag.getUUID("TargetedMonster");
        }
    }
}
