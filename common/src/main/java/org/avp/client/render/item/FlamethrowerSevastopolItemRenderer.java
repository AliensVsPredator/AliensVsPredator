package org.avp.client.render.item;

import mod.azure.azurelib.common.api.client.renderer.GeoItemRenderer;

import org.avp.client.model.item.FlamethrowerSevastopolModel;
import org.avp.common.item.weapon.FlamethrowerSevastopolItem;

public class FlamethrowerSevastopolItemRenderer extends GeoItemRenderer<FlamethrowerSevastopolItem> {

    public FlamethrowerSevastopolItemRenderer() {
        super(new FlamethrowerSevastopolModel());
    }
}
