package com.avp.common.entity.ai.sensor.inventory;

import net.minecraft.world.entity.LivingEntity;

import com.avp.common.entity.ai.GOAPConstants;
import com.avp.common.entity.ai.util.ItemType;
import com.avp.goap.GOAPSensor;
import com.avp.goap.state.GOAPMutableWorldState;

public class MainHandItemTypeSensor<T extends LivingEntity> implements GOAPSensor<T> {

    @Override
    public void sense(T context, GOAPMutableWorldState worldState) {
        worldState.set(GOAPConstants.MAIN_HAND_ITEM_TYPE, ItemType.getForItem(context.getMainHandItem()));
    }
}
