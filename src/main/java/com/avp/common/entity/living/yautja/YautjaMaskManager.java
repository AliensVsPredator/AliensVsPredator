package com.avp.common.entity.living.yautja;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;

public class YautjaMaskManager {

    private static final String MASK_KEY = "hasMask";

    private final Yautja yautja;

    private final EntityDataAccessor<Boolean> hasMaskEDA;

    public YautjaMaskManager(Yautja yautja, EntityDataAccessor<Boolean> hasMaskEDA) {
        this.yautja = yautja;
        this.hasMaskEDA = hasMaskEDA;
    }

    public void tick() {
        if (yautja.level().isClientSide) {
            return;
        }

        checkMask();
    }

    public void checkMask() {
        var overHalfHealth = yautja.getHealth() > yautja.getMaxHealth() / 2;
        yautja.getEntityData().set(hasMaskEDA, overHalfHealth);
    }

    public boolean hasMask() {
        return yautja.getEntityData().get(hasMaskEDA);
    }

    public void load(CompoundTag compoundTag) {
        yautja.getEntityData().set(hasMaskEDA, compoundTag.getBoolean(MASK_KEY));
    }

    public void save(CompoundTag compoundTag) {
        compoundTag.putBoolean(MASK_KEY, yautja.getEntityData().get(hasMaskEDA));
    }
}
