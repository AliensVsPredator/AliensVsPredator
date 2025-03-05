package com.avp.fabric.data.model;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

import com.avp.core.common.block.AVPBlocks;
import com.avp.core.common.item.AVPItems;
import com.avp.core.common.item.ArmorItems;

import java.util.function.Supplier;

public class ItemModelProvider extends FabricModelProvider {

    public ItemModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators generators) {
        generators.createSimpleFlatItemModel(AVPBlocks.NETHER_RESIN_WEB.get());
        generators.createSimpleFlatItemModel(AVPBlocks.RAZOR_WIRE.get());
        generators.createSimpleFlatItemModel(AVPBlocks.RESIN_WEB.get());
    }

    @Override
    public void generateItemModels(ItemModelGenerators generators) {
        generateStandardItem(generators, AVPItems.CASELESS_BULLET);
        generateStandardItem(generators, AVPItems.HEAVY_BULLET);
        generateStandardItem(generators, AVPItems.SMALL_BULLET);
        generateStandardItem(generators, AVPItems.MEDIUM_BULLET);
        generateStandardItem(generators, AVPItems.SHOTGUN_BULLET);
        generateStandardItem(generators, ArmorItems.CHITIN_BOOTS);
        generateStandardItem(generators, ArmorItems.CHITIN_CHESTPLATE);
        generateStandardItem(generators, ArmorItems.CHITIN_HELMET);
        generateStandardItem(generators, ArmorItems.CHITIN_LEGGINGS);
        generateStandardItem(generators, AVPItems.FUEL_TANK);
        generateStandardItem(generators, ArmorItems.JUNGLE_PREDATOR_BOOTS);
        generateStandardItem(generators, ArmorItems.JUNGLE_PREDATOR_CHESTPLATE);
        generateStandardItem(generators, ArmorItems.JUNGLE_PREDATOR_HELMET);
        generateStandardItem(generators, ArmorItems.JUNGLE_PREDATOR_LEGGINGS);
        generateStandardItem(generators, ArmorItems.MK50_BOOTS);
        generateStandardItem(generators, ArmorItems.MK50_CHESTPLATE);
        generateStandardItem(generators, ArmorItems.MK50_HELMET);
        generateStandardItem(generators, ArmorItems.MK50_LEGGINGS);
        generateStandardItem(generators, ArmorItems.NETHER_CHITIN_BOOTS);
        generateStandardItem(generators, ArmorItems.NETHER_CHITIN_CHESTPLATE);
        generateStandardItem(generators, ArmorItems.NETHER_CHITIN_HELMET);
        generateStandardItem(generators, ArmorItems.NETHER_CHITIN_LEGGINGS);
        generateStandardItem(generators, ArmorItems.PLATED_CHITIN_BOOTS);
        generateStandardItem(generators, ArmorItems.PLATED_CHITIN_CHESTPLATE);
        generateStandardItem(generators, ArmorItems.PLATED_CHITIN_HELMET);
        generateStandardItem(generators, ArmorItems.PLATED_CHITIN_LEGGINGS);
        generateStandardItem(generators, ArmorItems.PLATED_NETHER_CHITIN_BOOTS);
        generateStandardItem(generators, ArmorItems.PLATED_NETHER_CHITIN_CHESTPLATE);
        generateStandardItem(generators, ArmorItems.PLATED_NETHER_CHITIN_HELMET);
        generateStandardItem(generators, ArmorItems.PLATED_NETHER_CHITIN_LEGGINGS);
        generateStandardItem(generators, ArmorItems.PRESSURE_BOOTS);
        generateStandardItem(generators, ArmorItems.PRESSURE_CHESTPLATE);
        generateStandardItem(generators, ArmorItems.PRESSURE_HELMET);
        generateStandardItem(generators, ArmorItems.PRESSURE_LEGGINGS);
        generateStandardItem(generators, AVPItems.ROCKET);
        generateStandardItem(generators, ArmorItems.STEEL_BOOTS);
        generateStandardItem(generators, ArmorItems.STEEL_CHESTPLATE);
        generateStandardItem(generators, ArmorItems.STEEL_HELMET);
        generateStandardItem(generators, ArmorItems.STEEL_LEGGINGS);
        generateStandardItem(generators, ArmorItems.TACTICAL_BOOTS);
        generateStandardItem(generators, ArmorItems.TACTICAL_CHESTPLATE);
        generateStandardItem(generators, ArmorItems.TACTICAL_HELMET);
        generateStandardItem(generators, ArmorItems.TACTICAL_LEGGINGS);
        generateStandardItem(generators, ArmorItems.TITANIUM_BOOTS);
        generateStandardItem(generators, ArmorItems.TITANIUM_CHESTPLATE);
        generateStandardItem(generators, ArmorItems.TITANIUM_HELMET);
        generateStandardItem(generators, ArmorItems.TITANIUM_LEGGINGS);

        generateStandardItem(generators, AVPItems.ALUMINUM_INGOT);
        generateStandardItem(generators, AVPItems.AUTUNITE_DUST);
        generateStandardItem(generators, AVPItems.BARREL);
        generateStandardItem(generators, AVPItems.BATTERY_PACK);
        generateStandardItem(generators, AVPItems.BLUEPRINT_M37_12_SHOTGUN);
        generateStandardItem(generators, AVPItems.BLUEPRINT_F903WE_RIFLE);
        generateStandardItem(generators, AVPItems.BLUEPRINT_FLAMETHROWER_SEVASTOPOL);
        generateStandardItem(generators, AVPItems.BLUEPRINT_M41A_PULSE_RIFLE);
        generateStandardItem(generators, AVPItems.BLUEPRINT_M56_SMARTGUN);
        generateStandardItem(generators, AVPItems.BLUEPRINT_M6B_ROCKET_LAUNCHER);
        generateStandardItem(generators, AVPItems.BLUEPRINT_M88MOD4_COMBAT_PISTOL);
        generateStandardItem(generators, AVPItems.BLUEPRINT_OLD_PAINLESS);
        generateStandardItem(generators, AVPItems.BLUEPRINT_M42A3_SNIPER_RIFLE);
        generateStandardItem(generators, AVPItems.BLUEPRINT_M4RA_BATTLE_RIFLE);
        generateStandardItem(generators, AVPItems.BLUEPRINT_ZX_76_SHOTGUN);
        generateStandardItem(generators, AVPItems.BRASS_INGOT);
        generateStandardItem(generators, AVPItems.BULLET_TIP);
        generateStandardItem(generators, AVPItems.CANISTER);
        generateStandardItem(generators, AVPItems.CAPACITOR);
        generateStandardItem(generators, AVPItems.CARBON_DUST);
        generateStandardItem(generators, AVPItems.CASELESS_CASING);
        generateStandardItem(generators, AVPItems.CHITIN);
        generateStandardItem(generators, AVPItems.CPU);
        generateStandardItem(generators, AVPItems.DIODE);
        generateStandardItem(generators, AVPItems.FERROALUMINUM_INGOT);
        generateStandardItem(generators, AVPItems.GRIP);
        generateStandardItem(generators, AVPItems.HEAVY_CASING);
        generateStandardItem(generators, AVPItems.INTEGRATED_CIRCUIT);
        generateStandardItem(generators, AVPItems.LEAD_INGOT);
        generateStandardItem(generators, AVPItems.LED);
        generateStandardItem(generators, AVPItems.LED_DISPLAY);
        generateStandardItem(generators, AVPItems.LITHIUM_DUST);
        generateStandardItem(generators, AVPItems.MEDIUM_CASING);
        generateStandardItem(generators, AVPItems.MINIGUN_BARREL);
        generateStandardItem(generators, AVPItems.NEODYMIUM_MAGNET);
        generateStandardItem(generators, AVPItems.NETHER_CHITIN);
        generateStandardItem(generators, AVPItems.NETHER_RESIN_BALL);
        generateStandardItem(generators, AVPItems.OVOID_POTTERY_SHERD);
        generateStandardItem(generators, AVPItems.PARASITE_POTTERY_SHERD);
        generateStandardItem(generators, AVPItems.PLATED_CHITIN);
        generateStandardItem(generators, AVPItems.PLATED_NETHER_CHITIN);
        generateStandardItem(generators, AVPItems.POLYMER);
        generateStandardItem(generators, AVPItems.RAW_BAUXITE);
        generateStandardItem(generators, AVPItems.RAW_BRASS);
        generateStandardItem(generators, AVPItems.RAW_CRUDE_IRON);
        generateStandardItem(generators, AVPItems.RAW_FERROBAUXITE);
        generateStandardItem(generators, AVPItems.RAW_GALENA);
        generateStandardItem(generators, AVPItems.RAW_MONAZITE);
        generateStandardItem(generators, AVPItems.RAW_ROYAL_JELLY);
        generateStandardItem(generators, AVPItems.RAW_SILICA);
        generateStandardItem(generators, AVPItems.RAW_TITANIUM);
        generateStandardItem(generators, AVPItems.RAW_ZINC);
        generateStandardItem(generators, AVPItems.RECEIVER);
        generateStandardItem(generators, AVPItems.REGULATOR);
        generateStandardItem(generators, AVPItems.RESIN_BALL);
        generateStandardItem(generators, AVPItems.RESISTOR);
        generateStandardItem(generators, AVPItems.ROCKET_BARREL);
        generateStandardItem(generators, AVPItems.ROYAL_JELLY_CANISTER);
        generateStandardItem(generators, AVPItems.ROYALTY_POTTERY_SHERD);
        generateStandardItem(generators, AVPItems.SHOTGUN_CASING);
        generateStandardItem(generators, AVPItems.SMALL_CASING);
        generateStandardItem(generators, AVPItems.SMART_BARREL);
        generateStandardItem(generators, AVPItems.SMART_RECEIVER);
        generateHandheldItem(generators, AVPItems.STEEL_AXE);
        generateHandheldItem(generators, AVPItems.STEEL_HOE);
        generateStandardItem(generators, AVPItems.STEEL_INGOT);
        generateHandheldItem(generators, AVPItems.STEEL_PICKAXE);
        generateHandheldItem(generators, AVPItems.STEEL_SHOVEL);
        generateHandheldItem(generators, AVPItems.STEEL_SWORD);
        generateStandardItem(generators, AVPItems.STOCK);
        generateHandheldItem(generators, AVPItems.TITANIUM_AXE);
        generateHandheldItem(generators, AVPItems.TITANIUM_HOE);
        generateStandardItem(generators, AVPItems.TITANIUM_INGOT);
        generateHandheldItem(generators, AVPItems.TITANIUM_PICKAXE);
        generateHandheldItem(generators, AVPItems.TITANIUM_SHOVEL);
        generateHandheldItem(generators, AVPItems.TITANIUM_SWORD);
        generateHandheldItem(generators, AVPItems.VERITANIUM_AXE);
        generateHandheldItem(generators, AVPItems.VERITANIUM_HOE);
        generateHandheldItem(generators, AVPItems.VERITANIUM_PICKAXE);
        generateHandheldItem(generators, AVPItems.VERITANIUM_SHOVEL);
        generateHandheldItem(generators, AVPItems.VERITANIUM_SWORD);
        generateStandardItem(generators, AVPItems.TRANSISTOR);
        generateStandardItem(generators, AVPItems.URANIUM_INGOT);
        generateStandardItem(generators, AVPItems.VECTOR_POTTERY_SHERD);
        generateStandardItem(generators, AVPItems.VERITANIUM_SHARD);
        generateStandardItem(generators, AVPItems.ZINC_INGOT);
    }

    private void generateHandheldItem(ItemModelGenerators generators, Supplier<Item> item) {
        generateStandardItem(generators, item, ModelTemplates.FLAT_HANDHELD_ITEM);
    }

    private void generateStandardItem(ItemModelGenerators generators, Supplier<Item> item) {
        generateStandardItem(generators, item, ModelTemplates.FLAT_ITEM);
    }

    private void generateStandardItem(ItemModelGenerators generators, Supplier<Item> item, ModelTemplate modelTemplate) {
        generators.generateFlatItem(item.get(), modelTemplate);
    }

    @Override
    public @NotNull String getName() {
        return "Item Model Definitions";
    }
}
