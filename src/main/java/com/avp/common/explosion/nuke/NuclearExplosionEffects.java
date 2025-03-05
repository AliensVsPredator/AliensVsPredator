package com.avp.common.explosion.nuke;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.avp.common.explosion.Explosion;
import com.avp.common.util.ExplosionUtil;

public class NuclearExplosionEffects {

    private static final Map<Block, Block> BLOCK_TRANSFORMER_MAP = Map.ofEntries(
        Map.entry(Blocks.ANDESITE, Blocks.GRAVEL),
        Map.entry(Blocks.DEEPSLATE, Blocks.COBBLED_DEEPSLATE),
        Map.entry(Blocks.DIORITE, Blocks.SAND),
        Map.entry(Blocks.GRANITE, Blocks.RED_SAND),
        Map.entry(Blocks.GRASS_BLOCK, Blocks.COARSE_DIRT),
        Map.entry(Blocks.LAVA, Blocks.MAGMA_BLOCK),
        Map.entry(Blocks.STONE, Blocks.COBBLESTONE)
    );

    private final Set<ChunkPos> visitedChunks;

    public NuclearExplosionEffects() {
        this.visitedChunks = new HashSet<>();
    }

    public void apply(Explosion explosion, BlockPos pos) {
        var centerPos = explosion.config().centerBlockPosition();
        var level = explosion.level();
        var x = pos.getX() - centerPos.getX();
        var y = pos.getY() - centerPos.getY();
        var z = pos.getZ() - centerPos.getZ();
        var radiusX = explosion.config().largestRadius(Direction.Axis.X);
        var radiusZ = explosion.config().largestRadius(Direction.Axis.Z);
        var radiusYDown = explosion.config().radius(Direction.DOWN);
        var radiusYUp = explosion.config().radius(Direction.UP);

        // Scale Y contribution to better balance vertical vs horizontal edge checks
        var yScaleFactor = (radiusX + radiusZ) / 2.0 / (double) Math.max(radiusYUp, radiusYDown);

        var horizontalDistance = ExplosionUtil.getNormalizedHorizontalDistance(explosion, x, z);
        var verticalDistance = ExplosionUtil.getNormalizedVerticalDistance(explosion, y) * yScaleFactor;
        var distance = horizontalDistance + verticalDistance;
        var flags = Block.UPDATE_CLIENTS | Block.UPDATE_SUPPRESS_DROPS;
        var rand = level.random.nextFloat();

        if (distance > 0.98) {

            Block transformedBlock;

            if (rand > horizontalDistance) {
                transformedBlock = Blocks.BLACKSTONE;
            } else {
                var blockState = level.getBlockState(pos);

                if (blockState.is(BlockTags.DIRT)) {
                    transformedBlock = Blocks.COARSE_DIRT;
                } else if (blockState.is(BlockTags.SAND) || blockState.is(Blocks.SANDSTONE) || blockState.is(Blocks.RED_SANDSTONE)) {
                    var rand2 = level.random.nextInt(100);

                    if (rand2 < 33) {
                        transformedBlock = Blocks.GLASS;
                    } else {
                        transformedBlock = Blocks.MAGMA_BLOCK;
                    }
                } else {
                    transformedBlock = BLOCK_TRANSFORMER_MAP.getOrDefault(blockState.getBlock(), Blocks.BASALT);
                }
            }

            level.setBlock(pos, transformedBlock.defaultBlockState(), flags);
        } else if (distance > 0.75) {
            if (rand > horizontalDistance) {
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), flags);
            } else {
                level.setBlock(pos, Blocks.FIRE.defaultBlockState(), flags);
            }
        } else {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), flags);
        }

        tryTransformChunkBiome(level, pos);
    }

    private void tryTransformChunkBiome(ServerLevel level, BlockPos pos) {
        var chunkPos = new ChunkPos(pos);

        if (!visitedChunks.contains(chunkPos)) {
            var biome = level.registryAccess()
                .registryOrThrow(Registries.BIOME)
                .getHolderOrThrow(Biomes.BASALT_DELTAS);

            setBiome(level, pos, biome);
            level.getChunkSource().chunkMap.resendBiomesForChunks(List.of(level.getChunk(chunkPos.x, chunkPos.z)));

            visitedChunks.add(chunkPos);
        }
    }

    private void setBiome(ServerLevel level, BlockPos pos, Holder<Biome> holder) {
        var chunk = level.getChunkAt(pos);

        chunk.fillBiomesFromNoise((i, j, k, sampler) -> {
            int l = QuartPos.toBlock(i);
            int m = QuartPos.toBlock(j);
            int n = QuartPos.toBlock(k);

            Holder<Biome> holder2 = chunk.getNoiseBiome(i, j, k);

            return holder;
        }, level.getChunkSource().randomState().sampler());

        chunk.setUnsaved(true);
    }
}
