package org.avp.api.weapon.reload;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.avp.api.weapon.WeaponItemStack;
import org.avp.api.weapon.ammunition.ConsumeAmmunitionAction;
import org.avp.api.weapon.ammunition.CountAmmunitionAction;
import org.avp.api.util.BLPredicates;

public class LoadFromInventoryReloadBehavior implements ReloadBehavior {

    @Override
    public void tryReload(ServerLevel serverLevel, ServerPlayer serverPlayer, WeaponItemStack weaponItemStack) {
        // If the weapon already has max ammunition, abort.
        if (weaponItemStack.hasMaxAmmunition()) {
            return;
        }

        var fireModeData = weaponItemStack.getOrSetFireMode();
        var ammunitionData = fireModeData.ammunitionData();

        // Compute the ammunition item that will be consumed.
        var activeAmmunitionId = weaponItemStack.getOrSetActiveAmmunitionType();
        var ammunitionSupplier = ammunitionData.getAmmunitionSupplierByKeyOrFirst(activeAmmunitionId);
        var ammunitionItem = ammunitionSupplier.get();
        // Compute how much of that ammunition item the player has in their inventory.
        var ammunitionInInventory = CountAmmunitionAction.inPlayerInventoryIncludingShulkerBoxes(serverPlayer, ammunitionItem);
        var isPlayerImmortal = BLPredicates.IS_IMMORTAL.test(serverPlayer);

        // If the player is mortal, try and consume ammunition from their inventory.
        if (!isPlayerImmortal) {
            // If there is no ammunition to consume, abort.
            if (ammunitionInInventory <= 0) {
                // TODO: Play "click" sound or reload fail sound here.
                return;
            }

            // Consume ammunition.
            ConsumeAmmunitionAction.fromPlayerInventory(serverPlayer, 1, ammunitionItem);
        }

        // Perform reload logic (weapon cooldown, sound effects, etc.)
        ReloadAction.perform(serverLevel, serverPlayer, weaponItemStack);
    }
}
