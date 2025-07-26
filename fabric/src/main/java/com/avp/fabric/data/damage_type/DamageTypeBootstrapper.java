package com.avp.fabric.data.damage_type;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.damagesource.DamageType;

import com.avp.common.registry.key.AVPDamageTypeKeys;

public class DamageTypeBootstrapper {

    public static void bootstrap(BootstrapContext<DamageType> registry) {
        registry.register(AVPDamageTypeKeys.ACID, new DamageType("acid", 0.1F));
        registry.register(AVPDamageTypeKeys.BULLET, new DamageType("bullet", 0.1F));
        registry.register(AVPDamageTypeKeys.CHESTBURSTING, new DamageType("chestbursting", 0.1F));
        registry.register(AVPDamageTypeKeys.FLAMETHROW, new DamageType("flamethrow", 0.1F));
        registry.register(AVPDamageTypeKeys.RAZOR_WIRE, new DamageType("razor_wire", 0.1F));
        registry.register(AVPDamageTypeKeys.RADIATION, new DamageType("radiation", 0.1F));
        registry.register(AVPDamageTypeKeys.SMOTHERING, new DamageType("smothering", 0.1F));
    }
}
