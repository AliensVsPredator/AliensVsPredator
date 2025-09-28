package com.lib.common.gameplay.goap;

import com.just.goap.GOAPKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class GOAPKeys {

    public static final GOAPKey<Boolean> IS_FULL_HEALTH = new GOAPKey<>("is_full_health");

    public static final GOAPKey<Boolean> IS_IN_LAVA = new GOAPKey<>("is_in_lava");

    public static final GOAPKey<Boolean> IS_ON_FIRE = new GOAPKey<>("is_on_fire");

    public static final GOAPKey<Boolean> IS_UNDERWATER = new GOAPKey<>("is_underwater");

    public static final GOAPKey<List<Entity>> NEARBY_ENTITIES = new GOAPKey<>("nearby_entities");

    public static final GOAPKey<List<LivingEntity>> NEARBY_LIVING_ENTITIES = new GOAPKey<>("nearby_living_entities");
}
