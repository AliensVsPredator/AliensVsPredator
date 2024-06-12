package org.avp.client.render.item;

import mod.azure.azurelib.common.api.client.renderer.GeoItemRenderer;
import org.avp.api.common.weapon.data.WeaponData;
import org.avp.common.data.item.weapon.AK47Data;
import org.avp.common.data.item.weapon.F90RifleData;
import org.avp.common.data.item.weapon.FlamethrowerSevastopolData;
import org.avp.common.data.item.weapon.M3712ShotgunData;
import org.avp.common.data.item.weapon.M41APulseRifleData;
import org.avp.common.data.item.weapon.M4CarbineData;
import org.avp.common.data.item.weapon.M56SmartgunData;
import org.avp.common.data.item.weapon.M83A2SADARData;
import org.avp.common.data.item.weapon.M88Mod4CombatPistolData;
import org.avp.common.data.item.weapon.OldPainlessData;
import org.avp.common.data.item.weapon.SniperRifleData;

import java.util.Map;
import java.util.function.Supplier;

public class AVPWeaponItemRenderers {

    public static final Map<WeaponData, Supplier<GeoItemRenderer<?>>> WEAPON_ITEM_RENDERERS = Map.ofEntries(
        Map.entry(AK47Data.INSTANCE, AK47ItemRenderer::new),
        Map.entry(F90RifleData.INSTANCE, F90RifleItemRenderer::new),
        Map.entry(FlamethrowerSevastopolData.INSTANCE, FlamethrowerSevastopolItemRenderer::new),
        Map.entry(M3712ShotgunData.INSTANCE, M3712ShotgunItemRenderer::new),
        Map.entry(M4CarbineData.INSTANCE, M4CarbineItemRenderer::new),
        Map.entry(M41APulseRifleData.INSTANCE, M41APulseRifleItemRenderer::new),
        Map.entry(M56SmartgunData.INSTANCE, M56SmartgunItemRenderer::new),
        Map.entry(M83A2SADARData.INSTANCE, M83A2SADARItemRenderer::new),
        Map.entry(M88Mod4CombatPistolData.INSTANCE, M88Mod4CombatPistolItemRenderer::new),
        Map.entry(OldPainlessData.INSTANCE, OldPainlessItemRenderer::new),
        Map.entry(SniperRifleData.INSTANCE, SniperRifleItemRenderer::new)
    );

    private AVPWeaponItemRenderers() {
        throw new UnsupportedOperationException();
    }
}
