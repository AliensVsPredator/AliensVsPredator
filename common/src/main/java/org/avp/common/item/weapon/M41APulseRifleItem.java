package org.avp.common.item.weapon;

import org.avp.common.item.AbstractAVPWeaponItem;
import org.avp.common.item.weapon.data.M41APulseRifleData;

public class M41APulseRifleItem extends AbstractAVPWeaponItem {
    public M41APulseRifleItem(Properties properties) {
        super(properties, M41APulseRifleData.INSTANCE);
    }

}
