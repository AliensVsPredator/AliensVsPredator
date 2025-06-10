package com.human.common.gameplay.item.gun.attack;

import com.human.common.gameplay.item.gun.pipeline.GunShootResult;

@FunctionalInterface
public interface GunAttackAction {

    GunShootResult shoot(GunAttackConfig gunAttackConfig);
}
