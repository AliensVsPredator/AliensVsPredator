package org.avp.api.weapon.ammunition;

import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import org.avp.api.util.BLPredicates;
import org.avp.api.weapon.reload.ReloadBehavior;

public class CountAmmunitionAction {

    public static int inPlayerInventoryIncludingShulkerBoxes(ServerPlayer player, Item ammunitionItem) {
        var rawInventoryItemCount = 0;
        for (var itemStack : player.getInventory().items) {
            if (itemStack.is(ammunitionItem)) {
                rawInventoryItemCount += itemStack.getCount();
                continue;
            }

            if (BLPredicates.IS_ITEM_SHULKER_BOX.test(itemStack.getItem())) {
                var itemStackTag = itemStack.getTag();

                if (itemStackTag == null) {
                    continue;
                }

                var blockEntityTag = itemStackTag.getCompound(ReloadBehavior.BLOCK_ENTITY_TAG_ID);
                var items = NonNullList.withSize(27, ItemStack.EMPTY);
                ContainerHelper.loadAllItems(blockEntityTag, items);

                for (var shulkerBoxItemStack : items) {
                    if (shulkerBoxItemStack.is(ammunitionItem)) {
                        rawInventoryItemCount += shulkerBoxItemStack.getCount();
                    }
                }
            }
        }

        return rawInventoryItemCount;
    }

    private CountAmmunitionAction() {
        throw new UnsupportedOperationException();
    }
}
