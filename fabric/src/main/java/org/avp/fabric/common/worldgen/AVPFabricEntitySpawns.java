package org.avp.fabric.common.worldgen;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.world.entity.MobCategory;
import org.avp.common.config.AVPConfig;
import org.avp.common.entity.AVPBaseAlienEntityTypes;

/**
 * @author Boston Vanseghi
 */
public class AVPFabricEntitySpawns {

    public static void addEntitySpawns() {
        if (AVPConfig.Aliens.ENABLE_XENOMORPH_OVERWORLD_SPAWNS) {
            // TODO: Expand this to support all types of xenos spawning.
            BiomeModifications.addSpawn(
                BiomeSelectors.foundInOverworld(),
                MobCategory.MONSTER,
                AVPBaseAlienEntityTypes.DRONE.get(),
                100,
                1,
                3
            );
        }
    }

    private AVPFabricEntitySpawns() {
        throw new UnsupportedOperationException();
    }
}
