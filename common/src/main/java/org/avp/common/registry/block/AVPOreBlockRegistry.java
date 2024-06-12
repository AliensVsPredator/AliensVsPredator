package org.avp.common.registry.block;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.Set;
import java.util.function.Function;

import org.avp.api.common.registry.holder.BLHolder;
import org.avp.api.common.data.block.BlockData;
import org.avp.api.common.data.block.BlockTagData;
import org.avp.api.common.data.loot_table.LootProviders;
import org.avp.api.common.data.block.BlockModelData;
import org.avp.common.registry.item.AVPItemRegistry;
import org.avp.api.common.registry.AVPDeferredBlockRegistry;

public class AVPOreBlockRegistry extends AVPDeferredBlockRegistry {

    public static final AVPOreBlockRegistry INSTANCE = new AVPOreBlockRegistry();

    public final BLHolder<Block> oreBauxite;

    public final BLHolder<Block> oreCobalt;

    public final BLHolder<Block> oreLithium;

    public final BLHolder<Block> oreMonazite;

    public final BLHolder<Block> oreSilica;

    public final BLHolder<Block> oreTitanium;

    public final BLHolder<Block> rawBauxiteBlock;

    public final BLHolder<Block> rawTitaniumBlock;

    public final BLHolder<Block> cobaltBlock;

    public final BLHolder<Block> lithiumBlock;

    public final BLHolder<Block> neodymiumBlock;

    public final BLHolder<Block> silicaBlock;

    protected BLHolder<Block> createOreHolder(BlockData blockData) {
        return createHolder(blockData.withPrefixRegistryName("ore_"));
    }

    private AVPOreBlockRegistry() {
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
            .strength(4, 4);

        var woodTier = BlockTagData.ofBlock(Set.of(BlockTags.MINEABLE_WITH_PICKAXE));
        var stoneTier = BlockTagData.ofBlock(Set.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_STONE_TOOL));
        var ironTier = BlockTagData.ofBlock(Set.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL));
        var diamondTier = BlockTagData.ofBlock(Set.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_DIAMOND_TOOL));

        Function<Block, LootTable.Builder> bauxiteLootProvider =
            block -> LootProviders.ORE.apply(block, AVPItemRegistry.INSTANCE.rawBauxite.get());

        Function<Block, LootTable.Builder> cobaltLootProvider =
            block -> LootProviders.ORE.apply(block, AVPItemRegistry.INSTANCE.cobalt.get());

        Function<Block, LootTable.Builder> lithiumLootProvider =
            block -> LootProviders.ORE_VARIABLE.apply(block, AVPItemRegistry.INSTANCE.dustLithium.get(), 1, 3);

        Function<Block, LootTable.Builder> monaziteLootProvider =
            block -> LootProviders.ORE.apply(block, AVPItemRegistry.INSTANCE.neodymium.get());

        Function<Block, LootTable.Builder> silicaLootProvider =
            block -> LootProviders.ORE_VARIABLE.apply(block, AVPItemRegistry.INSTANCE.silica.get(), 3, 5);

        Function<Block, LootTable.Builder> titaniumLootProvider =
            block -> LootProviders.ORE.apply(block, AVPItemRegistry.INSTANCE.rawTitanium.get());

        oreBauxite = createOreHolder(
            new BlockData("bauxite", BlockModelData.cube(bauxiteProperties), stoneTier, bauxiteLootProvider)
        );
        oreCobalt = createOreHolder(
            new BlockData("cobalt", BlockModelData.cube(cobaltProperties), diamondTier, cobaltLootProvider)
        );

        oreLithium = createOreHolder(
            new BlockData("lithium", BlockModelData.cube(lithiumProperties), woodTier, lithiumLootProvider)
        );
        oreMonazite = createOreHolder(
            new BlockData("monazite", BlockModelData.cube(monaziteProperties), diamondTier, monaziteLootProvider)
        );

        oreSilica = createOreHolder(
            new BlockData("silica", BlockModelData.cube(silicaProperties), ironTier, silicaLootProvider)
        );

        oreTitanium = createOreHolder(
            new BlockData("titanium", BlockModelData.cube(titaniumProperties), ironTier, titaniumLootProvider)
        );

        // NOTE: DO NOT USE createOreHolder HERE.
        rawBauxiteBlock = createHolder("raw_bauxite_block", BlockModelData.cube(bauxiteProperties), stoneTier);
        rawTitaniumBlock = createHolder("raw_titanium_block", BlockModelData.cube(titaniumProperties), ironTier);
        cobaltBlock = createHolder("cobalt_block", BlockModelData.cube(cobaltProperties), diamondTier);
        lithiumBlock = createHolder("lithium_block", BlockModelData.cube(lithiumProperties), woodTier);
        neodymiumBlock = createHolder("neodymium_block", BlockModelData.cube(monaziteProperties), diamondTier);
        silicaBlock = createHolder("silica_block", BlockModelData.cube(silicaProperties), ironTier);
    }
}
