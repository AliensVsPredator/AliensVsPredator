package org.avp.api.item.weapon.reload;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import org.avp.api.item.weapon.WeaponItemData;

@FunctionalInterface
public interface ReloadBehavior {

    String BLOCK_ENTITY_TAG_ID = "BlockEntityTag";

    ReloadBehavior NO_OP = (l, p, i, w) -> {};

    ReloadBehavior LOAD_INTO_WEAPON = new LoadIntoWeaponReloadBehavior();

    void tryReload(ServerLevel serverLevel, ServerPlayer serverPlayer, ItemStack itemStack, WeaponItemData weaponItemData);
}
