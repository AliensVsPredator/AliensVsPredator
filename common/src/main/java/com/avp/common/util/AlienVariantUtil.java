package com.avp.common.util;

import com.bvanseg.just.functional.function.Lazy;
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
import com.avp.common.entity.living.alien.xenomorph.queen.Queen;
import com.avp.common.entity.type.AVPEntityTypes;
import com.avp.common.item.AVPItems;
import com.avp.common.particle.AVPParticleTypes;

public class AlienVariantUtil {

    // Made lazy so that the block/item access here doesn't cause NeoForge to crash during startup.
    private static final Lazy<Map<Block, Item>> RESIN_BALL_MAPPING = Lazy.of(
        () -> Map.ofEntries(
            Map.entry(AVPBlocks.ABERRANT_RESIN_NODE.get(), AVPItems.ABERRANT_RESIN_BALL.get()),
            Map.entry(AVPBlocks.ABERRANT_RESIN.get(), AVPItems.ABERRANT_RESIN_BALL.get()),
            Map.entry(AVPBlocks.ABERRANT_RESIN_WEB.get(), AVPItems.ABERRANT_RESIN_BALL.get()),
            Map.entry(AVPBlocks.ABERRANT_RESIN_VEIN.get(), AVPItems.ABERRANT_RESIN_BALL.get()),
            Map.entry(AVPBlocks.IRRADIATED_RESIN_NODE.get(), AVPItems.IRRADIATED_RESIN_BALL.get()),
            Map.entry(AVPBlocks.IRRADIATED_RESIN.get(), AVPItems.IRRADIATED_RESIN_BALL.get()),
            Map.entry(AVPBlocks.IRRADIATED_RESIN_WEB.get(), AVPItems.IRRADIATED_RESIN_BALL.get()),
            Map.entry(AVPBlocks.IRRADIATED_RESIN_VEIN.get(), AVPItems.IRRADIATED_RESIN_BALL.get()),
            Map.entry(AVPBlocks.NETHER_RESIN_NODE.get(), AVPItems.NETHER_RESIN_BALL.get()),
            Map.entry(AVPBlocks.NETHER_RESIN.get(), AVPItems.NETHER_RESIN_BALL.get()),
            Map.entry(AVPBlocks.NETHER_RESIN_WEB.get(), AVPItems.NETHER_RESIN_BALL.get()),
            Map.entry(AVPBlocks.NETHER_RESIN_VEIN.get(), AVPItems.NETHER_RESIN_BALL.get())
        )
    );

    private static final Lazy<Map<Block, Block>> RESIN_VEIN_MAPPING = Lazy.of(
        () -> Map.ofEntries(
            Map.entry(AVPBlocks.NETHER_RESIN_NODE.get(), AVPBlocks.NETHER_RESIN_VEIN.get()),
            Map.entry(AVPBlocks.ABERRANT_RESIN_NODE.get(), AVPBlocks.ABERRANT_RESIN_VEIN.get()),
            Map.entry(AVPBlocks.IRRADIATED_RESIN_NODE.get(), AVPBlocks.IRRADIATED_RESIN_VEIN.get())
        )
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
            case Alien netherAlien when netherAlien.isNetherAfflicted() -> AVPItems.NETHER_RESIN_BALL.get();
            case Alien aberrantAlien when aberrantAlien.isAberrant() -> AVPItems.ABERRANT_RESIN_BALL.get();
            case Alien irradiatedAlien when irradiatedAlien.isIrradiated() -> AVPItems.IRRADIATED_RESIN_BALL.get();
            default -> AVPItems.RESIN_BALL.get();
        };
    }

    public static Block getResinNodeFor(Alien alien) {
        return switch (alien) {
            case Alien netherAlien when netherAlien.isNetherAfflicted() -> AVPBlocks.NETHER_RESIN_NODE.get();
            case Alien aberrantAlien when aberrantAlien.isAberrant() -> AVPBlocks.ABERRANT_RESIN_NODE.get();
            case Alien irradiatedAlien when irradiatedAlien.isIrradiated() -> AVPBlocks.IRRADIATED_RESIN_NODE.get();
            default -> AVPBlocks.RESIN_NODE.get();
        };
    }

    public static ResinVeinBlock getResinVeinFor(Block block) {
        return (ResinVeinBlock) RESIN_VEIN_MAPPING.get().getOrDefault(block, AVPBlocks.RESIN_VEIN.get());
    }

    public static ParticleOptions getParticleFor(Acid acid) {
        return switch (acid) {
            case Acid irradiatedAcid when irradiatedAcid.isIrradiated() -> AVPParticleTypes.IRRADIATED_ACID.get();
            case Acid netherAcid when netherAcid.isNetherAfflicted() -> AVPParticleTypes.BLUE_ACID.get();
            default -> AVPParticleTypes.ACID.get();
        };
    }

    public static BlockState getResinNodeForType(Alien alien) {
        return switch (alien) {
            case Alien netherAlien when netherAlien.isNetherAfflicted() ->
                AVPBlocks.NETHER_RESIN_NODE.get().defaultBlockState();
            case Alien aberrantAlien when aberrantAlien.isAberrant() ->
                AVPBlocks.ABERRANT_RESIN_NODE.get().defaultBlockState();
            case Alien irradiatedAlien when irradiatedAlien.isIrradiated() ->
                AVPBlocks.IRRADIATED_RESIN_NODE.get().defaultBlockState();
            default -> AVPBlocks.RESIN_NODE.get().defaultBlockState();
        };
    }

    public static Item getResinBallForType(BlockState blockState) {
        return RESIN_BALL_MAPPING.get().getOrDefault(blockState.getBlock(), AVPItems.RESIN_BALL.get());
    }

    public static EntityType<?> getOvamorphTypeFor(Queen queen, boolean isRoyal) {
        return switch (queen) {
            case Queen netherQueen when netherQueen.isNetherAfflicted() && isRoyal ->
                AVPEntityTypes.ROYAL_NETHER_OVAMORPH.get();
            case Queen netherQueen when netherQueen.isNetherAfflicted() -> AVPEntityTypes.NETHER_OVAMORPH.get();
            case Queen aberrantQueen when aberrantQueen.isAberrant() && isRoyal ->
                AVPEntityTypes.ROYAL_ABERRANT_OVAMORPH.get();
            case Queen aberrantQueen when aberrantQueen.isAberrant() -> AVPEntityTypes.ABERRANT_OVAMORPH.get();
            default -> isRoyal ? AVPEntityTypes.ROYAL_OVAMORPH.get() : AVPEntityTypes.OVAMORPH.get();
        };
    }
}
