package com.blib.internal.service;

import com.blib.BLibHolder;
import com.blib.BLibMod;
import com.blib.common.gameplay.model.spawning.BLibEntitySpawnData;
import net.minecraft.core.Holder;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

@ApiStatus.Internal
public interface BLibRegistryService {

    <T> Holder<T> register(BLibHolder<T> holder, Supplier<? extends T> valueFactory);

    void registerCompostable(BLibHolder<? extends ItemLike> holder, float chance, boolean villagersCanCompost, boolean replace);

    void registerDecoratedPotPattern(String path, BLibHolder<? extends Item> holder);

    void registerEntityAttributes(
        BLibHolder<? extends EntityType<? extends LivingEntity>> holder,
        Supplier<AttributeSupplier.Builder> attributeSupplierBuilderSupplier
    );

    <T extends Mob> void registerEntitySpawnData(BLibEntitySpawnData<T> spawnData);

    void registerFurnaceFuel(BLibHolder<? extends ItemLike> holder, int burnTimeInTicks);

    void registerReloadListener(BLibMod mod, String path, PreparableReloadListener listener);
}
