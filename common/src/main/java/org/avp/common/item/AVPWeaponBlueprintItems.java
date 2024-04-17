package org.avp.common.item;

import net.minecraft.world.item.Item;

import org.avp.api.Holder;
import org.avp.common.registry.AVPDeferredItemRegistry;

public class AVPWeaponBlueprintItems extends AVPDeferredItemRegistry {

    public static final AVPWeaponBlueprintItems INSTANCE = new AVPWeaponBlueprintItems();

    public final Holder<Item> BLUEPRINT_37_12_SHOTGUN;

    public final Holder<Item> BLUEPRINT_AK_47;

    public final Holder<Item> BLUEPRINT_F90_RIFLE;

    public final Holder<Item> BLUEPRINT_FLAMETHROWER_SEVASTOPOL;

    public final Holder<Item> BLUEPRINT_M4_CARBINE;

    public final Holder<Item> BLUEPRINT_M41A_PULSE_RIFLE;

    public final Holder<Item> BLUEPRINT_M56_SMARTGUN;

    public final Holder<Item> BLUEPRINT_M83A2_SADAR;

    public final Holder<Item> BLUEPRINT_M88MOD4_COMBAT_PISTOL;

    public final Holder<Item> BLUEPRINT_OLD_PAINLESS;

    public final Holder<Item> BLUEPRINT_SNIPER_RIFLE;

    private AVPWeaponBlueprintItems() {
        BLUEPRINT_37_12_SHOTGUN = createHolder("37_12_shotgun");
        BLUEPRINT_AK_47 = createHolder("ak_47");
        BLUEPRINT_F90_RIFLE = createHolder("f90_rifle");
        BLUEPRINT_FLAMETHROWER_SEVASTOPOL = createHolder("flamethrower_sevastopol");
        BLUEPRINT_M4_CARBINE = createHolder("m4_carbine");
        BLUEPRINT_M41A_PULSE_RIFLE = createHolder("m41a_pulse_rifle");
        BLUEPRINT_M56_SMARTGUN = createHolder("m56_smartgun");
        BLUEPRINT_M83A2_SADAR = createHolder("m83a2_sadar");
        BLUEPRINT_M88MOD4_COMBAT_PISTOL = createHolder("m88mod4_combat_pistol");
        BLUEPRINT_OLD_PAINLESS = createHolder("old_painless");
        BLUEPRINT_SNIPER_RIFLE = createHolder("sniper_rifle");
    }

    @Override
    protected Holder<Item> createHolder(String registryName) {
        return super.createHolder("blueprint_" + registryName, () -> new Item(new Item.Properties()));
    }
}
