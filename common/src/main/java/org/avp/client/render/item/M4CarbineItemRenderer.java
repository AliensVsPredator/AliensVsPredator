package org.avp.client.render.item;

import mod.azure.azurelib.common.api.client.renderer.GeoItemRenderer;

import org.avp.client.model.entity.living.AVPGeoModel;
import org.avp.client.model.entity.living.GeoModelType;
import org.avp.common.item.weapon.M4CarbineItem;

public class M4CarbineItemRenderer extends GeoItemRenderer<M4CarbineItem> {

    public M4CarbineItemRenderer() {
        super(new AVPGeoModel<>("weapon_m4_carbine", GeoModelType.ITEM));
    }
}
