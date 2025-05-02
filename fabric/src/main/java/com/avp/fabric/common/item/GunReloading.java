package com.avp.fabric.common.item;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;

import java.time.Duration;
import java.util.Objects;

import com.avp.common.component.AVPDataComponents;
import com.avp.common.item.TempAVPBlockItems;
import com.avp.common.util.AVPPredicates;
import com.avp.common.util.EnchantmentUtil;
import com.avp.fabric.common.item.gun.GunData;
import com.avp.server.ServerScheduler;

public class GunReloading {

    public static void reload(ServerPlayer player) {
        if (player == null) {
            // Player is null, nothing we can do beyond this point.
            return;
        }

        var level = player.level();
        var usedItemHand = player.getUsedItemHand();
        var itemStack = player.getItemInHand(usedItemHand);
        var item = itemStack.getItem();

        if (!(item instanceof GunItem gunItem)) {
            return;
        }

        var gunConfig = gunItem.getGunConfig();
        var maximumAmmunition = gunConfig.maximumAmmunition();

        // TODO: Kinda hacky, find a better way to do this.
        if (gunConfig == GunData.OLD_PAINLESS) {
            return;
        }

        int currentAmmunition = itemStack.getOrDefault(AVPDataComponents.AMMUNITION.get(), 0);

        if (currentAmmunition >= maximumAmmunition) {
            // Gun is already max ammo, no need to continue trying to reload.
            return;
        }

        var ammunitionItemSupplier = gunConfig.ammunitionItemSupplier();

        if (ammunitionItemSupplier == null) {
            return;
        }

        var ammunitionItem = ammunitionItemSupplier.get();
        var reloadAmount = gunConfig.reloadAmount();
        var neededAmmunition = (int) Math.ceil((maximumAmmunition - currentAmmunition) / ((float) reloadAmount));

        var isPlayerImmortal = AVPPredicates.IS_IMMORTAL.test(player);
        // Result is how much we DIDN'T consume.
        var result = isPlayerImmortal
            // If the player is immortal, then assume they can get a full reload.
            ? ItemConsumptionResult.Full.INSTANCE
            // Otherwise, the player needs to use actual ammunition.
            : consumeItemAmountFromInventory(player, ammunitionItem, neededAmmunition);

        var ammunitionToRestore = switch (result) {
            // We successfully consumed all ammunition we needed, so this is just an identity assignment.
            case ItemConsumptionResult.Full full -> neededAmmunition;
            // We failed to consume any amount of ammunition, so we can't restore any ammunition.
            case ItemConsumptionResult.None none -> 0;
            // We failed to consume all the necessary ammunition.
            // The difference here is needed - failedToConsumeCount = successfullyConsumed.
            // Ex. we needed 500, we consumed and got back 230 remaining, 500 - 230 = 270, 270 is what we can restore.
            case ItemConsumptionResult.Partial partial -> neededAmmunition - partial.remainingAmount;
        };

        if (ammunitionToRestore == 0) {
            // There's no ammunition to restore, so why would we continue? Return.
            return;
        }

        // TODO: Shouldn't be here.
        GunItem.reload.sendForItem(player, itemStack);

        var fireModeConfig = gunConfig.getDefaultFireMode();
        var reloadStartSoundEvent = fireModeConfig.reloadStartSoundEvent();

        if (reloadStartSoundEvent != null) {
            level.playSound(null, player.blockPosition(), reloadStartSoundEvent.get(), SoundSource.PLAYERS);
        }

        itemStack.set(
            AVPDataComponents.AMMUNITION.get(),
            Math.min(currentAmmunition + (ammunitionToRestore * reloadAmount), maximumAmmunition)
        );

        if (!isPlayerImmortal) {
            var reloadTimeModifier = EnchantmentUtil.getLevel(level, itemStack, Enchantments.QUICK_CHARGE) * 0.2;
            var reloadTimeInTicks = (int) (gunConfig.reloadTimeInTicks() * (1 - reloadTimeModifier));

            player.getCooldowns().addCooldown(itemStack.getItem(), reloadTimeInTicks);

            ServerScheduler.schedule(() -> {
                var reloadFinishSoundEvent = fireModeConfig.reloadFinishSoundEvent();

                if (reloadFinishSoundEvent != null) {
                    var interactionHand = player.getUsedItemHand();
                    var itemInHand = player.getItemInHand(interactionHand);

                    if (Objects.equals(itemStack, itemInHand)) {
                        level.playSound(null, player.blockPosition(), reloadFinishSoundEvent.get(), SoundSource.PLAYERS);
                    }
                }
            }, Duration.ofMillis(reloadTimeInTicks * 50L));
        }
    }

