package com.blib.internal.service;

import com.blib.BLibHolder;
import com.blib.common.gameplay.model.spawning.BLibEntitySpawnData;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

import java.util.function.Supplier;

public interface BLibRegistryService {

    <T> Holder<T> register(BLibHolder<T> holder, Supplier<? extends T> valueFactory);

    void registerEntityAttributes(
        BLibHolder<? extends EntityType<? extends LivingEntity>> holder,
        Supplier<AttributeSupplier.Builder> attributeSupplierBuilderSupplier
    );

    <T extends Mob> void registerEntitySpawnData(BLibEntitySpawnData<T> spawnData);
}
