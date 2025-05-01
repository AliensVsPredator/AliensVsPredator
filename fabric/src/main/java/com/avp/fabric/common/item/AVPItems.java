package com.avp.fabric.common.item;

import mod.azure.azurelib.rewrite.animation.cache.AzIdentityRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;

import java.util.function.Function;

import com.avp.AVPResources;
import com.avp.common.component.AVPDataComponents;
import com.avp.fabric.common.item.grenades.GrendeItem;
import com.avp.fabric.common.item.gun.GunData;
import com.avp.fabric.common.item.old_painless.OldPainlessItem;
import com.avp.fabric.common.item.yautja_items.ShurikenItem;
import com.avp.fabric.common.item.yautja_items.SmartDiscItem;
import com.avp.fabric.common.sound.AVPJukeboxSongs;

public class AVPItems {

    // Combat Items
    public static final Item SMART_DISC = register(new SmartDiscItem(), "smart_disc");

    public static final Item SHURIKEN = register(new ShurikenItem(), "shuriken");

    public static final Item GRENADE = register(new GrendeItem(false, false), "grenade_standard");

    public static final Item GRENADE_INCENDIARY = register(new GrendeItem(true, false), "grenade_incendiary");

    public static final Item GRENADE_IRRADIATED = register(new GrendeItem(false, true), "grenade_irradiated");

    public static final Item CASELESS_BULLET = register("caseless_bullet");

    public static final Item F903WE_RIFLE = register(new GunItem(GunData.F903WE_RIFLE), "f903we_rifle");

    public static final Item FLAMETHROWER_SEVASTOPOL = register(
        new GunItem(GunData.FLAMETHROWER_SEVASTOPOL),
        "flamethrower_sevastopol"
    );

    public static final Item FUEL_TANK = register(new Item.Properties().stacksTo(1), "fuel_tank");

    public static final Item HEAVY_BULLET = register("heavy_bullet");

    public static final Item M37_12_SHOTGUN = register(new GunItem(GunData.M37_12_SHOTGUN), "m37_12_shotgun");

    public static final Item M41A_PULSE_RIFLE = register(new GunItem(GunData.M41A_PULSE_RIFLE), "m41a_pulse_rifle");

    public static final Item M42A3_SNIPER_RIFLE = register(new GunItem(GunData.M42A3_SNIPER_RIFLE), "m42a3_sniper_rifle");

    public static final Item M4RA_BATTLE_RIFLE = register(new GunItem(GunData.M4RA_BATTLE_RIFLE), "m4ra_battle_rifle");

    public static final Item M56_SMARTGUN = register(new GunItem(GunData.M56_SMARTGUN), "m56_smartgun");

    public static final Item M6B_ROCKET_LAUNCHER = register(new GunItem(GunData.M6B_ROCKET_LAUNCHER), "m6b_rocket_launcher");

    public static final Item M88MOD4_COMBAT_PISTOL = register(
        new GunItem(GunData.M88_MOD_4_COMBAT_PISTOL),
        "m88mod4_combat_pistol"
    );

    public static final Item MEDIUM_BULLET = register("medium_bullet");

    public static final Item OLD_PAINLESS = register(new OldPainlessItem(), "old_painless");

    public static final Item ZX_76_SHOTGUN = register(new GunItem(GunData.ZX_76_SHOTGUN), "zx_76_shotgun");

    // Decorative Items
    public static final Item OVOID_POTTERY_SHERD = register("ovoid_pottery_sherd");

    public static final Item PARASITE_POTTERY_SHERD = register("parasite_pottery_sherd");

    public static final Item ROYALTY_POTTERY_SHERD = register("royalty_pottery_sherd");

    public static final Item VECTOR_POTTERY_SHERD = register("vector_pottery_sherd");

    // Music Disc Items
    public static final Item ALIEN_MUSIC_DISC_1 = register(
        new Item.Properties().stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(AVPJukeboxSongs.ALIEN_MUSIC_1),
        "alien_music_disc_1"
    );

    public static final Item PREDATOR_MUSIC_DISC_1 = register(
        new Item.Properties().stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(AVPJukeboxSongs.PREDATOR_MUSIC_1),
        "predator_music_disc_1"
    );

