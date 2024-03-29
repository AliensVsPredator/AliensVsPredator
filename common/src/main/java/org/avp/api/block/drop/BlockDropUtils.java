package org.avp.api.block.drop;

import net.minecraft.world.item.Item;

import org.avp.api.block.drop.key.BlockDropKeys;
import org.avp.api.GameObject;

public class BlockDropUtils {

    public static BlockDrop ore(GameObject<Item> itemGameObject) {
        return BlockDrops.ITEM.apply(BlockDropKeys.ORE, itemGameObject::get);
    }

    private BlockDropUtils() {
        throw new UnsupportedOperationException();
    }
}
