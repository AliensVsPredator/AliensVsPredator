package com.avp.common.particle;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

import com.avp.AVPResources;

public class AVPParticleTypes {

    public static final SimpleParticleType ACID = register("acid");

    public static final SimpleParticleType BLUE_ACID = register("blue_acid");

    public static SimpleParticleType register(String id) {
        var resourceLocation = AVPResources.location(id);
        return Registry.register(BuiltInRegistries.PARTICLE_TYPE, resourceLocation, FabricParticleTypes.simple());
    }

    public static void initialize() {}
}