    public static final Item ALIEN_MUSIC_DISC_1_FRAGMENT = register(
        new DiscFragmentItem(new Item.Properties()),
        "alien_music_disc_1_fragment"
    );

    public static final Item PREDATOR_MUSIC_DISC_1_FRAGMENT = register(
        new DiscFragmentItem(new Item.Properties()),
        "predator_music_disc_1_fragment"
    );

    // Material Items

    public static final Item NETHER_CHITIN = register(new Item.Properties().fireResistant(), "nether_chitin");

    public static final Item NETHER_RESIN_BALL = register(new Item.Properties().fireResistant(), "nether_resin_ball");

    public static final Item ABERRANT_CHITIN = register(new Item.Properties().fireResistant(), "aberrant_chitin");

    public static final Item PLATED_ABERRANT_CHITIN = register(new Item.Properties().fireResistant(), "plated_aberrant_chitin");

    public static final Item ABERRANT_RESIN_BALL = register(new Item.Properties().fireResistant(), "aberrant_resin_ball");

    public static final Item POISON_JELLY = register(new PoisionJellyItem(), "poison_jelly");

    public static final Item VERITANIUM_SHARD = register(new Item.Properties().fireResistant(), "veritanium_shard");

    // Tools & Utilities
    public static final Item ARMOR_CASE = register(new ArmorCaseItem(new Item.Properties().stacksTo(1)), "armor_case");

    public static final Item CANISTER = register(
        new CanisterItem(Fluids.EMPTY, new Item.Properties().stacksTo(16)),
        "canister"
    );

    public static final Item WATER_CANISTER = register(
        new CanisterItem(
            Fluids.WATER,
            new Item.Properties().stacksTo(1).craftRemainder(CANISTER).component(AVPDataComponents.CANISTER_CAPACITY.get(), 1)
        ),
        "water_canister"
    );

    public static final Item LAVA_CANISTER = register(
        new CanisterItem(
            Fluids.LAVA,
            new Item.Properties().stacksTo(1).craftRemainder(CANISTER).component(AVPDataComponents.CANISTER_CAPACITY.get(), 1)
        ),
        "lava_canister"
    );

    public static final Item MILK_CANISTER = register(
        new MilkCanisterItem(
            new Item.Properties().craftRemainder(CANISTER).stacksTo(1).component(AVPDataComponents.CANISTER_CAPACITY.get(), 1)
        ),
        "milk_canister"
    );

    public static final Item POWDER_SNOW_CANISTER = register(
        new SolidCanisterItem(
            Blocks.POWDER_SNOW,
            SoundEvents.BUCKET_EMPTY_POWDER_SNOW,
            new Item.Properties().stacksTo(1).component(AVPDataComponents.CANISTER_CAPACITY.get(), 1)
        ),
        "powder_snow_canister"
    );

    public static final Item STEEL_AXE = register(
        new AxeItem(AVPTiers.STEEL, new Item.Properties().attributes(AxeItem.createAttributes(AVPTiers.STEEL, 6.0F, -3.1F))),
        "steel_axe"
    );

    public static final Item STEEL_HOE = register(
        new HoeItem(AVPTiers.STEEL, new Item.Properties().attributes(HoeItem.createAttributes(AVPTiers.STEEL, -2.0F, -1.0F))),
        "steel_hoe"
    );

    public static final Item STEEL_PICKAXE = register(
        new PickaxeItem(AVPTiers.STEEL, new Item.Properties().attributes(PickaxeItem.createAttributes(AVPTiers.STEEL, 1.0F, -2.8F))),
        "steel_pickaxe"
    );

    public static final Item STEEL_SHOVEL = register(
        new ShovelItem(AVPTiers.STEEL, new Item.Properties().attributes(ShovelItem.createAttributes(AVPTiers.STEEL, 1.5F, -3.0F))),
        "steel_shovel"
    );

    public static final Item STEEL_SWORD = register(
        new SwordItem(AVPTiers.STEEL, new Item.Properties().attributes(SwordItem.createAttributes(AVPTiers.STEEL, 3, -2.4F))),
        "steel_sword"
    );

