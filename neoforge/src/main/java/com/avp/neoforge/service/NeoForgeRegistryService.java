package com.avp.neoforge.service;

import com.avp.service.RegistryService;
import com.just.core.functional.tuple.Tuple3;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class NeoForgeRegistryService implements RegistryService {

    private final List<Tuple3<Supplier<VillagerProfession>, Integer, List<VillagerTrades.ItemListing>>> villagerTradeData;

    public NeoForgeRegistryService() {
        this.villagerTradeData = new ArrayList<>();
    }

    @Override
    public void registerVillagerTrade(
        Supplier<VillagerProfession> villagerProfessionSupplier,
        int level,
        List<VillagerTrades.ItemListing> villagerTradeItemListings
    ) {
        villagerTradeData.add(new Tuple3<>(villagerProfessionSupplier, level, villagerTradeItemListings));
    }

    public List<Tuple3<Supplier<VillagerProfession>, Integer, List<VillagerTrades.ItemListing>>> getVillagerTradeData() {
        return villagerTradeData;
    }
}
