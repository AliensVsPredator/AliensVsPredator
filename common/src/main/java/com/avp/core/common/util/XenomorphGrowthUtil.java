package com.avp.core.common.util;

import net.minecraft.world.entity.LivingEntity;

import java.util.function.Consumer;

import com.avp.core.common.util.resin.ResinProducer;

public class XenomorphGrowthUtil {

    public static final Consumer<LivingEntity> GROW_UP_CALLBACK = (newForm) -> {
        if (newForm instanceof ResinProducer resinProducer) {
            var resinManager = resinProducer.resinManager();
            var baseResinData = resinManager.baseResinData();
            var resinData = resinManager.resinData();
            // Growing up consumes all the resin the xenomorph has.
            resinData.setResin(0);
            // Set maximum resin to the base resin data's maximum.
            // We need to do this since the xenomorph carries on the resin max from its previous form.
            // The base resin data comes from the new form.
            resinData.setResinMax(baseResinData.resinMax());
        }
    };
}
