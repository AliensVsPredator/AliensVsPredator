package com.avp.common.entity.living.villager.trade;

import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;

import java.util.List;

import com.avp.common.block.AVPBlocks;
import com.avp.common.item.AVPArmorItems;
import com.avp.common.item.AVPItems;

public class CommissaryTradeLevels {

    public static List<VillagerTrades.ItemListing> LEVEL_1 = List.of(
        (entity, randomSource) -> new MerchantOffer(
            new ItemCost(Items.EMERALD, 1),
            new ItemStack(AVPItems.SMALL_BULLET.get(), 8),
            4,
            7,
            0.04f
        ),
        (entity, randomSource) -> new MerchantOffer(
            new ItemCost(Items.EMERALD, 2),
            new ItemStack(AVPItems.MEDIUM_BULLET.get(), 16),
            3,
            12,
            0.09f
        ),
        (entity, randomSource) -> new MerchantOffer(
            new ItemCost(Items.GUNPOWDER, 16),
            new ItemStack(Items.EMERALD, 4),
            3,
            12,
            0.09f
        )
    );

    public static List<VillagerTrades.ItemListing> LEVEL_2 = List.of(
        (entity, randomSource) -> new MerchantOffer(
            new ItemCost(Items.EMERALD, 5),
            new ItemStack(AVPArmorItems.TACTICAL_CAMO_HELMET.get(), 1),
            4,
            7,
            0.04f
        ),
        (entity, randomSource) -> new MerchantOffer(
            new ItemCost(Items.CLAY_BALL, 12),
            new ItemStack(Items.EMERALD, 2),
            3,
            12,
            0.09f
        ),
        (entity, randomSource) -> new MerchantOffer(
            new ItemCost(Items.EMERALD, 8),
            new ItemStack(AVPArmorItems.TACTICAL_CAMO_CHESTPLATE.get(), 1),
            3,
            12,
            0.09f
        )
    );

    public static List<VillagerTrades.ItemListing> LEVEL_3 = List.of(
        (entity, randomSource) -> new MerchantOffer(
            new ItemCost(Items.EMERALD, 15),
            new ItemStack(AVPItems.BLUEPRINT_M88MOD4_COMBAT_PISTOL.get(), 1),
            4,
            7,
            0.04f
        ),
        (entity, randomSource) -> new MerchantOffer(
            new ItemCost(Items.EMERALD, 10),
            new ItemStack(AVPArmorItems.TACTICAL_CAMO_LEGGINGS.get(), 1),
            3,
            12,
            0.09f
        ),
        (entity, randomSource) -> new MerchantOffer(
            new ItemCost(AVPItems.STEEL_INGOT.get(), 8),
            new ItemStack(Items.EMERALD, 8),
            3,
            12,
            0.09f
        )
    );

    public static List<VillagerTrades.ItemListing> LEVEL_4 = List.of(
        (entity, randomSource) -> new MerchantOffer(
            new ItemCost(Items.EMERALD, 8),
            new ItemStack(AVPArmorItems.TACTICAL_CAMO_BOOTS.get(), 1),
            4,
            7,
            0.04f
        ),
        (entity, randomSource) -> new MerchantOffer(
            new ItemCost(Items.EMERALD, 10),
            new ItemStack(AVPItems.BLUEPRINT_M4RA_BATTLE_RIFLE.get(), 1),
            3,
            12,
            0.09f
        ),
        (entity, randomSource) -> new MerchantOffer(
            new ItemCost(AVPItems.BRASS_INGOT.get(), 12),
            new ItemStack(Items.EMERALD, 10),
            3,
            12,
            0.09f
        )
    );

    public static List<VillagerTrades.ItemListing> LEVEL_5 = List.of(
        (entity, randomSource) -> new MerchantOffer(
            new ItemCost(Items.EMERALD, 25),
            new ItemStack(AVPBlocks.SENTRY_TURRET.get(), 1),
            4,
            7,
            0.04f
        ),
        (entity, randomSource) -> new MerchantOffer(
            new ItemCost(Items.EMERALD, 10),
            new ItemStack(AVPItems.GRENADE.get(), 3),
            3,
            12,
            0.09f
        ),
        (entity, randomSource) -> new MerchantOffer(
            new ItemCost(Items.EMERALD, 50),
            new ItemStack(AVPItems.BLUEPRINT_M6B_ROCKET_LAUNCHER.get(), 1),
            3,
            12,
            0.09f
        )
    );
}
