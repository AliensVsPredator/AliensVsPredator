package com.human.common.registry.init.item;

import com.human.common.registry.init.block.HumanIndustrialConcreteBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.avp.common.registry.init.item.AVPBlockItems;

public class HumanIndustrialConcreteBlockItems {

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_INDUSTRIAL_CONCRETE =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> AVPBlockItems.register(
                            dyeColor.getName() + "_industrial_concrete",
                            HumanIndustrialConcreteBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE.get(dyeColor)
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_INDUSTRIAL_CONCRETE_SLAB =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> AVPBlockItems.register(
                            dyeColor.getName() + "_industrial_concrete_slab",
                            HumanIndustrialConcreteBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_SLAB.get(dyeColor)
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_INDUSTRIAL_CONCRETE_STAIRS =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> AVPBlockItems.register(
                            dyeColor.getName() + "_industrial_concrete_stairs",
                            HumanIndustrialConcreteBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_STAIRS.get(dyeColor)
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_INDUSTRIAL_CONCRETE_WALL =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> AVPBlockItems.register(
                            dyeColor.getName() + "_industrial_concrete_wall",
                            HumanIndustrialConcreteBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_WALL.get(dyeColor)
                        )
                    )
                )
        );

    public static void initialize() {}
}
