package com.avp.fabric.common.profession;

import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;

import com.avp.fabric.common.block.AVPBlocks;
import com.avp.fabric.common.item.AVPItems;
import com.avp.fabric.common.item.ArmorItems;

public class AVPTrades {

    public static void initialize() {
        TradeOfferHelper.registerVillagerOffers(AVPProfessions.COMMISSARY, 1, factories -> {
            factories.add(
                (entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 1),
                    new ItemStack(AVPItems.SMALL_BULLET, 8),
                    4,
                    7,
                    0.04f
                )
            );
            factories.add(
                (entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 2),
                    new ItemStack(AVPItems.MEDIUM_BULLET, 16),
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
                    new ItemStack(ArmorItems.TACTICAL_CAMO_HELMET, 1),
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
                    new ItemStack(ArmorItems.TACTICAL_CAMO_CHESTPLATE, 1),
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
                    new ItemStack(AVPItems.BLUEPRINT_M88MOD4_COMBAT_PISTOL, 1),
                    4,
                    7,
                    0.04f
                )
            );
            factories.add(
                (entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 10),
                    new ItemStack(ArmorItems.TACTICAL_CAMO_LEGGINGS, 1),
                    3,
                    12,
                    0.09f
                )
            );
            factories.add(
                (entity, random) -> new MerchantOffer(
                    new ItemCost(AVPItems.STEEL_INGOT, 8),
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
                    new ItemStack(ArmorItems.TACTICAL_CAMO_BOOTS, 1),
                    4,
                    7,
                    0.04f
                )
            );
            factories.add(
                (entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 10),
                    new ItemStack(AVPItems.BLUEPRINT_M4RA_BATTLE_RIFLE, 1),
                    3,
                    12,
                    0.09f
                )
            );
            factories.add(
                (entity, random) -> new MerchantOffer(
                    new ItemCost(AVPItems.BRASS_INGOT, 12),
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
                    new ItemStack(AVPBlocks.SENTRY_TURRET, 1),
                    4,
                    7,
                    0.04f
                )
            );
            factories.add(
                (entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 10),
                    new ItemStack(AVPItems.GRENADE, 3),
                    3,
                    12,
                    0.09f
                )
            );
            factories.add(
                (entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 50),
                    new ItemStack(AVPItems.BLUEPRINT_M6B_ROCKET_LAUNCHER, 1),
                    3,
                    12,
                    0.09f
                )
            );
        });
    }
}
