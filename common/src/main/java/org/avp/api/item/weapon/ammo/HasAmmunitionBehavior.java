package org.avp.api.item.weapon.ammo;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.avp.api.item.weapon.WeaponItemData;
import org.avp.api.item.weapon.WeaponItemTagHelper;

@FunctionalInterface
public interface HasAmmunitionBehavior {

    HasAmmunitionBehavior NO_OP = (s, p, i, w) -> true;

    HasAmmunitionBehavior LOADED = (s, p, i, w) -> WeaponItemTagHelper.getAmmunition(i) > 0;

    boolean hasAmmunition(ServerLevel serverLevel, ServerPlayer serverPlayer, ItemStack itemStack, WeaponItemData weaponItemData);
}
