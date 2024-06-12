package org.avp.common.registry.block;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.Set;

import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import org.avp.api.common.game.block.CustomTransparentBlock;
import org.avp.api.common.registry.holder.BLHolder;
import org.avp.api.common.data.block.BlockData;
import org.avp.api.common.registry.holder.BlockHolderSet;
import org.avp.api.common.data.block.BlockTagData;
import org.avp.api.common.data.block.BlockModelData;
import org.avp.api.common.data.block.BlockModelRenderType;
import org.avp.api.common.data.block.BlockModelDataType;
import org.avp.api.common.registry.AVPDeferredBlockRegistry;

public class AVPBlockRegistry extends AVPDeferredBlockRegistry {

    public static final AVPBlockRegistry INSTANCE = new AVPBlockRegistry();

    public final FullBlockHolderMetalSet aluminum;

    public final BlockHolderMetalSet copper;

    public final FullBlockHolderMetalSet ferroaluminum;

    public final BlockHolderMetalSet gold;

    public final BlockHolderMetalSet iron;

    public final BlockHolderMetalSet netherite;

    public final BLHolder<Block> orioniteBlock;

    public final FullBlockHolderMetalSet steel;

    public final FullBlockHolderMetalSet titanium;

    private BlockHolderSet createBlockHolderSet(BLHolder<Block> holder, BlockBehaviour.Properties properties, BlockData blockData) {
        return new BlockHolderSet(
            holder,
            createHolder(BlockData.toSlab(holder, properties, blockData)),
            createHolder(BlockData.toStairs(holder, properties, blockData)),
            createHolder(BlockData.toWall(holder, properties, blockData))
        );
    }

    private BlockHolderMetalSet createBlockHolderMetalSet(
        String baseName,
        BlockBehaviour.Properties properties,
        BlockTagData blockTagData
    ) {
        var cubeModelData = BlockModelData.cube(properties);

        var grateProperties = BlockBehaviour.Properties.of().noOcclusion();
        var grateModelData = new BlockModelData(
            () -> new CustomTransparentBlock(grateProperties),
            BlockModelDataType.Cube::new,
            BlockModelRenderType.TRANSLUCENT
        );

        var platedBlockData = new BlockData(baseName + "_plated", cubeModelData, blockTagData);
        var platedParentHolder = createHolder(platedBlockData);

        var platedStackBlockData = new BlockData(baseName + "_plated_stack", cubeModelData, blockTagData);
        var platedStackParentHolder = createHolder(platedStackBlockData);

        var platedChevronBlockData = new BlockData(baseName + "_plated_chevron", cubeModelData, blockTagData);
        var platedChevronParentHolder = createHolder(platedChevronBlockData);

        return new BlockHolderMetalSet(
            createHolder(baseName + "_grate", grateModelData, blockTagData),
            createBlockHolderSet(platedParentHolder, properties, platedBlockData),
            createBlockHolderSet(platedStackParentHolder, properties, platedStackBlockData),
            createBlockHolderSet(platedChevronParentHolder, properties, platedChevronBlockData),
            createHolder(baseName + "_vent", cubeModelData, blockTagData)
        );
    }

    private FullBlockHolderMetalSet createFullBlockHolderMetalSet(
        String baseName,
        BlockBehaviour.Properties properties,
        BlockTagData blockTagData
    ) {
        var cubeModelData = BlockModelData.cube(properties);

        var baseBlockData = new BlockData(baseName + "_block", cubeModelData, blockTagData);

        var cutBlockData = new BlockData(baseName + "_cut", cubeModelData, blockTagData);
        var cutParentHolder = createHolder(cutBlockData);

        return new FullBlockHolderMetalSet(
            createHolder(baseBlockData),
            createBlockHolderSet(cutParentHolder, properties, cutBlockData),
            createBlockHolderMetalSet(baseName, properties, blockTagData)
        );
    }

    private AVPBlockRegistry() {
        var aluminumProperties = BlockBehaviour.Properties.of()
            .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
            .mapColor(MapColor.COLOR_LIGHT_GRAY)
            .requiresCorrectToolForDrops()
            .sound(SoundType.COPPER)
            .strength(4, 6);
        var ferroaluminumProperties = BlockBehaviour.Properties.of()
            .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
            .mapColor(MapColor.COLOR_GRAY)
            .requiresCorrectToolForDrops()
            .sound(SoundType.COPPER)
            .strength(7, 9);
        var orioniteProperties = BlockBehaviour.Properties.of()
            .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
            .mapColor(MapColor.SAND)
            .requiresCorrectToolForDrops()
            .sound(SoundType.COPPER)
            .strength(20, 12);
        var steelProperties = BlockBehaviour.Properties.of()
            .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
            .mapColor(MapColor.COLOR_LIGHT_BLUE)
            .requiresCorrectToolForDrops()
            .sound(SoundType.COPPER)
            .strength(10, 12);
        var titaniumProperties = BlockBehaviour.Properties.of()
            .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
            .mapColor(MapColor.SAND)
            .requiresCorrectToolForDrops()
            .sound(SoundType.COPPER)
            .strength(15, 12);

        var copperProperties = BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK);
        var goldProperties = BlockBehaviour.Properties.ofFullCopy(Blocks.GOLD_BLOCK);
        var ironProperties = BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK);
        var netheriteProperties = BlockBehaviour.Properties.ofFullCopy(Blocks.NETHERITE_BLOCK);

        var stoneTier = BlockTagData.ofBlock(Set.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_STONE_TOOL));
        var ironTier = BlockTagData.ofBlock(Set.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL));
        var diamondTier = BlockTagData.ofBlock(Set.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_DIAMOND_TOOL));

        aluminum = createFullBlockHolderMetalSet("aluminum", aluminumProperties, stoneTier);
        copper = createBlockHolderMetalSet("copper", copperProperties, stoneTier);
        ferroaluminum = createFullBlockHolderMetalSet("ferroaluminum", ferroaluminumProperties, stoneTier);
        gold = createBlockHolderMetalSet("gold", goldProperties, ironTier);
        iron = createBlockHolderMetalSet("iron", ironProperties, ironTier);
        netherite = createBlockHolderMetalSet("netherite", netheriteProperties, diamondTier);
        orioniteBlock = createHolder("orionite_block", BlockModelData.cube(orioniteProperties), diamondTier);
        steel = createFullBlockHolderMetalSet("steel", steelProperties, ironTier);
        titanium = createFullBlockHolderMetalSet("titanium", titaniumProperties, ironTier);
    }

    public record FullBlockHolderMetalSet(
        BLHolder<Block> base,
        BlockHolderSet cutSet,
        BlockHolderMetalSet extendedSet
    ) {}

    public record BlockHolderMetalSet(
        BLHolder<Block> grate,
        BlockHolderSet platedSet,
        BlockHolderSet platedStackSet,
        BlockHolderSet platedChevronSet,
        BLHolder<Block> vent
    ) {}
}
