package com.avp.fabric.common.entity.living;

import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;

import com.avp.fabric.common.entity.living.alien.parasite.Parasite;
import com.avp.fabric.common.manager.GeneManager;

public interface Host {

    GeneManager getOrCreateGeneManager();

    int parasiteGrowthTimeInTicks();

    @Nullable
    EntityType<?> parasiteType();

    void injectEmbryo(Parasite parasite);

    void clearParasiteSourceType();
}
