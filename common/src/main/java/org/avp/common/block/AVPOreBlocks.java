package org.avp.common.block;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.List;
import java.util.function.BiFunction;

import org.avp.api.block.BlockData;
import org.avp.api.block.drop.BlockDropUtils;
import org.avp.common.item.AVPItems;
import org.avp.api.GameObject;

/**
 * @author Boston Vanseghi
 */
public class AVPOreBlocks {

    public static final BlockBehaviour.Properties BAUXITE_PROPERTIES = BlockBehaviour.Properties.ofFullCopy(
        Blocks.STONE
    )
        .strength(3.2F, 2.6F);

    public static final BlockBehaviour.Properties COBALT_PROPERTIES = BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
        .strength(50F, 20F);

    public static final BlockBehaviour.Properties LITHIUM_PROPERTIES = BlockBehaviour.Properties.ofFullCopy(
        Blocks.STONE
    )
        .strength(4.2F, 5.4F);

    public static final BlockBehaviour.Properties MONAZITE_PROPERTIES = BlockBehaviour.Properties.ofFullCopy(
        Blocks.STONE
    )
        .strength(45F, 15F);

    public static final BlockBehaviour.Properties SILICA_PROPERTIES = BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
        .strength(2.2F, 1.4F);

    public static final GameObject<Block> ORE_BAUXITE;

    public static final GameObject<Block> ORE_COBALT;

    public static final GameObject<Block> ORE_LITHIUM;

    public static final GameObject<Block> ORE_MONAZITE;

    public static final GameObject<Block> ORE_SILICA;

    public static final GameObject<Block> RAW_BAUXITE_BLOCK;

    public static void forceInitialization() {
        // This method doesn't need to do anything
    }

    private static GameObject<Block> register(String name, BlockData.Builder builder) {
        return AVPBlocks.register("ore_" + name, builder);
    }

    private AVPOreBlocks() {}

    static {
        var stoneTier = List.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_STONE_TOOL);
        var diamondTier = List.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_DIAMOND_TOOL);

        BiFunction<BlockBehaviour.Properties, GameObject<Item>, BlockData.Builder> oreProps = (properties, item) -> BlockData.builder(
            properties
        ).drop(BlockDropUtils.ore(item));

        ORE_BAUXITE = register("bauxite", oreProps.apply(BAUXITE_PROPERTIES, AVPItems.RAW_BAUXITE).tags(stoneTier));
        ORE_COBALT = register("cobalt", oreProps.apply(COBALT_PROPERTIES, AVPItems.COBALT).tags(diamondTier));

        // TODO: Needs wood tool
        ORE_LITHIUM = register("lithium", oreProps.apply(LITHIUM_PROPERTIES, AVPItems.INGOT_LITHIUM));
        ORE_MONAZITE = register("monazite", oreProps.apply(MONAZITE_PROPERTIES, AVPItems.NEODYMIUM).tags(diamondTier));

        // TODO: Needs wood tool
        ORE_SILICA = register("silica", oreProps.apply(SILICA_PROPERTIES, AVPItems.SILICA));

        // NOTE: DO NOT USE AVPOreBlocks#register HERE.
        RAW_BAUXITE_BLOCK = AVPBlocks.register("raw_bauxite_block", BlockData.simple(BAUXITE_PROPERTIES).tags(stoneTier));
    }
}
