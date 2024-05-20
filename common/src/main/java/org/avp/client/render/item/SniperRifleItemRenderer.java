package org.avp.client.render.item;

import mod.azure.azurelib.common.api.client.renderer.GeoItemRenderer;

import org.avp.client.model.entity.living.AVPGeoModel;
import org.avp.client.model.entity.living.GeoModelType;
import org.avp.common.item.weapon.SniperRifleItem;

public class SniperRifleItemRenderer extends GeoItemRenderer<SniperRifleItem> {

    public SniperRifleItemRenderer() {
        super(new AVPGeoModel<>("weapon_sniper_rifle", GeoModelType.ITEM));
    }
}
