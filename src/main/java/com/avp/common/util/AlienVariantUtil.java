package com.avp.common.util;

import com.avp.common.entity.living.alien.xenomorph.queen.Queen;
import com.avp.common.entity.type.AVPEntityTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import com.avp.common.block.AVPBlocks;
import com.avp.common.block.resin.ResinVeinBlock;
import com.avp.common.entity.acid.Acid;
import com.avp.common.entity.living.alien.Alien;
import com.avp.common.item.AVPItems;
import com.avp.common.particle.AVPParticleTypes;

public class AlienVariantUtil {

    private static final Map<Block, Item> RESIN_BALL_MAPPING = Map.ofEntries(
            Map.entry(AVPBlocks.ABERRANT_RESIN_NODE, AVPItems.ABERRANT_RESIN_BALL),
            Map.entry(AVPBlocks.ABERRANT_RESIN, AVPItems.ABERRANT_RESIN_BALL),
            Map.entry(AVPBlocks.ABERRANT_RESIN_WEB, AVPItems.ABERRANT_RESIN_BALL),
            Map.entry(AVPBlocks.ABERRANT_RESIN_VEIN, AVPItems.ABERRANT_RESIN_BALL),
            Map.entry(AVPBlocks.IRRADIATED_RESIN_NODE, AVPItems.IRRADIATED_RESIN_BALL),
            Map.entry(AVPBlocks.IRRADIATED_RESIN, AVPItems.IRRADIATED_RESIN_BALL),
            Map.entry(AVPBlocks.IRRADIATED_RESIN_WEB, AVPItems.IRRADIATED_RESIN_BALL),
            Map.entry(AVPBlocks.IRRADIATED_RESIN_VEIN, AVPItems.IRRADIATED_RESIN_BALL),
            Map.entry(AVPBlocks.NETHER_RESIN_NODE, AVPItems.NETHER_RESIN_BALL),
            Map.entry(AVPBlocks.NETHER_RESIN, AVPItems.NETHER_RESIN_BALL),
            Map.entry(AVPBlocks.NETHER_RESIN_WEB, AVPItems.NETHER_RESIN_BALL),
            Map.entry(AVPBlocks.NETHER_RESIN_VEIN, AVPItems.NETHER_RESIN_BALL)
    );

    private static final Map<Block, Block> RESIN_VEIN_MAPPING = Map.ofEntries(
            Map.entry(AVPBlocks.NETHER_RESIN_NODE, AVPBlocks.NETHER_RESIN_VEIN),
            Map.entry(AVPBlocks.ABERRANT_RESIN_NODE, AVPBlocks.ABERRANT_RESIN_VEIN),
            Map.entry(AVPBlocks.IRRADIATED_RESIN_NODE, AVPBlocks.IRRADIATED_RESIN_VEIN)
    );

    private AlienVariantUtil() {}

    public static @Nullable EntityType<? extends Alien> getVariantTypeFor(Alien alien) {
        return switch (alien) {
            case Alien netherAlien when netherAlien.isNetherAfflicted() -> alien.getNetherType();
            case Alien aberrantAlien when aberrantAlien.isAberrant() -> alien.getAberrantType();
            case Alien irradiatedAlien when irradiatedAlien.isIrradiated() -> alien.getIrradiatedType();
            default -> alien.getDefaultType();
        };
    }

    public static ResourceKey<LootTable> getLootTableFor(Alien alien) {
        var type = getVariantTypeFor(alien);
        return type == null ? alien.getType().getDefaultLootTable() : type.getDefaultLootTable();
    }

    public static Item getResinBallFor(Alien alien) {
        return switch (alien) {
            case Alien netherAlien when netherAlien.isNetherAfflicted() -> AVPItems.NETHER_RESIN_BALL;
            case Alien aberrantAlien when aberrantAlien.isAberrant() -> AVPItems.ABERRANT_RESIN_BALL;
            case Alien irradiatedAlien when irradiatedAlien.isIrradiated() -> AVPItems.IRRADIATED_RESIN_BALL;
            default -> AVPItems.RESIN_BALL;
        };
    }

    public static Block getResinNodeFor(Alien alien) {
        return switch (alien) {
            case Alien netherAlien when netherAlien.isNetherAfflicted() -> AVPBlocks.NETHER_RESIN_NODE;
            case Alien aberrantAlien when aberrantAlien.isAberrant() -> AVPBlocks.ABERRANT_RESIN_NODE;
            case Alien irradiatedAlien when irradiatedAlien.isIrradiated() -> AVPBlocks.IRRADIATED_RESIN_NODE;
            default -> AVPBlocks.RESIN_NODE;
        };
    }

    public static ResinVeinBlock getResinVeinFor(Block block) {
        return (ResinVeinBlock) RESIN_VEIN_MAPPING.getOrDefault(block, AVPBlocks.RESIN_VEIN);
    }

    public static ParticleOptions getParticleFor(Acid acid) {
        return switch (acid) {
            case Acid irradiatedAcid when irradiatedAcid.isIrradiated() -> AVPParticleTypes.IRRADIATED_ACID;
            case Acid netherAcid when netherAcid.isNetherAfflicted() -> AVPParticleTypes.BLUE_ACID;
            default -> AVPParticleTypes.ACID;
        };
    }

    public static BlockState getResinNodeForType(Alien alien) {
        return switch (alien) {
            case Alien netherAlien when netherAlien.isNetherAfflicted() ->
                    AVPBlocks.NETHER_RESIN_NODE.defaultBlockState();
            case Alien aberrantAlien when aberrantAlien.isAberrant() ->
                    AVPBlocks.ABERRANT_RESIN_NODE.defaultBlockState();
            case Alien irradiatedAlien when irradiatedAlien.isIrradiated() ->
                    AVPBlocks.IRRADIATED_RESIN_NODE.defaultBlockState();
            default -> AVPBlocks.RESIN_NODE.defaultBlockState();
        };
    }

    public static Item getResinBallForType(BlockState blockState) {
        return RESIN_BALL_MAPPING.getOrDefault(blockState.getBlock(), AVPItems.RESIN_BALL);
    }

    public static EntityType<?> getOvamorphTypeFor(Queen queen, boolean isRoyal) {
        return switch (queen) {
            case Queen netherQueen when netherQueen.isNetherAfflicted() && isRoyal ->
                    AVPEntityTypes.ROYAL_NETHER_OVAMORPH;
            case Queen netherQueen when netherQueen.isNetherAfflicted() -> AVPEntityTypes.NETHER_OVAMORPH;
            case Queen aberrantQueen when aberrantQueen.isAberrant() && isRoyal ->
                    AVPEntityTypes.ROYAL_ABERRANT_OVAMORPH;
            case Queen aberrantQueen when aberrantQueen.isAberrant() -> AVPEntityTypes.ABERRANT_OVAMORPH;
            default -> isRoyal ? AVPEntityTypes.ROYAL_OVAMORPH : AVPEntityTypes.OVAMORPH;
        };
    }
}