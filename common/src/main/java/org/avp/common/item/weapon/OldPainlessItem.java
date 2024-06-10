package org.avp.common.item.weapon;

import org.avp.common.item.AbstractAVPWeaponItem;
import org.avp.common.item.weapon.data.OldPainlessData;

public class OldPainlessItem extends AbstractAVPWeaponItem {
    public OldPainlessItem(Properties properties) {
        super(properties, OldPainlessData.INSTANCE);
    }
}
