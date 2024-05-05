package org.avp.common.util;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public class AVPPredicates {

    public static final Predicate<Player> IS_IMMORTAL = player -> player.isCreative() || player.isSpectator();

    public static final Predicate<Item> IS_ITEM_SHULKER_BOX = item -> item instanceof BlockItem blockItem &&
        blockItem.getBlock().defaultBlockState().is(BlockTags.SHULKER_BOXES);

    private AVPPredicates() {
        throw new UnsupportedOperationException();
    }
}
