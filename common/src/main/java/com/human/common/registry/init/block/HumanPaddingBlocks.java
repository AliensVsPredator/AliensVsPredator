package com.human.common.registry.init.block;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.avp.common.gameplay.block.property.BlockProperties;
import com.avp.common.registry.AVPDeferredHolder;
import com.avp.common.registry.init.block.AVPBlocks;

public class HumanPaddingBlocks {

    public static final Map<DyeColor, AVPDeferredHolder<Block>> DYE_COLOR_TO_PADDING =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> AVPBlocks.register(
                            dyeColor.getName() + "_padding",
                            () -> new Block(BlockProperties.DYE_COLOR_TO_PADDING_PROPERTIES.get(dyeColor).build())
                        )
                    )
                )
        );

    public static final Map<DyeColor, AVPDeferredHolder<Block>> DYE_COLOR_TO_PADDING_STAIRS =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> AVPBlocks.register(
                            dyeColor.getName() + "_padding_stairs",
                            () -> new StairBlock(
                                DYE_COLOR_TO_PADDING.get(dyeColor).get().defaultBlockState(),
                                BlockProperties.DYE_COLOR_TO_PADDING_PROPERTIES.get(dyeColor).build()
                            )
                        )
                    )
                )
        );

    public static final Map<DyeColor, AVPDeferredHolder<Block>> DYE_COLOR_TO_PADDING_SLAB =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> AVPBlocks.register(
                            dyeColor.getName() + "_padding_slab",
                            () -> new SlabBlock(BlockProperties.DYE_COLOR_TO_PADDING_PROPERTIES.get(dyeColor).build())
                        )
                    )
                )
        );

    public static final Map<DyeColor, AVPDeferredHolder<Block>> DYE_COLOR_TO_PANEL_PADDING =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> AVPBlocks.register(
                            dyeColor.getName() + "_panel_padding",
                            () -> new Block(BlockProperties.DYE_COLOR_TO_PADDING_PROPERTIES.get(dyeColor).build())
                        )
                    )
                )
        );

    public static final Map<DyeColor, AVPDeferredHolder<Block>> DYE_COLOR_TO_PANEL_PADDING_STAIRS =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> AVPBlocks.register(
                            dyeColor.getName() + "_panel_padding_stairs",
                            () -> new StairBlock(
                                DYE_COLOR_TO_PANEL_PADDING.get(dyeColor).get().defaultBlockState(),
                                BlockProperties.DYE_COLOR_TO_PADDING_PROPERTIES.get(dyeColor).build()
                            )
                        )
                    )
                )
        );

    public static final Map<DyeColor, AVPDeferredHolder<Block>> DYE_COLOR_TO_PANEL_PADDING_SLAB =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> AVPBlocks.register(
                            dyeColor.getName() + "_panel_padding_slab",
                            () -> new SlabBlock(BlockProperties.DYE_COLOR_TO_PADDING_PROPERTIES.get(dyeColor).build())
                        )
                    )
                )
        );

    public static final Map<DyeColor, AVPDeferredHolder<Block>> DYE_COLOR_TO_PIPE_PADDING =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> AVPBlocks.register(
                            dyeColor.getName() + "_pipe_padding",
                            () -> new Block(BlockProperties.DYE_COLOR_TO_PADDING_PROPERTIES.get(dyeColor).build())
                        )
                    )
                )
        );

    public static final Map<DyeColor, AVPDeferredHolder<Block>> DYE_COLOR_TO_PIPE_PADDING_STAIRS =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> AVPBlocks.register(
                            dyeColor.getName() + "_pipe_padding_stairs",
                            () -> new StairBlock(
                                DYE_COLOR_TO_PIPE_PADDING.get(dyeColor).get().defaultBlockState(),
                                BlockProperties.DYE_COLOR_TO_PADDING_PROPERTIES.get(dyeColor).build()
                            )
                        )
                    )
                )
        );

    public static final Map<DyeColor, AVPDeferredHolder<Block>> DYE_COLOR_TO_PIPE_PADDING_SLAB =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> AVPBlocks.register(
                            dyeColor.getName() + "_pipe_padding_slab",
                            () -> new SlabBlock(BlockProperties.DYE_COLOR_TO_PADDING_PROPERTIES.get(dyeColor).build())
                        )
                    )
                )
        );

    public static final Map<DyeColor, AVPDeferredHolder<Block>> DYE_COLOR_TO_TILE_PADDING =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> AVPBlocks.register(
                            dyeColor.getName() + "_tile_padding",
                            () -> new Block(BlockProperties.DYE_COLOR_TO_PADDING_PROPERTIES.get(dyeColor).build())
                        )
                    )
                )
        );

    public static final Map<DyeColor, AVPDeferredHolder<Block>> DYE_COLOR_TO_TILE_PADDING_STAIRS =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> AVPBlocks.register(
                            dyeColor.getName() + "_tile_padding_stairs",
                            () -> new StairBlock(
                                DYE_COLOR_TO_TILE_PADDING.get(dyeColor).get().defaultBlockState(),
                                BlockProperties.DYE_COLOR_TO_PADDING_PROPERTIES.get(dyeColor).build()
                            )
                        )
                    )
                )
        );

    public static final Map<DyeColor, AVPDeferredHolder<Block>> DYE_COLOR_TO_TILE_PADDING_SLAB =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> AVPBlocks.register(
                            dyeColor.getName() + "_tile_padding_slab",
                            () -> new SlabBlock(BlockProperties.DYE_COLOR_TO_PADDING_PROPERTIES.get(dyeColor).build())
                        )
                    )
                )
        );

    public static void initialize() {}
}
