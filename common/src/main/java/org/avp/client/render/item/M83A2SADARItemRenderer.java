package org.avp.client.render.item;

import mod.azure.azurelib.common.api.client.renderer.GeoItemRenderer;

import org.avp.client.model.entity.living.AVPGeoModel;
import org.avp.client.model.entity.living.GeoModelType;
import org.avp.common.item.weapon.M83A2SADARItem;

public class M83A2SADARItemRenderer extends GeoItemRenderer<M83A2SADARItem> {

    public M83A2SADARItemRenderer() {
        super(new AVPGeoModel<>("weapon_m83a2_sadar", GeoModelType.ITEM));
    }
}
