package com.human.common.registry.init.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WaterloggedTransparentBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;

import com.avp.common.gameplay.block.AVPBlockSetTypes;
import com.avp.common.gameplay.block.property.BlockProperties;
import com.avp.common.registry.AVPDeferredHolder;
import com.avp.common.registry.init.block.AVPBlocks;

public class HumanFerroaluminumBlocks {

    public static final AVPDeferredHolder<Block> CHISELED_FERROALUMINUM = AVPBlocks.register(
        "chiseled_ferroaluminum",
        BlockProperties.FERROALUMINUM
    );

    public static final AVPDeferredHolder<Block> CUT_FERROALUMINUM = AVPBlocks.register("cut_ferroaluminum", BlockProperties.FERROALUMINUM);

    public static final AVPDeferredHolder<Block> CUT_FERROALUMINUM_STAIRS = AVPBlocks.register(
        "cut_ferroaluminum_stairs",
        () -> new StairBlock(
            CUT_FERROALUMINUM.get().defaultBlockState(),
            BlockProperties.FERROALUMINUM.build()
        )
    );

    public static final AVPDeferredHolder<Block> CUT_FERROALUMINUM_SLAB = AVPBlocks.register(
        "cut_ferroaluminum_slab",
        () -> new SlabBlock(BlockProperties.FERROALUMINUM.build())
    );

    public static final AVPDeferredHolder<Block> FERROALUMINUM_BLOCK = AVPBlocks.register(
        "ferroaluminum_block",
        BlockProperties.FERROALUMINUM
    );

    public static final AVPDeferredHolder<Block> FERROALUMINUM_STAIRS = AVPBlocks.register(
        "ferroaluminum_stairs",
        () -> new StairBlock(FERROALUMINUM_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(FERROALUMINUM_BLOCK.get()))
    );

