package com.avp.fabric.data.loot;

import com.alien.common.registry.init.AlienBlocks;
import com.alien.common.registry.init.AlienItems;
import com.alien.common.registry.init.block.AlienResinBlocks;
import com.human.common.registry.init.block.HumanFerroaluminumBlocks;
import com.human.common.registry.init.block.HumanIndustrialConcreteBlocks;
import com.human.common.registry.init.block.HumanIndustrialGlassBlocks;
import com.human.common.registry.init.block.HumanPaddingBlocks;
import com.human.common.registry.init.block.HumanPlasticBlocks;
import com.human.common.registry.init.block.HumanSteelBlocks;
import com.human.common.registry.init.block.HumanTitaniumBlocks;
import com.predator.common.registry.init.PredatorBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.avp.AVP;
import com.avp.common.registry.AVPDeferredHolder;
import com.avp.common.registry.init.block.AVPBlocks;
import com.avp.common.registry.init.block.CoreBlocks;
import com.avp.common.registry.init.item.AVPItems;

public class BlockLootTableProvider extends FabricBlockLootTableProvider {

    private final Set<Block> touchedBlockSuppliers;

    public BlockLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
        this.touchedBlockSuppliers = new HashSet<>();
    }

    @Override
    public void generate() {
        generateSelfDrops();
        generateSlabDrops();
        generateCustomDrops();
        generateOtherDrops();

        var unhandledBlocks = AVPBlocks.getAll()
            .stream()
            .map(AVPDeferredHolder::get)
            .filter(Predicate.not(touchedBlockSuppliers::contains))
            .toList();

        if (!unhandledBlocks.isEmpty()) {
            var unhandledBlocksStrings = String.join("\n", unhandledBlocks.stream().map(Block::getDescriptionId).toList());
            AVP.LOGGER.error(
                "Detected {} blocks with unhandled loot table generation. Blocks:\n{}",
                unhandledBlocks.size(),
                unhandledBlocksStrings
            );
            throw new IllegalStateException(
                "Block loot table generation did not complete successfully - there are unhandled blocks that need to be handled."
            );
        }
    }

    private void generateSelfDrops() {
        dropSelf(AlienResinBlocks.ABERRANT_RESIN);
        dropSelf(AlienResinBlocks.ABERRANT_RESIN_STAIRS);
        dropSelf(AlienResinBlocks.IRRADIATED_RESIN);
        dropSelf(AlienResinBlocks.IRRADIATED_RESIN_STAIRS);
        dropSelf(AlienResinBlocks.NETHER_RESIN);
        dropSelf(AlienResinBlocks.NETHER_RESIN_STAIRS);
        dropSelf(AlienResinBlocks.RESIN);
        dropSelf(AlienResinBlocks.RESIN_BRICKS);
        dropSelf(AlienResinBlocks.RESIN_VENT);
        dropSelf(AlienResinBlocks.RIBBED_RESIN);
        dropSelf(AlienResinBlocks.SMOOTH_RESIN);
        dropSelf(AlienResinBlocks.RESIN_STAIRS);
        dropSelf(AlienResinBlocks.RESIN_BRICK_STAIRS);
        dropSelf(AlienBlocks.ROYAL_JELLY_BLOCK);

        dropSelf(CoreBlocks.ALUMINUM_BLOCK);
        dropSelf(CoreBlocks.ASH_BLOCK);
        dropSelf(CoreBlocks.AUTUNITE_BLOCK);
        dropSelf(AVPBlocks.BLUEPRINT_BLOCK);
        dropSelf(CoreBlocks.BRASS_BLOCK);
        dropSelf(HumanFerroaluminumBlocks.CHISELED_FERROALUMINUM);
        dropSelf(HumanSteelBlocks.CHISELED_STEEL);
        dropSelf(HumanTitaniumBlocks.CHISELED_TITANIUM);
        dropSelf(HumanFerroaluminumBlocks.CUT_FERROALUMINUM);
        dropSelf(HumanFerroaluminumBlocks.CUT_FERROALUMINUM_STAIRS);
        dropSelf(HumanSteelBlocks.CUT_STEEL);
        dropSelf(HumanSteelBlocks.CUT_STEEL_STAIRS);
        dropSelf(HumanTitaniumBlocks.CUT_TITANIUM);
        dropSelf(HumanTitaniumBlocks.CUT_TITANIUM_STAIRS);
        dropSelf(AVPBlocks.DESK_TERMINAL_BLOCK);
        dropSelf(HumanFerroaluminumBlocks.FERROALUMINUM_BLOCK);
        dropSelf(HumanFerroaluminumBlocks.FERROALUMINUM_BUTTON);
        dropSelf(HumanFerroaluminumBlocks.FERROALUMINUM_CHAIN_FENCE);
        dropSelf(HumanFerroaluminumBlocks.FERROALUMINUM_COLUMN);
        dropSelf(HumanFerroaluminumBlocks.FERROALUMINUM_FASTENED_SIDING);
        dropSelf(HumanFerroaluminumBlocks.FERROALUMINUM_FASTENED_SIDING_STAIRS);
        dropSelf(HumanFerroaluminumBlocks.FERROALUMINUM_FASTENED_STANDING);
        dropSelf(HumanFerroaluminumBlocks.FERROALUMINUM_FASTENED_STANDING_STAIRS);
        dropSelf(HumanFerroaluminumBlocks.FERROALUMINUM_GRATE);
        dropSelf(HumanFerroaluminumBlocks.FERROALUMINUM_GRATE_STAIRS);
        dropSelf(HumanFerroaluminumBlocks.FERROALUMINUM_PLATING);
        dropSelf(HumanFerroaluminumBlocks.FERROALUMINUM_PLATING_STAIRS);
        dropSelf(HumanFerroaluminumBlocks.FERROALUMINUM_PRESSURE_PLATE);
        dropSelf(HumanFerroaluminumBlocks.FERROALUMINUM_SIDING);
        dropSelf(HumanFerroaluminumBlocks.FERROALUMINUM_SIDING_STAIRS);
        dropSelf(HumanFerroaluminumBlocks.FERROALUMINUM_STAIRS);
        dropSelf(HumanFerroaluminumBlocks.FERROALUMINUM_STANDING);
        dropSelf(HumanFerroaluminumBlocks.FERROALUMINUM_STANDING_STAIRS);
        dropSelf(HumanFerroaluminumBlocks.FERROALUMINUM_TRAP_DOOR);
        dropSelf(HumanFerroaluminumBlocks.FERROALUMINUM_TREAD);
        dropSelf(HumanFerroaluminumBlocks.FERROALUMINUM_TREAD_STAIRS);
        dropSelf(HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS);
        dropSelf(HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS_PANE);
        dropSelf(HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS_STAIRS);
        dropSelf(HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS_TRAP_DOOR);
        dropSelf(CoreBlocks.LEAD_BLOCK);
        dropSelf(CoreBlocks.LITHIUM_BLOCK);
        dropSelf(AVPBlocks.NUKE_BLOCK);
        dropSelf(CoreBlocks.RAW_BAUXITE_BLOCK);
        dropSelf(CoreBlocks.RAW_GALENA_BLOCK);
        dropSelf(CoreBlocks.RAW_MONAZITE_BLOCK);
        dropSelf(CoreBlocks.RAW_TITANIUM_BLOCK);
        dropSelf(CoreBlocks.RAW_ZINC_BLOCK);
        dropSelf(AVPBlocks.RAZOR_WIRE);
        dropSelf(AVPBlocks.REDSTONE_GENERATOR);
        dropSelf(AVPBlocks.RESONATOR_BLOCK);
        dropSelf(AVPBlocks.SENTRY_TURRET);
        dropSelf(CoreBlocks.SILICA_GRAVEL);
        dropSelf(CoreBlocks.SILICON_BLOCK);
        dropSelf(HumanSteelBlocks.STEEL_BARS);
        dropSelf(HumanSteelBlocks.STEEL_BLOCK);
        dropSelf(HumanSteelBlocks.STEEL_BUTTON);
        dropSelf(HumanSteelBlocks.STEEL_CHAIN_FENCE);
        dropSelf(HumanSteelBlocks.STEEL_COLUMN);
        dropSelf(HumanSteelBlocks.STEEL_FASTENED_SIDING);
        dropSelf(HumanSteelBlocks.STEEL_FASTENED_SIDING_STAIRS);
        dropSelf(HumanSteelBlocks.STEEL_FASTENED_STANDING);
        dropSelf(HumanSteelBlocks.STEEL_FASTENED_STANDING_STAIRS);
        dropSelf(HumanSteelBlocks.STEEL_GRATE);
        dropSelf(HumanSteelBlocks.STEEL_GRATE_STAIRS);
        dropSelf(HumanSteelBlocks.STEEL_PLATING);
        dropSelf(HumanSteelBlocks.STEEL_PLATING_STAIRS);
        dropSelf(HumanSteelBlocks.STEEL_PRESSURE_PLATE);
        dropSelf(HumanSteelBlocks.STEEL_SIDING);
        dropSelf(HumanSteelBlocks.STEEL_SIDING_STAIRS);
        dropSelf(HumanSteelBlocks.STEEL_STAIRS);
        dropSelf(HumanSteelBlocks.STEEL_STANDING);
        dropSelf(HumanSteelBlocks.STEEL_STANDING_STAIRS);
        dropSelf(HumanSteelBlocks.STEEL_TRAP_DOOR);
        dropSelf(HumanSteelBlocks.STEEL_TREAD);
        dropSelf(HumanSteelBlocks.STEEL_TREAD_STAIRS);
        dropSelf(HumanTitaniumBlocks.TITANIUM_BLOCK);
        dropSelf(HumanTitaniumBlocks.TITANIUM_BUTTON);
        dropSelf(HumanTitaniumBlocks.TITANIUM_CHAIN_FENCE);
        dropSelf(HumanTitaniumBlocks.TITANIUM_COLUMN);
        dropSelf(HumanTitaniumBlocks.TITANIUM_FASTENED_SIDING);
        dropSelf(HumanTitaniumBlocks.TITANIUM_FASTENED_SIDING_STAIRS);
        dropSelf(HumanTitaniumBlocks.TITANIUM_FASTENED_STANDING);
        dropSelf(HumanTitaniumBlocks.TITANIUM_FASTENED_STANDING_STAIRS);
        dropSelf(HumanTitaniumBlocks.TITANIUM_GRATE);
        dropSelf(HumanTitaniumBlocks.TITANIUM_GRATE_STAIRS);
        dropSelf(HumanTitaniumBlocks.TITANIUM_PLATING);
        dropSelf(HumanTitaniumBlocks.TITANIUM_PLATING_STAIRS);
        dropSelf(HumanTitaniumBlocks.TITANIUM_PRESSURE_PLATE);
        dropSelf(HumanTitaniumBlocks.TITANIUM_SIDING);
        dropSelf(HumanTitaniumBlocks.TITANIUM_SIDING_STAIRS);
        dropSelf(HumanTitaniumBlocks.TITANIUM_STAIRS);
        dropSelf(HumanTitaniumBlocks.TITANIUM_STANDING);
        dropSelf(HumanTitaniumBlocks.TITANIUM_STANDING_STAIRS);
        dropSelf(HumanTitaniumBlocks.TITANIUM_TRAP_DOOR);
        dropSelf(HumanTitaniumBlocks.TITANIUM_TREAD);
        dropSelf(HumanTitaniumBlocks.TITANIUM_TREAD_STAIRS);
        dropSelf(CoreBlocks.TRINITITE_BLOCK);
        dropSelf(PredatorBlocks.TRIP_MINE_BLOCK);
        dropSelf(CoreBlocks.URANIUM_BLOCK);
        dropSelf(CoreBlocks.ZINC_BLOCK);

        Stream.of(
            AVPBlocks.DYE_COLOR_TO_CONCRETE_STAIRS,
            HumanPlasticBlocks.DYE_COLOR_TO_CUT_PLASTIC,
            HumanPlasticBlocks.DYE_COLOR_TO_CUT_PLASTIC_STAIRS,
            HumanIndustrialConcreteBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE,
            HumanIndustrialConcreteBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_STAIRS,
            HumanIndustrialConcreteBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_WALL,
            HumanIndustrialGlassBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS,
            HumanIndustrialGlassBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS_PANE,
            HumanPaddingBlocks.DYE_COLOR_TO_PADDING,
            HumanPaddingBlocks.DYE_COLOR_TO_PADDING_STAIRS,
            HumanPaddingBlocks.DYE_COLOR_TO_PANEL_PADDING,
            HumanPaddingBlocks.DYE_COLOR_TO_PANEL_PADDING_STAIRS,
            HumanPaddingBlocks.DYE_COLOR_TO_PIPE_PADDING,
            HumanPaddingBlocks.DYE_COLOR_TO_PIPE_PADDING_STAIRS,
            HumanPlasticBlocks.DYE_COLOR_TO_PLASTIC,
            HumanPlasticBlocks.DYE_COLOR_TO_PLASTIC_STAIRS
        )
            .map(Map::values)
            .flatMap(Collection::stream)
            .forEach(this::dropSelf);
    }

    private void generateSlabDrops() {
        dropSlab(AlienResinBlocks.ABERRANT_RESIN_SLAB);
        dropSlab(AlienResinBlocks.IRRADIATED_RESIN_SLAB);
        dropSlab(AlienResinBlocks.NETHER_RESIN_SLAB);
        dropSlab(AlienResinBlocks.RESIN_SLAB);
        dropSlab(AlienResinBlocks.RESIN_BRICK_SLAB);

        dropSlab(HumanFerroaluminumBlocks.CUT_FERROALUMINUM_SLAB);
        dropSlab(HumanSteelBlocks.CUT_STEEL_SLAB);
        dropSlab(HumanTitaniumBlocks.CUT_TITANIUM_SLAB);
        dropSlab(HumanFerroaluminumBlocks.FERROALUMINUM_FASTENED_SIDING_SLAB);
        dropSlab(HumanFerroaluminumBlocks.FERROALUMINUM_FASTENED_STANDING_SLAB);
        dropSlab(HumanFerroaluminumBlocks.FERROALUMINUM_GRATE_SLAB);
        dropSlab(HumanFerroaluminumBlocks.FERROALUMINUM_PLATING_SLAB);
        dropSlab(HumanFerroaluminumBlocks.FERROALUMINUM_SIDING_SLAB);
        dropSlab(HumanFerroaluminumBlocks.FERROALUMINUM_SLAB);
        dropSlab(HumanFerroaluminumBlocks.FERROALUMINUM_STANDING_SLAB);
        dropSlab(HumanFerroaluminumBlocks.FERROALUMINUM_TREAD_SLAB);
        dropSlab(HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS_SLAB);
        dropSlab(HumanSteelBlocks.STEEL_FASTENED_SIDING_SLAB);
        dropSlab(HumanSteelBlocks.STEEL_FASTENED_STANDING_SLAB);
        dropSlab(HumanSteelBlocks.STEEL_GRATE_SLAB);
        dropSlab(HumanSteelBlocks.STEEL_PLATING_SLAB);
        dropSlab(HumanSteelBlocks.STEEL_SIDING_SLAB);
        dropSlab(HumanSteelBlocks.STEEL_SLAB);
        dropSlab(HumanSteelBlocks.STEEL_STANDING_SLAB);
        dropSlab(HumanSteelBlocks.STEEL_TREAD_SLAB);
        dropSlab(HumanTitaniumBlocks.TITANIUM_FASTENED_SIDING_SLAB);
        dropSlab(HumanTitaniumBlocks.TITANIUM_FASTENED_STANDING_SLAB);
        dropSlab(HumanTitaniumBlocks.TITANIUM_GRATE_SLAB);
        dropSlab(HumanTitaniumBlocks.TITANIUM_PLATING_SLAB);
        dropSlab(HumanTitaniumBlocks.TITANIUM_SIDING_SLAB);
        dropSlab(HumanTitaniumBlocks.TITANIUM_SLAB);
        dropSlab(HumanTitaniumBlocks.TITANIUM_STANDING_SLAB);
        dropSlab(HumanTitaniumBlocks.TITANIUM_TREAD_SLAB);

        Stream.of(
            AVPBlocks.DYE_COLOR_TO_CONCRETE_SLAB,
            HumanPlasticBlocks.DYE_COLOR_TO_CUT_PLASTIC_SLAB,
            HumanIndustrialConcreteBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_SLAB,
            HumanPaddingBlocks.DYE_COLOR_TO_PADDING_SLAB,
            HumanPaddingBlocks.DYE_COLOR_TO_PANEL_PADDING_SLAB,
            HumanPaddingBlocks.DYE_COLOR_TO_PIPE_PADDING_SLAB,
            HumanPlasticBlocks.DYE_COLOR_TO_PLASTIC_SLAB
        )
            .map(Map::values)
            .flatMap(Collection::stream)
            .forEach(this::dropSlab);
    }

    private void generateCustomDrops() {
        add(CoreBlocks.AUTUNITE_ORE, block -> createOreMultiDrop(block, AVPItems.AUTUNITE_DUST.get(), 2, 4));
        add(CoreBlocks.BAUXITE_ORE, block -> createOreDrop(block, AVPItems.RAW_BAUXITE.get()));
        add(CoreBlocks.DEEPSLATE_TITANIUM_ORE, block -> createOreDrop(block, AVPItems.RAW_TITANIUM.get()));
        add(CoreBlocks.DEEPSLATE_ZINC_ORE, block -> createOreMultiDrop(block, AVPItems.RAW_ZINC.get(), 2, 5));
        add(CoreBlocks.GALENA_ORE, block -> createOreDrop(block, AVPItems.RAW_GALENA.get()));
        add(AVPBlocks.LEAD_CHEST, this::createShulkerBoxDrop);
        add(AVPBlocks.AMMO_CHEST, this::createShulkerBoxDrop);
        add(CoreBlocks.LITHIUM_ORE, block -> createOreMultiDrop(block, AVPItems.LITHIUM_DUST.get(), 2, 4));
        add(CoreBlocks.MONAZITE_ORE, block -> createOreDrop(block, AVPItems.RAW_MONAZITE.get()));
        add(CoreBlocks.ZINC_ORE, block -> createOreMultiDrop(block, AVPItems.RAW_ZINC.get(), 2, 5));
        add(HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS_DOOR, this::createDoorTable);
        add(HumanFerroaluminumBlocks.FERROALUMINUM_DOOR, this::createDoorTable);
        add(HumanSteelBlocks.STEEL_DOOR, this::createDoorTable);
        add(HumanTitaniumBlocks.TITANIUM_DOOR, this::createDoorTable);
        add(AVPBlocks.INDUSTRIAL_FURNACE, this::createNameableBlockEntityTable);
    }

    private void generateOtherDrops() {
        dropOther(AlienResinBlocks.ABERRANT_RESIN_NODE, AlienResinBlocks.ABERRANT_RESIN);
        dropOther(AlienResinBlocks.ABERRANT_RESIN_VEIN, AlienItems.ABERRANT_RESIN_BALL);
        dropOther(AlienResinBlocks.ABERRANT_RESIN_WEB, AlienItems.ABERRANT_RESIN_BALL);
        dropOther(AlienResinBlocks.IRRADIATED_RESIN_NODE, AlienResinBlocks.IRRADIATED_RESIN);
        dropOther(AlienResinBlocks.IRRADIATED_RESIN_VEIN, AlienItems.IRRADIATED_RESIN_BALL);
        dropOther(AlienResinBlocks.IRRADIATED_RESIN_WEB, AlienItems.IRRADIATED_RESIN_BALL);
        dropOther(AlienResinBlocks.NETHER_RESIN_NODE, AlienResinBlocks.NETHER_RESIN);
        dropOther(AlienResinBlocks.NETHER_RESIN_VEIN, AlienItems.NETHER_RESIN_BALL);
        dropOther(AlienResinBlocks.NETHER_RESIN_WEB, AlienItems.NETHER_RESIN_BALL);
        dropOther(AlienResinBlocks.RESIN_NODE, AlienResinBlocks.RESIN);
        dropOther(AlienResinBlocks.RESIN_VEIN, AlienItems.RESIN_BALL);
        dropOther(AlienResinBlocks.RESIN_WEB, AlienItems.RESIN_BALL);
    }

    public void add(Supplier<? extends Block> blockSupplier, Function<Block, LootTable.Builder> factory) {
        var block = blockSupplier.get();
        add(block, factory);
        touchedBlockSuppliers.add(block);
    }

    public void dropOther(Supplier<? extends Block> blockSupplier, Supplier<? extends ItemLike> itemLikeSupplier) {
        var block = blockSupplier.get();
        dropOther(block, itemLikeSupplier.get());
        touchedBlockSuppliers.add(block);
    }

    public void dropSelf(Supplier<? extends Block> blockSupplier) {
        var block = blockSupplier.get();
        dropSelf(block);
        touchedBlockSuppliers.add(block);
    }

    public void dropSlab(Supplier<? extends Block> blockSupplier) {
        var block = blockSupplier.get();
        add(block, createSlabItemTable(block));
        touchedBlockSuppliers.add(block);
    }

    public LootTable.Builder createOreMultiDrop(Block block, Item item, int min, int max) {
        var registryLookup = registries.lookupOrThrow(Registries.ENCHANTMENT);

        return createSilkTouchDispatchTable(
            block,
            applyExplosionDecay(
                block,
                LootItem.lootTableItem(item)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max)))
                    .apply(ApplyBonusCount.addUniformBonusCount(registryLookup.getOrThrow(Enchantments.FORTUNE)))
            )
        );
    }
}
