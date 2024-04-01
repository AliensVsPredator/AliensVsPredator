package org.avp.client.model.item;

import mod.azure.azurelib.common.api.client.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

import org.avp.common.AVPResources;
import org.avp.common.item.weapon.M41APulseRifleItem;

public class M41APulseRifleModel extends GeoModel<M41APulseRifleItem> {

    private static final String ITEM_NAME = "weapon_m41a_pulse_rifle";

    private static final ResourceLocation MODEL_LOCATION = AVPResources.itemGeoLocation(ITEM_NAME);

    private static final ResourceLocation TEXTURE_LOCATION = AVPResources.itemTextureLocation(ITEM_NAME);

    private static final ResourceLocation ANIMATION_LOCATION = AVPResources.itemAnimationLocation(ITEM_NAME);

    @Override
    public ResourceLocation getModelResource(M41APulseRifleItem item) {
        return MODEL_LOCATION;
    }

    @Override
    public ResourceLocation getTextureResource(M41APulseRifleItem item) {
        return TEXTURE_LOCATION;
    }

    @Override
    public ResourceLocation getAnimationResource(M41APulseRifleItem item) {
        return ANIMATION_LOCATION;
    }
}
