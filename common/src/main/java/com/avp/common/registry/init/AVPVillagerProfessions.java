package com.avp.common.registry.init;

import com.google.common.collect.ImmutableSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;

import com.avp.common.registry.AVPDeferredHolder;
import com.avp.common.registry.key.AVPVillagerPoiKeys;
import com.avp.service.Services;

public class AVPVillagerProfessions {

    public static final AVPDeferredHolder<VillagerProfession> COMMISSARY = register("commissary", AVPVillagerPoiKeys.COMMISSARY_POI_KEY);

    private static AVPDeferredHolder<VillagerProfession> register(String id, ResourceKey<PoiType> type) {
        return Services.REGISTRY.register(
            BuiltInRegistries.VILLAGER_PROFESSION,
            id,
            () -> new VillagerProfession(
                id,
                entry -> entry.is(type),
                entry -> entry.is(type),
                ImmutableSet.of(),
                ImmutableSet.of(),
                SoundEvents.VILLAGER_WORK_WEAPONSMITH
            )
        );
    }

    public static void initialize() {}
}
