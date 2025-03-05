package com.avp.common.entity.living;

import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;

import com.avp.common.entity.living.alien.parasite.Parasite;
import com.avp.common.manager.GeneManager;

public interface Host {

    GeneManager getOrCreateGeneManager();

    int parasiteGrowthTimeInTicks();

    @Nullable
    EntityType<?> parasiteType();

    void injectEmbryo(Parasite parasite);
}
