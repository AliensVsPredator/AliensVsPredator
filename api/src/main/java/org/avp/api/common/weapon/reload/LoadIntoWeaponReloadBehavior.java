package org.avp.api.common.weapon.reload;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import org.avp.api.common.weapon.WeaponItemStack;
import org.avp.api.common.weapon.ammunition.ConsumeAmmunitionAction;
import org.avp.api.common.weapon.ammunition.CountAmmunitionAction;
import org.avp.api.util.BLPredicates;

public class LoadIntoWeaponReloadBehavior implements ReloadBehavior {

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
        // Compute how much ammunition is loaded into the weapon.
        var ammunitionInWeapon = weaponItemStack.getAmmunition();
        // How much ammunition is needed until the weapon is full again.
        var ammunitionMissing = ammunitionData.maxAmmunition() - ammunitionInWeapon;
        var isPlayerImmortal = BLPredicates.IS_IMMORTAL.test(serverPlayer);
        var ammunitionCountToRestore = isPlayerImmortal
            ? ammunitionData.maxAmmunition()
            : Math.min(ammunitionInInventory, ammunitionMissing);

        // If the player is mortal, try and consume ammunition from their inventory.
        if (!isPlayerImmortal) {
            // If there is no ammunition to consume, abort.
            if (ammunitionCountToRestore <= 0) {
                // TODO: Play "click" sound or reload fail sound here.
                return;
            }

            // Consume ammunition.
            ConsumeAmmunitionAction.fromPlayerInventory(serverPlayer, ammunitionMissing, ammunitionItem);
        }

        // Set ammunition to the given count.
        weaponItemStack.setActiveAmmunition(ammunitionInWeapon + ammunitionCountToRestore);
        // Perform reload logic (weapon cooldown, sound effects, etc.)
        ReloadAction.perform(serverLevel, serverPlayer, weaponItemStack);
    }
}
