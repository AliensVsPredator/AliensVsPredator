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
import com.avp.fabric.common.block.AVPBlocks;
import com.avp.fabric.common.item.AVPItems;

public class BlockLootTableProvider extends FabricBlockLootTableProvider {

    public BlockLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        dropSelf(AVPBlocks.BLUEPRINT_BLOCK);
        dropSelf(AVPBlocks.REDSTONE_GENERATOR);
        dropSelf(AVPBlocks.DESK_TERMINAL_BLOCK);
        dropSelf(AVPBlocks.TRIP_MINE_BLOCK);
        dropSelf(AVPBlocks.RESONATOR_BLOCK);
        dropSelf(AVPBlocks.SENTRY_TURRET);
        dropSelf(AVPBlocks.TRINITITE_BLOCK);
        dropSelf(AVPBlocks.ASH_BLOCK);
        dropSelf(AVPBlocks.NUKE_BLOCK);
        dropSelf(AVPBlocks.ROYAL_JELLY_BLOCK);
        dropSelf(AVPBlocks.ALUMINUM_BLOCK);
        dropSelf(AVPBlocks.AUTUNITE_BLOCK);
        add(TempAVPBlocks.AUTUNITE_ORE.get(), block -> createOreMultiDrop(block, AVPItems.AUTUNITE_DUST, 2, 4));
        add(TempAVPBlocks.BAUXITE_ORE.get(), block -> createOreDrop(block, AVPItems.RAW_BAUXITE));
        dropSelf(AVPBlocks.BRASS_BLOCK);
        dropSelf(AVPBlocks.CHISELED_FERROALUMINUM);
        dropSelf(AVPBlocks.CHISELED_STEEL);
        dropSelf(AVPBlocks.CHISELED_TITANIUM);
        dropSelf(AVPBlocks.CUT_FERROALUMINUM);
        dropSlab(AVPBlocks.CUT_FERROALUMINUM_SLAB);
        dropSelf(AVPBlocks.CUT_FERROALUMINUM_STAIRS);
        dropSelf(AVPBlocks.CUT_STEEL);
        dropSlab(AVPBlocks.CUT_STEEL_SLAB);
        dropSelf(AVPBlocks.CUT_STEEL_STAIRS);
        dropSelf(AVPBlocks.CUT_TITANIUM);
        dropSlab(AVPBlocks.CUT_TITANIUM_SLAB);
        dropSelf(AVPBlocks.CUT_TITANIUM_STAIRS);
        add(TempAVPBlocks.DEEPSLATE_TITANIUM_ORE.get(), block -> createOreDrop(block, AVPItems.RAW_TITANIUM));
        add(TempAVPBlocks.DEEPSLATE_ZINC_ORE.get(), block -> createOreMultiDrop(block, AVPItems.RAW_ZINC, 2, 5));
        dropSelf(AVPBlocks.FERROALUMINUM_BLOCK);
        dropSelf(AVPBlocks.FERROALUMINUM_CHAIN_FENCE);
        dropSelf(AVPBlocks.FERROALUMINUM_COLUMN);
        dropSelf(AVPBlocks.FERROALUMINUM_FASTENED_SIDING);
        dropSelf(AVPBlocks.FERROALUMINUM_FASTENED_STANDING);
        dropSelf(AVPBlocks.FERROALUMINUM_GRATE);
        dropSelf(AVPBlocks.FERROALUMINUM_PLATING);
        dropSelf(AVPBlocks.FERROALUMINUM_SIDING);
        dropSelf(AVPBlocks.FERROALUMINUM_STANDING);
        dropSelf(AVPBlocks.FERROALUMINUM_TREAD);
        add(TempAVPBlocks.GALENA_ORE.get(), block -> createOreDrop(block, AVPItems.RAW_GALENA));
        dropSelf(AVPBlocks.LEAD_BLOCK);
        add(AVPBlocks.LEAD_CHEST, this::createShulkerBoxDrop);
        add(AVPBlocks.AMMO_CHEST, this::createShulkerBoxDrop);
        dropSelf(AVPBlocks.LITHIUM_BLOCK);
        add(TempAVPBlocks.LITHIUM_ORE.get(), block -> createOreMultiDrop(block, AVPItems.LITHIUM_DUST, 2, 4));
        add(TempAVPBlocks.MONAZITE_ORE.get(), block -> createOreDrop(block, AVPItems.RAW_MONAZITE));
        dropSelf(AVPBlocks.NETHER_RESIN);
        dropOther(AVPBlocks.NETHER_RESIN_NODE, AVPBlocks.NETHER_RESIN);
        dropOther(AVPBlocks.NETHER_RESIN_VEIN, AVPItems.NETHER_RESIN_BALL);
        dropOther(AVPBlocks.NETHER_RESIN_WEB, AVPItems.NETHER_RESIN_BALL);
        dropSelf(AVPBlocks.ABERRANT_RESIN);
        dropOther(AVPBlocks.ABERRANT_RESIN_NODE, AVPBlocks.ABERRANT_RESIN);
        dropOther(AVPBlocks.ABERRANT_RESIN_VEIN, AVPItems.ABERRANT_RESIN_BALL);
        dropOther(AVPBlocks.ABERRANT_RESIN_WEB, AVPItems.ABERRANT_RESIN_BALL);
        dropSelf(AVPBlocks.IRRADIATED_RESIN);
        dropOther(AVPBlocks.IRRADIATED_RESIN_NODE, AVPBlocks.IRRADIATED_RESIN);
        dropOther(AVPBlocks.IRRADIATED_RESIN_VEIN, AVPItems.IRRADIATED_RESIN_BALL);
        dropOther(AVPBlocks.IRRADIATED_RESIN_WEB, AVPItems.IRRADIATED_RESIN_BALL);
        dropSelf(AVPBlocks.RESIN_BRICKS);
        dropSelf(AVPBlocks.RESIN_O);
        dropSelf(AVPBlocks.RESIN_RIBBED);
        dropSelf(AVPBlocks.RESIN_SMOOTH);

