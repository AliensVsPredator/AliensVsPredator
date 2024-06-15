package org.avp.common.data.alien.maturation;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;

import java.util.function.Predicate;

import org.avp.api.util.BLPredicates;

public record AlienMaturationStep(
    EntityType<? extends Monster> from,
    EntityType<? extends Monster> to,
    int growthTime,
    Predicate<? super Entity> canMaturePredicate
) {

    public AlienMaturationStep(
        EntityType<? extends Monster> from,
        EntityType<? extends Monster> to,
        int growthTime
    ) {
        this(from, to, growthTime, BLPredicates.ALWAYS_TRUE);
    }
}
