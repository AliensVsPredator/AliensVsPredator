package com.avp.common.manager;

import net.minecraft.resources.ResourceLocation;

import com.avp.AVPResources;
import com.avp.common.entity.living.human.AbstractHumanMob;

public class BeardManager {

    protected final AbstractHumanMob entity;

    protected int maxBeardTextures;

    protected ResourceLocation cachedMaleBeardTexture;

    public BeardManager(AbstractHumanMob entity, int maxBeardTextures) {
        this.entity = entity;
        this.maxBeardTextures = maxBeardTextures;
    }
}
