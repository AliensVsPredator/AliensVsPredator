package com.avp.fabric.common.profession;

import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;

import com.avp.common.profession.AVPCommonTrades;

public class AVPTrades {

    public static void initialize() {
        TradeOfferHelper.registerVillagerOffers(AVPProfessions.COMMISSARY, 1, factories -> factories.addAll(AVPCommonTrades.level1Trades));
        TradeOfferHelper.registerVillagerOffers(AVPProfessions.COMMISSARY, 2, factories -> factories.addAll(AVPCommonTrades.level2Trades));
        TradeOfferHelper.registerVillagerOffers(AVPProfessions.COMMISSARY, 3, factories -> factories.addAll(AVPCommonTrades.level3Trades));
        TradeOfferHelper.registerVillagerOffers(AVPProfessions.COMMISSARY, 4, factories -> factories.addAll(AVPCommonTrades.level4Trades));
        TradeOfferHelper.registerVillagerOffers(AVPProfessions.COMMISSARY, 5, factories -> factories.addAll(AVPCommonTrades.level5Trades));
    }
}
