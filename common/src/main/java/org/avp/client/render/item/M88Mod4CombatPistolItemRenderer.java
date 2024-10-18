package org.avp.client.render.item;

import mod.azure.azurelib.common.api.client.renderer.GeoItemRenderer;

import org.avp.client.model.AVPGeoModel;
import org.avp.client.model.GeoModelType;
import org.avp.common.game.item.AbstractAVPWeaponItem;

public class M88Mod4CombatPistolItemRenderer extends GeoItemRenderer<AbstractAVPWeaponItem> {

    public M88Mod4CombatPistolItemRenderer() {
        super(new AVPGeoModel<>("weapon_m88mod4_combat_pistol", GeoModelType.ITEM));
    }
}
