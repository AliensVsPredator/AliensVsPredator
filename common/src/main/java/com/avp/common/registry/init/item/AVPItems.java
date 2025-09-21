package com.avp.common.registry.init.item;

import com.human.common.gameplay.item.ArmorCaseItem;
import com.human.common.gameplay.item.CanisterItem;
import com.human.common.gameplay.item.GeneReaderItem;
import com.human.common.gameplay.item.MilkCanisterItem;
import com.human.common.gameplay.item.SolidCanisterItem;
import com.human.common.gameplay.item.SyringeItem;
import com.human.common.gameplay.item.grenade.GrenadeItem;
import com.human.common.registry.init.HumanDataComponents;
import com.human.common.registry.init.item.HumanGunItems;
import com.predator.common.gameplay.item.ShurikenItem;
import com.predator.common.gameplay.item.SmartDiscItem;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import com.avp.common.registry.AVPDeferredHolder;
import com.avp.common.registry.init.AVPTiers;
import com.avp.common.registry.key.AVPJukeboxSongKeys;
import com.avp.service.Services;

public class AVPItems {

    private static final List<AVPDeferredHolder<? extends Item>> HOLDERS = new ArrayList<>();

    public static List<AVPDeferredHolder<? extends Item>> getAll() {
        return Collections.unmodifiableList(HOLDERS);
    }

    public static final AVPDeferredHolder<Item> ALUMINUM_INGOT = register("aluminum_ingot");

    public static final AVPDeferredHolder<Item> ALUMINUM_NUGGET = register("aluminum_nugget");

    public static final AVPDeferredHolder<Item> ARMOR_CASE = register(
        "armor_case",
        () -> new ArmorCaseItem(new Item.Properties().stacksTo(1))
    );

    public static final AVPDeferredHolder<Item> AUTUNITE_DUST = register("autunite_dust");

    public static final AVPDeferredHolder<Item> BARREL = register("barrel");

    public static final AVPDeferredHolder<Item> BATTERY_PACK = register("battery_pack");

    public static final AVPDeferredHolder<Item> BLUEPRINT_F903WE_RIFLE = register("blueprint_f903we_rifle");

    public static final AVPDeferredHolder<Item> BLUEPRINT_FLAMETHROWER_SEVASTOPOL = register("blueprint_flamethrower_sevastopol");

    public static final AVPDeferredHolder<Item> BLUEPRINT_M37_12_SHOTGUN = register("blueprint_m37_12_shotgun");

    public static final AVPDeferredHolder<Item> BLUEPRINT_M41A_PULSE_RIFLE = register("blueprint_m41a_pulse_rifle");

    public static final AVPDeferredHolder<Item> BLUEPRINT_M42A3_SNIPER_RIFLE = register("blueprint_m42a3_sniper_rifle");

    public static final AVPDeferredHolder<Item> BLUEPRINT_M4RA_BATTLE_RIFLE = register("blueprint_m4ra_battle_rifle");

    public static final AVPDeferredHolder<Item> BLUEPRINT_M56_SMARTGUN = register("blueprint_m56_smartgun");

    public static final AVPDeferredHolder<Item> BLUEPRINT_M6B_ROCKET_LAUNCHER = register("blueprint_m6b_rocket_launcher");

    public static final AVPDeferredHolder<Item> BLUEPRINT_M88MOD4_COMBAT_PISTOL = register("blueprint_m88mod4_combat_pistol");

    public static final AVPDeferredHolder<Item> BLUEPRINT_OLD_PAINLESS = register("blueprint_old_painless");

    public static final AVPDeferredHolder<Item> BLUEPRINT_ZX_76_SHOTGUN = register("blueprint_zx_76_shotgun");

    public static final AVPDeferredHolder<Item> BRASS_INGOT = register("brass_ingot");

    public static final AVPDeferredHolder<Item> BRASS_NUGGET = register("brass_nugget");

    public static final AVPDeferredHolder<Item> CANISTER = register(
        "canister",
        () -> new CanisterItem(Fluids.EMPTY, new Item.Properties().stacksTo(16))
    );

    public static final AVPDeferredHolder<Item> CAPACITOR = register("capacitor");

    public static final AVPDeferredHolder<Item> CARBON_DUST = register("carbon_dust");

    public static final AVPDeferredHolder<Item> CASELESS_BULLET = register("caseless_bullet");

    public static final AVPDeferredHolder<Item> CPU = register("cpu");

    public static final AVPDeferredHolder<Item> DIODE = register("diode");

    public static final AVPDeferredHolder<Item> FERROALUMINUM_INGOT = register("ferroaluminum_ingot");

