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

public class HumanTitaniumBlocks {

    public static final AVPDeferredHolder<Block> CHISELED_TITANIUM = AVPBlocks.register("chiseled_titanium", BlockProperties.TITANIUM);

    public static final AVPDeferredHolder<Block> CUT_TITANIUM = AVPBlocks.register("cut_titanium", BlockProperties.TITANIUM);

    public static final AVPDeferredHolder<Block> CUT_TITANIUM_STAIRS = AVPBlocks.register(
        "cut_titanium_stairs",
        () -> new StairBlock(
            CUT_TITANIUM.get().defaultBlockState(),
            BlockProperties.TITANIUM.build()
        )
    );

    public static final AVPDeferredHolder<Block> CUT_TITANIUM_SLAB = AVPBlocks.register(
        "cut_titanium_slab",
        () -> new SlabBlock(BlockProperties.TITANIUM.build())
    );

    public static final AVPDeferredHolder<Block> TITANIUM_BLOCK = AVPBlocks.register("titanium_block", BlockProperties.TITANIUM);

    public static final AVPDeferredHolder<Block> TITANIUM_STAIRS = AVPBlocks.register(
        "titanium_stairs",
        () -> new StairBlock(TITANIUM_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(TITANIUM_BLOCK.get()))
    );

    public static final AVPDeferredHolder<Block> TITANIUM_SLAB = AVPBlocks.register(
        "titanium_slab",
        () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(TITANIUM_BLOCK.get()))
    );

    public static final AVPDeferredHolder<Block> TITANIUM_BUTTON = AVPBlocks.register(
        "titanium_button",
        () -> new ButtonBlock(AVPBlockSetTypes.TITANIUM, 20, BlockProperties.TITANIUM.build())
    );

    public static final AVPDeferredHolder<Block> TITANIUM_CHAIN_FENCE = AVPBlocks.register(
        "titanium_chain_fence",
        () -> new IronBarsBlock(BlockProperties.TITANIUM_BARS.build().sound(SoundType.CHAIN))
    );

    public static final AVPDeferredHolder<Block> TITANIUM_COLUMN = AVPBlocks.register(
        "titanium_column",
        () -> new RotatedPillarBlock(BlockProperties.TITANIUM.build())
    );

    public static final AVPDeferredHolder<Block> TITANIUM_DOOR = AVPBlocks.register(
        "titanium_door",
        () -> new DoorBlock(BlockSetType.COPPER, BlockProperties.TITANIUM.build().noOcclusion())
    );

    public static final AVPDeferredHolder<Block> TITANIUM_FASTENED_SIDING = AVPBlocks.register(
        "titanium_fastened_siding",
        BlockProperties.TITANIUM
    );

    public static final AVPDeferredHolder<Block> TITANIUM_FASTENED_SIDING_STAIRS = AVPBlocks.register(
        "titanium_fastened_siding_stairs",
        () -> new StairBlock(TITANIUM_FASTENED_SIDING.get().defaultBlockState(), BlockProperties.TITANIUM.build())
    );

    public static final AVPDeferredHolder<Block> TITANIUM_FASTENED_SIDING_SLAB = AVPBlocks.register(
        "titanium_fastened_siding_slab",
        () -> new SlabBlock(BlockProperties.TITANIUM.build())
    );

    public static final AVPDeferredHolder<Block> TITANIUM_FASTENED_STANDING = AVPBlocks.register(
        "titanium_fastened_standing",
        BlockProperties.TITANIUM
    );

    public static final AVPDeferredHolder<Block> TITANIUM_FASTENED_STANDING_STAIRS = AVPBlocks.register(
        "titanium_fastened_standing_stairs",
        () -> new StairBlock(TITANIUM_FASTENED_STANDING.get().defaultBlockState(), BlockProperties.TITANIUM.build())
    );

    public static final AVPDeferredHolder<Block> TITANIUM_FASTENED_STANDING_SLAB = AVPBlocks.register(
        "titanium_fastened_standing_slab",
        () -> new SlabBlock(BlockProperties.TITANIUM.build())
    );

    public static final AVPDeferredHolder<Block> TITANIUM_GRATE = AVPBlocks.register(
        "titanium_grate",
        () -> new WaterloggedTransparentBlock(BlockProperties.TITANIUM_GRATE.build())
    );

    public static final AVPDeferredHolder<Block> TITANIUM_GRATE_STAIRS = AVPBlocks.register(
        "titanium_grate_stairs",
        () -> new StairBlock(TITANIUM_GRATE.get().defaultBlockState(), BlockProperties.TITANIUM.build().noOcclusion())
    );

    public static final AVPDeferredHolder<Block> TITANIUM_GRATE_SLAB = AVPBlocks.register(
        "titanium_grate_slab",
        () -> new SlabBlock(BlockProperties.TITANIUM.build().noOcclusion())
    );

    public static final AVPDeferredHolder<Block> TITANIUM_PLATING = AVPBlocks.register("titanium_plating", BlockProperties.TITANIUM);

    public static final AVPDeferredHolder<Block> TITANIUM_PLATING_STAIRS = AVPBlocks.register(
        "titanium_plating_stairs",
        () -> new StairBlock(TITANIUM_PLATING.get().defaultBlockState(), BlockProperties.TITANIUM.build())
    );

    public static final AVPDeferredHolder<Block> TITANIUM_PLATING_SLAB = AVPBlocks.register(
        "titanium_plating_slab",
        () -> new SlabBlock(BlockProperties.TITANIUM.build())
    );

    public static final AVPDeferredHolder<Block> TITANIUM_PRESSURE_PLATE = AVPBlocks.register(
        "titanium_pressure_plate",
        () -> new PressurePlateBlock(BlockSetType.COPPER, BlockProperties.TITANIUM.build())
    );

    public static final AVPDeferredHolder<Block> TITANIUM_SIDING = AVPBlocks.register("titanium_siding", BlockProperties.TITANIUM);

    public static final AVPDeferredHolder<Block> TITANIUM_SIDING_STAIRS = AVPBlocks.register(
        "titanium_siding_stairs",
        () -> new StairBlock(TITANIUM_SIDING.get().defaultBlockState(), BlockProperties.TITANIUM.build())
    );

    public static final AVPDeferredHolder<Block> TITANIUM_SIDING_SLAB = AVPBlocks.register(
        "titanium_siding_slab",
        () -> new SlabBlock(BlockProperties.TITANIUM.build())
    );

    public static final AVPDeferredHolder<Block> TITANIUM_STANDING = AVPBlocks.register("titanium_standing", BlockProperties.TITANIUM);

    public static final AVPDeferredHolder<Block> TITANIUM_STANDING_STAIRS = AVPBlocks.register(
        "titanium_standing_stairs",
        () -> new StairBlock(TITANIUM_STANDING.get().defaultBlockState(), BlockProperties.TITANIUM.build())
    );

    public static final AVPDeferredHolder<Block> TITANIUM_STANDING_SLAB = AVPBlocks.register(
        "titanium_standing_slab",
        () -> new SlabBlock(BlockProperties.TITANIUM.build())
    );

    public static final AVPDeferredHolder<Block> TITANIUM_TRAP_DOOR = AVPBlocks.register(
        "titanium_trapdoor",
        () -> new TrapDoorBlock(BlockSetType.COPPER, BlockProperties.TITANIUM.build())
    );

    public static final AVPDeferredHolder<Block> TITANIUM_TREAD = AVPBlocks.register("titanium_tread", BlockProperties.TITANIUM);

    public static final AVPDeferredHolder<Block> TITANIUM_TREAD_STAIRS = AVPBlocks.register(
        "titanium_tread_stairs",
        () -> new StairBlock(TITANIUM_TREAD.get().defaultBlockState(), BlockProperties.TITANIUM.build())
    );

    public static final AVPDeferredHolder<Block> TITANIUM_TREAD_SLAB = AVPBlocks.register(
        "titanium_tread_slab",
        () -> new SlabBlock(BlockProperties.TITANIUM.build())
    );

    public static void initialize() {}
}
