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

import com.avp.common.block.TempAVPBlocks;
import com.avp.common.item.TempAVPItems;
import com.avp.fabric.common.block.AVPBlocks;

public class BlockLootTableProvider extends FabricBlockLootTableProvider {

    public BlockLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        dropSelf(TempAVPBlocks.BLUEPRINT_BLOCK.get());
        dropSelf(TempAVPBlocks.REDSTONE_GENERATOR.get());
        dropSelf(TempAVPBlocks.DESK_TERMINAL_BLOCK.get());
        dropSelf(TempAVPBlocks.TRIP_MINE_BLOCK.get());
        dropSelf(TempAVPBlocks.RESONATOR_BLOCK.get());
        dropSelf(TempAVPBlocks.SENTRY_TURRET.get());
        dropSelf(TempAVPBlocks.TRINITITE_BLOCK.get());
        dropSelf(TempAVPBlocks.ASH_BLOCK.get());
        dropSelf(AVPBlocks.NUKE_BLOCK);
        dropSelf(TempAVPBlocks.ROYAL_JELLY_BLOCK.get());
        dropSelf(TempAVPBlocks.ALUMINUM_BLOCK.get());
        dropSelf(TempAVPBlocks.AUTUNITE_BLOCK.get());
        add(TempAVPBlocks.AUTUNITE_ORE.get(), block -> createOreMultiDrop(block, TempAVPItems.AUTUNITE_DUST.get(), 2, 4));
        add(TempAVPBlocks.BAUXITE_ORE.get(), block -> createOreDrop(block, TempAVPItems.RAW_BAUXITE.get()));
        dropSelf(TempAVPBlocks.BRASS_BLOCK.get());
        dropSelf(TempAVPBlocks.CHISELED_FERROALUMINUM.get());
        dropSelf(TempAVPBlocks.CHISELED_STEEL.get());
        dropSelf(TempAVPBlocks.CHISELED_TITANIUM.get());
        dropSelf(TempAVPBlocks.CUT_FERROALUMINUM.get());
        dropSlab(TempAVPBlocks.CUT_FERROALUMINUM_SLAB.get());
        dropSelf(TempAVPBlocks.CUT_FERROALUMINUM_STAIRS.get());
        dropSelf(TempAVPBlocks.CUT_STEEL.get());
        dropSlab(TempAVPBlocks.CUT_STEEL_SLAB.get());
        dropSelf(TempAVPBlocks.CUT_STEEL_STAIRS.get());
        dropSelf(TempAVPBlocks.CUT_TITANIUM.get());
        dropSlab(TempAVPBlocks.CUT_TITANIUM_SLAB.get());
        dropSelf(TempAVPBlocks.CUT_TITANIUM_STAIRS.get());
        add(TempAVPBlocks.DEEPSLATE_TITANIUM_ORE.get(), block -> createOreDrop(block, TempAVPItems.RAW_TITANIUM.get()));
        add(TempAVPBlocks.DEEPSLATE_ZINC_ORE.get(), block -> createOreMultiDrop(block, TempAVPItems.RAW_ZINC.get(), 2, 5));
        dropSelf(TempAVPBlocks.FERROALUMINUM_BLOCK.get());
        dropSelf(TempAVPBlocks.FERROALUMINUM_CHAIN_FENCE.get());
        dropSelf(TempAVPBlocks.FERROALUMINUM_COLUMN.get());
        dropSelf(TempAVPBlocks.FERROALUMINUM_FASTENED_SIDING.get());
        dropSelf(TempAVPBlocks.FERROALUMINUM_FASTENED_STANDING.get());
        dropSelf(TempAVPBlocks.FERROALUMINUM_GRATE.get());
        dropSelf(TempAVPBlocks.FERROALUMINUM_PLATING.get());
        dropSelf(TempAVPBlocks.FERROALUMINUM_SIDING.get());
        dropSelf(TempAVPBlocks.FERROALUMINUM_STANDING.get());
        dropSelf(TempAVPBlocks.FERROALUMINUM_TREAD.get());
        add(TempAVPBlocks.GALENA_ORE.get(), block -> createOreDrop(block, TempAVPItems.RAW_GALENA.get()));
        dropSelf(TempAVPBlocks.LEAD_BLOCK.get());
        add(TempAVPBlocks.LEAD_CHEST.get(), this::createShulkerBoxDrop);
        add(TempAVPBlocks.AMMO_CHEST.get(), this::createShulkerBoxDrop);
        dropSelf(TempAVPBlocks.LITHIUM_BLOCK.get());
        add(TempAVPBlocks.LITHIUM_ORE.get(), block -> createOreMultiDrop(block, TempAVPItems.LITHIUM_DUST.get(), 2, 4));
        add(TempAVPBlocks.MONAZITE_ORE.get(), block -> createOreDrop(block, TempAVPItems.RAW_MONAZITE.get()));
        dropSelf(TempAVPBlocks.NETHER_RESIN.get());
        dropOther(TempAVPBlocks.NETHER_RESIN_NODE.get(), TempAVPBlocks.NETHER_RESIN.get());
        dropOther(TempAVPBlocks.NETHER_RESIN_VEIN.get(), TempAVPItems.NETHER_RESIN_BALL.get());
        dropOther(TempAVPBlocks.NETHER_RESIN_WEB.get(), TempAVPItems.NETHER_RESIN_BALL.get());
        dropSelf(TempAVPBlocks.ABERRANT_RESIN.get());
        dropOther(TempAVPBlocks.ABERRANT_RESIN_NODE.get(), TempAVPBlocks.ABERRANT_RESIN.get());
        dropOther(TempAVPBlocks.ABERRANT_RESIN_VEIN.get(), TempAVPItems.ABERRANT_RESIN_BALL.get());
        dropOther(TempAVPBlocks.ABERRANT_RESIN_WEB.get(), TempAVPItems.ABERRANT_RESIN_BALL.get());
        dropSelf(TempAVPBlocks.IRRADIATED_RESIN.get());
        dropOther(TempAVPBlocks.IRRADIATED_RESIN_NODE.get(), TempAVPBlocks.IRRADIATED_RESIN.get());
        dropOther(TempAVPBlocks.IRRADIATED_RESIN_VEIN.get(), TempAVPItems.IRRADIATED_RESIN_BALL.get());
        dropOther(TempAVPBlocks.IRRADIATED_RESIN_WEB.get(), TempAVPItems.IRRADIATED_RESIN_BALL.get());
        dropSelf(TempAVPBlocks.RESIN_BRICKS.get());
        dropSelf(TempAVPBlocks.RESIN_O.get());
        dropSelf(TempAVPBlocks.RESIN_RIBBED.get());
        dropSelf(TempAVPBlocks.RESIN_SMOOTH.get());

