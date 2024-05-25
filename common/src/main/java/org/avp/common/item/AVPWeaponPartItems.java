package org.avp.common.item;

import net.minecraft.world.item.Item;

import org.avp.api.Holder;
import org.avp.api.item.ItemData;
import org.avp.common.registry.AVPDeferredItemRegistry;

public class AVPWeaponPartItems extends AVPDeferredItemRegistry {

    public static final AVPWeaponPartItems INSTANCE = new AVPWeaponPartItems();

    public final Holder<Item> weaponPartBarrelGeneric;

    public final Holder<Item> weaponPartBarrelMinigun;

    public final Holder<Item> weaponPartBarrelRocket;

    public final Holder<Item> weaponPartBarrelSmart;

    public final Holder<Item> weaponPartGripGeneric;

    public final Holder<Item> weaponPartReceiverGeneric;

    public final Holder<Item> weaponPartReceiverSmart;

    public final Holder<Item> weaponPartStockGeneric;

    @Override
    protected Holder<Item> createHolder(ItemData itemData) {
        return super.createHolder(itemData.withPrefixRegistryName("weapon_part_"));
    }

    private AVPWeaponPartItems() {
        weaponPartBarrelGeneric = createHolder("barrel");
        weaponPartBarrelMinigun = createHolder("barrel_minigun");
        weaponPartBarrelRocket = createHolder("barrel_rocket");
        weaponPartBarrelSmart = createHolder("barrel_smart");
        weaponPartGripGeneric = createHolder("grip");
        weaponPartReceiverGeneric = createHolder("receiver");
        weaponPartReceiverSmart = createHolder("receiver_smart");
        weaponPartStockGeneric = createHolder("stock");
    }
}
