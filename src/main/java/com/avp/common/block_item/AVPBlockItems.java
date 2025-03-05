package com.avp.common.block_item;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.avp.AVPResources;
import com.avp.common.block.AVPBlocks;

public class AVPBlockItems {

    public static final BlockItem ALUMINUM_BLOCK = register(AVPBlocks.ALUMINUM_BLOCK);

    public static final BlockItem AUTUNITE_BLOCK = register(AVPBlocks.AUTUNITE_BLOCK);

    public static final BlockItem AUTUNITE_ORE = register(AVPBlocks.AUTUNITE_ORE);

    public static final BlockItem BAUXITE_ORE = register(AVPBlocks.BAUXITE_ORE);

    public static final BlockItem BRASS_BLOCK = register(AVPBlocks.BRASS_BLOCK);

    public static final BlockItem CHISELED_FERROALUMINUM = register(AVPBlocks.CHISELED_FERROALUMINUM);

    public static final BlockItem CHISELED_STEEL = register(AVPBlocks.CHISELED_STEEL);

    public static final BlockItem CHISELED_TITANIUM = register(AVPBlocks.CHISELED_TITANIUM);

    public static final BlockItem CUT_FERROALUMINUM = register(AVPBlocks.CUT_FERROALUMINUM);

    public static final BlockItem CUT_FERROALUMINUM_SLAB = register(AVPBlocks.CUT_FERROALUMINUM_SLAB);

    public static final BlockItem CUT_FERROALUMINUM_STAIRS = register(AVPBlocks.CUT_FERROALUMINUM_STAIRS);

    public static final BlockItem CUT_STEEL = register(AVPBlocks.CUT_STEEL);

    public static final BlockItem CUT_STEEL_SLAB = register(AVPBlocks.CUT_STEEL_SLAB);

    public static final BlockItem CUT_STEEL_STAIRS = register(AVPBlocks.CUT_STEEL_STAIRS);

    public static final BlockItem CUT_TITANIUM = register(AVPBlocks.CUT_TITANIUM);

    public static final BlockItem CUT_TITANIUM_SLAB = register(AVPBlocks.CUT_TITANIUM_SLAB);

    public static final BlockItem CUT_TITANIUM_STAIRS = register(AVPBlocks.CUT_TITANIUM_STAIRS);

    public static final BlockItem DEEPSLATE_TITANIUM_ORE = register(AVPBlocks.DEEPSLATE_TITANIUM_ORE);

    public static final BlockItem DEEPSLATE_ZINC_ORE = register(AVPBlocks.DEEPSLATE_ZINC_ORE);

    public static final BlockItem FERROALUMINUM_BLOCK = register(AVPBlocks.FERROALUMINUM_BLOCK);

    public static final BlockItem FERROALUMINUM_CHAIN_FENCE = register(AVPBlocks.FERROALUMINUM_CHAIN_FENCE);

    public static final BlockItem FERROALUMINUM_COLUMN = register(AVPBlocks.FERROALUMINUM_COLUMN);

    public static final BlockItem FERROALUMINUM_FASTENED_SIDING = register(AVPBlocks.FERROALUMINUM_FASTENED_SIDING);

    public static final BlockItem FERROALUMINUM_FASTENED_STANDING = register(AVPBlocks.FERROALUMINUM_FASTENED_STANDING);

    public static final BlockItem FERROALUMINUM_GRATE = register(AVPBlocks.FERROALUMINUM_GRATE);

    public static final BlockItem FERROALUMINUM_PLATING = register(AVPBlocks.FERROALUMINUM_PLATING);

    public static final BlockItem FERROALUMINUM_SIDING = register(AVPBlocks.FERROALUMINUM_SIDING);

    public static final BlockItem FERROALUMINUM_STANDING = register(AVPBlocks.FERROALUMINUM_STANDING);

    public static final BlockItem FERROALUMINUM_TREAD = register(AVPBlocks.FERROALUMINUM_TREAD);

    public static final BlockItem GALENA_ORE = register(AVPBlocks.GALENA_ORE);

    public static final BlockItem INDUSTRIAL_GLASS = register(AVPBlocks.INDUSTRIAL_GLASS);

    public static final BlockItem INDUSTRIAL_GLASS_PANE = register(AVPBlocks.INDUSTRIAL_GLASS_PANE);

    public static final BlockItem LEAD_BLOCK = register(AVPBlocks.LEAD_BLOCK);

    public static final BlockItem LITHIUM_ORE = register(AVPBlocks.LITHIUM_ORE);

    public static final BlockItem MONAZITE_ORE = register(AVPBlocks.MONAZITE_ORE);

    public static final BlockItem NETHER_RESIN = register(new Item.Properties().fireResistant(), AVPBlocks.NETHER_RESIN);

    public static final BlockItem NETHER_RESIN_NODE = register(new Item.Properties().fireResistant(), AVPBlocks.NETHER_RESIN_NODE);

    public static final BlockItem NETHER_RESIN_VEIN = register(new Item.Properties().fireResistant(), AVPBlocks.NETHER_RESIN_VEIN);

