package org.avp.common.network;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.Objects;

import org.avp.api.item.weapon.WeaponItemTagHelper;
import org.avp.common.item.AbstractAVPWeaponItem;
import org.avp.common.network.payload.ServerboundWeaponReloadRequestPayload;
import org.avp.common.network.payload.ServerboundWeaponSwapAmmunitionTypeRequestPayload;
import org.avp.common.network.payload.ServerboundWeaponSwapFireModeRequestPayload;

public class AVPServerListener {

    public static void handleWeaponReloadRequest(
        ServerLevel serverLevel,
        ServerboundWeaponReloadRequestPayload weaponReloadRequestPayload
    ) {
        var playerUUID = weaponReloadRequestPayload.playerUUID();
        var player = (ServerPlayer) serverLevel.getPlayerByUUID(playerUUID);

        if (player == null) {
            return;
        }

        var usedItemHand = player.getUsedItemHand();
        var itemStack = player.getItemInHand(usedItemHand);
        var item = itemStack.getItem();

        // Do not reload if weapon is busy.
        if (player.getCooldowns().isOnCooldown(item)) {
            return;
        }

        if (!(item instanceof AbstractAVPWeaponItem abstractAVPWeaponItem)) {
            return;
        }

        var weaponItemData = abstractAVPWeaponItem.getWeaponItemData();
        weaponItemData.getReloadStrategy().tryReload(serverLevel, player, itemStack, weaponItemData);
    }

    public static void handleWeaponSwapFireModeRequest(
        ServerLevel serverLevel,
        ServerboundWeaponSwapFireModeRequestPayload weaponSwapFireModeRequestPayload
    ) {
        var playerUUID = weaponSwapFireModeRequestPayload.playerUUID();
        var player = (ServerPlayer) serverLevel.getPlayerByUUID(playerUUID);

        if (player == null) {
            return;
        }

        var usedItemHand = player.getUsedItemHand();
        var itemStack = player.getItemInHand(usedItemHand);
        var item = itemStack.getItem();

        if (!(item instanceof AbstractAVPWeaponItem abstractAVPWeaponItem)) {
            return;
        }

        var weaponItemData = abstractAVPWeaponItem.getWeaponItemData();
        var fireModes = weaponItemData.getFireModes();

        if (fireModes.size() < 2) {
            return;
        }

        var currentFireMode = WeaponItemTagHelper.getOrSetFireMode(itemStack, weaponItemData);
        var currentFireModeIndex = weaponItemData.getFireModes().indexOf(currentFireMode);
        var nextFireMode = fireModes.get((currentFireModeIndex + 1) % fireModes.size());
        WeaponItemTagHelper.setFireMode(itemStack, nextFireMode);
        // TODO: Add some sort of visual or audible cue is needed here to indicate the fire mode has changed.
    }

    public static void handleWeaponSwapAmmunitionTypeRequest(
        ServerLevel serverLevel,
        ServerboundWeaponSwapAmmunitionTypeRequestPayload weaponSwapAmmunitionTypeRequestPayload
    ) {
        var playerUUID = weaponSwapAmmunitionTypeRequestPayload.playerUUID();
        var player = (ServerPlayer) serverLevel.getPlayerByUUID(playerUUID);

        if (player == null) {
            return;
        }

        var usedItemHand = player.getUsedItemHand();
        var itemStack = player.getItemInHand(usedItemHand);
        var item = itemStack.getItem();

        if (!(item instanceof AbstractAVPWeaponItem abstractAVPWeaponItem)) {
            return;
        }

        var weaponItemData = abstractAVPWeaponItem.getWeaponItemData();
        var ammunitionSuppliers = weaponItemData.getAmmunitionStrategy().getAmmunitionSuppliers();

        if (ammunitionSuppliers.size() < 2) {
            return;
        }

        var activeAmmunitionType = WeaponItemTagHelper.getOrSetActiveAmmunitionType(itemStack, weaponItemData);

        ammunitionSuppliers.stream()
            .filter(ammunitionSupplier -> {
                var ammunitionItem = ammunitionSupplier.get();
                var resourceLocation = BuiltInRegistries.ITEM.getKey(ammunitionItem);
                return Objects.equals(resourceLocation.toString(), activeAmmunitionType);
            })
            .findFirst()
            .map(ammunitionSuppliers::indexOf)
            .ifPresent(index -> {
                var nextAmmunitionType = ammunitionSuppliers.get((index + 1) % ammunitionSuppliers.size());
                WeaponItemTagHelper.setActiveAmmunitionType(itemStack, nextAmmunitionType.get());
            });
    }

    private AVPServerListener() {
        throw new UnsupportedOperationException();
    }
}
