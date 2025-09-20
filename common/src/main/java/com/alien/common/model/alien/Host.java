package com.alien.common.model.alien;

import com.alien.common.gameplay.entity.living.alien.parasite.Parasite;
import com.just.core.functional.option.Option;
import com.lib.common.gameplay.entity.manager.GeneContainer;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;

public interface Host {

    Option<EntityType<?>> getEmbryoType();

    void setEmbryoType(@Nullable EntityType<?> embryoType);

    void implantEmbryo(Parasite parasite);

    GeneContainer getOrCreateParasiteGeneContainer();

    int getEmbryoGrowthTimeInTicks();

    void setEmbryoGrowthTimeInTicks(int growthTimeInTicks);

    default void incrementEmbryoGrowthTimeInTicks() {
        setEmbryoGrowthTimeInTicks(getEmbryoGrowthTimeInTicks() + 1);
    }

    default void removeEmbryo() {
        setEmbryoType(null);
        setEmbryoGrowthTimeInTicks(0);
        getOrCreateParasiteGeneContainer().clear();
    }
}
