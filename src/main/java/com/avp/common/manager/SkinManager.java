package com.avp.common.manager;

import net.minecraft.resources.ResourceLocation;

import com.avp.AVPResources;
import com.avp.common.entity.living.human.AbstractHumanMob;

public class SkinManager {

    protected ResourceLocation cachedMaleTexture;

    protected ResourceLocation cachedFemaleTexture;

    protected int maxMaleTextures;

    protected int maxFemaleTextures;

    protected final AbstractHumanMob entity;

    public SkinManager(AbstractHumanMob entity, int maxMaleTextures, int maxFemaleTextures) {
        this.entity = entity;
        this.maxMaleTextures = maxMaleTextures;
        this.maxFemaleTextures = maxFemaleTextures;
    }

    public ResourceLocation getMaleTexture(String humanType) {
        if (cachedMaleTexture == null) {
            cachedMaleTexture = AVPResources.entityTextureLocation(
                humanType + "_male_" + this.entity.getRandom().nextIntBetweenInclusive(1, this.maxMaleTextures)
            );
        }
        return cachedMaleTexture;
    }

    public ResourceLocation getFemaleTexture(String humanType) {
        if (cachedFemaleTexture == null) {
            cachedFemaleTexture = AVPResources.entityTextureLocation(
                humanType + "_female_" + this.entity.getRandom().nextIntBetweenInclusive(1, this.maxFemaleTextures)
            );
        }
        return cachedFemaleTexture;
    }
}
