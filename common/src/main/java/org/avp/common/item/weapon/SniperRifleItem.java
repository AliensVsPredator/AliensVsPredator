package org.avp.common.item.weapon;

import org.avp.common.item.AbstractAVPWeaponItem;
import org.avp.common.item.weapon.data.SniperRifleData;

public class SniperRifleItem extends AbstractAVPWeaponItem {
    public SniperRifleItem(Properties properties) {
        super(properties, SniperRifleData.INSTANCE);
    }
}