        // TODO: Use stream concat here.
        TempAVPBlocks.DYE_COLOR_TO_CONCRETE_SLAB.values().stream().map(Supplier::get).forEach(this::dropSlab);
        TempAVPBlocks.DYE_COLOR_TO_CONCRETE_STAIRS.values().stream().map(Supplier::get).forEach(this::dropSelf);

        // TODO: Use stream concat here.
        TempAVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE.values().stream().map(Supplier::get).forEach(this::dropSelf);
        TempAVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_SLAB.values().stream().map(Supplier::get).forEach(this::dropSlab);
        TempAVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_STAIRS.values().stream().map(Supplier::get).forEach(this::dropSelf);
        TempAVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_WALL.values().stream().map(Supplier::get).forEach(this::dropSelf);
        dropSelf(TempAVPBlocks.INDUSTRIAL_GLASS.get());
        dropSelf(TempAVPBlocks.INDUSTRIAL_GLASS_PANE.get());
        TempAVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS.values().stream().map(Supplier::get).forEach(this::dropSelf);
        TempAVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS_PANE.values().stream().map(Supplier::get).forEach(this::dropSelf);

        // TODO: Use stream concat here.
        TempAVPBlocks.DYE_COLOR_TO_PADDING.values().stream().map(Supplier::get).forEach(this::dropSelf);
        TempAVPBlocks.DYE_COLOR_TO_PADDING_SLAB.values().stream().map(Supplier::get).forEach(this::dropSlab);
        TempAVPBlocks.DYE_COLOR_TO_PADDING_STAIRS.values().stream().map(Supplier::get).forEach(this::dropSelf);

        // TODO: Use stream concat here.
        TempAVPBlocks.DYE_COLOR_TO_PANEL_PADDING.values().stream().map(Supplier::get).forEach(this::dropSelf);
        TempAVPBlocks.DYE_COLOR_TO_PANEL_PADDING_SLAB.values().stream().map(Supplier::get).forEach(this::dropSlab);
        TempAVPBlocks.DYE_COLOR_TO_PANEL_PADDING_STAIRS.values().stream().map(Supplier::get).forEach(this::dropSelf);

