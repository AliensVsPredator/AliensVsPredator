package org.avp.client.render.item;

import mod.azure.azurelib.common.api.client.renderer.GeoItemRenderer;

import org.avp.client.model.item.F90RifleModel;
import org.avp.common.item.weapon.F90RifleItem;

public class F90RifleItemRenderer extends GeoItemRenderer<F90RifleItem> {

    public F90RifleItemRenderer() {
        super(new F90RifleModel());
    }
}
