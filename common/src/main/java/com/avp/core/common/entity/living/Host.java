package com.avp.core.common.entity.living;

import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;

import com.avp.core.common.entity.living.alien.parasite.Parasite;
import com.avp.core.common.manager.GeneManager;

public interface Host {

    GeneManager getOrCreateGeneManager();

    int parasiteGrowthTimeInTicks();

    @Nullable
    EntityType<?> parasiteType();

    void injectEmbryo(Parasite parasite);
}
