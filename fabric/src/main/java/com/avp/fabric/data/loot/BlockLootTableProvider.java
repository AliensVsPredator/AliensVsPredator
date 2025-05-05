package com.avp.fabric.data.loot;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.avp.common.block.AVPBlocks;
import com.avp.common.item.AVPItems;

public class BlockLootTableProvider extends FabricBlockLootTableProvider {

    public BlockLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        generateSelfDrops();
        generateSlabDrops();
        generateCustomDrops();
        generateOtherDrops();
    }

    private void generateSelfDrops() {
        Stream.of(
            Stream.of(
                AVPBlocks.ABERRANT_RESIN,
                AVPBlocks.ALUMINUM_BLOCK,
                AVPBlocks.ASH_BLOCK,
                AVPBlocks.AUTUNITE_BLOCK,
                AVPBlocks.BLUEPRINT_BLOCK,
                AVPBlocks.BRASS_BLOCK,
                AVPBlocks.CHISELED_FERROALUMINUM,
                AVPBlocks.CHISELED_STEEL,
                AVPBlocks.CHISELED_TITANIUM,
                AVPBlocks.CUT_FERROALUMINUM,
                AVPBlocks.CUT_FERROALUMINUM_STAIRS,
                AVPBlocks.CUT_STEEL,
                AVPBlocks.CUT_STEEL_STAIRS,
                AVPBlocks.CUT_TITANIUM,
                AVPBlocks.CUT_TITANIUM_STAIRS,
                AVPBlocks.DESK_TERMINAL_BLOCK,
                AVPBlocks.FERROALUMINUM_BLOCK,
                AVPBlocks.FERROALUMINUM_BUTTON,
                AVPBlocks.FERROALUMINUM_CHAIN_FENCE,
                AVPBlocks.FERROALUMINUM_COLUMN,
                AVPBlocks.FERROALUMINUM_FASTENED_SIDING,
                AVPBlocks.FERROALUMINUM_FASTENED_SIDING_STAIRS,
                AVPBlocks.FERROALUMINUM_FASTENED_STANDING,
                AVPBlocks.FERROALUMINUM_FASTENED_STANDING_STAIRS,
                AVPBlocks.FERROALUMINUM_GRATE,
                AVPBlocks.FERROALUMINUM_GRATE_STAIRS,
                AVPBlocks.FERROALUMINUM_PLATING,
                AVPBlocks.FERROALUMINUM_PRESSURE_PLATE,
                AVPBlocks.FERROALUMINUM_SIDING,
                AVPBlocks.FERROALUMINUM_SIDING_STAIRS,
                AVPBlocks.FERROALUMINUM_STANDING,
                AVPBlocks.FERROALUMINUM_STANDING_STAIRS,
                AVPBlocks.FERROALUMINUM_TRAP_DOOR,
                AVPBlocks.FERROALUMINUM_TREAD,
                AVPBlocks.INDUSTRIAL_GLASS,
                AVPBlocks.INDUSTRIAL_GLASS_PANE,
                AVPBlocks.INDUSTRIAL_GLASS_STAIRS,
                AVPBlocks.INDUSTRIAL_GLASS_TRAP_DOOR,
                AVPBlocks.IRRADIATED_RESIN,
                AVPBlocks.LEAD_BLOCK,
                AVPBlocks.LITHIUM_BLOCK,
                AVPBlocks.NETHER_RESIN,
                AVPBlocks.NUKE_BLOCK,
                AVPBlocks.RAW_BAUXITE_BLOCK,
                AVPBlocks.RAW_GALENA_BLOCK,
                AVPBlocks.RAW_MONAZITE_BLOCK,
                AVPBlocks.RAW_SILICA_BLOCK,
                AVPBlocks.RAW_TITANIUM_BLOCK,
                AVPBlocks.RAW_ZINC_BLOCK,
                AVPBlocks.RAZOR_WIRE,
                AVPBlocks.REDSTONE_GENERATOR,
                AVPBlocks.RESIN,
                AVPBlocks.RESIN_BRICKS,
                AVPBlocks.RESIN_O,
                AVPBlocks.RESIN_RIBBED,
                AVPBlocks.RESIN_SMOOTH,
                AVPBlocks.RESONATOR_BLOCK,
                AVPBlocks.ROYAL_JELLY_BLOCK,
                AVPBlocks.SENTRY_TURRET,
                AVPBlocks.SILICA_GRAVEL,
                AVPBlocks.STEEL_BARS,
                AVPBlocks.STEEL_BLOCK,
                AVPBlocks.STEEL_BUTTON,
                AVPBlocks.STEEL_CHAIN_FENCE,
                AVPBlocks.STEEL_COLUMN,
                AVPBlocks.STEEL_FASTENED_SIDING,
                AVPBlocks.STEEL_FASTENED_SIDING_STAIRS,
                AVPBlocks.STEEL_FASTENED_STANDING,
                AVPBlocks.STEEL_FASTENED_STANDING_STAIRS,
                AVPBlocks.STEEL_GRATE,
                AVPBlocks.STEEL_GRATE_STAIRS,
                AVPBlocks.STEEL_PLATING,
                AVPBlocks.STEEL_PRESSURE_PLATE,
                AVPBlocks.STEEL_SIDING,
                AVPBlocks.STEEL_SIDING_STAIRS,
                AVPBlocks.STEEL_STANDING,
                AVPBlocks.STEEL_STANDING_STAIRS,
                AVPBlocks.STEEL_TRAP_DOOR,
                AVPBlocks.STEEL_TREAD,
                AVPBlocks.TITANIUM_BLOCK,
                AVPBlocks.TITANIUM_BUTTON,
                AVPBlocks.TITANIUM_CHAIN_FENCE,
                AVPBlocks.TITANIUM_COLUMN,
                AVPBlocks.TITANIUM_FASTENED_SIDING,
                AVPBlocks.TITANIUM_FASTENED_SIDING_STAIRS,
                AVPBlocks.TITANIUM_FASTENED_STANDING,
                AVPBlocks.TITANIUM_FASTENED_STANDING_STAIRS,
                AVPBlocks.TITANIUM_GRATE,
                AVPBlocks.TITANIUM_GRATE_STAIRS,
                AVPBlocks.TITANIUM_PLATING,
                AVPBlocks.TITANIUM_PRESSURE_PLATE,
                AVPBlocks.TITANIUM_SIDING,
                AVPBlocks.TITANIUM_SIDING_STAIRS,
                AVPBlocks.TITANIUM_STANDING,
                AVPBlocks.TITANIUM_STANDING_STAIRS,
                AVPBlocks.TITANIUM_TRAP_DOOR,
                AVPBlocks.TITANIUM_TREAD,
                AVPBlocks.TRINITITE_BLOCK,
                AVPBlocks.TRIP_MINE_BLOCK,
                AVPBlocks.URANIUM_BLOCK,
                AVPBlocks.ZINC_BLOCK
            ),
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
        )
            .flatMap(Function.identity())
            .map(Supplier::get)
            .forEach(this::dropSelf);
    }

    private void generateSlabDrops() {
        Stream.of(
            Stream.of(
                AVPBlocks.CUT_FERROALUMINUM_SLAB,
                AVPBlocks.CUT_STEEL_SLAB,
                AVPBlocks.CUT_TITANIUM_SLAB,
                AVPBlocks.FERROALUMINUM_FASTENED_SIDING_SLAB,
                AVPBlocks.FERROALUMINUM_FASTENED_STANDING_SLAB,
                AVPBlocks.FERROALUMINUM_GRATE_SLAB,
                AVPBlocks.FERROALUMINUM_SIDING_SLAB,
                AVPBlocks.FERROALUMINUM_STANDING_SLAB,
                AVPBlocks.INDUSTRIAL_GLASS_SLAB,
                AVPBlocks.STEEL_FASTENED_SIDING_SLAB,
                AVPBlocks.STEEL_FASTENED_STANDING_SLAB,
                AVPBlocks.STEEL_GRATE_SLAB,
                AVPBlocks.STEEL_SIDING_SLAB,
                AVPBlocks.STEEL_STANDING_SLAB,
                AVPBlocks.TITANIUM_FASTENED_SIDING_SLAB,
                AVPBlocks.TITANIUM_FASTENED_STANDING_SLAB,
                AVPBlocks.TITANIUM_GRATE_SLAB,
                AVPBlocks.TITANIUM_SIDING_SLAB,
                AVPBlocks.TITANIUM_STANDING_SLAB
            ),
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
        )
            .flatMap(Function.identity())
            .map(Supplier::get)
            .forEach(this::dropSlab);
    }

    private void generateCustomDrops() {
        add(AVPBlocks.AUTUNITE_ORE.get(), block -> createOreMultiDrop(block, AVPItems.AUTUNITE_DUST.get(), 2, 4));
        add(AVPBlocks.BAUXITE_ORE.get(), block -> createOreDrop(block, AVPItems.RAW_BAUXITE.get()));
        add(AVPBlocks.DEEPSLATE_TITANIUM_ORE.get(), block -> createOreDrop(block, AVPItems.RAW_TITANIUM.get()));
        add(AVPBlocks.DEEPSLATE_ZINC_ORE.get(), block -> createOreMultiDrop(block, AVPItems.RAW_ZINC.get(), 2, 5));
        add(AVPBlocks.GALENA_ORE.get(), block -> createOreDrop(block, AVPItems.RAW_GALENA.get()));
        add(AVPBlocks.LEAD_CHEST.get(), this::createShulkerBoxDrop);
        add(AVPBlocks.AMMO_CHEST.get(), this::createShulkerBoxDrop);
        add(AVPBlocks.LITHIUM_ORE.get(), block -> createOreMultiDrop(block, AVPItems.LITHIUM_DUST.get(), 2, 4));
        add(AVPBlocks.MONAZITE_ORE.get(), block -> createOreDrop(block, AVPItems.RAW_MONAZITE.get()));
        add(AVPBlocks.ZINC_ORE.get(), block -> createOreMultiDrop(block, AVPItems.RAW_ZINC.get(), 2, 5));
        add(AVPBlocks.INDUSTRIAL_GLASS_DOOR.get(), this::createDoorTable);
        add(AVPBlocks.FERROALUMINUM_DOOR.get(), this::createDoorTable);
        add(AVPBlocks.STEEL_DOOR.get(), this::createDoorTable);
        add(AVPBlocks.TITANIUM_DOOR.get(), this::createDoorTable);
        add(AVPBlocks.INDUSTRIAL_FURNACE.get(), this::createNameableBlockEntityTable);
    }

    private void generateOtherDrops() {
        dropOther(AVPBlocks.ABERRANT_RESIN_NODE.get(), AVPBlocks.ABERRANT_RESIN.get());
        dropOther(AVPBlocks.ABERRANT_RESIN_VEIN.get(), AVPItems.ABERRANT_RESIN_BALL.get());
        dropOther(AVPBlocks.ABERRANT_RESIN_WEB.get(), AVPItems.ABERRANT_RESIN_BALL.get());
        dropOther(AVPBlocks.IRRADIATED_RESIN_NODE.get(), AVPBlocks.IRRADIATED_RESIN.get());
        dropOther(AVPBlocks.IRRADIATED_RESIN_VEIN.get(), AVPItems.IRRADIATED_RESIN_BALL.get());
        dropOther(AVPBlocks.IRRADIATED_RESIN_WEB.get(), AVPItems.IRRADIATED_RESIN_BALL.get());
        dropOther(AVPBlocks.NETHER_RESIN_NODE.get(), AVPBlocks.NETHER_RESIN.get());
        dropOther(AVPBlocks.NETHER_RESIN_VEIN.get(), AVPItems.NETHER_RESIN_BALL.get());
        dropOther(AVPBlocks.NETHER_RESIN_WEB.get(), AVPItems.NETHER_RESIN_BALL.get());
        dropOther(AVPBlocks.RESIN_NODE.get(), AVPBlocks.RESIN.get());
        dropOther(AVPBlocks.RESIN_VEIN.get(), AVPItems.RESIN_BALL.get());
        dropOther(AVPBlocks.RESIN_WEB.get(), AVPItems.RESIN_BALL.get());
    }

    public void dropSlab(Block block) {
        add(block, createSlabItemTable(block));
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
