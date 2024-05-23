package org.avp.client.render.item;

import mod.azure.azurelib.common.api.client.renderer.GeoItemRenderer;

import org.avp.client.model.entity.living.AVPGeoModel;
import org.avp.client.model.entity.living.GeoModelType;
import org.avp.common.item.weapon.M3712ShotgunItem;

public class M3712ShotgunItemRenderer extends GeoItemRenderer<M3712ShotgunItem> {

    public M3712ShotgunItemRenderer() {
        super(new AVPGeoModel<>("weapon_37_12_shotgun", GeoModelType.ITEM));
    }
}
