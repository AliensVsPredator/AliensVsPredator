package com.blib.api.common.registry.v1.impl;

import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

import com.blib.api.common.mod.v1.BLibMod;
import com.blib.api.common.mod.v1.model.BLibModState;
import com.blib.api.common.registry.v1.BLibHolder;
import com.blib.api.common.registry.v1.exception.BLibRegistrationException;
import com.blib.internal.service.BLibInternalServices;

public class BLibVillagerTradeRegistry {

    private final BLibMod mod;

    @ApiStatus.Internal
    public BLibVillagerTradeRegistry(BLibMod mod) {
        this.mod = mod;
    }

    public void register(BLibHolder<VillagerProfession> holder, int level, List<VillagerTrades.ItemListing> villagerTradeItemListings) {
        if (mod.state() != BLibModState.INITIALIZING) {
            throw new BLibRegistrationException(
                "Attempted to register villager trades outside of mod's initialization window. Villager Profession BLibHolder: %s, Mod State: %s"
                    .formatted(holder, mod.state())
            );
        }

        BLibInternalServices.REGISTRY.registerVillagerTrade(holder, level, villagerTradeItemListings);
    }
}
