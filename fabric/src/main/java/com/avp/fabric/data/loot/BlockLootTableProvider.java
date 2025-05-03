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

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import com.avp.common.block.AVPBlocks;
import com.avp.common.item.AVPItems;

public class BlockLootTableProvider extends FabricBlockLootTableProvider {

    public BlockLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        dropSelf(AVPBlocks.BLUEPRINT_BLOCK.get());
        dropSelf(AVPBlocks.REDSTONE_GENERATOR.get());
        dropSelf(AVPBlocks.DESK_TERMINAL_BLOCK.get());
        dropSelf(AVPBlocks.TRIP_MINE_BLOCK.get());
        dropSelf(AVPBlocks.RESONATOR_BLOCK.get());
        dropSelf(AVPBlocks.SENTRY_TURRET.get());
        dropSelf(AVPBlocks.TRINITITE_BLOCK.get());
        dropSelf(AVPBlocks.ASH_BLOCK.get());
        dropSelf(AVPBlocks.NUKE_BLOCK.get());
        dropSelf(AVPBlocks.ROYAL_JELLY_BLOCK.get());
        dropSelf(AVPBlocks.ALUMINUM_BLOCK.get());
        dropSelf(AVPBlocks.AUTUNITE_BLOCK.get());
        add(AVPBlocks.AUTUNITE_ORE.get(), block -> createOreMultiDrop(block, AVPItems.AUTUNITE_DUST.get(), 2, 4));
        add(AVPBlocks.BAUXITE_ORE.get(), block -> createOreDrop(block, AVPItems.RAW_BAUXITE.get()));
        dropSelf(AVPBlocks.BRASS_BLOCK.get());
        dropSelf(AVPBlocks.CHISELED_FERROALUMINUM.get());
        dropSelf(AVPBlocks.CHISELED_STEEL.get());
        dropSelf(AVPBlocks.CHISELED_TITANIUM.get());
        dropSelf(AVPBlocks.CUT_FERROALUMINUM.get());
        dropSlab(AVPBlocks.CUT_FERROALUMINUM_SLAB.get());
        dropSelf(AVPBlocks.CUT_FERROALUMINUM_STAIRS.get());
        dropSelf(AVPBlocks.CUT_STEEL.get());
        dropSlab(AVPBlocks.CUT_STEEL_SLAB.get());
        dropSelf(AVPBlocks.CUT_STEEL_STAIRS.get());
        dropSelf(AVPBlocks.CUT_TITANIUM.get());
        dropSlab(AVPBlocks.CUT_TITANIUM_SLAB.get());
        dropSelf(AVPBlocks.CUT_TITANIUM_STAIRS.get());
        add(AVPBlocks.DEEPSLATE_TITANIUM_ORE.get(), block -> createOreDrop(block, AVPItems.RAW_TITANIUM.get()));
        add(AVPBlocks.DEEPSLATE_ZINC_ORE.get(), block -> createOreMultiDrop(block, AVPItems.RAW_ZINC.get(), 2, 5));
        dropSelf(AVPBlocks.FERROALUMINUM_BLOCK.get());
        dropSelf(AVPBlocks.FERROALUMINUM_CHAIN_FENCE.get());
        dropSelf(AVPBlocks.FERROALUMINUM_COLUMN.get());
        dropSelf(AVPBlocks.FERROALUMINUM_FASTENED_SIDING.get());
        dropSelf(AVPBlocks.FERROALUMINUM_FASTENED_STANDING.get());
        dropSelf(AVPBlocks.FERROALUMINUM_GRATE.get());
        dropSelf(AVPBlocks.FERROALUMINUM_PLATING.get());
        dropSelf(AVPBlocks.FERROALUMINUM_SIDING.get());
        dropSelf(AVPBlocks.FERROALUMINUM_STANDING.get());
        dropSelf(AVPBlocks.FERROALUMINUM_TREAD.get());
        add(AVPBlocks.GALENA_ORE.get(), block -> createOreDrop(block, AVPItems.RAW_GALENA.get()));
        dropSelf(AVPBlocks.LEAD_BLOCK.get());
        add(AVPBlocks.LEAD_CHEST.get(), this::createShulkerBoxDrop);
        add(AVPBlocks.AMMO_CHEST.get(), this::createShulkerBoxDrop);
        dropSelf(AVPBlocks.LITHIUM_BLOCK.get());
        add(AVPBlocks.LITHIUM_ORE.get(), block -> createOreMultiDrop(block, AVPItems.LITHIUM_DUST.get(), 2, 4));
        add(AVPBlocks.MONAZITE_ORE.get(), block -> createOreDrop(block, AVPItems.RAW_MONAZITE.get()));
        dropSelf(AVPBlocks.NETHER_RESIN.get());
        dropOther(AVPBlocks.NETHER_RESIN_NODE.get(), AVPBlocks.NETHER_RESIN.get());
        dropOther(AVPBlocks.NETHER_RESIN_VEIN.get(), AVPItems.NETHER_RESIN_BALL.get());
        dropOther(AVPBlocks.NETHER_RESIN_WEB.get(), AVPItems.NETHER_RESIN_BALL.get());
        dropSelf(AVPBlocks.ABERRANT_RESIN.get());
        dropOther(AVPBlocks.ABERRANT_RESIN_NODE.get(), AVPBlocks.ABERRANT_RESIN.get());
        dropOther(AVPBlocks.ABERRANT_RESIN_VEIN.get(), AVPItems.ABERRANT_RESIN_BALL.get());
        dropOther(AVPBlocks.ABERRANT_RESIN_WEB.get(), AVPItems.ABERRANT_RESIN_BALL.get());
        dropSelf(AVPBlocks.IRRADIATED_RESIN.get());
        dropOther(AVPBlocks.IRRADIATED_RESIN_NODE.get(), AVPBlocks.IRRADIATED_RESIN.get());
        dropOther(AVPBlocks.IRRADIATED_RESIN_VEIN.get(), AVPItems.IRRADIATED_RESIN_BALL.get());
        dropOther(AVPBlocks.IRRADIATED_RESIN_WEB.get(), AVPItems.IRRADIATED_RESIN_BALL.get());
        dropSelf(AVPBlocks.RESIN_BRICKS.get());
        dropSelf(AVPBlocks.RESIN_O.get());
        dropSelf(AVPBlocks.RESIN_RIBBED.get());
        dropSelf(AVPBlocks.RESIN_SMOOTH.get());

