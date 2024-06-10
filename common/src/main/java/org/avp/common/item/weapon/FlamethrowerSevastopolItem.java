package org.avp.common.item.weapon;

import org.avp.common.item.AbstractAVPWeaponItem;
import org.avp.common.item.weapon.data.FlamethrowerSevastopolData;

public class FlamethrowerSevastopolItem extends AbstractAVPWeaponItem {
    public FlamethrowerSevastopolItem(Properties properties) {
        super(properties, FlamethrowerSevastopolData.INSTANCE);
    }
}
