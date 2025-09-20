package com.alien.common.util;

import com.alien.common.gameplay.entity.living.alien.Alien;
import com.alien.common.model.alien.variant.AlienVariant;
import net.minecraft.world.entity.EntityType;

import com.avp.common.util.AVPEntityTransitionUtil;

public class AlienTransitionUtil {

    public static <T extends Alien> AlienTransitionResult transitionIntoVariant(T alien, AlienVariant alienVariant) {
        var level = alien.level();

        if (level.isClientSide) {
            // Can't create entities client-side, so return.
            return AlienTransitionResult.ClientSide.INSTANCE;
        }

        if (alien.getVariant() == alienVariant) {
            // The alien is already the given variant, so return.
            return AlienTransitionResult.AlreadyDesiredVariant.INSTANCE;
        }

        @SuppressWarnings("unchecked")
        var variantType = (EntityType<T>) alien.getTypeForVariant(alienVariant);

        if (variantType == null) {
            // The alien has no corresponding type for the given variant, nothing we can do here, so return.
            return AlienTransitionResult.NoTypeForVariant.INSTANCE;
        }

        return new AlienTransitionResult.Result(AVPEntityTransitionUtil.transitionInto(alien, variantType));
    }

    public sealed interface AlienTransitionResult {

        enum ClientSide implements AlienTransitionResult {
            INSTANCE
        }

        enum AlreadyDesiredVariant implements AlienTransitionResult {
            INSTANCE
        }

        enum NoTypeForVariant implements AlienTransitionResult {
            INSTANCE
        }

        record Result(AVPEntityTransitionUtil.EntityTransitionResult result) implements AlienTransitionResult {}
    }
}