        // TODO: Use stream concat here.
        TempAVPBlocks.DYE_COLOR_TO_PIPE_PADDING.values().stream().map(Supplier::get).forEach(this::dropSelf);
        TempAVPBlocks.DYE_COLOR_TO_PIPE_PADDING_SLAB.values().stream().map(Supplier::get).forEach(this::dropSlab);
        TempAVPBlocks.DYE_COLOR_TO_PIPE_PADDING_STAIRS.values().stream().map(Supplier::get).forEach(this::dropSelf);

        // TODO: Use stream concat here.
        TempAVPBlocks.DYE_COLOR_TO_CUT_PLASTIC.values().stream().map(Supplier::get).forEach(this::dropSelf);
        TempAVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_SLAB.values().stream().map(Supplier::get).forEach(this::dropSlab);
        TempAVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_STAIRS.values().stream().map(Supplier::get).forEach(this::dropSelf);

        // TODO: Use stream concat here.
        TempAVPBlocks.DYE_COLOR_TO_PLASTIC.values().stream().map(Supplier::get).forEach(this::dropSelf);
        TempAVPBlocks.DYE_COLOR_TO_PLASTIC_SLAB.values().stream().map(Supplier::get).forEach(this::dropSlab);
        TempAVPBlocks.DYE_COLOR_TO_PLASTIC_STAIRS.values().stream().map(Supplier::get).forEach(this::dropSelf);

        dropSelf(TempAVPBlocks.RAW_BAUXITE_BLOCK.get());
        dropSelf(TempAVPBlocks.RAW_GALENA_BLOCK.get());
        dropSelf(TempAVPBlocks.RAW_MONAZITE_BLOCK.get());
        dropSelf(TempAVPBlocks.RAW_SILICA_BLOCK.get());
        dropSelf(TempAVPBlocks.RAW_TITANIUM_BLOCK.get());
        dropSelf(TempAVPBlocks.RAW_ZINC_BLOCK.get());
        dropSelf(TempAVPBlocks.RAZOR_WIRE.get());
        dropSelf(TempAVPBlocks.RESIN.get());
        dropOther(TempAVPBlocks.RESIN_NODE.get(), TempAVPBlocks.RESIN.get());
        dropOther(TempAVPBlocks.RESIN_VEIN.get(), TempAVPItems.RESIN_BALL.get());
        dropOther(TempAVPBlocks.RESIN_WEB.get(), TempAVPItems.RESIN_BALL.get());
        dropSelf(TempAVPBlocks.SILICA_GRAVEL.get());
        dropSelf(TempAVPBlocks.STEEL_BARS.get());
        dropSelf(TempAVPBlocks.STEEL_BLOCK.get());
        dropSelf(TempAVPBlocks.STEEL_CHAIN_FENCE.get());
        dropSelf(TempAVPBlocks.STEEL_COLUMN.get());
        dropSelf(TempAVPBlocks.STEEL_FASTENED_SIDING.get());
        dropSelf(TempAVPBlocks.STEEL_FASTENED_STANDING.get());
        dropSelf(TempAVPBlocks.STEEL_GRATE.get());
        dropSelf(TempAVPBlocks.STEEL_PLATING.get());
        dropSelf(TempAVPBlocks.STEEL_SIDING.get());
        dropSelf(TempAVPBlocks.STEEL_STANDING.get());
        dropSelf(TempAVPBlocks.STEEL_TREAD.get());
        dropSelf(TempAVPBlocks.TITANIUM_BLOCK.get());
        dropSelf(TempAVPBlocks.TITANIUM_CHAIN_FENCE.get());
        dropSelf(TempAVPBlocks.TITANIUM_COLUMN.get());
        dropSelf(TempAVPBlocks.TITANIUM_FASTENED_SIDING.get());
        dropSelf(TempAVPBlocks.TITANIUM_FASTENED_STANDING.get());
        dropSelf(TempAVPBlocks.TITANIUM_GRATE.get());
        dropSelf(TempAVPBlocks.TITANIUM_PLATING.get());
        dropSelf(TempAVPBlocks.TITANIUM_SIDING.get());
        dropSelf(TempAVPBlocks.TITANIUM_STANDING.get());
        dropSelf(TempAVPBlocks.TITANIUM_TREAD.get());
        dropSelf(TempAVPBlocks.URANIUM_BLOCK.get());
        dropSelf(TempAVPBlocks.ZINC_BLOCK.get());
        add(TempAVPBlocks.ZINC_ORE.get(), block -> createOreMultiDrop(block, TempAVPItems.RAW_ZINC.get(), 2, 5));

