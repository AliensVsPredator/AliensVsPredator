package com.alien.common.util;

import com.alien.common.model.resin.ResinProducer;
import net.minecraft.world.entity.Entity;

import java.util.function.Consumer;

public class XenomorphGrowthUtil {

    public static final Consumer<Entity> GROW_UP_CALLBACK = (newForm) -> {
        if (newForm instanceof ResinProducer resinProducer) {
            var resinManager = resinProducer.getResinManager();
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
