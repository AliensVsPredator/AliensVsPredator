package org.avp.client.model.item;

import mod.azure.azurelib.common.api.client.model.GeoModel;
import net.minecraft.resources.ResourceLocation;
import org.avp.common.AVPResources;
import org.avp.common.item.weapon.OldPainlessItem;

public class OldPainlessModel extends GeoModel<OldPainlessItem> {

    private static final String ITEM_NAME = "weapon_old_painless";

    private static final ResourceLocation MODEL_LOCATION = AVPResources.itemGeoLocation(ITEM_NAME);

    private static final ResourceLocation TEXTURE_LOCATION = AVPResources.itemTextureLocation(ITEM_NAME);

    private static final ResourceLocation ANIMATION_LOCATION = AVPResources.itemAnimationLocation(ITEM_NAME);

    @Override
    public ResourceLocation getModelResource(OldPainlessItem item) {
        return MODEL_LOCATION;
    }

    @Override
    public ResourceLocation getTextureResource(OldPainlessItem item) {
        return TEXTURE_LOCATION;
    }

    @Override
    public ResourceLocation getAnimationResource(OldPainlessItem item) {
        return ANIMATION_LOCATION;
    }
}