        add(TempAVPBlocks.INDUSTRIAL_GLASS_DOOR.get(), this::createDoorTable);
        add(TempAVPBlocks.FERROALUMINUM_DOOR.get(), this::createDoorTable);
        add(TempAVPBlocks.STEEL_DOOR.get(), this::createDoorTable);
        add(TempAVPBlocks.TITANIUM_DOOR.get(), this::createDoorTable);

        dropSelf(TempAVPBlocks.INDUSTRIAL_GLASS_TRAP_DOOR.get());
        dropSelf(TempAVPBlocks.FERROALUMINUM_TRAP_DOOR.get());
        dropSelf(TempAVPBlocks.STEEL_TRAP_DOOR.get());
        dropSelf(TempAVPBlocks.TITANIUM_TRAP_DOOR.get());

        dropSelf(TempAVPBlocks.STEEL_PRESSURE_PLATE.get());
        dropSelf(TempAVPBlocks.TITANIUM_PRESSURE_PLATE.get());
        dropSelf(TempAVPBlocks.FERROALUMINUM_PRESSURE_PLATE.get());

        dropSelf(TempAVPBlocks.STEEL_BUTTON.get());
        dropSelf(TempAVPBlocks.TITANIUM_BUTTON.get());
        dropSelf(TempAVPBlocks.FERROALUMINUM_BUTTON.get());

        dropSlab(TempAVPBlocks.INDUSTRIAL_GLASS_SLAB.get());
        dropSlab(TempAVPBlocks.FERROALUMINUM_FASTENED_SIDING_SLAB.get());
        dropSlab(TempAVPBlocks.FERROALUMINUM_FASTENED_STANDING_SLAB.get());
        dropSlab(TempAVPBlocks.FERROALUMINUM_GRATE_SLAB.get());
        dropSlab(TempAVPBlocks.FERROALUMINUM_SIDING_SLAB.get());
        dropSlab(TempAVPBlocks.FERROALUMINUM_STANDING_SLAB.get());
        dropSlab(TempAVPBlocks.STEEL_GRATE_SLAB.get());
        dropSlab(TempAVPBlocks.STEEL_SIDING_SLAB.get());
        dropSlab(TempAVPBlocks.STEEL_STANDING_SLAB.get());
        dropSlab(TempAVPBlocks.STEEL_FASTENED_SIDING_SLAB.get());
        dropSlab(TempAVPBlocks.STEEL_FASTENED_STANDING_SLAB.get());
        dropSlab(TempAVPBlocks.TITANIUM_GRATE_SLAB.get());
        dropSlab(TempAVPBlocks.TITANIUM_SIDING_SLAB.get());
        dropSlab(TempAVPBlocks.TITANIUM_STANDING_SLAB.get());
        dropSlab(TempAVPBlocks.TITANIUM_FASTENED_SIDING_SLAB.get());
        dropSlab(TempAVPBlocks.TITANIUM_FASTENED_STANDING_SLAB.get());

        dropSelf(TempAVPBlocks.INDUSTRIAL_GLASS_STAIRS.get());
        dropSelf(TempAVPBlocks.FERROALUMINUM_FASTENED_SIDING_STAIRS.get());
        dropSelf(TempAVPBlocks.FERROALUMINUM_FASTENED_STANDING_STAIRS.get());
        dropSelf(TempAVPBlocks.FERROALUMINUM_GRATE_STAIRS.get());
        dropSelf(TempAVPBlocks.FERROALUMINUM_SIDING_STAIRS.get());
        dropSelf(TempAVPBlocks.FERROALUMINUM_STANDING_STAIRS.get());
        dropSelf(TempAVPBlocks.STEEL_GRATE_STAIRS.get());
        dropSelf(TempAVPBlocks.STEEL_SIDING_STAIRS.get());
        dropSelf(TempAVPBlocks.STEEL_STANDING_STAIRS.get());
        dropSelf(TempAVPBlocks.STEEL_FASTENED_SIDING_STAIRS.get());
        dropSelf(TempAVPBlocks.STEEL_FASTENED_STANDING_STAIRS.get());
        dropSelf(TempAVPBlocks.TITANIUM_GRATE_STAIRS.get());
        dropSelf(TempAVPBlocks.TITANIUM_SIDING_STAIRS.get());
        dropSelf(TempAVPBlocks.TITANIUM_STANDING_STAIRS.get());
        dropSelf(TempAVPBlocks.TITANIUM_FASTENED_SIDING_STAIRS.get());
        dropSelf(TempAVPBlocks.TITANIUM_FASTENED_STANDING_STAIRS.get());

        add(TempAVPBlocks.INDUSTRIAL_FURNACE.get(), this::createNameableBlockEntityTable);
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
