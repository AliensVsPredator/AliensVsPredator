package com.avp.neoforge.common.profession;

import com.google.common.collect.ImmutableSet;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import com.avp.AVP;
import com.avp.common.block.AVPBlocks;

// FIXME NOT QUITE SURE HOW TO BEST COMMON THIS MYSELF
public class AVPProfessions {

    public static final DeferredRegister<PoiType> POI_TYPES =
        DeferredRegister.create(BuiltInRegistries.POINT_OF_INTEREST_TYPE, AVP.MOD_ID);

    public static final DeferredRegister<VillagerProfession> VILLAGER_PROFESSIONS =
        DeferredRegister.create(BuiltInRegistries.VILLAGER_PROFESSION, AVP.MOD_ID);

    public static final Holder<PoiType> COMMISSARY_POI = POI_TYPES.register(
        "commissary_poi",
        () -> new PoiType(ImmutableSet.copyOf(AVPBlocks.BLUEPRINT_BLOCK.get().getStateDefinition().getPossibleStates()), 1, 1)
    );

    public static final Holder<VillagerProfession> COMMISSARY = VILLAGER_PROFESSIONS.register(
        "commissary",
        () -> new VillagerProfession(
            "commissary",
            holder -> holder.value() == COMMISSARY_POI.value(),
            poiTypeHolder -> poiTypeHolder.value() == COMMISSARY_POI.value(),
            ImmutableSet.of(),
            ImmutableSet.of(),
            SoundEvents.VILLAGER_WORK_WEAPONSMITH
        )
    );

    public static void register(IEventBus eventBus) {
        POI_TYPES.register(eventBus);
        VILLAGER_PROFESSIONS.register(eventBus);
    }
}
