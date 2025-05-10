package com.avp.common.item.gun.attack;

import com.avp.common.item.gun.pipeline.GunShootResult;

@FunctionalInterface
public interface GunAttackAction {

    GunShootResult shoot(GunAttackConfig gunAttackConfig);
}
