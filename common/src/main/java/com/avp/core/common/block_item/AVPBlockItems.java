package com.avp.core.common.block_item;

import com.avp.core.platform.service.Services;
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

import com.avp.core.common.block.AVPBlocks;

public class AVPBlockItems {

    public static final Supplier<BlockItem> ALUMINUM_BLOCK = register("aluminum_block", AVPBlocks.ALUMINUM_BLOCK);

    public static final Supplier<BlockItem> AUTUNITE_BLOCK = register("autunite_block", AVPBlocks.AUTUNITE_BLOCK);

    public static final Supplier<BlockItem> AUTUNITE_ORE = register("autunite_ore", AVPBlocks.AUTUNITE_ORE);

    public static final Supplier<BlockItem> BAUXITE_ORE = register("bauxite_ore", AVPBlocks.BAUXITE_ORE);

    public static final Supplier<BlockItem> BRASS_BLOCK = register("brass_block", AVPBlocks.BRASS_BLOCK);

    public static final Supplier<BlockItem> CHISELED_FERROALUMINUM = register("chiseled_ferroaluminum", AVPBlocks.CHISELED_FERROALUMINUM);

    public static final Supplier<BlockItem> CHISELED_STEEL = register("chiseled_steel", AVPBlocks.CHISELED_STEEL);

    public static final Supplier<BlockItem> CHISELED_TITANIUM = register("chiseled_titanium", AVPBlocks.CHISELED_TITANIUM);

    public static final Supplier<BlockItem> CUT_FERROALUMINUM = register("cut_ferroaluminum", AVPBlocks.CUT_FERROALUMINUM);

    public static final Supplier<BlockItem> CUT_FERROALUMINUM_SLAB = register("cut_ferroaluminum_slab", AVPBlocks.CUT_FERROALUMINUM_SLAB);

    public static final Supplier<BlockItem> CUT_FERROALUMINUM_STAIRS = register("cut_ferroaluminum_stairs", AVPBlocks.CUT_FERROALUMINUM_STAIRS);

    public static final Supplier<BlockItem> CUT_STEEL = register("cut_steel", AVPBlocks.CUT_STEEL);

    public static final Supplier<BlockItem> CUT_STEEL_SLAB = register("cut_steel_slab", AVPBlocks.CUT_STEEL_SLAB);

    public static final Supplier<BlockItem> CUT_STEEL_STAIRS = register("cut_steel_stairs", AVPBlocks.CUT_STEEL_STAIRS);

    public static final Supplier<BlockItem> CUT_TITANIUM = register("cut_titanium", AVPBlocks.CUT_TITANIUM);

    public static final Supplier<BlockItem> CUT_TITANIUM_SLAB = register("cut_titanium_slab", AVPBlocks.CUT_TITANIUM_SLAB);

    public static final Supplier<BlockItem> CUT_TITANIUM_STAIRS = register("cut_titanium_stairs", AVPBlocks.CUT_TITANIUM_STAIRS);

    public static final Supplier<BlockItem> DEEPSLATE_TITANIUM_ORE = register("deepslate_titanium_ore", AVPBlocks.DEEPSLATE_TITANIUM_ORE);

    public static final Supplier<BlockItem> DEEPSLATE_ZINC_ORE = register("deepslate_zinc_ore", AVPBlocks.DEEPSLATE_ZINC_ORE);

    public static final Supplier<BlockItem> FERROALUMINUM_BLOCK = register("ferroaluminum_block", AVPBlocks.FERROALUMINUM_BLOCK);

    public static final Supplier<BlockItem> FERROALUMINUM_CHAIN_FENCE = register("ferroaluminum_chain_fence", AVPBlocks.FERROALUMINUM_CHAIN_FENCE);

    public static final Supplier<BlockItem> FERROALUMINUM_COLUMN = register("ferroaluminum_column", AVPBlocks.FERROALUMINUM_COLUMN);

    public static final Supplier<BlockItem> FERROALUMINUM_FASTENED_SIDING = register("ferroaluminum_fastened_siding", AVPBlocks.FERROALUMINUM_FASTENED_SIDING);

