package org.avp.common.item.weapon;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import org.avp.client.render.item.M83A2SADARItemRenderer;
import org.avp.common.item.AVPWeaponItemData;
import org.avp.common.item.AbstractAVPWeaponItem;

public class M83A2SADARItem extends AbstractAVPWeaponItem {

    public M83A2SADARItem(Properties properties) {
        super(properties, AVPWeaponItemData.SHOTGUN);
    }

    @Override
    protected BlockEntityWithoutLevelRenderer createRenderer() {
        return new M83A2SADARItemRenderer();
    }
}
