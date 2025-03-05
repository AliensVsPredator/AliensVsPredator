package com.avp.common.item.gun.attack;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import com.avp.common.item.gun.FireModeConfig;
import com.avp.common.item.gun.GunConfig;

public record GunAttackConfig(
    GunConfig gunConfig,
    FireModeConfig fireModeConfig,
    LivingEntity shooter,
    ItemStack gunItemStack
) {}
