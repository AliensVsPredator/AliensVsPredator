package com.human.common.gameplay.block.power;

import com.human.common.gameplay.block.entity.power.impl.DeskTerminalBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DeskTerminalBlock extends PowerNodeEntityBlock<DeskTerminalBlockEntity> {

    public static final MapCodec<DeskTerminalBlock> CODEC = simpleCodec(DeskTerminalBlock::new);

    public static final DirectionProperty FACING = DirectionalBlock.FACING;

    public DeskTerminalBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected @NotNull BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected @NotNull VoxelShape getShape(
        BlockState state,
        @NotNull BlockGetter level,
        @NotNull BlockPos pos,
        @NotNull CollisionContext context
    ) {
        var facing = state.getValue(FACING);
        var southShape = Shapes.join(
            Block.box(1, 0, 6, 15, 13, 17),
            Block.box(2, 0, 1, 14, 2, 7),
            BooleanOp.OR
        );

        return switch (facing) {
            case EAST -> rotateShape(southShape, Rotation.CLOCKWISE_90);
            case WEST -> rotateShape(southShape, Rotation.COUNTERCLOCKWISE_90);
            case SOUTH -> rotateShape(southShape, Rotation.CLOCKWISE_180);
            default -> southShape;
        };
    }

    public static VoxelShape rotateShape(VoxelShape shape, Rotation rotation) {
        var aabbList = shape.toAabbs();
        var rotatedShapes = aabbList.stream()
            .map(box -> rotateBox(box, rotation))
            .map(Shapes::create)
            .toArray(VoxelShape[]::new);
        var combined = Shapes.empty();

        for (var rotatedShape : rotatedShapes) {
            combined = Shapes.or(combined, rotatedShape);
        }

        return combined;
    }

    public static AABB rotateBox(AABB box, Rotation rotation) {
        var x1 = box.minX;
        var y1 = box.minY;
        var z1 = box.minZ;
        var x2 = box.maxX;
        var y2 = box.maxY;
        var z2 = box.maxZ;

        return switch (rotation) {
            case CLOCKWISE_90 -> new AABB(1 - z2, y1, x1, 1 - z1, y2, x2);
            case COUNTERCLOCKWISE_90 -> new AABB(z1, y1, 1 - x2, z2, y2, 1 - x1);
            case CLOCKWISE_180 -> new AABB(1 - x2, y1, 1 - z2, 1 - x1, y2, 1 - z1);
            default -> box;
        };
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new DeskTerminalBlockEntity(blockPos, blockState);
    }
}
