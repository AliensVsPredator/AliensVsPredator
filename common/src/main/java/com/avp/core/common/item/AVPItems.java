package com.avp.core.common.item;

import com.avp.core.platform.service.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;

import java.util.function.Function;
import java.util.function.Supplier;

import com.avp.core.common.item.gun.GunData;
import com.avp.core.common.item.old_painless.OldPainlessItem;

public class AVPItems {

    // Combat Items
    public static final Supplier<Item> CASELESS_BULLET = register("caseless_bullet");

    public static final Supplier<Item> F903WE_RIFLE = register(() -> new GunItem(GunData.F903WE_RIFLE), "f903we_rifle");

    public static final Supplier<Item> FLAMETHROWER_SEVASTOPOL = register(
        () -> new GunItem(GunData.FLAMETHROWER_SEVASTOPOL),
        "flamethrower_sevastopol"
    );

    public static final Supplier<Item> FUEL_TANK = register(new Item.Properties().stacksTo(1), "fuel_tank");

    public static final Supplier<Item> HEAVY_BULLET = register("heavy_bullet");

    public static final Supplier<Item> M37_12_SHOTGUN = register(() -> new GunItem(GunData.M37_12_SHOTGUN), "m37_12_shotgun");

    public static final Supplier<Item> M41A_PULSE_RIFLE = register(() -> new GunItem(GunData.M41A_PULSE_RIFLE), "m41a_pulse_rifle");

    public static final Supplier<Item> M42A3_SNIPER_RIFLE = register(() -> new GunItem(GunData.M42A3_SNIPER_RIFLE), "m42a3_sniper_rifle");

    public static final Supplier<Item> M4RA_BATTLE_RIFLE = register(() -> new GunItem(GunData.M4RA_BATTLE_RIFLE), "m4ra_battle_rifle");

    public static final Supplier<Item> M56_SMARTGUN = register(() -> new GunItem(GunData.M56_SMARTGUN), "m56_smartgun");

    public static final Supplier<Item> M6B_ROCKET_LAUNCHER = register(() -> new GunItem(GunData.M6B_ROCKET_LAUNCHER), "m6b_rocket_launcher");

    public static final Supplier<Item> M88_MOD_4_COMBAT_PISTOL = register(
        () -> new GunItem(GunData.M88_MOD_4_COMBAT_PISTOL),
        "m88mod4_combat_pistol"
    );

    public static final Supplier<Item> MEDIUM_BULLET = register("medium_bullet");

    public static final Supplier<Item> OLD_PAINLESS = register(OldPainlessItem::new, "old_painless");

    public static final Supplier<Item> ROCKET = register("rocket");

    public static final Supplier<Item> SHOTGUN_BULLET = register("shotgun_bullet");

    public static final Supplier<Item> SMALL_BULLET = register("small_bullet");

    public static final Supplier<Item> ZX_76_SHOTGUN = register(() -> new GunItem(GunData.ZX_76_SHOTGUN), "zx_76_shotgun");

    // Decorative Items
    public static final Supplier<Item> OVOID_POTTERY_SHERD = register("ovoid_pottery_sherd");

    public static final Supplier<Item> PARASITE_POTTERY_SHERD = register("parasite_pottery_sherd");

    public static final Supplier<Item> ROYALTY_POTTERY_SHERD = register("royalty_pottery_sherd");

    public static final Supplier<Item> VECTOR_POTTERY_SHERD = register("vector_pottery_sherd");

    // Material Items
    public static final Supplier<Item> ALUMINUM_INGOT = register("aluminum_ingot");

    public static final Supplier<Item> AUTUNITE_DUST = register("autunite_dust");

    public static final Supplier<Item> BARREL = register("barrel");

    public static final Supplier<Item> BATTERY_PACK = register("battery_pack");

    public static final Supplier<Item> BLUEPRINT_F903WE_RIFLE = register("blueprint_f903we_rifle");

    public static final Supplier<Item> BLUEPRINT_FLAMETHROWER_SEVASTOPOL = register("blueprint_flamethrower_sevastopol");

    public static final Supplier<Item> BLUEPRINT_M37_12_SHOTGUN = register("blueprint_m37_12_shotgun");

    public static final Supplier<Item> BLUEPRINT_M41A_PULSE_RIFLE = register("blueprint_m41a_pulse_rifle");

    public static final Supplier<Item> BLUEPRINT_M42A3_SNIPER_RIFLE = register("blueprint_m42a3_sniper_rifle");

    public static final Supplier<Item> BLUEPRINT_M4RA_BATTLE_RIFLE = register("blueprint_m4ra_battle_rifle");

    public static final Supplier<Item> BLUEPRINT_M56_SMARTGUN = register("blueprint_m56_smartgun");

    public static final Supplier<Item> BLUEPRINT_M6B_ROCKET_LAUNCHER = register("blueprint_m6b_rocket_launcher");

    public static final Supplier<Item> BLUEPRINT_M88MOD4_COMBAT_PISTOL = register("blueprint_m88mod4_combat_pistol");

