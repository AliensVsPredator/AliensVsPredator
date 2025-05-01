package com.avp.common.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.avp.common.block.TempAVPBlocks;
import com.avp.service.Services;

// TODO: Rename this once multi-loader migration is finished.
public class TempAVPBlockItems {

    public static final Supplier<BlockItem> AUTUNITE_ORE = register("autunite_ore", TempAVPBlocks.AUTUNITE_ORE);

    public static final Supplier<BlockItem> BAUXITE_ORE = register("bauxite_ore", TempAVPBlocks.BAUXITE_ORE);

    public static final Supplier<BlockItem> DEEPSLATE_TITANIUM_ORE = register(
        "deepslate_titanium_ore",
        TempAVPBlocks.DEEPSLATE_TITANIUM_ORE
    );

    public static final Supplier<BlockItem> DEEPSLATE_ZINC_ORE = register("deepslate_zinc_ore", TempAVPBlocks.DEEPSLATE_ZINC_ORE);

    public static final Supplier<BlockItem> FERROALUMINUM_BLOCK = register("ferroaluminum_block", TempAVPBlocks.FERROALUMINUM_BLOCK);

    public static final Supplier<BlockItem> FERROALUMINUM_CHAIN_FENCE = register("ferroaluminum_chain_fence", TempAVPBlocks.FERROALUMINUM_CHAIN_FENCE);

    public static final Supplier<BlockItem> FERROALUMINUM_COLUMN = register("ferroaluminum_column", TempAVPBlocks.FERROALUMINUM_COLUMN);

    public static final Supplier<BlockItem> FERROALUMINUM_FASTENED_SIDING = register("ferroaluminum_fastened_siding", TempAVPBlocks.FERROALUMINUM_FASTENED_SIDING);

    public static final Supplier<BlockItem> FERROALUMINUM_FASTENED_SIDING_SLAB = register("ferroaluminum_fastened_siding_slab", TempAVPBlocks.FERROALUMINUM_FASTENED_SIDING_SLAB);

    public static final Supplier<BlockItem> FERROALUMINUM_FASTENED_SIDING_STAIRS = register("ferroaluminum_fastened_siding_stairs", TempAVPBlocks.FERROALUMINUM_FASTENED_SIDING_STAIRS);

    public static final Supplier<BlockItem> FERROALUMINUM_FASTENED_STANDING = register("ferroaluminum_fastened_standing", TempAVPBlocks.FERROALUMINUM_FASTENED_STANDING);

    public static final Supplier<BlockItem> FERROALUMINUM_FASTENED_STANDING_SLAB = register("ferroaluminum_fastened_standing_slab", TempAVPBlocks.FERROALUMINUM_FASTENED_STANDING_SLAB);

    public static final Supplier<BlockItem> FERROALUMINUM_FASTENED_STANDING_STAIRS = register("ferroaluminum_fastened_standing_stairs", TempAVPBlocks.FERROALUMINUM_FASTENED_STANDING_STAIRS);

    public static final Supplier<BlockItem> FERROALUMINUM_GRATE = register("ferroaluminum_grate", TempAVPBlocks.FERROALUMINUM_GRATE);

    public static final Supplier<BlockItem> FERROALUMINUM_GRATE_SLAB = register("ferroaluminum_grate_slab", TempAVPBlocks.FERROALUMINUM_GRATE_SLAB);

    public static final Supplier<BlockItem> FERROALUMINUM_GRATE_STAIRS = register("ferroaluminum_grate_stairs", TempAVPBlocks.FERROALUMINUM_GRATE_STAIRS);

    public static final Supplier<BlockItem> FERROALUMINUM_PLATING = register("ferroaluminum_plating", TempAVPBlocks.FERROALUMINUM_PLATING);

    public static final Supplier<BlockItem> FERROALUMINUM_PLATING_SLAB = register("ferroaluminum_plating_slab", TempAVPBlocks.FERROALUMINUM_PLATING_SLAB);

    public static final Supplier<BlockItem> FERROALUMINUM_PLATING_STAIRS = register("ferroaluminum_plating_stairs", TempAVPBlocks.FERROALUMINUM_PLATING_STAIRS);

