package org.avp.common.item;

import net.minecraft.world.item.Item;

import java.util.function.Supplier;

import org.avp.api.Holder;
import org.avp.common.registry.AVPDeferredItemRegistry;

public class AVPWeaponPartItems extends AVPDeferredItemRegistry {

    public static final AVPWeaponPartItems INSTANCE = new AVPWeaponPartItems();

    public final Holder<Item> weaponPartBarrelGeneric = createHolder("barrel");

    public final Holder<Item> weaponPartBarrelMinigun = createHolder("barrel_minigun");

    public final Holder<Item> weaponPartBarrelRocket = createHolder("barrel_rocket");

    public final Holder<Item> weaponPartBarrelSmart = createHolder("barrel_smart");

    public final Holder<Item> weaponPartGripGeneric = createHolder("grip");

    public final Holder<Item> weaponPartReceiverGeneric = createHolder("receiver");

    public final Holder<Item> weaponPartReceiverSmart = createHolder("receiver_smart");

    public final Holder<Item> weaponPartStockGeneric = createHolder("stock");

    @Override
    protected Holder<Item> createHolder(String registryName, Supplier<Item> itemSupplier) {
        return super.createHolder("weapon_part_" + registryName, itemSupplier);
    }

    private AVPWeaponPartItems() {}
}
