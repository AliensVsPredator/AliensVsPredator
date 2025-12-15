package com.blib.fabric.service;

import com.avp.service.RegistryService;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;

import java.util.List;
import java.util.function.Supplier;

public class FabricRegistryService implements RegistryService {

    @Override
    public void registerVillagerTrade(
        Supplier<VillagerProfession> villagerProfessionSupplier,
        int level,
        List<VillagerTrades.ItemListing> villagerTradeItemListings
    ) {
        TradeOfferHelper.registerVillagerOffers(
            villagerProfessionSupplier.get(),
            level,
            factories -> factories.addAll(villagerTradeItemListings)
        );
    }
}
