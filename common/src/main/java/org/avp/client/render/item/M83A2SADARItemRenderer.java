package org.avp.client.render.item;

import mod.azure.azurelib.common.api.client.renderer.GeoItemRenderer;

import org.avp.client.model.AVPGeoModel;
import org.avp.client.model.GeoModelType;
import org.avp.common.game.item.AbstractAVPWeaponItem;

public class M83A2SADARItemRenderer extends GeoItemRenderer<AbstractAVPWeaponItem> {

    public M83A2SADARItemRenderer() {
        super(new AVPGeoModel<>("weapon_m83a2_sadar", GeoModelType.ITEM));
    }
}
