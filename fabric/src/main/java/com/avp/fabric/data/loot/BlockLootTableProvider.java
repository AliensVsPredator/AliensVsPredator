package com.avp.fabric.data.loot;

import com.alien.common.registry.init.AlienBlocks;
import com.alien.common.registry.init.AlienItems;
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
import com.avp.common.registry.init.AVPBlocks;
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
        dropSelf(AlienBlocks.ABERRANT_RESIN);
        dropSelf(AVPBlocks.ALUMINUM_BLOCK);
        dropSelf(AVPBlocks.ASH_BLOCK);
        dropSelf(AVPBlocks.AUTUNITE_BLOCK);
        dropSelf(AVPBlocks.BLUEPRINT_BLOCK);
        dropSelf(AVPBlocks.BRASS_BLOCK);
        dropSelf(AVPBlocks.CHISELED_FERROALUMINUM);
        dropSelf(AVPBlocks.CHISELED_STEEL);
        dropSelf(AVPBlocks.CHISELED_TITANIUM);
        dropSelf(AVPBlocks.CUT_FERROALUMINUM);
        dropSelf(AVPBlocks.CUT_FERROALUMINUM_STAIRS);
        dropSelf(AVPBlocks.CUT_STEEL);
        dropSelf(AVPBlocks.CUT_STEEL_STAIRS);
        dropSelf(AVPBlocks.CUT_TITANIUM);
        dropSelf(AVPBlocks.CUT_TITANIUM_STAIRS);
        dropSelf(AVPBlocks.DESK_TERMINAL_BLOCK);
        dropSelf(AVPBlocks.FERROALUMINUM_BLOCK);
        dropSelf(AVPBlocks.FERROALUMINUM_BUTTON);
        dropSelf(AVPBlocks.FERROALUMINUM_CHAIN_FENCE);
        dropSelf(AVPBlocks.FERROALUMINUM_COLUMN);
        dropSelf(AVPBlocks.FERROALUMINUM_FASTENED_SIDING);
        dropSelf(AVPBlocks.FERROALUMINUM_FASTENED_SIDING_STAIRS);
        dropSelf(AVPBlocks.FERROALUMINUM_FASTENED_STANDING);
        dropSelf(AVPBlocks.FERROALUMINUM_FASTENED_STANDING_STAIRS);
        dropSelf(AVPBlocks.FERROALUMINUM_GRATE);
        dropSelf(AVPBlocks.FERROALUMINUM_GRATE_STAIRS);
        dropSelf(AVPBlocks.FERROALUMINUM_PLATING);
        dropSelf(AVPBlocks.FERROALUMINUM_PLATING_STAIRS);
        dropSelf(AVPBlocks.FERROALUMINUM_PRESSURE_PLATE);
        dropSelf(AVPBlocks.FERROALUMINUM_SIDING);
        dropSelf(AVPBlocks.FERROALUMINUM_SIDING_STAIRS);
        dropSelf(AVPBlocks.FERROALUMINUM_STAIRS);
        dropSelf(AVPBlocks.FERROALUMINUM_STANDING);
        dropSelf(AVPBlocks.FERROALUMINUM_STANDING_STAIRS);
        dropSelf(AVPBlocks.FERROALUMINUM_TRAP_DOOR);
        dropSelf(AVPBlocks.FERROALUMINUM_TREAD);
        dropSelf(AVPBlocks.FERROALUMINUM_TREAD_STAIRS);
        dropSelf(AVPBlocks.INDUSTRIAL_GLASS);
        dropSelf(AVPBlocks.INDUSTRIAL_GLASS_PANE);
        dropSelf(AVPBlocks.INDUSTRIAL_GLASS_STAIRS);
        dropSelf(AVPBlocks.INDUSTRIAL_GLASS_TRAP_DOOR);
        dropSelf(AlienBlocks.IRRADIATED_RESIN);
        dropSelf(AVPBlocks.LEAD_BLOCK);
        dropSelf(AVPBlocks.LITHIUM_BLOCK);
        dropSelf(AlienBlocks.NETHER_RESIN);
        dropSelf(AVPBlocks.NUKE_BLOCK);
        dropSelf(AVPBlocks.RAW_BAUXITE_BLOCK);
        dropSelf(AVPBlocks.RAW_GALENA_BLOCK);
        dropSelf(AVPBlocks.RAW_MONAZITE_BLOCK);
        dropSelf(AVPBlocks.RAW_TITANIUM_BLOCK);
        dropSelf(AVPBlocks.RAW_ZINC_BLOCK);
        dropSelf(AVPBlocks.RAZOR_WIRE);
        dropSelf(AVPBlocks.REDSTONE_GENERATOR);
        dropSelf(AlienBlocks.RESIN);
        dropSelf(AlienBlocks.RESIN_BRICKS);
        dropSelf(AlienBlocks.RESIN_O);
        dropSelf(AlienBlocks.RESIN_RIBBED);
        dropSelf(AlienBlocks.RESIN_SMOOTH);
        dropSelf(AVPBlocks.RESONATOR_BLOCK);
        dropSelf(AlienBlocks.ROYAL_JELLY_BLOCK);
        dropSelf(AVPBlocks.SENTRY_TURRET);
        dropSelf(AVPBlocks.SILICA_GRAVEL);
        dropSelf(AVPBlocks.SILICON_BLOCK);
        dropSelf(AVPBlocks.STEEL_BARS);
        dropSelf(AVPBlocks.STEEL_BLOCK);
        dropSelf(AVPBlocks.STEEL_BUTTON);
        dropSelf(AVPBlocks.STEEL_CHAIN_FENCE);
        dropSelf(AVPBlocks.STEEL_COLUMN);
        dropSelf(AVPBlocks.STEEL_FASTENED_SIDING);
        dropSelf(AVPBlocks.STEEL_FASTENED_SIDING_STAIRS);
        dropSelf(AVPBlocks.STEEL_FASTENED_STANDING);
        dropSelf(AVPBlocks.STEEL_FASTENED_STANDING_STAIRS);
        dropSelf(AVPBlocks.STEEL_GRATE);
        dropSelf(AVPBlocks.STEEL_GRATE_STAIRS);
        dropSelf(AVPBlocks.STEEL_PLATING);
        dropSelf(AVPBlocks.STEEL_PLATING_STAIRS);
        dropSelf(AVPBlocks.STEEL_PRESSURE_PLATE);
        dropSelf(AVPBlocks.STEEL_SIDING);
        dropSelf(AVPBlocks.STEEL_SIDING_STAIRS);
        dropSelf(AVPBlocks.STEEL_STAIRS);
        dropSelf(AVPBlocks.STEEL_STANDING);
        dropSelf(AVPBlocks.STEEL_STANDING_STAIRS);
        dropSelf(AVPBlocks.STEEL_TRAP_DOOR);
        dropSelf(AVPBlocks.STEEL_TREAD);
        dropSelf(AVPBlocks.STEEL_TREAD_STAIRS);
        dropSelf(AVPBlocks.TITANIUM_BLOCK);
        dropSelf(AVPBlocks.TITANIUM_BUTTON);
        dropSelf(AVPBlocks.TITANIUM_CHAIN_FENCE);
        dropSelf(AVPBlocks.TITANIUM_COLUMN);
        dropSelf(AVPBlocks.TITANIUM_FASTENED_SIDING);
        dropSelf(AVPBlocks.TITANIUM_FASTENED_SIDING_STAIRS);
        dropSelf(AVPBlocks.TITANIUM_FASTENED_STANDING);
        dropSelf(AVPBlocks.TITANIUM_FASTENED_STANDING_STAIRS);
        dropSelf(AVPBlocks.TITANIUM_GRATE);
        dropSelf(AVPBlocks.TITANIUM_GRATE_STAIRS);
        dropSelf(AVPBlocks.TITANIUM_PLATING);
        dropSelf(AVPBlocks.TITANIUM_PLATING_STAIRS);
        dropSelf(AVPBlocks.TITANIUM_PRESSURE_PLATE);
        dropSelf(AVPBlocks.TITANIUM_SIDING);
        dropSelf(AVPBlocks.TITANIUM_SIDING_STAIRS);
        dropSelf(AVPBlocks.TITANIUM_STAIRS);
        dropSelf(AVPBlocks.TITANIUM_STANDING);
        dropSelf(AVPBlocks.TITANIUM_STANDING_STAIRS);
        dropSelf(AVPBlocks.TITANIUM_TRAP_DOOR);
        dropSelf(AVPBlocks.TITANIUM_TREAD);
        dropSelf(AVPBlocks.TITANIUM_TREAD_STAIRS);
        dropSelf(AVPBlocks.TRINITITE_BLOCK);
        dropSelf(AVPBlocks.TRIP_MINE_BLOCK);
        dropSelf(AVPBlocks.URANIUM_BLOCK);
        dropSelf(AVPBlocks.ZINC_BLOCK);

        Stream.of(
            AVPBlocks.DYE_COLOR_TO_CONCRETE_STAIRS,
            AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC,
            AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_STAIRS,
            AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE,
            AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_STAIRS,
            AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_WALL,
            AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS,
            AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS_PANE,
            AVPBlocks.DYE_COLOR_TO_PADDING,
            AVPBlocks.DYE_COLOR_TO_PADDING_STAIRS,
            AVPBlocks.DYE_COLOR_TO_PANEL_PADDING,
            AVPBlocks.DYE_COLOR_TO_PANEL_PADDING_STAIRS,
            AVPBlocks.DYE_COLOR_TO_PIPE_PADDING,
            AVPBlocks.DYE_COLOR_TO_PIPE_PADDING_STAIRS,
            AVPBlocks.DYE_COLOR_TO_PLASTIC,
            AVPBlocks.DYE_COLOR_TO_PLASTIC_STAIRS
        )
            .map(Map::values)
            .flatMap(Collection::stream)
            .forEach(this::dropSelf);
    }

    private void generateSlabDrops() {
        dropSlab(AVPBlocks.CUT_FERROALUMINUM_SLAB);
        dropSlab(AVPBlocks.CUT_STEEL_SLAB);
        dropSlab(AVPBlocks.CUT_TITANIUM_SLAB);
        dropSlab(AVPBlocks.FERROALUMINUM_FASTENED_SIDING_SLAB);
        dropSlab(AVPBlocks.FERROALUMINUM_FASTENED_STANDING_SLAB);
        dropSlab(AVPBlocks.FERROALUMINUM_GRATE_SLAB);
        dropSlab(AVPBlocks.FERROALUMINUM_PLATING_SLAB);
        dropSlab(AVPBlocks.FERROALUMINUM_SIDING_SLAB);
        dropSlab(AVPBlocks.FERROALUMINUM_SLAB);
        dropSlab(AVPBlocks.FERROALUMINUM_STANDING_SLAB);
        dropSlab(AVPBlocks.FERROALUMINUM_TREAD_SLAB);
        dropSlab(AVPBlocks.INDUSTRIAL_GLASS_SLAB);
        dropSlab(AVPBlocks.STEEL_FASTENED_SIDING_SLAB);
        dropSlab(AVPBlocks.STEEL_FASTENED_STANDING_SLAB);
        dropSlab(AVPBlocks.STEEL_GRATE_SLAB);
        dropSlab(AVPBlocks.STEEL_PLATING_SLAB);
        dropSlab(AVPBlocks.STEEL_SIDING_SLAB);
        dropSlab(AVPBlocks.STEEL_SLAB);
        dropSlab(AVPBlocks.STEEL_STANDING_SLAB);
        dropSlab(AVPBlocks.STEEL_TREAD_SLAB);
        dropSlab(AVPBlocks.TITANIUM_FASTENED_SIDING_SLAB);
        dropSlab(AVPBlocks.TITANIUM_FASTENED_STANDING_SLAB);
        dropSlab(AVPBlocks.TITANIUM_GRATE_SLAB);
        dropSlab(AVPBlocks.TITANIUM_PLATING_SLAB);
        dropSlab(AVPBlocks.TITANIUM_SIDING_SLAB);
        dropSlab(AVPBlocks.TITANIUM_SLAB);
        dropSlab(AVPBlocks.TITANIUM_STANDING_SLAB);
        dropSlab(AVPBlocks.TITANIUM_TREAD_SLAB);

        Stream.of(
            AVPBlocks.DYE_COLOR_TO_CONCRETE_SLAB,
            AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_SLAB,
            AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_SLAB,
            AVPBlocks.DYE_COLOR_TO_PADDING_SLAB,
            AVPBlocks.DYE_COLOR_TO_PANEL_PADDING_SLAB,
            AVPBlocks.DYE_COLOR_TO_PIPE_PADDING_SLAB,
            AVPBlocks.DYE_COLOR_TO_PLASTIC_SLAB
        )
            .map(Map::values)
            .flatMap(Collection::stream)
            .forEach(this::dropSlab);
    }

    private void generateCustomDrops() {
        add(AVPBlocks.AUTUNITE_ORE, block -> createOreMultiDrop(block, AVPItems.AUTUNITE_DUST.get(), 2, 4));
        add(AVPBlocks.BAUXITE_ORE, block -> createOreDrop(block, AVPItems.RAW_BAUXITE.get()));
        add(AVPBlocks.DEEPSLATE_TITANIUM_ORE, block -> createOreDrop(block, AVPItems.RAW_TITANIUM.get()));
        add(AVPBlocks.DEEPSLATE_ZINC_ORE, block -> createOreMultiDrop(block, AVPItems.RAW_ZINC.get(), 2, 5));
        add(AVPBlocks.GALENA_ORE, block -> createOreDrop(block, AVPItems.RAW_GALENA.get()));
        add(AVPBlocks.LEAD_CHEST, this::createShulkerBoxDrop);
        add(AVPBlocks.AMMO_CHEST, this::createShulkerBoxDrop);
        add(AVPBlocks.LITHIUM_ORE, block -> createOreMultiDrop(block, AVPItems.LITHIUM_DUST.get(), 2, 4));
        add(AVPBlocks.MONAZITE_ORE, block -> createOreDrop(block, AVPItems.RAW_MONAZITE.get()));
        add(AVPBlocks.ZINC_ORE, block -> createOreMultiDrop(block, AVPItems.RAW_ZINC.get(), 2, 5));
        add(AVPBlocks.INDUSTRIAL_GLASS_DOOR, this::createDoorTable);
        add(AVPBlocks.FERROALUMINUM_DOOR, this::createDoorTable);
        add(AVPBlocks.STEEL_DOOR, this::createDoorTable);
        add(AVPBlocks.TITANIUM_DOOR, this::createDoorTable);
        add(AVPBlocks.INDUSTRIAL_FURNACE, this::createNameableBlockEntityTable);
    }

    private void generateOtherDrops() {
        dropOther(AlienBlocks.ABERRANT_RESIN_NODE, AlienBlocks.ABERRANT_RESIN);
        dropOther(AlienBlocks.ABERRANT_RESIN_VEIN, AlienItems.ABERRANT_RESIN_BALL);
        dropOther(AlienBlocks.ABERRANT_RESIN_WEB, AlienItems.ABERRANT_RESIN_BALL);
        dropOther(AlienBlocks.IRRADIATED_RESIN_NODE, AlienBlocks.IRRADIATED_RESIN);
        dropOther(AlienBlocks.IRRADIATED_RESIN_VEIN, AlienItems.IRRADIATED_RESIN_BALL);
        dropOther(AlienBlocks.IRRADIATED_RESIN_WEB, AlienItems.IRRADIATED_RESIN_BALL);
        dropOther(AlienBlocks.NETHER_RESIN_NODE, AlienBlocks.NETHER_RESIN);
        dropOther(AlienBlocks.NETHER_RESIN_VEIN, AlienItems.NETHER_RESIN_BALL);
        dropOther(AlienBlocks.NETHER_RESIN_WEB, AlienItems.NETHER_RESIN_BALL);
        dropOther(AlienBlocks.RESIN_NODE, AlienBlocks.RESIN);
        dropOther(AlienBlocks.RESIN_VEIN, AlienItems.RESIN_BALL);
        dropOther(AlienBlocks.RESIN_WEB, AlienItems.RESIN_BALL);
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
