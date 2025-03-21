package com.avp.common.util;

import net.minecraft.world.item.Item;

import com.avp.common.entity.living.alien.Alien;
import com.avp.common.item.AVPItems;

public class AlienVariantUtil {

    public static Item getResinBallFor(Alien alien) {
        return switch (alien) {
            case Alien netherAlien when netherAlien.isNetherAfflicted() -> AVPItems.NETHER_RESIN_BALL;
            case Alien aberrantAlien when aberrantAlien.isAberrant() -> AVPItems.ABERRANT_RESIN_BALL;
            case Alien irradiatedAlien when irradiatedAlien.isIrradiated() -> AVPItems.IRRADIATED_RESIN_BALL;
            default -> AVPItems.RESIN_BALL;
        };
    }
}
