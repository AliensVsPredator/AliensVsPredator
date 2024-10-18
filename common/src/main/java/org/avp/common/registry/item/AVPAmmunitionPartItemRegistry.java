package org.avp.common.registry.item;

import net.minecraft.world.item.Item;

import org.avp.api.common.registry.AVPDeferredItemRegistry;
import org.avp.api.common.registry.holder.BLHolder;

public class AVPAmmunitionPartItemRegistry extends AVPDeferredItemRegistry {

    public static final AVPAmmunitionPartItemRegistry INSTANCE = new AVPAmmunitionPartItemRegistry();

    public final BLHolder<Item> ammoChargePack;

    public final BLHolder<Item> ammoFlamethrower;

    public final BLHolder<Item> bulletTip;

    public final BLHolder<Item> casingCaseless;

    public final BLHolder<Item> casingHeavy;

    public final BLHolder<Item> casingPistol;

    public final BLHolder<Item> casingRifle;

    public final BLHolder<Item> casingShotgun;

    public final BLHolder<Item> rocket;

    public final BLHolder<Item> rocketElectric;

    public final BLHolder<Item> rocketIncendiary;

    public final BLHolder<Item> rocketPenetration;

    private AVPAmmunitionPartItemRegistry() {
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
