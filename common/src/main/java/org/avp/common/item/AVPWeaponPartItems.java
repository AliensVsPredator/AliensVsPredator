package org.avp.common.item;

import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.avp.api.GameObject;
import org.avp.common.registry.AVPDeferredItemRegistry;
import org.avp.common.service.Services;

public class AVPWeaponPartItems extends AVPDeferredItemRegistry {

    public static final AVPWeaponPartItems INSTANCE = new AVPWeaponPartItems();

    public final GameObject<Item> WEAPON_PART_BARREL_GENERIC = createHolder("barrel");

    public final GameObject<Item> WEAPON_PART_BARREL_MINIGUN = createHolder("barrel_minigun");

    public final GameObject<Item> WEAPON_PART_BARREL_ROCKET = createHolder("barrel_rocket");

    public final GameObject<Item> WEAPON_PART_BARREL_SMART = createHolder("barrel_smart");

    public final GameObject<Item> WEAPON_PART_GRIP_GENERIC = createHolder("grip");

    public final GameObject<Item> WEAPON_PART_RECEIVER_GENERIC = createHolder("receiver");

    public final GameObject<Item> WEAPON_PART_RECEIVER_SMART = createHolder("receiver_smart");

    public final GameObject<Item> WEAPON_PART_STOCK_GENERIC = createHolder("stock");

    private GameObject<Item> createHolder(String registryName) {
        return createHolder(registryName, () -> new Item(new Item.Properties()));
    }

    @Override
    protected GameObject<Item> createHolder(String registryName, Supplier<Item> itemSupplier) {
        return super.createHolder("weapon_part_" + registryName, itemSupplier);
    }

    private AVPWeaponPartItems() {}
}