    public static final AVPDeferredHolder<Item> FERROALUMINUM_NUGGET = register("ferroaluminum_nugget");

    public static final AVPDeferredHolder<Item> FUEL_TANK = register("fuel_tank", new Item.Properties().stacksTo(1));

    public static final AVPDeferredHolder<Item> GENE_READER = register("gene_reader", GeneReaderItem::new);

    public static final AVPDeferredHolder<Item> GRENADE = register("grenade_standard", () -> new GrenadeItem(false, false));

    public static final AVPDeferredHolder<Item> GRENADE_INCENDIARY = register("grenade_incendiary", () -> new GrenadeItem(true, false));

    public static final AVPDeferredHolder<Item> GRENADE_IRRADIATED = register("grenade_irradiated", () -> new GrenadeItem(false, true));

    public static final AVPDeferredHolder<Item> GRIP = register("grip");

    public static final AVPDeferredHolder<Item> HEAVY_BULLET = register("heavy_bullet");

    public static final AVPDeferredHolder<Item> INTEGRATED_CIRCUIT = register("integrated_circuit");

    public static final AVPDeferredHolder<Item> LAVA_CANISTER = register(
        "lava_canister",
        () -> new CanisterItem(
            Fluids.LAVA,
            new Item.Properties().stacksTo(1).craftRemainder(CANISTER.get()).component(HumanDataComponents.CANISTER_CAPACITY.get(), 1)
        )
    );

    public static final AVPDeferredHolder<Item> LEAD_INGOT = register("lead_ingot");

    public static final AVPDeferredHolder<Item> LEAD_NUGGET = register("lead_nugget");

    public static final AVPDeferredHolder<Item> LED = register("led");

    public static final AVPDeferredHolder<Item> LED_DISPLAY = register("led_display");

    public static final AVPDeferredHolder<Item> LITHIUM_DUST = register("lithium_dust");

    public static final AVPDeferredHolder<Item> MEDIUM_BULLET = register("medium_bullet");

    public static final AVPDeferredHolder<Item> MILK_CANISTER = register(
        "milk_canister",
        () -> new MilkCanisterItem(
            new Item.Properties().craftRemainder(CANISTER.get()).stacksTo(1).component(HumanDataComponents.CANISTER_CAPACITY.get(), 1)
        )
    );

    public static final AVPDeferredHolder<Item> MINIGUN_BARREL = register("minigun_barrel");

    public static final AVPDeferredHolder<Item> NEODYMIUM_MAGNET = register("neodymium_magnet");

    public static final AVPDeferredHolder<Item> NUCLEAR_BATTERY = register("nuclear_battery");

    public static final AVPDeferredHolder<Item> POLYMER = register("polymer");

    public static final AVPDeferredHolder<Item> POWDER_SNOW_CANISTER = register(
        "powder_snow_canister",
        () -> new SolidCanisterItem(
            Blocks.POWDER_SNOW,
            SoundEvents.BUCKET_EMPTY_POWDER_SNOW,
            new Item.Properties().stacksTo(1).component(HumanDataComponents.CANISTER_CAPACITY.get(), 1)
        )
    );

    public static final AVPDeferredHolder<Item> PREDATOR_MUSIC_DISC_1 = register(
        "predator_music_disc_1",
        new Item.Properties().stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(AVPJukeboxSongKeys.PREDATOR_MUSIC_1)
    );

    public static final AVPDeferredHolder<Item> PREDATOR_MUSIC_DISC_1_FRAGMENT = register(
        "predator_music_disc_1_fragment",
        () -> new DiscFragmentItem(new Item.Properties())
    );

    public static final AVPDeferredHolder<Item> RAW_BAUXITE = register("raw_bauxite");

    public static final AVPDeferredHolder<Item> RAW_BRASS = register("raw_brass");

    public static final AVPDeferredHolder<Item> RAW_CRUDE_IRON = register("raw_crude_iron");

    public static final AVPDeferredHolder<Item> RAW_FERROBAUXITE = register("raw_ferrobauxite");

    public static final AVPDeferredHolder<Item> RAW_GALENA = register("raw_galena");

    public static final AVPDeferredHolder<Item> RAW_MONAZITE = register("raw_monazite");

    public static final AVPDeferredHolder<Item> RAW_TITANIUM = register("raw_titanium");

    public static final AVPDeferredHolder<Item> RAW_ZINC = register("raw_zinc");

    public static final AVPDeferredHolder<Item> RECEIVER = register("receiver");

    public static final AVPDeferredHolder<Item> REDSTONE_CRYSTAL = register("redstone_crystal");

    public static final AVPDeferredHolder<Item> REGULATOR = register("regulator");

    public static final AVPDeferredHolder<Item> RESISTOR = register("resistor");