    public static final Supplier<BlockItem> FERROALUMINUM_FASTENED_STANDING = register("ferroaluminum_fastened_standing", AVPBlocks.FERROALUMINUM_FASTENED_STANDING);

    public static final Supplier<BlockItem> FERROALUMINUM_GRATE = register("ferroaluminum_grate", AVPBlocks.FERROALUMINUM_GRATE);

    public static final Supplier<BlockItem> FERROALUMINUM_PLATING = register("ferroaluminum_plating", AVPBlocks.FERROALUMINUM_PLATING);

    public static final Supplier<BlockItem> FERROALUMINUM_SIDING = register("ferroaluminum_siding", AVPBlocks.FERROALUMINUM_SIDING);

    public static final Supplier<BlockItem> FERROALUMINUM_STANDING = register("ferroaluminum_standing", AVPBlocks.FERROALUMINUM_STANDING);

    public static final Supplier<BlockItem> FERROALUMINUM_TREAD = register("ferroaluminum_tread", AVPBlocks.FERROALUMINUM_TREAD);

    public static final Supplier<BlockItem> GALENA_ORE = register("galena_ore", AVPBlocks.GALENA_ORE);

    public static final Supplier<BlockItem> INDUSTRIAL_GLASS = register("industrial_glass", AVPBlocks.INDUSTRIAL_GLASS);

    public static final Supplier<BlockItem> INDUSTRIAL_GLASS_PANE = register("industrial_glass_pane", AVPBlocks.INDUSTRIAL_GLASS_PANE);

    public static final Supplier<BlockItem> LEAD_BLOCK = register("lead_block", AVPBlocks.LEAD_BLOCK);

    public static final Supplier<BlockItem> LITHIUM_ORE = register("lithium_ore", AVPBlocks.LITHIUM_ORE);

    public static final Supplier<BlockItem> MONAZITE_ORE = register("monazite_ore", AVPBlocks.MONAZITE_ORE);

    public static final Supplier<BlockItem> NETHER_RESIN = register("nether_resin", new Item.Properties().fireResistant(), AVPBlocks.NETHER_RESIN);

    public static final Supplier<BlockItem> NETHER_RESIN_NODE = register("nether_resin_node", new Item.Properties().fireResistant(), AVPBlocks.NETHER_RESIN_NODE);

    public static final Supplier<BlockItem> NETHER_RESIN_VEIN = register("nether_resin_vein", new Item.Properties().fireResistant(), AVPBlocks.NETHER_RESIN_VEIN);

    public static final Supplier<BlockItem> NETHER_RESIN_WEB = register("nether_resin_web", new Item.Properties().fireResistant(), AVPBlocks.NETHER_RESIN_WEB);

    public static final Supplier<BlockItem> RAW_BAUXITE_BLOCK = register("raw_bauxite_block", AVPBlocks.RAW_BAUXITE_BLOCK);

    public static final Supplier<BlockItem> RAW_GALENA_BLOCK = register("raw_galena_block", AVPBlocks.RAW_GALENA_BLOCK);

    public static final Supplier<BlockItem> LITHIUM_BLOCK = register("lithium_block", AVPBlocks.LITHIUM_BLOCK);

    public static final Supplier<BlockItem> RAW_MONAZITE_BLOCK = register("raw_monazite_block", AVPBlocks.RAW_MONAZITE_BLOCK);

    public static final Supplier<BlockItem> RAW_SILICA_BLOCK = register("raw_silica_block", AVPBlocks.RAW_SILICA_BLOCK);

    public static final Supplier<BlockItem> RAW_TITANIUM_BLOCK = register("raw_titanium_block", AVPBlocks.RAW_TITANIUM_BLOCK);

    public static final Supplier<BlockItem> RAW_ZINC_BLOCK = register("raw_zinc_block", AVPBlocks.RAW_ZINC_BLOCK);

    public static final Supplier<BlockItem> RAZOR_WIRE = register("razor_wire", AVPBlocks.RAZOR_WIRE);

    public static final Supplier<BlockItem> RESIN = register("resin", AVPBlocks.RESIN);

    public static final Supplier<BlockItem> RESIN_NODE = register("resin_node", AVPBlocks.RESIN_NODE);

