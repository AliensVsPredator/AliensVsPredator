package org.avp.client.render.item;

import mod.azure.azurelib.common.api.client.renderer.GeoItemRenderer;

import org.avp.client.model.entity.living.AVPGeoModel;
import org.avp.client.model.entity.living.GeoModelType;
import org.avp.common.item.weapon.FlamethrowerSevastopolItem;

public class FlamethrowerSevastopolItemRenderer extends GeoItemRenderer<FlamethrowerSevastopolItem> {

    public FlamethrowerSevastopolItemRenderer() {
        super(new AVPGeoModel<>("weapon_flamethrower_sevastopol", GeoModelType.ITEM));
    }
}
