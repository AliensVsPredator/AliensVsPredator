package org.avp.common.util;

import net.minecraft.world.entity.player.Player;

import java.util.function.Predicate;

public class AVPPredicates {

    public static final Predicate<Player> IS_IMMORTAL = player -> player.isCreative() || player.isSpectator();

    private AVPPredicates() {
        throw new UnsupportedOperationException();
    }
}
