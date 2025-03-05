package com.avp.common.block;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.avp.common.entity.AVPEntityTypeTags;

public class BlockProperties {

    private static final Supplier<BlockBehaviour.Properties> INDUSTRIAL_GLASS_SUPPLIER = () -> BlockBehaviour.Properties.ofFullCopy(
        Blocks.GLASS
    )
        .requiresCorrectToolForDrops()
        .strength(5);

    public static final BlockBehaviour.Properties INDUSTRIAL_GLASS = INDUSTRIAL_GLASS_SUPPLIER.get();

    public static final Supplier<BlockBehaviour.Properties> INDUSTRIAL_GLASS_PANE_SUPPLIER = () -> INDUSTRIAL_GLASS_SUPPLIER.get()
        .instrument(NoteBlockInstrument.HAT);

    public static final BlockBehaviour.Properties INDUSTRIAL_GLASS_PANE = INDUSTRIAL_GLASS_PANE_SUPPLIER.get();

    private static final Supplier<BlockBehaviour.Properties> RESIN_PROPERTIES_SUPPLIER = () -> BlockBehaviour.Properties.of()
        .mapColor(MapColor.COLOR_BLACK)
        .requiresCorrectToolForDrops()
        .strength(4F)
        // FIXME:
        .sound(SoundType.HONEY_BLOCK);

    private static final Supplier<BlockBehaviour.Properties> NETHER_RESIN_BLOCK_PROPERTIES_SUPPLIER = () -> RESIN_PROPERTIES_SUPPLIER.get()
        .isValidSpawn(($1, $2, $3, entityType) -> entityType.is(AVPEntityTypeTags.NETHER_ALIENS));

    private static final Supplier<BlockBehaviour.Properties> RESIN_BLOCK_PROPERTIES_SUPPLIER = () -> RESIN_PROPERTIES_SUPPLIER.get()
        .isValidSpawn(($1, $2, $3, entityType) -> entityType.is(AVPEntityTypeTags.NORMAL_ALIENS));

    public static final BlockBehaviour.Properties NETHER_RESIN = NETHER_RESIN_BLOCK_PROPERTIES_SUPPLIER.get()
        .mapColor(MapColor.COLOR_RED);

    public static final BlockBehaviour.Properties NETHER_RESIN_VEIN = RESIN_PROPERTIES_SUPPLIER.get()
        .mapColor(MapColor.COLOR_RED)
        .noCollission()
        .noOcclusion()
        .pushReaction(PushReaction.DESTROY)
        .replaceable();

    public static final BlockBehaviour.Properties NETHER_RESIN_WEB = NETHER_RESIN_BLOCK_PROPERTIES_SUPPLIER.get()
        .mapColor(MapColor.COLOR_RED)
        .noCollission()
        .noOcclusion()
        .pushReaction(PushReaction.DESTROY);

    public static final BlockBehaviour.Properties RESIN = RESIN_BLOCK_PROPERTIES_SUPPLIER.get();

    public static final BlockBehaviour.Properties RESIN_VEIN = RESIN_PROPERTIES_SUPPLIER.get()
        .noCollission()
        .noOcclusion()
        .pushReaction(PushReaction.DESTROY)
        .replaceable();

    public static final BlockBehaviour.Properties RESIN_WEB = RESIN_BLOCK_PROPERTIES_SUPPLIER.get()
        .noCollission()
        .noOcclusion()
        .pushReaction(PushReaction.DESTROY);

    private static final Supplier<BlockBehaviour.Properties> DEEPSLATE_ORE_PROPERTIES_SUPPLIER = () -> BlockBehaviour.Properties.ofFullCopy(
        Blocks.DEEPSLATE
    )
        .mapColor(MapColor.DEEPSLATE)
        .requiresCorrectToolForDrops()
        .strength(4.5F, 3)
        .sound(SoundType.DEEPSLATE);

    private static final Supplier<BlockBehaviour.Properties> STONE_ORE_PROPERTIES_SUPPLIER = () -> BlockBehaviour.Properties.ofFullCopy(
        Blocks.STONE
    )
        .mapColor(MapColor.STONE)
        .requiresCorrectToolForDrops()
        .strength(3, 3)
        .sound(SoundType.STONE);

