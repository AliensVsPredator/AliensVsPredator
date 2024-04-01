package org.avp.client.render.item;

import mod.azure.azurelib.common.api.client.renderer.GeoItemRenderer;

import org.avp.client.model.item.SniperRifleModel;
import org.avp.common.item.weapon.SniperRifleItem;

public class SniperRifleItemRenderer extends GeoItemRenderer<SniperRifleItem> {

    public SniperRifleItemRenderer() {
        super(new SniperRifleModel());
    }
}
