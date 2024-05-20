package org.avp.client.render.item;

import mod.azure.azurelib.common.api.client.renderer.GeoItemRenderer;

import org.avp.client.model.entity.living.AVPGeoModel;
import org.avp.client.model.entity.living.GeoModelType;
import org.avp.common.item.weapon.OldPainlessItem;

public class OldPainlessItemRenderer extends GeoItemRenderer<OldPainlessItem> {

    public OldPainlessItemRenderer() {
        super(new AVPGeoModel<>("weapon_old_painless", GeoModelType.ITEM));
    }
}
