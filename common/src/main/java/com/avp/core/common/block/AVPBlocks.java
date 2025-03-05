package com.avp.core.common.block;

import com.avp.core.common.block.impl.CustomTransparentBlock;
import com.avp.core.common.block.impl.LithiumBlock;
import com.avp.core.common.block.impl.RazorWireBlock;
import com.avp.core.common.block.impl.ResinBlock;
import com.avp.core.common.block.impl.ResinNodeBlock;
import com.avp.core.common.block.impl.ResinWebBlock;
import com.avp.core.common.block.resin.ResinVeinBlock;
import com.avp.core.platform.service.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ColorRGBA;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ColoredFallingBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.WaterloggedTransparentBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class AVPBlocks {

    public static final Supplier<Block> ALUMINUM_BLOCK = register(BlockProperties.ALUMINUM, "aluminum_block");

    public static final Supplier<Block> AUTUNITE_BLOCK = register(BlockProperties.AUTUNITE_ORE, "autunite_block");

    public static final Supplier<Block> AUTUNITE_ORE = register(BlockProperties.AUTUNITE_ORE, "autunite_ore");

    public static final Supplier<Block> BAUXITE_ORE = register(BlockProperties.BAUXITE_ORE, "bauxite_ore");

    public static final Supplier<Block> BRASS_BLOCK = register(BlockProperties.BRASS, "brass_block");

    public static final Supplier<Block> CHISELED_FERROALUMINUM = register(BlockProperties.FERROALUMINUM, "chiseled_ferroaluminum");

    public static final Supplier<Block> CHISELED_STEEL = register(BlockProperties.STEEL, "chiseled_steel");

    public static final Supplier<Block> CHISELED_TITANIUM = register(BlockProperties.TITANIUM, "chiseled_titanium");

    public static final Supplier<Block> CUT_FERROALUMINUM = register(BlockProperties.FERROALUMINUM, "cut_ferroaluminum");

    public static final Supplier<Block> CUT_FERROALUMINUM_SLAB = register(() -> new SlabBlock(BlockProperties.FERROALUMINUM), "cut_ferroaluminum_slab");

    public static final Supplier<Block> CUT_FERROALUMINUM_STAIRS = register(
        () -> new StairBlock(
            CUT_FERROALUMINUM.get().defaultBlockState(),
            BlockProperties.FERROALUMINUM
        ),
        "cut_ferroaluminum_stairs"
    );

    public static final Supplier<Block> CUT_STEEL = register(BlockProperties.STEEL, "cut_steel");

    public static final Supplier<Block> CUT_STEEL_SLAB = register(() -> new SlabBlock(BlockProperties.STEEL), "cut_steel_slab");

    public static final Supplier<Block> CUT_STEEL_STAIRS = register(
        () -> new StairBlock(
            CUT_STEEL.get().defaultBlockState(),
            BlockProperties.STEEL
        ),
        "cut_steel_stairs"
    );

    public static final Supplier<Block> CUT_TITANIUM = register(BlockProperties.TITANIUM, "cut_titanium");

    public static final Supplier<Block> CUT_TITANIUM_SLAB = register(() -> new SlabBlock(BlockProperties.TITANIUM), "cut_titanium_slab");

    public static final Supplier<Block> CUT_TITANIUM_STAIRS = register(
        () -> new StairBlock(
            CUT_TITANIUM.get().defaultBlockState(),
            BlockProperties.TITANIUM
        ),
        "cut_titanium_stairs"
    );

    public static final Supplier<Block> DEEPSLATE_TITANIUM_ORE = register(BlockProperties.DEEPSLATE_TITANIUM_ORE, "deepslate_titanium_ore");

    public static final Supplier<Block> DEEPSLATE_ZINC_ORE = register(BlockProperties.DEEPSLATE_ZINC_ORE, "deepslate_zinc_ore");

    public static final Supplier<Block> FERROALUMINUM_BLOCK = register(BlockProperties.FERROALUMINUM, "ferroaluminum_block");

    public static final Supplier<Block> FERROALUMINUM_CHAIN_FENCE = register(
        // FIXME:
        () -> new IronBarsBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BARS).sound(SoundType.CHAIN)),
        "ferroaluminum_chain_fence"
    );

    public static final Supplier<Block> FERROALUMINUM_COLUMN = register(
        () -> new RotatedPillarBlock(BlockProperties.FERROALUMINUM),
        "ferroaluminum_column"
    );

    public static final Supplier<Block> FERROALUMINUM_FASTENED_SIDING = register(BlockProperties.FERROALUMINUM, "ferroaluminum_fastened_siding");

    public static final Supplier<Block> FERROALUMINUM_FASTENED_STANDING = register(
        BlockProperties.FERROALUMINUM,
        "ferroaluminum_fastened_standing"
    );

    public static final Supplier<Block> FERROALUMINUM_GRATE = register(
        () -> new WaterloggedTransparentBlock(BlockProperties.FERROALUMINUM_GRATE),
        "ferroaluminum_grate"
    );

    public static final Supplier<Block> FERROALUMINUM_PLATING = register(BlockProperties.FERROALUMINUM, "ferroaluminum_plating");

    public static final Supplier<Block> FERROALUMINUM_SIDING = register(BlockProperties.FERROALUMINUM, "ferroaluminum_siding");

    public static final Supplier<Block> FERROALUMINUM_STANDING = register(BlockProperties.FERROALUMINUM, "ferroaluminum_standing");

    public static final Supplier<Block> FERROALUMINUM_TREAD = register(BlockProperties.FERROALUMINUM, "ferroaluminum_tread");

    public static final Supplier<Block> GALENA_ORE = register(BlockProperties.GALENA_ORE, "galena_ore");

    public static final Supplier<Block> INDUSTRIAL_GLASS = register(() -> new TransparentBlock(BlockProperties.INDUSTRIAL_GLASS), "industrial_glass");

    public static final Supplier<Block> INDUSTRIAL_GLASS_PANE = register(
        () -> new IronBarsBlock(BlockProperties.INDUSTRIAL_GLASS_PANE),
        "industrial_glass_pane"
    );

    public static final Supplier<Block> LEAD_BLOCK = register(BlockProperties.LEAD, "lead_block");

    public static final Supplier<Block> LITHIUM_BLOCK = register(() -> new LithiumBlock(BlockProperties.LITHIUM_ORE), "lithium_block");

    public static final Supplier<Block> LITHIUM_ORE = register(() -> new LithiumBlock(BlockProperties.LITHIUM_ORE), "lithium_ore");

    public static final Supplier<Block> MONAZITE_ORE = register(BlockProperties.MONAZITE_ORE, "monazite_ore");

    public static final Supplier<Block> NETHER_RESIN = register(() -> new ResinBlock(BlockProperties.NETHER_RESIN), "nether_resin");

    public static final Supplier<Block> NETHER_RESIN_NODE = register(() -> new ResinNodeBlock(BlockProperties.RESIN), "nether_resin_node");

    public static final Supplier<Block> NETHER_RESIN_VEIN = register(() -> new ResinVeinBlock(BlockProperties.NETHER_RESIN_VEIN), "nether_resin_vein");

    public static final Supplier<Block> NETHER_RESIN_WEB = register(() -> new ResinWebBlock(BlockProperties.NETHER_RESIN_WEB), "nether_resin_web");

    public static final Supplier<Block> RAW_BAUXITE_BLOCK = register(BlockProperties.BAUXITE_ORE, "raw_bauxite_block");

    public static final Supplier<Block> RAW_GALENA_BLOCK = register(BlockProperties.GALENA_ORE, "raw_galena_block");

    public static final Supplier<Block> RAW_MONAZITE_BLOCK = register(BlockProperties.MONAZITE_ORE, "raw_monazite_block");

    public static final Supplier<Block> RAW_SILICA_BLOCK = register(
        BlockBehaviour.Properties.ofFullCopy(Blocks.GRAVEL).strength(0.9F),
        "raw_silica_block"
    );

    public static final Supplier<Block> RAW_TITANIUM_BLOCK = register(BlockProperties.TITANIUM_ORE, "raw_titanium_block");

    public static final Supplier<Block> RAW_ZINC_BLOCK = register(BlockProperties.ZINC_ORE, "raw_zinc_block");

    public static final Supplier<Block> RAZOR_WIRE = register(() -> new RazorWireBlock(BlockProperties.RAZOR_WIRE), "razor_wire");

    public static final Supplier<Block> RESIN = register(() -> new ResinBlock(BlockProperties.RESIN), "resin");

    public static final Supplier<Block> RESIN_NODE = register(() -> new ResinNodeBlock(BlockProperties.RESIN), "resin_node");

    public static final Supplier<Block> RESIN_VEIN = register(() -> new ResinVeinBlock(BlockProperties.RESIN_VEIN), "resin_vein");

    public static final Supplier<Block> RESIN_WEB = register(() -> new ResinWebBlock(BlockProperties.RESIN_WEB), "resin_web");

    public static final Supplier<Block> SILICA_GRAVEL = register(
        () -> new ColoredFallingBlock(new ColorRGBA(-8356741), BlockBehaviour.Properties.ofFullCopy(Blocks.GRAVEL)),
        "silica_gravel"
    );

    public static final Supplier<Block> STEEL_BARS = register(
        // FIXME:
        () -> new IronBarsBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BARS)),
        "steel_bars"
    );

    public static final Supplier<Block> STEEL_BLOCK = register(BlockProperties.STEEL, "steel_block");

    public static final Supplier<Block> STEEL_CHAIN_FENCE = register(
        // FIXME:
        () -> new IronBarsBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BARS).sound(SoundType.CHAIN)),
        "steel_chain_fence"
    );

    public static final Supplier<Block> STEEL_COLUMN = register(() -> new RotatedPillarBlock(BlockProperties.STEEL), "steel_column");

    public static final Supplier<Block> STEEL_FASTENED_SIDING = register(BlockProperties.STEEL, "steel_fastened_siding");

    public static final Supplier<Block> STEEL_FASTENED_STANDING = register(BlockProperties.STEEL, "steel_fastened_standing");

    public static final Supplier<Block> STEEL_GRATE = register(() -> new WaterloggedTransparentBlock(BlockProperties.STEEL_GRATE), "steel_grate");

    public static final Supplier<Block> STEEL_PLATING = register(BlockProperties.STEEL, "steel_plating");

    public static final Supplier<Block> STEEL_SIDING = register(BlockProperties.STEEL, "steel_siding");

    public static final Supplier<Block> STEEL_STANDING = register(BlockProperties.STEEL, "steel_standing");

    public static final Supplier<Block> STEEL_TREAD = register(BlockProperties.STEEL, "steel_tread");

    public static final Supplier<Block> TITANIUM_BLOCK = register(BlockProperties.TITANIUM, "titanium_block");

    public static final Supplier<Block> TITANIUM_CHAIN_FENCE = register(
        // FIXME:
        () -> new IronBarsBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BARS).sound(SoundType.CHAIN)),
        "titanium_chain_fence"
    );

    public static final Supplier<Block> TITANIUM_COLUMN = register(() -> new RotatedPillarBlock(BlockProperties.TITANIUM), "titanium_column");

    public static final Supplier<Block> TITANIUM_FASTENED_SIDING = register(BlockProperties.TITANIUM, "titanium_fastened_siding");

    public static final Supplier<Block> TITANIUM_FASTENED_STANDING = register(BlockProperties.TITANIUM, "titanium_fastened_standing");

    public static final Supplier<Block> TITANIUM_GRATE = register(
        () -> new WaterloggedTransparentBlock(BlockProperties.TITANIUM_GRATE),
        "titanium_grate"
    );

    public static final Supplier<Block> TITANIUM_PLATING = register(BlockProperties.TITANIUM, "titanium_plating");

    public static final Supplier<Block> TITANIUM_SIDING = register(BlockProperties.TITANIUM, "titanium_siding");

    public static final Supplier<Block> TITANIUM_STANDING = register(BlockProperties.TITANIUM, "titanium_standing");

    public static final Supplier<Block> TITANIUM_TREAD = register(BlockProperties.TITANIUM, "titanium_tread");

    public static final Supplier<Block> URANIUM_BLOCK = register(BlockProperties.URANIUM, "uranium_block");

    public static final Supplier<Block> ZINC_BLOCK = register(BlockProperties.ZINC, "zinc_block");

    public static final Supplier<Block> ZINC_ORE = register(BlockProperties.ZINC_ORE, "zinc_ore");

    public static final Map<DyeColor, Supplier<Block>> DYE_COLOR_TO_CONCRETE_SLAB =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            () -> new SlabBlock(BlockProperties.DYE_COLOR_TO_CONCRETE_PROPERTIES.get(dyeColor)),
                            dyeColor.getName() + "_concrete_slab"
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
                            () -> new StairBlock(
                                BlockProperties.DYE_COLOR_TO_CONCRETE_BLOCKS.get(dyeColor).defaultBlockState(),
                                BlockProperties.DYE_COLOR_TO_CONCRETE_PROPERTIES.get(dyeColor)
                            ),
                            dyeColor.getName() + "_concrete_stairs"
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
                            () -> new Block(BlockProperties.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_PROPERTIES.get(dyeColor)),
                            dyeColor.getName() + "_industrial_concrete"
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
                            () -> new SlabBlock(BlockProperties.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_PROPERTIES.get(dyeColor)),
                            dyeColor.getName() + "_industrial_concrete_slab"
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
                            () -> new StairBlock(
                                DYE_COLOR_TO_INDUSTRIAL_CONCRETE.get(dyeColor).get().defaultBlockState(),
                                BlockProperties.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_PROPERTIES.get(dyeColor)
                            ),
                            dyeColor.getName() + "_industrial_concrete_stairs"
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
                            () -> new WallBlock(BlockProperties.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_PROPERTIES.get(dyeColor)),
                            dyeColor.getName() + "_industrial_concrete_wall"
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
                            () -> new CustomTransparentBlock(BlockProperties.DYE_COLOR_TO_INDUSTRIAL_GLASS_PROPERTIES.get(dyeColor)),
                            dyeColor.getName() + "_industrial_glass"
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
                            () -> new IronBarsBlock(BlockProperties.DYE_COLOR_TO_INDUSTRIAL_GLASS_PANE_PROPERTIES.get(dyeColor)),
                            dyeColor.getName() + "_industrial_glass_pane"
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
                            () -> new Block(BlockProperties.DYE_COLOR_TO_PADDING_PROPERTIES.get(dyeColor)),
                            dyeColor.getName() + "_padding"
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
                            () -> new SlabBlock(BlockProperties.DYE_COLOR_TO_PADDING_PROPERTIES.get(dyeColor)),
                            dyeColor.getName() + "_padding_slab"
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
                            () -> new StairBlock(
                                DYE_COLOR_TO_PADDING.get(dyeColor).get().defaultBlockState(),
                                BlockProperties.DYE_COLOR_TO_PADDING_PROPERTIES.get(dyeColor)
                            ),
                            dyeColor.getName() + "_padding_stairs"
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
                            () -> new Block(BlockProperties.DYE_COLOR_TO_PADDING_PROPERTIES.get(dyeColor)),
                            dyeColor.getName() + "_panel_padding"
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
                            () -> new SlabBlock(BlockProperties.DYE_COLOR_TO_PADDING_PROPERTIES.get(dyeColor)),
                            dyeColor.getName() + "_panel_padding_slab"
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
                            () -> new StairBlock(
                                DYE_COLOR_TO_PANEL_PADDING.get(dyeColor).get().defaultBlockState(),
                                BlockProperties.DYE_COLOR_TO_PADDING_PROPERTIES.get(dyeColor)
                            ),
                            dyeColor.getName() + "_panel_padding_stairs"
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
                            () -> new Block(BlockProperties.DYE_COLOR_TO_PADDING_PROPERTIES.get(dyeColor)),
                            dyeColor.getName() + "_pipe_padding"
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
                            () -> new SlabBlock(BlockProperties.DYE_COLOR_TO_PADDING_PROPERTIES.get(dyeColor)),
                            dyeColor.getName() + "_pipe_padding_slab"
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
                            () -> new StairBlock(
                                DYE_COLOR_TO_PIPE_PADDING.get(dyeColor).get().defaultBlockState(),
                                BlockProperties.DYE_COLOR_TO_PADDING_PROPERTIES.get(dyeColor)
                            ),
                            dyeColor.getName() + "_pipe_padding_stairs"
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
                            () -> new Block(BlockProperties.DYE_COLOR_TO_PLASTIC_PROPERTIES.get(dyeColor)),
                            dyeColor.getName() + "_cut_plastic"
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
                            () -> new SlabBlock(BlockProperties.DYE_COLOR_TO_PLASTIC_PROPERTIES.get(dyeColor)),
                            dyeColor.getName() + "_cut_plastic_slab"
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
                            () -> new StairBlock(
                                DYE_COLOR_TO_CUT_PLASTIC.get(dyeColor).get().defaultBlockState(),
                                BlockProperties.DYE_COLOR_TO_PLASTIC_PROPERTIES.get(dyeColor)
                            ),
                            dyeColor.getName() + "_cut_plastic_stairs"
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
                            () -> new Block(BlockProperties.DYE_COLOR_TO_PLASTIC_PROPERTIES.get(dyeColor)),
                            dyeColor.getName() + "_plastic"
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
                            () -> new SlabBlock(BlockProperties.DYE_COLOR_TO_PLASTIC_PROPERTIES.get(dyeColor)),
                            dyeColor.getName() + "_plastic_slab"
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
                            () -> new StairBlock(
                                DYE_COLOR_TO_PLASTIC.get(dyeColor).get().defaultBlockState(),
                                BlockProperties.DYE_COLOR_TO_PLASTIC_PROPERTIES.get(dyeColor)
                            ),
                            dyeColor.getName() + "_plastic_stairs"
                        )
                    )
                )
        );

    public static Supplier<Block> register(BlockBehaviour.Properties properties, String id) {
        return register(() -> new Block(properties), id);
    }

    public static Supplier<Block> register(Supplier<Block> blockSupplier, String id) {
        return Services.REGISTRY.register(BuiltInRegistries.BLOCK, id, blockSupplier);
    }

    public static void initialize() {}
}