    public static final Supplier<BlockItem> RESIN_VEIN = register("resin_vein", AVPBlocks.RESIN_VEIN);

    public static final Supplier<BlockItem> RESIN_WEB = register("resin_web", AVPBlocks.RESIN_WEB);

    public static final Supplier<BlockItem> SILICA_GRAVEL = register("silica_gravel", AVPBlocks.SILICA_GRAVEL);

    public static final Supplier<BlockItem> STEEL_BARS = register("steel_bars", AVPBlocks.STEEL_BARS);

    public static final Supplier<BlockItem> STEEL_BLOCK = register("steel_block", AVPBlocks.STEEL_BLOCK);

    public static final Supplier<BlockItem> STEEL_CHAIN_FENCE = register("steel_chain_fence", AVPBlocks.STEEL_CHAIN_FENCE);

    public static final Supplier<BlockItem> STEEL_COLUMN = register("steel_column", AVPBlocks.STEEL_COLUMN);

    public static final Supplier<BlockItem> STEEL_FASTENED_SIDING = register("steel_fastened_siding", AVPBlocks.STEEL_FASTENED_SIDING);

    public static final Supplier<BlockItem> STEEL_FASTENED_STANDING = register("steel_fastened_standing", AVPBlocks.STEEL_FASTENED_STANDING);

    public static final Supplier<BlockItem> STEEL_GRATE = register("steel_grate", AVPBlocks.STEEL_GRATE);

    public static final Supplier<BlockItem> STEEL_PLATING = register("steel_plating", AVPBlocks.STEEL_PLATING);

    public static final Supplier<BlockItem> STEEL_SIDING = register("steel_siding", AVPBlocks.STEEL_SIDING);

    public static final Supplier<BlockItem> STEEL_STANDING = register("steel_standing", AVPBlocks.STEEL_STANDING);

    public static final Supplier<BlockItem> STEEL_TREAD = register("steel_tread", AVPBlocks.STEEL_TREAD);

    public static final Supplier<BlockItem> TITANIUM_BLOCK = register("titanium_block", AVPBlocks.TITANIUM_BLOCK);

    public static final Supplier<BlockItem> TITANIUM_CHAIN_FENCE = register("titanium_chain_fence", AVPBlocks.TITANIUM_CHAIN_FENCE);

    public static final Supplier<BlockItem> TITANIUM_COLUMN = register("titanium_column", AVPBlocks.TITANIUM_COLUMN);

    public static final Supplier<BlockItem> TITANIUM_FASTENED_SIDING = register("titanium_fastened_siding", AVPBlocks.TITANIUM_FASTENED_SIDING);

    public static final Supplier<BlockItem> TITANIUM_FASTENED_STANDING = register("titanium_fastened_standing", AVPBlocks.TITANIUM_FASTENED_STANDING);

    public static final Supplier<BlockItem> TITANIUM_GRATE = register("titanium_grate", AVPBlocks.TITANIUM_GRATE);

    public static final Supplier<BlockItem> TITANIUM_PLATING = register("titanium_plating", AVPBlocks.TITANIUM_PLATING);

    public static final Supplier<BlockItem> TITANIUM_SIDING = register("titanium_siding", AVPBlocks.TITANIUM_SIDING);

    public static final Supplier<BlockItem> TITANIUM_STANDING = register("titanium_standing", AVPBlocks.TITANIUM_STANDING);

    public static final Supplier<BlockItem> TITANIUM_TREAD = register("titanium_tread", AVPBlocks.TITANIUM_TREAD);

    public static final Supplier<BlockItem> URANIUM_BLOCK = register("uranium_block", AVPBlocks.URANIUM_BLOCK);

    public static final Supplier<BlockItem> ZINC_BLOCK = register("zinc_block", AVPBlocks.ZINC_BLOCK);

