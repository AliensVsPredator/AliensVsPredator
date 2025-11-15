package com.avp.neoforge.service;

import com.human.common.gameplay.item.gun.GunConfig;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

import com.avp.common.registry.init.AVPMobCategoryData;
import com.avp.neoforge.common.item.NeoForgeGunItem;
import com.avp.neoforge.common.item.NeoForgeOldPainlessItem;
import com.avp.service.BridgeService;

public class NeoForgeBridgeService implements BridgeService {

    public static final EnumProxy<MobCategory> ALIEN_MOB_CATEGORY_ENUM_PROXY = fromMobCategoryData(AVPMobCategoryData.ALIEN);

    public static final EnumProxy<MobCategory> OVOMORPH_MOB_CATEGORY_ENUM_PROXY = fromMobCategoryData(AVPMobCategoryData.OVOMORPH);

    public static final EnumProxy<MobCategory> PREDATOR_MOB_CATEGORY_ENUM_PROXY = fromMobCategoryData(AVPMobCategoryData.PREDATOR);

    private static @NotNull EnumProxy<MobCategory> fromMobCategoryData(AVPMobCategoryData.Data data) {
        return new EnumProxy<>(
            MobCategory.class,
            data.name(),
            data.max(),
            data.isFriendly(),
            data.isPersistent(),
            data.despawnDistance()
        );
    }

    @Override
    public Supplier<Item> createGunSupplier(GunConfig gunConfig) {
        return () -> new NeoForgeGunItem(gunConfig);
    }

    @Override
    public Supplier<Item> createOldPainlessSupplier() {
        return NeoForgeOldPainlessItem::new;
    }

    @Override
    public <E extends Mob> Supplier<SpawnEggItem> createSpawnEggSupplier(
        Supplier<EntityType<E>> entityType,
        int primaryEggColour,
        int secondaryEggColour,
        Item.Properties itemProperties
    ) {
        return () -> new DeferredSpawnEggItem(entityType, primaryEggColour, secondaryEggColour, itemProperties);
    }

    @Override
    public MobCategory getAlienMobCategory() {
        return ALIEN_MOB_CATEGORY_ENUM_PROXY.getValue();
    }

    @Override
    public MobCategory getOvomorphMobCategory() {
        return OVOMORPH_MOB_CATEGORY_ENUM_PROXY.getValue();
    }

    @Override
    public MobCategory getPredatorMobCategory() {
        return PREDATOR_MOB_CATEGORY_ENUM_PROXY.getValue();
    }
}
