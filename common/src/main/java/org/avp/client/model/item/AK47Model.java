package org.avp.client.model.item;

import mod.azure.azurelib.common.api.client.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

import org.avp.common.AVPResources;
import org.avp.common.item.weapon.AK47Item;

public class AK47Model extends GeoModel<AK47Item> {

    private static final String ITEM_NAME = "weapon_ak_47";

    private static final ResourceLocation MODEL_LOCATION = AVPResources.itemGeoLocation(ITEM_NAME);

    private static final ResourceLocation TEXTURE_LOCATION = AVPResources.itemTextureLocation(ITEM_NAME);

    private static final ResourceLocation ANIMATION_LOCATION = AVPResources.itemAnimationLocation(ITEM_NAME);

    @Override
    public ResourceLocation getModelResource(AK47Item item) {
        return MODEL_LOCATION;
    }

    @Override
    public ResourceLocation getTextureResource(AK47Item item) {
        return TEXTURE_LOCATION;
    }

    @Override
    public ResourceLocation getAnimationResource(AK47Item item) {
        return ANIMATION_LOCATION;
    }
}
