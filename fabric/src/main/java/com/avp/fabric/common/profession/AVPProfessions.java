package com.avp.fabric.common.profession;

import com.google.common.collect.ImmutableSet;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.level.block.Block;

import com.avp.AVPResources;
import com.avp.common.block.TempAVPBlocks;

public class AVPProfessions {

    public static final ResourceKey<PoiType> COMMISSARY_KEY = poiKey("commissary_poi");

    public static final PoiType COMMISSARY_POI = registerPoi("commissary_poi", TempAVPBlocks.BLUEPRINT_BLOCK.get());

    public static final VillagerProfession COMMISSARY = registerProfession("commissary", COMMISSARY_KEY);

    private static VillagerProfession registerProfession(String name, ResourceKey<PoiType> type) {
        return Registry.register(
            BuiltInRegistries.VILLAGER_PROFESSION,
            AVPResources.location(name),
            new VillagerProfession(
                name,
                entry -> entry.is(type),
                entry -> entry.is(type),
                ImmutableSet.of(),
                ImmutableSet.of(),
                SoundEvents.VILLAGER_WORK_WEAPONSMITH
            )
        );
    }

    private static PoiType registerPoi(String name, Block block) {
        return PointOfInterestHelper.register(AVPResources.location(name), 1, 1, block);
    }

    private static ResourceKey<PoiType> poiKey(String name) {
        return ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, AVPResources.location(name));
    }

    public static void initialize() {}
}
