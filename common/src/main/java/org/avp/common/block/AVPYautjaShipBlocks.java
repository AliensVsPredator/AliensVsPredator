package org.avp.common.block;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import org.avp.api.Holder;
import org.avp.api.block.BlockData;
import org.avp.api.block.BlockDataUtils;
import org.avp.common.registry.AVPDeferredBlockRegistry;

public class AVPYautjaShipBlocks extends AVPDeferredBlockRegistry {

    public static final AVPYautjaShipBlocks INSTANCE = new AVPYautjaShipBlocks();

    public final Holder<Block> brick;

    public final Holder<Block> brickSlab;

    public final Holder<Block> brickStairs;

    public final Holder<Block> brickWall;

    public final Holder<Block> decor1;

    public final Holder<Block> decor2;

    public final Holder<Block> decor3;

    public final Holder<Block> decor3Slab;

    public final Holder<Block> decor3Stairs;

    public final Holder<Block> decor3Wall;

    public final Holder<Block> panel;

    public final Holder<Block> supportPillar;

    public final Holder<Block> wallBase;

    @Override
    protected Holder<Block> createHolder(String registryName, BlockData.Builder blockDataBuilder) {
        return super.createHolder("yautja_ship_" + registryName, blockDataBuilder);
    }

    private AVPYautjaShipBlocks() {
        var metalProperties = BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
            .mapColor(MapColor.COLOR_RED)
            .strength(75, 1500);

        var metal = List.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_DIAMOND_TOOL);

        Supplier<BlockData.Builder> pickProps = () -> BlockData.simple(metalProperties).blockTags(metal);

        Function<Holder<Block>, BlockData.Builder> slabProps =
            parent -> BlockDataUtils.slab(parent, metalProperties).blockTags(metal);
        Function<Holder<Block>, BlockData.Builder> stairProps =
            parent -> BlockDataUtils.stairs(parent, metalProperties).blockTags(metal);
        Function<Holder<Block>, BlockData.Builder> wallProps =
            parent -> BlockDataUtils.wall(parent, metalProperties).blockTags(metal);

        brick = createHolder("brick", pickProps.get());
        brickSlab = createHolder("brick_slab", slabProps.apply(brick));
        brickStairs = createHolder("brick_stairs", stairProps.apply(brick));
        brickWall = createHolder("brick_wall", wallProps.apply(brick));

        decor1 = createHolder("decor_1", pickProps.get());
        decor2 = createHolder("decor_2", pickProps.get());

        decor3 = createHolder("decor_3", pickProps.get());
        decor3Slab = createHolder("decor_3_slab", slabProps.apply(decor3));
        decor3Stairs = createHolder("decor_3_stairs", stairProps.apply(decor3));
        decor3Wall = createHolder("decor_3_wall", wallProps.apply(decor3));

        panel = createHolder("panel", pickProps.get());
        supportPillar = createHolder("support_pillar", pickProps.get());
        wallBase = createHolder("wall_base", pickProps.get());
    }
}