    public static final AVPDeferredHolder<Item> ROCKET = register("rocket");

    public static final AVPDeferredHolder<Item> ROCKET_BARREL = register("rocket_barrel");

    public static final AVPDeferredHolder<Item> SERVO = register("servo");

    public static final AVPDeferredHolder<Item> SHOTGUN_SHELL = register("shotgun_shell");

    public static final AVPDeferredHolder<Item> SHURIKEN = register("shuriken", ShurikenItem::new);

    // TODO: Change this to "silicon" with 0.2.0.
    public static final AVPDeferredHolder<Item> SILICON = register("raw_silica");

    public static final AVPDeferredHolder<Item> SMALL_BULLET = register("small_bullet");

    public static final AVPDeferredHolder<Item> SMART_BARREL = register("smart_barrel");

    public static final AVPDeferredHolder<Item> SMART_DISC = register("smart_disc", SmartDiscItem::new);

    public static final AVPDeferredHolder<Item> SMART_RECEIVER = register("smart_receiver");

    public static final AVPDeferredHolder<Item> SPEAKER = register("speaker");

    public static final AVPDeferredHolder<Item> STOCK = register("stock");

    public static final AVPDeferredHolder<Item> STEEL_AXE = register(
        "steel_axe",
        () -> new AxeItem(AVPTiers.STEEL, new Item.Properties().attributes(AxeItem.createAttributes(AVPTiers.STEEL, 6.0F, -3.1F)))
    );

    public static final AVPDeferredHolder<Item> STEEL_HOE = register(
        "steel_hoe",
        () -> new HoeItem(AVPTiers.STEEL, new Item.Properties().attributes(HoeItem.createAttributes(AVPTiers.STEEL, -2.0F, -1.0F)))
    );

    public static final AVPDeferredHolder<Item> STEEL_INGOT = register("steel_ingot");

    public static final AVPDeferredHolder<Item> STEEL_NUGGET = register("steel_nugget");

    public static final AVPDeferredHolder<Item> STEEL_PICKAXE = register(
        "steel_pickaxe",
        () -> new PickaxeItem(AVPTiers.STEEL, new Item.Properties().attributes(PickaxeItem.createAttributes(AVPTiers.STEEL, 1.0F, -2.8F)))
    );

    public static final AVPDeferredHolder<Item> STEEL_SHOVEL = register(
        "steel_shovel",
        () -> new ShovelItem(AVPTiers.STEEL, new Item.Properties().attributes(ShovelItem.createAttributes(AVPTiers.STEEL, 1.5F, -3.0F)))
    );

    public static final AVPDeferredHolder<Item> STEEL_SWORD = register(
        "steel_sword",
        () -> new SwordItem(AVPTiers.STEEL, new Item.Properties().attributes(SwordItem.createAttributes(AVPTiers.STEEL, 3, -2.4F)))
    );

    public static final AVPDeferredHolder<Item> SYRINGE = register("syringe", SyringeItem::new);

    public static final AVPDeferredHolder<Item> TITANIUM_AXE = register(
        "titanium_axe",
        () -> new AxeItem(AVPTiers.TITANIUM, new Item.Properties().attributes(AxeItem.createAttributes(AVPTiers.TITANIUM, 6.0F, -3.1F)))
    );

    public static final AVPDeferredHolder<Item> TITANIUM_HOE = register(
        "titanium_hoe",
        () -> new HoeItem(AVPTiers.TITANIUM, new Item.Properties().attributes(HoeItem.createAttributes(AVPTiers.TITANIUM, -2.0F, -1.0F)))
    );

    public static final AVPDeferredHolder<Item> TITANIUM_INGOT = register("titanium_ingot");

    public static final AVPDeferredHolder<Item> TITANIUM_NUGGET = register("titanium_nugget");

    public static final AVPDeferredHolder<Item> TITANIUM_PICKAXE = register(
        "titanium_pickaxe",
        () -> new PickaxeItem(
            AVPTiers.TITANIUM,
            new Item.Properties().attributes(PickaxeItem.createAttributes(AVPTiers.TITANIUM, 1.0F, -2.8F))
        )
    );

    public static final AVPDeferredHolder<Item> TITANIUM_SHOVEL = register(
        "titanium_shovel",
        () -> new ShovelItem(
            AVPTiers.TITANIUM,
            new Item.Properties().attributes(ShovelItem.createAttributes(AVPTiers.TITANIUM, 1.5F, -3.0F))
        )
    );

    public static final AVPDeferredHolder<Item> TITANIUM_SWORD = register(
        "titanium_sword",
        () -> new SwordItem(AVPTiers.TITANIUM, new Item.Properties().attributes(SwordItem.createAttributes(AVPTiers.TITANIUM, 3, -2.4F)))
    );

