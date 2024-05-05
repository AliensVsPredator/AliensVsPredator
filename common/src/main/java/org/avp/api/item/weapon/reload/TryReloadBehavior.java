package org.avp.api.item.weapon.reload;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.avp.api.item.weapon.WeaponItemData;
import org.avp.api.item.weapon.WeaponItemTagHelper;
import org.avp.common.util.AVPPredicates;
import org.avp.server.ServerScheduler;

import java.time.Duration;
import java.util.Objects;

@FunctionalInterface
public interface TryReloadBehavior {

    String BLOCK_ENTITY_TAG_ID = "BlockEntityTag";

    TryReloadBehavior NO_OP = (l, p, i, w) -> {};

    TryReloadBehavior STANDARD = (level, player, itemStack, weaponItemData) -> {
        if (WeaponItemTagHelper.hasMaxAmmunition(itemStack, weaponItemData)) {
            return;
        }

        var ammunitionStrategy = weaponItemData.getAmmunitionStrategy();
        var reloadStrategy = weaponItemData.getReloadStrategy();
        var reloadTimeInTicks = reloadStrategy.getReloadTimeInTicks();

        var activeAmmunitionId = WeaponItemTagHelper.getOrSetActiveAmmunitionType(itemStack, weaponItemData);
        var ammunitionSupplier = ammunitionStrategy.getAmmunitionSupplierByKeyOrFirst(activeAmmunitionId);
        var ammunition = ammunitionSupplier.get();
        var ammunitionInInventory = countAmmunitionIncludingShulkerBoxes(player, ammunition);
        var ammunitionInWeapon = WeaponItemTagHelper.getAmmunition(itemStack, weaponItemData);
        var ammunitionMissing = weaponItemData.getAmmunitionStrategy().getMaxAmmunition() - ammunitionInWeapon;
        var ammunitionCountToRestore = AVPPredicates.IS_IMMORTAL.test(player)
            ? weaponItemData.getAmmunitionStrategy().getMaxAmmunition()
            : Math.min(ammunitionInInventory, ammunitionMissing);

        if (!AVPPredicates.IS_IMMORTAL.test(player)) {
            if (ammunitionCountToRestore <= 0) {
                // TODO: Play "click" sound or reload fail sound here.
                return;
            }

            consumeAmmunitionFromPlayerInventory(player, ammunitionMissing, ammunition);
        }

        WeaponItemTagHelper.setActiveAmmunition(itemStack, weaponItemData, ammunitionInWeapon + ammunitionCountToRestore);
        player.getCooldowns().addCooldown(itemStack.getItem(), reloadTimeInTicks);

        var reloadStartSound = reloadStrategy.getReloadStartSound().get();
        level.playSound(null, player.blockPosition(), reloadStartSound, SoundSource.PLAYERS);

        reloadStrategy
            .getReloadFinishSound()
            .ifPresent(
                reloadFinishSound -> ServerScheduler.schedule(
                    () -> {
                        var interactionHand = player.getUsedItemHand();
                        var itemInHand = player.getItemInHand(interactionHand);

                        if (Objects.equals(itemStack, itemInHand)) {
                            level.playSound(null, player.blockPosition(), reloadFinishSound.get(), SoundSource.PLAYERS);
                        }
                    },
                    Duration.ofMillis(reloadTimeInTicks * 50L)
                )
            );
    };

    private static int countAmmunitionIncludingShulkerBoxes(ServerPlayer player, Item ammunition) {
        var rawInventoryItemCount = 0;
        for (var itemStack : player.getInventory().items) {
            if (itemStack.is(ammunition)) {
                rawInventoryItemCount += itemStack.getCount();
            }

            if (AVPPredicates.IS_ITEM_SHULKER_BOX.test(itemStack.getItem())) {
                var itemStackTag = itemStack.getTag();

                if (itemStackTag == null) continue;

                var blockEntityTag = itemStackTag.getCompound(BLOCK_ENTITY_TAG_ID);
                var items = NonNullList.withSize(27, ItemStack.EMPTY);
                ContainerHelper.loadAllItems(blockEntityTag, items);

                for (var shulkerBoxItemStack: items) {
                    if (shulkerBoxItemStack.is(ammunition)) {
                        rawInventoryItemCount += shulkerBoxItemStack.getCount();
                    }
                }
            }
        }

        return rawInventoryItemCount;
    }

    private static void consumeAmmunitionFromPlayerInventory(ServerPlayer player, int ammunitionCountToRestore, Item ammunition) {
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
            } else if (AVPPredicates.IS_ITEM_SHULKER_BOX.test(itemStack.getItem())) {
                var itemStackTag = itemStack.getTag();

                if (itemStackTag == null) continue;

                counter = consumeAmmunitionFromShulkerBox(itemStack, itemStackTag, counter);
            }
        }

        player.getInventory().setChanged();
    }

    private static int consumeAmmunitionFromShulkerBox(ItemStack itemStack, CompoundTag itemStackTag, int counter) {
        var blockEntityTag = itemStackTag.getCompound(BLOCK_ENTITY_TAG_ID);
        var shulkerBoxItemStacks = NonNullList.withSize(27, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(blockEntityTag, shulkerBoxItemStacks);

        for (var shulkerBoxItemStack: shulkerBoxItemStacks) {
            // If the stack item is the same type as the ammunition item, consume part or all of the stack.
            var count = shulkerBoxItemStack.getCount();
            var amountToConsume = Math.min(count, counter);
            shulkerBoxItemStack.setCount(count - amountToConsume);
            counter -= amountToConsume;
        }

        // Save the modified shulker box item stacks back to the shulker box block entity tag.
        ContainerHelper.saveAllItems(blockEntityTag, shulkerBoxItemStacks, false);
        // Save the block entity tag back to the item stack tag.
        itemStackTag.put(BLOCK_ENTITY_TAG_ID, blockEntityTag);
        // Set the item stack tag back on the item stack.
        itemStack.setTag(itemStackTag);
        return counter;
    }

    void tryReload(ServerLevel serverLevel, ServerPlayer serverPlayer, ItemStack itemStack, WeaponItemData weaponItemData);
}
