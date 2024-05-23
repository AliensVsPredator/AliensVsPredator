package org.avp.client.render.item;

import mod.azure.azurelib.common.api.client.renderer.GeoItemRenderer;

import org.avp.client.model.entity.living.AVPGeoModel;
import org.avp.client.model.entity.living.GeoModelType;
import org.avp.common.item.weapon.M56SmartgunItem;

public class M56SmartgunItemRenderer extends GeoItemRenderer<M56SmartgunItem> {

    public M56SmartgunItemRenderer() {
        super(new AVPGeoModel<>("weapon_m56_smartgun", GeoModelType.ITEM));
    }
}