    public static final BlockItem NETHER_RESIN_WEB = register(new Item.Properties().fireResistant(), AVPBlocks.NETHER_RESIN_WEB);

    public static final BlockItem RAW_BAUXITE_BLOCK = register(AVPBlocks.RAW_BAUXITE_BLOCK);

    public static final BlockItem RAW_GALENA_BLOCK = register(AVPBlocks.RAW_GALENA_BLOCK);

    public static final BlockItem LITHIUM_BLOCK = register(AVPBlocks.LITHIUM_BLOCK);

    public static final BlockItem RAW_MONAZITE_BLOCK = register(AVPBlocks.RAW_MONAZITE_BLOCK);

    public static final BlockItem RAW_SILICA_BLOCK = register(AVPBlocks.RAW_SILICA_BLOCK);

    public static final BlockItem RAW_TITANIUM_BLOCK = register(AVPBlocks.RAW_TITANIUM_BLOCK);

    public static final BlockItem RAW_ZINC_BLOCK = register(AVPBlocks.RAW_ZINC_BLOCK);

    public static final BlockItem RAZOR_WIRE = register(AVPBlocks.RAZOR_WIRE);

    public static final BlockItem RESIN = register(AVPBlocks.RESIN);

    public static final BlockItem RESIN_NODE = register(AVPBlocks.RESIN_NODE);

    public static final BlockItem RESIN_VEIN = register(AVPBlocks.RESIN_VEIN);

    public static final BlockItem RESIN_WEB = register(AVPBlocks.RESIN_WEB);

    public static final BlockItem SILICA_GRAVEL = register(AVPBlocks.SILICA_GRAVEL);

    public static final BlockItem STEEL_BARS = register(AVPBlocks.STEEL_BARS);

    public static final BlockItem STEEL_BLOCK = register(AVPBlocks.STEEL_BLOCK);

    public static final BlockItem STEEL_CHAIN_FENCE = register(AVPBlocks.STEEL_CHAIN_FENCE);

    public static final BlockItem STEEL_COLUMN = register(AVPBlocks.STEEL_COLUMN);

    public static final BlockItem STEEL_FASTENED_SIDING = register(AVPBlocks.STEEL_FASTENED_SIDING);

    public static final BlockItem STEEL_FASTENED_STANDING = register(AVPBlocks.STEEL_FASTENED_STANDING);

    public static final BlockItem STEEL_GRATE = register(AVPBlocks.STEEL_GRATE);

    public static final BlockItem STEEL_PLATING = register(AVPBlocks.STEEL_PLATING);

    public static final BlockItem STEEL_SIDING = register(AVPBlocks.STEEL_SIDING);

    public static final BlockItem STEEL_STANDING = register(AVPBlocks.STEEL_STANDING);

    public static final BlockItem STEEL_TREAD = register(AVPBlocks.STEEL_TREAD);

    public static final BlockItem TITANIUM_BLOCK = register(AVPBlocks.TITANIUM_BLOCK);

    public static final BlockItem TITANIUM_CHAIN_FENCE = register(AVPBlocks.TITANIUM_CHAIN_FENCE);

    public static final BlockItem TITANIUM_COLUMN = register(AVPBlocks.TITANIUM_COLUMN);

    public static final BlockItem TITANIUM_FASTENED_SIDING = register(AVPBlocks.TITANIUM_FASTENED_SIDING);

    public static final BlockItem TITANIUM_FASTENED_STANDING = register(AVPBlocks.TITANIUM_FASTENED_STANDING);

    public static final BlockItem TITANIUM_GRATE = register(AVPBlocks.TITANIUM_GRATE);

    public static final BlockItem TITANIUM_PLATING = register(AVPBlocks.TITANIUM_PLATING);

    public static final BlockItem TITANIUM_SIDING = register(AVPBlocks.TITANIUM_SIDING);

    public static final BlockItem TITANIUM_STANDING = register(AVPBlocks.TITANIUM_STANDING);

    public static final BlockItem TITANIUM_TREAD = register(AVPBlocks.TITANIUM_TREAD);

    public static final BlockItem URANIUM_BLOCK = register(AVPBlocks.URANIUM_BLOCK);

    public static final BlockItem ZINC_BLOCK = register(AVPBlocks.ZINC_BLOCK);

    public static final BlockItem ZINC_ORE = register(AVPBlocks.ZINC_ORE);

