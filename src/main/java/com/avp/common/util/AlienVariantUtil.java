package com.avp.common.util;

import com.avp.common.block.AVPBlocks;
import com.avp.common.block.resin.ResinVeinBlock;
import com.avp.common.entity.acid.Acid;
import com.avp.common.particle.AVPParticleTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.item.Item;

import com.avp.common.entity.living.alien.Alien;
import com.avp.common.item.AVPItems;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

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
            case Alien netherAlien when netherAlien.isNetherAfflicted() -> AVPBlocks.NETHER_RESIN_NODE.defaultBlockState();
            case Alien aberrantAlien when aberrantAlien.isAberrant() -> AVPBlocks.ABERRANT_RESIN_NODE.defaultBlockState();
            case Alien irradiatedAlien when irradiatedAlien.isIrradiated() -> AVPBlocks.IRRADIATED_RESIN_NODE.defaultBlockState();
            default -> AVPBlocks.RESIN_NODE.defaultBlockState();
        };
    }

    public static Item getResinBallForType(BlockState blockState) {
        return RESIN_BALL_MAPPING.getOrDefault(blockState.getBlock(), AVPItems.RESIN_BALL);
    }
}
