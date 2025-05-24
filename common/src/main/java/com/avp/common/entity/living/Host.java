package com.avp.common.entity.living;

import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;

import com.avp.common.entity.living.alien.parasite.Parasite;
import com.avp.common.entity.living.manager.GeneManager;

public interface Host {

    GeneManager getOrCreateGeneManager();

    @Nullable
    EntityType<?> getParasiteType();

    void injectEmbryo(Parasite parasite);

    void clearParasiteType();
}
