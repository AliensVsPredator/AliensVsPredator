package com.avp.fabric.common.block;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.avp.fabric.common.entity.AVPEntityTypeTags;

public class BlockProperties {

    public static final BlockPropertyBuilder ASH_BLOCK = BlockPropertyBuilder.of()
        .mapColor(MapColor.SNOW)
        .replaceable()
        .forceSolidOff()
        .randomTicks()
        .strength(0.1F)
        .requiresCorrectToolForDrops()
        .sound(SoundType.SNOW)
        .isViewBlocking(
            (blockStatex, blockGetter, blockPos) -> blockStatex.getValue(
                SnowLayerBlock.LAYERS
            ) >= 8
        )
        .pushReaction(PushReaction.DESTROY);

    public static final BlockPropertyBuilder STEEL_BARS = BlockPropertyBuilder.of()
        .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
        .mapColor(MapColor.COLOR_GRAY)
        .requiresCorrectToolForDrops()
        .sound(SoundType.COPPER)
        .strength(6, 7)
        .noOcclusion();

    public static final BlockPropertyBuilder TITANIUM_BARS = BlockPropertyBuilder.of()
        .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
        .mapColor(MapColor.SAND)
        .requiresCorrectToolForDrops()
        .sound(SoundType.COPPER)
        .strength(7, 8)
        .noOcclusion();

    public static final BlockPropertyBuilder FERROALUMINUM_BARS = BlockPropertyBuilder.of()
        .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
        .mapColor(MapColor.COLOR_GRAY)
        .requiresCorrectToolForDrops()
        .sound(SoundType.COPPER)
        .strength(5.5F, 5.5F)
        .noOcclusion();

    public static final BlockPropertyBuilder TRINITITE = BlockPropertyBuilder.of()
        .instrument(NoteBlockInstrument.HAT)
        .strength(0.3F)
        .sound(SoundType.GLASS)
        .noOcclusion()
        .isValidSpawn(Blocks::never)
        .isRedstoneConductor(Blocks::never)
        .isSuffocating(Blocks::never)
        .isViewBlocking(Blocks::never);

    public static final BlockPropertyBuilder JELLY = BlockPropertyBuilder.of()
        .mapColor(MapColor.GRASS)
        .sound(SoundType.SLIME_BLOCK)
        .noOcclusion()
        .friction(0.8F);

    private static final Supplier<BlockPropertyBuilder> INDUSTRIAL_GLASS_SUPPLIER = () -> BlockPropertyBuilder.inherit(Blocks.GLASS)
        .requiresCorrectToolForDrops()
        .strength(5);

    public static final BlockPropertyBuilder INDUSTRIAL_GLASS = INDUSTRIAL_GLASS_SUPPLIER.get();

    public static final Supplier<BlockPropertyBuilder> INDUSTRIAL_GLASS_PANE_SUPPLIER = () -> INDUSTRIAL_GLASS_SUPPLIER.get()
        .instrument(NoteBlockInstrument.HAT);

    public static final BlockPropertyBuilder INDUSTRIAL_GLASS_PANE = INDUSTRIAL_GLASS_PANE_SUPPLIER.get();

    private static final Supplier<BlockPropertyBuilder> RESIN_PROPERTIES_SUPPLIER = () -> BlockPropertyBuilder.of()
        .mapColor(MapColor.COLOR_BLACK)
        .requiresCorrectToolForDrops()
        .strength(4F)
        // FIXME:
        .sound(SoundType.HONEY_BLOCK);

    private static final Supplier<BlockPropertyBuilder> NETHER_RESIN_BLOCK_PROPERTIES_SUPPLIER = () -> RESIN_PROPERTIES_SUPPLIER.get()
        .isValidSpawn(($1, $2, $3, entityType) -> entityType.is(AVPEntityTypeTags.NETHER_ALIENS));

    private static final Supplier<BlockPropertyBuilder> RESIN_BLOCK_PROPERTIES_SUPPLIER = () -> RESIN_PROPERTIES_SUPPLIER.get()
        .isValidSpawn(($1, $2, $3, entityType) -> entityType.is(AVPEntityTypeTags.NORMAL_ALIENS));

    public static final BlockPropertyBuilder NETHER_RESIN = NETHER_RESIN_BLOCK_PROPERTIES_SUPPLIER.get()
        .mapColor(MapColor.COLOR_RED);

    public static final BlockPropertyBuilder NETHER_RESIN_VEIN = RESIN_PROPERTIES_SUPPLIER.get()
        .mapColor(MapColor.COLOR_RED)
        .noCollision()
        .noOcclusion()
        .pushReaction(PushReaction.DESTROY)
        .replaceable();

