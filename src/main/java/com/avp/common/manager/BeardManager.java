package com.avp.common.manager;

import com.avp.AVPResources;
import com.avp.common.entity.living.human.AbstractHumanMob;
import net.minecraft.resources.ResourceLocation;

public class BeardManager {

    protected final AbstractHumanMob entity;

    protected int maxBeardTextures;

    protected ResourceLocation cachedMaleBeardTexture;

    public BeardManager(AbstractHumanMob entity, int maxBeardTextures) {
        this.entity = entity;
        this.maxBeardTextures = maxBeardTextures;
    }

    public ResourceLocation getMaleBeardTexture(String humanType) {
        if (cachedMaleBeardTexture == null) {
            var random1 = this.entity.getRandom().nextIntBetweenInclusive(1, this.maxBeardTextures);
            int random2;
            if (random1 == 3) {
                random2 = 6;
            } else {
                random2 = this.entity.getSharedSecondRandomValue(6);
            }
            cachedMaleBeardTexture = AVPResources.entityTextureLocation(humanType + "_male_beard" + random1 + "_" + random2);
        }
        return cachedMaleBeardTexture;
    }
}
