package org.avp.client.render.item;

import mod.azure.azurelib.common.api.client.renderer.GeoItemRenderer;

import org.avp.client.model.AVPGeoModel;
import org.avp.client.model.GeoModelType;
import org.avp.common.game.item.AbstractAVPWeaponItem;

public class AK47ItemRenderer extends GeoItemRenderer<AbstractAVPWeaponItem> {

    public AK47ItemRenderer() {
        super(new AVPGeoModel<>("weapon_ak_47", GeoModelType.ITEM));
    }
}
