package org.avp.api.item.weapon;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class WeaponItemTagHelper {

    private static final String ACTIVE_AMMUNITION_TYPE_KEY = "ActiveAmmunitionType";

    private static final String AMMUNITIONS_KEY = "Ammunitions";

    private static final String FIRE_MODE_KEY = "FireMode";

    private static CompoundTag getTagSafely(ItemStack itemStack) {
        return itemStack.getOrCreateTag();
    }

    private static void setAmmunitionForAmmunitionType(ItemStack itemStack, String ammunitionType, int count) {
        var tag = getTagSafely(itemStack);
        var ammunitionsTag = tag.getCompound(AMMUNITIONS_KEY);
        ammunitionsTag.putInt(ammunitionType, count);
        tag.put(AMMUNITIONS_KEY, ammunitionsTag);
    }

    public static void consumeAmmunition(ItemStack itemStack, WeaponItemData weaponItemData) {
        var tag = getTagSafely(itemStack);
        var fireMode = getOrSetFireMode(itemStack, weaponItemData);
        var activeAmmunitionType = getOrSetActiveAmmunitionType(itemStack, weaponItemData);
        var currentAmmunition = getAmmunition(itemStack, weaponItemData);
        var newAmmunitionCount = Math.max(currentAmmunition - fireMode.consumedAmmunition(), 0);
        setAmmunitionForAmmunitionType(itemStack, activeAmmunitionType, newAmmunitionCount);
    }

    public static int getAmmunition(ItemStack itemStack, WeaponItemData weaponItemData) {
        var tag = getTagSafely(itemStack);
        var activeAmmunitionType = getOrSetActiveAmmunitionType(itemStack, weaponItemData);
        var ammunitionsTag = tag.getCompound(AMMUNITIONS_KEY);
        return ammunitionsTag.getInt(activeAmmunitionType);
    }

    private static String getOrSetActiveAmmunitionType(ItemStack itemStack, WeaponItemData weaponItemData) {
        var tag = getTagSafely(itemStack);
        var activeAmmunitionType = tag.getString(ACTIVE_AMMUNITION_TYPE_KEY);

        if (activeAmmunitionType.isEmpty()) {
            var standardAmmunition = weaponItemData.getAmmunitionStrategy().getStandardAmmunitionSupplier().get();
            var resourceLocation = BuiltInRegistries.ITEM.getKey(standardAmmunition);
            var resourceLocationString = resourceLocation.toString();
            tag.putString(ACTIVE_AMMUNITION_TYPE_KEY, resourceLocationString);
            activeAmmunitionType = resourceLocationString;
        }

        return activeAmmunitionType;
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
        return getAmmunition(itemStack, weaponItemData) == weaponItemData.getAmmunitionStrategy().getMaxAmmunition();
    }

    public static void setActiveAmmunition(ItemStack itemStack, WeaponItemData weaponItemData, int count) {
        var activeAmmunitionType = getOrSetActiveAmmunitionType(itemStack, weaponItemData);
        setAmmunitionForAmmunitionType(itemStack, activeAmmunitionType, count);
    }

    public static void setFireMode(ItemStack itemStack, FireMode fireMode) {
        var tag = getTagSafely(itemStack);
        tag.putString(FIRE_MODE_KEY, fireMode.identifier());
    }

    private WeaponItemTagHelper() {
        throw new UnsupportedOperationException();
    }
}
