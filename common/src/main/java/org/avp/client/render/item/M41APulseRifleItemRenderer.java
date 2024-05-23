package org.avp.client.render.item;

import mod.azure.azurelib.common.api.client.renderer.GeoItemRenderer;

import org.avp.client.model.entity.living.AVPGeoModel;
import org.avp.client.model.entity.living.GeoModelType;
import org.avp.common.item.weapon.M41APulseRifleItem;

public class M41APulseRifleItemRenderer extends GeoItemRenderer<M41APulseRifleItem> {

    public M41APulseRifleItemRenderer() {
        super(new AVPGeoModel<>("weapon_m41a_pulse_rifle", GeoModelType.ITEM));
    }
}
