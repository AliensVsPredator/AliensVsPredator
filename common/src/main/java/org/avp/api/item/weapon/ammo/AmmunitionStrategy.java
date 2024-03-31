package org.avp.api.item.weapon.ammo;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.avp.api.item.weapon.WeaponItemData;

@FunctionalInterface
public interface AmmunitionStrategy {

    boolean hasAmmunition(ServerLevel serverLevel, ServerPlayer serverPlayer, ItemStack itemStack, WeaponItemData weaponItemData);
}
