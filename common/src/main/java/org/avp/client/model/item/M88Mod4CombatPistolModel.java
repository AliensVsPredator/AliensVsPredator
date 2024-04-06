package org.avp.client.model.item;

import mod.azure.azurelib.common.api.client.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

import org.avp.common.AVPResources;
import org.avp.common.item.weapon.M88Mod4CombatPistolItem;

public class M88Mod4CombatPistolModel extends GeoModel<M88Mod4CombatPistolItem> {

    private static final String ITEM_NAME = "weapon_m88mod4_combat_pistol";

    private static final ResourceLocation MODEL_LOCATION = AVPResources.itemGeoLocation(ITEM_NAME);

    private static final ResourceLocation TEXTURE_LOCATION = AVPResources.itemTextureLocation(ITEM_NAME);

    private static final ResourceLocation ANIMATION_LOCATION = AVPResources.itemAnimationLocation(ITEM_NAME);

    @Override
    public ResourceLocation getModelResource(M88Mod4CombatPistolItem item) {
        return MODEL_LOCATION;
    }

    @Override
    public ResourceLocation getTextureResource(M88Mod4CombatPistolItem item) {
        return TEXTURE_LOCATION;
    }

    @Override
    public ResourceLocation getAnimationResource(M88Mod4CombatPistolItem item) {
        return ANIMATION_LOCATION;
    }
}
