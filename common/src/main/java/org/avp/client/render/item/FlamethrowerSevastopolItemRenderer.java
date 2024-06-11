package org.avp.client.render.item;

import mod.azure.azurelib.common.api.client.renderer.GeoItemRenderer;

import org.avp.client.model.AVPGeoModel;
import org.avp.client.model.GeoModelType;
import org.avp.common.game.item.AbstractAVPWeaponItem;

public class FlamethrowerSevastopolItemRenderer extends GeoItemRenderer<AbstractAVPWeaponItem> {

    public FlamethrowerSevastopolItemRenderer() {
        super(new AVPGeoModel<>("weapon_flamethrower_sevastopol", GeoModelType.ITEM));
    }
}