    public static final Supplier<Item> BLUEPRINT_OLD_PAINLESS = register("blueprint_old_painless");

    public static final Supplier<Item> BLUEPRINT_ZX_76_SHOTGUN = register("blueprint_zx_76_shotgun");

    public static final Supplier<Item> BRASS_INGOT = register("brass_ingot");

    public static final Supplier<Item> BULLET_TIP = register("bullet_tip");

    public static final Supplier<Item> CAPACITOR = register("capacitor");

    public static final Supplier<Item> CARBON_DUST = register("carbon_dust");

    public static final Supplier<Item> CASELESS_CASING = register("caseless_casing");

    public static final Supplier<Item> CHITIN = register("chitin");

    public static final Supplier<Item> CPU = register("cpu");

    public static final Supplier<Item> DIODE = register("diode");

    public static final Supplier<Item> FERROALUMINUM_INGOT = register("ferroaluminum_ingot");

    public static final Supplier<Item> GRIP = register("grip");

    public static final Supplier<Item> HEAVY_CASING = register("heavy_casing");

    public static final Supplier<Item> INTEGRATED_CIRCUIT = register("integrated_circuit");

    public static final Supplier<Item> LEAD_INGOT = register("lead_ingot");

    public static final Supplier<Item> LED = register("led");

    public static final Supplier<Item> LED_DISPLAY = register("led_display");

    public static final Supplier<Item> LITHIUM_DUST = register("lithium_dust");

    public static final Supplier<Item> MEDIUM_CASING = register("medium_casing");

    public static final Supplier<Item> MINIGUN_BARREL = register("minigun_barrel");

    public static final Supplier<Item> NEODYMIUM_MAGNET = register("neodymium_magnet");

    public static final Supplier<Item> NETHER_CHITIN = register(new Item.Properties().fireResistant(), "nether_chitin");

    public static final Supplier<Item> NETHER_RESIN_BALL = register(new Item.Properties().fireResistant(), "nether_resin_ball");

    public static final Supplier<Item> PLATED_CHITIN = register("plated_chitin");

    public static final Supplier<Item> PLATED_NETHER_CHITIN = register(new Item.Properties().fireResistant(), "plated_nether_chitin");

    public static final Supplier<Item> POLYMER = register("polymer");

    public static final Supplier<Item> RAW_BAUXITE = register("raw_bauxite");

    public static final Supplier<Item> RAW_BRASS = register("raw_brass");

    public static final Supplier<Item> RAW_CRUDE_IRON = register("raw_crude_iron");

    public static final Supplier<Item> RAW_FERROBAUXITE = register("raw_ferrobauxite");

    public static final Supplier<Item> RAW_GALENA = register("raw_galena");

    public static final Supplier<Item> RAW_MONAZITE = register("raw_monazite");

    public static final Supplier<Item> RAW_ROYAL_JELLY = register("raw_royal_jelly");

    public static final Supplier<Item> RAW_SILICA = register("raw_silica");

    public static final Supplier<Item> RAW_TITANIUM = register("raw_titanium");

    public static final Supplier<Item> RAW_ZINC = register("raw_zinc");

    public static final Supplier<Item> RECEIVER = register("receiver");

    public static final Supplier<Item> REGULATOR = register("regulator");

    public static final Supplier<Item> RESIN_BALL = register("resin_ball");

    public static final Supplier<Item> RESISTOR = register("resistor");

    public static final Supplier<Item> ROCKET_BARREL = register("rocket_barrel");

    public static final Supplier<Item> SHOTGUN_CASING = register("shotgun_casing");

    public static final Supplier<Item> SMALL_CASING = register("small_casing");

    public static final Supplier<Item> SMART_BARREL = register("smart_barrel");

    public static final Supplier<Item> SMART_RECEIVER = register("smart_receiver");

    public static final Supplier<Item> STOCK = register("stock");

    public static final Supplier<Item> STEEL_INGOT = register("steel_ingot");

    public static final Supplier<Item> TITANIUM_INGOT = register("titanium_ingot");

    public static final Supplier<Item> TRANSISTOR = register("transistor");

    public static final Supplier<Item> URANIUM_INGOT = register("uranium_ingot");

    public static final Supplier<Item> VERITANIUM_SHARD = register(new Item.Properties().fireResistant(), "veritanium_shard");

    public static final Supplier<Item> ZINC_INGOT = register("zinc_ingot");

    // Tools & Utilities
    public static final Supplier<Item> ARMOR_CASE = register(() -> new ArmorCaseItem(new Item.Properties().stacksTo(1)), "armor_case");

    public static final Supplier<Item> CANISTER = register("canister");

    public static final Supplier<Item> ROYAL_JELLY_CANISTER = register("royal_jelly_canister");

    public static final Supplier<Item> STEEL_AXE = register(
        () -> new AxeItem(AVPTiers.STEEL, new Item.Properties().attributes(AxeItem.createAttributes(AVPTiers.STEEL, 6.0F, -3.1F))),
        "steel_axe"
    );