    public static final Item TITANIUM_AXE = register(
        new AxeItem(AVPTiers.TITANIUM, new Item.Properties().attributes(AxeItem.createAttributes(AVPTiers.TITANIUM, 6.0F, -3.1F))),
        "titanium_axe"
    );

    public static final Item TITANIUM_HOE = register(
        new HoeItem(AVPTiers.TITANIUM, new Item.Properties().attributes(HoeItem.createAttributes(AVPTiers.TITANIUM, -2.0F, -1.0F))),
        "titanium_hoe"
    );

    public static final Item TITANIUM_PICKAXE = register(
        new PickaxeItem(AVPTiers.TITANIUM, new Item.Properties().attributes(PickaxeItem.createAttributes(AVPTiers.TITANIUM, 1.0F, -2.8F))),
        "titanium_pickaxe"
    );

    public static final Item TITANIUM_SHOVEL = register(
        new ShovelItem(AVPTiers.TITANIUM, new Item.Properties().attributes(ShovelItem.createAttributes(AVPTiers.TITANIUM, 1.5F, -3.0F))),
        "titanium_shovel"
    );

    public static final Item TITANIUM_SWORD = register(
        new SwordItem(AVPTiers.TITANIUM, new Item.Properties().attributes(SwordItem.createAttributes(AVPTiers.TITANIUM, 3, -2.4F))),
        "titanium_sword"
    );

    public static final Item VERITANIUM_AXE = register(
        new AxeItem(
            AVPTiers.VERITANIUM,
            new Item.Properties().fireResistant().attributes(AxeItem.createAttributes(AVPTiers.VERITANIUM, 6.0F, -3.1F))
        ),
        "veritanium_axe"
    );

    public static final Item VERITANIUM_HOE = register(
        new HoeItem(
            AVPTiers.VERITANIUM,
            new Item.Properties().fireResistant().attributes(HoeItem.createAttributes(AVPTiers.VERITANIUM, -2.0F, -1.0F))
        ),
        "veritanium_hoe"
    );

    public static final Item VERITANIUM_PICKAXE = register(
        new PickaxeItem(
            AVPTiers.VERITANIUM,
            new Item.Properties().fireResistant().attributes(PickaxeItem.createAttributes(AVPTiers.VERITANIUM, 1.0F, -2.8F))
        ),
        "veritanium_pickaxe"
    );

    public static final Item VERITANIUM_SHOVEL = register(
        new ShovelItem(
            AVPTiers.VERITANIUM,
            new Item.Properties().fireResistant().attributes(ShovelItem.createAttributes(AVPTiers.VERITANIUM, 1.5F, -3.0F))
        ),
        "veritanium_shovel"
    );

    public static final Item VERITANIUM_SWORD = register(
        new SwordItem(
            AVPTiers.VERITANIUM,
            new Item.Properties().fireResistant().attributes(SwordItem.createAttributes(AVPTiers.VERITANIUM, 3, -2.4F))
        ),
        "veritanium_sword"
    );

    public static Item register(Function<Item.Properties, Item> itemSupplier, String id) {
        return register(itemSupplier.apply(new Item.Properties()), id);
    }

    public static Item register(Item.Properties itemProperties, String id) {
        return register(new Item(itemProperties), id);
    }

    public static Item register(Item item, String id) {
        var resourceLocation = AVPResources.location(id);
        return Registry.register(BuiltInRegistries.ITEM, resourceLocation, item);
    }

    public static Item register(String id) {
        return register(new Item(new Item.Properties()), id);
    }

    public static void initialize() {
        AzIdentityRegistry.register(
            OLD_PAINLESS,
            F903WE_RIFLE,
            FLAMETHROWER_SEVASTOPOL,
            M4RA_BATTLE_RIFLE,
            M6B_ROCKET_LAUNCHER,
            M37_12_SHOTGUN,
            M41A_PULSE_RIFLE,
            M42A3_SNIPER_RIFLE,
            M56_SMARTGUN,
            M88MOD4_COMBAT_PISTOL,
            ZX_76_SHOTGUN
        );
    }
}
