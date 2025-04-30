package com.avp.common.block;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StainedGlassBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.avp.service.Services;

// TODO: Rename this once multi-loader migration is finished.
public class TempAVPBlocks {

    // FIXME: Change this to new RadiatedBlock once RadiatedBlock is migrated.
    public static final Supplier<Block> AUTUNITE_ORE = register("autunite_ore", () -> new Block(BlockProperties.AUTUNITE_ORE.build()));

    public static final Supplier<Block> BAUXITE_ORE = register("bauxite_ore", BlockProperties.BAUXITE_ORE);

    public static final Supplier<Block> DEEPSLATE_TITANIUM_ORE = register("deepslate_titanium_ore", BlockProperties.DEEPSLATE_TITANIUM_ORE);

    public static final Supplier<Block> DEEPSLATE_ZINC_ORE = register("deepslate_zinc_ore", BlockProperties.DEEPSLATE_ZINC_ORE);

    public static final Supplier<Block> GALENA_ORE = register("galena_ore", BlockProperties.GALENA_ORE);

    public static final Supplier<Block> LITHIUM_ORE = register("lithium_ore", () -> new LithiumBlock(BlockProperties.LITHIUM_ORE.build()));

    public static final Supplier<Block> MONAZITE_ORE = register("monazite_ore", BlockProperties.MONAZITE_ORE);

    public static final Supplier<Block> ZINC_ORE = register("zinc_ore", BlockProperties.ZINC_ORE);

