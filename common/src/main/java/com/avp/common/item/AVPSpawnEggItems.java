package com.avp.common.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

import java.util.function.Supplier;

import com.avp.common.entity.type.TempAVPEntityTypes;
import com.avp.common.registry.AVPDeferredHolder;
import com.avp.service.Services;

public class AVPSpawnEggItems {

    public static final AVPDeferredHolder<Item> ABERRANT_CHESTBURSTER_SPAWN_EGG = register(
        "aberrant_chestburster",
        TempAVPEntityTypes.ABERRANT_CHESTBURSTER,
        0xD9D9B8,
        0xBFBF88
    );

    public static final AVPDeferredHolder<Item> ABERRANT_DRONE_SPAWN_EGG = register(
        "aberrant_drone",
        TempAVPEntityTypes.ABERRANT_DRONE,
        0xE5BF05,
        0xD7D7DF
    );

    public static final AVPDeferredHolder<Item> ABERRANT_FACEHUGGER_SPAWN_EGG = register(
        "aberrant_facehugger",
        TempAVPEntityTypes.ABERRANT_FACEHUGGER,
        0xCDCCA3,
        0xC9CE3B
    );

    public static final AVPDeferredHolder<Item> ABERRANT_OVAMORPH_SPAWN_EGG = register(
        "aberrant_ovamorph",
        TempAVPEntityTypes.ABERRANT_OVAMORPH,
        0x767139,
        0xC9C432
    );

    public static final AVPDeferredHolder<Item> ABERRANT_PRAETORIAN_SPAWN_EGG = register(
        "aberrant_praetorian",
        TempAVPEntityTypes.ABERRANT_PRAETORIAN,
        0xF1C810,
        0xD7D7DF
    );

    public static final AVPDeferredHolder<Item> ABERRANT_WARRIOR_SPAWN_EGG = register(
        "aberrant_warrior",
        TempAVPEntityTypes.ABERRANT_WARRIOR,
        0xF3CE07,
        0xD7D7DF
    );

    public static final AVPDeferredHolder<Item> ABERRANT_QUEEN_SPAWN_EGG = register(
        "aberrant_queen",
        TempAVPEntityTypes.ABERRANT_QUEEN,
        0xF3CE07,
        0xD7D7DF
    );

    public static final AVPDeferredHolder<Item> CHESTBURSTER_SPAWN_EGG = register(
        "chestburster",
        TempAVPEntityTypes.CHESTBURSTER,
        0xD8B877,
        0xF7E2B4
    );

    public static final AVPDeferredHolder<Item> DRONE_SPAWN_EGG = register("drone", TempAVPEntityTypes.DRONE, 0x010202, 0xDFE2E4);

    public static final AVPDeferredHolder<Item> FACEHUGGER_SPAWN_EGG = register(
        "facehugger",
        TempAVPEntityTypes.FACEHUGGER,
        0xE4D597,
        0xA55863
    );

    public static final AVPDeferredHolder<Item> IRRADIATED_DRONE_SPAWN_EGG = register(
        "irradiated_drone",
        TempAVPEntityTypes.IRRADIATED_DRONE,
        0xD5F2ED,
        0x73D9CF
    );

    public static final AVPDeferredHolder<Item> IRRADIATED_PRAETORIAN_SPAWN_EGG = register(
        "irradiated_praetorian",
        TempAVPEntityTypes.IRRADIATED_PRAETORIAN,
        0xD5F2ED,
        0x73D9CF
    );

    public static final AVPDeferredHolder<Item> IRRADIATED_QUEEN_SPAWN_EGG = register(
        "irradiated_queen",
        TempAVPEntityTypes.IRRADIATED_QUEEN,
        0xD5F2ED,
        0x73D9CF
    );

    public static final AVPDeferredHolder<Item> IRRADIATED_WARRIOR_SPAWN_EGG = register(
        "irradiated_warrior",
        TempAVPEntityTypes.IRRADIATED_WARRIOR,
        0xD5F2ED,
        0x73D9CF
    );

    public static final AVPDeferredHolder<Item> MARINE_SPAWN_EGG = register("marine", TempAVPEntityTypes.MARINE, 0x5a5941, 0x414441);

    public static final AVPDeferredHolder<Item> NETHER_CHESTBURSTER_SPAWN_EGG = register(
        "nether_chestburster",
        TempAVPEntityTypes.NETHER_CHESTBURSTER,
        0x360101,
        0x803232
    );

    public static final AVPDeferredHolder<Item> NETHER_DRONE_SPAWN_EGG = register(
        "nether_drone",
        TempAVPEntityTypes.NETHER_DRONE,
        0x360101,
        0x803232
    );

    public static final AVPDeferredHolder<Item> NETHER_FACEHUGGER_SPAWN_EGG = register(
        "nether_facehugger",
        TempAVPEntityTypes.NETHER_FACEHUGGER,
        0x27221C,
        0xFAD855
    );