        // TODO: Use stream concat here.
        AVPBlocks.DYE_COLOR_TO_CONCRETE_SLAB.values().stream().map(Supplier::get).forEach(this::dropSlab);
        AVPBlocks.DYE_COLOR_TO_CONCRETE_STAIRS.values().stream().map(Supplier::get).forEach(this::dropSelf);

        // TODO: Use stream concat here.
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE.values().stream().map(Supplier::get).forEach(this::dropSelf);
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_SLAB.values().stream().map(Supplier::get).forEach(this::dropSlab);
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_STAIRS.values().stream().map(Supplier::get).forEach(this::dropSelf);
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_WALL.values().stream().map(Supplier::get).forEach(this::dropSelf);
        dropSelf(AVPBlocks.INDUSTRIAL_GLASS.get());
        dropSelf(AVPBlocks.INDUSTRIAL_GLASS_PANE.get());
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS.values().stream().map(Supplier::get).forEach(this::dropSelf);
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS_PANE.values().stream().map(Supplier::get).forEach(this::dropSelf);

        // TODO: Use stream concat here.
        AVPBlocks.DYE_COLOR_TO_PADDING.values().stream().map(Supplier::get).forEach(this::dropSelf);
        AVPBlocks.DYE_COLOR_TO_PADDING_SLAB.values().stream().map(Supplier::get).forEach(this::dropSlab);
        AVPBlocks.DYE_COLOR_TO_PADDING_STAIRS.values().stream().map(Supplier::get).forEach(this::dropSelf);

        // TODO: Use stream concat here.
        AVPBlocks.DYE_COLOR_TO_PANEL_PADDING.values().stream().map(Supplier::get).forEach(this::dropSelf);
        AVPBlocks.DYE_COLOR_TO_PANEL_PADDING_SLAB.values().stream().map(Supplier::get).forEach(this::dropSlab);
        AVPBlocks.DYE_COLOR_TO_PANEL_PADDING_STAIRS.values().stream().map(Supplier::get).forEach(this::dropSelf);

