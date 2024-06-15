package org.avp.api.common.weapon.ammunition;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

import org.avp.api.common.weapon.reload.ReloadBehavior;
import org.avp.api.util.BLPredicates;

public class ConsumeAmmunitionAction {

    public static void fromPlayerInventory(ServerPlayer player, int ammunitionCountToRestore, Item ammunition) {
        var counter = ammunitionCountToRestore;
        for (var itemStack : player.getInventory().items) {
            if (counter <= 0) {
                break;
            }

            if (Objects.equals(itemStack.getItem(), ammunition)) {
                // If the stack item is the same type as the ammunition item, consume part or all of the stack.
                var count = itemStack.getCount();
                var amountToConsume = Math.min(count, counter);
                itemStack.setCount(count - amountToConsume);
                counter -= amountToConsume;
            } else if (BLPredicates.IS_ITEM_SHULKER_BOX.test(itemStack.getItem())) {
                // If the stack item is a shulker box, try to consume ammunition from it.
                var itemStackTag = itemStack.getTag();

                if (itemStackTag == null)
                    continue;

                counter = fromShulkerBox(itemStack, itemStackTag, ammunition, counter);
            }
        }
    }

    private static int fromShulkerBox(ItemStack itemStack, CompoundTag itemStackTag, Item ammunition, int counter) {
        var blockEntityTag = itemStackTag.getCompound(ReloadBehavior.BLOCK_ENTITY_TAG_ID);
        var shulkerBoxItemStacks = NonNullList.withSize(27, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(blockEntityTag, shulkerBoxItemStacks);

        for (var shulkerBoxItemStack : shulkerBoxItemStacks) {
            if (shulkerBoxItemStack.getItem() != ammunition)
                continue;

            // If the stack item is the same type as the ammunition item, consume part or all of the stack.
            var count = shulkerBoxItemStack.getCount();
            var amountToConsume = Math.min(count, counter);
            shulkerBoxItemStack.setCount(count - amountToConsume);
            counter -= amountToConsume;
        }

        // Save the modified shulker box item stacks back to the shulker box block entity tag.
        ContainerHelper.saveAllItems(blockEntityTag, shulkerBoxItemStacks, true);
        // Save the block entity tag back to the item stack tag.
        itemStackTag.put(ReloadBehavior.BLOCK_ENTITY_TAG_ID, blockEntityTag);
        // Set the item stack tag back on the item stack.
        itemStack.setTag(itemStackTag);

        return counter;
    }

    private ConsumeAmmunitionAction() {
        throw new UnsupportedOperationException();
    }
}
