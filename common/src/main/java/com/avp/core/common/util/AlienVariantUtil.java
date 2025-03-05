package com.avp.core.common.util;

import net.minecraft.world.item.Item;

import com.avp.core.common.entity.living.alien.Alien;
import com.avp.core.common.item.AVPItems;

public class AlienVariantUtil {

    public static Item getResinBallFor(Alien alien) {
        return alien.isNetherAfflicted() ? AVPItems.NETHER_RESIN_BALL.get() : AVPItems.RESIN_BALL.get();
    }
}