        // TODO: Use stream concat here.
        AVPBlocks.DYE_COLOR_TO_PIPE_PADDING.values().stream().map(Supplier::get).forEach(this::dropSelf);
        AVPBlocks.DYE_COLOR_TO_PIPE_PADDING_SLAB.values().stream().map(Supplier::get).forEach(this::dropSlab);
        AVPBlocks.DYE_COLOR_TO_PIPE_PADDING_STAIRS.values().stream().map(Supplier::get).forEach(this::dropSelf);

        // TODO: Use stream concat here.
        AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC.values().stream().map(Supplier::get).forEach(this::dropSelf);
        AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_SLAB.values().stream().map(Supplier::get).forEach(this::dropSlab);
        AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_STAIRS.values().stream().map(Supplier::get).forEach(this::dropSelf);

        // TODO: Use stream concat here.
        AVPBlocks.DYE_COLOR_TO_PLASTIC.values().stream().map(Supplier::get).forEach(this::dropSelf);
        AVPBlocks.DYE_COLOR_TO_PLASTIC_SLAB.values().stream().map(Supplier::get).forEach(this::dropSlab);
        AVPBlocks.DYE_COLOR_TO_PLASTIC_STAIRS.values().stream().map(Supplier::get).forEach(this::dropSelf);

        dropSelf(AVPBlocks.RAW_BAUXITE_BLOCK.get());
        dropSelf(AVPBlocks.RAW_GALENA_BLOCK.get());
        dropSelf(AVPBlocks.RAW_MONAZITE_BLOCK.get());
        dropSelf(AVPBlocks.RAW_SILICA_BLOCK.get());
        dropSelf(AVPBlocks.RAW_TITANIUM_BLOCK.get());
        dropSelf(AVPBlocks.RAW_ZINC_BLOCK.get());
        dropSelf(AVPBlocks.RAZOR_WIRE.get());
        dropSelf(AVPBlocks.RESIN.get());
        dropOther(AVPBlocks.RESIN_NODE.get(), AVPBlocks.RESIN.get());
        dropOther(AVPBlocks.RESIN_VEIN.get(), AVPItems.RESIN_BALL.get());
        dropOther(AVPBlocks.RESIN_WEB.get(), AVPItems.RESIN_BALL.get());
        dropSelf(AVPBlocks.SILICA_GRAVEL.get());
        dropSelf(AVPBlocks.STEEL_BARS.get());
        dropSelf(AVPBlocks.STEEL_BLOCK.get());
        dropSelf(AVPBlocks.STEEL_CHAIN_FENCE.get());
        dropSelf(AVPBlocks.STEEL_COLUMN.get());
        dropSelf(AVPBlocks.STEEL_FASTENED_SIDING.get());
        dropSelf(AVPBlocks.STEEL_FASTENED_STANDING.get());
        dropSelf(AVPBlocks.STEEL_GRATE.get());
        dropSelf(AVPBlocks.STEEL_PLATING.get());
        dropSelf(AVPBlocks.STEEL_SIDING.get());
        dropSelf(AVPBlocks.STEEL_STANDING.get());
        dropSelf(AVPBlocks.STEEL_TREAD.get());
        dropSelf(AVPBlocks.TITANIUM_BLOCK.get());
        dropSelf(AVPBlocks.TITANIUM_CHAIN_FENCE.get());
        dropSelf(AVPBlocks.TITANIUM_COLUMN.get());
        dropSelf(AVPBlocks.TITANIUM_FASTENED_SIDING.get());
        dropSelf(AVPBlocks.TITANIUM_FASTENED_STANDING.get());
        dropSelf(AVPBlocks.TITANIUM_GRATE.get());
        dropSelf(AVPBlocks.TITANIUM_PLATING.get());
        dropSelf(AVPBlocks.TITANIUM_SIDING.get());
        dropSelf(AVPBlocks.TITANIUM_STANDING.get());
        dropSelf(AVPBlocks.TITANIUM_TREAD.get());
        dropSelf(AVPBlocks.URANIUM_BLOCK.get());
        dropSelf(AVPBlocks.ZINC_BLOCK.get());
        add(AVPBlocks.ZINC_ORE.get(), block -> createOreMultiDrop(block, AVPItems.RAW_ZINC.get(), 2, 5));