        // TODO: Use stream concat here.
        TempAVPBlocks.DYE_COLOR_TO_CONCRETE_SLAB.values().stream().map(Supplier::get).forEach(this::dropSlab);
        AVPBlocks.DYE_COLOR_TO_CONCRETE_STAIRS.values().forEach(this::dropSelf);

        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE.values().forEach(this::dropSelf);
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_SLAB.values().forEach(this::dropSlab);
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_STAIRS.values().forEach(this::dropSelf);
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_WALL.values().forEach(this::dropSelf);
        dropSelf(AVPBlocks.INDUSTRIAL_GLASS);
        dropSelf(AVPBlocks.INDUSTRIAL_GLASS_PANE);
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS.values().forEach(this::dropSelf);
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS_PANE.values().forEach(this::dropSelf);

        AVPBlocks.DYE_COLOR_TO_PADDING.values().forEach(this::dropSelf);
        AVPBlocks.DYE_COLOR_TO_PADDING_SLAB.values().forEach(this::dropSlab);
        AVPBlocks.DYE_COLOR_TO_PADDING_STAIRS.values().forEach(this::dropSelf);

        AVPBlocks.DYE_COLOR_TO_PANEL_PADDING.values().forEach(this::dropSelf);
        AVPBlocks.DYE_COLOR_TO_PANEL_PADDING_SLAB.values().forEach(this::dropSlab);
        AVPBlocks.DYE_COLOR_TO_PANEL_PADDING_STAIRS.values().forEach(this::dropSelf);

