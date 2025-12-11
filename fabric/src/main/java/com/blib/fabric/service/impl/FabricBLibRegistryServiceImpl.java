package com.blib.fabric.service.impl;

import com.blib.BLibHolder;
import com.blib.BLibMod;
import com.blib.service.BLibRegistryService;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class FabricBLibRegistryServiceImpl implements BLibRegistryService {

    @Override
    public <T> Holder<T> register(BLibHolder<T> holder, Supplier<? extends T> valueFactory) {
        var object = valueFactory.get();
        var resourceLocation = holder.getResourceLocation();
        var registry = holder.getBackingRegistry();

        if (object instanceof PoiType poiType) {
            // We have to do special handling for PoiType registration on the Fabric side, since Fabric wants
            // Poi registrations to go through their "PointOfInterestHelper" type.
            return registerPoiType(resourceLocation, poiType);
        }

        var reference = Registry.registerForHolder(registry, resourceLocation, object);
        @SuppressWarnings("unchecked")
        var registeredHolder = (Holder<T>) reference;
        return registeredHolder;
    }

    @Override
    public void registerEntityAttributes(
        BLibHolder<? extends EntityType<? extends LivingEntity>> holder,
        Supplier<AttributeSupplier.Builder> attributeSupplierBuilderSupplier
    ) {
        FabricDefaultAttributeRegistry.register(holder.get(), attributeSupplierBuilderSupplier.get());
    }

    private <T> @NotNull Holder<T> registerPoiType(ResourceLocation resourceLocation, PoiType poiType) {
        PointOfInterestHelper.register(resourceLocation, poiType.maxTickets(), poiType.validRange(), poiType.matchingStates());
        // Immediately get the holder or throw. This should be safe to do since we registered the PoiType in the last
        // line. This is necessary because PointOfInterestHelper doesn't return back a holder after registration.
        @SuppressWarnings("unchecked")
        var registeredHolder = (Holder<T>) BuiltInRegistries.POINT_OF_INTEREST_TYPE.getHolder(resourceLocation).orElseThrow();
        return registeredHolder;
    }
}
