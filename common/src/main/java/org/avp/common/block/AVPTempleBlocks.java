package org.avp.common.block;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import org.avp.api.Holder;
import org.avp.api.block.BlockData;
import org.avp.api.block.BlockDataUtils;
import org.avp.common.registry.AVPDeferredBlockRegistry;

public class AVPTempleBlocks extends AVPDeferredBlockRegistry {

    public static final AVPTempleBlocks INSTANCE = new AVPTempleBlocks();

    public final BlockBehaviour.Properties BRICK_PROPERTIES = BlockBehaviour.Properties.ofFullCopy(
        Blocks.STONE
    )
        .strength(3.0F, 6.0F);

    public final BlockBehaviour.Properties SKULLS_PROPERTIES = BlockBehaviour.Properties.ofFullCopy(
        Blocks.BONE_BLOCK
    )
        .strength(3.0F, 6.0F);

    public final Holder<Block> BRICK;

    public final Holder<Block> BRICK_SLAB;

    public final Holder<Block> BRICK_STAIRS;

    public final Holder<Block> BRICK_WALL;

    public final Holder<Block> BRICK_CHESTBURSTER;

    public final Holder<Block> BRICK_FACEHUGGER;

    public final Holder<Block> BRICK_SINGLE;

    public final Holder<Block> BRICK_SINGLE_SLAB;

    public final Holder<Block> BRICK_SINGLE_STAIRS;

    public final Holder<Block> BRICK_SINGLE_WALL;

    public final Holder<Block> FLOOR;

    public final Holder<Block> FLOOR_SLAB;

    public final Holder<Block> FLOOR_STAIRS;

    public final Holder<Block> FLOOR_WALL;

    public final Holder<Block> SKULLS;

    public final Holder<Block> TILE;

    public final Holder<Block> TILE_SLAB;

    public final Holder<Block> TILE_STAIRS;

    public final Holder<Block> TILE_WALL;

    public final Holder<Block> WALL_BASE;

    @Override
    protected Holder<Block> createHolder(String registryName, BlockData.Builder blockDataBuilder) {
        return super.createHolder("temple_" + registryName, blockDataBuilder);
    }

    private AVPTempleBlocks() {
        var stone = List.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);

        Supplier<BlockData.Builder> pickProps = () -> BlockData.simple(BRICK_PROPERTIES).tags(stone);

        Function<Holder<Block>, BlockData.Builder> slabProps =
            parent -> BlockDataUtils.slab(parent, BRICK_PROPERTIES).tags(stone);
        Function<Holder<Block>, BlockData.Builder> stairProps =
            parent -> BlockDataUtils.stairs(parent, BRICK_PROPERTIES).tags(stone);
        Function<Holder<Block>, BlockData.Builder> wallProps =
            parent -> BlockDataUtils.wall(parent, BRICK_PROPERTIES).tags(stone);

        BRICK = createHolder("brick", pickProps.get());
        BRICK_SLAB = createHolder("brick_slab", slabProps.apply(BRICK));
        BRICK_STAIRS = createHolder("brick_stairs", stairProps.apply(BRICK));
        BRICK_WALL = createHolder("brick_wall", wallProps.apply(BRICK));

        BRICK_CHESTBURSTER = createHolder("brick_chestburster", pickProps.get());
        BRICK_FACEHUGGER = createHolder("brick_facehugger", pickProps.get());

        BRICK_SINGLE = createHolder("brick_single", pickProps.get());
        BRICK_SINGLE_SLAB = createHolder("brick_single_slab", slabProps.apply(BRICK_SINGLE));
        BRICK_SINGLE_STAIRS = createHolder("brick_single_stairs", stairProps.apply(BRICK_SINGLE));
        BRICK_SINGLE_WALL = createHolder("brick_single_wall", wallProps.apply(BRICK_SINGLE));

        FLOOR = createHolder("floor", pickProps.get());
        FLOOR_SLAB = createHolder("floor_slab", slabProps.apply(FLOOR));
        FLOOR_STAIRS = createHolder("floor_stairs", stairProps.apply(FLOOR));
        FLOOR_WALL = createHolder("floor_wall", wallProps.apply(FLOOR));

        SKULLS = createHolder("skulls", BlockData.simple(SKULLS_PROPERTIES).tags(stone));

        TILE = createHolder("tile", pickProps.get());
        TILE_SLAB = createHolder("tile_slab", slabProps.apply(TILE));
        TILE_STAIRS = createHolder("tile_stairs", stairProps.apply(TILE));
        TILE_WALL = createHolder("tile_wall", wallProps.apply(TILE));

        WALL_BASE = createHolder("wall_base", pickProps.get());
    }
}
