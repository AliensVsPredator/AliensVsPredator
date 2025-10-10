package com.human.common.gameplay.block.power;

import com.human.common.gameplay.power.PowerNode;
import com.human.common.gameplay.power.PowerSystem;
import com.lib.common.util.DirectionUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class CableBlock extends Block {

    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;

    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;

    public static final BooleanProperty EAST = BlockStateProperties.EAST;

    public static final BooleanProperty WEST = BlockStateProperties.WEST;

    public static final BooleanProperty UP = BlockStateProperties.UP;

    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;

    public static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = Map.of(
        Direction.NORTH,
        NORTH,
        Direction.SOUTH,
        SOUTH,
        Direction.EAST,
        EAST,
        Direction.WEST,
        WEST,
        Direction.UP,
        UP,
        Direction.DOWN,
        DOWN
    );

    private static final VoxelShape CORE = box(6, 6, 6, 10, 10, 10);

    private static final VoxelShape SIDE_NORTH = box(6, 6, 0, 10, 10, 6);

    private static final VoxelShape SIDE_SOUTH = box(6, 6, 10, 10, 10, 16);

    private static final VoxelShape SIDE_EAST = box(10, 6, 6, 16, 10, 10);

    private static final VoxelShape SIDE_WEST = box(0, 6, 6, 6, 10, 10);

    private static final VoxelShape SIDE_UP = box(6, 10, 6, 10, 16, 10);

    private static final VoxelShape SIDE_DOWN = box(6, 0, 6, 10, 6, 10);

    // bit order: N,S,E,W,U,D
    private static final VoxelShape[] SHAPES_BY_MASK = makeShapes();

    private static VoxelShape[] makeShapes() {
        var voxelShapes = new VoxelShape[64];

        for (var mask = 0; mask < 64; mask++) {
            var voxelShape = CORE;

            if ((mask & 1) != 0) {
                voxelShape = Shapes.or(voxelShape, SIDE_NORTH);
            }

            if ((mask & 2) != 0) {
                voxelShape = Shapes.or(voxelShape, SIDE_SOUTH);
            }

            if ((mask & 4) != 0) {
                voxelShape = Shapes.or(voxelShape, SIDE_EAST);
            }

            if ((mask & 8) != 0) {
                voxelShape = Shapes.or(voxelShape, SIDE_WEST);
            }

            if ((mask & 16) != 0) {
                voxelShape = Shapes.or(voxelShape, SIDE_UP);
            }

            if ((mask & 32) != 0) {
                voxelShape = Shapes.or(voxelShape, SIDE_DOWN);
            }

            // merge boxes to reduce raytrace cost.
            voxelShapes[mask] = voxelShape.optimize();
        }
        return voxelShapes;
    }

    private static int maskFromState(BlockState s) {
        var mask = 0;

        if (s.getValue(NORTH)) {
            mask |= 1;
        }

        if (s.getValue(SOUTH)) {
            mask |= 2;
        }

        if (s.getValue(EAST)) {
            mask |= 4;
        }

        if (s.getValue(WEST)) {
            mask |= 8;
        }

        if (s.getValue(UP)) {
            mask |= 16;
        }

        if (s.getValue(DOWN)) {
            mask |= 32;
        }

        return mask;
    }

    public CableBlock(Properties properties) {
        super(properties);

        registerDefaultState(
            this.defaultBlockState()
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(EAST, false)
                .setValue(WEST, false)
                .setValue(UP, false)
                .setValue(DOWN, false)
        );
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        var level = context.getLevel();
        var pos = context.getClickedPos();

        var state = this.defaultBlockState();

        for (var direction : DirectionUtil.VALUES) {
            var neighborPos = pos.relative(direction);
            state = state.setValue(PROPERTY_BY_DIRECTION.get(direction), canConnectTo(level, neighborPos));
        }

        return state;
    }

    @Override
    public @NotNull BlockState updateShape(
        BlockState state,
        @NotNull Direction direction,
        @NotNull BlockState neighborState,
        @NotNull LevelAccessor level,
        @NotNull BlockPos pos,
        @NotNull BlockPos neighborPos
    ) {
        return state.setValue(PROPERTY_BY_DIRECTION.get(direction), canConnectTo(level, neighborPos));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN);
    }

    @Override
    public @NotNull VoxelShape getShape(
        @NotNull BlockState state,
        @NotNull BlockGetter level,
        @NotNull BlockPos pos,
        @NotNull CollisionContext ctx
    ) {
        // used for outline + ray hit.
        return SHAPES_BY_MASK[maskFromState(state)];
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(
        @NotNull BlockState state,
        @NotNull BlockGetter level,
        @NotNull BlockPos pos,
        @NotNull CollisionContext ctx
    ) {
        // entities collide with the cable only.
        return SHAPES_BY_MASK[maskFromState(state)];
    }

    @Override
    public @NotNull VoxelShape getOcclusionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        // matches lighting/occlusion too
        return SHAPES_BY_MASK[maskFromState(state)];
    }

    @Override
    public boolean propagatesSkylightDown(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        // let skylight pass through to blocks below.
        return true;
    }

    @Override
    public int getLightBlock(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        // this block blocks 0 light (0..15).
        return 0;
    }

    @Override
    public boolean useShapeForLightOcclusion(@NotNull BlockState state) {
        return true;
    }

    // TODO: Deduplicate this code.
    @Override
    protected void onPlace(
        @NotNull BlockState state,
        @NotNull Level level,
        @NotNull BlockPos blockPos,
        @NotNull BlockState oldState,
        boolean movedByPiston
    ) {
        super.onPlace(state, level, blockPos, oldState, movedByPiston);

        if (level.isClientSide) {
            return;
        }

        var manager = PowerSystem.get((ServerLevel) level);

        for (var direction : DirectionUtil.VALUES) {
            var neighbor = blockPos.relative(direction);

            if (canConnectTo(level, neighbor)) {
                manager.union(blockPos, neighbor);
            }
        }
    }

    // TODO: Deduplicate this code.
    @Override
    protected void onRemove(
        @NotNull BlockState state,
        Level level,
        @NotNull BlockPos pos,
        @NotNull BlockState newState,
        boolean movedByPiston
    ) {
        if (!level.isClientSide && state.getBlock() != newState.getBlock()) {
            PowerSystem.get((ServerLevel) level).splitGrid(level, pos);
        }

        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    private boolean canConnectTo(LevelAccessor level, BlockPos neighbor) {
        return canConnectTo(level, neighbor, level.getBlockState(neighbor));
    }

    // TODO: Use block tags here.
    private boolean canConnectTo(LevelAccessor level, BlockPos neighbor, BlockState neighborState) {
        // Cable connects to other cables or valid machines.
        return neighborState.getBlock() instanceof CableBlock || level.getBlockEntity(neighbor) instanceof PowerNode;
    }
}
