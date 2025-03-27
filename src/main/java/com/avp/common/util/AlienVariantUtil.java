package com.avp.common.util;

import com.avp.common.block.AVPBlocks;
import com.avp.common.entity.acid.Acid;
import com.avp.common.particle.AVPParticleTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.item.Item;

import com.avp.common.entity.living.alien.Alien;
import com.avp.common.item.AVPItems;
import net.minecraft.world.level.block.Block;

public class AlienVariantUtil {

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

    public static ParticleOptions getParticleFor(Acid acid) {
        return switch (acid) {
            case Acid irradiatedAcid when irradiatedAcid.isIrradiated() -> AVPParticleTypes.IRRADIATED_ACID;
            case Acid netherAcid when netherAcid.isNetherAfflicted() -> AVPParticleTypes.BLUE_ACID;
            default -> AVPParticleTypes.ACID;
        };
    }
}
