package org.avp.api.item.weapon;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class WeaponItemTagHelper {

    private static final String AMMUNITION_KEY = "Ammunition";

    private static final String FIRE_MODE_KEY = "FireMode";

    private static CompoundTag getTagSafely(ItemStack itemStack) {
        var maybeTag = itemStack.getTag();
        return maybeTag != null ? maybeTag : itemStack.getOrCreateTag();
    }

    public static void consumeAmmunition(ItemStack itemStack, WeaponItemData weaponItemData) {
        var currentAmmunition = getAmmunition(itemStack);
        var fireMode = getOrSetFireMode(itemStack, weaponItemData);
        getTagSafely(itemStack).putInt(AMMUNITION_KEY, currentAmmunition - fireMode.consumedAmmunition());
    }

    public static int getAmmunition(ItemStack itemStack) {
        return getTagSafely(itemStack).getInt(AMMUNITION_KEY);
    }

    public static FireMode getOrSetFireMode(ItemStack itemStack, WeaponItemData weaponItemData) {
        var tag = getTagSafely(itemStack);
        var fireModeIdentifier = tag.getString(FIRE_MODE_KEY);
        var fireMode = weaponItemData.getFireModeByIdOrFirst(fireModeIdentifier);

        if (fireModeIdentifier.isEmpty()) {
            WeaponItemTagHelper.setFireMode(itemStack, fireMode);
        }

        return fireMode;
    }

    public static boolean hasMaxAmmunition(ItemStack itemStack, WeaponItemData weaponItemData) {
        return getAmmunition(itemStack) == weaponItemData.getAmmunitionStrategy().getMaxAmmunition();
    }

    public static void restoreAmmunition(ItemStack itemStack, WeaponItemData weaponItemData) {
        getTagSafely(itemStack).putInt(AMMUNITION_KEY, weaponItemData.getAmmunitionStrategy().getMaxAmmunition());
    }

    public static void setFireMode(ItemStack itemStack, FireMode fireMode) {
        var tag = getTagSafely(itemStack);
        tag.putString(FIRE_MODE_KEY, fireMode.identifier());
    }

    private WeaponItemTagHelper() {
        throw new UnsupportedOperationException();
    }
}
