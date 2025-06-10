package com.alien.common.util;

import com.alien.common.gameplay.entity.living.alien.Alien;
import com.alien.common.model.alien.variant.AlienVariant;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;

import com.avp.common.util.AVPEntityTransitionUtil;

public class AlienTransitionUtil {

    public static <T extends Alien> @Nullable T transitionIntoVariant(T alien, AlienVariant alienVariant) {
        var level = alien.level();

        if (level.isClientSide) {
            // Can't create entities client-side, so return.
            return null;
        }

        if (alien.getVariant() == alienVariant) {
            // The alien is already the given variant, so return.
            return null;
        }

        @SuppressWarnings("unchecked")
        var variantType = (EntityType<T>) alien.getTypeForVariant(alienVariant);

        if (variantType == null) {
            // The alien has no corresponding type for the given variant, nothing we can do here, so return.
            return null;
        }

        return AVPEntityTransitionUtil.transitionInto(alien, variantType);
    }

}
