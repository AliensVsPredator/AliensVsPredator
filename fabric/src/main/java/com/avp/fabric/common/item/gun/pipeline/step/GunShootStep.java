package com.avp.fabric.common.item.gun.pipeline.step;

import com.avp.fabric.common.item.gun.pipeline.GunShootContext;
import com.avp.fabric.common.item.gun.pipeline.GunShootResult;

@FunctionalInterface
public interface GunShootStep {

    GunShootResult apply(GunShootContext context);
}
