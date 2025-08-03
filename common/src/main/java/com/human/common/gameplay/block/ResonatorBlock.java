package com.human.common.gameplay.block;

import com.alien.common.data.AlienVariantTypes;
import com.human.common.gameplay.block.entity.ResonatorBlockEntity;
import com.human.common.gameplay.block.power.PowerConsumerEntityBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicInteger;

import com.avp.AVP;
import com.avp.common.registry.tag.AVPBlockTags;

public class ResonatorBlock extends PowerConsumerEntityBlock<ResonatorBlockEntity> {

    public static final MapCodec<ResonatorBlock> CODEC = simpleCodec(ResonatorBlock::new);

    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;

    public ResonatorBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(TRIGGERED, Boolean.FALSE));
    }

    @Override
    public void unpoweredTick(Level level, BlockPos blockPos, BlockState blockState, ResonatorBlockEntity resonatorBlockEntity) {
        resonatorBlockEntity.getAnimationDispatcher().unpowered(resonatorBlockEntity);
    }

    @Override
    public void poweredTick(Level level, BlockPos blockPos, BlockState blockState, ResonatorBlockEntity resonatorBlockEntity) {
        resonatorBlockEntity.getAnimationDispatcher().powered(resonatorBlockEntity);

        resonatorBlockEntity.incrementTickCounter();

        var tickValue = AVP.config.blockConfigs.RESONATOR_REPLACE_TICKS;

        if (resonatorBlockEntity.getTickCounter() % tickValue != 0) {
            return;
        }

        var radius = AVP.config.blockConfigs.RESONATOR_REPLACE_RADIUS;

        var resinBallsGained = new AtomicInteger(0);

        BlockPos.betweenClosedStream(blockPos.offset(-radius, -radius, -radius), blockPos.offset(radius, radius, radius))
            .forEach(currentPos -> {
                var currentState = level.getBlockState(currentPos);

                AlienVariantTypes.getFor(currentState)
                    .ifSome(alienVariantType -> {
                        // TODO: Use variant-specific tag here.
                        if (currentState.is(AVPBlockTags.RESIN_VEINS)) {
                            level.setBlockAndUpdate(currentPos, Blocks.AIR.defaultBlockState());

                            var resinBallItem = alienVariantType.resinBall().get();
                            resonatorBlockEntity.addResinBallItem(resinBallItem);

                            resinBallsGained.incrementAndGet();

                            if (resinBallsGained.get() > 0) {
                                resonatorBlockEntity.setChanged();
                            }

                            return;
                        }

                        // TODO: Use variant-specific tag here.
                        if (currentState.is(AVPBlockTags.RESIN)) {
                            // TODO: This is not a safe assumption to make!
                            var isDeepstone = currentPos.getY() <= 0;
                            var replacementBlock = isDeepstone ? Blocks.DEEPSLATE : Blocks.STONE;

                            level.setBlockAndUpdate(currentPos, replacementBlock.defaultBlockState());

                            var resinBallItem = alienVariantType.resinBall().get();
                            resonatorBlockEntity.addResinBallItem(resinBallItem);

                            resinBallsGained.incrementAndGet();

                            if (resinBallsGained.get() > 0) {
                                resonatorBlockEntity.setChanged();
                            }
                        }
                    });
            });
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TRIGGERED);
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
    protected boolean hasAnalogOutputSignal(@NotNull BlockState state) {
        return true;
    }

    @Override
    protected void neighborChanged(
        BlockState state,
        Level level,
        @NotNull BlockPos pos,
        @NotNull Block neighborBlock,
        @NotNull BlockPos neighborPos,
        boolean movedByPiston
    ) {
        var hasSignal = level.hasNeighborSignal(pos) || level.hasNeighborSignal(pos.above());
        var triggeredValue = state.getValue(TRIGGERED);

        if (hasSignal && !triggeredValue) {
            level.scheduleTick(pos, this, 4);
            level.setBlock(pos, state.setValue(TRIGGERED, Boolean.TRUE), 2);
        } else if (!hasSignal && triggeredValue) {
            level.setBlock(pos, state.setValue(TRIGGERED, Boolean.FALSE), 2);
        }
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
