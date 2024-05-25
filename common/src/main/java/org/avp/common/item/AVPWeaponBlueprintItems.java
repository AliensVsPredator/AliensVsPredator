package org.avp.common.item;

import net.minecraft.world.item.Item;

import org.avp.api.Holder;
import org.avp.api.item.ItemData;
import org.avp.common.registry.AVPDeferredItemRegistry;

public class AVPWeaponBlueprintItems extends AVPDeferredItemRegistry {

    public static final AVPWeaponBlueprintItems INSTANCE = new AVPWeaponBlueprintItems();

    public final Holder<Item> blueprint3712Shotgun;

    public final Holder<Item> blueprintAk47;

    public final Holder<Item> blueprintF90Rifle;

    public final Holder<Item> blueprintFlamethrowerSevastopol;

    public final Holder<Item> blueprintM4Carbine;

    public final Holder<Item> blueprintM41APulseRifle;

    public final Holder<Item> blueprintM56Smartgun;

    public final Holder<Item> blueprintM83A2Sadar;

    public final Holder<Item> blueprintM88Mod4CombatPistol;

    public final Holder<Item> blueprintOldPainless;

    public final Holder<Item> blueprintSniperRifle;

    @Override
    protected Holder<Item> createHolder(ItemData itemData) {
        return super.createHolder(itemData.withPrefixRegistryName("blueprint_"));
    }

    private AVPWeaponBlueprintItems() {
        blueprint3712Shotgun = createHolder("37_12_shotgun");
        blueprintAk47 = createHolder("ak_47");
        blueprintF90Rifle = createHolder("f90_rifle");
        blueprintFlamethrowerSevastopol = createHolder("flamethrower_sevastopol");
        blueprintM4Carbine = createHolder("m4_carbine");
        blueprintM41APulseRifle = createHolder("m41a_pulse_rifle");
        blueprintM56Smartgun = createHolder("m56_smartgun");
        blueprintM83A2Sadar = createHolder("m83a2_sadar");
        blueprintM88Mod4CombatPistol = createHolder("m88mod4_combat_pistol");
        blueprintOldPainless = createHolder("old_painless");
        blueprintSniperRifle = createHolder("sniper_rifle");
    }
}
