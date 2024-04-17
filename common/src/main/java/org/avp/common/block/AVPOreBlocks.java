package org.avp.common.block;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.List;
import java.util.function.BiFunction;

import org.avp.api.Holder;
import org.avp.api.block.BlockData;
import org.avp.api.block.drop.BlockDropUtils;
import org.avp.common.item.AVPItems;
import org.avp.common.registry.AVPDeferredBlockRegistry;

public class AVPOreBlocks extends AVPDeferredBlockRegistry {

    public static final AVPOreBlocks INSTANCE = new AVPOreBlocks();

    private final BlockBehaviour.Properties BAUXITE_PROPERTIES = BlockBehaviour.Properties.ofFullCopy(
        Blocks.STONE
    )
        .strength(3.2F, 2.6F);

    private final BlockBehaviour.Properties COBALT_PROPERTIES = BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
        .strength(50F, 20F);

    private final BlockBehaviour.Properties LITHIUM_PROPERTIES = BlockBehaviour.Properties.ofFullCopy(
        Blocks.STONE
    )
        .strength(4.2F, 5.4F);

    private final BlockBehaviour.Properties MONAZITE_PROPERTIES = BlockBehaviour.Properties.ofFullCopy(
        Blocks.STONE
    )
        .strength(45F, 15F);

    private final BlockBehaviour.Properties SILICA_PROPERTIES = BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
        .strength(2.2F, 1.4F);

    private final BlockBehaviour.Properties TITANIUM_PROPERTIES = BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
        .strength(4.0F, 4.0F);

    public final Holder<Block> ORE_BAUXITE;

    public final Holder<Block> ORE_COBALT;

    public final Holder<Block> ORE_LITHIUM;

    public final Holder<Block> ORE_MONAZITE;

    public final Holder<Block> ORE_SILICA;

    public final Holder<Block> ORE_TITANIUM;

    public final Holder<Block> RAW_BAUXITE_BLOCK;

    public final Holder<Block> RAW_TITANIUM_BLOCK;

    public final Holder<Block> COBALT_BLOCK;

    public final Holder<Block> LITHIUM_BLOCK;

    public final Holder<Block> NEODYMIUM_BLOCK;

    public final Holder<Block> SILICA_BLOCK;

    protected Holder<Block> createOreHolder(String registryName, BlockData.Builder blockDataBuilder) {
        return super.createHolder("ore_" + registryName, blockDataBuilder);
    }

    private AVPOreBlocks() {
        var woodTier = List.of(BlockTags.MINEABLE_WITH_PICKAXE);
        var stoneTier = List.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_STONE_TOOL);
        var ironTier = List.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
        var diamondTier = List.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_DIAMOND_TOOL);

        BiFunction<BlockBehaviour.Properties, Holder<Item>, BlockData.Builder> oreProps = (properties, item) -> BlockData.builder(
            properties
        ).drop(BlockDropUtils.ore(item));

        ORE_BAUXITE = createOreHolder("bauxite", oreProps.apply(BAUXITE_PROPERTIES, AVPItems.INSTANCE.RAW_BAUXITE).tags(stoneTier));
        ORE_COBALT = createOreHolder("cobalt", oreProps.apply(COBALT_PROPERTIES, AVPItems.INSTANCE.COBALT).tags(diamondTier));

        ORE_LITHIUM = createOreHolder("lithium", oreProps.apply(LITHIUM_PROPERTIES, AVPItems.INSTANCE.DUST_LITHIUM).tags(woodTier));
        ORE_MONAZITE = createOreHolder("monazite", oreProps.apply(MONAZITE_PROPERTIES, AVPItems.INSTANCE.NEODYMIUM).tags(diamondTier));

        ORE_SILICA = createOreHolder("silica", oreProps.apply(SILICA_PROPERTIES, AVPItems.INSTANCE.SILICA).tags().tags(ironTier));

        ORE_TITANIUM = createOreHolder("titanium", oreProps.apply(TITANIUM_PROPERTIES, AVPItems.INSTANCE.RAW_TITANIUM).tags().tags(ironTier));

        // NOTE: DO NOT USE AVPOreBlocks#createHolder HERE.
        RAW_BAUXITE_BLOCK = createHolder("raw_bauxite_block", BlockData.simple(BAUXITE_PROPERTIES).tags(stoneTier));
        RAW_TITANIUM_BLOCK = createHolder("raw_titanium_block", BlockData.simple(TITANIUM_PROPERTIES).tags(ironTier));
        COBALT_BLOCK = createHolder("cobalt_block", BlockData.simple(COBALT_PROPERTIES).tags(diamondTier));
        LITHIUM_BLOCK = createHolder("lithium_block", BlockData.simple(LITHIUM_PROPERTIES).tags(woodTier));
        NEODYMIUM_BLOCK = createHolder("neodymium_block", BlockData.simple(MONAZITE_PROPERTIES).tags(diamondTier));
        SILICA_BLOCK = createHolder("silica_block", BlockData.simple(SILICA_PROPERTIES).tags(ironTier));
    }
}