    public static final AVPDeferredHolder<Block> FERROALUMINUM_SLAB = AVPBlocks.register(
        "ferroaluminum_slab",
        () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(FERROALUMINUM_BLOCK.get()))
    );

    public static final AVPDeferredHolder<Block> FERROALUMINUM_BUTTON = AVPBlocks.register(
        "ferroaluminum_button",
        () -> new ButtonBlock(AVPBlockSetTypes.FERROALUMINUM, 20, BlockProperties.FERROALUMINUM.build())
    );

    public static final AVPDeferredHolder<Block> FERROALUMINUM_CHAIN_FENCE = AVPBlocks.register(
        "ferroaluminum_chain_fence",
        () -> new IronBarsBlock(BlockProperties.FERROALUMINUM_BARS.build().sound(SoundType.CHAIN))
    );

    public static final AVPDeferredHolder<Block> FERROALUMINUM_COLUMN = AVPBlocks.register(
        "ferroaluminum_column",
        () -> new RotatedPillarBlock(BlockProperties.FERROALUMINUM.build())
    );

    public static final AVPDeferredHolder<Block> FERROALUMINUM_DOOR = AVPBlocks.register(
        "ferroaluminum_door",
        () -> new DoorBlock(BlockSetType.COPPER, BlockProperties.FERROALUMINUM.build().noOcclusion())
    );

    public static final AVPDeferredHolder<Block> FERROALUMINUM_FASTENED_SIDING = AVPBlocks.register(
        "ferroaluminum_fastened_siding",
        BlockProperties.FERROALUMINUM
    );

    public static final AVPDeferredHolder<Block> FERROALUMINUM_FASTENED_SIDING_STAIRS = AVPBlocks.register(
        "ferroaluminum_fastened_siding_stairs",
        () -> new StairBlock(FERROALUMINUM_FASTENED_SIDING.get().defaultBlockState(), BlockProperties.FERROALUMINUM.build())
    );

    public static final AVPDeferredHolder<Block> FERROALUMINUM_FASTENED_SIDING_SLAB = AVPBlocks.register(
        "ferroaluminum_fastened_siding_slab",
        () -> new SlabBlock(BlockProperties.FERROALUMINUM.build())
    );

    public static final AVPDeferredHolder<Block> FERROALUMINUM_FASTENED_STANDING = AVPBlocks.register(
        "ferroaluminum_fastened_standing",
        BlockProperties.FERROALUMINUM
    );

    public static final AVPDeferredHolder<Block> FERROALUMINUM_FASTENED_STANDING_STAIRS = AVPBlocks.register(
        "ferroaluminum_fastened_standing_stairs",
        () -> new StairBlock(FERROALUMINUM_FASTENED_STANDING.get().defaultBlockState(), BlockProperties.FERROALUMINUM.build())
    );

    public static final AVPDeferredHolder<Block> FERROALUMINUM_FASTENED_STANDING_SLAB = AVPBlocks.register(
        "ferroaluminum_fastened_standing_slab",
        () -> new SlabBlock(BlockProperties.FERROALUMINUM.build())
    );

    public static final AVPDeferredHolder<Block> FERROALUMINUM_GRATE = AVPBlocks.register(
        "ferroaluminum_grate",
        () -> new WaterloggedTransparentBlock(BlockProperties.FERROALUMINUM_GRATE.build())
    );

    public static final AVPDeferredHolder<Block> FERROALUMINUM_GRATE_STAIRS = AVPBlocks.register(
        "ferroaluminum_grate_stairs",
        () -> new StairBlock(FERROALUMINUM_GRATE.get().defaultBlockState(), BlockProperties.FERROALUMINUM.build().noOcclusion())
    );

    public static final AVPDeferredHolder<Block> FERROALUMINUM_GRATE_SLAB = AVPBlocks.register(
        "ferroaluminum_grate_slab",
        () -> new SlabBlock(BlockProperties.FERROALUMINUM.build().noOcclusion())
    );

    public static final AVPDeferredHolder<Block> FERROALUMINUM_PLATING = AVPBlocks.register(
        "ferroaluminum_plating",
        BlockProperties.FERROALUMINUM
    );

    public static final AVPDeferredHolder<Block> FERROALUMINUM_PLATING_STAIRS = AVPBlocks.register(
        "ferroaluminum_plating_stairs",
        () -> new StairBlock(FERROALUMINUM_PLATING.get().defaultBlockState(), BlockProperties.FERROALUMINUM.build())
    );

    public static final AVPDeferredHolder<Block> FERROALUMINUM_PLATING_SLAB = AVPBlocks.register(
        "ferroaluminum_plating_slab",
        () -> new SlabBlock(BlockProperties.FERROALUMINUM.build())
    );

    public static final AVPDeferredHolder<Block> FERROALUMINUM_PRESSURE_PLATE = AVPBlocks.register(
        "ferroaluminum_pressure_plate",
        () -> new PressurePlateBlock(BlockSetType.COPPER, BlockProperties.FERROALUMINUM.build())
    );

    public static final AVPDeferredHolder<Block> FERROALUMINUM_SIDING = AVPBlocks.register(
        "ferroaluminum_siding",
        BlockProperties.FERROALUMINUM
    );

    public static final AVPDeferredHolder<Block> FERROALUMINUM_SIDING_STAIRS = AVPBlocks.register(
        "ferroaluminum_siding_stairs",
        () -> new StairBlock(FERROALUMINUM_SIDING.get().defaultBlockState(), BlockProperties.FERROALUMINUM.build())
    );

    public static final AVPDeferredHolder<Block> FERROALUMINUM_SIDING_SLAB = AVPBlocks.register(
        "ferroaluminum_siding_slab",
        () -> new SlabBlock(BlockProperties.FERROALUMINUM.build())
    );

    public static final AVPDeferredHolder<Block> FERROALUMINUM_STANDING = AVPBlocks.register(
        "ferroaluminum_standing",
        BlockProperties.FERROALUMINUM
    );

    public static final AVPDeferredHolder<Block> FERROALUMINUM_STANDING_STAIRS = AVPBlocks.register(
        "ferroaluminum_standing_stairs",
        () -> new StairBlock(FERROALUMINUM_STANDING.get().defaultBlockState(), BlockProperties.FERROALUMINUM.build())
    );

    public static final AVPDeferredHolder<Block> FERROALUMINUM_STANDING_SLAB = AVPBlocks.register(
        "ferroaluminum_standing_slab",
        () -> new SlabBlock(BlockProperties.FERROALUMINUM.build())
    );

    public static final AVPDeferredHolder<Block> FERROALUMINUM_TRAP_DOOR = AVPBlocks.register(
        "ferroaluminum_trapdoor",
        () -> new TrapDoorBlock(BlockSetType.COPPER, BlockProperties.FERROALUMINUM.build().noOcclusion())
    );

    public static final AVPDeferredHolder<Block> FERROALUMINUM_TREAD = AVPBlocks.register(
        "ferroaluminum_tread",
        BlockProperties.FERROALUMINUM
    );

    public static final AVPDeferredHolder<Block> FERROALUMINUM_TREAD_STAIRS = AVPBlocks.register(
        "ferroaluminum_tread_stairs",
        () -> new StairBlock(FERROALUMINUM_TREAD.get().defaultBlockState(), BlockProperties.FERROALUMINUM.build())
    );

    public static final AVPDeferredHolder<Block> FERROALUMINUM_TREAD_SLAB = AVPBlocks.register(
        "ferroaluminum_tread_slab",
        () -> new SlabBlock(BlockProperties.FERROALUMINUM.build())
    );

    public static void initialize() {}
}
