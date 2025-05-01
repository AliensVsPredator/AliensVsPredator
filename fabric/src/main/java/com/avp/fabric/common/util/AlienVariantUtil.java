package com.avp.fabric.common.util;

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

import com.avp.common.block.TempAVPBlocks;
import com.avp.common.item.TempAVPItems;
import com.avp.fabric.common.block.resin.ResinVeinBlock;
import com.avp.fabric.common.entity.acid.Acid;
import com.avp.fabric.common.entity.living.alien.Alien;
import com.avp.fabric.common.entity.living.alien.xenomorph.queen.Queen;
import com.avp.fabric.common.entity.type.AVPEntityTypes;
import com.avp.fabric.common.item.AVPItems;
import com.avp.fabric.common.particle.AVPParticleTypes;

public class AlienVariantUtil {

    // Made lazy so that the block/item access here doesn't cause NeoForge to crash during startup.
    private static final Lazy<Map<Block, Item>> RESIN_BALL_MAPPING = Lazy.of(
        () -> Map.ofEntries(
            Map.entry(TempAVPBlocks.ABERRANT_RESIN_NODE.get(), AVPItems.ABERRANT_RESIN_BALL),
            Map.entry(TempAVPBlocks.ABERRANT_RESIN.get(), AVPItems.ABERRANT_RESIN_BALL),
            Map.entry(TempAVPBlocks.ABERRANT_RESIN_WEB.get(), AVPItems.ABERRANT_RESIN_BALL),
            Map.entry(TempAVPBlocks.ABERRANT_RESIN_VEIN.get(), AVPItems.ABERRANT_RESIN_BALL),
            Map.entry(TempAVPBlocks.IRRADIATED_RESIN_NODE.get(), TempAVPItems.IRRADIATED_RESIN_BALL.get()),
            Map.entry(TempAVPBlocks.IRRADIATED_RESIN.get(), TempAVPItems.IRRADIATED_RESIN_BALL.get()),
            Map.entry(TempAVPBlocks.IRRADIATED_RESIN_WEB.get(), TempAVPItems.IRRADIATED_RESIN_BALL.get()),
            Map.entry(TempAVPBlocks.IRRADIATED_RESIN_VEIN.get(), TempAVPItems.IRRADIATED_RESIN_BALL.get()),
            Map.entry(TempAVPBlocks.NETHER_RESIN_NODE.get(), AVPItems.NETHER_RESIN_BALL),
            Map.entry(TempAVPBlocks.NETHER_RESIN.get(), AVPItems.NETHER_RESIN_BALL),
            Map.entry(TempAVPBlocks.NETHER_RESIN_WEB.get(), AVPItems.NETHER_RESIN_BALL),
            Map.entry(TempAVPBlocks.NETHER_RESIN_VEIN.get(), AVPItems.NETHER_RESIN_BALL)
        )
    );

    private static final Lazy<Map<Block, Block>> RESIN_VEIN_MAPPING = Lazy.of(
        () -> Map.ofEntries(
            Map.entry(TempAVPBlocks.NETHER_RESIN_NODE.get(), TempAVPBlocks.NETHER_RESIN_VEIN.get()),
            Map.entry(TempAVPBlocks.ABERRANT_RESIN_NODE.get(), TempAVPBlocks.ABERRANT_RESIN_VEIN.get()),
            Map.entry(TempAVPBlocks.IRRADIATED_RESIN_NODE.get(), TempAVPBlocks.IRRADIATED_RESIN_VEIN.get())
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
            case Alien netherAlien when netherAlien.isNetherAfflicted() -> AVPItems.NETHER_RESIN_BALL;
            case Alien aberrantAlien when aberrantAlien.isAberrant() -> AVPItems.ABERRANT_RESIN_BALL;
            case Alien irradiatedAlien when irradiatedAlien.isIrradiated() -> TempAVPItems.IRRADIATED_RESIN_BALL.get();
            default -> TempAVPItems.RESIN_BALL.get();
        };
    }

    public static Block getResinNodeFor(Alien alien) {
        return switch (alien) {
            case Alien netherAlien when netherAlien.isNetherAfflicted() -> TempAVPBlocks.NETHER_RESIN_NODE.get();
            case Alien aberrantAlien when aberrantAlien.isAberrant() -> TempAVPBlocks.ABERRANT_RESIN_NODE.get();
            case Alien irradiatedAlien when irradiatedAlien.isIrradiated() -> TempAVPBlocks.IRRADIATED_RESIN_NODE.get();
            default -> TempAVPBlocks.RESIN_NODE.get();
        };
    }

    public static ResinVeinBlock getResinVeinFor(Block block) {
        return (ResinVeinBlock) RESIN_VEIN_MAPPING.get().getOrDefault(block, TempAVPBlocks.RESIN_VEIN.get());
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
                TempAVPBlocks.NETHER_RESIN_NODE.get().defaultBlockState();
            case Alien aberrantAlien when aberrantAlien.isAberrant() ->
                TempAVPBlocks.ABERRANT_RESIN_NODE.get().defaultBlockState();
            case Alien irradiatedAlien when irradiatedAlien.isIrradiated() ->
                TempAVPBlocks.IRRADIATED_RESIN_NODE.get().defaultBlockState();
            default -> TempAVPBlocks.RESIN_NODE.get().defaultBlockState();
        };
    }

    public static Item getResinBallForType(BlockState blockState) {
        return RESIN_BALL_MAPPING.get().getOrDefault(blockState.getBlock(), TempAVPItems.RESIN_BALL.get());
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