    public static final BlockBehaviour.Properties AUTUNITE_ORE = STONE_ORE_PROPERTIES_SUPPLIER.get()
        .lightLevel($ -> 7);

    public static final BlockBehaviour.Properties BAUXITE_ORE = STONE_ORE_PROPERTIES_SUPPLIER.get();

    public static final BlockBehaviour.Properties DEEPSLATE_TITANIUM_ORE = DEEPSLATE_ORE_PROPERTIES_SUPPLIER.get();

    public static final BlockBehaviour.Properties DEEPSLATE_ZINC_ORE = DEEPSLATE_ORE_PROPERTIES_SUPPLIER.get();

    public static final BlockBehaviour.Properties GALENA_ORE = STONE_ORE_PROPERTIES_SUPPLIER.get();

    public static final BlockBehaviour.Properties LITHIUM_ORE = STONE_ORE_PROPERTIES_SUPPLIER.get();

    public static final BlockBehaviour.Properties MONAZITE_ORE = STONE_ORE_PROPERTIES_SUPPLIER.get();

    public static final BlockBehaviour.Properties RAZOR_WIRE = BlockBehaviour.Properties.ofFullCopy(Blocks.CHAIN)
        .noOcclusion()
        .noCollission()
        .pushReaction(PushReaction.DESTROY);

    public static final BlockBehaviour.Properties TITANIUM_ORE = STONE_ORE_PROPERTIES_SUPPLIER.get();

    public static final BlockBehaviour.Properties ZINC_ORE = STONE_ORE_PROPERTIES_SUPPLIER.get();

    public static final BlockBehaviour.Properties ALUMINUM = BlockBehaviour.Properties.of()
        .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
        .mapColor(MapColor.COLOR_LIGHT_GRAY)
        .requiresCorrectToolForDrops()
        .sound(SoundType.COPPER)
        .strength(3, 3);

    public static final BlockBehaviour.Properties BRASS = BlockBehaviour.Properties.of()
        .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
        .mapColor(MapColor.COLOR_ORANGE)
        .requiresCorrectToolForDrops()
        .sound(SoundType.COPPER)
        .strength(4, 4);

    public static final Supplier<BlockBehaviour.Properties> FERROALUMINUM_SUPPLIER = () -> BlockBehaviour.Properties.of()
        .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
        .mapColor(MapColor.COLOR_GRAY)
        .requiresCorrectToolForDrops()
        .sound(SoundType.COPPER)
        .strength(5.5F, 5.5F);

    public static final BlockBehaviour.Properties FERROALUMINUM = FERROALUMINUM_SUPPLIER.get();

    public static final BlockBehaviour.Properties FERROALUMINUM_GRATE = FERROALUMINUM_SUPPLIER.get()
        .noOcclusion();

    public static final BlockBehaviour.Properties LEAD = BlockBehaviour.Properties.of()
        .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
        .mapColor(MapColor.COLOR_BLUE)
        .requiresCorrectToolForDrops()
        .sound(SoundType.COPPER)
        .strength(2, 2);

    public static final Supplier<BlockBehaviour.Properties> STEEL_SUPPLIER = () -> BlockBehaviour.Properties.of()
        .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
        .mapColor(MapColor.COLOR_GRAY)
        .requiresCorrectToolForDrops()
        .sound(SoundType.COPPER)
        .strength(6, 7);

    public static final BlockBehaviour.Properties STEEL = STEEL_SUPPLIER.get();

    public static final BlockBehaviour.Properties STEEL_GRATE = STEEL_SUPPLIER.get()
        .noOcclusion();

    public static final Supplier<BlockBehaviour.Properties> TITANIUM_SUPPLIER = () -> BlockBehaviour.Properties.of()
        .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
        .mapColor(MapColor.SAND)
        .requiresCorrectToolForDrops()
        .sound(SoundType.COPPER)
        .strength(7, 8);

    public static final BlockBehaviour.Properties TITANIUM = TITANIUM_SUPPLIER.get();

    public static final BlockBehaviour.Properties TITANIUM_GRATE = TITANIUM_SUPPLIER.get()
        .noOcclusion();

    public static final Supplier<BlockBehaviour.Properties> URANIUM_SUPPLIER = () -> BlockBehaviour.Properties.of()
        .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
        .mapColor(MapColor.COLOR_LIGHT_GREEN)
        .requiresCorrectToolForDrops()
        .sound(SoundType.COPPER)
        .strength(8, 9);

