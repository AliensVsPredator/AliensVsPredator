package com.human.common.gameplay.item.gun.pipeline.step;

import com.human.common.gameplay.item.gun.pipeline.GunShootContext;
import com.human.common.gameplay.item.gun.pipeline.GunShootResult;

@FunctionalInterface
public interface GunShootStep {

    GunShootResult apply(GunShootContext context);
}
