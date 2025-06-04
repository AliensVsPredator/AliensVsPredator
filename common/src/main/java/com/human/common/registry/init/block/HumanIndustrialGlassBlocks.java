package com.human.common.registry.init.block;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StainedGlassBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.properties.BlockSetType;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.avp.common.gameplay.block.property.BlockProperties;
import com.avp.common.registry.AVPDeferredHolder;
import com.avp.common.registry.init.block.AVPBlocks;

public class HumanIndustrialGlassBlocks {

    public static final AVPDeferredHolder<Block> INDUSTRIAL_GLASS = AVPBlocks.register(
        "industrial_glass",
        () -> new TransparentBlock(BlockProperties.INDUSTRIAL_GLASS.build())
    );

    public static final AVPDeferredHolder<Block> INDUSTRIAL_GLASS_STAIRS = AVPBlocks.register(
        "industrial_glass_stairs",
        () -> new StairBlock(INDUSTRIAL_GLASS.get().defaultBlockState(), BlockProperties.INDUSTRIAL_GLASS.build())
    );

    public static final AVPDeferredHolder<Block> INDUSTRIAL_GLASS_DOOR = AVPBlocks.register(
        "industrial_glass_door",
        () -> new DoorBlock(BlockSetType.COPPER, BlockProperties.INDUSTRIAL_GLASS.build().noOcclusion())
    );

    public static final AVPDeferredHolder<Block> INDUSTRIAL_GLASS_TRAP_DOOR = AVPBlocks.register(
        "industrial_glass_trapdoor",
        () -> new TrapDoorBlock(BlockSetType.COPPER, BlockProperties.INDUSTRIAL_GLASS.build().noOcclusion())
    );

    public static final AVPDeferredHolder<Block> INDUSTRIAL_GLASS_PANE = AVPBlocks.register(
        "industrial_glass_pane",
        () -> new IronBarsBlock(BlockProperties.INDUSTRIAL_GLASS_PANE.build())
    );

    // Slabs And Stairs
    public static final AVPDeferredHolder<Block> INDUSTRIAL_GLASS_SLAB = AVPBlocks.register(
        "industrial_glass_slab",
        () -> new SlabBlock(BlockProperties.INDUSTRIAL_GLASS.build())
    );

    public static final Map<DyeColor, AVPDeferredHolder<Block>> DYE_COLOR_TO_INDUSTRIAL_GLASS =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> AVPBlocks.register(
                            dyeColor.getName() + "_industrial_glass",
                            () -> new StainedGlassBlock(
                                dyeColor,
                                BlockProperties.DYE_COLOR_TO_INDUSTRIAL_GLASS_PROPERTIES.get(dyeColor).build()
                            )
                        )
                    )
                )
        );

    public static final Map<DyeColor, AVPDeferredHolder<Block>> DYE_COLOR_TO_INDUSTRIAL_GLASS_PANE =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> AVPBlocks.register(
                            dyeColor.getName() + "_industrial_glass_pane",
                            () -> new IronBarsBlock(BlockProperties.DYE_COLOR_TO_INDUSTRIAL_GLASS_PANE_PROPERTIES.get(dyeColor).build())
                        )
                    )
                )
        );

    public static void initialize() {}
}