    public static final Supplier<BlockItem> FERROALUMINUM_SIDING = register("ferroaluminum_siding", TempAVPBlocks.FERROALUMINUM_SIDING);

    public static final Supplier<BlockItem> FERROALUMINUM_SIDING_SLAB = register("ferroaluminum_siding_slab", TempAVPBlocks.FERROALUMINUM_SIDING_SLAB);

    public static final Supplier<BlockItem> FERROALUMINUM_SIDING_STAIRS = register("ferroaluminum_siding_stairs", TempAVPBlocks.FERROALUMINUM_SIDING_STAIRS);

    public static final Supplier<BlockItem> FERROALUMINUM_SLAB = register("ferroaluminum_slab", TempAVPBlocks.FERROALUMINUM_SLAB);

    public static final Supplier<BlockItem> FERROALUMINUM_STAIRS = register("ferroaluminum_stairs", TempAVPBlocks.FERROALUMINUM_STAIRS);

    public static final Supplier<BlockItem> FERROALUMINUM_STANDING = register("ferroaluminum_standing", TempAVPBlocks.FERROALUMINUM_STANDING);

    public static final Supplier<BlockItem> FERROALUMINUM_STANDING_SLAB = register("ferroaluminum_standing_slab", TempAVPBlocks.FERROALUMINUM_STANDING_SLAB);

    public static final Supplier<BlockItem> FERROALUMINUM_STANDING_STAIRS = register("ferroaluminum_standing_stairs", TempAVPBlocks.FERROALUMINUM_STANDING_STAIRS);

    public static final Supplier<BlockItem> FERROALUMINUM_TREAD = register("ferroaluminum_tread", TempAVPBlocks.FERROALUMINUM_TREAD);

    public static final Supplier<BlockItem> FERROALUMINUM_TREAD_SLAB = register("ferroaluminum_tread_slab", TempAVPBlocks.FERROALUMINUM_TREAD_SLAB);

    public static final Supplier<BlockItem> FERROALUMINUM_TREAD_STAIRS = register("ferroaluminum_tread_stairs", TempAVPBlocks.FERROALUMINUM_TREAD_STAIRS);

    public static final Supplier<BlockItem> GALENA_ORE = register("galena_ore", TempAVPBlocks.GALENA_ORE);

    public static final Supplier<BlockItem> LITHIUM_ORE = register("lithium_ore", TempAVPBlocks.LITHIUM_ORE);

    public static final Supplier<BlockItem> MONAZITE_ORE = register("monazite_ore", TempAVPBlocks.MONAZITE_ORE);

    public static final Supplier<BlockItem> STEEL_BARS = register("steel_bars", TempAVPBlocks.STEEL_BARS);

    public static final Supplier<BlockItem> STEEL_BLOCK = register("steel_block", TempAVPBlocks.STEEL_BLOCK);

    public static final Supplier<BlockItem> STEEL_CHAIN_FENCE = register("steel_chain_fence", TempAVPBlocks.STEEL_CHAIN_FENCE);

    public static final Supplier<BlockItem> STEEL_COLUMN = register("steel_column", TempAVPBlocks.STEEL_COLUMN);

    public static final Supplier<BlockItem> STEEL_FASTENED_SIDING = register("steel_fastened_siding", TempAVPBlocks.STEEL_FASTENED_SIDING);

    public static final Supplier<BlockItem> STEEL_FASTENED_SIDING_SLAB = register(
        "steel_fastened_siding_slab",
        TempAVPBlocks.STEEL_FASTENED_SIDING_SLAB
    );

    public static final Supplier<BlockItem> STEEL_FASTENED_SIDING_STAIRS = register(
        "steel_fastened_siding_stairs",
        TempAVPBlocks.STEEL_FASTENED_SIDING_STAIRS
    );

    public static final Supplier<BlockItem> STEEL_FASTENED_STANDING = register(
        "steel_fastened_standing",
        TempAVPBlocks.STEEL_FASTENED_STANDING
    );

    public static final Supplier<BlockItem> STEEL_FASTENED_STANDING_SLAB = register(
        "steel_fastened_standing_slab",
        TempAVPBlocks.STEEL_FASTENED_STANDING_SLAB
    );

