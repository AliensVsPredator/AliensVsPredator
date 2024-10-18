package org.avp.client.render.item;

import mod.azure.azurelib.common.api.client.renderer.GeoItemRenderer;

import org.avp.client.model.AVPGeoModel;
import org.avp.client.model.GeoModelType;
import org.avp.common.game.item.AbstractAVPWeaponItem;

public class OldPainlessItemRenderer extends GeoItemRenderer<AbstractAVPWeaponItem> {

    public OldPainlessItemRenderer() {
        super(new AVPGeoModel<>("weapon_old_painless", GeoModelType.ITEM));
    }
}
