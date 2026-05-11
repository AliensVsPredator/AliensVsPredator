package com.blib.engine.jigsaw;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * A {@link BlockAndTintGetter} that exposes a structure's own blocks "as the world", so vanilla's
 * {@link net.minecraft.client.renderer.block.ModelBlockRenderer#tesselateBlock} can perform neighbor-aware face culling
 * during a preview bake — the same culling the chunk mesher uses against the real world.
 * <p>
 * Positions outside the supplied {@code states} map return {@link Blocks#AIR}, which guarantees that <em>exterior</em>
 * faces of the structure render (because their neighbor is air = transparent), while <em>interior</em> faces hidden
 * behind solid neighbors get culled by the mesher exactly as they would in-world. For a solid-volume piece this cuts
 * face count from O(blocks × 6) to O(surface area), roughly proportional to the cube root of the volume.
 * <p>
 * Lighting and tint queries return neutral values (full bright, no tint) so the preview reads at consistent brightness
 * regardless of where the piece is held in the world — matching the previous {@code renderSingleBlock} path which fed
 * {@link net.minecraft.client.renderer.LightTexture#FULL_BRIGHT} and a flat tint.
 */
@ApiStatus.Internal
final class StructureBlockAndTintGetter implements BlockAndTintGetter {

    private final Map<BlockPos, BlockState> states;

    private final int minBuildHeight;

    private final int height;

    StructureBlockAndTintGetter(Map<BlockPos, BlockState> states, int minBuildHeight, int height) {
        this.states = states;
        this.minBuildHeight = minBuildHeight;
        this.height = height;
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        var state = states.get(pos);
        return state != null ? state : Blocks.AIR.defaultBlockState();
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return getBlockState(pos).getFluidState();
    }

    @Override
    @Nullable
    public BlockEntity getBlockEntity(BlockPos pos) {
        // The preview never instantiates real BlockEntities — the placement packet does that server-side after the
        // user confirms. Returning null skips entity-block rendering (chest lids, beds, etc.) in the preview, which
        // matches what the un-batched path produced too.
        return null;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getMinBuildHeight() {
        return minBuildHeight;
    }

    @Override
    public float getShade(Direction direction, boolean shade) {
        // Match vanilla's per-axis shading so the preview's lighting matches what the structure looks like once
        // placed (the chunk shader applies the same shade per face direction).
        return switch (direction) {
            case DOWN -> 0.5f;
            case UP -> 1.0f;
            case NORTH, SOUTH -> 0.8f;
            case EAST, WEST -> 0.6f;
        };
    }

    @Override
    public LevelLightEngine getLightEngine() {
        // tesselateBlock queries lighting via getBrightness / getRawBrightness (both overridden below) rather than
        // touching the engine directly, so this is effectively never called. Return the world's engine as a non-null
        // fallback in case some future MC path consults it; queries against template-local coords would land in
        // unrelated chunks but wouldn't crash.
        return Minecraft.getInstance().level.getLightEngine();
    }

    /**
     * Returns the per-resolver default tint — what the previous {@code renderSingleBlock} path effectively used via
     * {@code BlockColors.getColor(state, null, null, tintIndex)}. We deliberately ignore the world biome here so the
     * preview reads consistently regardless of where the piece is held: leaves stay leaf-green, grass stays
     * grass-green, water stays vanilla blue, even if the player is standing in a swamp / desert / mesa. Unknown
     * resolvers fall back to {@code -1} (no tint), matching vanilla's "no registered BlockColor" return.
     */
    @Override
    public int getBlockTint(BlockPos pos, ColorResolver colorResolver) {
        if (colorResolver == BiomeColors.GRASS_COLOR_RESOLVER) {
            return GrassColor.getDefaultColor();
        }
        if (colorResolver == BiomeColors.FOLIAGE_COLOR_RESOLVER) {
            return FoliageColor.getDefaultColor();
        }
        if (colorResolver == BiomeColors.WATER_COLOR_RESOLVER) {
            return DEFAULT_WATER_COLOR;
        }
        return -1;
    }

    /** Vanilla's default biome water color — used when the resolver lookup falls through to no-biome. */
    private static final int DEFAULT_WATER_COLOR = 0x3F76E4;

    @Override
    public int getBrightness(LightLayer lightType, BlockPos pos) {
        return 15;
    }

    @Override
    public int getRawBrightness(BlockPos pos, int amount) {
        return 15;
    }
}
