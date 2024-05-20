package org.avp.client.render.item;

import mod.azure.azurelib.common.api.client.renderer.GeoItemRenderer;

import org.avp.client.model.entity.living.AVPGeoModel;
import org.avp.client.model.entity.living.GeoModelType;
import org.avp.common.item.weapon.AK47Item;

public class AK47ItemRenderer extends GeoItemRenderer<AK47Item> {

    public AK47ItemRenderer() {
        super(new AVPGeoModel<>("weapon_ak_47", GeoModelType.ITEM));
    }
}
