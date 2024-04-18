package org.avp.client.render.particle;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

import java.util.function.Supplier;

import org.avp.api.Holder;
import org.avp.common.registry.AVPDeferredRegistry;
import org.avp.common.service.Services;

public class AVPParticleTypeProviders extends AVPDeferredRegistry<ParticleProviderData<ParticleOptions>> {

    public static final AVPParticleTypeProviders INSTANCE = new AVPParticleTypeProviders();

    @Override
    protected Holder<ParticleProviderData<ParticleOptions>> createHolder(
        String registryName,
        Supplier<ParticleProviderData<ParticleOptions>> supplier
    ) {
        var holder = Services.PARTICLE_PROVIDER_SERVICE.createHolder(registryName, supplier);
        entries.add(holder);
        return holder;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void register() {
        createHolder(
            "acid_provider",
            () -> new ParticleProviderData<>(
                AVPParticleTypes.INSTANCE.acid,
                AcidParticleProvider::new
            )
        );

        entries.forEach(providerDataHolder -> {
            var providerData = providerDataHolder.get();
            var particleTypeHolder = providerDataHolder.get().particleTypeHolder();
            var factory = providerData.providerFactory();
            Services.PARTICLE_PROVIDER_SERVICE.register(
                () -> (ParticleType<ParticleOptions>) particleTypeHolder.get(),
                factory
            );
        });
    }
}
