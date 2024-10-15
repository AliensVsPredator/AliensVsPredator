package org.avp.common;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AVPConstants {

    public static final String MOD_ID = "avp";

    public static final String MOD_NAME = "AliensVsPredator";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static Predicate<Entity> isCreativeSpecPlayer = entity -> (entity instanceof Player playerEntity && (playerEntity.isCreative() || playerEntity.isSpectator()));

    private AVPConstants() {
        throw new UnsupportedOperationException();
    }
}