    public static final Map<DyeColor, Supplier<Block>> DYE_COLOR_TO_CONCRETE_SLAB =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_concrete_slab",
                            () -> new SlabBlock(BlockProperties.DYE_COLOR_TO_CONCRETE_PROPERTIES.get(dyeColor).build())
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<Block>> DYE_COLOR_TO_CONCRETE_STAIRS =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_concrete_stairs",
                            () -> new StairBlock(
                                BlockProperties.DYE_COLOR_TO_CONCRETE_BLOCKS.get(dyeColor).defaultBlockState(),
                                BlockProperties.DYE_COLOR_TO_CONCRETE_PROPERTIES.get(dyeColor).build()
                            )
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<Block>> DYE_COLOR_TO_INDUSTRIAL_CONCRETE =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_industrial_concrete",
                            () -> new Block(BlockProperties.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_PROPERTIES.get(dyeColor).build())
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<Block>> DYE_COLOR_TO_INDUSTRIAL_CONCRETE_SLAB =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_industrial_concrete_slab",
                            () -> new SlabBlock(BlockProperties.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_PROPERTIES.get(dyeColor).build())
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<Block>> DYE_COLOR_TO_INDUSTRIAL_CONCRETE_STAIRS =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_industrial_concrete_stairs",
                            () -> new StairBlock(
                                DYE_COLOR_TO_INDUSTRIAL_CONCRETE.get(dyeColor).get().defaultBlockState(),
                                BlockProperties.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_PROPERTIES.get(dyeColor).build()
                            )
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<Block>> DYE_COLOR_TO_INDUSTRIAL_CONCRETE_WALL =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_industrial_concrete_wall",
                            () -> new WallBlock(BlockProperties.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_PROPERTIES.get(dyeColor).build())
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<Block>> DYE_COLOR_TO_INDUSTRIAL_GLASS =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_industrial_glass",
                            () -> new StainedGlassBlock(
                                dyeColor,
                                BlockProperties.DYE_COLOR_TO_INDUSTRIAL_GLASS_PROPERTIES.get(dyeColor).build()
                            )
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<Block>> DYE_COLOR_TO_INDUSTRIAL_GLASS_PANE =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_industrial_glass_pane",
                            () -> new IronBarsBlock(BlockProperties.DYE_COLOR_TO_INDUSTRIAL_GLASS_PANE_PROPERTIES.get(dyeColor).build())
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<Block>> DYE_COLOR_TO_PADDING =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_padding",
                            () -> new Block(BlockProperties.DYE_COLOR_TO_PADDING_PROPERTIES.get(dyeColor).build())
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<Block>> DYE_COLOR_TO_PADDING_SLAB =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_padding_slab",
                            () -> new SlabBlock(BlockProperties.DYE_COLOR_TO_PADDING_PROPERTIES.get(dyeColor).build())
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<Block>> DYE_COLOR_TO_PADDING_STAIRS =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_padding_stairs",
                            () -> new StairBlock(
                                DYE_COLOR_TO_PADDING.get(dyeColor).get().defaultBlockState(),
                                BlockProperties.DYE_COLOR_TO_PADDING_PROPERTIES.get(dyeColor).build()
                            )
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<Block>> DYE_COLOR_TO_PANEL_PADDING =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_panel_padding",
                            () -> new Block(BlockProperties.DYE_COLOR_TO_PADDING_PROPERTIES.get(dyeColor).build())
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<Block>> DYE_COLOR_TO_PANEL_PADDING_SLAB =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_panel_padding_slab",
                            () -> new SlabBlock(BlockProperties.DYE_COLOR_TO_PADDING_PROPERTIES.get(dyeColor).build())
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<Block>> DYE_COLOR_TO_PANEL_PADDING_STAIRS =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_panel_padding_stairs",
                            () -> new StairBlock(
                                DYE_COLOR_TO_PANEL_PADDING.get(dyeColor).get().defaultBlockState(),
                                BlockProperties.DYE_COLOR_TO_PADDING_PROPERTIES.get(dyeColor).build()
                            )
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<Block>> DYE_COLOR_TO_PIPE_PADDING =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_pipe_padding",
                            () -> new Block(BlockProperties.DYE_COLOR_TO_PADDING_PROPERTIES.get(dyeColor).build())
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<Block>> DYE_COLOR_TO_PIPE_PADDING_SLAB =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_pipe_padding_slab",
                            () -> new SlabBlock(BlockProperties.DYE_COLOR_TO_PADDING_PROPERTIES.get(dyeColor).build())
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<Block>> DYE_COLOR_TO_PIPE_PADDING_STAIRS =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_pipe_padding_stairs",
                            () -> new StairBlock(
                                DYE_COLOR_TO_PIPE_PADDING.get(dyeColor).get().defaultBlockState(),
                                BlockProperties.DYE_COLOR_TO_PADDING_PROPERTIES.get(dyeColor).build()
                            )
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<Block>> DYE_COLOR_TO_CUT_PLASTIC =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_cut_plastic",
                            () -> new Block(BlockProperties.DYE_COLOR_TO_PLASTIC_PROPERTIES.get(dyeColor).build())
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<Block>> DYE_COLOR_TO_CUT_PLASTIC_SLAB =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_cut_plastic_slab",
                            () -> new SlabBlock(BlockProperties.DYE_COLOR_TO_PLASTIC_PROPERTIES.get(dyeColor).build())
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<Block>> DYE_COLOR_TO_CUT_PLASTIC_STAIRS =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_cut_plastic_stairs",
                            () -> new StairBlock(
                                DYE_COLOR_TO_CUT_PLASTIC.get(dyeColor).get().defaultBlockState(),
                                BlockProperties.DYE_COLOR_TO_PLASTIC_PROPERTIES.get(dyeColor).build()
                            )
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<Block>> DYE_COLOR_TO_PLASTIC =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_plastic",
                            () -> new Block(BlockProperties.DYE_COLOR_TO_PLASTIC_PROPERTIES.get(dyeColor).build())
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<Block>> DYE_COLOR_TO_PLASTIC_SLAB =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_plastic_slab",
                            () -> new SlabBlock(BlockProperties.DYE_COLOR_TO_PLASTIC_PROPERTIES.get(dyeColor).build())
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<Block>> DYE_COLOR_TO_PLASTIC_STAIRS =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_plastic_stairs",
                            () -> new StairBlock(
                                DYE_COLOR_TO_PLASTIC.get(dyeColor).get().defaultBlockState(),
                                BlockProperties.DYE_COLOR_TO_PLASTIC_PROPERTIES.get(dyeColor).build()
                            )
                        )
                    )
                )
        );

    private static Supplier<Block> register(String id, BlockPropertyBuilder blockPropertyBuilder) {
        return register(id, () -> new Block(blockPropertyBuilder.build()));
    }

    private static Supplier<Block> register(String id, Supplier<Block> blockSupplier) {
        return Services.REGISTRY.register(BuiltInRegistries.BLOCK, id, blockSupplier);
    }

    public static void initialize() {}
}
