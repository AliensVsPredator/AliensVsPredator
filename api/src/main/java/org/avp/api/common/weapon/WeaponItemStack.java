package org.avp.api.common.weapon;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Set;

import org.avp.api.common.weapon.bullet_effect.BulletEffect;
import org.avp.api.common.weapon.bullet_effect.BulletEffectRegistry;
import org.avp.api.common.weapon.data.FireModeData;
import org.avp.api.common.weapon.data.WeaponData;

public class WeaponItemStack {

    private static final String ACTIVE_AMMUNITION_TYPE_KEY = "ActiveAmmunitionType";

    private static final String AMMUNITIONS_KEY = "Ammunitions";

    private static final String FIRE_MODE_KEY = "FireMode";

    private final ItemStack itemStack;

    private final WeaponData weaponData;

    public WeaponItemStack(ItemStack itemStack, WeaponData weaponData) {
        this.itemStack = itemStack;
        this.weaponData = weaponData;
    }

    private void setAmmunitionForAmmunitionType(String ammunitionType, int count) {
        var tag = getTagSafely(itemStack);
        var ammunitionsTag = tag.getCompound(AMMUNITIONS_KEY);
        ammunitionsTag.putInt(ammunitionType, count);
        tag.put(AMMUNITIONS_KEY, ammunitionsTag);
    }

    public int getAmmunition() {
        var tag = getTagSafely(itemStack);
        var activeAmmunitionType = getOrSetActiveAmmunitionType();
        var ammunitionsTag = tag.getCompound(AMMUNITIONS_KEY);
        return ammunitionsTag.getInt(activeAmmunitionType);
    }

    public void consumeAmmunition() {
        var fireModeData = getOrSetFireMode();
        var activeAmmunitionType = getOrSetActiveAmmunitionType();
        var currentAmmunition = getAmmunition();
        var newAmmunitionCount = Math.max(currentAmmunition - fireModeData.ammunitionData().consumedAmmunition(), 0);
        setAmmunitionForAmmunitionType(activeAmmunitionType, newAmmunitionCount);
    }

    public void setActiveAmmunitionType(Item ammunitionType) {
        var tag = getTagSafely(itemStack);
        var resourceLocation = BuiltInRegistries.ITEM.getKey(ammunitionType);
        var resourceLocationString = resourceLocation.toString();
        tag.putString(ACTIVE_AMMUNITION_TYPE_KEY, resourceLocationString);
    }

    public Set<BulletEffect> getBulletEffects() {
        var activeAmmunitionType = getOrSetActiveAmmunitionType();
        var resourceLocation = new ResourceLocation(activeAmmunitionType);
        return BulletEffectRegistry.getBulletEffects(resourceLocation);
    }

    public String getOrSetActiveAmmunitionType() {
        var tag = getTagSafely(itemStack);
        var activeAmmunitionType = tag.getString(ACTIVE_AMMUNITION_TYPE_KEY);
        var fireMode = getOrSetFireMode();

        if (activeAmmunitionType.isEmpty()) {
            var standardAmmunition = fireMode.ammunitionData().getAmmunitionSupplierByKeyOrFirst(activeAmmunitionType).get();
            setActiveAmmunitionType(standardAmmunition);
            return tag.getString(ACTIVE_AMMUNITION_TYPE_KEY);
        }

        return activeAmmunitionType;
    }

    public FireModeData getOrSetFireMode() {
        var tag = getTagSafely(itemStack);
        var fireModeIdentifier = tag.getString(FIRE_MODE_KEY);
        var fireMode = weaponData.getFireModeByIdOrFirst(fireModeIdentifier);

        if (fireModeIdentifier.isEmpty()) {
            setFireMode(fireMode);
        }

        return fireMode;
    }

    public boolean hasMaxAmmunition() {
        var fireModeData = getOrSetFireMode();
        return getAmmunition() == fireModeData.ammunitionData().maxAmmunition();
    }

    public void setActiveAmmunition(int count) {
        var activeAmmunitionType = getOrSetActiveAmmunitionType();
        setAmmunitionForAmmunitionType(activeAmmunitionType, count);
    }

    public void setFireMode(FireModeData fireModeData) {
        var tag = getTagSafely(itemStack);
        tag.putString(FIRE_MODE_KEY, fireModeData.identifier());
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    private CompoundTag getTagSafely(ItemStack itemStack) {
        return itemStack.getOrCreateTag();
    }
}
