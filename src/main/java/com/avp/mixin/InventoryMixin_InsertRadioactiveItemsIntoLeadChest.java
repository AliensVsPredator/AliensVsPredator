package com.avp.mixin;

import com.avp.common.block_item.AVPBlockItems;
import com.avp.common.item.AVPItemTags;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Inventory.class)
public class InventoryMixin_InsertRadioactiveItemsIntoLeadChest {

    @Shadow @Final public Player player;

    @Shadow @Final public NonNullList<ItemStack> items;

    @Inject(method = "add(ILnet/minecraft/world/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    private void onAddItem(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (!stack.is(AVPItemTags.RADIATION_ITEMS)) return;

        for (ItemStack inventoryItemStack : items) {
            if (!inventoryItemStack.is(AVPBlockItems.LEAD_CHEST)) continue;

            insertItemIntoLeadChest(inventoryItemStack, stack);

            if (stack.isEmpty())
                cir.setReturnValue(true);
        }
    }

    @Inject(method = "setItem", at = @At("HEAD"), cancellable = true)
    private void onSetItem(int slot, ItemStack stack, CallbackInfo ci) {
        if (!stack.is(AVPItemTags.RADIATION_ITEMS)) return;

        for (ItemStack inventoryItemStack : items) {
            if (!inventoryItemStack.is(AVPBlockItems.LEAD_CHEST)) continue;

            insertItemIntoLeadChest(inventoryItemStack, stack);

            if (stack.isEmpty())
                ci.cancel();
        }
    }

    @Unique
    private void insertItemIntoLeadChest(ItemStack leadChestItemStack, ItemStack toAddItemStack) {
        Iterable<ItemStack> oldLeadChestItems = leadChestItemStack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).nonEmptyItems();
        NonNullList<ItemStack> newLeadChestItems = NonNullList.create();

        int remainingToAdd = toAddItemStack.getCount();

        for (ItemStack oldLeadChestItem : oldLeadChestItems) {
            if (oldLeadChestItem.isStackable() && ItemStack.isSameItemSameComponents(oldLeadChestItem, toAddItemStack)) {
                int remainingToStack = oldLeadChestItem.getMaxStackSize() - oldLeadChestItem.getCount();
                if (remainingToStack > 0 && remainingToAdd > 0) {
                    int toAdd = Math.min(remainingToStack, remainingToAdd);
                    oldLeadChestItem.grow(toAdd);
                    remainingToAdd -= toAdd;
                    toAddItemStack.shrink(toAdd);
                }
            }
            newLeadChestItems.add(oldLeadChestItem);
        }

        if (remainingToAdd > 0 && newLeadChestItems.size() < 27)
            newLeadChestItems.add(toAddItemStack.copyAndClear());

        leadChestItemStack.applyComponents(DataComponentPatch.builder()
                .set(DataComponents.CONTAINER, ItemContainerContents.fromItems(newLeadChestItems))
                .build()
        );
    }
}
