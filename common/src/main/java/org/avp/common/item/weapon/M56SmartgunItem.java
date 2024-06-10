package org.avp.common.item.weapon;

import org.avp.common.item.AbstractAVPWeaponItem;
import org.avp.common.item.weapon.data.M56SmartgunData;

public class M56SmartgunItem extends AbstractAVPWeaponItem {
    public M56SmartgunItem(Properties properties) {
        super(properties, M56SmartgunData.INSTANCE);
    }

}
