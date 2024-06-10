package org.avp.common.item.weapon;

import org.avp.common.item.AbstractAVPWeaponItem;
import org.avp.common.item.weapon.data.M83A2SADARData;

public class M83A2SADARItem extends AbstractAVPWeaponItem {
    public M83A2SADARItem(Properties properties) {
        super(properties, M83A2SADARData.INSTANCE);
    }
}
