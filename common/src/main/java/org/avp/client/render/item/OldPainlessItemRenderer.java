package org.avp.client.render.item;

import mod.azure.azurelib.common.api.client.renderer.GeoItemRenderer;

import org.avp.client.model.item.OldPainlessModel;
import org.avp.common.item.weapon.OldPainlessItem;

public class OldPainlessItemRenderer extends GeoItemRenderer<OldPainlessItem> {

    public OldPainlessItemRenderer() {
        super(new OldPainlessModel());
    }
}