    public static final Supplier<BlockItem> ZINC_ORE = register("zinc_ore", AVPBlocks.ZINC_ORE);

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_CONCRETE_SLAB =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(dyeColor.getName() + "_concrete_slab", AVPBlocks.DYE_COLOR_TO_CONCRETE_SLAB.get(dyeColor))
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_CONCRETE_STAIRS =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(dyeColor.getName() + "_concrete_stairs", AVPBlocks.DYE_COLOR_TO_CONCRETE_STAIRS.get(dyeColor))
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_INDUSTRIAL_CONCRETE =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(dyeColor.getName() + "_industrial_concrete", AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE.get(dyeColor))
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_INDUSTRIAL_CONCRETE_SLAB =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(dyeColor.getName() + "_industrial_concrete_slab", AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_SLAB.get(dyeColor))
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_INDUSTRIAL_CONCRETE_STAIRS =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(dyeColor.getName() + "_industrial_concrete_stairs", AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_STAIRS.get(dyeColor))
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_INDUSTRIAL_CONCRETE_WALL =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(dyeColor.getName() + "_industrial_concrete_wall", AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_WALL.get(dyeColor))
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_INDUSTRIAL_GLASS =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(dyeColor.getName() + "_industrial_glass", AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS.get(dyeColor))
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_INDUSTRIAL_GLASS_PANE =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(dyeColor.getName() + "_industrial_glass_pane", AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS_PANE.get(dyeColor))
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_PADDING =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(dyeColor.getName() + "_padding", AVPBlocks.DYE_COLOR_TO_PADDING.get(dyeColor))
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_PADDING_SLAB =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(dyeColor.getName() + "_padding_slab", AVPBlocks.DYE_COLOR_TO_PADDING_SLAB.get(dyeColor))
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_PADDING_STAIRS =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(dyeColor.getName() + "_padding_stairs", AVPBlocks.DYE_COLOR_TO_PADDING_STAIRS.get(dyeColor))
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_PANEL_PADDING =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(dyeColor.getName() + "_panel_padding", AVPBlocks.DYE_COLOR_TO_PANEL_PADDING.get(dyeColor))
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_PANEL_PADDING_SLAB =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(dyeColor.getName() + "_panel_padding_slab", AVPBlocks.DYE_COLOR_TO_PANEL_PADDING_SLAB.get(dyeColor))
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_PANEL_PADDING_STAIRS =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(dyeColor.getName() + "_panel_padding_stairs", AVPBlocks.DYE_COLOR_TO_PANEL_PADDING_STAIRS.get(dyeColor))
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_PIPE_PADDING =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(dyeColor.getName() + "_pipe_padding", AVPBlocks.DYE_COLOR_TO_PIPE_PADDING.get(dyeColor))
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_PIPE_PADDING_SLAB =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(dyeColor.getName() + "_pipe_padding_slab", AVPBlocks.DYE_COLOR_TO_PIPE_PADDING_SLAB.get(dyeColor))
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_PIPE_PADDING_STAIRS =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(dyeColor.getName() + "_pipe_padding_stairs", AVPBlocks.DYE_COLOR_TO_PIPE_PADDING_STAIRS.get(dyeColor))
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_CUT_PLASTIC =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(dyeColor.getName() + "_cut_plastic", AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC.get(dyeColor))
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_CUT_PLASTIC_SLAB =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(dyeColor.getName() + "_cut_plastic_slab", AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_SLAB.get(dyeColor))
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_CUT_PLASTIC_STAIRS =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(dyeColor.getName() + "_cut_plastic_stairs", AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_STAIRS.get(dyeColor))
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_PLASTIC =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(dyeColor.getName() + "_plastic", AVPBlocks.DYE_COLOR_TO_PLASTIC.get(dyeColor))
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_PLASTIC_SLAB =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(dyeColor.getName() + "_plastic_slab", AVPBlocks.DYE_COLOR_TO_PLASTIC_SLAB.get(dyeColor))
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_PLASTIC_STAIRS =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(dyeColor.getName() + "_plastic_stairs", AVPBlocks.DYE_COLOR_TO_PLASTIC_STAIRS.get(dyeColor))
                    )
                )
        );

    public static Supplier<BlockItem> register(String name, Supplier<Block> blockSupplier) {
        return register(name, new Item.Properties(), blockSupplier);
    }

    public static Supplier<BlockItem> register(String name, Item.Properties properties, Supplier<Block> blockSupplier) {
        return Services.REGISTRY.registerItem(name, () -> new BlockItem(blockSupplier.get(), properties));
    }

    public static void initialize() {}
}
