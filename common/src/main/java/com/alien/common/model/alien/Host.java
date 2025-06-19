package com.alien.common.model.alien;

import com.alien.common.gameplay.entity.living.alien.parasite.Parasite;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;

public interface Host extends GeneCarrier {

    @Nullable
    EntityType<?> getEmbryoType();

    void implantEmbryo(Parasite parasite);

    void removeEmbryo();

    int getEmbryoGrowthTimeInTicks();

    void setEmbryoGrowthTimeInTicks(int growthTimeInTicks);

    default void incrementEmbryoGrowthTimeInTicks() {
        setEmbryoGrowthTimeInTicks(getEmbryoGrowthTimeInTicks() + 1);
    }
}