    public static final Supplier<BlockItem> STEEL_FASTENED_STANDING_STAIRS = register(
        "steel_fastened_standing_stairs",
        TempAVPBlocks.STEEL_FASTENED_STANDING_STAIRS
    );

    public static final Supplier<BlockItem> STEEL_GRATE = register("steel_grate", TempAVPBlocks.STEEL_GRATE);

    public static final Supplier<BlockItem> STEEL_GRATE_SLAB = register("steel_grate_slab", TempAVPBlocks.STEEL_GRATE_SLAB);

    public static final Supplier<BlockItem> STEEL_GRATE_STAIRS = register("steel_grate_stairs", TempAVPBlocks.STEEL_GRATE_STAIRS);

    public static final Supplier<BlockItem> STEEL_PLATING = register("steel_plating", TempAVPBlocks.STEEL_PLATING);

    public static final Supplier<BlockItem> STEEL_PLATING_SLAB = register("steel_plating_slab", TempAVPBlocks.STEEL_PLATING_SLAB);

    public static final Supplier<BlockItem> STEEL_PLATING_STAIRS = register("steel_plating_stairs", TempAVPBlocks.STEEL_PLATING_STAIRS);

    public static final Supplier<BlockItem> STEEL_PRESSURE_PLATE = register("steel_pressure_plate", TempAVPBlocks.STEEL_PRESSURE_PLATE);

    public static final Supplier<BlockItem> STEEL_SIDING = register("steel_siding", TempAVPBlocks.STEEL_SIDING);

    public static final Supplier<BlockItem> STEEL_SIDING_SLAB = register("steel_siding_slab", TempAVPBlocks.STEEL_SIDING_SLAB);

    public static final Supplier<BlockItem> STEEL_SIDING_STAIRS = register("steel_siding_stairs", TempAVPBlocks.STEEL_SIDING_STAIRS);

    public static final Supplier<BlockItem> STEEL_SLAB = register("steel_slab", TempAVPBlocks.STEEL_SLAB);

    public static final Supplier<BlockItem> STEEL_STAIRS = register("steel_stairs", TempAVPBlocks.STEEL_STAIRS);

    public static final Supplier<BlockItem> STEEL_STANDING = register("steel_standing", TempAVPBlocks.STEEL_STANDING);

    public static final Supplier<BlockItem> STEEL_STANDING_SLAB = register("steel_standing_slab", TempAVPBlocks.STEEL_STANDING_SLAB);

    public static final Supplier<BlockItem> STEEL_STANDING_STAIRS = register("steel_standing_stairs", TempAVPBlocks.STEEL_STANDING_STAIRS);

    public static final Supplier<BlockItem> STEEL_TREAD = register("steel_tread", TempAVPBlocks.STEEL_TREAD);

    public static final Supplier<BlockItem> STEEL_TREAD_SLAB = register("steel_tread_slab", TempAVPBlocks.STEEL_TREAD_SLAB);

    public static final Supplier<BlockItem> STEEL_TREAD_STAIRS = register("steel_tread_stairs", TempAVPBlocks.STEEL_TREAD_STAIRS);

    public static final Supplier<BlockItem> TITANIUM_BLOCK = register("titanium_block", TempAVPBlocks.TITANIUM_BLOCK);

    public static final Supplier<BlockItem> TITANIUM_CHAIN_FENCE = register("titanium_chain_fence", TempAVPBlocks.TITANIUM_CHAIN_FENCE);

    public static final Supplier<BlockItem> TITANIUM_COLUMN = register("titanium_column", TempAVPBlocks.TITANIUM_COLUMN);

    public static final Supplier<BlockItem> TITANIUM_FASTENED_SIDING = register("titanium_fastened_siding", TempAVPBlocks.TITANIUM_FASTENED_SIDING);

    public static final Supplier<BlockItem> TITANIUM_FASTENED_SIDING_SLAB = register("titanium_fastened_siding_slab", TempAVPBlocks.TITANIUM_FASTENED_SIDING_SLAB);