    public static final Map<DyeColor, BlockItem> DYE_COLOR_TO_CONCRETE_SLAB =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(AVPBlocks.DYE_COLOR_TO_CONCRETE_SLAB.get(dyeColor))
                    )
                )
        );

    public static final Map<DyeColor, BlockItem> DYE_COLOR_TO_CONCRETE_STAIRS =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(AVPBlocks.DYE_COLOR_TO_CONCRETE_STAIRS.get(dyeColor))
                    )
                )
        );

    public static final Map<DyeColor, BlockItem> DYE_COLOR_TO_INDUSTRIAL_CONCRETE =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE.get(dyeColor))
                    )
                )
        );

    public static final Map<DyeColor, BlockItem> DYE_COLOR_TO_INDUSTRIAL_CONCRETE_SLAB =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_SLAB.get(dyeColor))
                    )
                )
        );

    public static final Map<DyeColor, BlockItem> DYE_COLOR_TO_INDUSTRIAL_CONCRETE_STAIRS =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_STAIRS.get(dyeColor))
                    )
                )
        );

    public static final Map<DyeColor, BlockItem> DYE_COLOR_TO_INDUSTRIAL_CONCRETE_WALL =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_WALL.get(dyeColor))
                    )
                )
        );

    public static final Map<DyeColor, BlockItem> DYE_COLOR_TO_INDUSTRIAL_GLASS =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS.get(dyeColor))
                    )
                )
        );

    public static final Map<DyeColor, BlockItem> DYE_COLOR_TO_INDUSTRIAL_GLASS_PANE =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS_PANE.get(dyeColor))
                    )
                )
        );

    public static final Map<DyeColor, BlockItem> DYE_COLOR_TO_PADDING =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(AVPBlocks.DYE_COLOR_TO_PADDING.get(dyeColor))
                    )
                )
        );

    public static final Map<DyeColor, BlockItem> DYE_COLOR_TO_PADDING_SLAB =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(AVPBlocks.DYE_COLOR_TO_PADDING_SLAB.get(dyeColor))
                    )
                )
        );

    public static final Map<DyeColor, BlockItem> DYE_COLOR_TO_PADDING_STAIRS =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(AVPBlocks.DYE_COLOR_TO_PADDING_STAIRS.get(dyeColor))
                    )
                )
        );

    public static final Map<DyeColor, BlockItem> DYE_COLOR_TO_PANEL_PADDING =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(AVPBlocks.DYE_COLOR_TO_PANEL_PADDING.get(dyeColor))
                    )
                )
        );

    public static final Map<DyeColor, BlockItem> DYE_COLOR_TO_PANEL_PADDING_SLAB =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(AVPBlocks.DYE_COLOR_TO_PANEL_PADDING_SLAB.get(dyeColor))
                    )
                )
        );

    public static final Map<DyeColor, BlockItem> DYE_COLOR_TO_PANEL_PADDING_STAIRS =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(AVPBlocks.DYE_COLOR_TO_PANEL_PADDING_STAIRS.get(dyeColor))
                    )
                )
        );

    public static final Map<DyeColor, BlockItem> DYE_COLOR_TO_PIPE_PADDING =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(AVPBlocks.DYE_COLOR_TO_PIPE_PADDING.get(dyeColor))
                    )
                )
        );

    public static final Map<DyeColor, BlockItem> DYE_COLOR_TO_PIPE_PADDING_SLAB =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(AVPBlocks.DYE_COLOR_TO_PIPE_PADDING_SLAB.get(dyeColor))
                    )
                )
        );

    public static final Map<DyeColor, BlockItem> DYE_COLOR_TO_PIPE_PADDING_STAIRS =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(AVPBlocks.DYE_COLOR_TO_PIPE_PADDING_STAIRS.get(dyeColor))
                    )
                )
        );

    public static final Map<DyeColor, BlockItem> DYE_COLOR_TO_CUT_PLASTIC =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC.get(dyeColor))
                    )
                )
        );

    public static final Map<DyeColor, BlockItem> DYE_COLOR_TO_CUT_PLASTIC_SLAB =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_SLAB.get(dyeColor))
                    )
                )
        );

    public static final Map<DyeColor, BlockItem> DYE_COLOR_TO_CUT_PLASTIC_STAIRS =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_STAIRS.get(dyeColor))
                    )
                )
        );

    public static final Map<DyeColor, BlockItem> DYE_COLOR_TO_PLASTIC =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(AVPBlocks.DYE_COLOR_TO_PLASTIC.get(dyeColor))
                    )
                )
        );

    public static final Map<DyeColor, BlockItem> DYE_COLOR_TO_PLASTIC_SLAB =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(AVPBlocks.DYE_COLOR_TO_PLASTIC_SLAB.get(dyeColor))
                    )
                )
        );

    public static final Map<DyeColor, BlockItem> DYE_COLOR_TO_PLASTIC_STAIRS =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(AVPBlocks.DYE_COLOR_TO_PLASTIC_STAIRS.get(dyeColor))
                    )
                )
        );

    public static BlockItem register(Block block) {
        return register(new Item.Properties(), block);
    }

    public static BlockItem register(Item.Properties properties, Block block) {
        return register(properties, block, BuiltInRegistries.BLOCK.getKey(block).getPath());
    }

    public static BlockItem register(Item.Properties properties, Block block, String id) {
        var resourceLocation = AVPResources.location(id);
        var blockItem = new BlockItem(block, properties);

        return Registry.register(BuiltInRegistries.ITEM, resourceLocation, blockItem);
    }

    public static void initialize() {}
}
