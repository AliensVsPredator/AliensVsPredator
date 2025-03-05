package com.avp.common.entity.living.alien;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biomes;

import com.avp.AVP;
import com.avp.common.block.AVPBlockTags;
import com.avp.common.config.ConfigAlienSpawningContainer;
import com.avp.common.entity.AVPEntityTypeTags;

public class AlienSpawning {

    public static <T extends Alien> SpawnPlacements.SpawnPredicate<T> createPredicate(
        ConfigAlienSpawningContainer container
    ) {
        return (
            entityType,
            serverLevelAccessor,
            mobSpawnType,
            blockPos,
            randomSource
        ) -> {
            var properties = AVP.SPAWNING_CONFIG.properties();
            var maxY = properties.getOrThrow(container.mobSpawning().maxY());
            var minY = properties.getOrThrow(container.mobSpawning().minY());
            var requiresResin = properties.getOrThrow(container.requiresResin());
            var belowState = serverLevelAccessor.getBlockState(blockPos.below());
            var resinBlock = entityType.is(AVPEntityTypeTags.NETHER_ALIENS) ? AVPBlockTags.NETHER_RESIN : AVPBlockTags.NORMAL_RESIN;
            var isValidResinPos = belowState.is(resinBlock);

            if (requiresResin && !isValidResinPos) {
                return false;
            }

            var biome = serverLevelAccessor.getBiome(blockPos);
            // If the position is resin, then it's already a valid "biome".
            boolean isValidBiome = isValidResinPos;

            if (!isValidBiome) {
                // If the position is not resin (i.e. natural spawn attempt), then check to see if the biome is valid
                // for the type of alien.
                if (entityType.is(AVPEntityTypeTags.NETHER_ALIENS)) {
                    isValidBiome = biome.is(Biomes.CRIMSON_FOREST) || biome.is(Biomes.NETHER_WASTES);
                } else if (entityType.is(AVPEntityTypeTags.NORMAL_ALIENS)) {
                    isValidBiome = biome.is(BiomeTags.IS_OVERWORLD);
                }
            }

            return blockPos.getY() <= maxY
                && blockPos.getY() >= minY
                && isValidBiome
                && checkSpawnRules(entityType, serverLevelAccessor, mobSpawnType, blockPos, randomSource);
        };
    }

    public static boolean checkSpawnRules(
        EntityType<? extends Monster> entityType,
        ServerLevelAccessor serverLevelAccessor,
        MobSpawnType mobSpawnType,
        BlockPos blockPos,
        RandomSource randomSource
    ) {
        return Monster.checkMonsterSpawnRules(
            entityType,
            serverLevelAccessor,
            mobSpawnType,
            blockPos,
            randomSource
        );
    }
}