    public static ItemConsumptionResult consumeItemAmountFromInventory(
        ServerPlayer serverPlayer,
        ItemLike ammunitionItem,
        int amountToConsume
    ) {
        var result = consumeItemAmountFromInventoryNoSync(serverPlayer, ammunitionItem, amountToConsume);

        if (result == ItemConsumptionResult.None.INSTANCE) {
            return result;
        }

        // Result must be partial or full, in either case it has changed and needs to be updated to the client.
        serverPlayer.getInventory().setChanged();
        serverPlayer.inventoryMenu.broadcastChanges();
        return result;
    }

    private static ItemConsumptionResult consumeItemAmountFromInventoryNoSync(
        ServerPlayer serverPlayer,
        ItemLike ammunitionItem,
        int amountToConsume
    ) {
        var playerInventory = serverPlayer.getInventory();
        var remainingAmountToConsume = amountToConsume;

        // We iterate over ammo chests first, since we want to consume from them before the player's bare inventory.
        for (var playerItemStack : playerInventory.items) {
            if (!playerItemStack.is(TempAVPBlockItems.AMMO_CHEST.get())) {
                // Skip non-ammo chests.
                continue;
            }

            // Try and consume some amount from the ammo chest.
            var result = consumeFromAmmoChestItem(playerItemStack, remainingAmountToConsume, ammunitionItem);

            switch (result) {
                case ItemConsumptionResult.Full full -> {
                    // We consumed all items successfully, so we can exit the function.
                    return full;
                }
                case ItemConsumptionResult.Partial partial -> {
                    // We were able to consume some of the items, but not all.
                    remainingAmountToConsume = partial.remainingAmount;
                }
                case ItemConsumptionResult.None none -> {
                    // We weren't able to consume any items and need to continue searching, so this is a NO-OP.
                }
            }
        }

        for (var playerItemStack : playerInventory.items) {
            if (!playerItemStack.is(ammunitionItem.asItem())) {
                // Skip non-ammunition items.
                continue;
            }

            var consumeCount = Math.min(playerItemStack.getCount(), remainingAmountToConsume);
            playerItemStack.shrink(consumeCount);
            remainingAmountToConsume -= consumeCount;

            if (remainingAmountToConsume == 0) {
                return ItemConsumptionResult.Full.INSTANCE;
            }
        }

        if (remainingAmountToConsume == amountToConsume) {
            return ItemConsumptionResult.None.INSTANCE;
        }

        return new ItemConsumptionResult.Partial(remainingAmountToConsume);
    }

    private static ItemConsumptionResult consumeFromAmmoChestItem(ItemStack ammoChestStack, int amountToConsume, ItemLike ammunitionItem) {
        var container = ammoChestStack.get(net.minecraft.core.component.DataComponents.CONTAINER);

        if (container == null || amountToConsume <= 0) {
            // Can't consume or nothing to consume.
            return ItemConsumptionResult.None.INSTANCE;
        }

        var remainingAmountToConsume = amountToConsume;

        for (var itemStack : container.nonEmptyItems()) {
            if (!itemStack.is(ammunitionItem.asItem())) {
                // Skip non-ammunition items.
                continue;
            }

            // Item is our target consumable by this point.
            var consumeCount = Math.min(itemStack.getCount(), remainingAmountToConsume);
            itemStack.shrink(consumeCount);
            remainingAmountToConsume -= consumeCount;

            if (remainingAmountToConsume == 0) {
                // We've consumed all the items we needed to, so return.
                return ItemConsumptionResult.Full.INSTANCE;
            }
        }

        // If we reached this point, that means we didn't fully consume the desired amount.
        return new ItemConsumptionResult.Partial(remainingAmountToConsume);
    }

    public sealed interface ItemConsumptionResult {

        enum None implements ItemConsumptionResult {
            INSTANCE
        }

        enum Full implements ItemConsumptionResult {
            INSTANCE
        }

        record Partial(int remainingAmount) implements ItemConsumptionResult {}
    }
}
