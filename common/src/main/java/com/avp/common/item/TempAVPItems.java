package com.avp.common.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.DiscFragmentItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;

import java.util.function.Supplier;

import com.avp.common.component.AVPDataComponents;
import com.avp.common.sound.AVPJukeboxSongs;
import com.avp.service.Services;

// TODO: Rename this once multi-loader migration is finished.
public class TempAVPItems {

    public static final Supplier<Item> ABERRANT_CHITIN = register("aberrant_chitin", new Item.Properties().fireResistant());

    public static final Supplier<Item> ABERRANT_RESIN_BALL = register("aberrant_resin_ball", new Item.Properties().fireResistant());

    public static final Supplier<Item> ALIEN_MUSIC_DISC_1 = register(
        "alien_music_disc_1",
        new Item.Properties().stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(AVPJukeboxSongs.ALIEN_MUSIC_1)
    );

    public static final Supplier<Item> ALIEN_MUSIC_DISC_1_FRAGMENT = register(
        "alien_music_disc_1_fragment",
        () -> new DiscFragmentItem(new Item.Properties())
    );

    public static final Supplier<Item> ALUMINUM_INGOT = register("aluminum_ingot");

    public static final Supplier<Item> ALUMINUM_NUGGET = register("aluminum_nugget");

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

    public static final Supplier<Item> BRASS_NUGGET = register("brass_nugget");

    public static final Supplier<Item> BULLET_TIP = register("bullet_tip");

    public static final Supplier<Item> CANISTER = register(
        "canister",
        () -> new CanisterItem(Fluids.EMPTY, new Item.Properties().stacksTo(16))
    );

    public static final Supplier<Item> CAPACITOR = register("capacitor");

    public static final Supplier<Item> CARBON_DUST = register("carbon_dust");

    public static final Supplier<Item> CASELESS_BULLET = register("caseless_bullet");

    public static final Supplier<Item> CASELESS_CARTRIDGE = register("caseless_cartridge");

    public static final Supplier<Item> CHITIN = register("chitin");

    public static final Supplier<Item> CPU = register("cpu");

    public static final Supplier<Item> DIODE = register("diode");

    public static final Supplier<Item> FERROALUMINUM_INGOT = register("ferroaluminum_ingot");

    public static final Supplier<Item> FERROALUMINUM_NUGGET = register("ferroaluminum_nugget");

    public static final Supplier<Item> FUEL_TANK = register("fuel_tank", new Item.Properties().stacksTo(1));

    public static final Supplier<Item> GRIP = register("grip");

    public static final Supplier<Item> HEAVY_BULLET = register("heavy_bullet");

    public static final Supplier<Item> HEAVY_CASING = register("heavy_casing");

    public static final Supplier<Item> INTEGRATED_CIRCUIT = register("integrated_circuit");

    public static final Supplier<Item> IRRADIATED_CHITIN = register("irradiated_chitin");

    public static final Supplier<Item> IRRADIATED_RESIN_BALL = register("irradiated_resin_ball");

    public static final Supplier<Item> LAVA_CANISTER = register(
        "lava_canister",
        () -> new CanisterItem(
            Fluids.LAVA,
            new Item.Properties().stacksTo(1).craftRemainder(CANISTER.get()).component(AVPDataComponents.CANISTER_CAPACITY.get(), 1)
        )
    );

    public static final Supplier<Item> LEAD_INGOT = register("lead_ingot");

    public static final Supplier<Item> LEAD_NUGGET = register("lead_nugget");

    public static final Supplier<Item> LED = register("led");

    public static final Supplier<Item> LED_DISPLAY = register("led_display");

    public static final Supplier<Item> LITHIUM_DUST = register("lithium_dust");

    public static final Supplier<Item> MEDIUM_BULLET = register("medium_bullet");

    public static final Supplier<Item> MEDIUM_CASING = register("medium_casing");

    public static final Supplier<Item> MILK_CANISTER = register(
        "milk_canister",
        () -> new MilkCanisterItem(
            new Item.Properties().craftRemainder(CANISTER.get()).stacksTo(1).component(AVPDataComponents.CANISTER_CAPACITY.get(), 1)
        )
    );

    public static final Supplier<Item> MINIGUN_BARREL = register("minigun_barrel");

    public static final Supplier<Item> NEODYMIUM_MAGNET = register("neodymium_magnet");

    public static final Supplier<Item> NETHER_CHITIN = register("nether_chitin", new Item.Properties().fireResistant());

    public static final Supplier<Item> NETHER_RESIN_BALL = register("nether_resin_ball", new Item.Properties().fireResistant());

    public static final Supplier<Item> NUCLEAR_BATTERY = register("nuclear_battery");

    public static final Supplier<Item> OVOID_POTTERY_SHERD = register("ovoid_pottery_sherd");

    public static final Supplier<Item> PARASITE_POTTERY_SHERD = register("parasite_pottery_sherd");

    public static final Supplier<Item> PLATED_CHITIN = register("plated_chitin");

