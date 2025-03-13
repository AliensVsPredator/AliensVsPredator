package com.avp.common.manager;

import com.avp.AVPResources;
import com.avp.common.entity.living.human.AbstractHumanMob;
import net.minecraft.resources.ResourceLocation;

public class HairManager {

    protected ResourceLocation cachedMaleHairTexture;

    protected ResourceLocation cachedFemaleHairTexture;

    protected int maxMaleHairTextures;

    protected int maxFemaleHairTextures;

    protected final AbstractHumanMob entity;

    public HairManager(AbstractHumanMob entity, int maxMaleHairTextures, int maxFemaleHairTextures) {
        this.entity = entity;
        this.maxMaleHairTextures = maxMaleHairTextures;
        this.maxFemaleHairTextures = maxFemaleHairTextures;
    }

    public ResourceLocation getMaleHairTexture(String humanType) {
        if (cachedMaleHairTexture == null) {
            var random1 = entity.getRandom().nextIntBetweenInclusive(1, this.maxMaleHairTextures);
            var random2 = entity.getSharedSecondRandomValue();
            cachedMaleHairTexture = AVPResources.entityTextureLocation("marine_male_hair" + random1 + "_" + random2);
        }
        return cachedMaleHairTexture;
    }

    public ResourceLocation getFemaleHairTexture(String humanType) {
        if (cachedFemaleHairTexture == null) {
            cachedFemaleHairTexture = AVPResources.entityTextureLocation(humanType + "_female_hair" +
                    entity.getRandom().nextIntBetweenInclusive(1, this.maxFemaleHairTextures) + "_" +
                    entity.getRandom().nextIntBetweenInclusive(1, 6));
        }
        return cachedFemaleHairTexture;
    }
}