        AVPBlocks.DYE_COLOR_TO_PIPE_PADDING.values().forEach(this::dropSelf);
        AVPBlocks.DYE_COLOR_TO_PIPE_PADDING_SLAB.values().forEach(this::dropSlab);
        AVPBlocks.DYE_COLOR_TO_PIPE_PADDING_STAIRS.values().forEach(this::dropSelf);

        AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC.values().forEach(this::dropSelf);
        AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_SLAB.values().forEach(this::dropSlab);
        AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_STAIRS.values().forEach(this::dropSelf);

        AVPBlocks.DYE_COLOR_TO_PLASTIC.values().forEach(this::dropSelf);
        AVPBlocks.DYE_COLOR_TO_PLASTIC_SLAB.values().forEach(this::dropSlab);
        AVPBlocks.DYE_COLOR_TO_PLASTIC_STAIRS.values().forEach(this::dropSelf);

        dropSelf(AVPBlocks.RAW_BAUXITE_BLOCK);
        dropSelf(AVPBlocks.RAW_GALENA_BLOCK);
        dropSelf(AVPBlocks.RAW_MONAZITE_BLOCK);
        dropSelf(AVPBlocks.RAW_SILICA_BLOCK);
        dropSelf(AVPBlocks.RAW_TITANIUM_BLOCK);
        dropSelf(AVPBlocks.RAW_ZINC_BLOCK);
        dropSelf(AVPBlocks.RAZOR_WIRE);
        dropSelf(AVPBlocks.RESIN);
        dropOther(AVPBlocks.RESIN_NODE, AVPBlocks.RESIN);
        dropOther(AVPBlocks.RESIN_VEIN, AVPItems.RESIN_BALL);
        dropOther(AVPBlocks.RESIN_WEB, AVPItems.RESIN_BALL);
        dropSelf(AVPBlocks.SILICA_GRAVEL);
        dropSelf(AVPBlocks.STEEL_BARS);
        dropSelf(AVPBlocks.STEEL_BLOCK);
        dropSelf(AVPBlocks.STEEL_CHAIN_FENCE);
        dropSelf(AVPBlocks.STEEL_COLUMN);
        dropSelf(AVPBlocks.STEEL_FASTENED_SIDING);
        dropSelf(AVPBlocks.STEEL_FASTENED_STANDING);
        dropSelf(AVPBlocks.STEEL_GRATE);
        dropSelf(AVPBlocks.STEEL_PLATING);
        dropSelf(AVPBlocks.STEEL_SIDING);
        dropSelf(AVPBlocks.STEEL_STANDING);
        dropSelf(AVPBlocks.STEEL_TREAD);
        dropSelf(AVPBlocks.TITANIUM_BLOCK);
        dropSelf(AVPBlocks.TITANIUM_CHAIN_FENCE);
        dropSelf(AVPBlocks.TITANIUM_COLUMN);
        dropSelf(AVPBlocks.TITANIUM_FASTENED_SIDING);
        dropSelf(AVPBlocks.TITANIUM_FASTENED_STANDING);
        dropSelf(AVPBlocks.TITANIUM_GRATE);
        dropSelf(AVPBlocks.TITANIUM_PLATING);
        dropSelf(AVPBlocks.TITANIUM_SIDING);
        dropSelf(AVPBlocks.TITANIUM_STANDING);
        dropSelf(AVPBlocks.TITANIUM_TREAD);
        dropSelf(AVPBlocks.URANIUM_BLOCK);
        dropSelf(AVPBlocks.ZINC_BLOCK);
        add(TempAVPBlocks.ZINC_ORE.get(), block -> createOreMultiDrop(block, AVPItems.RAW_ZINC, 2, 5));

        add(AVPBlocks.INDUSTRIAL_GLASS_DOOR, this::createDoorTable);
        add(AVPBlocks.FERROALUMINUM_DOOR, this::createDoorTable);
        add(AVPBlocks.STEEL_DOOR, this::createDoorTable);
        add(AVPBlocks.TITANIUM_DOOR, this::createDoorTable);

