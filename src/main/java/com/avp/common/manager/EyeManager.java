package com.avp.common.manager;

import net.minecraft.resources.ResourceLocation;

import com.avp.AVPResources;
import com.avp.common.entity.living.human.AbstractHuman;

@Deprecated(forRemoval = true)
public class EyeManager {

    protected ResourceLocation cachedMaleEyeTexture;

    protected ResourceLocation cachedFemaleEyeTexture;

    protected int maxMaleEyesTextures;

    protected int maxFemaleEyesTextures;

    protected final AbstractHuman entity;

    public EyeManager(AbstractHuman entity, int maxMaleEyesTextures, int maxFemaleEyesTextures) {
        this.entity = entity;
        this.maxMaleEyesTextures = maxMaleEyesTextures;
        this.maxFemaleEyesTextures = maxFemaleEyesTextures;
    }

    public ResourceLocation getMaleEyeTexture(String humanType) {
        if (cachedMaleEyeTexture == null) {
            cachedMaleEyeTexture = AVPResources.entityTextureLocation(
                "marine_male_eyes_" + this.entity.getRandom().nextIntBetweenInclusive(1, this.maxMaleEyesTextures)
            );
        }
        return cachedMaleEyeTexture;
    }

    public ResourceLocation getFemaleEyeTexture(String humanType) {
        if (cachedFemaleEyeTexture == null) {
            cachedFemaleEyeTexture = AVPResources.entityTextureLocation(
                humanType + "_female_eyes_" + this.entity.getRandom().nextIntBetweenInclusive(1, this.maxFemaleEyesTextures)
            );
        }
        return cachedFemaleEyeTexture;
    }
}
