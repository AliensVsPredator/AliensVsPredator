package org.avp.common.block;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import org.avp.api.block.BlockData;
import org.avp.api.block.BlockDataUtils;
import org.avp.api.GameObject;

/**
 * @author Boston Vanseghi
 */
public class AVPYautjaShipBlocks {

    public static final BlockBehaviour.Properties PROPERTIES = BlockBehaviour.Properties.ofFullCopy(
        Blocks.IRON_BLOCK
    )
        .mapColor(MapColor.COLOR_RED)
        .strength(75.0F, 1500.0F);

    public static final GameObject<Block> BRICK;

    public static final GameObject<Block> BRICK_SLAB;

    public static final GameObject<Block> BRICK_STAIRS;

    public static final GameObject<Block> BRICK_WALL;

    public static final GameObject<Block> DECOR_1;

    public static final GameObject<Block> DECOR_2;

    public static final GameObject<Block> DECOR_3;

    public static final GameObject<Block> DECOR_3_SLAB;

    public static final GameObject<Block> DECOR_3_STAIRS;

    public static final GameObject<Block> DECOR_3_WALL;

    public static final GameObject<Block> PANEL;

    public static final GameObject<Block> SUPPORT_PILLAR;

    public static final GameObject<Block> WALL_BASE;

    public static void forceInitialization() {
        // This method doesn't need to do anything
    }

    private static GameObject<Block> register(String name, BlockData.Builder builder) {
        return AVPBlocks.register("yautja_ship_" + name, builder);
    }

    private AVPYautjaShipBlocks() {}

    static {
        var metal = List.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_DIAMOND_TOOL);

        Supplier<BlockData.Builder> pickProps = () -> BlockData.simple(PROPERTIES).tags(metal);

        Function<GameObject<Block>, BlockData.Builder> slabProps =
            parent -> BlockDataUtils.slab(parent, PROPERTIES).tags(metal);
        Function<GameObject<Block>, BlockData.Builder> stairProps =
            parent -> BlockDataUtils.stairs(parent, PROPERTIES).tags(metal);
        Function<GameObject<Block>, BlockData.Builder> wallProps =
            parent -> BlockDataUtils.wall(parent, PROPERTIES).tags(metal);

        BRICK = register("brick", pickProps.get());
        BRICK_SLAB = register("brick_slab", slabProps.apply(BRICK));
        BRICK_STAIRS = register("brick_stairs", stairProps.apply(BRICK));
        BRICK_WALL = register("brick_wall", wallProps.apply(BRICK));

        DECOR_1 = register("decor_1", pickProps.get());
        DECOR_2 = register("decor_2", pickProps.get());

        DECOR_3 = register("decor_3", pickProps.get());
        DECOR_3_SLAB = register("decor_3_slab", slabProps.apply(DECOR_3));
        DECOR_3_STAIRS = register("decor_3_stairs", stairProps.apply(DECOR_3));
        DECOR_3_WALL = register("decor_3_wall", wallProps.apply(DECOR_3));

        PANEL = register("panel", pickProps.get());
        SUPPORT_PILLAR = register("support_pillar", pickProps.get());
        WALL_BASE = register("wall_base", pickProps.get());
    }
}