    public static final Supplier<BlockItem> TITANIUM_FASTENED_SIDING_STAIRS = register("titanium_fastened_siding_stairs", TempAVPBlocks.TITANIUM_FASTENED_SIDING_STAIRS);

    public static final Supplier<BlockItem> TITANIUM_FASTENED_STANDING = register("titanium_fastened_standing", TempAVPBlocks.TITANIUM_FASTENED_STANDING);

    public static final Supplier<BlockItem> TITANIUM_FASTENED_STANDING_SLAB = register("titanium_fastened_standing_slab", TempAVPBlocks.TITANIUM_FASTENED_STANDING_SLAB);

    public static final Supplier<BlockItem> TITANIUM_FASTENED_STANDING_STAIRS = register("titanium_fastened_standing_stairs", TempAVPBlocks.TITANIUM_FASTENED_STANDING_STAIRS);

    public static final Supplier<BlockItem> TITANIUM_GRATE = register("titanium_grate", TempAVPBlocks.TITANIUM_GRATE);

    public static final Supplier<BlockItem> TITANIUM_GRATE_SLAB = register("titanium_grate_slab", TempAVPBlocks.TITANIUM_GRATE_SLAB);

    public static final Supplier<BlockItem> TITANIUM_GRATE_STAIRS = register("titanium_grate_stairs", TempAVPBlocks.TITANIUM_GRATE_STAIRS);

    public static final Supplier<BlockItem> TITANIUM_PLATING = register("titanium_plating", TempAVPBlocks.TITANIUM_PLATING);

    public static final Supplier<BlockItem> TITANIUM_PLATING_SLAB = register("titanium_plating_slab", TempAVPBlocks.TITANIUM_PLATING_SLAB);

    public static final Supplier<BlockItem> TITANIUM_PLATING_STAIRS = register("titanium_plating_stairs", TempAVPBlocks.TITANIUM_PLATING_STAIRS);

    public static final Supplier<BlockItem> TITANIUM_SIDING = register("titanium_siding", TempAVPBlocks.TITANIUM_SIDING);

    public static final Supplier<BlockItem> TITANIUM_SIDING_SLAB = register("titanium_siding_slab", TempAVPBlocks.TITANIUM_SIDING_SLAB);

    public static final Supplier<BlockItem> TITANIUM_SIDING_STAIRS = register("titanium_siding_stairs", TempAVPBlocks.TITANIUM_SIDING_STAIRS);

    public static final Supplier<BlockItem> TITANIUM_SLAB = register("titanium_slab", TempAVPBlocks.TITANIUM_SLAB);

    public static final Supplier<BlockItem> TITANIUM_STAIRS = register("titanium_stairs", TempAVPBlocks.TITANIUM_STAIRS);

    public static final Supplier<BlockItem> TITANIUM_STANDING = register("titanium_standing", TempAVPBlocks.TITANIUM_STANDING);

    public static final Supplier<BlockItem> TITANIUM_STANDING_SLAB = register("titanium_standing_slab", TempAVPBlocks.TITANIUM_STANDING_SLAB);

    public static final Supplier<BlockItem> TITANIUM_STANDING_STAIRS = register("titanium_standing_stairs", TempAVPBlocks.TITANIUM_STANDING_STAIRS);

    public static final Supplier<BlockItem> TITANIUM_TREAD = register("titanium_tread", TempAVPBlocks.TITANIUM_TREAD);

    public static final Supplier<BlockItem> TITANIUM_TREAD_SLAB = register("titanium_tread_slab", TempAVPBlocks.TITANIUM_TREAD_SLAB);

    public static final Supplier<BlockItem> TITANIUM_TREAD_STAIRS = register("titanium_tread_stairs", TempAVPBlocks.TITANIUM_TREAD_STAIRS);

    public static final Supplier<BlockItem> URANIUM_BLOCK = register("uranium_block", TempAVPBlocks.URANIUM_BLOCK);

    public static final Supplier<BlockItem> ZINC_BLOCK = register("zinc_block", TempAVPBlocks.ZINC_BLOCK);

