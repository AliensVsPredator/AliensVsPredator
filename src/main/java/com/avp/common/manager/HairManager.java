package com.avp.common.manager;

import net.minecraft.resources.ResourceLocation;

import com.avp.AVPResources;
import com.avp.common.entity.living.human.AbstractHumanMob;

public class HairManager {

    protected ResourceLocation cachedMaleHairTexture;

    protected ResourceLocation cachedFemaleHairTexture;

    protected int maxMaleHairTextures;

    protected int maxFemaleHairTextures;

    protected int maxFemaleHairTypes;

    protected final AbstractHumanMob entity;

    public HairManager(AbstractHumanMob entity, int maxMaleHairTextures, int maxFemaleHairTextures, int maxFemaleHairTypes) {
        this.entity = entity;
        this.maxMaleHairTextures = maxMaleHairTextures;
        this.maxFemaleHairTextures = maxFemaleHairTextures;
        this.maxFemaleHairTypes = maxFemaleHairTypes;
    }

    public ResourceLocation getMaleHairTexture(String humanType) {
        if (cachedMaleHairTexture == null) {
            var random1 = entity.getRandom().nextIntBetweenInclusive(1, this.maxMaleHairTextures);
            var random2 = entity.getSharedSecondRandomValue(6);
            cachedMaleHairTexture = AVPResources.entityTextureLocation(humanType + "_male_hair" + random1 + "_" + random2);
        }
        return cachedMaleHairTexture;
    }

    public ResourceLocation getFemaleHairTexture(String humanType) {
        if (cachedFemaleHairTexture == null) {
            var random1 = entity.getRandom().nextIntBetweenInclusive(1, this.maxFemaleHairTextures);
            var random2 = entity.getSharedSecondRandomValue(maxFemaleHairTypes);
            cachedFemaleHairTexture = AVPResources.entityTextureLocation(humanType + "_female_hair" + random1 + "_" + random2);
        }
        return cachedFemaleHairTexture;
    }
}
