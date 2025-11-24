package com.avp.fabric.data.lang.en_us.provider;

import com.avp.common.registry.AVPRegistryValidation;
import com.avp.common.registry.init.item.AVPArmorItems;
import com.avp.common.registry.init.item.AVPItems;
import com.human.common.registry.init.item.HumanGunItems;
import com.human.common.registry.init.item.HumanSpawnEggItems;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

import java.util.HashSet;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class EnUsItemProvider {

    private static final HashSet<Item> TOUCHED_ENTRIES = new HashSet<>();

    public static final Consumer<FabricLanguageProvider.TranslationBuilder> CONSUMER = builder -> {
        // Combat Items
        addItem(builder, AVPItems.GRENADE, "Grenade");
        addItem(builder, AVPItems.GRENADE_INCENDIARY, "Incendiary Grenade");
        addItem(builder, AVPItems.GRENADE_IRRADIATED, "Irradiated Grenade");
        addItem(builder, AVPItems.CASELESS_BULLET, "Caseless Bullet");
        addItem(builder, HumanGunItems.F903WE_RIFLE, "F903WE Rifle");
        addItem(builder, HumanGunItems.FLAMETHROWER_SEVASTOPOL, "Flamethrower (Sevastopol)");
        addItem(builder, AVPItems.FUEL_TANK, "Fuel Tank");
        addItem(builder, AVPItems.HEAVY_BULLET, "Heavy Bullet");
        addItem(builder, HumanGunItems.M37_12_SHOTGUN, "M37-12 Shotgun");
        addItem(builder, HumanGunItems.M41A_PULSE_RIFLE, "M41A Pulse Rifle");
        addItem(builder, HumanGunItems.M42A3_SNIPER_RIFLE, "M42A3 Sniper Rifle");
        addItem(builder, HumanGunItems.M4RA_BATTLE_RIFLE, "M4RA Battle Rifle");
        addItem(builder, HumanGunItems.M56_SMARTGUN, "M56 Smartgun");
        addItem(builder, HumanGunItems.M6B_ROCKET_LAUNCHER, "M6B Rocket Launcher");
        addItem(builder, HumanGunItems.M88MOD4_COMBAT_PISTOL, "88 Mod 4 Combat Pistol");
        addItem(builder, AVPItems.MEDIUM_BULLET, "Medium Bullet");
        addItem(builder, AVPArmorItems.MK50_BOOTS, "MK50 Boots");
        addItem(builder, AVPArmorItems.MK50_CHESTPLATE, "MK50 Chestplate");
        addItem(builder, AVPArmorItems.MK50_HELMET, "MK50 Helmet");
        addItem(builder, AVPArmorItems.MK50_LEGGINGS, "MK50 Leggings");
        addItem(builder, HumanGunItems.OLD_PAINLESS, "Old Painless");
        addItem(builder, AVPArmorItems.PRESSURE_BOOTS, "Pressure Boots");
        addItem(builder, AVPArmorItems.PRESSURE_CHESTPLATE, "Pressure Chestplate");
        addItem(builder, AVPArmorItems.PRESSURE_HELMET, "Pressure Helmet");
        addItem(builder, AVPArmorItems.PRESSURE_LEGGINGS, "Pressure Leggings");
        addItem(builder, AVPItems.ROCKET, "Rocket");
        addItem(builder, AVPItems.SHOTGUN_SHELL, "Shotgun Shell");
        addItem(builder, AVPItems.SMALL_BULLET, "Small Bullet");
        addItem(builder, AVPArmorItems.STEEL_BOOTS, "Steel Boots");
        addItem(builder, AVPArmorItems.STEEL_CHESTPLATE, "Steel Chestplate");
        addItem(builder, AVPArmorItems.STEEL_HELMET, "Steel Helmet");
        addItem(builder, AVPArmorItems.STEEL_LEGGINGS, "Steel Leggings");
        addItem(builder, AVPArmorItems.TACTICAL_BOOTS, "Tactical Boots");
        addItem(builder, AVPArmorItems.TACTICAL_CHESTPLATE, "Tactical Chestplate");
        addItem(builder, AVPArmorItems.TACTICAL_HELMET, "Tactical Helmet");
        addItem(builder, AVPArmorItems.TACTICAL_LEGGINGS, "Tactical Leggings");
        addItem(builder, AVPArmorItems.TACTICAL_CAMO_BOOTS, "Tactical Camo Boots");
        addItem(builder, AVPArmorItems.TACTICAL_CAMO_CHESTPLATE, "Tactical Camo Chestplate");
        addItem(builder, AVPArmorItems.TACTICAL_CAMO_HELMET, "Tactical Camo Helmet");
        addItem(builder, AVPArmorItems.TACTICAL_CAMO_LEGGINGS, "Tactical Camo Leggings");
        addItem(builder, AVPArmorItems.TITANIUM_BOOTS, "Titanium Boots");
        addItem(builder, AVPArmorItems.TITANIUM_CHESTPLATE, "Titanium Chestplate");
        addItem(builder, AVPArmorItems.TITANIUM_HELMET, "Titanium Helmet");
        addItem(builder, AVPArmorItems.TITANIUM_LEGGINGS, "Titanium Leggings");
        addItem(builder, AVPArmorItems.WY_COMMANDO_BOOTS, "WY Commando Boots");
        addItem(builder, AVPArmorItems.WY_COMMANDO_CHESTPLATE, "WY Commando Chestplate");
        addItem(builder, AVPArmorItems.WY_COMMANDO_HELMET, "WY Commando Helmet");
        addItem(builder, AVPArmorItems.WY_COMMANDO_LEGGINGS, "WY Commando Leggings");
        addItem(builder, AVPArmorItems.WY_ELITE_BOOTS, "WY Elite Boots");
        addItem(builder, AVPArmorItems.WY_ELITE_CHESTPLATE, "WY Elite Chestplate");
        addItem(builder, AVPArmorItems.WY_ELITE_HELMET, "WY Elite Helmet");
        addItem(builder, AVPArmorItems.WY_ELITE_LEGGINGS, "WY Elite Leggings");
        addItem(builder, HumanGunItems.ZX_76_SHOTGUN, "ZX-76 Shotgun");

        // Ingredient Items
        addItem(builder, AVPItems.NUCLEAR_BATTERY, "Nuclear Battery");
        addItem(builder, AVPItems.REDSTONE_CRYSTAL, "Redstone Crystal");
        addItem(builder, AVPItems.SERVO, "Servo");
        addItem(builder, AVPItems.SPEAKER, "Speaker");
        addItem(builder, AVPItems.ALUMINUM_INGOT, "Aluminum Ingot");
        addItem(builder, AVPItems.AUTUNITE_DUST, "Autunite Dust");
        addItem(builder, AVPItems.BARREL, "Barrel");
        addItem(builder, AVPItems.BATTERY_PACK, "Battery Pack");
        addItem(builder, AVPItems.BLUEPRINT_F903WE_RIFLE, "F903WE Rifle Blueprint");
        addItem(builder, AVPItems.BLUEPRINT_FLAMETHROWER_SEVASTOPOL, "Flamethrower (Sevastopol) Blueprint");
        addItem(builder, AVPItems.BLUEPRINT_M37_12_SHOTGUN, "M37-12 Shotgun Blueprint");
        addItem(builder, AVPItems.BLUEPRINT_M41A_PULSE_RIFLE, "M41A Pulse Rifle Blueprint");
        addItem(builder, AVPItems.BLUEPRINT_M42A3_SNIPER_RIFLE, "M42A3 Sniper Rifle Blueprint");
        addItem(builder, AVPItems.BLUEPRINT_M4RA_BATTLE_RIFLE, "M4RA Battle Rifle Blueprint");
        addItem(builder, AVPItems.BLUEPRINT_M56_SMARTGUN, "M56 Smartgun Blueprint");
        addItem(builder, AVPItems.BLUEPRINT_M6B_ROCKET_LAUNCHER, "M6B Rocket Launcher Blueprint");
        addItem(builder, AVPItems.BLUEPRINT_M88MOD4_COMBAT_PISTOL, "M88 Mod 4 Combat Pistol Blueprint");
        addItem(builder, AVPItems.BLUEPRINT_OLD_PAINLESS, "Old Painless Blueprint");
        addItem(builder, AVPItems.BLUEPRINT_ZX_76_SHOTGUN, "ZX-76 Shotgun Blueprint");
        addItem(builder, AVPItems.BRASS_INGOT, "Brass Ingot");
        addItem(builder, AVPItems.CAPACITOR, "Capacitor");
        addItem(builder, AVPItems.CARBON_DUST, "Carbon Dust");
        addItem(builder, AVPItems.CPU, "CPU");
        addItem(builder, AVPItems.DIODE, "Diode");
        addItem(builder, AVPItems.FERROALUMINUM_INGOT, "Ferroaluminum Ingot");
        addItem(builder, AVPItems.GRIP, "Grip");
        addItem(builder, AVPItems.INTEGRATED_CIRCUIT, "Integrated Circuit");
        addItem(builder, AVPItems.LEAD_INGOT, "Lead Ingot");
        addItem(builder, AVPItems.LED, "LED");
        addItem(builder, AVPItems.LED_DISPLAY, "LED Display");
        addItem(builder, AVPItems.LITHIUM_DUST, "Lithium Dust");
        addItem(builder, AVPItems.MINIGUN_BARREL, "Minigun Barrel");
        addItem(builder, AVPItems.NEODYMIUM_MAGNET, "Neodymium Magnet");
        addItem(builder, AVPItems.POLYMER, "Polymer");
        addItem(builder, AVPItems.RAW_BAUXITE, "Raw Bauxite");
        addItem(builder, AVPItems.RAW_BRASS, "Raw Brass");
        addItem(builder, AVPItems.RAW_CRUDE_IRON, "Raw Crude Iron");
        addItem(builder, AVPItems.RAW_FERROBAUXITE, "Raw Ferrobauxite");
        addItem(builder, AVPItems.RAW_GALENA, "Raw Galena");
        addItem(builder, AVPItems.RAW_MONAZITE, "Raw Monazite");
        addItem(builder, AVPItems.SILICON, "Silicon");
        addItem(builder, AVPItems.RAW_TITANIUM, "Raw Titanium");
        addItem(builder, AVPItems.RAW_ZINC, "Raw Zinc");
        addItem(builder, AVPItems.RECEIVER, "Receiver");
        addItem(builder, AVPItems.REGULATOR, "Regulator");
        addItem(builder, AVPItems.RESISTOR, "Resistor");
        addItem(builder, AVPItems.ROCKET_BARREL, "Rocket Barrel");
        addItem(builder, AVPItems.SMART_BARREL, "Smart Barrel");
        addItem(builder, AVPItems.SMART_RECEIVER, "Smart Receiver");
        addItem(builder, AVPItems.STEEL_INGOT, "Steel Ingot");
        addItem(builder, AVPItems.STOCK, "Stock");
        addItem(builder, AVPItems.TITANIUM_INGOT, "Titanium Ingot");
        addItem(builder, AVPItems.TRANSISTOR, "Transistor");
        addItem(builder, AVPItems.URANIUM_INGOT, "Uranium Ingot");
        addItem(builder, AVPItems.ZINC_INGOT, "Zinc Ingot");
        addItem(builder, AVPItems.ALUMINUM_NUGGET, "Aluminum Nugget");
        addItem(builder, AVPItems.BRASS_NUGGET, "Brass Nugget");
        addItem(builder, AVPItems.FERROALUMINUM_NUGGET, "Ferroaluminum Nugget");
        addItem(builder, AVPItems.LEAD_NUGGET, "Lead Nugget");
        addItem(builder, AVPItems.STEEL_NUGGET, "Steel Nugget");
        addItem(builder, AVPItems.TITANIUM_NUGGET, "Titanium Nugget");
        addItem(builder, AVPItems.URANIUM_NUGGET, "Uranium Nugget");
        addItem(builder, AVPItems.ZINC_NUGGET, "Zinc Nugget");

        // Tools & Utilities Items
        addItem(builder, AVPItems.ARMOR_CASE, "Armor Case");
        addItem(builder, AVPItems.CANISTER, "Canister");
        addItem(builder, AVPItems.GENE_READER, "Gene Reader");
        addItem(builder, AVPItems.WATER_CANISTER, "Water Canister");
        addItem(builder, AVPItems.LAVA_CANISTER, "Lava Canister");
        addItem(builder, AVPItems.MILK_CANISTER, "Milk Canister");
        addItem(builder, AVPItems.POWDER_SNOW_CANISTER, "Powder Snow Canister");
        addItem(builder, AVPItems.STEEL_AXE, "Steel Axe");
        addItem(builder, AVPItems.STEEL_HOE, "Steel Hoe");
        addItem(builder, AVPItems.STEEL_PICKAXE, "Steel Pickaxe");
        addItem(builder, AVPItems.STEEL_SHOVEL, "Steel Shovel");
        addItem(builder, AVPItems.STEEL_SWORD, "Steel Sword");
        addItem(builder, AVPItems.SYRINGE, "Syringe");
        addItem(builder, AVPItems.TITANIUM_AXE, "Titanium Axe");
        addItem(builder, AVPItems.TITANIUM_HOE, "Titanium Hoe");
        addItem(builder, AVPItems.TITANIUM_PICKAXE, "Titanium Pickaxe");
        addItem(builder, AVPItems.TITANIUM_SHOVEL, "Titanium Shovel");
        addItem(builder, AVPItems.TITANIUM_SWORD, "Titanium Sword");

        // Spawn Egg Items
        addItem(builder, HumanSpawnEggItems.MARINE_SPAWN_EGG, "Marine Spawn Egg");

        AVPRegistryValidation.throwIfMissingEntries(
            AVPItems.getAll()
                .stream()
                .filter(deferredHolder -> !(deferredHolder.get() instanceof BlockItem))
                .toList(),
            TOUCHED_ENTRIES::contains,
            Item::getDescriptionId,
            "Item translation did not complete successfully - there are unhandled items that need to be handled."
        );
    };

    private static void addItem(
        FabricLanguageProvider.TranslationBuilder translationBuilder,
        Supplier<? extends Item> itemSupplier,
        String value
    ) {
        addItem(translationBuilder, itemSupplier.get(), value);
    }

    private static void addItem(FabricLanguageProvider.TranslationBuilder translationBuilder, Item item, String value) {
        TOUCHED_ENTRIES.add(item);
        translationBuilder.add(item, value);
    }

}
