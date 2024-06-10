package org.avp.common.item.weapon;

import org.avp.common.item.AbstractAVPWeaponItem;
import org.avp.common.item.weapon.data.F90RifleData;

public class F90RifleItem extends AbstractAVPWeaponItem {
    public F90RifleItem(Properties properties) {
        super(properties, F90RifleData.INSTANCE);
    }

}
