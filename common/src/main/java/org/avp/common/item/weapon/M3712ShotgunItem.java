package org.avp.common.item.weapon;

import org.avp.common.item.AbstractAVPWeaponItem;
import org.avp.common.item.weapon.data.M3712ShotgunData;

public class M3712ShotgunItem extends AbstractAVPWeaponItem {
    public M3712ShotgunItem(Properties properties) {
        super(properties, M3712ShotgunData.INSTANCE);
    }
}
