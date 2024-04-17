package org.avp.common.item;

import net.minecraft.world.item.Item;
import org.avp.api.Holder;
import org.avp.common.registry.AVPDeferredItemRegistry;

public class AVPAmmunitionPartItems extends AVPDeferredItemRegistry {

    public static final AVPAmmunitionPartItems INSTANCE = new AVPAmmunitionPartItems();

    public final Holder<Item> AMMO_CHARGE_PACK;

    public final Holder<Item> AMMO_FLAMETHROWER;

    public final Holder<Item> BULLET_TIP;

    public final Holder<Item> CASING_CASELESS;

    public final Holder<Item> CASING_HEAVY;

    public final Holder<Item> CASING_PISTOL;

    public final Holder<Item> CASING_RIFLE;

    public final Holder<Item> CASING_SHOTGUN;

    public final Holder<Item> ROCKET;

    public final Holder<Item> ROCKET_ELECTRIC;

    public final Holder<Item> ROCKET_INCENDIARY;

    public final Holder<Item> ROCKET_PENETRATION;

    private AVPAmmunitionPartItems() {
        AMMO_CHARGE_PACK = createHolder("ammo_charge_pack", () -> new Item(new Item.Properties()));
        AMMO_FLAMETHROWER = createHolder("ammo_flamethrower", () -> new Item(new Item.Properties()));

        BULLET_TIP = createHolder("bullet_tip", () -> new Item(new Item.Properties()));

        CASING_CASELESS = createHolder("casing_caseless", () -> new Item(new Item.Properties()));
        CASING_HEAVY = createHolder("casing_heavy", () -> new Item(new Item.Properties()));
        CASING_PISTOL = createHolder("casing_pistol", () -> new Item(new Item.Properties()));
        CASING_RIFLE = createHolder("casing_rifle", () -> new Item(new Item.Properties()));
        CASING_SHOTGUN = createHolder("casing_shotgun", () -> new Item(new Item.Properties()));

        ROCKET = createHolder("rocket", () -> new Item(new Item.Properties()));
        ROCKET_ELECTRIC = createHolder("rocket_electric", () -> new Item(new Item.Properties()));
        ROCKET_INCENDIARY = createHolder("rocket_incendiary", () -> new Item(new Item.Properties()));
        ROCKET_PENETRATION = createHolder("rocket_penetration", () -> new Item(new Item.Properties()));
    }
}
