package com.avp.fabric.data.growth_stages;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

import com.avp.fabric.data.growth_stages.provider.AberrantAlienGrowthStageProvider;
import com.avp.fabric.data.growth_stages.provider.AlienGrowthStageProvider;
import com.avp.fabric.data.growth_stages.provider.NetherAlienGrowthStageProvider;

public class GrowthStageSubProvider extends GrowthStageDataProvider {

    public GrowthStageSubProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    protected void generate() {
        AlienGrowthStageProvider.provide(this::add);
        AberrantAlienGrowthStageProvider.provide(this::add);
        NetherAlienGrowthStageProvider.provide(this::add);
    }
}
