package com.avp.common.block;

import com.avp.common.block.base.BaseBlockEntity;
import com.avp.common.block.entity.TripMineBE;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TripMineBlock extends BaseBlockEntity {

    public static final MapCodec<TripMineBlock> CODEC = simpleCodec(TripMineBlock::new);

    public TripMineBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new TripMineBE(blockPos, blockState);
    }

    @Override
    protected boolean isTickingBE() {
        return true;
    }

    @Override
    protected @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Block.box(4, 0, 4, 12, 2.2, 11);
    }
}
