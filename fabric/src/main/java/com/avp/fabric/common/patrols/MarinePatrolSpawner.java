package com.avp.fabric.common.patrols;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;

import com.avp.common.block.AVPBlockTags;
import com.avp.common.entity.type.TempAVPEntityTypes;
import com.avp.common.worldgen.biome.AVPBiomeTags;

public class MarinePatrolSpawner implements CustomSpawner {

    private int nextTick;

    public MarinePatrolSpawner() {}

    @Override
    public int tick(ServerLevel level, boolean spawnEnemies, boolean spawnFriendlies) {
        if (!spawnEnemies || !level.getGameRules().getBoolean(GameRules.RULE_DO_PATROL_SPAWNING)) {
            return 0;
        }

        var randomSource = level.random;
        --this.nextTick;

        if (this.nextTick > 0) {
            return 0;
        }

        this.nextTick += 12000 + randomSource.nextInt(1200);

        if (!isValidSpawnTime(level)) {
            return 0;
        }

        var player = getRandomPlayer(level);
        if (player == null) {
            return 0;
        }

        var mutableBlockPos = getRandomNearbyPosition(player, randomSource);
        if (!isValidSpawnLocation(mutableBlockPos, level)) {
            return 0;
        }

        return spawnPatrolMembers(level, mutableBlockPos);
    }

    private boolean isValidSpawnTime(ServerLevel level) {
        return level.random.nextInt(5) == 0;
    }

    private Player getRandomPlayer(ServerLevel level) {
        var players = level.players();
        if (players.isEmpty()) {
            return null;
        }

        var player = players.get(level.random.nextInt(players.size()));
        return player.isSpectator() ? null : player;
    }

    private boolean isValidPlayerSpawn(Player player, ServerLevel level) {
        return !level.isCloseToVillage(player.blockPosition(), 2);
    }

    private BlockPos getRandomNearbyPosition(Player player, RandomSource randomSource) {
        var xOffset = (24 + randomSource.nextInt(24)) * (randomSource.nextBoolean() ? -1 : 1);
        var zOffset = (24 + randomSource.nextInt(24)) * (randomSource.nextBoolean() ? -1 : 1);
        return player.blockPosition().mutable().move(xOffset, 0, zOffset);
    }

    @SuppressWarnings("deprecation")
    private boolean isValidSpawnLocation(BlockPos pos, ServerLevel level) {
        if (!level.hasChunksAt(pos.getX() - 10, pos.getZ() - 10, pos.getX() + 10, pos.getZ() + 10)) {
            return false;
        }

        var biomeHolder = level.getBiome(pos);
        return biomeHolder.is(AVPBiomeTags.HAS_MARINE_CAMP_GRASS);
    }

    private int spawnPatrolMembers(ServerLevel level, BlockPos startPos) {
        var randomSource = level.random;
        var difficulty = (int) Math.ceil(level.getCurrentDifficultyAt(startPos).getEffectiveDifficulty()) + 1;

        var spawnCount = 0;
        for (var i = 0; i < difficulty; ++i) {
            spawnCount++;

            startPos.setY(level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, startPos).getY());
            this.spawnPatrolMember(level, startPos);

            startPos.setX(startPos.getX() + randomSource.nextInt(5) - randomSource.nextInt(5));
            startPos.setZ(startPos.getZ() + randomSource.nextInt(5) - randomSource.nextInt(5));
        }

        return spawnCount;
    }

    private void spawnPatrolMember(ServerLevel level, BlockPos pos) {
        var belowState = level.getBlockState(pos.below());
        var spawnableBlock = AVPBlockTags.MARINE_SPAWN_BLOCKS;

        if (!belowState.is(Blocks.GRASS_BLOCK)) {
            return;
        }

        var marineMob = TempAVPEntityTypes.MARINE.get().create(level);

        if (marineMob == null) {
            return;
        }

        marineMob.setPos(pos.getX(), pos.getY(), pos.getZ());
        marineMob.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), MobSpawnType.PATROL, null);
        level.addFreshEntityWithPassengers(marineMob);
    }
}
