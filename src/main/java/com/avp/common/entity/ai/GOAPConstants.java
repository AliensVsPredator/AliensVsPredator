package com.avp.common.entity.ai;

import net.minecraft.world.entity.Entity;

import java.util.List;

import com.avp.goap.TypedIdentifier;

public class GOAPConstants {

    public static final TypedIdentifier<Boolean> HAS_FOOD = new TypedIdentifier<>("hasFood");

    public static final TypedIdentifier<Boolean> IS_HEALTHY = new TypedIdentifier<>("isHealthy");

    public static final TypedIdentifier<List<? extends Entity>> NEARBY_ENTITIES = new TypedIdentifier<>("nearbyEntities");
}
