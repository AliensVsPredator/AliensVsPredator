package org.avp.common.registry.item;

import net.minecraft.world.item.Item;

import org.avp.api.common.data.item.ItemData;
import org.avp.api.common.registry.AVPDeferredItemRegistry;
import org.avp.api.common.registry.holder.BLHolder;

public class AVPWeaponPartItemRegistry extends AVPDeferredItemRegistry {

    public static final AVPWeaponPartItemRegistry INSTANCE = new AVPWeaponPartItemRegistry();

    public final BLHolder<Item> weaponPartBarrelGeneric;

    public final BLHolder<Item> weaponPartBarrelMinigun;

    public final BLHolder<Item> weaponPartBarrelRocket;

    public final BLHolder<Item> weaponPartBarrelSmart;

    public final BLHolder<Item> weaponPartGripGeneric;

    public final BLHolder<Item> weaponPartReceiverGeneric;

    public final BLHolder<Item> weaponPartReceiverSmart;

    public final BLHolder<Item> weaponPartStockGeneric;

    @Override
    protected BLHolder<Item> createHolder(ItemData itemData) {
        return super.createHolder(itemData.withPrefixRegistryName("weapon_part_"));
    }

    private AVPWeaponPartItemRegistry() {
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
