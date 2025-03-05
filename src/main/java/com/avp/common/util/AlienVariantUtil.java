package com.avp.common.util;

import net.minecraft.world.item.Item;

import com.avp.common.entity.living.alien.Alien;
import com.avp.common.item.AVPItems;

public class AlienVariantUtil {

    public static Item getResinBallFor(Alien alien) {
        return alien.isNetherAfflicted() ? AVPItems.NETHER_RESIN_BALL : AVPItems.RESIN_BALL;
    }
}
