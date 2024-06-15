package org.avp.client.render.item;

import mod.azure.azurelib.common.api.client.renderer.GeoItemRenderer;

import org.avp.client.model.AVPGeoModel;
import org.avp.client.model.GeoModelType;
import org.avp.common.game.item.AbstractAVPWeaponItem;

public class M4CarbineItemRenderer extends GeoItemRenderer<AbstractAVPWeaponItem> {

    public M4CarbineItemRenderer() {
        super(new AVPGeoModel<>("weapon_m4_carbine", GeoModelType.ITEM));
    }
}