    public static final Supplier<Item> STEEL_HOE = register(
        () -> new HoeItem(AVPTiers.STEEL, new Item.Properties().attributes(HoeItem.createAttributes(AVPTiers.STEEL, -2.0F, -1.0F))),
        "steel_hoe"
    );

    public static final Supplier<Item> STEEL_PICKAXE = register(
        () -> new PickaxeItem(AVPTiers.STEEL, new Item.Properties().attributes(PickaxeItem.createAttributes(AVPTiers.STEEL, 1.0F, -2.8F))),
        "steel_pickaxe"
    );

    public static final Supplier<Item> STEEL_SHOVEL = register(
        () -> new ShovelItem(AVPTiers.STEEL, new Item.Properties().attributes(ShovelItem.createAttributes(AVPTiers.STEEL, 1.5F, -3.0F))),
        "steel_shovel"
    );

    public static final Supplier<Item> STEEL_SWORD = register(
        () -> new SwordItem(AVPTiers.STEEL, new Item.Properties().attributes(SwordItem.createAttributes(AVPTiers.STEEL, 3, -2.4F))),
        "steel_sword"
    );

    public static final Supplier<Item> TITANIUM_AXE = register(
        () -> new AxeItem(AVPTiers.TITANIUM, new Item.Properties().attributes(AxeItem.createAttributes(AVPTiers.TITANIUM, 6.0F, -3.1F))),
        "titanium_axe"
    );

    public static final Supplier<Item> TITANIUM_HOE = register(
        () -> new HoeItem(AVPTiers.TITANIUM, new Item.Properties().attributes(HoeItem.createAttributes(AVPTiers.TITANIUM, -2.0F, -1.0F))),
        "titanium_hoe"
    );

    public static final Supplier<Item> TITANIUM_PICKAXE = register(
        () -> new PickaxeItem(AVPTiers.TITANIUM, new Item.Properties().attributes(PickaxeItem.createAttributes(AVPTiers.TITANIUM, 1.0F, -2.8F))),
        "titanium_pickaxe"
    );

    public static final Supplier<Item> TITANIUM_SHOVEL = register(
        () -> new ShovelItem(AVPTiers.TITANIUM, new Item.Properties().attributes(ShovelItem.createAttributes(AVPTiers.TITANIUM, 1.5F, -3.0F))),
        "titanium_shovel"
    );

    public static final Supplier<Item> TITANIUM_SWORD = register(
        () -> new SwordItem(AVPTiers.TITANIUM, new Item.Properties().attributes(SwordItem.createAttributes(AVPTiers.TITANIUM, 3, -2.4F))),
        "titanium_sword"
    );

    public static final Supplier<Item> VERITANIUM_AXE = register(
        () -> new AxeItem(
            AVPTiers.VERITANIUM,
            new Item.Properties().fireResistant().attributes(AxeItem.createAttributes(AVPTiers.VERITANIUM, 6.0F, -3.1F))
        ),
        "veritanium_axe"
    );

    public static final Supplier<Item> VERITANIUM_HOE = register(
        () -> new HoeItem(
            AVPTiers.VERITANIUM,
            new Item.Properties().fireResistant().attributes(HoeItem.createAttributes(AVPTiers.VERITANIUM, -2.0F, -1.0F))
        ),
        "veritanium_hoe"
    );

    public static final Supplier<Item> VERITANIUM_PICKAXE = register(
        () -> new PickaxeItem(
            AVPTiers.VERITANIUM,
            new Item.Properties().fireResistant().attributes(PickaxeItem.createAttributes(AVPTiers.VERITANIUM, 1.0F, -2.8F))
        ),
        "veritanium_pickaxe"
    );

    public static final Supplier<Item> VERITANIUM_SHOVEL = register(
        () -> new ShovelItem(
            AVPTiers.VERITANIUM,
            new Item.Properties().fireResistant().attributes(ShovelItem.createAttributes(AVPTiers.VERITANIUM, 1.5F, -3.0F))
        ),
        "veritanium_shovel"
    );

    public static final Supplier<Item> VERITANIUM_SWORD = register(
        () -> new SwordItem(
            AVPTiers.VERITANIUM,
            new Item.Properties().fireResistant().attributes(SwordItem.createAttributes(AVPTiers.VERITANIUM, 3, -2.4F))
        ),
        "veritanium_sword"
    );

    public static Supplier<Item> register(Function<Item.Properties, Item> itemSupplier, String id) {
        return register(() -> itemSupplier.apply(new Item.Properties()), id);
    }

    public static Supplier<Item> register(Item.Properties itemProperties, String id) {
        return register(() -> new Item(itemProperties), id);
    }

    public static Supplier<Item> register(String id) {
        return register(() -> new Item(new Item.Properties()), id);
    }

    public static Supplier<Item> register(Supplier<Item> itemSupplier, String id) {
        return Services.REGISTRY.registerItem(id, itemSupplier);
    }

    public static void initialize() {
        // FIXME:
//        AzIdentityRegistry.register(OLD_PAINLESS);
    }
}