    public static final Supplier<BlockItem> ZINC_ORE = register("zinc_ore", TempAVPBlocks.ZINC_ORE);

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_CONCRETE_SLAB =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_concrete_slab",
                            TempAVPBlocks.DYE_COLOR_TO_CONCRETE_SLAB.get(dyeColor)
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_CONCRETE_STAIRS =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_concrete_stairs",
                            TempAVPBlocks.DYE_COLOR_TO_CONCRETE_STAIRS.get(dyeColor)
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_INDUSTRIAL_CONCRETE =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_industrial_concrete",
                            TempAVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE.get(dyeColor)
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
                        dyeColor -> register(
                            dyeColor.getName() + "_industrial_concrete_slab",
                            TempAVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_SLAB.get(dyeColor)
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
                        dyeColor -> register(
                            dyeColor.getName() + "_industrial_concrete_stairs",
                            TempAVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_STAIRS.get(dyeColor)
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
                        dyeColor -> register(
                            dyeColor.getName() + "_industrial_concrete_wall",
                            TempAVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_WALL.get(dyeColor)
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_INDUSTRIAL_GLASS =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_industrial_glass",
                            TempAVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS.get(dyeColor)
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
                        dyeColor -> register(
                            dyeColor.getName() + "_industrial_glass_pane",
                            TempAVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS_PANE.get(dyeColor)
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_PADDING =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_padding",
                            TempAVPBlocks.DYE_COLOR_TO_PADDING.get(dyeColor)
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_PADDING_SLAB =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_padding_slab",
                            TempAVPBlocks.DYE_COLOR_TO_PADDING_SLAB.get(dyeColor)
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_PADDING_STAIRS =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_padding_stairs",
                            TempAVPBlocks.DYE_COLOR_TO_PADDING_STAIRS.get(dyeColor)
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_PANEL_PADDING =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_panel_padding",
                            TempAVPBlocks.DYE_COLOR_TO_PANEL_PADDING.get(dyeColor)
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_PANEL_PADDING_SLAB =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_panel_padding_slab",
                            TempAVPBlocks.DYE_COLOR_TO_PANEL_PADDING_SLAB.get(dyeColor)
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_PANEL_PADDING_STAIRS =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_panel_padding_stairs",
                            TempAVPBlocks.DYE_COLOR_TO_PANEL_PADDING_STAIRS.get(dyeColor)
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_PIPE_PADDING =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_pipe_padding",
                            TempAVPBlocks.DYE_COLOR_TO_PIPE_PADDING.get(dyeColor)
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_PIPE_PADDING_SLAB =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_pipe_padding_slab",
                            TempAVPBlocks.DYE_COLOR_TO_PIPE_PADDING_SLAB.get(dyeColor)
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_PIPE_PADDING_STAIRS =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_pipe_padding_stairs",
                            TempAVPBlocks.DYE_COLOR_TO_PIPE_PADDING_STAIRS.get(dyeColor)
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_CUT_PLASTIC =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_cut_plastic",
                            TempAVPBlocks.DYE_COLOR_TO_CUT_PLASTIC.get(dyeColor)
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
                        dyeColor -> register(
                            dyeColor.getName() + "_cut_plastic_slab",
                            TempAVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_SLAB.get(dyeColor)
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
                        dyeColor -> register(
                            dyeColor.getName() + "_cut_plastic_stairs",
                            TempAVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_STAIRS.get(dyeColor)
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
                        dyeColor -> register(
                            dyeColor.getName() + "_plastic",
                            TempAVPBlocks.DYE_COLOR_TO_PLASTIC.get(dyeColor)
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
                        dyeColor -> register(
                            dyeColor.getName() + "_plastic_slab",
                            TempAVPBlocks.DYE_COLOR_TO_PLASTIC_SLAB.get(dyeColor)
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
                        dyeColor -> register(
                            dyeColor.getName() + "_plastic_stairs",
                            TempAVPBlocks.DYE_COLOR_TO_PLASTIC_STAIRS.get(dyeColor)
                        )
                    )
                )
        );

    private static Supplier<BlockItem> register(String id, Supplier<Block> blockSupplier) {
        return Services.REGISTRY.register(BuiltInRegistries.ITEM, id, () -> new BlockItem(blockSupplier.get(), new Item.Properties()));
    }

    public static void initialize() {}
}