        add(AVPBlocks.INDUSTRIAL_GLASS_DOOR.get(), this::createDoorTable);
        add(AVPBlocks.FERROALUMINUM_DOOR.get(), this::createDoorTable);
        add(AVPBlocks.STEEL_DOOR.get(), this::createDoorTable);
        add(AVPBlocks.TITANIUM_DOOR.get(), this::createDoorTable);

        dropSelf(AVPBlocks.INDUSTRIAL_GLASS_TRAP_DOOR.get());
        dropSelf(AVPBlocks.FERROALUMINUM_TRAP_DOOR.get());
        dropSelf(AVPBlocks.STEEL_TRAP_DOOR.get());
        dropSelf(AVPBlocks.TITANIUM_TRAP_DOOR.get());

        dropSelf(AVPBlocks.STEEL_PRESSURE_PLATE.get());
        dropSelf(AVPBlocks.TITANIUM_PRESSURE_PLATE.get());
        dropSelf(AVPBlocks.FERROALUMINUM_PRESSURE_PLATE.get());

        dropSelf(AVPBlocks.STEEL_BUTTON.get());
        dropSelf(AVPBlocks.TITANIUM_BUTTON.get());
        dropSelf(AVPBlocks.FERROALUMINUM_BUTTON.get());

        dropSlab(AVPBlocks.INDUSTRIAL_GLASS_SLAB.get());
        dropSlab(AVPBlocks.FERROALUMINUM_FASTENED_SIDING_SLAB.get());
        dropSlab(AVPBlocks.FERROALUMINUM_FASTENED_STANDING_SLAB.get());
        dropSlab(AVPBlocks.FERROALUMINUM_GRATE_SLAB.get());
        dropSlab(AVPBlocks.FERROALUMINUM_SIDING_SLAB.get());
        dropSlab(AVPBlocks.FERROALUMINUM_STANDING_SLAB.get());
        dropSlab(AVPBlocks.STEEL_GRATE_SLAB.get());
        dropSlab(AVPBlocks.STEEL_SIDING_SLAB.get());
        dropSlab(AVPBlocks.STEEL_STANDING_SLAB.get());
        dropSlab(AVPBlocks.STEEL_FASTENED_SIDING_SLAB.get());
        dropSlab(AVPBlocks.STEEL_FASTENED_STANDING_SLAB.get());
        dropSlab(AVPBlocks.TITANIUM_GRATE_SLAB.get());
        dropSlab(AVPBlocks.TITANIUM_SIDING_SLAB.get());
        dropSlab(AVPBlocks.TITANIUM_STANDING_SLAB.get());
        dropSlab(AVPBlocks.TITANIUM_FASTENED_SIDING_SLAB.get());
        dropSlab(AVPBlocks.TITANIUM_FASTENED_STANDING_SLAB.get());

        dropSelf(AVPBlocks.INDUSTRIAL_GLASS_STAIRS.get());
        dropSelf(AVPBlocks.FERROALUMINUM_FASTENED_SIDING_STAIRS.get());
        dropSelf(AVPBlocks.FERROALUMINUM_FASTENED_STANDING_STAIRS.get());
        dropSelf(AVPBlocks.FERROALUMINUM_GRATE_STAIRS.get());
        dropSelf(AVPBlocks.FERROALUMINUM_SIDING_STAIRS.get());
        dropSelf(AVPBlocks.FERROALUMINUM_STANDING_STAIRS.get());
        dropSelf(AVPBlocks.STEEL_GRATE_STAIRS.get());
        dropSelf(AVPBlocks.STEEL_SIDING_STAIRS.get());
        dropSelf(AVPBlocks.STEEL_STANDING_STAIRS.get());
        dropSelf(AVPBlocks.STEEL_FASTENED_SIDING_STAIRS.get());
        dropSelf(AVPBlocks.STEEL_FASTENED_STANDING_STAIRS.get());
        dropSelf(AVPBlocks.TITANIUM_GRATE_STAIRS.get());
        dropSelf(AVPBlocks.TITANIUM_SIDING_STAIRS.get());
        dropSelf(AVPBlocks.TITANIUM_STANDING_STAIRS.get());
        dropSelf(AVPBlocks.TITANIUM_FASTENED_SIDING_STAIRS.get());
        dropSelf(AVPBlocks.TITANIUM_FASTENED_STANDING_STAIRS.get());

        add(AVPBlocks.INDUSTRIAL_FURNACE.get(), this::createNameableBlockEntityTable);
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
