package org.avp.client.model.item;

import mod.azure.azurelib.common.api.client.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

import org.avp.common.AVPResources;
import org.avp.common.item.weapon.M83A2SADARItem;

public class M83A2SADARModel extends GeoModel<M83A2SADARItem> {

    private static final String ITEM_NAME = "weapon_m83a2_sadar";

    private static final ResourceLocation MODEL_LOCATION = AVPResources.itemGeoLocation(ITEM_NAME);

    private static final ResourceLocation TEXTURE_LOCATION = AVPResources.itemTextureLocation(ITEM_NAME);

    private static final ResourceLocation ANIMATION_LOCATION = AVPResources.itemAnimationLocation(ITEM_NAME);

    @Override
    public ResourceLocation getModelResource(M83A2SADARItem item) {
        return MODEL_LOCATION;
    }

    @Override
    public ResourceLocation getTextureResource(M83A2SADARItem item) {
        return TEXTURE_LOCATION;
    }

    @Override
    public ResourceLocation getAnimationResource(M83A2SADARItem item) {
        return ANIMATION_LOCATION;
    }
}
