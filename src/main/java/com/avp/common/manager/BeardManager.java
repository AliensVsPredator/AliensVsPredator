package com.avp.common.manager;

import net.minecraft.resources.ResourceLocation;

import com.avp.common.entity.living.human.AbstractHuman;

@Deprecated(forRemoval = true)
public class BeardManager {

    protected final AbstractHuman entity;

    protected int maxBeardTextures;

    protected ResourceLocation cachedMaleBeardTexture;

    public BeardManager(AbstractHuman entity, int maxBeardTextures) {
        this.entity = entity;
        this.maxBeardTextures = maxBeardTextures;
    }
}
