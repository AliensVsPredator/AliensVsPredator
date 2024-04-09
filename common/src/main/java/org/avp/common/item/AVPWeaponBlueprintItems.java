package org.avp.common.item;

import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.avp.api.GameObject;
import org.avp.common.service.Services;

/**
 * @author Boston Vanseghi
 */
public class AVPWeaponBlueprintItems {

    private static final List<GameObject<Item>> ENTRIES = new ArrayList<>();

    public static final GameObject<Item> BLUEPRINT_37_12_SHOTGUN;

    public static final GameObject<Item> BLUEPRINT_AK_47;

    public static final GameObject<Item> BLUEPRINT_F90_RIFLE;

    public static final GameObject<Item> BLUEPRINT_FLAMETHROWER_SEVASTOPOL;

    public static final GameObject<Item> BLUEPRINT_M4_CARBINE;

    public static final GameObject<Item> BLUEPRINT_M41A_PULSE_RIFLE;

    public static final GameObject<Item> BLUEPRINT_M56_SMARTGUN;

    public static final GameObject<Item> BLUEPRINT_M83A2_SADAR;

    public static final GameObject<Item> BLUEPRINT_M88MOD4_COMBAT_PISTOL;

    public static final GameObject<Item> BLUEPRINT_OLD_PAINLESS;

    public static final GameObject<Item> BLUEPRINT_SNIPER_RIFLE;

    public static void forceInitialization() {
        // This method doesn't need to do anything
    }

    public static List<GameObject<Item>> getEntries() {
        return ENTRIES;
    }

    private static GameObject<Item> register(String registryName) {
        Supplier<Item> itemSupplier = () -> new Item(new Item.Properties());
        var gameObject = Services.ITEM_REGISTRY.register("blueprint_" + registryName, itemSupplier);
        ENTRIES.add(gameObject);
        return gameObject;
    }

    private AVPWeaponBlueprintItems() {}

    static {
        BLUEPRINT_37_12_SHOTGUN = register("37_12_shotgun");
        BLUEPRINT_AK_47 = register("ak_47");
        BLUEPRINT_F90_RIFLE = register("f90_rifle");
        BLUEPRINT_FLAMETHROWER_SEVASTOPOL = register("flamethrower_sevastopol");
        BLUEPRINT_M4_CARBINE = register("m4_carbine");
        BLUEPRINT_M41A_PULSE_RIFLE = register("m41a_pulse_rifle");
        BLUEPRINT_M56_SMARTGUN = register("m56_smartgun");
        BLUEPRINT_M83A2_SADAR = register("m83a2_sadar");
        BLUEPRINT_M88MOD4_COMBAT_PISTOL = register("m88mod4_combat_pistol");
        BLUEPRINT_OLD_PAINLESS = register("old_painless");
        BLUEPRINT_SNIPER_RIFLE = register("sniper_rifle");
    }
}
