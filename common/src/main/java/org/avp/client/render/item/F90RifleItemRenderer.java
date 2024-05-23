package org.avp.client.render.item;

import mod.azure.azurelib.common.api.client.renderer.GeoItemRenderer;

import org.avp.client.model.entity.living.AVPGeoModel;
import org.avp.client.model.entity.living.GeoModelType;
import org.avp.common.item.weapon.F90RifleItem;

public class F90RifleItemRenderer extends GeoItemRenderer<F90RifleItem> {

    public F90RifleItemRenderer() {
        super(new AVPGeoModel<>("weapon_f90_rifle", GeoModelType.ITEM));
    }
}
