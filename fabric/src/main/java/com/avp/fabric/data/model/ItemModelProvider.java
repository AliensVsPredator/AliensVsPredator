package com.avp.fabric.data.model;

import com.alien.common.registry.init.AlienItems;
import com.alien.common.registry.init.block.AlienResinBlocks;
import com.alien.common.registry.init.item.AlienArmorItems;
import com.human.common.registry.init.item.HumanFerroaluminumBlockItems;
import com.human.common.registry.init.item.HumanSteelBlockItems;
import com.human.common.registry.init.item.HumanTitaniumBlockItems;
import com.predator.common.registry.init.item.PredatorArmorItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

import com.avp.common.registry.init.block.AVPBlocks;
import com.avp.common.registry.init.item.AVPArmorItems;
import com.avp.common.registry.init.item.AVPItems;

public class ItemModelProvider extends FabricModelProvider {

    public ItemModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators generators) {
        generators.createSimpleFlatItemModel(AlienResinBlocks.NETHER_RESIN_WEB.get());
        generators.createSimpleFlatItemModel(AVPBlocks.RAZOR_WIRE.get());
        generators.createSimpleFlatItemModel(AlienResinBlocks.RESIN_WEB.get());
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
        generateStandardItem(generators, AVPItems.SMALL_BULLET);
        generateStandardItem(generators, AVPItems.MEDIUM_BULLET);
        generateStandardItem(generators, AVPItems.SHOTGUN_SHELL);
        generateStandardItem(generators, AlienArmorItems.ABERRANT_CHITIN_BOOTS);
        generateStandardItem(generators, AlienArmorItems.ABERRANT_CHITIN_CHESTPLATE);
        generateStandardItem(generators, AlienArmorItems.ABERRANT_CHITIN_HELMET);
        generateStandardItem(generators, AlienArmorItems.ABERRANT_CHITIN_LEGGINGS);
        generateStandardItem(generators, AlienArmorItems.CHITIN_BOOTS);
        generateStandardItem(generators, AlienArmorItems.CHITIN_CHESTPLATE);
        generateStandardItem(generators, AlienArmorItems.CHITIN_HELMET);
        generateStandardItem(generators, AlienArmorItems.CHITIN_LEGGINGS);
        generateStandardItem(generators, AlienArmorItems.IRRADIATED_CHITIN_BOOTS);
        generateStandardItem(generators, AlienArmorItems.IRRADIATED_CHITIN_CHESTPLATE);
        generateStandardItem(generators, AlienArmorItems.IRRADIATED_CHITIN_HELMET);
        generateStandardItem(generators, AlienArmorItems.IRRADIATED_CHITIN_LEGGINGS);
        generateStandardItem(generators, AVPItems.FUEL_TANK);
        generateStandardItem(generators, PredatorArmorItems.JUNGLE_PREDATOR_BOOTS);
        generateStandardItem(generators, PredatorArmorItems.JUNGLE_PREDATOR_CHESTPLATE);
        generateStandardItem(generators, PredatorArmorItems.JUNGLE_PREDATOR_HELMET);
        generateStandardItem(generators, PredatorArmorItems.JUNGLE_PREDATOR_LEGGINGS);
        generateStandardItem(generators, AVPArmorItems.MK50_BOOTS);
        generateStandardItem(generators, AVPArmorItems.MK50_CHESTPLATE);
        generateStandardItem(generators, AVPArmorItems.MK50_HELMET);
        generateStandardItem(generators, AVPArmorItems.MK50_LEGGINGS);
        generateStandardItem(generators, AlienArmorItems.NETHER_CHITIN_BOOTS);
        generateStandardItem(generators, AlienArmorItems.NETHER_CHITIN_CHESTPLATE);
        generateStandardItem(generators, AlienArmorItems.NETHER_CHITIN_HELMET);
        generateStandardItem(generators, AlienArmorItems.NETHER_CHITIN_LEGGINGS);
        generateStandardItem(generators, AlienArmorItems.PLATED_ABERRANT_CHITIN_BOOTS);
        generateStandardItem(generators, AlienArmorItems.PLATED_ABERRANT_CHITIN_CHESTPLATE);
        generateStandardItem(generators, AlienArmorItems.PLATED_ABERRANT_CHITIN_HELMET);
        generateStandardItem(generators, AlienArmorItems.PLATED_ABERRANT_CHITIN_LEGGINGS);
        generateStandardItem(generators, AlienArmorItems.PLATED_CHITIN_BOOTS);
        generateStandardItem(generators, AlienArmorItems.PLATED_CHITIN_CHESTPLATE);
        generateStandardItem(generators, AlienArmorItems.PLATED_CHITIN_HELMET);
        generateStandardItem(generators, AlienArmorItems.PLATED_CHITIN_LEGGINGS);
        generateStandardItem(generators, AlienArmorItems.PLATED_IRRADIATED_CHITIN_BOOTS);
        generateStandardItem(generators, AlienArmorItems.PLATED_IRRADIATED_CHITIN_CHESTPLATE);
        generateStandardItem(generators, AlienArmorItems.PLATED_IRRADIATED_CHITIN_HELMET);
        generateStandardItem(generators, AlienArmorItems.PLATED_IRRADIATED_CHITIN_LEGGINGS);
        generateStandardItem(generators, AlienArmorItems.PLATED_NETHER_CHITIN_BOOTS);
        generateStandardItem(generators, AlienArmorItems.PLATED_NETHER_CHITIN_CHESTPLATE);
        generateStandardItem(generators, AlienArmorItems.PLATED_NETHER_CHITIN_HELMET);
        generateStandardItem(generators, AlienArmorItems.PLATED_NETHER_CHITIN_LEGGINGS);
        generateStandardItem(generators, AVPArmorItems.PRESSURE_BOOTS);
        generateStandardItem(generators, AVPArmorItems.PRESSURE_CHESTPLATE);
        generateStandardItem(generators, AVPArmorItems.PRESSURE_HELMET);
        generateStandardItem(generators, AVPArmorItems.PRESSURE_LEGGINGS);
        generateStandardItem(generators, AVPItems.ROCKET);
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

        generateStandardItem(generators, AlienItems.IRRADIATED_CHITIN);
        generateStandardItem(generators, AlienItems.PLATED_IRRADIATED_CHITIN);
        generateStandardItem(generators, AlienItems.IRRADIATED_RESIN_BALL);
        generateStandardItem(generators, AlienItems.ABERRANT_CHITIN);
        generateStandardItem(generators, AlienItems.ABERRANT_RESIN_BALL);
        generateStandardItem(generators, AlienItems.PLATED_ABERRANT_CHITIN);
        generateStandardItem(generators, AVPItems.ALUMINUM_INGOT);
        generateStandardItem(generators, AlienItems.ALIEN_MUSIC_DISC_1);
        generateStandardItem(generators, AVPItems.PREDATOR_MUSIC_DISC_1);
        generateStandardItem(generators, AlienItems.ALIEN_MUSIC_DISC_1_FRAGMENT);
        generateStandardItem(generators, AVPItems.PREDATOR_MUSIC_DISC_1_FRAGMENT);
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
        generateStandardItem(generators, AVPItems.WATER_CANISTER);
        generateStandardItem(generators, AVPItems.LAVA_CANISTER);
        generateStandardItem(generators, AVPItems.MILK_CANISTER);
        generateStandardItem(generators, AVPItems.POWDER_SNOW_CANISTER);
        generateStandardItem(generators, AVPItems.CAPACITOR);
        generateStandardItem(generators, AVPItems.NUCLEAR_BATTERY);
        generateStandardItem(generators, AVPItems.REDSTONE_CRYSTAL);
        generateStandardItem(generators, AVPItems.SERVO);
        generateStandardItem(generators, AVPItems.SPEAKER);
        generateStandardItem(generators, AVPItems.CARBON_DUST);
        generateStandardItem(generators, AVPItems.CASELESS_CARTRIDGE);
        generateStandardItem(generators, AlienItems.CHITIN);
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
        generateStandardItem(generators, AlienItems.NETHER_CHITIN);
        generateStandardItem(generators, AlienItems.NETHER_RESIN_BALL);
        generateStandardItem(generators, AlienItems.OVOID_POTTERY_SHERD);
        generateStandardItem(generators, AlienItems.PARASITE_POTTERY_SHERD);
        generateStandardItem(generators, AlienItems.PLATED_CHITIN);
        generateStandardItem(generators, AlienItems.PLATED_NETHER_CHITIN);
        generateStandardItem(generators, AVPItems.POLYMER);
        generateStandardItem(generators, AVPItems.RAW_BAUXITE);
        generateStandardItem(generators, AVPItems.RAW_BRASS);
        generateStandardItem(generators, AVPItems.RAW_CRUDE_IRON);
        generateStandardItem(generators, AVPItems.RAW_FERROBAUXITE);
        generateStandardItem(generators, AVPItems.RAW_GALENA);
        generateStandardItem(generators, AVPItems.RAW_MONAZITE);
        generateStandardItem(generators, AlienItems.RAW_ROYAL_JELLY);
        generateStandardItem(generators, AVPItems.RAW_TITANIUM);
        generateStandardItem(generators, AVPItems.RAW_ZINC);
        generateStandardItem(generators, AVPItems.RECEIVER);
        generateStandardItem(generators, AVPItems.REGULATOR);
        generateStandardItem(generators, AlienItems.RESIN_BALL);
        generateStandardItem(generators, AVPItems.RESISTOR);
        generateStandardItem(generators, AVPItems.ROCKET_BARREL);
        generateStandardItem(generators, AlienItems.ROYALTY_POTTERY_SHERD);
        generateStandardItem(generators, AVPItems.SHOTGUN_CASING);
        generateStandardItem(generators, AVPItems.SILICON);
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
        generateStandardItem(generators, AlienItems.VECTOR_POTTERY_SHERD);
        generateStandardItem(generators, AVPItems.VERITANIUM_SHARD);
        generateStandardItem(generators, AVPItems.ZINC_INGOT);

        generateStandardItem(generators, AVPItems.FERROALUMINUM_NUGGET);
        generateStandardItem(generators, AVPItems.STEEL_NUGGET);
        generateStandardItem(generators, AVPItems.SYRINGE);
        generateStandardItem(generators, AVPItems.BRASS_NUGGET);
        generateStandardItem(generators, AVPItems.TITANIUM_NUGGET);
        generateStandardItem(generators, AVPItems.ZINC_NUGGET);
        generateStandardItem(generators, AVPItems.LEAD_NUGGET);
        generateStandardItem(generators, AVPItems.URANIUM_NUGGET);
        generateStandardItem(generators, AVPItems.ALUMINUM_NUGGET);

        generateHandheldItem(generators, HumanFerroaluminumBlockItems.FERROALUMINUM_DOOR);
        generateHandheldItem(generators, HumanSteelBlockItems.STEEL_DOOR);
        generateHandheldItem(generators, HumanTitaniumBlockItems.TITANIUM_DOOR);

        generateStandardItem(generators, AlienItems.POISON_JELLY);
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
