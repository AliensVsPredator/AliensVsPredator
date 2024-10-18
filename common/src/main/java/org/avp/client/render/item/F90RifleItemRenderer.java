package org.avp.client.render.item;

import mod.azure.azurelib.common.api.client.renderer.GeoItemRenderer;

import org.avp.client.model.AVPGeoModel;
import org.avp.client.model.GeoModelType;
import org.avp.common.game.item.AbstractAVPWeaponItem;

public class F90RifleItemRenderer extends GeoItemRenderer<AbstractAVPWeaponItem> {

    public F90RifleItemRenderer() {
        super(new AVPGeoModel<>("weapon_f90_rifle", GeoModelType.ITEM));
    }
}
