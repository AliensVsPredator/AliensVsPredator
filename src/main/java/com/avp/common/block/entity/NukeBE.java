package com.avp.common.block.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

import com.avp.AVP;
import com.avp.common.block.AVPBlocks;
import com.avp.common.entity.type.AVPEntityTypes;
import com.avp.common.util.ExplosionUtil;
import com.avp.server.ServerScheduler;

public class NukeBE extends Entity {

    private static final EntityDataAccessor<Integer> DATA_FUSE_ID = SynchedEntityData.defineId(NukeBE.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<BlockState> DATA_BLOCK_STATE_ID = SynchedEntityData.defineId(
        NukeBE.class,
        EntityDataSerializers.BLOCK_STATE
    );

    public NukeBE(EntityType<? extends Entity> entityType, Level level) {
        super(entityType, level);
        this.noCulling = true;
    }

    public NukeBE(Level level) {
        this(AVPEntityTypes.NUKE_BE, level);
    }

    public void setFuse(int i) {
        this.entityData.set(DATA_FUSE_ID, i);
    }

    public int getFuse() {
        return this.entityData.get(DATA_FUSE_ID);
    }

    public void setBlockState(BlockState blockState) {
        this.entityData.set(DATA_BLOCK_STATE_ID, blockState);
    }

    public BlockState getBlockState() {
        return this.entityData.get(DATA_BLOCK_STATE_ID);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_FUSE_ID, 80);
        builder.define(DATA_BLOCK_STATE_ID, AVPBlocks.NUKE_BLOCK.defaultBlockState());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        this.setFuse(compoundTag.getShort("fuse"));
        if (compoundTag.contains("block_state", 10)) {
            this.setBlockState(
                NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK), compoundTag.getCompound("block_state"))
            );
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.putShort("fuse", (short) this.getFuse());
        compoundTag.put("block_state", NbtUtils.writeBlockState(this.getBlockState()));
    }

    @Override
    protected Entity.@NotNull MovementEmission getMovementEmission() {
        return Entity.MovementEmission.NONE;
    }

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    @Override
    protected double getDefaultGravity() {
        return 0.04;
    }

    @Override
    public void tick() {
        this.applyGravity();
        this.move(MoverType.SELF, this.getDeltaMovement());
        this.setDeltaMovement(this.getDeltaMovement().scale(0.98));

        var fuseValue = this.getFuse() - 1;
        this.setFuse(fuseValue);
        if (fuseValue <= 0) {
            if (!this.level().isClientSide) {
                if (isNukeEnabled()) {
                    ServerScheduler.schedule(() -> {
                        var explosion = ExplosionUtil.createNuclearExplosion(
                            (ServerLevel) this.level(),
                            this.blockPosition().getCenter(),
                            16 * 8,
                            5
                        );
                        explosion.explode();
                    }, Duration.ofSeconds(1));
                }
                this.discard();
            }
        } else if (tickCount % 20 == 0) {
            // TODO: Change to custom sound
            this.level().playSound(null, this.blockPosition(), SoundEvents.SMOKER_SMOKE, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    public boolean isNukeEnabled() {
        return AVP.config.weaponConfigs.ENABLE_NUKE_BLOCK_MECHS;
    }
}