    public static final Supplier<Item> PLATED_ABERRANT_CHITIN = register("plated_aberrant_chitin", new Item.Properties().fireResistant());

    public static final Supplier<Item> PLATED_IRRADIATED_CHITIN = register("plated_irradiated_chitin");

    public static final Supplier<Item> PLATED_NETHER_CHITIN = register("plated_nether_chitin", new Item.Properties().fireResistant());

    public static final Supplier<Item> POLYMER = register("polymer");

    public static final Supplier<Item> POWDER_SNOW_CANISTER = register(
        "powder_snow_canister",
        () -> new SolidCanisterItem(
            Blocks.POWDER_SNOW,
            SoundEvents.BUCKET_EMPTY_POWDER_SNOW,
            new Item.Properties().stacksTo(1).component(AVPDataComponents.CANISTER_CAPACITY.get(), 1)
        )
    );

    public static final Supplier<Item> PREDATOR_MUSIC_DISC_1 = register(
        "predator_music_disc_1",
        new Item.Properties().stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(AVPJukeboxSongs.PREDATOR_MUSIC_1)
    );

    public static final Supplier<Item> PREDATOR_MUSIC_DISC_1_FRAGMENT = register(
        "predator_music_disc_1_fragment",
        () -> new DiscFragmentItem(new Item.Properties())
    );

    public static final Supplier<Item> RAW_BAUXITE = register("raw_bauxite");

    public static final Supplier<Item> RAW_BRASS = register("raw_brass");

    public static final Supplier<Item> RAW_CRUDE_IRON = register("raw_crude_iron");

    public static final Supplier<Item> RAW_FERROBAUXITE = register("raw_ferrobauxite");

    public static final Supplier<Item> RAW_GALENA = register("raw_galena");

    public static final Supplier<Item> RAW_MONAZITE = register("raw_monazite");

    // FIXME: Make item type RoyalJellyItem.
    public static final Supplier<Item> RAW_ROYAL_JELLY = register("raw_royal_jelly");

    public static final Supplier<Item> RAW_SILICA = register("raw_silica");

    public static final Supplier<Item> RAW_TITANIUM = register("raw_titanium");

    public static final Supplier<Item> RAW_ZINC = register("raw_zinc");

    public static final Supplier<Item> RECEIVER = register("receiver");

    public static final Supplier<Item> REDSTONE_CRYSTAL = register("redstone_crystal");

    public static final Supplier<Item> REGULATOR = register("regulator");

    public static final Supplier<Item> RESIN_BALL = register("resin_ball");

    public static final Supplier<Item> RESISTOR = register("resistor");

    public static final Supplier<Item> ROCKET = register("rocket");

    public static final Supplier<Item> ROCKET_BARREL = register("rocket_barrel");

    public static final Supplier<Item> ROYALTY_POTTERY_SHERD = register("royalty_pottery_sherd");

    public static final Supplier<Item> SERVO = register("servo");

    public static final Supplier<Item> SHOTGUN_CASING = register("shotgun_casing");

    public static final Supplier<Item> SHOTGUN_SHELL = register("shotgun_shell");

    public static final Supplier<Item> SMALL_BULLET = register("small_bullet");

    public static final Supplier<Item> SMALL_CASING = register("small_casing");

    public static final Supplier<Item> SMART_BARREL = register("smart_barrel");

    public static final Supplier<Item> SMART_RECEIVER = register("smart_receiver");

    public static final Supplier<Item> SPEAKER = register("speaker");

    public static final Supplier<Item> STOCK = register("stock");

    public static final Supplier<Item> STEEL_AXE = register(
        "steel_axe",
        () -> new AxeItem(AVPTiers.STEEL, new Item.Properties().attributes(AxeItem.createAttributes(AVPTiers.STEEL, 6.0F, -3.1F)))
    );

    public static final Supplier<Item> STEEL_HOE = register(
        "steel_hoe",
        () -> new HoeItem(AVPTiers.STEEL, new Item.Properties().attributes(HoeItem.createAttributes(AVPTiers.STEEL, -2.0F, -1.0F)))
    );

    public static final Supplier<Item> STEEL_INGOT = register("steel_ingot");

    public static final Supplier<Item> STEEL_NUGGET = register("steel_nugget");

    public static final Supplier<Item> STEEL_PICKAXE = register(
        "steel_pickaxe",
        () -> new PickaxeItem(AVPTiers.STEEL, new Item.Properties().attributes(PickaxeItem.createAttributes(AVPTiers.STEEL, 1.0F, -2.8F)))
    );

    public static final Supplier<Item> STEEL_SHOVEL = register(
        "steel_shovel",
        () -> new ShovelItem(AVPTiers.STEEL, new Item.Properties().attributes(ShovelItem.createAttributes(AVPTiers.STEEL, 1.5F, -3.0F)))
    );

