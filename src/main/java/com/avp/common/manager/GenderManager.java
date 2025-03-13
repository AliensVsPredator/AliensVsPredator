package com.avp.common.manager;

import com.avp.common.entity.living.human.AbstractHumanMob;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;

public class GenderManager {
    private static final String GENDER_TAG_KEY = "gender";

    private final AbstractHumanMob entity;

    private final EntityDataAccessor<Boolean> genderEDA;

    public GenderManager(AbstractHumanMob entity, EntityDataAccessor<Boolean> genderEDA) {
        this.entity = entity;
        this.genderEDA = genderEDA;
    }

    public void tick() {
        if (entity.level().isClientSide) {
            return;
        }
        entity.getEntityData().set(genderEDA, entity.getRandom().nextIntBetweenInclusive(0, 10) <= 6);
    }

    public boolean getGender() {
        return entity.getEntityData().get(genderEDA);
    }

    public void load(CompoundTag compoundTag) {
        entity.getEntityData().set(genderEDA, compoundTag.getBoolean(GENDER_TAG_KEY));
    }

    public void save(CompoundTag compoundTag) {
        compoundTag.putBoolean(GENDER_TAG_KEY, entity.getEntityData().get(genderEDA));
    }
}
