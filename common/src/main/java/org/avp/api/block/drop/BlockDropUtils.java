package org.avp.api.block.drop;

import net.minecraft.world.item.Item;

import org.avp.api.Holder;
import org.avp.api.block.drop.key.BlockDropKeys;

public class BlockDropUtils {

    public static BlockDrop ore(Holder<Item> itemHolder) {
        return BlockDrops.ITEM.apply(BlockDropKeys.ORE, itemHolder::get);
    }

    private BlockDropUtils() {
        throw new UnsupportedOperationException();
    }
}