    public static final BlockPropertyBuilder NETHER_RESIN_WEB = NETHER_RESIN_BLOCK_PROPERTIES_SUPPLIER.get()
        .mapColor(MapColor.COLOR_RED)
        .noCollision()
        .noOcclusion()
        .pushReaction(PushReaction.DESTROY);

    public static final BlockPropertyBuilder RESIN = RESIN_BLOCK_PROPERTIES_SUPPLIER.get();

    public static final BlockPropertyBuilder RESIN_VEIN = RESIN_PROPERTIES_SUPPLIER.get()
        .noCollision()
        .noOcclusion()
        .pushReaction(PushReaction.DESTROY)
        .replaceable();

    public static final BlockPropertyBuilder RESIN_WEB = RESIN_BLOCK_PROPERTIES_SUPPLIER.get()
        .noCollision()
        .noOcclusion()
        .pushReaction(PushReaction.DESTROY);

    private static final Supplier<BlockPropertyBuilder> DEEPSLATE_ORE_PROPERTIES_SUPPLIER = () -> BlockPropertyBuilder.inherit(
        Blocks.DEEPSLATE
    )
        .mapColor(MapColor.DEEPSLATE)
        .requiresCorrectToolForDrops()
        .strength(4.5F, 3)
        .sound(SoundType.DEEPSLATE);

    private static final Supplier<BlockPropertyBuilder> STONE_ORE_PROPERTIES_SUPPLIER = () -> BlockPropertyBuilder.inherit(Blocks.STONE)
        .mapColor(MapColor.STONE)
        .requiresCorrectToolForDrops()
        .strength(3, 3)
        .sound(SoundType.STONE);

    public static final BlockPropertyBuilder AUTUNITE_ORE = STONE_ORE_PROPERTIES_SUPPLIER.get()
        .lightLevel($ -> 7);

    public static final BlockPropertyBuilder BAUXITE_ORE = STONE_ORE_PROPERTIES_SUPPLIER.get();

    public static final BlockPropertyBuilder DEEPSLATE_TITANIUM_ORE = DEEPSLATE_ORE_PROPERTIES_SUPPLIER.get();

    public static final BlockPropertyBuilder DEEPSLATE_ZINC_ORE = DEEPSLATE_ORE_PROPERTIES_SUPPLIER.get();

    public static final BlockPropertyBuilder GALENA_ORE = STONE_ORE_PROPERTIES_SUPPLIER.get();

    public static final BlockPropertyBuilder LITHIUM_ORE = STONE_ORE_PROPERTIES_SUPPLIER.get();

    public static final BlockPropertyBuilder MONAZITE_ORE = STONE_ORE_PROPERTIES_SUPPLIER.get();

    public static final BlockPropertyBuilder RAZOR_WIRE = BlockPropertyBuilder.of()
        .forceSolidOn()
        .noOcclusion()
        .noCollision()
        .sound(SoundType.CHAIN)
        .strength(1.1F)
        .pushReaction(PushReaction.DESTROY);

    public static final BlockPropertyBuilder TITANIUM_ORE = STONE_ORE_PROPERTIES_SUPPLIER.get();

    public static final BlockPropertyBuilder ZINC_ORE = STONE_ORE_PROPERTIES_SUPPLIER.get();

    public static final BlockPropertyBuilder ALUMINUM = BlockPropertyBuilder.of()
        .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
        .mapColor(MapColor.COLOR_LIGHT_GRAY)
        .requiresCorrectToolForDrops()
        .sound(SoundType.COPPER)
        .strength(3, 3);

    public static final BlockPropertyBuilder BRASS = BlockPropertyBuilder.of()
        .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
        .mapColor(MapColor.COLOR_ORANGE)
        .requiresCorrectToolForDrops()
        .sound(SoundType.COPPER)
        .strength(4, 4);

    public static final Supplier<BlockPropertyBuilder> FERROALUMINUM_SUPPLIER = () -> BlockPropertyBuilder.of()
        .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
        .mapColor(MapColor.COLOR_GRAY)
        .requiresCorrectToolForDrops()
        .sound(SoundType.COPPER)
        .strength(5.5F, 5.5F);

    public static final BlockPropertyBuilder FERROALUMINUM = FERROALUMINUM_SUPPLIER.get();

    public static final BlockPropertyBuilder FERROALUMINUM_GRATE = FERROALUMINUM_SUPPLIER.get()
        .noOcclusion();

