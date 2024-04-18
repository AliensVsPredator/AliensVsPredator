package org.avp.common.item;

import net.minecraft.world.item.Item;

import org.avp.api.Holder;
import org.avp.common.registry.AVPDeferredItemRegistry;

public class AVPAmmunitionPartItems extends AVPDeferredItemRegistry {

    public static final AVPAmmunitionPartItems INSTANCE = new AVPAmmunitionPartItems();

    public final Holder<Item> ammoChargePack;

    public final Holder<Item> ammoFlamethrower;

    public final Holder<Item> bulletTip;

    public final Holder<Item> casingCaseless;

    public final Holder<Item> casingHeavy;

    public final Holder<Item> casingPistol;

    public final Holder<Item> casingRifle;

    public final Holder<Item> casingShotgun;

    public final Holder<Item> rocket;

    public final Holder<Item> rocketElectric;

    public final Holder<Item> rocketIncendiary;

    public final Holder<Item> rocketPenetration;

    private AVPAmmunitionPartItems() {
        ammoChargePack = createHolder("ammo_charge_pack");
        ammoFlamethrower = createHolder("ammo_flamethrower");

        bulletTip = createHolder("bullet_tip");

        casingCaseless = createHolder("casing_caseless");
        casingHeavy = createHolder("casing_heavy");
        casingPistol = createHolder("casing_pistol");
        casingRifle = createHolder("casing_rifle");
        casingShotgun = createHolder("casing_shotgun");

        rocket = createHolder("rocket");
        rocketElectric = createHolder("rocket_electric");
        rocketIncendiary = createHolder("rocket_incendiary");
        rocketPenetration = createHolder("rocket_penetration");
    }
}
