package com.avp.fabric.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import com.avp.fabric.common.block.entity.AVPBlockEntityTypes;
import com.avp.fabric.common.block.entity.IndustrialFurnaceBlockEntity;

public class IndustrialFurnaceBlock extends AbstractFurnaceBlock {

    public static final MapCodec<IndustrialFurnaceBlock> CODEC = IndustrialFurnaceBlock.simpleCodec(IndustrialFurnaceBlock::new);

    public IndustrialFurnaceBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends AbstractFurnaceBlock> codec() {
        return CODEC;
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return IndustrialFurnaceBlock.createFurnaceTicker(level, blockEntityType, AVPBlockEntityTypes.INDUSTRIAL_FURNACE);
    }

    @Override
    protected void openContainer(Level level, BlockPos blockPos, Player player) {
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity instanceof IndustrialFurnaceBlockEntity) {
            player.openMenu((MenuProvider) (blockEntity));
        }
    }

    @Nullable
    protected static <T extends BlockEntity> BlockEntityTicker<T> createFurnaceTicker(
        Level level,
        BlockEntityType<T> blockEntityType,
        BlockEntityType<? extends AbstractFurnaceBlockEntity> blockEntityType2
    ) {
        return level.isClientSide ? null : createTickerHelper(blockEntityType, blockEntityType2, IndustrialFurnaceBlockEntity::serverTick);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new IndustrialFurnaceBlockEntity(blockPos, blockState);
    }

    @Override
    public void animateTick(BlockState blockState, Level level, BlockPos blockPos, RandomSource randomSource) {
        if (!blockState.getValue(LIT).booleanValue()) {
            return;
        }
        double d = (double) blockPos.getX() + 0.5;
        double e = blockPos.getY();
        double f = (double) blockPos.getZ() + 0.5;
        if (randomSource.nextDouble() < 0.1) {
            level.playLocalSound(d, e, f, SoundEvents.BLASTFURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0f, 1.0f, false);
        }
        Direction direction = blockState.getValue(FACING);
        Direction.Axis axis = direction.getAxis();
        double g = 0.52;
        double h = randomSource.nextDouble() * 0.6 - 0.3;
        double i = axis == Direction.Axis.X ? (double) direction.getStepX() * 0.52 : h;
        double j = randomSource.nextDouble() * 9.0 / 16.0;
        double k = axis == Direction.Axis.Z ? (double) direction.getStepZ() * 0.52 : h;
        level.addParticle(ParticleTypes.SMOKE, d + i, e + j, f + k, 0.0, 0.0, 0.0);
    }

}
