package com.avp.mixin;

import com.avp.common.block_item.AVPBlockItems;
import com.avp.common.item.AVPItemTags;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ItemEntity.class)
public class ItemEntityMixin_InsertRadioactiveItemsIntoLeadChest {

    @Shadow private int pickupDelay;

    @Shadow private @Nullable UUID target;

    @Inject(method = "playerTouch", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getCount()I"), cancellable = true)
    private void onPickupItem(Player player, CallbackInfo ci, @Local(ordinal = 0) ItemStack pickedItemStack) {
        if (!pickedItemStack.is(AVPItemTags.RADIATION_ITEMS) || pickupDelay > 0) return;

        if (target == null || target.equals(player.getUUID()))
            for (ItemStack inventoryItemStack : player.getInventory().items) {
                if (!inventoryItemStack.is(AVPBlockItems.LEAD_CHEST)) continue;

                int count = pickedItemStack.getCount();
                insertPickedItemIntoLeadChest(inventoryItemStack, pickedItemStack);

                if (pickedItemStack.isEmpty()) {
                    ItemEntity thisItemEntity = (ItemEntity) (Object) this;
                    player.take(thisItemEntity, count);

                    thisItemEntity.discard();

                    player.awardStat(Stats.ITEM_PICKED_UP.get(pickedItemStack.getItem()), count);
                    player.onItemPickup(thisItemEntity);
                    ci.cancel();
                }
            }
    }

    @Unique
    private void insertPickedItemIntoLeadChest(ItemStack leadChestItemStack, ItemStack pickedItemStack) {
        Iterable<ItemStack> oldLeadChestItems = leadChestItemStack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).nonEmptyItems();
        NonNullList<ItemStack> newLeadChestItems = NonNullList.create();

        int remainingToAdd = pickedItemStack.getCount();

        for (ItemStack oldLeadChestItem : oldLeadChestItems) {
            if (oldLeadChestItem.isStackable() && ItemStack.isSameItemSameComponents(oldLeadChestItem, pickedItemStack)) {
                int remainingToStack = oldLeadChestItem.getMaxStackSize() - oldLeadChestItem.getCount();
                if (remainingToStack > 0 && remainingToAdd > 0) {
                    int toAdd = Math.min(remainingToStack, remainingToAdd);
                    oldLeadChestItem.grow(toAdd);
                    remainingToAdd -= toAdd;
                    pickedItemStack.shrink(toAdd);
                }
            }
            newLeadChestItems.add(oldLeadChestItem);
        }

        if (remainingToAdd > 0 && newLeadChestItems.size() < 27)
            newLeadChestItems.add(pickedItemStack.copyAndClear());

        leadChestItemStack.applyComponents(DataComponentPatch.builder()
                .set(DataComponents.CONTAINER, ItemContainerContents.fromItems(newLeadChestItems))
                .build()
        );
    }
}
