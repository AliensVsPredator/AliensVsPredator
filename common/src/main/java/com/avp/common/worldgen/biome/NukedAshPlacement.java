package com.avp.common.worldgen.biome;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.levelgen.Heightmap;

import com.avp.common.block.AVPBlocks;

public class NukedAshPlacement {

    public void tick(ServerLevel serverLevel) {
        if (serverLevel.random.nextInt(100) != 0) {
            return;
        }

        var players = serverLevel.players();
        if (players.isEmpty()) {
            return;
        }

        var player = players.get(serverLevel.random.nextInt(players.size()));
        if (player.isSpectator()) {
            return;
        }
        if (serverLevel.getBiome(player.blockPosition()).is(AVPBiomes.NUKED_BIOME)) {
            var playerChunkPos = player.chunkPosition();
            var random = serverLevel.random;

            var radius = 3;
            for (var chunkX = playerChunkPos.x - radius; chunkX <= playerChunkPos.x + radius; chunkX++) {
                for (var chunkZ = playerChunkPos.z - radius; chunkZ <= playerChunkPos.z + radius; chunkZ++) {
                    if (!serverLevel.hasChunk(chunkX, chunkZ)) {
                        continue;
                    }

                    for (var i = 0; i < 10; i++) {
                        var x = (chunkX * 16) + random.nextInt(16);
                        var z = (chunkZ * 16) + random.nextInt(16);
                        var y = serverLevel.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z) - 1;
                        var blockPos = new BlockPos(x, y, z);

                        if (serverLevel.getBiome(blockPos).is(AVPBiomes.NUKED_BIOME)) {
                            var currentBlock = serverLevel.getBlockState(blockPos);
                            var currentBlockAbove = serverLevel.getBlockState(blockPos.above());
                            if (currentBlock.isSolidRender(serverLevel, blockPos) && !currentBlock.liquid() && currentBlockAbove.isAir()) {
                                var ashBlock = AVPBlocks.ASH_BLOCK.get().defaultBlockState().setValue(SnowLayerBlock.LAYERS, 1);
                                serverLevel.setBlock(blockPos.above(), ashBlock, 3);
                            }
                        }
                    }
                }
            }
        }
    }
}
