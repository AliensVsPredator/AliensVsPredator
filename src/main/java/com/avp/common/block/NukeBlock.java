package com.avp.common.block;

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

import com.avp.common.block.entity.NukeBE;

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
    public boolean dropFromExplosion(Explosion explosion) {
        return false;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(UNSTABLE);
    }

    @Override
    protected void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
        if (level.hasNeighborSignal(blockPos)) {
            this.summonNukeBE(level, blockPos);
        }
    }

    @Override
    protected void onPlace(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (!blockState2.is(blockState.getBlock()) && level.hasNeighborSignal(blockPos)) {
            this.summonNukeBE(level, blockPos);
        }
    }

    @Override
    public @NotNull BlockState playerWillDestroy(Level level, BlockPos blockPos, BlockState blockState, Player player) {
        if (!level.isClientSide() && !player.isCreative() && Boolean.TRUE.equals(blockState.getValue(UNSTABLE))) {
            this.summonNukeBE(level, blockPos);
        }

        return super.playerWillDestroy(level, blockPos, blockState, player);
    }

    @Override
    protected void onProjectileHit(Level level, BlockState blockState, BlockHitResult blockHitResult, Projectile projectile) {
        if (!level.isClientSide) {
            var blockPos = blockHitResult.getBlockPos();
            if (projectile.isOnFire() && projectile.mayInteract(level, blockPos)) {
                this.summonNukeBE(level, blockPos);
            } else if (projectile.mayInteract(level, blockPos)) {
                blockState.setValue(UNSTABLE, true);
            }
        }
    }

    private void summonNukeBE(Level level, BlockPos blockPos) {
        var nukeBE = new NukeBE(level);
        nukeBE.setPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        nukeBE.setFuse(300);
        level.addFreshEntity(nukeBE);
        level.removeBlock(blockPos, false);
    }
}
