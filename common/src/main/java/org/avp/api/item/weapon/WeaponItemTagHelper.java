package org.avp.api.item.weapon;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import org.avp.common.item.AbstractAVPWeaponItem;

public class WeaponItemTagHelper {

    private static final String AMMUNITION_KEY = "Ammunition";

    private static final String FIRE_MODE_KEY = "FireMode";

    private static final String IS_WOUND_UP_KEY = "IsWoundUp";

    private static CompoundTag getTagSafely(ItemStack itemStack) {
        var maybeTag = itemStack.getTag();
        return maybeTag != null ? maybeTag : itemStack.getOrCreateTag();
    }

    public static void consumeAmmunition(ItemStack itemStack, WeaponItemData weaponItemData) {
        var currentAmmunition = getAmmunition(itemStack);
        var fireMode = getFireMode(itemStack, weaponItemData);
        getTagSafely(itemStack).putInt(AMMUNITION_KEY, currentAmmunition - fireMode.consumedAmmunition());
    }

    public static int getAmmunition(ItemStack itemStack) {
        return getTagSafely(itemStack).getInt(AMMUNITION_KEY);
    }

    public static FireMode getFireMode(ItemStack itemStack, WeaponItemData weaponItemData) {
        var tag = getTagSafely(itemStack);
        var fireModeIdentifier = tag.getString(FIRE_MODE_KEY);
        var fireMode = weaponItemData.getFireMode(fireModeIdentifier);

        if (fireModeIdentifier.isEmpty()) {
            tag.putString(FIRE_MODE_KEY, fireMode.identifier());
        }

        return fireMode;
    }

    public static boolean hasMaxAmmunition(ItemStack itemStack, WeaponItemData weaponItemData) {
        return getAmmunition(itemStack) == weaponItemData.getAmmunitionStrategy().getMaxAmmunition();
    }

    public static boolean isWeapon(ItemStack itemStack) {
        return itemStack.getItem() instanceof AbstractAVPWeaponItem;
    }

    public static boolean isWoundUp(ItemStack itemStack) {
        if (!isWeapon(itemStack))
            return false;

        if (!getTagSafely(itemStack).contains(IS_WOUND_UP_KEY)) {
            return false;
        }

        return getTagSafely(itemStack).getBoolean(IS_WOUND_UP_KEY);
    }

    public static void restoreAmmunition(ItemStack itemStack, WeaponItemData weaponItemData) {
        getTagSafely(itemStack).putInt(AMMUNITION_KEY, weaponItemData.getAmmunitionStrategy().getMaxAmmunition());
    }

    public static void setWoundUp(ItemStack itemStack, boolean isWoundUp) {
        if (!isWeapon(itemStack))
            return;

        getTagSafely(itemStack).putBoolean(IS_WOUND_UP_KEY, isWoundUp);
    }

    private WeaponItemTagHelper() {
        throw new UnsupportedOperationException();
    }
}
