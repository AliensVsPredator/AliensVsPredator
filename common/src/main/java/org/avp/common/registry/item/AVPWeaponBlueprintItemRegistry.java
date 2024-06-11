package org.avp.common.registry.item;

import net.minecraft.world.item.Item;

import org.avp.api.registry.holder.BLHolder;
import org.avp.api.data.item.ItemData;
import org.avp.api.registry.AVPDeferredItemRegistry;

public class AVPWeaponBlueprintItemRegistry extends AVPDeferredItemRegistry {

    public static final AVPWeaponBlueprintItemRegistry INSTANCE = new AVPWeaponBlueprintItemRegistry();

    public final BLHolder<Item> blueprint3712Shotgun;

    public final BLHolder<Item> blueprintAk47;

    public final BLHolder<Item> blueprintF90Rifle;

    public final BLHolder<Item> blueprintFlamethrowerSevastopol;

    public final BLHolder<Item> blueprintM4Carbine;

    public final BLHolder<Item> blueprintM41APulseRifle;

    public final BLHolder<Item> blueprintM56Smartgun;

    public final BLHolder<Item> blueprintM83A2Sadar;

    public final BLHolder<Item> blueprintM88Mod4CombatPistol;

    public final BLHolder<Item> blueprintOldPainless;

    public final BLHolder<Item> blueprintSniperRifle;

    @Override
    protected BLHolder<Item> createHolder(ItemData itemData) {
        return super.createHolder(itemData.withPrefixRegistryName("blueprint_"));
    }

    private AVPWeaponBlueprintItemRegistry() {
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