    public static final AVPDeferredHolder<Item> TRANSISTOR = register("transistor");

    public static final AVPDeferredHolder<Item> URANIUM_INGOT = register("uranium_ingot");

    public static final AVPDeferredHolder<Item> URANIUM_NUGGET = register("uranium_nugget");

    public static final AVPDeferredHolder<Item> VERITANIUM_AXE = register(
        "veritanium_axe",
        () -> new AxeItem(
            AVPTiers.VERITANIUM,
            new Item.Properties().fireResistant().attributes(AxeItem.createAttributes(AVPTiers.VERITANIUM, 6.0F, -3.1F))
        )
    );

    public static final AVPDeferredHolder<Item> VERITANIUM_HOE = register(
        "veritanium_hoe",
        () -> new HoeItem(
            AVPTiers.VERITANIUM,
            new Item.Properties().fireResistant().attributes(HoeItem.createAttributes(AVPTiers.VERITANIUM, -2.0F, -1.0F))
        )
    );

    public static final AVPDeferredHolder<Item> VERITANIUM_PICKAXE = register(
        "veritanium_pickaxe",
        () -> new PickaxeItem(
            AVPTiers.VERITANIUM,
            new Item.Properties().fireResistant().attributes(PickaxeItem.createAttributes(AVPTiers.VERITANIUM, 1.0F, -2.8F))
        )
    );

    public static final AVPDeferredHolder<Item> VERITANIUM_SHARD = register("veritanium_shard", new Item.Properties().fireResistant());

    public static final AVPDeferredHolder<Item> VERITANIUM_SHOVEL = register(
        "veritanium_shovel",
        () -> new ShovelItem(
            AVPTiers.VERITANIUM,
            new Item.Properties().fireResistant().attributes(ShovelItem.createAttributes(AVPTiers.VERITANIUM, 1.5F, -3.0F))
        )
    );

    public static final AVPDeferredHolder<Item> VERITANIUM_SWORD = register(
        "veritanium_sword",
        () -> new SwordItem(
            AVPTiers.VERITANIUM,
            new Item.Properties().fireResistant().attributes(SwordItem.createAttributes(AVPTiers.VERITANIUM, 3, -2.4F))
        )
    );

    public static final AVPDeferredHolder<Item> WATER_CANISTER = register(
        "water_canister",
        () -> new CanisterItem(
            Fluids.WATER,
            new Item.Properties().stacksTo(1).craftRemainder(CANISTER.get()).component(HumanDataComponents.CANISTER_CAPACITY.get(), 1)
        )
    );

    public static final AVPDeferredHolder<Item> ZINC_INGOT = register("zinc_ingot");

    public static final AVPDeferredHolder<Item> ZINC_NUGGET = register("zinc_nugget");

    public static AVPDeferredHolder<Item> register(String name) {
        return register(name, new Item.Properties());
    }

    public static AVPDeferredHolder<Item> register(String name, Item.Properties properties) {
        return register(name, () -> new Item(properties));
    }

    public static <T extends Item> AVPDeferredHolder<T> register(String name, Supplier<T> itemSupplier) {
        var holder = Services.REGISTRY.register(BuiltInRegistries.ITEM, name, itemSupplier);
        HOLDERS.add(holder);
        return holder;
    }

    public static void initialize() {
        Services.REGISTRY.registerAzureLibIdentity(HumanGunItems.F903WE_RIFLE);
        Services.REGISTRY.registerAzureLibIdentity(HumanGunItems.FLAMETHROWER_SEVASTOPOL);
        Services.REGISTRY.registerAzureLibIdentity(HumanGunItems.M37_12_SHOTGUN);
        Services.REGISTRY.registerAzureLibIdentity(HumanGunItems.M41A_PULSE_RIFLE);
        Services.REGISTRY.registerAzureLibIdentity(HumanGunItems.M42A3_SNIPER_RIFLE);
        Services.REGISTRY.registerAzureLibIdentity(HumanGunItems.M4RA_BATTLE_RIFLE);
        Services.REGISTRY.registerAzureLibIdentity(HumanGunItems.M56_SMARTGUN);
        Services.REGISTRY.registerAzureLibIdentity(HumanGunItems.M6B_ROCKET_LAUNCHER);
        Services.REGISTRY.registerAzureLibIdentity(HumanGunItems.M88MOD4_COMBAT_PISTOL);
        Services.REGISTRY.registerAzureLibIdentity(HumanGunItems.OLD_PAINLESS);
        Services.REGISTRY.registerAzureLibIdentity(HumanGunItems.ZX_76_SHOTGUN);
    }
}
