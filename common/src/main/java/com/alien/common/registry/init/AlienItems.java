package com.alien.common.registry.init;

import com.alien.common.gameplay.item.PoisonJellyItem;
import com.alien.common.gameplay.item.RoyalJellyItem;
import net.minecraft.world.item.DiscFragmentItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import com.avp.common.registry.AVPDeferredHolder;
import com.avp.common.registry.init.item.AVPItems;
import com.avp.common.registry.key.AVPJukeboxSongKeys;

public class AlienItems {

    public static final AVPDeferredHolder<Item> ABERRANT_CHITIN = AVPItems.register(
        "aberrant_chitin",
        new Item.Properties().fireResistant()
    );

    public static final AVPDeferredHolder<Item> ABERRANT_RESIN_BALL = AVPItems.register(
        "aberrant_resin_ball",
        new Item.Properties().fireResistant()
    );

    public static final AVPDeferredHolder<Item> ALIEN_MUSIC_DISC_1 = AVPItems.register(
        "alien_music_disc_1",
        new Item.Properties().stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(AVPJukeboxSongKeys.ALIEN_MUSIC_1)
    );

    public static final AVPDeferredHolder<Item> ALIEN_MUSIC_DISC_1_FRAGMENT = AVPItems.register(
        "alien_music_disc_1_fragment",
        () -> new DiscFragmentItem(new Item.Properties())
    );

    public static final AVPDeferredHolder<Item> CHITIN = AVPItems.register("chitin");

    public static final AVPDeferredHolder<Item> IRRADIATED_CHITIN = AVPItems.register("irradiated_chitin");

    public static final AVPDeferredHolder<Item> IRRADIATED_RESIN_BALL = AVPItems.register("irradiated_resin_ball");

    public static final AVPDeferredHolder<Item> NETHER_CHITIN = AVPItems.register("nether_chitin", new Item.Properties().fireResistant());

    public static final AVPDeferredHolder<Item> NETHER_RESIN_BALL = AVPItems.register(
        "nether_resin_ball",
        new Item.Properties().fireResistant()
    );

    public static final AVPDeferredHolder<Item> OVOID_POTTERY_SHERD = AVPItems.register("ovoid_pottery_sherd");

    public static final AVPDeferredHolder<Item> PARASITE_POTTERY_SHERD = AVPItems.register("parasite_pottery_sherd");

    public static final AVPDeferredHolder<Item> PLATED_CHITIN = AVPItems.register("plated_chitin");

    public static final AVPDeferredHolder<Item> PLATED_ABERRANT_CHITIN = AVPItems.register(
        "plated_aberrant_chitin",
        new Item.Properties().fireResistant()
    );

    public static final AVPDeferredHolder<Item> PLATED_IRRADIATED_CHITIN = AVPItems.register("plated_irradiated_chitin");

    public static final AVPDeferredHolder<Item> PLATED_NETHER_CHITIN = AVPItems.register(
        "plated_nether_chitin",
        new Item.Properties().fireResistant()
    );

    public static final AVPDeferredHolder<Item> POISON_JELLY = AVPItems.register("poison_jelly", PoisonJellyItem::new);

    public static final AVPDeferredHolder<Item> RAW_ROYAL_JELLY = AVPItems.register("raw_royal_jelly", RoyalJellyItem::new);

    public static final AVPDeferredHolder<Item> RESIN_BALL = AVPItems.register("resin_ball");

    public static final AVPDeferredHolder<Item> ROYALTY_POTTERY_SHERD = AVPItems.register("royalty_pottery_sherd");

    public static final AVPDeferredHolder<Item> VECTOR_POTTERY_SHERD = AVPItems.register("vector_pottery_sherd");

    public static void initialize() {}
}
