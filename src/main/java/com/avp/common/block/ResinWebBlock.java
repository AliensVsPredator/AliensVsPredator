package com.avp.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import com.avp.common.entity.AVPEntityTypeTags;

public class ResinWebBlock extends Block {

    private static final VoxelShape SHAPE = Block.box(0.8, 0.0, 0.8, 15.2, 16.0, 15.2);

    private static final Vec3 MOVEMENT_MODIFIER = new Vec3(0.05, 0.05F, 0.05);

    public ResinWebBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull VoxelShape getShape(
        BlockState blockState,
        BlockGetter blockGetter,
        BlockPos blockPos,
        CollisionContext collisionContext
    ) {
        return SHAPE;
    }

    @Override
    protected void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
        if (!entity.getType().is(AVPEntityTypeTags.ALIENS)) {
            entity.makeStuckInBlock(blockState, MOVEMENT_MODIFIER);
        }
    }
}
