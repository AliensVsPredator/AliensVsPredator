package com.avp.common.registry.init;

import com.avp.common.data.CommissaryTradeLevels;
import com.avp.service.Services;

public class AVPVillagerTrades {

    public static void initialize() {
        Services.REGISTRY.registerVillagerTrade(AVPVillagerProfessions.COMMISSARY, 1, CommissaryTradeLevels.LEVEL_1);
        Services.REGISTRY.registerVillagerTrade(AVPVillagerProfessions.COMMISSARY, 2, CommissaryTradeLevels.LEVEL_2);
        Services.REGISTRY.registerVillagerTrade(AVPVillagerProfessions.COMMISSARY, 3, CommissaryTradeLevels.LEVEL_3);
        Services.REGISTRY.registerVillagerTrade(AVPVillagerProfessions.COMMISSARY, 4, CommissaryTradeLevels.LEVEL_4);
        Services.REGISTRY.registerVillagerTrade(AVPVillagerProfessions.COMMISSARY, 5, CommissaryTradeLevels.LEVEL_5);
    }
}
