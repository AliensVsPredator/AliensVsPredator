package org.avp.client.model.item;

import mod.azure.azurelib.common.api.client.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

import org.avp.common.AVPResources;
import org.avp.common.item.weapon.M4CarbineItem;

public class M4CarbineModel extends GeoModel<M4CarbineItem> {

    private static final String ITEM_NAME = "weapon_m4_carbine";

    private static final ResourceLocation MODEL_LOCATION = AVPResources.itemGeoLocation(ITEM_NAME);

    private static final ResourceLocation TEXTURE_LOCATION = AVPResources.itemTextureLocation(ITEM_NAME);

    private static final ResourceLocation ANIMATION_LOCATION = AVPResources.itemAnimationLocation(ITEM_NAME);

    @Override
    public ResourceLocation getModelResource(M4CarbineItem item) {
        return MODEL_LOCATION;
    }

    @Override
    public ResourceLocation getTextureResource(M4CarbineItem item) {
        return TEXTURE_LOCATION;
    }

    @Override
    public ResourceLocation getAnimationResource(M4CarbineItem item) {
        return ANIMATION_LOCATION;
    }
}
