package org.avp.client.render.item;

import mod.azure.azurelib.common.api.client.renderer.GeoItemRenderer;

import org.avp.client.model.entity.living.AVPGeoModel;
import org.avp.client.model.entity.living.GeoModelType;
import org.avp.common.item.weapon.M88Mod4CombatPistolItem;

public class M88Mod4CombatPistolItemRenderer extends GeoItemRenderer<M88Mod4CombatPistolItem> {

    public M88Mod4CombatPistolItemRenderer() {
        super(new AVPGeoModel<>("weapon_m88mod4_combat_pistol", GeoModelType.ITEM));
    }
}