    public static final AVPDeferredHolder<Item> NETHER_OVAMORPH_SPAWN_EGG = register(
        "nether_ovamorph",
        TempAVPEntityTypes.NETHER_OVAMORPH,
        0x20131C,
        0xFCEE4B
    );

    public static final AVPDeferredHolder<Item> NETHER_PRAETORIAN_SPAWN_EGG = register(
        "nether_praetorian",
        TempAVPEntityTypes.NETHER_PRAETORIAN,
        0x310808,
        0x5d1f1f
    );

    public static final AVPDeferredHolder<Item> NETHER_WARRIOR_SPAWN_EGG = register(
        "nether_warrior",
        TempAVPEntityTypes.NETHER_WARRIOR,
        0x2b0000,
        0x67261f
    );

    public static final AVPDeferredHolder<Item> NETHER_QUEEN_SPAWN_EGG = register(
        "nether_queen",
        TempAVPEntityTypes.NETHER_QUEEN,
        0x2b0000,
        0x67261f
    );

    public static final AVPDeferredHolder<Item> OVAMORPH_SPAWN_EGG = register(
        "ovamorph",
        TempAVPEntityTypes.OVAMORPH,
        0x615B45,
        0xBF7872
    );

    public static final AVPDeferredHolder<Item> PRAETORIAN_SPAWN_EGG = register(
        "praetorian",
        TempAVPEntityTypes.PRAETORIAN,
        0x010202,
        0x363534
    );

    public static final AVPDeferredHolder<Item> QUEEN_SPAWN_EGG = register("queen", TempAVPEntityTypes.QUEEN, 0x010202, 0x363534);

    public static final AVPDeferredHolder<Item> ROYAL_ABERRANT_CHESTBURSTER_SPAWN_EGG = register(
        "royal_aberrant_chestburster",
        TempAVPEntityTypes.ROYAL_ABERRANT_CHESTBURSTER,
        0x706c36,
        0xd3cf51
    );

    public static final AVPDeferredHolder<Item> ROYAL_ABERRANT_FACEHUGGER_SPAWN_EGG = register(
        "royal_aberrant_facehugger",
        TempAVPEntityTypes.ROYAL_ABERRANT_FACEHUGGER,
        0x706c36,
        0xd3cf51
    );

    public static final AVPDeferredHolder<Item> ROYAL_ABERRANT_OVAMORPH_SPAWN_EGG = register(
        "royal_aberrant_ovamorph",
        TempAVPEntityTypes.ROYAL_ABERRANT_OVAMORPH,
        0x706c36,
        0xd3cf51
    );

    public static final AVPDeferredHolder<Item> ROYAL_NETHER_CHESTBURSTER_SPAWN_EGG = register(
        "royal_nether_chestburster",
        TempAVPEntityTypes.ROYAL_NETHER_CHESTBURSTER,
        0x331715,
        0xfcee4b
    );

    public static final AVPDeferredHolder<Item> ROYAL_CHESTBURSTER_SPAWN_EGG = register(
        "royal_chestburster",
        TempAVPEntityTypes.ROYAL_CHESTBURSTER,
        0x29140a,
        0xe6b57b
    );

    public static final AVPDeferredHolder<Item> ROYAL_FACEHUGGER_SPAWN_EGG = register(
        "royal_facehugger",
        TempAVPEntityTypes.ROYAL_FACEHUGGER,
        0x4b4946,
        0x292729
    );

    public static final AVPDeferredHolder<Item> ROYAL_NETHER_FACEHUGGER_SPAWN_EGG = register(
        "royal_nether_facehugger",
        TempAVPEntityTypes.ROYAL_NETHER_FACEHUGGER,
        0x331715,
        0xfcee4b
    );

    public static final AVPDeferredHolder<Item> ROYAL_NETHER_OVAMORPH_SPAWN_EGG = register(
        "royal_nether_ovamorph",
        TempAVPEntityTypes.ROYAL_NETHER_OVAMORPH,
        0x331715,
        0xfcee4b
    );

    public static final AVPDeferredHolder<Item> ROYAL_OVAMORPH_SPAWN_EGG = register(
        "royal_ovamorph",
        TempAVPEntityTypes.ROYAL_OVAMORPH,
        0x2a2918,
        0x34341f
    );

    public static final AVPDeferredHolder<Item> WARRIOR_SPAWN_EGG = register("warrior", TempAVPEntityTypes.WARRIOR, 0x010202, 0x4A4E55);

    public static final AVPDeferredHolder<Item> YAUTJA_SPAWN_EGG = register("yautja", TempAVPEntityTypes.YAUTJA, 0xB9A86C, 0x5A4728);

    private static AVPDeferredHolder<Item> register(
        String baseId,
        Supplier<? extends EntityType<? extends Mob>> entityTypeSupplier,
        int primaryColor,
        int secondaryColor
    ) {
        return Services.REGISTRY.register(
            BuiltInRegistries.ITEM,
            baseId + "_spawn_egg",
            () -> new SpawnEggItem(entityTypeSupplier.get(), primaryColor, secondaryColor, new Item.Properties())
        );
    }

    public static void initialize() {}
}
