package org.avp.api.item.weapon.reload;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.avp.api.item.weapon.WeaponItemStack;

@FunctionalInterface
public interface ReloadBehavior {

    String BLOCK_ENTITY_TAG_ID = "BlockEntityTag";

    ReloadBehavior NO_OP = (l, p, w) -> {};

    ReloadBehavior LOAD_FROM_INVENTORY = new LoadFromInventoryReloadBehavior();

    ReloadBehavior LOAD_INTO_WEAPON = new LoadIntoWeaponReloadBehavior();

    void tryReload(ServerLevel serverLevel, ServerPlayer serverPlayer, WeaponItemStack weaponItemStack);
}
