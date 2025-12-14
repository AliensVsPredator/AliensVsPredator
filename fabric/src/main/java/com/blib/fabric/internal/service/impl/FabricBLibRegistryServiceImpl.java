package com.blib.fabric.internal.service.impl;

import com.blib.BLibHolder;
import com.blib.BLibMod;
import com.blib.common.gameplay.model.spawning.BLibEntitySpawnData;
import com.blib.internal.service.BLibRegistryService;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@ApiStatus.Internal
public class FabricBLibRegistryServiceImpl implements BLibRegistryService {

    private final Map<BLibMod, BLibFabricModContainer> modToContainerMap;

    public FabricBLibRegistryServiceImpl() {
        this.modToContainerMap = new ConcurrentHashMap<>();
    }

    @Override
    public <T> Holder<T> register(BLibHolder<T> holder, Supplier<? extends T> valueFactory) {
        getModContainer(holder)
            .deferRegistration(holder, valueFactory);

        return holder;
    }

    @Override
    public void registerCompostable(
        BLibHolder<? extends ItemLike> holder,
        float chance,
        boolean villagersCanCompost,
        boolean replace
    ) {
        getModContainer(holder)
            .deferCompostableRegistration(holder, chance);
    }

    public void registerDecoratedPotPattern(String path, BLibHolder<? extends Item> holder) {
        getModContainer(holder)
            .deferDecoratedPotPatternRegistration(path, holder);
    }

    @Override
    public void registerEntityAttributes(
        BLibHolder<? extends EntityType<? extends LivingEntity>> holder,
        Supplier<AttributeSupplier.Builder> attributeSupplierBuilderSupplier
    ) {
        getModContainer(holder)
            .deferEntityAttributesRegistration(holder, attributeSupplierBuilderSupplier);
    }

    @Override
    public <T extends Mob> void registerEntitySpawnData(BLibEntitySpawnData<T> spawnData) {
        getModContainer(spawnData.getEntityTypeHolder())
            .deferEntitySpawnDataRegistration(spawnData);
    }

    /* package-private */ void finalize(BLibMod mod) {
        getModContainer(mod)
            .runDeferredRegistrations();
    }

    private BLibFabricModContainer getModContainer(BLibHolder<?> holder) {
        return getModContainer(holder.getRegistry().getMod());
    }

    private BLibFabricModContainer getModContainer(BLibMod mod) {
        return modToContainerMap.computeIfAbsent(mod, $ -> new BLibFabricModContainer(mod));
    }
}
