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

    private static GameObject<Item> register(String registryName, Supplier<Item> itemSupplier) {
        var gameObject = Services.ITEM_REGISTRY.register(registryName, itemSupplier);
        ENTRIES.add(gameObject);
        return gameObject;
    }

    private AVPWeaponBlueprintItems() {}

    static {
        BLUEPRINT_37_12_SHOTGUN = register("blueprint_37_12_shotgun", () -> new Item(new Item.Properties()));
        BLUEPRINT_AK_47 = register("blueprint_ak_47", () -> new Item(new Item.Properties()));
        BLUEPRINT_F90_RIFLE = register("blueprint_f90_rifle", () -> new Item(new Item.Properties()));
        BLUEPRINT_FLAMETHROWER_SEVASTOPOL = register("blueprint_flamethrower_sevastopol", () -> new Item(new Item.Properties()));
        BLUEPRINT_M4_CARBINE = register("blueprint_m4_carbine", () -> new Item(new Item.Properties()));
        BLUEPRINT_M41A_PULSE_RIFLE = register("blueprint_m41a_pulse_rifle", () -> new Item(new Item.Properties()));
        BLUEPRINT_M56_SMARTGUN = register("blueprint_m56_smartgun", () -> new Item(new Item.Properties()));
        BLUEPRINT_M83A2_SADAR = register("blueprint_m83a2_sadar", () -> new Item(new Item.Properties()));
        BLUEPRINT_M88MOD4_COMBAT_PISTOL = register("blueprint_m88mod4_combat_pistol", () -> new Item(new Item.Properties()));
        BLUEPRINT_OLD_PAINLESS = register("blueprint_old_painless", () -> new Item(new Item.Properties()));
        BLUEPRINT_SNIPER_RIFLE = register("blueprint_sniper_rifle", () -> new Item(new Item.Properties()));
    }
}
