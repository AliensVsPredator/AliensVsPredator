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

    public final Holder<Block> oreBauxite;

    public final Holder<Block> oreCobalt;

    public final Holder<Block> oreLithium;

    public final Holder<Block> oreMonazite;

    public final Holder<Block> oreSilica;

    public final Holder<Block> oreTitanium;

    public final Holder<Block> rawBauxiteBlock;

    public final Holder<Block> rawTitaniumBlock;

    public final Holder<Block> cobaltBlock;

    public final Holder<Block> lithiumBlock;

    public final Holder<Block> neodymiumBlock;

    public final Holder<Block> silicaBlock;

    protected Holder<Block> createOreHolder(String registryName, BlockData.Builder blockDataBuilder) {
        return super.createHolder("ore_" + registryName, blockDataBuilder);
    }

    private AVPOreBlocks() {
        var bauxiteProperties = BlockBehaviour.Properties.ofFullCopy(
                Blocks.STONE
            )
            .strength(3.2F, 2.6F);

        var cobaltProperties = BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
            .strength(50F, 20F);

        var lithiumProperties = BlockBehaviour.Properties.ofFullCopy(
                Blocks.STONE
            )
            .strength(4.2F, 5.4F);

        var monaziteProperties = BlockBehaviour.Properties.ofFullCopy(
                Blocks.STONE
            )
            .strength(45F, 15F);

        var silicaProperties = BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
            .strength(2.2F, 1.4F);

        var titaniumProperties = BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
            .strength(4.0F, 4.0F);

        var woodTier = List.of(BlockTags.MINEABLE_WITH_PICKAXE);
        var stoneTier = List.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_STONE_TOOL);
        var ironTier = List.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
        var diamondTier = List.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_DIAMOND_TOOL);

        BiFunction<BlockBehaviour.Properties, Holder<Item>, BlockData.Builder> oreProps = (properties, item) -> BlockData.builder(
            properties
        ).drop(BlockDropUtils.ore(item));

        oreBauxite = createOreHolder("bauxite", oreProps.apply(bauxiteProperties, AVPItems.INSTANCE.rawBauxite).tags(stoneTier));
        oreCobalt = createOreHolder("cobalt", oreProps.apply(cobaltProperties, AVPItems.INSTANCE.cobalt).tags(diamondTier));

        oreLithium = createOreHolder("lithium", oreProps.apply(lithiumProperties, AVPItems.INSTANCE.dustLithium).tags(woodTier));
        oreMonazite = createOreHolder("monazite", oreProps.apply(monaziteProperties, AVPItems.INSTANCE.neodymium).tags(diamondTier));

        oreSilica = createOreHolder("silica", oreProps.apply(silicaProperties, AVPItems.INSTANCE.silica).tags().tags(ironTier));

        oreTitanium = createOreHolder("titanium", oreProps.apply(titaniumProperties, AVPItems.INSTANCE.rawTitanium).tags().tags(ironTier));

        // NOTE: DO NOT USE AVPOreBlocks#createHolder HERE.
        rawBauxiteBlock = createHolder("raw_bauxite_block", BlockData.simple(bauxiteProperties).tags(stoneTier));
        rawTitaniumBlock = createHolder("raw_titanium_block", BlockData.simple(titaniumProperties).tags(ironTier));
        cobaltBlock = createHolder("cobalt_block", BlockData.simple(cobaltProperties).tags(diamondTier));
        lithiumBlock = createHolder("lithium_block", BlockData.simple(lithiumProperties).tags(woodTier));
        neodymiumBlock = createHolder("neodymium_block", BlockData.simple(monaziteProperties).tags(diamondTier));
        silicaBlock = createHolder("silica_block", BlockData.simple(silicaProperties).tags(ironTier));
    }
}
