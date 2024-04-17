package org.avp.common.item;

import net.minecraft.world.item.Item;

import java.util.function.Supplier;

import org.avp.api.Holder;
import org.avp.common.registry.AVPDeferredItemRegistry;

public class AVPWeaponPartItems extends AVPDeferredItemRegistry {

    public static final AVPWeaponPartItems INSTANCE = new AVPWeaponPartItems();

    public final Holder<Item> WEAPON_PART_BARREL_GENERIC = createHolder("barrel");

    public final Holder<Item> WEAPON_PART_BARREL_MINIGUN = createHolder("barrel_minigun");

    public final Holder<Item> WEAPON_PART_BARREL_ROCKET = createHolder("barrel_rocket");

    public final Holder<Item> WEAPON_PART_BARREL_SMART = createHolder("barrel_smart");

    public final Holder<Item> WEAPON_PART_GRIP_GENERIC = createHolder("grip");

    public final Holder<Item> WEAPON_PART_RECEIVER_GENERIC = createHolder("receiver");

    public final Holder<Item> WEAPON_PART_RECEIVER_SMART = createHolder("receiver_smart");

    public final Holder<Item> WEAPON_PART_STOCK_GENERIC = createHolder("stock");

    private Holder<Item> createHolder(String registryName) {
        return createHolder(registryName, () -> new Item(new Item.Properties()));
    }

    @Override
    protected Holder<Item> createHolder(String registryName, Supplier<Item> itemSupplier) {
        return super.createHolder("weapon_part_" + registryName, itemSupplier);
    }

    private AVPWeaponPartItems() {}
}
