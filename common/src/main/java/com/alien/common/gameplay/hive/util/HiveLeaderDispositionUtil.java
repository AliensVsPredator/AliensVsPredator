package com.alien.common.gameplay.hive.util;

import net.minecraft.world.entity.EntityType;

import com.avp.common.registry.tag.AVPEntityTypeTags;

public class HiveLeaderDispositionUtil {

    public static boolean isLeftLowerDisposition(EntityType<?> current, EntityType<?> other) {
        var currentDisposition = getDispositionForEntityType(current);
        var contestantDisposition = getDispositionForEntityType(other);
        return currentDisposition < contestantDisposition;
    }

    public static int getDispositionForEntityType(EntityType<?> entityType) {
        if (entityType.is(AVPEntityTypeTags.DRONES) || entityType.is(AVPEntityTypeTags.RUNNERS)) {
            return 0;
        } else if (entityType.is(AVPEntityTypeTags.WARRIORS) || entityType.is(AVPEntityTypeTags.PROWLERS)) {
            return 1;
        } else if (entityType.is(AVPEntityTypeTags.PRAETORIANS) || entityType.is(AVPEntityTypeTags.CRUSHERS)) {
            return 2;
        } else if (entityType.is(AVPEntityTypeTags.PREDALIENS)) {
            return 3;
        } else if (entityType.is(AVPEntityTypeTags.QUEENS)) {
            return 4;
        }

        return -1;
    }
}
