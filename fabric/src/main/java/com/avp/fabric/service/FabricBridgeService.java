package com.avp.fabric.service;

import com.human.common.gameplay.item.gun.GunConfig;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

import java.util.function.Supplier;

import com.avp.fabric.common.item.FabricGunItem;
import com.avp.fabric.common.item.FabricOldPainlessItem;
import com.avp.service.BridgeService;

public class FabricBridgeService implements BridgeService {

    static {
        // Ensure class is loaded before the category is accessed.
        MobCategory.values();
    }

    private MobCategory alien;

    private MobCategory ovomorph;

    private MobCategory predator;

    @Override
    public Supplier<Item> createGunSupplier(GunConfig gunConfig) {
        return () -> new FabricGunItem(gunConfig);
    }

    @Override
    public Supplier<Item> createOldPainlessSupplier() {
        return FabricOldPainlessItem::new;
    }

    @Override
    public <E extends Mob> Supplier<SpawnEggItem> createSpawnEggSupplier(
        Supplier<EntityType<E>> entityType,
        int primaryEggColour,
        int secondaryEggColour,
        Item.Properties itemProperties
    ) {
        return () -> new SpawnEggItem(entityType.get(), primaryEggColour, secondaryEggColour, itemProperties);
    }

    @Override
    public MobCategory getAlienMobCategory() {
        return alien;
    }

    @Override
    public MobCategory getOvomorphMobCategory() {
        return ovomorph;
    }

    @Override
    public MobCategory getPredatorMobCategory() {
        return predator;
    }

    public void setAlienMobCategory(MobCategory alien) {
        this.alien = alien;
    }

    public void setOvomorphMobCategory(MobCategory ovomorph) {
        this.ovomorph = ovomorph;
    }

    public void setPredatorMobCategory(MobCategory predator) {
        this.predator = predator;
    }
}
