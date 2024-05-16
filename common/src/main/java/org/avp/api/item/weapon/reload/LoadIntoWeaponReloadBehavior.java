package org.avp.api.item.weapon.reload;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import org.avp.api.item.weapon.WeaponItemData;
import org.avp.api.item.weapon.WeaponItemTagHelper;
import org.avp.api.item.weapon.ammunition.ConsumeAmmunitionAction;
import org.avp.api.item.weapon.ammunition.CountAmmunitionAction;
import org.avp.common.util.AVPPredicates;

public class LoadIntoWeaponReloadBehavior implements ReloadBehavior {

    @Override
    public void tryReload(ServerLevel serverLevel, ServerPlayer serverPlayer, ItemStack itemStack, WeaponItemData weaponItemData) {
        // If the weapon already has max ammunition, abort.
        if (WeaponItemTagHelper.hasMaxAmmunition(itemStack, weaponItemData)) {
            return;
        }

        // Compute the ammunition item that will be consumed.
        var ammunitionStrategy = weaponItemData.getAmmunitionStrategy();
        var activeAmmunitionId = WeaponItemTagHelper.getOrSetActiveAmmunitionType(itemStack, weaponItemData);
        var ammunitionSupplier = ammunitionStrategy.getAmmunitionSupplierByKeyOrFirst(activeAmmunitionId);
        var ammunitionItem = ammunitionSupplier.get();
        // Compute how much of that ammunition item the player has in their inventory.
        var ammunitionInInventory = CountAmmunitionAction.inPlayerInventoryIncludingShulkerBoxes(serverPlayer, ammunitionItem);
        // Compute how much ammunition is loaded into the weapon.
        var ammunitionInWeapon = WeaponItemTagHelper.getAmmunition(itemStack, weaponItemData);
        // How much ammunition is needed until the weapon is full again.
        var ammunitionMissing = weaponItemData.getAmmunitionStrategy().getMaxAmmunition() - ammunitionInWeapon;
        var isPlayerImmortal = AVPPredicates.IS_IMMORTAL.test(serverPlayer);
        var ammunitionCountToRestore = isPlayerImmortal
            ? weaponItemData.getAmmunitionStrategy().getMaxAmmunition()
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
        WeaponItemTagHelper.setActiveAmmunition(itemStack, weaponItemData, ammunitionInWeapon + ammunitionCountToRestore);
        // Perform reload logic (weapon cooldown, sound effects, etc.)
        ReloadAction.perform(serverLevel, serverPlayer, itemStack, weaponItemData);
    }
}
