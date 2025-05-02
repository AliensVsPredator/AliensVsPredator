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

import com.avp.common.block.AVPBlocks;
import com.avp.common.item.AVPArmorItems;
import com.avp.common.item.AVPBlockItems;
import com.avp.common.item.TempAVPItems;
import com.avp.fabric.common.item.AVPItems;

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
        generateStandardItem(generators, TempAVPItems.SHURIKEN);
        generateStandardItem(generators, TempAVPItems.SMART_DISC);
        generateStandardItem(generators, TempAVPItems.GRENADE);
        generateStandardItem(generators, TempAVPItems.GRENADE_INCENDIARY);
        generateStandardItem(generators, TempAVPItems.GRENADE_IRRADIATED);
        generateStandardItem(generators, TempAVPItems.CASELESS_BULLET);
        generateStandardItem(generators, TempAVPItems.HEAVY_BULLET);
        generateStandardItem(generators, TempAVPItems.SMALL_BULLET);
        generateStandardItem(generators, TempAVPItems.MEDIUM_BULLET);
        generateStandardItem(generators, TempAVPItems.SHOTGUN_SHELL);
        generateStandardItem(generators, AVPArmorItems.ABERRANT_CHITIN_BOOTS);
        generateStandardItem(generators, AVPArmorItems.ABERRANT_CHITIN_CHESTPLATE);
        generateStandardItem(generators, AVPArmorItems.ABERRANT_CHITIN_HELMET);
        generateStandardItem(generators, AVPArmorItems.ABERRANT_CHITIN_LEGGINGS);
        generateStandardItem(generators, AVPArmorItems.CHITIN_BOOTS);
        generateStandardItem(generators, AVPArmorItems.CHITIN_CHESTPLATE);
        generateStandardItem(generators, AVPArmorItems.CHITIN_HELMET);
        generateStandardItem(generators, AVPArmorItems.CHITIN_LEGGINGS);
        generateStandardItem(generators, AVPArmorItems.IRRADIATED_CHITIN_BOOTS);
        generateStandardItem(generators, AVPArmorItems.IRRADIATED_CHITIN_CHESTPLATE);
        generateStandardItem(generators, AVPArmorItems.IRRADIATED_CHITIN_HELMET);
        generateStandardItem(generators, AVPArmorItems.IRRADIATED_CHITIN_LEGGINGS);
        generateStandardItem(generators, TempAVPItems.FUEL_TANK);
        generateStandardItem(generators, AVPArmorItems.JUNGLE_PREDATOR_BOOTS);
        generateStandardItem(generators, AVPArmorItems.JUNGLE_PREDATOR_CHESTPLATE);
        generateStandardItem(generators, AVPArmorItems.JUNGLE_PREDATOR_HELMET);
        generateStandardItem(generators, AVPArmorItems.JUNGLE_PREDATOR_LEGGINGS);
        generateStandardItem(generators, AVPArmorItems.MK50_BOOTS);
        generateStandardItem(generators, AVPArmorItems.MK50_CHESTPLATE);
        generateStandardItem(generators, AVPArmorItems.MK50_HELMET);
        generateStandardItem(generators, AVPArmorItems.MK50_LEGGINGS);
        generateStandardItem(generators, AVPArmorItems.NETHER_CHITIN_BOOTS);
        generateStandardItem(generators, AVPArmorItems.NETHER_CHITIN_CHESTPLATE);
        generateStandardItem(generators, AVPArmorItems.NETHER_CHITIN_HELMET);
        generateStandardItem(generators, AVPArmorItems.NETHER_CHITIN_LEGGINGS);
        generateStandardItem(generators, AVPArmorItems.PLATED_ABERRANT_CHITIN_BOOTS);
        generateStandardItem(generators, AVPArmorItems.PLATED_ABERRANT_CHITIN_CHESTPLATE);
        generateStandardItem(generators, AVPArmorItems.PLATED_ABERRANT_CHITIN_HELMET);
        generateStandardItem(generators, AVPArmorItems.PLATED_ABERRANT_CHITIN_LEGGINGS);
        generateStandardItem(generators, AVPArmorItems.PLATED_CHITIN_BOOTS);
        generateStandardItem(generators, AVPArmorItems.PLATED_CHITIN_CHESTPLATE);
        generateStandardItem(generators, AVPArmorItems.PLATED_CHITIN_HELMET);
        generateStandardItem(generators, AVPArmorItems.PLATED_CHITIN_LEGGINGS);
        generateStandardItem(generators, AVPArmorItems.PLATED_IRRADIATED_CHITIN_BOOTS);
        generateStandardItem(generators, AVPArmorItems.PLATED_IRRADIATED_CHITIN_CHESTPLATE);
        generateStandardItem(generators, AVPArmorItems.PLATED_IRRADIATED_CHITIN_HELMET);
        generateStandardItem(generators, AVPArmorItems.PLATED_IRRADIATED_CHITIN_LEGGINGS);
        generateStandardItem(generators, AVPArmorItems.PLATED_NETHER_CHITIN_BOOTS);
        generateStandardItem(generators, AVPArmorItems.PLATED_NETHER_CHITIN_CHESTPLATE);
        generateStandardItem(generators, AVPArmorItems.PLATED_NETHER_CHITIN_HELMET);
        generateStandardItem(generators, AVPArmorItems.PLATED_NETHER_CHITIN_LEGGINGS);
        generateStandardItem(generators, AVPArmorItems.PRESSURE_BOOTS);
        generateStandardItem(generators, AVPArmorItems.PRESSURE_CHESTPLATE);
        generateStandardItem(generators, AVPArmorItems.PRESSURE_HELMET);
        generateStandardItem(generators, AVPArmorItems.PRESSURE_LEGGINGS);
        generateStandardItem(generators, TempAVPItems.ROCKET);
        generateStandardItem(generators, AVPArmorItems.STEEL_BOOTS);
        generateStandardItem(generators, AVPArmorItems.STEEL_CHESTPLATE);
        generateStandardItem(generators, AVPArmorItems.STEEL_HELMET);
        generateStandardItem(generators, AVPArmorItems.STEEL_LEGGINGS);
        generateStandardItem(generators, AVPArmorItems.TACTICAL_BOOTS);
        generateStandardItem(generators, AVPArmorItems.TACTICAL_CHESTPLATE);
        generateStandardItem(generators, AVPArmorItems.TACTICAL_HELMET);
        generateStandardItem(generators, AVPArmorItems.TACTICAL_LEGGINGS);
        generateStandardItem(generators, AVPArmorItems.TACTICAL_CAMO_BOOTS);
        generateStandardItem(generators, AVPArmorItems.TACTICAL_CAMO_CHESTPLATE);
        generateStandardItem(generators, AVPArmorItems.TACTICAL_CAMO_HELMET);
        generateStandardItem(generators, AVPArmorItems.TACTICAL_CAMO_LEGGINGS);
        generateStandardItem(generators, AVPArmorItems.TITANIUM_BOOTS);
        generateStandardItem(generators, AVPArmorItems.TITANIUM_CHESTPLATE);
        generateStandardItem(generators, AVPArmorItems.TITANIUM_HELMET);
        generateStandardItem(generators, AVPArmorItems.TITANIUM_LEGGINGS);

        generateStandardItem(generators, TempAVPItems.IRRADIATED_CHITIN);
        generateStandardItem(generators, TempAVPItems.PLATED_IRRADIATED_CHITIN);
        generateStandardItem(generators, TempAVPItems.IRRADIATED_RESIN_BALL);
        generateStandardItem(generators, TempAVPItems.ABERRANT_CHITIN);
        generateStandardItem(generators, TempAVPItems.ABERRANT_RESIN_BALL);
        generateStandardItem(generators, TempAVPItems.PLATED_ABERRANT_CHITIN);
        generateStandardItem(generators, TempAVPItems.ALUMINUM_INGOT);
        generateStandardItem(generators, TempAVPItems.ALIEN_MUSIC_DISC_1);
        generateStandardItem(generators, TempAVPItems.PREDATOR_MUSIC_DISC_1);
        generateStandardItem(generators, TempAVPItems.ALIEN_MUSIC_DISC_1_FRAGMENT);
        generateStandardItem(generators, TempAVPItems.PREDATOR_MUSIC_DISC_1_FRAGMENT);
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
        generateStandardItem(generators, TempAVPItems.CANISTER);
        generateStandardItem(generators, TempAVPItems.WATER_CANISTER);
        generateStandardItem(generators, TempAVPItems.LAVA_CANISTER);
        generateStandardItem(generators, TempAVPItems.MILK_CANISTER);
        generateStandardItem(generators, TempAVPItems.POWDER_SNOW_CANISTER);
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
        generateStandardItem(generators, TempAVPItems.NETHER_CHITIN);
        generateStandardItem(generators, TempAVPItems.NETHER_RESIN_BALL);
        generateStandardItem(generators, TempAVPItems.OVOID_POTTERY_SHERD);
        generateStandardItem(generators, TempAVPItems.PARASITE_POTTERY_SHERD);
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
        generateStandardItem(generators, TempAVPItems.ROYALTY_POTTERY_SHERD);
        generateStandardItem(generators, TempAVPItems.SHOTGUN_CASING);
        generateStandardItem(generators, TempAVPItems.SMALL_CASING);
        generateStandardItem(generators, TempAVPItems.SMART_BARREL);
        generateStandardItem(generators, TempAVPItems.SMART_RECEIVER);
        generateHandheldItem(generators, TempAVPItems.STEEL_AXE);
        generateHandheldItem(generators, TempAVPItems.STEEL_HOE);
        generateStandardItem(generators, TempAVPItems.STEEL_INGOT);
        generateHandheldItem(generators, TempAVPItems.STEEL_PICKAXE);
        generateHandheldItem(generators, TempAVPItems.STEEL_SHOVEL);
        generateHandheldItem(generators, TempAVPItems.STEEL_SWORD);
        generateStandardItem(generators, TempAVPItems.STOCK);
        generateHandheldItem(generators, TempAVPItems.TITANIUM_AXE);
        generateHandheldItem(generators, TempAVPItems.TITANIUM_HOE);
        generateStandardItem(generators, TempAVPItems.TITANIUM_INGOT);
        generateHandheldItem(generators, TempAVPItems.TITANIUM_PICKAXE);
        generateHandheldItem(generators, TempAVPItems.TITANIUM_SHOVEL);
        generateHandheldItem(generators, TempAVPItems.TITANIUM_SWORD);
        generateHandheldItem(generators, TempAVPItems.VERITANIUM_AXE);
        generateHandheldItem(generators, TempAVPItems.VERITANIUM_HOE);
        generateHandheldItem(generators, TempAVPItems.VERITANIUM_PICKAXE);
        generateHandheldItem(generators, TempAVPItems.VERITANIUM_SHOVEL);
        generateHandheldItem(generators, TempAVPItems.VERITANIUM_SWORD);
        generateStandardItem(generators, TempAVPItems.TRANSISTOR);
        generateStandardItem(generators, TempAVPItems.URANIUM_INGOT);
        generateStandardItem(generators, TempAVPItems.VECTOR_POTTERY_SHERD);
        generateStandardItem(generators, TempAVPItems.VERITANIUM_SHARD);
        generateStandardItem(generators, TempAVPItems.ZINC_INGOT);

        generateStandardItem(generators, TempAVPItems.FERROALUMINUM_NUGGET);
        generateStandardItem(generators, TempAVPItems.STEEL_NUGGET);
        generateStandardItem(generators, TempAVPItems.BRASS_NUGGET);
        generateStandardItem(generators, TempAVPItems.TITANIUM_NUGGET);
        generateStandardItem(generators, TempAVPItems.ZINC_NUGGET);
        generateStandardItem(generators, TempAVPItems.LEAD_NUGGET);
        generateStandardItem(generators, TempAVPItems.URANIUM_NUGGET);
        generateStandardItem(generators, TempAVPItems.ALUMINUM_NUGGET);

        generateHandheldItem(generators, AVPBlockItems.FERROALUMINUM_DOOR.get());
        generateHandheldItem(generators, AVPBlockItems.STEEL_DOOR.get());
        generateHandheldItem(generators, AVPBlockItems.TITANIUM_DOOR.get());

        generateStandardItem(generators, AVPItems.POISON_JELLY);
    }

    private void generateHandheldItem(ItemModelGenerators generators, Supplier<? extends Item> itemSupplier) {
        generateHandheldItem(generators, itemSupplier.get());
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