    public static final BlockPropertyBuilder LEAD = BlockPropertyBuilder.of()
        .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
        .mapColor(MapColor.COLOR_BLUE)
        .requiresCorrectToolForDrops()
        .sound(SoundType.COPPER)
        .strength(2, 2);

    public static final Supplier<BlockPropertyBuilder> NUKE_SUPPLIER = () -> BlockPropertyBuilder.of()
        .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
        .mapColor(MapColor.COLOR_GRAY)
        .requiresCorrectToolForDrops()
        .sound(SoundType.COPPER)
        .strength(4);

    public static final BlockPropertyBuilder NUKE = NUKE_SUPPLIER.get();

    public static final Supplier<BlockPropertyBuilder> STEEL_SUPPLIER = () -> BlockPropertyBuilder.of()
        .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
        .mapColor(MapColor.COLOR_GRAY)
        .requiresCorrectToolForDrops()
        .sound(SoundType.COPPER)
        .strength(6, 7);

    public static final BlockPropertyBuilder STEEL = STEEL_SUPPLIER.get();

    public static final BlockPropertyBuilder STEEL_GRATE = STEEL_SUPPLIER.get()
        .noOcclusion();

    public static final Supplier<BlockPropertyBuilder> TITANIUM_SUPPLIER = () -> BlockPropertyBuilder.of()
        .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
        .mapColor(MapColor.SAND)
        .requiresCorrectToolForDrops()
        .sound(SoundType.COPPER)
        .strength(7, 8);

    public static final BlockPropertyBuilder TITANIUM = TITANIUM_SUPPLIER.get();

    public static final BlockPropertyBuilder TITANIUM_GRATE = TITANIUM_SUPPLIER.get()
        .noOcclusion();

    public static final Supplier<BlockPropertyBuilder> URANIUM_SUPPLIER = () -> BlockPropertyBuilder.of()
        .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
        .mapColor(MapColor.COLOR_LIGHT_GREEN)
        .requiresCorrectToolForDrops()
        .sound(SoundType.COPPER)
        .strength(8, 9);

    public static final BlockPropertyBuilder URANIUM = URANIUM_SUPPLIER.get();

    public static final BlockPropertyBuilder ZINC = BlockPropertyBuilder.of()
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

    public static final Map<DyeColor, BlockPropertyBuilder> DYE_COLOR_TO_CONCRETE_PROPERTIES = DYE_COLOR_TO_CONCRETE_BLOCKS.entrySet()
        .stream()
        .collect(Collectors.toMap(Map.Entry::getKey, entry -> BlockPropertyBuilder.inherit(entry.getValue())));

    public static final Map<DyeColor, BlockPropertyBuilder> DYE_COLOR_TO_INDUSTRIAL_CONCRETE_PROPERTIES = Arrays.stream(
        DyeColor.values()
    )
        .collect(
            Collectors.toMap(
                Function.identity(),
                dyeColor -> BlockPropertyBuilder.inherit(Blocks.IRON_BLOCK)
                    .sound(SoundType.STONE)
            )
        );

    public static final Map<DyeColor, BlockPropertyBuilder> DYE_COLOR_TO_INDUSTRIAL_GLASS_PROPERTIES = Arrays.stream(
        DyeColor.values()
    )
        .collect(
            Collectors.toMap(
                Function.identity(),
                dyeColor -> INDUSTRIAL_GLASS_SUPPLIER.get()
                    .mapColor(dyeColor)
            )
        );

    public static final Map<DyeColor, BlockPropertyBuilder> DYE_COLOR_TO_INDUSTRIAL_GLASS_PANE_PROPERTIES = Arrays.stream(
        DyeColor.values()
    )
        .collect(
            Collectors.toMap(
                Function.identity(),
                dyeColor -> INDUSTRIAL_GLASS_PANE_SUPPLIER.get()
                    .mapColor(dyeColor)
            )
        );

    public static final Map<DyeColor, BlockPropertyBuilder> DYE_COLOR_TO_PADDING_PROPERTIES = Arrays.stream(DyeColor.values())
        .collect(
            Collectors.toMap(
                Function.identity(),
                dyeColor -> BlockPropertyBuilder.inherit(Blocks.WHITE_WOOL)
                    .mapColor(dyeColor)
            )
        );

    public static final Map<DyeColor, BlockPropertyBuilder> DYE_COLOR_TO_PLASTIC_PROPERTIES = Arrays.stream(DyeColor.values())
        .collect(
            Collectors.toMap(
                Function.identity(),
                dyeColor -> BlockPropertyBuilder.of()
                    .mapColor(dyeColor)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)
                    .strength(4, 4)
            )
        );
}
