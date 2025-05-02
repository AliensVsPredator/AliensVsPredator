package com.avp.fabric.common.profession;

import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;

import com.avp.common.block.TempAVPBlocks;
import com.avp.common.item.AVPArmorItems;
import com.avp.common.item.TempAVPItems;

public class AVPTrades {

    public static void initialize() {
        TradeOfferHelper.registerVillagerOffers(AVPProfessions.COMMISSARY, 1, factories -> {
            factories.add(
                (entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 1),
                    new ItemStack(TempAVPItems.SMALL_BULLET.get(), 8),
                    4,
                    7,
                    0.04f
                )
            );
            factories.add(
                (entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 2),
                    new ItemStack(TempAVPItems.MEDIUM_BULLET.get(), 16),
                    3,
                    12,
                    0.09f
                )
            );
            factories.add(
                (entity, random) -> new MerchantOffer(
                    new ItemCost(Items.GUNPOWDER, 16),
                    new ItemStack(Items.EMERALD, 4),
                    3,
                    12,
                    0.09f
                )
            );
        });

        TradeOfferHelper.registerVillagerOffers(AVPProfessions.COMMISSARY, 2, factories -> {
            factories.add(
                (entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 5),
                    new ItemStack(AVPArmorItems.TACTICAL_CAMO_HELMET, 1),
                    4,
                    7,
                    0.04f
                )
            );
            factories.add(
                (entity, random) -> new MerchantOffer(
                    new ItemCost(Items.CLAY_BALL, 12),
                    new ItemStack(Items.EMERALD, 2),
                    3,
                    12,
                    0.09f
                )
            );
            factories.add(
                (entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 8),
                    new ItemStack(AVPArmorItems.TACTICAL_CAMO_CHESTPLATE, 1),
                    3,
                    12,
                    0.09f
                )
            );
        });

        TradeOfferHelper.registerVillagerOffers(AVPProfessions.COMMISSARY, 3, factories -> {
            factories.add(
                (entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 15),
                    new ItemStack(TempAVPItems.BLUEPRINT_M88MOD4_COMBAT_PISTOL.get(), 1),
                    4,
                    7,
                    0.04f
                )
            );
            factories.add(
                (entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 10),
                    new ItemStack(AVPArmorItems.TACTICAL_CAMO_LEGGINGS, 1),
                    3,
                    12,
                    0.09f
                )
            );
            factories.add(
                (entity, random) -> new MerchantOffer(
                    new ItemCost(TempAVPItems.STEEL_INGOT.get(), 8),
                    new ItemStack(Items.EMERALD, 8),
                    3,
                    12,
                    0.09f
                )
            );
        });

        TradeOfferHelper.registerVillagerOffers(AVPProfessions.COMMISSARY, 4, factories -> {
            factories.add(
                (entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 8),
                    new ItemStack(AVPArmorItems.TACTICAL_CAMO_BOOTS, 1),
                    4,
                    7,
                    0.04f
                )
            );
            factories.add(
                (entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 10),
                    new ItemStack(TempAVPItems.BLUEPRINT_M4RA_BATTLE_RIFLE.get(), 1),
                    3,
                    12,
                    0.09f
                )
            );
            factories.add(
                (entity, random) -> new MerchantOffer(
                    new ItemCost(TempAVPItems.BRASS_INGOT.get(), 12),
                    new ItemStack(Items.EMERALD, 10),
                    3,
                    12,
                    0.09f
                )
            );
        });

        TradeOfferHelper.registerVillagerOffers(AVPProfessions.COMMISSARY, 5, factories -> {
            factories.add(
                (entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 25),
                    new ItemStack(TempAVPBlocks.SENTRY_TURRET.get(), 1),
                    4,
                    7,
                    0.04f
                )
            );
            factories.add(
                (entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 10),
                    new ItemStack(TempAVPItems.GRENADE, 3),
                    3,
                    12,
                    0.09f
                )
            );
            factories.add(
                (entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 50),
                    new ItemStack(TempAVPItems.BLUEPRINT_M6B_ROCKET_LAUNCHER.get(), 1),
                    3,
                    12,
                    0.09f
                )
            );
        });
    }
}
