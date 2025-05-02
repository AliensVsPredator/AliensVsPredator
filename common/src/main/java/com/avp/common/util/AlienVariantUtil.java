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

import com.avp.common.block.TempAVPBlocks;
import com.avp.common.block.resin.ResinVeinBlock;
import com.avp.common.entity.acid.Acid;
import com.avp.common.entity.living.alien.Alien;
import com.avp.common.entity.living.alien.xenomorph.queen.Queen;
import com.avp.common.entity.type.TempAVPEntityTypes;
import com.avp.common.item.TempAVPItems;

public class AlienVariantUtil {

    // Made lazy so that the block/item access here doesn't cause NeoForge to crash during startup.
    private static final Lazy<Map<Block, Item>> RESIN_BALL_MAPPING = Lazy.of(
        () -> Map.ofEntries(
            Map.entry(TempAVPBlocks.ABERRANT_RESIN_NODE.get(), TempAVPItems.ABERRANT_RESIN_BALL.get()),
            Map.entry(TempAVPBlocks.ABERRANT_RESIN.get(), TempAVPItems.ABERRANT_RESIN_BALL.get()),
            Map.entry(TempAVPBlocks.ABERRANT_RESIN_WEB.get(), TempAVPItems.ABERRANT_RESIN_BALL.get()),
            Map.entry(TempAVPBlocks.ABERRANT_RESIN_VEIN.get(), TempAVPItems.ABERRANT_RESIN_BALL.get()),
            Map.entry(TempAVPBlocks.IRRADIATED_RESIN_NODE.get(), TempAVPItems.IRRADIATED_RESIN_BALL.get()),
            Map.entry(TempAVPBlocks.IRRADIATED_RESIN.get(), TempAVPItems.IRRADIATED_RESIN_BALL.get()),
            Map.entry(TempAVPBlocks.IRRADIATED_RESIN_WEB.get(), TempAVPItems.IRRADIATED_RESIN_BALL.get()),
            Map.entry(TempAVPBlocks.IRRADIATED_RESIN_VEIN.get(), TempAVPItems.IRRADIATED_RESIN_BALL.get()),
            Map.entry(TempAVPBlocks.NETHER_RESIN_NODE.get(), TempAVPItems.NETHER_RESIN_BALL.get()),
            Map.entry(TempAVPBlocks.NETHER_RESIN.get(), TempAVPItems.NETHER_RESIN_BALL.get()),
            Map.entry(TempAVPBlocks.NETHER_RESIN_WEB.get(), TempAVPItems.NETHER_RESIN_BALL.get()),
            Map.entry(TempAVPBlocks.NETHER_RESIN_VEIN.get(), TempAVPItems.NETHER_RESIN_BALL.get())
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
            case Alien netherAlien when netherAlien.isNetherAfflicted() -> TempAVPItems.NETHER_RESIN_BALL.get();
            case Alien aberrantAlien when aberrantAlien.isAberrant() -> TempAVPItems.ABERRANT_RESIN_BALL.get();
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
        // FIXME:
        return null;
        // return switch (acid) {
        // case Acid irradiatedAcid when irradiatedAcid.isIrradiated() -> AVPParticleTypes.IRRADIATED_ACID;
        // case Acid netherAcid when netherAcid.isNetherAfflicted() -> AVPParticleTypes.BLUE_ACID;
        // default -> AVPParticleTypes.ACID;
        // };
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
                TempAVPEntityTypes.ROYAL_NETHER_OVAMORPH.get();
            case Queen netherQueen when netherQueen.isNetherAfflicted() -> TempAVPEntityTypes.NETHER_OVAMORPH.get();
            case Queen aberrantQueen when aberrantQueen.isAberrant() && isRoyal ->
                TempAVPEntityTypes.ROYAL_ABERRANT_OVAMORPH.get();
            case Queen aberrantQueen when aberrantQueen.isAberrant() -> TempAVPEntityTypes.ABERRANT_OVAMORPH.get();
            default -> isRoyal ? TempAVPEntityTypes.ROYAL_OVAMORPH.get() : TempAVPEntityTypes.OVAMORPH.get();
        };
    }
}
