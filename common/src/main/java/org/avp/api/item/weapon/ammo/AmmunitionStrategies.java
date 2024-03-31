package org.avp.api.item.weapon.ammo;

import net.minecraft.world.item.Items;
import org.avp.api.item.weapon.WeaponItemTagHelper;

import java.util.Set;

/**
 * @author Boston Vanseghi
 */
public class AmmunitionStrategies {

    public static final AmmunitionStrategy NO_OP = (l, p, i, w) -> true;

    public static final AmmunitionStrategy LOADED = (level, player, itemStack, weaponItemData) -> WeaponItemTagHelper.getAmmunition(itemStack) > 0;

    // TODO: Change to support specifying ammunition items.
    public static final AmmunitionStrategy INVENTORY = (l, p, i, w) -> p.getInventory().hasAnyOf(Set.of(Items.SNOWBALL));

    private AmmunitionStrategies() {
        throw new UnsupportedOperationException();
    }
}
