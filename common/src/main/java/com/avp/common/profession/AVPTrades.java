package com.avp.common.profession;

import com.avp.service.Services;

public class AVPTrades {

    public static void initialize() {
        Services.REGISTRY.registerVillagerTrade(AVPProfessions.COMMISSARY, 1, AVPCommonTrades.level1Trades);
        Services.REGISTRY.registerVillagerTrade(AVPProfessions.COMMISSARY, 2, AVPCommonTrades.level2Trades);
        Services.REGISTRY.registerVillagerTrade(AVPProfessions.COMMISSARY, 3, AVPCommonTrades.level3Trades);
        Services.REGISTRY.registerVillagerTrade(AVPProfessions.COMMISSARY, 4, AVPCommonTrades.level4Trades);
        Services.REGISTRY.registerVillagerTrade(AVPProfessions.COMMISSARY, 5, AVPCommonTrades.level5Trades);
    }
}
