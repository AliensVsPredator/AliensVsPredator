package org.avp.client.render.item;

import mod.azure.azurelib.common.api.client.renderer.GeoItemRenderer;

import org.avp.client.model.item.M83A2SADARModel;
import org.avp.common.item.weapon.M83A2SADARItem;

public class M83A2SADARItemRenderer extends GeoItemRenderer<M83A2SADARItem> {

    public M83A2SADARItemRenderer() {
        super(new M83A2SADARModel());
    }
}
