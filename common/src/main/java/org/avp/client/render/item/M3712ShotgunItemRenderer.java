package org.avp.client.render.item;

import mod.azure.azurelib.common.api.client.renderer.GeoItemRenderer;
import org.avp.client.model.AVPGeoModel;
import org.avp.client.model.GeoModelType;
import org.avp.common.game.item.AbstractAVPWeaponItem;

public class M3712ShotgunItemRenderer extends GeoItemRenderer<AbstractAVPWeaponItem> {

    public M3712ShotgunItemRenderer() {
        super(new AVPGeoModel<>("weapon_37_12_shotgun", GeoModelType.ITEM));
    }
}
