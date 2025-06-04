package com.human.common.registry.init.item;

import com.human.common.registry.init.block.HumanIndustrialGlassBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.avp.common.registry.AVPDeferredHolder;
import com.avp.common.registry.init.item.AVPBlockItems;

public class HumanIndustrialGlassBlockItems {

    public static final AVPDeferredHolder<BlockItem> INDUSTRIAL_GLASS = AVPBlockItems.register(
        "industrial_glass",
        HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS
    );

    public static final AVPDeferredHolder<BlockItem> INDUSTRIAL_GLASS_DOOR = AVPBlockItems.register(
        "industrial_glass_door",
        HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS_DOOR
    );

    public static final AVPDeferredHolder<BlockItem> INDUSTRIAL_GLASS_PANE = AVPBlockItems.register(
        "industrial_glass_pane",
        HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS_PANE
    );

    public static final AVPDeferredHolder<BlockItem> INDUSTRIAL_GLASS_SLAB = AVPBlockItems.register(
        "industrial_glass_slab",
        HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS_SLAB
    );

    public static final AVPDeferredHolder<BlockItem> INDUSTRIAL_GLASS_STAIRS = AVPBlockItems.register(
        "industrial_glass_stairs",
        HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS_STAIRS
    );

    public static final AVPDeferredHolder<BlockItem> INDUSTRIAL_GLASS_TRAP_DOOR = AVPBlockItems.register(
        "industrial_glass_trapdoor",
        HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS_TRAP_DOOR
    );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_INDUSTRIAL_GLASS =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> AVPBlockItems.register(
                            dyeColor.getName() + "_industrial_glass",
                            HumanIndustrialGlassBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS.get(dyeColor)
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_INDUSTRIAL_GLASS_PANE =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> AVPBlockItems.register(
                            dyeColor.getName() + "_industrial_glass_pane",
                            HumanIndustrialGlassBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS_PANE.get(dyeColor)
                        )
                    )
                )
        );

    public static void initialize() {}
}
