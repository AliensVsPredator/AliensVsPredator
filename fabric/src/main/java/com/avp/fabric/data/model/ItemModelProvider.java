package com.avp.fabric.data.model;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

import com.avp.common.item.TempAVPItems;
import com.avp.fabric.common.block.AVPBlocks;
import com.avp.fabric.common.block_item.AVPBlockItems;
import com.avp.fabric.common.item.AVPItems;
import com.avp.fabric.common.item.ArmorItems;

public class ItemModelProvider extends FabricModelProvider {

    public ItemModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators generators) {
        generators.createSimpleFlatItemModel(AVPBlocks.NETHER_RESIN_WEB);
        generators.createSimpleFlatItemModel(AVPBlocks.RAZOR_WIRE);
        generators.createSimpleFlatItemModel(AVPBlocks.RESIN_WEB);
    }

    @Override
    public void generateItemModels(ItemModelGenerators generators) {
        generateStandardItem(generators, AVPItems.SHURIKEN);
        generateStandardItem(generators, AVPItems.SMART_DISC);
        generateStandardItem(generators, AVPItems.GRENADE);
        generateStandardItem(generators, AVPItems.GRENADE_INCENDIARY);
        generateStandardItem(generators, AVPItems.GRENADE_IRRADIATED);
        generateStandardItem(generators, AVPItems.CASELESS_BULLET);
        generateStandardItem(generators, AVPItems.HEAVY_BULLET);
        generateStandardItem(generators, TempAVPItems.SMALL_BULLET);
        generateStandardItem(generators, AVPItems.MEDIUM_BULLET);
        generateStandardItem(generators, TempAVPItems.SHOTGUN_SHELL);
        generateStandardItem(generators, ArmorItems.ABERRANT_CHITIN_BOOTS);
        generateStandardItem(generators, ArmorItems.ABERRANT_CHITIN_CHESTPLATE);
        generateStandardItem(generators, ArmorItems.ABERRANT_CHITIN_HELMET);
        generateStandardItem(generators, ArmorItems.ABERRANT_CHITIN_LEGGINGS);
        generateStandardItem(generators, ArmorItems.CHITIN_BOOTS);
        generateStandardItem(generators, ArmorItems.CHITIN_CHESTPLATE);
        generateStandardItem(generators, ArmorItems.CHITIN_HELMET);
        generateStandardItem(generators, ArmorItems.CHITIN_LEGGINGS);
        generateStandardItem(generators, ArmorItems.IRRADIATED_CHITIN_BOOTS);
        generateStandardItem(generators, ArmorItems.IRRADIATED_CHITIN_CHESTPLATE);
        generateStandardItem(generators, ArmorItems.IRRADIATED_CHITIN_HELMET);
        generateStandardItem(generators, ArmorItems.IRRADIATED_CHITIN_LEGGINGS);
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
        generateStandardItem(generators, ArmorItems.PLATED_ABERRANT_CHITIN_BOOTS);
        generateStandardItem(generators, ArmorItems.PLATED_ABERRANT_CHITIN_CHESTPLATE);
        generateStandardItem(generators, ArmorItems.PLATED_ABERRANT_CHITIN_HELMET);
        generateStandardItem(generators, ArmorItems.PLATED_ABERRANT_CHITIN_LEGGINGS);
        generateStandardItem(generators, ArmorItems.PLATED_CHITIN_BOOTS);
        generateStandardItem(generators, ArmorItems.PLATED_CHITIN_CHESTPLATE);
        generateStandardItem(generators, ArmorItems.PLATED_CHITIN_HELMET);
        generateStandardItem(generators, ArmorItems.PLATED_CHITIN_LEGGINGS);
        generateStandardItem(generators, ArmorItems.PLATED_IRRADIATED_CHITIN_BOOTS);
        generateStandardItem(generators, ArmorItems.PLATED_IRRADIATED_CHITIN_CHESTPLATE);
        generateStandardItem(generators, ArmorItems.PLATED_IRRADIATED_CHITIN_HELMET);
        generateStandardItem(generators, ArmorItems.PLATED_IRRADIATED_CHITIN_LEGGINGS);
        generateStandardItem(generators, ArmorItems.PLATED_NETHER_CHITIN_BOOTS);
        generateStandardItem(generators, ArmorItems.PLATED_NETHER_CHITIN_CHESTPLATE);
        generateStandardItem(generators, ArmorItems.PLATED_NETHER_CHITIN_HELMET);
        generateStandardItem(generators, ArmorItems.PLATED_NETHER_CHITIN_LEGGINGS);
        generateStandardItem(generators, ArmorItems.PRESSURE_BOOTS);
        generateStandardItem(generators, ArmorItems.PRESSURE_CHESTPLATE);
        generateStandardItem(generators, ArmorItems.PRESSURE_HELMET);
        generateStandardItem(generators, ArmorItems.PRESSURE_LEGGINGS);
        generateStandardItem(generators, TempAVPItems.ROCKET);
        generateStandardItem(generators, ArmorItems.STEEL_BOOTS);
        generateStandardItem(generators, ArmorItems.STEEL_CHESTPLATE);
        generateStandardItem(generators, ArmorItems.STEEL_HELMET);
        generateStandardItem(generators, ArmorItems.STEEL_LEGGINGS);
        generateStandardItem(generators, ArmorItems.TACTICAL_BOOTS);
        generateStandardItem(generators, ArmorItems.TACTICAL_CHESTPLATE);
        generateStandardItem(generators, ArmorItems.TACTICAL_HELMET);
        generateStandardItem(generators, ArmorItems.TACTICAL_LEGGINGS);
        generateStandardItem(generators, ArmorItems.TACTICAL_CAMO_BOOTS);
        generateStandardItem(generators, ArmorItems.TACTICAL_CAMO_CHESTPLATE);
        generateStandardItem(generators, ArmorItems.TACTICAL_CAMO_HELMET);
        generateStandardItem(generators, ArmorItems.TACTICAL_CAMO_LEGGINGS);
        generateStandardItem(generators, ArmorItems.TITANIUM_BOOTS);
        generateStandardItem(generators, ArmorItems.TITANIUM_CHESTPLATE);
        generateStandardItem(generators, ArmorItems.TITANIUM_HELMET);
        generateStandardItem(generators, ArmorItems.TITANIUM_LEGGINGS);

        generateStandardItem(generators, TempAVPItems.IRRADIATED_CHITIN);
        generateStandardItem(generators, TempAVPItems.PLATED_IRRADIATED_CHITIN);
        generateStandardItem(generators, TempAVPItems.IRRADIATED_RESIN_BALL);
        generateStandardItem(generators, AVPItems.ABERRANT_CHITIN);
        generateStandardItem(generators, AVPItems.ABERRANT_RESIN_BALL);
        generateStandardItem(generators, AVPItems.PLATED_ABERRANT_CHITIN);
        generateStandardItem(generators, TempAVPItems.ALUMINUM_INGOT);
        generateStandardItem(generators, AVPItems.ALIEN_MUSIC_DISC_1);
        generateStandardItem(generators, AVPItems.PREDATOR_MUSIC_DISC_1);
        generateStandardItem(generators, AVPItems.ALIEN_MUSIC_DISC_1_FRAGMENT);
        generateStandardItem(generators, AVPItems.PREDATOR_MUSIC_DISC_1_FRAGMENT);
        generateStandardItem(generators, TempAVPItems.AUTUNITE_DUST);
        generateStandardItem(generators, TempAVPItems.BARREL);
        generateStandardItem(generators, TempAVPItems.BATTERY_PACK);
        generateStandardItem(generators, TempAVPItems.BLUEPRINT_M37_12_SHOTGUN);
        generateStandardItem(generators, TempAVPItems.BLUEPRINT_F903WE_RIFLE);
        generateStandardItem(generators, TempAVPItems.BLUEPRINT_FLAMETHROWER_SEVASTOPOL);
        generateStandardItem(generators, TempAVPItems.BLUEPRINT_M41A_PULSE_RIFLE);
        generateStandardItem(generators, TempAVPItems.BLUEPRINT_M56_SMARTGUN);
        generateStandardItem(generators, TempAVPItems.BLUEPRINT_M6B_ROCKET_LAUNCHER);
        generateStandardItem(generators, TempAVPItems.BLUEPRINT_M88MOD4_COMBAT_PISTOL);
        generateStandardItem(generators, TempAVPItems.BLUEPRINT_OLD_PAINLESS);
        generateStandardItem(generators, TempAVPItems.BLUEPRINT_M42A3_SNIPER_RIFLE);
        generateStandardItem(generators, TempAVPItems.BLUEPRINT_M4RA_BATTLE_RIFLE);
        generateStandardItem(generators, TempAVPItems.BLUEPRINT_ZX_76_SHOTGUN);
        generateStandardItem(generators, TempAVPItems.BRASS_INGOT);
        generateStandardItem(generators, TempAVPItems.BULLET_TIP);
        generateStandardItem(generators, AVPItems.CANISTER);
        generateStandardItem(generators, AVPItems.WATER_CANISTER);
        generateStandardItem(generators, AVPItems.LAVA_CANISTER);
        generateStandardItem(generators, AVPItems.MILK_CANISTER);
        generateStandardItem(generators, AVPItems.POWDER_SNOW_CANISTER);
        generateStandardItem(generators, TempAVPItems.CAPACITOR);
        generateStandardItem(generators, TempAVPItems.NUCLEAR_BATTERY);
        generateStandardItem(generators, TempAVPItems.REDSTONE_CRYSTAL);
        generateStandardItem(generators, TempAVPItems.SERVO);
        generateStandardItem(generators, TempAVPItems.SPEAKER);
        generateStandardItem(generators, TempAVPItems.CARBON_DUST);
        generateStandardItem(generators, TempAVPItems.CASELESS_CARTRIDGE);
        generateStandardItem(generators, TempAVPItems.CHITIN);
        generateStandardItem(generators, TempAVPItems.CPU);
        generateStandardItem(generators, TempAVPItems.DIODE);
        generateStandardItem(generators, TempAVPItems.FERROALUMINUM_INGOT);
        generateStandardItem(generators, TempAVPItems.GRIP);
        generateStandardItem(generators, TempAVPItems.HEAVY_CASING);
        generateStandardItem(generators, TempAVPItems.INTEGRATED_CIRCUIT);
        generateStandardItem(generators, TempAVPItems.LEAD_INGOT);
        generateStandardItem(generators, TempAVPItems.LED);
        generateStandardItem(generators, TempAVPItems.LED_DISPLAY);
        generateStandardItem(generators, TempAVPItems.LITHIUM_DUST);
        generateStandardItem(generators, TempAVPItems.MEDIUM_CASING);
        generateStandardItem(generators, TempAVPItems.MINIGUN_BARREL);
        generateStandardItem(generators, TempAVPItems.NEODYMIUM_MAGNET);
        generateStandardItem(generators, AVPItems.NETHER_CHITIN);
        generateStandardItem(generators, AVPItems.NETHER_RESIN_BALL);
        generateStandardItem(generators, AVPItems.OVOID_POTTERY_SHERD);
        generateStandardItem(generators, AVPItems.PARASITE_POTTERY_SHERD);
        generateStandardItem(generators, TempAVPItems.PLATED_CHITIN);
        generateStandardItem(generators, TempAVPItems.PLATED_NETHER_CHITIN);
        generateStandardItem(generators, TempAVPItems.POLYMER);
        generateStandardItem(generators, TempAVPItems.RAW_BAUXITE);
        generateStandardItem(generators, TempAVPItems.RAW_BRASS);
        generateStandardItem(generators, TempAVPItems.RAW_CRUDE_IRON);
        generateStandardItem(generators, TempAVPItems.RAW_FERROBAUXITE);
        generateStandardItem(generators, TempAVPItems.RAW_GALENA);
        generateStandardItem(generators, TempAVPItems.RAW_MONAZITE);
        generateStandardItem(generators, TempAVPItems.RAW_ROYAL_JELLY);
        generateStandardItem(generators, TempAVPItems.RAW_SILICA);
        generateStandardItem(generators, TempAVPItems.RAW_TITANIUM);
        generateStandardItem(generators, TempAVPItems.RAW_ZINC);
        generateStandardItem(generators, TempAVPItems.RECEIVER);
        generateStandardItem(generators, TempAVPItems.REGULATOR);
        generateStandardItem(generators, TempAVPItems.RESIN_BALL);
        generateStandardItem(generators, TempAVPItems.RESISTOR);
        generateStandardItem(generators, TempAVPItems.ROCKET_BARREL);
        generateStandardItem(generators, AVPItems.ROYALTY_POTTERY_SHERD);
        generateStandardItem(generators, TempAVPItems.SHOTGUN_CASING);
        generateStandardItem(generators, TempAVPItems.SMALL_CASING);
        generateStandardItem(generators, TempAVPItems.SMART_BARREL);
        generateStandardItem(generators, TempAVPItems.SMART_RECEIVER);
        generateHandheldItem(generators, AVPItems.STEEL_AXE);
        generateHandheldItem(generators, AVPItems.STEEL_HOE);
        generateStandardItem(generators, TempAVPItems.STEEL_INGOT);
        generateHandheldItem(generators, AVPItems.STEEL_PICKAXE);
        generateHandheldItem(generators, AVPItems.STEEL_SHOVEL);
        generateHandheldItem(generators, AVPItems.STEEL_SWORD);
        generateStandardItem(generators, TempAVPItems.STOCK);
        generateHandheldItem(generators, AVPItems.TITANIUM_AXE);
        generateHandheldItem(generators, AVPItems.TITANIUM_HOE);
        generateStandardItem(generators, TempAVPItems.TITANIUM_INGOT);
        generateHandheldItem(generators, AVPItems.TITANIUM_PICKAXE);
        generateHandheldItem(generators, AVPItems.TITANIUM_SHOVEL);
        generateHandheldItem(generators, AVPItems.TITANIUM_SWORD);
        generateHandheldItem(generators, AVPItems.VERITANIUM_AXE);
        generateHandheldItem(generators, AVPItems.VERITANIUM_HOE);
        generateHandheldItem(generators, AVPItems.VERITANIUM_PICKAXE);
        generateHandheldItem(generators, AVPItems.VERITANIUM_SHOVEL);
        generateHandheldItem(generators, AVPItems.VERITANIUM_SWORD);
        generateStandardItem(generators, TempAVPItems.TRANSISTOR);
        generateStandardItem(generators, TempAVPItems.URANIUM_INGOT);
        generateStandardItem(generators, AVPItems.VECTOR_POTTERY_SHERD);
        generateStandardItem(generators, AVPItems.VERITANIUM_SHARD);
        generateStandardItem(generators, TempAVPItems.ZINC_INGOT);

        generateStandardItem(generators, TempAVPItems.FERROALUMINUM_NUGGET);
        generateStandardItem(generators, TempAVPItems.STEEL_NUGGET);
        generateStandardItem(generators, TempAVPItems.BRASS_NUGGET);
        generateStandardItem(generators, TempAVPItems.TITANIUM_NUGGET);
        generateStandardItem(generators, TempAVPItems.ZINC_NUGGET);
        generateStandardItem(generators, TempAVPItems.LEAD_NUGGET);
        generateStandardItem(generators, TempAVPItems.URANIUM_NUGGET);
        generateStandardItem(generators, TempAVPItems.ALUMINUM_NUGGET);

        generateHandheldItem(generators, AVPBlockItems.FERROALUMINUM_DOOR);
        generateHandheldItem(generators, AVPBlockItems.STEEL_DOOR);
        generateHandheldItem(generators, AVPBlockItems.TITANIUM_DOOR);

        generateStandardItem(generators, AVPItems.POISON_JELLY);
    }

    private void generateHandheldItem(ItemModelGenerators generators, Item item) {
        generateStandardItem(generators, item, ModelTemplates.FLAT_HANDHELD_ITEM);
    }

    private void generateStandardItem(ItemModelGenerators generators, Supplier<? extends Item> itemSupplier) {
        generateStandardItem(generators, itemSupplier.get());
    }

    private void generateStandardItem(ItemModelGenerators generators, Item item) {
        generateStandardItem(generators, item, ModelTemplates.FLAT_ITEM);
    }

    private void generateStandardItem(ItemModelGenerators generators, Item item, ModelTemplate modelTemplate) {
        generators.generateFlatItem(item, modelTemplate);
    }

    @Override
    public @NotNull String getName() {
        return "Item Model Definitions";
    }
}
