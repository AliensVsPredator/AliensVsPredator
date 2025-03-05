package com.avp.data.damage_type;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.damagesource.DamageType;

import com.avp.common.damage.AVPDamageTypes;

public class DamageTypeBootstrapper {

    public static void bootstrap(BootstrapContext<DamageType> registry) {
        registry.register(AVPDamageTypes.ACID, new DamageType("acid", 0.1F));
        registry.register(AVPDamageTypes.BULLET, new DamageType("bullet", 0.1F));
        registry.register(AVPDamageTypes.FLAMETHROW, new DamageType("flamethrow", 0.1F));
        registry.register(AVPDamageTypes.RAZOR_WIRE, new DamageType("razor_wire", 0.1F));
    }
}
