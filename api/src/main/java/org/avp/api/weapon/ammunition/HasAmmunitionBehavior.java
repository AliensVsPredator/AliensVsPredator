package org.avp.api.weapon.ammunition;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.avp.api.weapon.WeaponItemStack;

@FunctionalInterface
public interface HasAmmunitionBehavior {

    HasAmmunitionBehavior NO_OP = (s, p, w) -> true;

    HasAmmunitionBehavior LOADED = (s, p, w) -> w.getAmmunition() > 0;

    HasAmmunitionBehavior INVENTORY = new InventoryHasAmmunitionBehavior();

    boolean hasAmmunition(ServerLevel serverLevel, ServerPlayer serverPlayer, WeaponItemStack weaponItemStack);
}
