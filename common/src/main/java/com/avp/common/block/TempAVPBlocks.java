package com.avp.common.block;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StainedGlassBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.WaterloggedTransparentBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;

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

    public static final Supplier<Block> FERROALUMINUM_BLOCK = register("ferroaluminum_block", BlockProperties.FERROALUMINUM);

    public static final Supplier<Block> FERROALUMINUM_CHAIN_FENCE = register(
        "ferroaluminum_chain_fence",
        () -> new IronBarsBlock(BlockProperties.FERROALUMINUM_BARS.build().sound(SoundType.CHAIN))
    );

    public static final Supplier<Block> FERROALUMINUM_COLUMN = register(
        "ferroaluminum_column",
        () -> new RotatedPillarBlock(BlockProperties.FERROALUMINUM.build())
    );

    public static final Supplier<Block> FERROALUMINUM_FASTENED_SIDING = register("ferroaluminum_fastened_siding", BlockProperties.FERROALUMINUM);

    public static final Supplier<Block> FERROALUMINUM_FASTENED_SIDING_SLAB = register(
        "ferroaluminum_fastened_siding_slab",
        () -> new SlabBlock(BlockProperties.FERROALUMINUM.build())
    );

    public static final Supplier<Block> FERROALUMINUM_FASTENED_SIDING_STAIRS = register(
        "ferroaluminum_fastened_siding_stairs",
        () -> new StairBlock(FERROALUMINUM_FASTENED_SIDING.get().defaultBlockState(), BlockProperties.FERROALUMINUM.build())
    );

    public static final Supplier<Block> FERROALUMINUM_FASTENED_STANDING = register(
        "ferroaluminum_fastened_standing",
        BlockProperties.FERROALUMINUM
    );

    public static final Supplier<Block> FERROALUMINUM_FASTENED_STANDING_SLAB = register(
        "ferroaluminum_fastened_standing_slab",
        () -> new SlabBlock(BlockProperties.FERROALUMINUM.build())
    );

    public static final Supplier<Block> FERROALUMINUM_FASTENED_STANDING_STAIRS = register(
        "ferroaluminum_fastened_standing_stairs",
        () -> new StairBlock(FERROALUMINUM_FASTENED_STANDING.get().defaultBlockState(), BlockProperties.FERROALUMINUM.build())
    );

    public static final Supplier<Block> FERROALUMINUM_GRATE = register(
        "ferroaluminum_grate",
        () -> new WaterloggedTransparentBlock(BlockProperties.FERROALUMINUM_GRATE.build())
    );

    public static final Supplier<Block> FERROALUMINUM_GRATE_SLAB = register(
        "ferroaluminum_grate_slab",
        () -> new SlabBlock(BlockProperties.FERROALUMINUM.build().noOcclusion())
    );

    public static final Supplier<Block> FERROALUMINUM_GRATE_STAIRS = register(
        "ferroaluminum_grate_stairs",
        () -> new StairBlock(FERROALUMINUM_GRATE.get().defaultBlockState(), BlockProperties.FERROALUMINUM.build().noOcclusion())
    );

    public static final Supplier<Block> FERROALUMINUM_PLATING = register("ferroaluminum_plating", BlockProperties.FERROALUMINUM);

    public static final Supplier<Block> FERROALUMINUM_PLATING_SLAB = register(
        "ferroaluminum_plating_slab",
        () -> new SlabBlock(BlockProperties.FERROALUMINUM.build())
    );

    public static final Supplier<Block> FERROALUMINUM_PLATING_STAIRS = register(
        "ferroaluminum_plating_stairs",
        () -> new StairBlock(FERROALUMINUM_PLATING.get().defaultBlockState(), BlockProperties.FERROALUMINUM.build())
    );

    public static final Supplier<Block> FERROALUMINUM_SLAB = register(
        "ferroaluminum_slab",
        () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(FERROALUMINUM_BLOCK.get()))
    );

    public static final Supplier<Block> FERROALUMINUM_SIDING = register("ferroaluminum_siding", BlockProperties.FERROALUMINUM);

    public static final Supplier<Block> FERROALUMINUM_SIDING_SLAB = register(
        "ferroaluminum_siding_slab",
        () -> new SlabBlock(BlockProperties.FERROALUMINUM.build())
    );

    public static final Supplier<Block> FERROALUMINUM_SIDING_STAIRS = register(
        "ferroaluminum_siding_stairs",
        () -> new StairBlock(FERROALUMINUM_SIDING.get().defaultBlockState(), BlockProperties.FERROALUMINUM.build())
    );

    public static final Supplier<Block> FERROALUMINUM_STAIRS = register(
        "ferroaluminum_stairs",
        () -> new StairBlock(FERROALUMINUM_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(FERROALUMINUM_BLOCK.get()))
    );

    public static final Supplier<Block> FERROALUMINUM_STANDING = register("ferroaluminum_standing", BlockProperties.FERROALUMINUM);

    public static final Supplier<Block> FERROALUMINUM_STANDING_SLAB = register(
        "ferroaluminum_standing_slab",
        () -> new SlabBlock(BlockProperties.FERROALUMINUM.build())
    );

    public static final Supplier<Block> FERROALUMINUM_STANDING_STAIRS = register(
        "ferroaluminum_standing_stairs",
        () -> new StairBlock(FERROALUMINUM_STANDING.get().defaultBlockState(), BlockProperties.FERROALUMINUM.build())
    );

    public static final Supplier<Block> FERROALUMINUM_TREAD = register("ferroaluminum_tread", BlockProperties.FERROALUMINUM);

    public static final Supplier<Block> FERROALUMINUM_TREAD_SLAB = register(
        "ferroaluminum_tread_slab",
        () -> new SlabBlock(BlockProperties.FERROALUMINUM.build())
    );

    public static final Supplier<Block> FERROALUMINUM_TREAD_STAIRS = register(
        "ferroaluminum_tread_stairs",
        () -> new StairBlock(FERROALUMINUM_TREAD.get().defaultBlockState(), BlockProperties.FERROALUMINUM.build())
    );

    public static final Supplier<Block> GALENA_ORE = register("galena_ore", BlockProperties.GALENA_ORE);

    public static final Supplier<Block> LITHIUM_ORE = register("lithium_ore", () -> new LithiumBlock(BlockProperties.LITHIUM_ORE.build()));

    public static final Supplier<Block> MONAZITE_ORE = register("monazite_ore", BlockProperties.MONAZITE_ORE);

    public static final Supplier<Block> STEEL_BLOCK = register("steel_block", BlockProperties.STEEL);

    public static final Supplier<Block> STEEL_BARS = register(
        "steel_bars",
        () -> new IronBarsBlock(BlockProperties.STEEL_BARS.build())
    );

    public static final Supplier<Block> STEEL_CHAIN_FENCE = register(
        "steel_chain_fence",
        () -> new IronBarsBlock(BlockProperties.STEEL_BARS.build().sound(SoundType.CHAIN))
    );

    public static final Supplier<Block> STEEL_COLUMN = register("steel_column", () -> new RotatedPillarBlock(BlockProperties.STEEL.build()));

    public static final Supplier<Block> STEEL_FASTENED_SIDING = register("steel_fastened_siding", BlockProperties.STEEL);

    public static final Supplier<Block> STEEL_FASTENED_SIDING_SLAB = register(
        "steel_fastened_siding_slab",
        () -> new SlabBlock(BlockProperties.STEEL.build())
    );

    public static final Supplier<Block> STEEL_FASTENED_SIDING_STAIRS = register(
        "steel_fastened_siding_stairs",
        () -> new StairBlock(STEEL_FASTENED_SIDING.get().defaultBlockState(), BlockProperties.STEEL.build())
    );

    public static final Supplier<Block> STEEL_FASTENED_STANDING = register("steel_fastened_standing", BlockProperties.STEEL);

    public static final Supplier<Block> STEEL_FASTENED_STANDING_SLAB = register(
        "steel_fastened_standing_slab",
        () -> new SlabBlock(BlockProperties.STEEL.build())
    );

    public static final Supplier<Block> STEEL_FASTENED_STANDING_STAIRS = register(
        "steel_fastened_standing_stairs",
        () -> new StairBlock(STEEL_FASTENED_STANDING.get().defaultBlockState(), BlockProperties.STEEL.build())
    );

    public static final Supplier<Block> STEEL_GRATE = register(
        "steel_grate",
        () -> new WaterloggedTransparentBlock(BlockProperties.STEEL_GRATE.build())
    );

    public static final Supplier<Block> STEEL_GRATE_SLAB = register("steel_grate_slab", () -> new SlabBlock(BlockProperties.STEEL.build().noOcclusion()));

    public static final Supplier<Block> STEEL_GRATE_STAIRS = register(
        "steel_grate_stairs",
        () -> new StairBlock(STEEL_GRATE.get().defaultBlockState(), BlockProperties.STEEL.build().noOcclusion())
    );

    public static final Supplier<Block> STEEL_PLATING = register("steel_plating", BlockProperties.STEEL);

    public static final Supplier<Block> STEEL_PLATING_SLAB = register("steel_plating_slab", () -> new SlabBlock(BlockProperties.STEEL.build()));

    public static final Supplier<Block> STEEL_PLATING_STAIRS = register(
        "steel_plating_stairs",
        () -> new StairBlock(STEEL_PLATING.get().defaultBlockState(), BlockProperties.STEEL.build())
    );

    public static final Supplier<Block> STEEL_PRESSURE_PLATE = register(
        "steel_pressure_plate",
        () -> new PressurePlateBlock(BlockSetType.COPPER, BlockProperties.STEEL.build())
    );

    public static final Supplier<Block> STEEL_SLAB = register(
        "steel_slab",
        () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(STEEL_BLOCK.get()))
    );

    public static final Supplier<Block> STEEL_SIDING = register("steel_siding", BlockProperties.STEEL);

    public static final Supplier<Block> STEEL_SIDING_SLAB = register("steel_siding_slab", () -> new SlabBlock(BlockProperties.STEEL.build()));

    public static final Supplier<Block> STEEL_SIDING_STAIRS = register(
        "steel_siding_stairs",
        () -> new StairBlock(STEEL_SIDING.get().defaultBlockState(), BlockProperties.STEEL.build())
    );

    public static final Supplier<Block> STEEL_STAIRS = register(
        "steel_stairs",
        () -> new StairBlock(STEEL_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(STEEL_BLOCK.get()))
    );

    public static final Supplier<Block> STEEL_STANDING = register("steel_standing", BlockProperties.STEEL);

    public static final Supplier<Block> STEEL_STANDING_SLAB = register("steel_standing_slab", () -> new SlabBlock(BlockProperties.STEEL.build()));

    public static final Supplier<Block> STEEL_STANDING_STAIRS = register(
        "steel_standing_stairs",
        () -> new StairBlock(STEEL_STANDING.get().defaultBlockState(), BlockProperties.STEEL.build())
    );

    public static final Supplier<Block> STEEL_TREAD = register("steel_tread", BlockProperties.STEEL);

    public static final Supplier<Block> STEEL_TREAD_SLAB = register("steel_tread_slab", () -> new SlabBlock(BlockProperties.STEEL.build()));

    public static final Supplier<Block> STEEL_TREAD_STAIRS = register(
        "steel_tread_stairs",
        () -> new StairBlock(STEEL_TREAD.get().defaultBlockState(), BlockProperties.STEEL.build())
    );

    public static final Supplier<Block>  TITANIUM_BLOCK = register("titanium_block", BlockProperties.TITANIUM);

    public static final Supplier<Block>  TITANIUM_CHAIN_FENCE = register(
        "titanium_chain_fence",
        () -> new IronBarsBlock(BlockProperties.TITANIUM_BARS.build().sound(SoundType.CHAIN))
    );

    public static final Supplier<Block>  TITANIUM_COLUMN = register("titanium_column", () -> new RotatedPillarBlock(BlockProperties.TITANIUM.build()));

    public static final Supplier<Block>  TITANIUM_FASTENED_SIDING = register("titanium_fastened_siding", BlockProperties.TITANIUM);

    public static final Supplier<Block> TITANIUM_FASTENED_SIDING_SLAB = register(
        "titanium_fastened_siding_slab",
        () -> new SlabBlock(BlockProperties.TITANIUM.build())
    );

    public static final Supplier<Block> TITANIUM_FASTENED_SIDING_STAIRS = register(
        "titanium_fastened_siding_stairs",
        () -> new StairBlock(TITANIUM_FASTENED_SIDING.get().defaultBlockState(), BlockProperties.TITANIUM.build())
    );

    public static final Supplier<Block>  TITANIUM_FASTENED_STANDING = register("titanium_fastened_standing", BlockProperties.TITANIUM);

    public static final Supplier<Block> TITANIUM_FASTENED_STANDING_SLAB = register(
        "titanium_fastened_standing_slab",
        () -> new SlabBlock(BlockProperties.TITANIUM.build())
    );

    public static final Supplier<Block> TITANIUM_FASTENED_STANDING_STAIRS = register(
        "titanium_fastened_standing_stairs",
        () -> new StairBlock(TITANIUM_FASTENED_STANDING.get().defaultBlockState(), BlockProperties.TITANIUM.build())
    );

    public static final Supplier<Block>  TITANIUM_GRATE = register(
        "titanium_grate",
        () -> new WaterloggedTransparentBlock(BlockProperties.TITANIUM_GRATE.build())
    );

    public static final Supplier<Block> TITANIUM_GRATE_SLAB = register(
        "titanium_grate_slab",
        () -> new SlabBlock(BlockProperties.TITANIUM.build().noOcclusion())
    );

    public static final Supplier<Block> TITANIUM_GRATE_STAIRS = register(
        "titanium_grate_stairs",
        () -> new StairBlock(TITANIUM_GRATE.get().defaultBlockState(), BlockProperties.TITANIUM.build().noOcclusion())
    );

    public static final Supplier<Block>  TITANIUM_PLATING = register("titanium_plating", BlockProperties.TITANIUM);

    public static final Supplier<Block> TITANIUM_PLATING_SLAB = register("titanium_plating_slab", () -> new SlabBlock(BlockProperties.TITANIUM.build()));

    public static final Supplier<Block> TITANIUM_PLATING_STAIRS = register(
        "titanium_plating_stairs",
        () -> new StairBlock(TITANIUM_PLATING.get().defaultBlockState(), BlockProperties.TITANIUM.build())
    );

    public static final Supplier<Block>  TITANIUM_SIDING = register("titanium_siding", BlockProperties.TITANIUM);

    public static final Supplier<Block> TITANIUM_SIDING_SLAB = register("titanium_siding_slab", () -> new SlabBlock(BlockProperties.TITANIUM.build()));

    public static final Supplier<Block> TITANIUM_SIDING_STAIRS = register(
        "titanium_siding_stairs",
        () -> new StairBlock(TITANIUM_SIDING.get().defaultBlockState(), BlockProperties.TITANIUM.build())
    );

    public static final Supplier<Block> TITANIUM_SLAB = register(
        "titanium_slab",
        () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(TITANIUM_BLOCK.get()))
    );

    public static final Supplier<Block> TITANIUM_STAIRS = register(
        "titanium_stairs",
        () -> new StairBlock(TITANIUM_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(TITANIUM_BLOCK.get()))
    );

    public static final Supplier<Block>  TITANIUM_STANDING = register("titanium_standing", BlockProperties.TITANIUM);

    public static final Supplier<Block> TITANIUM_STANDING_SLAB = register("titanium_standing_slab", () -> new SlabBlock(BlockProperties.TITANIUM.build()));

    public static final Supplier<Block> TITANIUM_STANDING_STAIRS = register(
        "titanium_standing_stairs",
        () -> new StairBlock(TITANIUM_STANDING.get().defaultBlockState(), BlockProperties.TITANIUM.build())
    );

    public static final Supplier<Block>  TITANIUM_TREAD = register("titanium_tread", BlockProperties.TITANIUM);

    public static final Supplier<Block> TITANIUM_TREAD_SLAB = register("titanium_tread_slab", () -> new SlabBlock(BlockProperties.TITANIUM.build()));

    public static final Supplier<Block> TITANIUM_TREAD_STAIRS = register(
        "titanium_tread_stairs",
        () -> new StairBlock(TITANIUM_TREAD.get().defaultBlockState(), BlockProperties.TITANIUM.build())
    );

    // FIXME: Change this to new RadiatedBlock once RadiatedBlock is migrated.
    public static final Supplier<Block>  URANIUM_BLOCK = register("uranium_block", BlockProperties.URANIUM);

    public static final Supplier<Block>  ZINC_BLOCK = register("zinc_block", BlockProperties.ZINC);

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