    public static final Supplier<Item> STEEL_SWORD = register(
        "steel_sword",
        () -> new SwordItem(AVPTiers.STEEL, new Item.Properties().attributes(SwordItem.createAttributes(AVPTiers.STEEL, 3, -2.4F)))
    );

    public static final Supplier<Item> TITANIUM_AXE = register(
        "titanium_axe",
        () -> new AxeItem(AVPTiers.TITANIUM, new Item.Properties().attributes(AxeItem.createAttributes(AVPTiers.TITANIUM, 6.0F, -3.1F)))
    );

    public static final Supplier<Item> TITANIUM_HOE = register(
        "titanium_hoe",
        () -> new HoeItem(AVPTiers.TITANIUM, new Item.Properties().attributes(HoeItem.createAttributes(AVPTiers.TITANIUM, -2.0F, -1.0F)))
    );

    public static final Supplier<Item> TITANIUM_INGOT = register("titanium_ingot");

    public static final Supplier<Item> TITANIUM_NUGGET = register("titanium_nugget");

    public static final Supplier<Item> TITANIUM_PICKAXE = register(
        "titanium_pickaxe",
        () -> new PickaxeItem(
            AVPTiers.TITANIUM,
            new Item.Properties().attributes(PickaxeItem.createAttributes(AVPTiers.TITANIUM, 1.0F, -2.8F))
        )
    );

    public static final Supplier<Item> TITANIUM_SHOVEL = register(
        "titanium_shovel",
        () -> new ShovelItem(
            AVPTiers.TITANIUM,
            new Item.Properties().attributes(ShovelItem.createAttributes(AVPTiers.TITANIUM, 1.5F, -3.0F))
        )
    );

    public static final Supplier<Item> TITANIUM_SWORD = register(
        "titanium_sword",
        () -> new SwordItem(AVPTiers.TITANIUM, new Item.Properties().attributes(SwordItem.createAttributes(AVPTiers.TITANIUM, 3, -2.4F)))
    );

    public static final Supplier<Item> TRANSISTOR = register("transistor");

    public static final Supplier<Item> URANIUM_INGOT = register("uranium_ingot");

    public static final Supplier<Item> URANIUM_NUGGET = register("uranium_nugget");

    public static final Supplier<Item> VECTOR_POTTERY_SHERD = register("vector_pottery_sherd");

    public static final Supplier<Item> VERITANIUM_AXE = register(
        "veritanium_axe",
        () -> new AxeItem(
            AVPTiers.VERITANIUM,
            new Item.Properties().fireResistant().attributes(AxeItem.createAttributes(AVPTiers.VERITANIUM, 6.0F, -3.1F))
        )
    );

    public static final Supplier<Item> VERITANIUM_HOE = register(
        "veritanium_hoe",
        () -> new HoeItem(
            AVPTiers.VERITANIUM,
            new Item.Properties().fireResistant().attributes(HoeItem.createAttributes(AVPTiers.VERITANIUM, -2.0F, -1.0F))
        )
    );

    public static final Supplier<Item> VERITANIUM_PICKAXE = register(
        "veritanium_pickaxe",
        () -> new PickaxeItem(
            AVPTiers.VERITANIUM,
            new Item.Properties().fireResistant().attributes(PickaxeItem.createAttributes(AVPTiers.VERITANIUM, 1.0F, -2.8F))
        )
    );

    public static final Supplier<Item> VERITANIUM_SHARD = register("veritanium_shard", new Item.Properties().fireResistant());

    public static final Supplier<Item> VERITANIUM_SHOVEL = register(
        "veritanium_shovel",
        () -> new ShovelItem(
            AVPTiers.VERITANIUM,
            new Item.Properties().fireResistant().attributes(ShovelItem.createAttributes(AVPTiers.VERITANIUM, 1.5F, -3.0F))
        )
    );

    public static final Supplier<Item> VERITANIUM_SWORD = register(
        "veritanium_sword",
        () -> new SwordItem(
            AVPTiers.VERITANIUM,
            new Item.Properties().fireResistant().attributes(SwordItem.createAttributes(AVPTiers.VERITANIUM, 3, -2.4F))
        )
    );

    public static final Supplier<Item> WATER_CANISTER = register(
        "water_canister",
        () -> new CanisterItem(
            Fluids.WATER,
            new Item.Properties().stacksTo(1).craftRemainder(CANISTER.get()).component(AVPDataComponents.CANISTER_CAPACITY.get(), 1)
        )
    );

    public static final Supplier<Item> ZINC_INGOT = register("zinc_ingot");

    public static final Supplier<Item> ZINC_NUGGET = register("zinc_nugget");

    private static Supplier<Item> register(String name) {
        return register(name, new Item.Properties());
    }

    private static Supplier<Item> register(String name, Item.Properties properties) {
        return register(name, () -> new Item(properties));
    }

    private static Supplier<Item> register(String name, Supplier<Item> itemSupplier) {
        return Services.REGISTRY.register(BuiltInRegistries.ITEM, name, itemSupplier);
    }

    public static void initialize() {}
}
