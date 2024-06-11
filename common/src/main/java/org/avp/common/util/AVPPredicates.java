package org.avp.common.util;

import net.minecraft.world.entity.LivingEntity;
import org.avp.api.util.BLPredicates;
import org.avp.common.data.tag.AVPEntityTypeTags;

import java.util.function.Predicate;

public class AVPPredicates {

    public static final Predicate<LivingEntity> IS_HOST = livingEntity ->
        !BLPredicates.IS_IMMORTAL.test(livingEntity) &&
        !livingEntity.getType().is(AVPEntityTypeTags.ALIENS) &&
        !livingEntity.getType().is(AVPEntityTypeTags.NON_HOSTS);

    private AVPPredicates() {
        throw new UnsupportedOperationException();
    }
}
