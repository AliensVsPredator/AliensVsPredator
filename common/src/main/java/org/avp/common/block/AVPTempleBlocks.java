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

    public final Holder<Block> brick;

    public final Holder<Block> brickSlab;

    public final Holder<Block> brickStairs;

    public final Holder<Block> brickWall;

    public final Holder<Block> brickChestburster;

    public final Holder<Block> brickFacehugger;

    public final Holder<Block> brickSingle;

    public final Holder<Block> brickSingleSlab;

    public final Holder<Block> brickSingleStairs;

    public final Holder<Block> brickSingleWall;

    public final Holder<Block> floor;

    public final Holder<Block> floorSlab;

    public final Holder<Block> floorStairs;

    public final Holder<Block> floorWall;

    public final Holder<Block> skulls;

    public final Holder<Block> tile;

    public final Holder<Block> tileSlab;

    public final Holder<Block> tileStairs;

    public final Holder<Block> tileWall;

    public final Holder<Block> wallBase;

    @Override
    protected Holder<Block> createHolder(String registryName, BlockData.Builder blockDataBuilder) {
        return super.createHolder("temple_" + registryName, blockDataBuilder);
    }

    private AVPTempleBlocks() {
        var brickProperties = BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).strength(3, 6);
        var skullProperties = BlockBehaviour.Properties.ofFullCopy(Blocks.BONE_BLOCK).strength(3, 6);

        var stone = List.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);

        Supplier<BlockData.Builder> pickProps = () -> BlockData.simple(brickProperties).blockTags(stone);

        Function<Holder<Block>, BlockData.Builder> slabProps =
            parent -> BlockDataUtils.slab(parent, brickProperties).blockTags(stone);
        Function<Holder<Block>, BlockData.Builder> stairProps =
            parent -> BlockDataUtils.stairs(parent, brickProperties).blockTags(stone);
        Function<Holder<Block>, BlockData.Builder> wallProps =
            parent -> BlockDataUtils.wall(parent, brickProperties).blockTags(stone);

        brick = createHolder("brick", pickProps.get());
        brickSlab = createHolder("brick_slab", slabProps.apply(brick));
        brickStairs = createHolder("brick_stairs", stairProps.apply(brick));
        brickWall = createHolder("brick_wall", wallProps.apply(brick));

        brickChestburster = createHolder("brick_chestburster", pickProps.get());
        brickFacehugger = createHolder("brick_facehugger", pickProps.get());

        brickSingle = createHolder("brick_single", pickProps.get());
        brickSingleSlab = createHolder("brick_single_slab", slabProps.apply(brickSingle));
        brickSingleStairs = createHolder("brick_single_stairs", stairProps.apply(brickSingle));
        brickSingleWall = createHolder("brick_single_wall", wallProps.apply(brickSingle));

        floor = createHolder("floor", pickProps.get());
        floorSlab = createHolder("floor_slab", slabProps.apply(floor));
        floorStairs = createHolder("floor_stairs", stairProps.apply(floor));
        floorWall = createHolder("floor_wall", wallProps.apply(floor));

        skulls = createHolder("skulls", BlockData.simple(skullProperties).blockTags(stone));

        tile = createHolder("tile", pickProps.get());
        tileSlab = createHolder("tile_slab", slabProps.apply(tile));
        tileStairs = createHolder("tile_stairs", stairProps.apply(tile));
        tileWall = createHolder("tile_wall", wallProps.apply(tile));

        wallBase = createHolder("wall_base", pickProps.get());
    }
}
