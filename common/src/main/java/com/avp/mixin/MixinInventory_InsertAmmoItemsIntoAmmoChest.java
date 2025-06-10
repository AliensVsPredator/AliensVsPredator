package com.avp.mixin;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
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

import com.avp.common.registry.init.item.AVPBlockItems;
import com.avp.common.registry.tag.AVPItemTags;
import com.avp.common.util.AVPPredicates;

@Mixin(Inventory.class)
public class MixinInventory_InsertAmmoItemsIntoAmmoChest {

    @Shadow
    @Final
    public Player player;

    @Shadow
    @Final
    public NonNullList<ItemStack> items;

    @Inject(method = "add(ILnet/minecraft/world/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    private void onAddItem(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (!stack.is(AVPItemTags.AMMO_ITEMS)) {
            return;
        }

        for (ItemStack inventoryItemStack : items) {
            if (!inventoryItemStack.is(AVPBlockItems.AMMO_CHEST.get()))
                continue;

            insertItemIntoAmmoChest(inventoryItemStack, stack);

            if (stack.isEmpty())
                cir.setReturnValue(true);
        }
    }

    @Inject(method = "setItem", at = @At("HEAD"), cancellable = true)
    private void onSetItem(int slot, ItemStack stack, CallbackInfo ci) {
        if (!stack.is(AVPItemTags.AMMO_ITEMS))
            return;

        for (ItemStack inventoryItemStack : items) {
            if (!inventoryItemStack.is(AVPBlockItems.AMMO_CHEST.get()))
                continue;

            insertItemIntoAmmoChest(inventoryItemStack, stack);

            if (stack.isEmpty())
                ci.cancel();
        }
    }

    @Unique
    private void insertItemIntoAmmoChest(ItemStack ammoChestItemStack, ItemStack toAddItemStack) {
        var oldAmmoChestItems = ammoChestItemStack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).nonEmptyItems();
        NonNullList<ItemStack> newAmmoChestItems = NonNullList.create();

        int remainingToAdd = toAddItemStack.getCount();

        if (AVPPredicates.IS_IMMORTAL.test(player)) {
            return;
        }

        for (var oldAmmoChestItem : oldAmmoChestItems) {
            if (oldAmmoChestItem.isStackable() && ItemStack.isSameItemSameComponents(oldAmmoChestItem, toAddItemStack)) {
                var remainingToStack = oldAmmoChestItem.getMaxStackSize() - oldAmmoChestItem.getCount();
                if (remainingToStack > 0 && remainingToAdd > 0) {
                    var toAdd = Math.min(remainingToStack, remainingToAdd);
                    oldAmmoChestItem.grow(toAdd);
                    remainingToAdd -= toAdd;
                    toAddItemStack.shrink(toAdd);
                }
            }
            newAmmoChestItems.add(oldAmmoChestItem);
        }

        if (remainingToAdd > 0 && newAmmoChestItems.size() < 27) {
            var copyToAdd = toAddItemStack.copyAndClear();

            if (newAmmoChestItems.add(copyToAdd) && player instanceof ServerPlayer serverPlayer)
                CriteriaTriggers.INVENTORY_CHANGED.trigger(serverPlayer, serverPlayer.getInventory(), copyToAdd);
        }

        ammoChestItemStack.applyComponents(
            DataComponentPatch.builder()
                .set(DataComponents.CONTAINER, ItemContainerContents.fromItems(newAmmoChestItems))
                .build()
        );
    }
}
