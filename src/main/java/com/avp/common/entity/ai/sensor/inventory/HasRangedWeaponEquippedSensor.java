package com.avp.common.entity.ai.sensor.inventory;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;

import com.avp.common.entity.ai.GOAPConstants;
import com.avp.common.item.GunItem;
import com.avp.goap.GOAPSensor;
import com.avp.goap.state.GOAPMutableWorldState;

public class HasRangedWeaponEquippedSensor<T extends LivingEntity> implements GOAPSensor<T> {

    @Override
    public void sense(T context, GOAPMutableWorldState worldState) {
        var mainhandItem = context.getMainHandItem().getItem();

        worldState.set(
            GOAPConstants.HAS_RANGED_WEAPON_EQUIPPED,
            mainhandItem instanceof BowItem
                || mainhandItem instanceof CrossbowItem
                || mainhandItem instanceof GunItem
        );
    }
}