        dropSelf(AVPBlocks.INDUSTRIAL_GLASS_TRAP_DOOR);
        dropSelf(AVPBlocks.FERROALUMINUM_TRAP_DOOR);
        dropSelf(AVPBlocks.STEEL_TRAP_DOOR);
        dropSelf(AVPBlocks.TITANIUM_TRAP_DOOR);

        dropSelf(AVPBlocks.STEEL_PRESSURE_PLATE);
        dropSelf(AVPBlocks.TITANIUM_PRESSURE_PLATE);
        dropSelf(AVPBlocks.FERROALUMINUM_PRESSURE_PLATE);

        dropSelf(AVPBlocks.STEEL_BUTTON);
        dropSelf(AVPBlocks.TITANIUM_BUTTON);
        dropSelf(AVPBlocks.FERROALUMINUM_BUTTON);

        dropSlab(AVPBlocks.INDUSTRIAL_GLASS_SLAB);
        dropSlab(AVPBlocks.FERROALUMINUM_FASTENED_SIDING_SLAB);
        dropSlab(AVPBlocks.FERROALUMINUM_FASTENED_STANDING_SLAB);
        dropSlab(AVPBlocks.FERROALUMINUM_GRATE_SLAB);
        dropSlab(AVPBlocks.FERROALUMINUM_SIDING_SLAB);
        dropSlab(AVPBlocks.FERROALUMINUM_STANDING_SLAB);
        dropSlab(AVPBlocks.STEEL_GRATE_SLAB);
        dropSlab(AVPBlocks.STEEL_SIDING_SLAB);
        dropSlab(AVPBlocks.STEEL_STANDING_SLAB);
        dropSlab(AVPBlocks.STEEL_FASTENED_SIDING_SLAB);
        dropSlab(AVPBlocks.STEEL_FASTENED_STANDING_SLAB);
        dropSlab(AVPBlocks.TITANIUM_GRATE_SLAB);
        dropSlab(AVPBlocks.TITANIUM_SIDING_SLAB);
        dropSlab(AVPBlocks.TITANIUM_STANDING_SLAB);
        dropSlab(AVPBlocks.TITANIUM_FASTENED_SIDING_SLAB);
        dropSlab(AVPBlocks.TITANIUM_FASTENED_STANDING_SLAB);

        dropSelf(AVPBlocks.INDUSTRIAL_GLASS_STAIRS);
        dropSelf(AVPBlocks.FERROALUMINUM_FASTENED_SIDING_STAIRS);
        dropSelf(AVPBlocks.FERROALUMINUM_FASTENED_STANDING_STAIRS);
        dropSelf(AVPBlocks.FERROALUMINUM_GRATE_STAIRS);
        dropSelf(AVPBlocks.FERROALUMINUM_SIDING_STAIRS);
        dropSelf(AVPBlocks.FERROALUMINUM_STANDING_STAIRS);
        dropSelf(AVPBlocks.STEEL_GRATE_STAIRS);
        dropSelf(AVPBlocks.STEEL_SIDING_STAIRS);
        dropSelf(AVPBlocks.STEEL_STANDING_STAIRS);
        dropSelf(AVPBlocks.STEEL_FASTENED_SIDING_STAIRS);
        dropSelf(AVPBlocks.STEEL_FASTENED_STANDING_STAIRS);
        dropSelf(AVPBlocks.TITANIUM_GRATE_STAIRS);
        dropSelf(AVPBlocks.TITANIUM_SIDING_STAIRS);
        dropSelf(AVPBlocks.TITANIUM_STANDING_STAIRS);
        dropSelf(AVPBlocks.TITANIUM_FASTENED_SIDING_STAIRS);
        dropSelf(AVPBlocks.TITANIUM_FASTENED_STANDING_STAIRS);

        add(AVPBlocks.INDUSTRIAL_FURNACE, this::createNameableBlockEntityTable);
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
