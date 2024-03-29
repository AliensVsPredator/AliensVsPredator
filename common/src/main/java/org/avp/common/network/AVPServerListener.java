package org.avp.common.network;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.avp.common.item.AbstractAVPWeaponItem;
import org.avp.common.network.payload.ServerboundWeaponReloadRequestPayload;

/**
 * @author Boston Vanseghi
 */
public class AVPServerListener {

    public static void handleWeaponReloadRequest(
        ServerLevel serverLevel,
        ServerboundWeaponReloadRequestPayload weaponReloadRequestPayload
    ) {
        var playerUUID = weaponReloadRequestPayload.playerUUID();
        var player = (ServerPlayer) serverLevel.getPlayerByUUID(playerUUID);

        if (player != null) {
            var usedItemHand = player.getUsedItemHand();
            var itemStack = player.getItemInHand(usedItemHand);
            var item = itemStack.getItem();

            if (item instanceof AbstractAVPWeaponItem abstractAVPWeaponItem) {
                var weaponItemData = abstractAVPWeaponItem.getWeaponItemData();
                weaponItemData.getReloadStrategy().tryReload(serverLevel, player, itemStack, weaponItemData);
            }
        }
    }

    private AVPServerListener() {
        throw new UnsupportedOperationException();
    }
}
