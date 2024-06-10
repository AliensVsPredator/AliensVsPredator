package org.avp.common.item.weapon;

import org.avp.common.item.AbstractAVPWeaponItem;
import org.avp.common.item.weapon.data.M4CarbineData;

public class M4CarbineItem extends AbstractAVPWeaponItem {
    public M4CarbineItem(Properties properties) {
        super(properties, M4CarbineData.INSTANCE);
    }

}
