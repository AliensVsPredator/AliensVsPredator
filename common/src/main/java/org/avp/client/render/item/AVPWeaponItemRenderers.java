package org.avp.client.render.item;

import mod.azure.azurelib.common.api.client.renderer.GeoItemRenderer;
import net.minecraft.world.item.Item;

import java.util.Map;
import java.util.function.Supplier;

import org.avp.common.item.weapon.AK47Item;
import org.avp.common.item.weapon.F90RifleItem;
import org.avp.common.item.weapon.FlamethrowerSevastopolItem;
import org.avp.common.item.weapon.M3712ShotgunItem;
import org.avp.common.item.weapon.M41APulseRifleItem;
import org.avp.common.item.weapon.M4CarbineItem;
import org.avp.common.item.weapon.M56SmartgunItem;
import org.avp.common.item.weapon.M83A2SADARItem;
import org.avp.common.item.weapon.M88Mod4CombatPistolItem;
import org.avp.common.item.weapon.OldPainlessItem;
import org.avp.common.item.weapon.SniperRifleItem;

public class AVPWeaponItemRenderers {

    public static final Map<Class<? extends Item>, Supplier<GeoItemRenderer<?>>> WEAPON_ITEM_RENDERERS = Map.ofEntries(
        Map.entry(AK47Item.class, AK47ItemRenderer::new),
        Map.entry(F90RifleItem.class, F90RifleItemRenderer::new),
        Map.entry(FlamethrowerSevastopolItem.class, FlamethrowerSevastopolItemRenderer::new),
        Map.entry(M3712ShotgunItem.class, M3712ShotgunItemRenderer::new),
        Map.entry(M4CarbineItem.class, M4CarbineItemRenderer::new),
        Map.entry(M41APulseRifleItem.class, M41APulseRifleItemRenderer::new),
        Map.entry(M56SmartgunItem.class, M56SmartgunItemRenderer::new),
        Map.entry(M83A2SADARItem.class, M83A2SADARItemRenderer::new),
        Map.entry(M88Mod4CombatPistolItem.class, M88Mod4CombatPistolItemRenderer::new),
        Map.entry(OldPainlessItem.class, OldPainlessItemRenderer::new),
        Map.entry(SniperRifleItem.class, SniperRifleItemRenderer::new)
    );

    private AVPWeaponItemRenderers() {
        throw new UnsupportedOperationException();
    }
}
