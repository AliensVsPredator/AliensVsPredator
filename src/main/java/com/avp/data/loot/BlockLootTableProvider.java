package com.avp.data.loot;

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

import com.avp.common.block.AVPBlocks;
import com.avp.common.item.AVPItems;

public class BlockLootTableProvider extends FabricBlockLootTableProvider {

    public BlockLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        dropSelf(AVPBlocks.ALUMINUM_BLOCK);
        dropSelf(AVPBlocks.AUTUNITE_BLOCK);
        add(AVPBlocks.AUTUNITE_ORE, block -> createOreMultiDrop(block, AVPItems.AUTUNITE_DUST, 2, 4));
        add(AVPBlocks.BAUXITE_ORE, block -> createOreDrop(block, AVPItems.RAW_BAUXITE));
        dropSelf(AVPBlocks.BRASS_BLOCK);
        dropSelf(AVPBlocks.CHISELED_FERROALUMINUM);
        dropSelf(AVPBlocks.CHISELED_STEEL);
        dropSelf(AVPBlocks.CHISELED_TITANIUM);
        dropSelf(AVPBlocks.CUT_FERROALUMINUM);
        add(AVPBlocks.CUT_FERROALUMINUM_SLAB, createSlabItemTable(AVPBlocks.CUT_FERROALUMINUM_SLAB));
        dropSelf(AVPBlocks.CUT_FERROALUMINUM_STAIRS);
        dropSelf(AVPBlocks.CUT_STEEL);
        add(AVPBlocks.CUT_STEEL_SLAB, createSlabItemTable(AVPBlocks.CUT_STEEL_SLAB));
        dropSelf(AVPBlocks.CUT_STEEL_STAIRS);
        dropSelf(AVPBlocks.CUT_TITANIUM);
        add(AVPBlocks.CUT_TITANIUM_SLAB, createSlabItemTable(AVPBlocks.CUT_TITANIUM_SLAB));
        dropSelf(AVPBlocks.CUT_TITANIUM_STAIRS);
        add(AVPBlocks.DEEPSLATE_TITANIUM_ORE, block -> createOreDrop(block, AVPItems.RAW_TITANIUM));
        add(AVPBlocks.DEEPSLATE_ZINC_ORE, block -> createOreMultiDrop(block, AVPItems.RAW_ZINC, 2, 5));
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
        add(AVPBlocks.GALENA_ORE, block -> createOreDrop(block, AVPItems.RAW_GALENA));
        dropSelf(AVPBlocks.LEAD_BLOCK);
        dropSelf(AVPBlocks.LITHIUM_BLOCK);
        add(AVPBlocks.LITHIUM_ORE, block -> createOreMultiDrop(block, AVPItems.LITHIUM_DUST, 2, 4));
        add(AVPBlocks.MONAZITE_ORE, block -> createOreDrop(block, AVPItems.RAW_MONAZITE));
        dropSelf(AVPBlocks.NETHER_RESIN);
        dropOther(AVPBlocks.NETHER_RESIN_NODE, AVPBlocks.NETHER_RESIN);
        dropOther(AVPBlocks.NETHER_RESIN_VEIN, AVPItems.NETHER_RESIN_BALL);
        dropOther(AVPBlocks.NETHER_RESIN_WEB, AVPItems.NETHER_RESIN_BALL);

        AVPBlocks.DYE_COLOR_TO_CONCRETE_SLAB.values().forEach((block) -> add(block, createSlabItemTable(block)));
        AVPBlocks.DYE_COLOR_TO_CONCRETE_STAIRS.values().forEach(this::dropSelf);

        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE.values().forEach(this::dropSelf);
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_SLAB.values().forEach((block) -> add(block, createSlabItemTable(block)));
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_STAIRS.values().forEach(this::dropSelf);
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_WALL.values().forEach(this::dropSelf);
        dropSelf(AVPBlocks.INDUSTRIAL_GLASS);
        dropSelf(AVPBlocks.INDUSTRIAL_GLASS_PANE);
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS.values().forEach(this::dropSelf);
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS_PANE.values().forEach(this::dropSelf);

        AVPBlocks.DYE_COLOR_TO_PADDING.values().forEach(this::dropSelf);
        AVPBlocks.DYE_COLOR_TO_PADDING_SLAB.values().forEach((block) -> add(block, createSlabItemTable(block)));
        AVPBlocks.DYE_COLOR_TO_PADDING_STAIRS.values().forEach(this::dropSelf);

        AVPBlocks.DYE_COLOR_TO_PANEL_PADDING.values().forEach(this::dropSelf);
        AVPBlocks.DYE_COLOR_TO_PANEL_PADDING_SLAB.values().forEach((block) -> add(block, createSlabItemTable(block)));
        AVPBlocks.DYE_COLOR_TO_PANEL_PADDING_STAIRS.values().forEach(this::dropSelf);

        AVPBlocks.DYE_COLOR_TO_PIPE_PADDING.values().forEach(this::dropSelf);
        AVPBlocks.DYE_COLOR_TO_PIPE_PADDING_SLAB.values().forEach((block) -> add(block, createSlabItemTable(block)));
        AVPBlocks.DYE_COLOR_TO_PIPE_PADDING_STAIRS.values().forEach(this::dropSelf);

        AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC.values().forEach(this::dropSelf);
        AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_SLAB.values().forEach((block) -> add(block, createSlabItemTable(block)));
        AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_STAIRS.values().forEach(this::dropSelf);

        AVPBlocks.DYE_COLOR_TO_PLASTIC.values().forEach(this::dropSelf);
        AVPBlocks.DYE_COLOR_TO_PLASTIC_SLAB.values().forEach((block) -> add(block, createSlabItemTable(block)));
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
        add(AVPBlocks.ZINC_ORE, block -> createOreMultiDrop(block, AVPItems.RAW_ZINC, 2, 5));
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
