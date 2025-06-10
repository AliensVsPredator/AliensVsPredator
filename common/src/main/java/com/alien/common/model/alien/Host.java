package com.alien.common.model.alien;

import com.alien.common.gameplay.entity.living.alien.parasite.Parasite;
import com.lib.common.gameplay.entity.manager.GeneManager;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;

public interface Host {

    GeneManager getOrCreateGeneManager();

    @Nullable
    EntityType<?> getParasiteType();

    void injectEmbryo(Parasite parasite);

    void clearParasiteType();
}
