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

public class HumanSteelBlocks {

    public static final AVPDeferredHolder<Block> CHISELED_STEEL = AVPBlocks.register("chiseled_steel", BlockProperties.STEEL);

    public static final AVPDeferredHolder<Block> CUT_STEEL = AVPBlocks.register("cut_steel", BlockProperties.STEEL);

    public static final AVPDeferredHolder<Block> CUT_STEEL_STAIRS = AVPBlocks.register(
        "cut_steel_stairs",
        () -> new StairBlock(
            CUT_STEEL.get().defaultBlockState(),
            BlockProperties.STEEL.build()
        )
    );

    public static final AVPDeferredHolder<Block> CUT_STEEL_SLAB = AVPBlocks.register(
        "cut_steel_slab",
        () -> new SlabBlock(BlockProperties.STEEL.build())
    );

    public static final AVPDeferredHolder<Block> STEEL_BLOCK = AVPBlocks.register("steel_block", BlockProperties.STEEL);

    public static final AVPDeferredHolder<Block> STEEL_STAIRS = AVPBlocks.register(
        "steel_stairs",
        () -> new StairBlock(STEEL_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(STEEL_BLOCK.get()))
    );

    public static final AVPDeferredHolder<Block> STEEL_SLAB = AVPBlocks.register(
        "steel_slab",
        () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(STEEL_BLOCK.get()))
    );

    public static final AVPDeferredHolder<Block> STEEL_BUTTON = AVPBlocks.register(
        "steel_button",
        () -> new ButtonBlock(AVPBlockSetTypes.STEEL, 20, BlockProperties.STEEL.build())
    );

    public static final AVPDeferredHolder<Block> STEEL_BARS = AVPBlocks.register(
        "steel_bars",
        () -> new IronBarsBlock(BlockProperties.STEEL_BARS.build())
    );

    public static final AVPDeferredHolder<Block> STEEL_CHAIN_FENCE = AVPBlocks.register(
        "steel_chain_fence",
        () -> new IronBarsBlock(BlockProperties.STEEL_BARS.build().sound(SoundType.CHAIN))
    );

    public static final AVPDeferredHolder<Block> STEEL_COLUMN = AVPBlocks.register(
        "steel_column",
        () -> new RotatedPillarBlock(BlockProperties.STEEL.build())
    );

    public static final AVPDeferredHolder<Block> STEEL_DOOR = AVPBlocks.register(
        "steel_door",
        () -> new DoorBlock(BlockSetType.COPPER, BlockProperties.STEEL.build().noOcclusion())
    );

    public static final AVPDeferredHolder<Block> STEEL_FASTENED_SIDING = AVPBlocks.register("steel_fastened_siding", BlockProperties.STEEL);

    public static final AVPDeferredHolder<Block> STEEL_FASTENED_SIDING_STAIRS = AVPBlocks.register(
        "steel_fastened_siding_stairs",
        () -> new StairBlock(STEEL_FASTENED_SIDING.get().defaultBlockState(), BlockProperties.STEEL.build())
    );

    public static final AVPDeferredHolder<Block> STEEL_FASTENED_SIDING_SLAB = AVPBlocks.register(
        "steel_fastened_siding_slab",
        () -> new SlabBlock(BlockProperties.STEEL.build())
    );

    public static final AVPDeferredHolder<Block> STEEL_FASTENED_STANDING = AVPBlocks.register(
        "steel_fastened_standing",
        BlockProperties.STEEL
    );

    public static final AVPDeferredHolder<Block> STEEL_FASTENED_STANDING_STAIRS = AVPBlocks.register(
        "steel_fastened_standing_stairs",
        () -> new StairBlock(STEEL_FASTENED_STANDING.get().defaultBlockState(), BlockProperties.STEEL.build())
    );

    public static final AVPDeferredHolder<Block> STEEL_FASTENED_STANDING_SLAB = AVPBlocks.register(
        "steel_fastened_standing_slab",
        () -> new SlabBlock(BlockProperties.STEEL.build())
    );

    public static final AVPDeferredHolder<Block> STEEL_GRATE = AVPBlocks.register(
        "steel_grate",
        () -> new WaterloggedTransparentBlock(BlockProperties.STEEL_GRATE.build())
    );

    public static final AVPDeferredHolder<Block> STEEL_GRATE_STAIRS = AVPBlocks.register(
        "steel_grate_stairs",
        () -> new StairBlock(STEEL_GRATE.get().defaultBlockState(), BlockProperties.STEEL.build().noOcclusion())
    );

    public static final AVPDeferredHolder<Block> STEEL_GRATE_SLAB = AVPBlocks.register(
        "steel_grate_slab",
        () -> new SlabBlock(BlockProperties.STEEL.build().noOcclusion())
    );

    public static final AVPDeferredHolder<Block> STEEL_PLATING = AVPBlocks.register("steel_plating", BlockProperties.STEEL);

    public static final AVPDeferredHolder<Block> STEEL_PLATING_STAIRS = AVPBlocks.register(
        "steel_plating_stairs",
        () -> new StairBlock(STEEL_PLATING.get().defaultBlockState(), BlockProperties.STEEL.build())
    );

    public static final AVPDeferredHolder<Block> STEEL_PLATING_SLAB = AVPBlocks.register(
        "steel_plating_slab",
        () -> new SlabBlock(BlockProperties.STEEL.build())
    );

    public static final AVPDeferredHolder<Block> STEEL_PRESSURE_PLATE = AVPBlocks.register(
        "steel_pressure_plate",
        () -> new PressurePlateBlock(BlockSetType.COPPER, BlockProperties.STEEL.build())
    );

    public static final AVPDeferredHolder<Block> STEEL_SIDING = AVPBlocks.register("steel_siding", BlockProperties.STEEL);

    public static final AVPDeferredHolder<Block> STEEL_SIDING_STAIRS = AVPBlocks.register(
        "steel_siding_stairs",
        () -> new StairBlock(STEEL_SIDING.get().defaultBlockState(), BlockProperties.STEEL.build())
    );

    public static final AVPDeferredHolder<Block> STEEL_SIDING_SLAB = AVPBlocks.register(
        "steel_siding_slab",
        () -> new SlabBlock(BlockProperties.STEEL.build())
    );

    public static final AVPDeferredHolder<Block> STEEL_STANDING = AVPBlocks.register("steel_standing", BlockProperties.STEEL);

    public static final AVPDeferredHolder<Block> STEEL_STANDING_STAIRS = AVPBlocks.register(
        "steel_standing_stairs",
        () -> new StairBlock(STEEL_STANDING.get().defaultBlockState(), BlockProperties.STEEL.build())
    );

    public static final AVPDeferredHolder<Block> STEEL_STANDING_SLAB = AVPBlocks.register(
        "steel_standing_slab",
        () -> new SlabBlock(BlockProperties.STEEL.build())
    );

    public static final AVPDeferredHolder<Block> STEEL_TRAP_DOOR = AVPBlocks.register(
        "steel_trapdoor",
        () -> new TrapDoorBlock(BlockSetType.COPPER, BlockProperties.STEEL.build())
    );

    public static final AVPDeferredHolder<Block> STEEL_TREAD = AVPBlocks.register("steel_tread", BlockProperties.STEEL);

    public static final AVPDeferredHolder<Block> STEEL_TREAD_STAIRS = AVPBlocks.register(
        "steel_tread_stairs",
        () -> new StairBlock(STEEL_TREAD.get().defaultBlockState(), BlockProperties.STEEL.build())
    );

    public static final AVPDeferredHolder<Block> STEEL_TREAD_SLAB = AVPBlocks.register(
        "steel_tread_slab",
        () -> new SlabBlock(BlockProperties.STEEL.build())
    );

    public static void initialize() {}
}
