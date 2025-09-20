package com.human.common.gameplay.block.power;

import com.human.common.gameplay.block.entity.power.impl.ResonatorBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ResonatorBlock extends PowerNodeEntityBlock<ResonatorBlockEntity> {

    public static final MapCodec<ResonatorBlock> CODEC = simpleCodec(ResonatorBlock::new);

    public ResonatorBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new ResonatorBlockEntity(blockPos, blockState);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(
        @NotNull BlockState state,
        Level level,
        @NotNull BlockPos pos,
        @NotNull Player player,
        @NotNull BlockHitResult hitResult
    ) {
        if (!level.isClientSide) {
            var blockEntity = level.getBlockEntity(pos);

            if (blockEntity instanceof ResonatorBlockEntity resonatorBlockEntity) {
                resonatorBlockEntity.onRightClick(player);
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void onRemove(
        @NotNull BlockState state,
        @NotNull Level level,
        @NotNull BlockPos pos,
        @NotNull BlockState newState,
        boolean isMoving
    ) {
        if (!state.is(newState.getBlock())) {
            var blockEntity = level.getBlockEntity(pos);

            if (blockEntity instanceof ResonatorBlockEntity resonatorBlockEntity) {
                resonatorBlockEntity.getResinBallCounts().forEach((resinBallItem, count) -> {
                    if (count > 0) {
                        var resinBallStack = new ItemStack(resinBallItem, count);
                        var resinBallEntity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, resinBallStack);

                        level.addFreshEntity(resinBallEntity);
                    }
                });
            }

            super.onRemove(state, level, pos, newState, isMoving);
        }
    }
}
