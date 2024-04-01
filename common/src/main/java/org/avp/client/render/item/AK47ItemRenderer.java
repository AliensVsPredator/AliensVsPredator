package org.avp.client.render.item;

import mod.azure.azurelib.common.api.client.renderer.GeoItemRenderer;

import org.avp.client.model.item.AK47Model;
import org.avp.common.item.weapon.AK47Item;

public class AK47ItemRenderer extends GeoItemRenderer<AK47Item> {

    public AK47ItemRenderer() {
        super(new AK47Model());
    }
}
