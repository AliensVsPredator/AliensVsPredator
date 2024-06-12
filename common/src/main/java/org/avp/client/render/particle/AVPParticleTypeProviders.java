package org.avp.client.render.particle;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

import java.util.function.Supplier;

import org.avp.api.client.ParticleProviderData;
import org.avp.api.common.registry.holder.BLHolder;
import org.avp.api.common.registry.AVPDeferredRegistry;
import org.avp.api.service.Services;

public class AVPParticleTypeProviders extends AVPDeferredRegistry<ParticleProviderData<ParticleOptions>> {

    public static final AVPParticleTypeProviders INSTANCE = new AVPParticleTypeProviders();

    @Override
    protected BLHolder<ParticleProviderData<ParticleOptions>> createHolder(
        String registryName,
        Supplier<ParticleProviderData<ParticleOptions>> supplier
    ) {
        var holder = Services.PARTICLE_PROVIDER_SERVICE.createHolder(registryName, supplier);
        entries.put(registryName, holder);
        return holder;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void register() {
        createHolder(
            "acid_provider",
            () -> new ParticleProviderData<>(
                AVPParticleTypeRegistry.INSTANCE.acid,
                AcidParticleProvider::new
            )
        );

        getValues().forEach(providerDataHolder -> {
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
