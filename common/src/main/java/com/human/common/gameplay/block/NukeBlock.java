package com.human.common.gameplay.block;

import com.human.common.gameplay.entity.nuke.PrimedNuke;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class NukeBlock extends Block {

    public static final MapCodec<NukeBlock> CODEC = simpleCodec(NukeBlock::new);

    public static final BooleanProperty UNSTABLE = BlockStateProperties.UNSTABLE;

    public NukeBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(UNSTABLE, false));
    }

    @Override
    protected @NotNull MapCodec<? extends NukeBlock> codec() {
        return CODEC;
    }

    @Override
    public boolean dropFromExplosion(@NotNull Explosion explosion) {
        return false;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(UNSTABLE);
    }

    @Override
    protected void neighborChanged(
        @NotNull BlockState blockState,
        Level level,
        @NotNull BlockPos blockPos,
        @NotNull Block block,
        @NotNull BlockPos blockPos2,
        boolean bl
    ) {
        if (level.hasNeighborSignal(blockPos)) {
            this.summonNuke(level, blockPos);
        }
    }

    @Override
    protected void onPlace(BlockState blockState, @NotNull Level level, @NotNull BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (!blockState2.is(blockState.getBlock()) && level.hasNeighborSignal(blockPos)) {
            this.summonNuke(level, blockPos);
        }
    }

    @Override
    public @NotNull BlockState playerWillDestroy(
        Level level,
        @NotNull BlockPos blockPos,
        @NotNull BlockState blockState,
        @NotNull Player player
    ) {
        if (!level.isClientSide() && !player.isCreative() && blockState.getValue(UNSTABLE)) {
            this.summonNuke(level, blockPos);
        }

        return super.playerWillDestroy(level, blockPos, blockState, player);
    }

    @Override
    protected void onProjectileHit(
        Level level,
        @NotNull BlockState blockState,
        @NotNull BlockHitResult blockHitResult,
        @NotNull Projectile projectile
    ) {
        if (!level.isClientSide) {
            var blockPos = blockHitResult.getBlockPos();
            if (projectile.isOnFire() && projectile.mayInteract(level, blockPos)) {
                this.summonNuke(level, blockPos);
            } else if (projectile.mayInteract(level, blockPos)) {
                blockState.setValue(UNSTABLE, true);
            }
        }
    }

    private void summonNuke(Level level, BlockPos blockPos) {
        var nuke = new PrimedNuke(level);
        nuke.setPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        nuke.setFuse(300);
        level.addFreshEntity(nuke);
        level.removeBlock(blockPos, false);
    }
}
