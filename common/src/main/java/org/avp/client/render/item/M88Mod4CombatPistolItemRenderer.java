package org.avp.client.render.item;

import mod.azure.azurelib.common.api.client.renderer.GeoItemRenderer;
import org.avp.client.model.item.M88Mod4CombatPistolModel;
import org.avp.common.item.weapon.M88Mod4CombatPistolItem;

public class M88Mod4CombatPistolItemRenderer extends GeoItemRenderer<M88Mod4CombatPistolItem> {

    public M88Mod4CombatPistolItemRenderer() {
        super(new M88Mod4CombatPistolModel());
    }
}
