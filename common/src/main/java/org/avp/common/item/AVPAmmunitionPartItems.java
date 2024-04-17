package org.avp.common.item;

import net.minecraft.world.item.Item;
import org.avp.api.GameObject;
import org.avp.common.registry.AVPDeferredItemRegistry;

public class AVPAmmunitionPartItems extends AVPDeferredItemRegistry {

    public static final AVPAmmunitionPartItems INSTANCE = new AVPAmmunitionPartItems();

    public final GameObject<Item> AMMO_CHARGE_PACK;

    public final GameObject<Item> AMMO_FLAMETHROWER;

    public final GameObject<Item> BULLET_TIP;

    public final GameObject<Item> CASING_CASELESS;

    public final GameObject<Item> CASING_HEAVY;

    public final GameObject<Item> CASING_PISTOL;

    public final GameObject<Item> CASING_RIFLE;

    public final GameObject<Item> CASING_SHOTGUN;

    public final GameObject<Item> ROCKET;

    public final GameObject<Item> ROCKET_ELECTRIC;

    public final GameObject<Item> ROCKET_INCENDIARY;

    public final GameObject<Item> ROCKET_PENETRATION;

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
