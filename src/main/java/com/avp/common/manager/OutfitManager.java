package com.avp.common.manager;

import net.minecraft.resources.ResourceLocation;

import com.avp.AVPResources;
import com.avp.common.entity.living.human.AbstractHumanMob;

public class OutfitManager {

    protected ResourceLocation cachedMaleOutfitTexture;

    protected ResourceLocation cachedFemaleOutfitTexture;

    protected int maxMaleOutfitTextures;

    protected int maxFemaleOutfitTextures;

    protected final AbstractHumanMob entity;

    public OutfitManager(AbstractHumanMob entity, int maxMaleOutfitTextures, int maxFemaleOutfitTextures) {
        this.entity = entity;
        this.maxMaleOutfitTextures = maxMaleOutfitTextures;
        this.maxFemaleOutfitTextures = maxFemaleOutfitTextures;
    }

    public ResourceLocation getMaleOutfitTexture(String humanType) {
        if (cachedMaleOutfitTexture == null) {
            cachedMaleOutfitTexture = AVPResources.entityTextureLocation(
                humanType + "_male_outfit" + entity.getRandom().nextIntBetweenInclusive(1, maxMaleOutfitTextures)
            );
        }
        return cachedMaleOutfitTexture;
    }

    public ResourceLocation getFemaleOutfitTexture(String humanType) {
        if (cachedFemaleOutfitTexture == null) {
            cachedFemaleOutfitTexture = AVPResources.entityTextureLocation(
                humanType + "_female_outfit" + entity.getRandom().nextIntBetweenInclusive(1, maxFemaleOutfitTextures)
            );
        }
        return cachedFemaleOutfitTexture;
    }

}