    public static final BlockBehaviour.Properties URANIUM = URANIUM_SUPPLIER.get();

    public static final BlockBehaviour.Properties ZINC = BlockBehaviour.Properties.of()
        .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
        .mapColor(MapColor.COLOR_LIGHT_GRAY)
        .requiresCorrectToolForDrops()
        .sound(SoundType.COPPER)
        .strength(4, 4);

    public static final Map<DyeColor, Block> DYE_COLOR_TO_CONCRETE_BLOCKS = Map.ofEntries(
        Map.entry(DyeColor.BLACK, Blocks.BLACK_CONCRETE),
        Map.entry(DyeColor.BLUE, Blocks.BLUE_CONCRETE),
        Map.entry(DyeColor.BROWN, Blocks.BROWN_CONCRETE),
        Map.entry(DyeColor.CYAN, Blocks.CYAN_CONCRETE),
        Map.entry(DyeColor.GREEN, Blocks.GREEN_CONCRETE),
        Map.entry(DyeColor.GRAY, Blocks.GRAY_CONCRETE),
        Map.entry(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_CONCRETE),
        Map.entry(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_CONCRETE),
        Map.entry(DyeColor.LIME, Blocks.LIME_CONCRETE),
        Map.entry(DyeColor.MAGENTA, Blocks.MAGENTA_CONCRETE),
        Map.entry(DyeColor.ORANGE, Blocks.ORANGE_CONCRETE),
        Map.entry(DyeColor.PINK, Blocks.PINK_CONCRETE),
        Map.entry(DyeColor.PURPLE, Blocks.PURPLE_CONCRETE),
        Map.entry(DyeColor.RED, Blocks.RED_CONCRETE),
        Map.entry(DyeColor.YELLOW, Blocks.YELLOW_CONCRETE),
        Map.entry(DyeColor.WHITE, Blocks.WHITE_CONCRETE)
    );

    public static final Map<DyeColor, BlockBehaviour.Properties> DYE_COLOR_TO_CONCRETE_PROPERTIES = DYE_COLOR_TO_CONCRETE_BLOCKS.entrySet()
        .stream()
        .collect(Collectors.toMap(Map.Entry::getKey, entry -> BlockBehaviour.Properties.ofFullCopy(entry.getValue())));

    public static final Map<DyeColor, BlockBehaviour.Properties> DYE_COLOR_TO_INDUSTRIAL_CONCRETE_PROPERTIES = Arrays.stream(
        DyeColor.values()
    )
        .collect(
            Collectors.toMap(
                Function.identity(),
                dyeColor -> BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                    .sound(SoundType.STONE)
            )
        );

    public static final Map<DyeColor, BlockBehaviour.Properties> DYE_COLOR_TO_INDUSTRIAL_GLASS_PROPERTIES = Arrays.stream(
        DyeColor.values()
    )
        .collect(
            Collectors.toMap(
                Function.identity(),
                dyeColor -> INDUSTRIAL_GLASS_SUPPLIER.get()
                    .mapColor(dyeColor)
            )
        );

    public static final Map<DyeColor, BlockBehaviour.Properties> DYE_COLOR_TO_INDUSTRIAL_GLASS_PANE_PROPERTIES = Arrays.stream(
        DyeColor.values()
    )
        .collect(
            Collectors.toMap(
                Function.identity(),
                dyeColor -> INDUSTRIAL_GLASS_PANE_SUPPLIER.get()
                    .mapColor(dyeColor)
            )
        );

    public static final Map<DyeColor, BlockBehaviour.Properties> DYE_COLOR_TO_PADDING_PROPERTIES = Arrays.stream(DyeColor.values())
        .collect(
            Collectors.toMap(
                Function.identity(),
                dyeColor -> BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_WOOL)
                    .mapColor(dyeColor)
            )
        );

    public static final Map<DyeColor, BlockBehaviour.Properties> DYE_COLOR_TO_PLASTIC_PROPERTIES = Arrays.stream(DyeColor.values())
        .collect(
            Collectors.toMap(
                Function.identity(),
                dyeColor -> BlockBehaviour.Properties.of()
                    .mapColor(dyeColor)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)
                    .strength(4, 4)
            )
        );
}
