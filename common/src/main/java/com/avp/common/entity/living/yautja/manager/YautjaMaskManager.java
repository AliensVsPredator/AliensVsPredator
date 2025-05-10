package com.avp.common.entity.living.yautja.manager;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;

import com.avp.common.entity.living.yautja.Yautja;

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
        var hasMask = yautja.getEntityData().get(hasMaskEDA);

        if (!hasMask) {
            return;
        }

        var overHalfHealth = yautja.getHealth() > yautja.getMaxHealth() / 2;
        yautja.getEntityData().set(hasMaskEDA, overHalfHealth);
    }

    public boolean hasMask() {
        return yautja.getEntityData().get(hasMaskEDA);
    }

    public void load(CompoundTag compoundTag) {
        if (compoundTag.contains(MASK_KEY)) {
            yautja.getEntityData().set(hasMaskEDA, compoundTag.getBoolean(MASK_KEY));
        }
    }

    public void save(CompoundTag compoundTag) {
        compoundTag.putBoolean(MASK_KEY, yautja.getEntityData().get(hasMaskEDA));
    }
}
