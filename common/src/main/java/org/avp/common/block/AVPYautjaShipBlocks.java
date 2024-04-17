package org.avp.common.block;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import org.avp.api.GameObject;
import org.avp.api.block.BlockData;
import org.avp.api.block.BlockDataUtils;
import org.avp.common.registry.AVPDeferredBlockRegistry;

public class AVPYautjaShipBlocks extends AVPDeferredBlockRegistry {

    public static final AVPYautjaShipBlocks INSTANCE = new AVPYautjaShipBlocks();

    public final BlockBehaviour.Properties PROPERTIES = BlockBehaviour.Properties.ofFullCopy(
        Blocks.IRON_BLOCK
    )
        .mapColor(MapColor.COLOR_RED)
        .strength(75.0F, 1500.0F);

    public final GameObject<Block> BRICK;

    public final GameObject<Block> BRICK_SLAB;

    public final GameObject<Block> BRICK_STAIRS;

    public final GameObject<Block> BRICK_WALL;

    public final GameObject<Block> DECOR_1;

    public final GameObject<Block> DECOR_2;

    public final GameObject<Block> DECOR_3;

    public final GameObject<Block> DECOR_3_SLAB;

    public final GameObject<Block> DECOR_3_STAIRS;

    public final GameObject<Block> DECOR_3_WALL;

    public final GameObject<Block> PANEL;

    public final GameObject<Block> SUPPORT_PILLAR;

    public final GameObject<Block> WALL_BASE;

    @Override
    protected GameObject<Block> createHolder(String registryName, BlockData.Builder blockDataBuilder) {
        return super.createHolder("yautja_ship_" + registryName, blockDataBuilder);
    }

    private AVPYautjaShipBlocks() {
        var metal = List.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_DIAMOND_TOOL);

        Supplier<BlockData.Builder> pickProps = () -> BlockData.simple(PROPERTIES).tags(metal);

        Function<GameObject<Block>, BlockData.Builder> slabProps =
            parent -> BlockDataUtils.slab(parent, PROPERTIES).tags(metal);
        Function<GameObject<Block>, BlockData.Builder> stairProps =
            parent -> BlockDataUtils.stairs(parent, PROPERTIES).tags(metal);
        Function<GameObject<Block>, BlockData.Builder> wallProps =
            parent -> BlockDataUtils.wall(parent, PROPERTIES).tags(metal);

        BRICK = createHolder("brick", pickProps.get());
        BRICK_SLAB = createHolder("brick_slab", slabProps.apply(BRICK));
        BRICK_STAIRS = createHolder("brick_stairs", stairProps.apply(BRICK));
        BRICK_WALL = createHolder("brick_wall", wallProps.apply(BRICK));

        DECOR_1 = createHolder("decor_1", pickProps.get());
        DECOR_2 = createHolder("decor_2", pickProps.get());

        DECOR_3 = createHolder("decor_3", pickProps.get());
        DECOR_3_SLAB = createHolder("decor_3_slab", slabProps.apply(DECOR_3));
        DECOR_3_STAIRS = createHolder("decor_3_stairs", stairProps.apply(DECOR_3));
        DECOR_3_WALL = createHolder("decor_3_wall", wallProps.apply(DECOR_3));

        PANEL = createHolder("panel", pickProps.get());
        SUPPORT_PILLAR = createHolder("support_pillar", pickProps.get());
        WALL_BASE = createHolder("wall_base", pickProps.get());
    }
}
