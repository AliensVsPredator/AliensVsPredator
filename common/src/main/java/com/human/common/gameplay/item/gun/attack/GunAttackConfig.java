package com.human.common.gameplay.item.gun.attack;

import com.human.common.gameplay.item.gun.FireModeConfig;
import com.human.common.gameplay.item.gun.GunConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public record GunAttackConfig(
    GunConfig gunConfig,
    FireModeConfig fireModeConfig,
    LivingEntity shooter,
    ItemStack gunItemStack
) {}
