package com.human.common.registry.init.item;

import com.human.common.registry.init.block.HumanPlasticBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.avp.common.registry.init.item.AVPBlockItems;

public class HumanPlasticBlockItems {

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_CUT_PLASTIC =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> AVPBlockItems.register(
                            dyeColor.getName() + "_cut_plastic",
                            HumanPlasticBlocks.DYE_COLOR_TO_CUT_PLASTIC.get(dyeColor)
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_CUT_PLASTIC_SLAB =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> AVPBlockItems.register(
                            dyeColor.getName() + "_cut_plastic_slab",
                            HumanPlasticBlocks.DYE_COLOR_TO_CUT_PLASTIC_SLAB.get(dyeColor)
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_CUT_PLASTIC_STAIRS =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> AVPBlockItems.register(
                            dyeColor.getName() + "_cut_plastic_stairs",
                            HumanPlasticBlocks.DYE_COLOR_TO_CUT_PLASTIC_STAIRS.get(dyeColor)
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_FRAMED_PLASTIC =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> AVPBlockItems.register(
                            dyeColor.getName() + "_framed_plastic",
                            HumanPlasticBlocks.DYE_COLOR_TO_FRAMED_PLASTIC.get(dyeColor)
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_PITTED_PLASTIC =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> AVPBlockItems.register(
                            dyeColor.getName() + "_pitted_plastic",
                            HumanPlasticBlocks.DYE_COLOR_TO_PITTED_PLASTIC.get(dyeColor)
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_PITTED_PLASTIC_SLAB =
            Collections.unmodifiableMap(
                    Arrays.stream(DyeColor.values())
                            .collect(
                                    Collectors.toMap(
                                            Function.identity(),
                                            dyeColor -> AVPBlockItems.register(
                                                    dyeColor.getName() + "_pitted_plastic_slab",
                                                    HumanPlasticBlocks.DYE_COLOR_TO_PITTED_PLASTIC_SLAB.get(dyeColor)
                                            )
                                    )
                            )
            );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_PITTED_PLASTIC_STAIRS =
            Collections.unmodifiableMap(
                    Arrays.stream(DyeColor.values())
                            .collect(
                                    Collectors.toMap(
                                            Function.identity(),
                                            dyeColor -> AVPBlockItems.register(
                                                    dyeColor.getName() + "_pitted_plastic_stairs",
                                                    HumanPlasticBlocks.DYE_COLOR_TO_PITTED_PLASTIC_STAIRS.get(dyeColor)
                                            )
                                    )
                            )
            );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_PLASTIC =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> AVPBlockItems.register(
                            dyeColor.getName() + "_plastic",
                            HumanPlasticBlocks.DYE_COLOR_TO_PLASTIC.get(dyeColor)
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_PLASTIC_SLAB =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> AVPBlockItems.register(
                            dyeColor.getName() + "_plastic_slab",
                            HumanPlasticBlocks.DYE_COLOR_TO_PLASTIC_SLAB.get(dyeColor)
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_PLASTIC_STAIRS =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> AVPBlockItems.register(
                            dyeColor.getName() + "_plastic_stairs",
                            HumanPlasticBlocks.DYE_COLOR_TO_PLASTIC_STAIRS.get(dyeColor)
                        )
                    )
                )
        );

    public static void initialize() {}
}
