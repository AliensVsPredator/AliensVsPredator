package com.avp.common.registry.init.entity_type;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.avp.common.registry.AVPDeferredHolder;
import com.avp.service.Services;

public class AVPEntityTypes {

    public static final MobCategory ALIEN_CATEGORY = Services.BRIDGE.getAlienMobCategory();

    public static final MobCategory OVOMORPH_CATEGORY = Services.BRIDGE.getOvomorphMobCategory();

    public static final MobCategory PREDATOR_CATEGORY = Services.BRIDGE.getPredatorMobCategory();

    private static final List<AVPDeferredHolder<? extends EntityType<?>>> ENTITY_TYPE_HOLDERS = new ArrayList<>();

    public static List<AVPDeferredHolder<? extends EntityType<?>>> getAll() {
        return Collections.unmodifiableList(ENTITY_TYPE_HOLDERS);
    }

    public static <T extends Entity> AVPDeferredHolder<EntityType<T>> register(String id, EntityType.Builder<T> builder) {
        var holder = Services.REGISTRY.register(
            BuiltInRegistries.ENTITY_TYPE,
            id,
            () -> ((SilencedEntityTypeBuilder) builder).<T>avp$buildWithoutDataFixerCheck()
        );

        ENTITY_TYPE_HOLDERS.add(holder);

        return holder;
    }

    public static void initialize() {}
}
