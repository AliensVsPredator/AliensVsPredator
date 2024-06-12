package org.avp.api.weapon.ammunition;

import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

import org.avp.api.util.BLPredicates;
import org.avp.api.weapon.WeaponItemStack;
import org.avp.api.weapon.reload.ReloadBehavior;

public class InventoryHasAmmunitionBehavior implements HasAmmunitionBehavior {

    @Override
    public boolean hasAmmunition(
        ServerLevel serverLevel,
        ServerPlayer serverPlayer,
        WeaponItemStack weaponItemStack
    ) {
        var activeAmmunitionType = weaponItemStack.getOrSetActiveAmmunitionType();
        var fireModeData = weaponItemStack.getOrSetFireMode();
        var ammunitionSupplierOptional = fireModeData.ammunitionData().ammunitionSuppliers()
            .stream()
            .filter(ammunitionSupplier -> {
                var ammunitionItem = ammunitionSupplier.get();
                var resourceLocation = BuiltInRegistries.ITEM.getKey(ammunitionItem);
                return Objects.equals(resourceLocation.toString(), activeAmmunitionType);
            })
            .findFirst();

        if (ammunitionSupplierOptional.isEmpty()) {
            return false;
        }

        var ammunitionItem = ammunitionSupplierOptional.get().get();

        for (var itemStack : serverPlayer.getInventory().items) {
            if (itemStack.is(ammunitionItem) && !itemStack.isEmpty()) {
                return true;
            }

            if (BLPredicates.IS_ITEM_SHULKER_BOX.test(itemStack.getItem())) {
                var itemStackTag = itemStack.getTag();

                if (itemStackTag == null)
                    continue;

                var blockEntityTag = itemStackTag.getCompound(ReloadBehavior.BLOCK_ENTITY_TAG_ID);
                var items = NonNullList.withSize(27, ItemStack.EMPTY);
                ContainerHelper.loadAllItems(blockEntityTag, items);

                for (var shulkerBoxItemStack : items) {
                    if (shulkerBoxItemStack.is(ammunitionItem) && !shulkerBoxItemStack.isEmpty()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
