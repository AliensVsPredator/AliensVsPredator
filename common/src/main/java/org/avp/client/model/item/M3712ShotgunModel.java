package org.avp.client.model.item;

import mod.azure.azurelib.common.api.client.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

import org.avp.common.AVPResources;
import org.avp.common.item.weapon.M3712ShotgunItem;

public class M3712ShotgunModel extends GeoModel<M3712ShotgunItem> {

    private static final String ITEM_NAME = "weapon_37_12_shotgun";

    private static final ResourceLocation MODEL_LOCATION = AVPResources.itemGeoLocation(ITEM_NAME);

    private static final ResourceLocation TEXTURE_LOCATION = AVPResources.itemTextureLocation(ITEM_NAME);

    private static final ResourceLocation ANIMATION_LOCATION = AVPResources.itemAnimationLocation(ITEM_NAME);

    @Override
    public ResourceLocation getModelResource(M3712ShotgunItem item) {
        return MODEL_LOCATION;
    }

    @Override
    public ResourceLocation getTextureResource(M3712ShotgunItem item) {
        return TEXTURE_LOCATION;
    }

    @Override
    public ResourceLocation getAnimationResource(M3712ShotgunItem item) {
        return ANIMATION_LOCATION;
    }
}
