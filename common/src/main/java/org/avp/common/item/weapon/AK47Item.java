package org.avp.common.item.weapon;

import org.avp.common.item.AbstractAVPWeaponItem;
import org.avp.common.item.weapon.data.AK47Data;

public class AK47Item extends AbstractAVPWeaponItem {
    public AK47Item(Properties properties) {
        super(properties, AK47Data.INSTANCE);
    }
}
