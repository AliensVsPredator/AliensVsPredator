package org.avp.common.item.weapon;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import org.avp.client.render.item.M4CarbineItemRenderer;
import org.avp.common.item.AVPWeaponItemData;
import org.avp.common.item.AbstractAVPWeaponItem;

public class M4CarbineItem extends AbstractAVPWeaponItem {

    public M4CarbineItem(Properties properties) {
        super(properties, AVPWeaponItemData.M4_CARBINE);
    }

    @Override
    protected BlockEntityWithoutLevelRenderer createRenderer() {
        return new M4CarbineItemRenderer();
    }
}
