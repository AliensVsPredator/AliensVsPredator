package org.avp.client.render.item;

import mod.azure.azurelib.common.api.client.renderer.GeoItemRenderer;

import org.avp.client.model.item.M41APulseRifleModel;
import org.avp.common.item.weapon.M41APulseRifleItem;

public class M41APulseRifleItemRenderer extends GeoItemRenderer<M41APulseRifleItem> {

    public M41APulseRifleItemRenderer() {
        super(new M41APulseRifleModel());
    }
}
