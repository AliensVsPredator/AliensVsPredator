package com.blib.internal.client.territory.compat.xaero;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;
import xaero.map.WorldMapSession;
import xaero.map.highlight.ChunkHighlighter;
import xaero.map.highlight.HighlighterRegistry;

import java.awt.Color;
import java.util.List;

import com.blib.api.common.territory.v1.Claimant;
import com.blib.internal.client.territory.ClientTerritoryCache;

@ApiStatus.Internal
public class BLibChunkHighlighter extends ChunkHighlighter {

    private static final int FILL_OPACITY = 100;

    private static final int BORDER_OPACITY = 200;

    public BLibChunkHighlighter() {
        super(true);
    }

    public static void register(HighlighterRegistry registry) {
        registry.register(new BLibChunkHighlighter());
    }

    public static void invalidateChunk(int chunkX, int chunkZ) {
        Minecraft.getInstance().tell(() -> {
            var session = WorldMapSession.getCurrentSession();

            if (session == null) {
                return;
            }

            var mapWorld = session.getMapProcessor().getMapWorld();

            if (mapWorld == null) {
                return;
            }

            var dimension = mapWorld.getCurrentDimension();

            if (dimension == null) {
                return;
            }

            var regionX = chunkX >> 5;
            var regionZ = chunkZ >> 5;

            dimension.getHighlightHandler().clearCachedHash(regionX, regionZ);
        });
    }

    @Override
    public boolean regionHasHighlights(ResourceKey<Level> dimension, int regionX, int regionZ) {
        var cache = ClientTerritoryCache.INSTANCE;
        var startX = regionX * 32;
        var startZ = regionZ * 32;

        for (var x = startX; x < startX + 32; x++) {
            for (var z = startZ; z < startZ + 32; z++) {
                if (cache.isClaimed(new ChunkPos(x, z))) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean chunkIsHighlit(ResourceKey<Level> dimension, int x, int z) {
        return ClientTerritoryCache.INSTANCE.isClaimed(new ChunkPos(x, z));
    }

    @Override
    protected int[] getColors(ResourceKey<Level> dimension, int x, int z) {
        var cache = ClientTerritoryCache.INSTANCE;
        var pos = new ChunkPos(x, z);
        var claimants = cache.getClaimants(pos);

        if (claimants.isEmpty()) {
            return null;
        }

        var primaryClaimant = claimants.getFirst();
        var rgb = colorFromClaimant(primaryClaimant);
        var packed = packColor(rgb);
        var fill = (packed & 0xFFFFFF00) | FILL_OPACITY;
        var edge = (packed & 0xFFFFFF00) | BORDER_OPACITY;

        resultStore[0] = fill;
        resultStore[1] = sameOwner(cache, x, z - 1, primaryClaimant) ? fill : edge;
        resultStore[2] = sameOwner(cache, x + 1, z, primaryClaimant) ? fill : edge;
        resultStore[3] = sameOwner(cache, x, z + 1, primaryClaimant) ? fill : edge;
        resultStore[4] = sameOwner(cache, x - 1, z, primaryClaimant) ? fill : edge;

        return resultStore;
    }

    @Override
    public int calculateRegionHash(ResourceKey<Level> dimension, int regionX, int regionZ) {
        var cache = ClientTerritoryCache.INSTANCE;
        var startX = regionX * 32;
        var startZ = regionZ * 32;
        var hash = 0L;

        for (var x = startX; x < startX + 32; x++) {
            for (var z = startZ; z < startZ + 32; z++) {
                var claimants = cache.getClaimants(new ChunkPos(x, z));

                for (var claimant : claimants) {
                    hash = hash * 37L + claimant.hashCode();
                }

                hash = hash * 37L;
            }
        }

        return (int) (hash >> 32) * 37 + (int) (hash & 0xFFFFFFFFL);
    }

    @Override
    public Component getChunkHighlightSubtleTooltip(ResourceKey<Level> dimension, int x, int z) {
        var claimants = ClientTerritoryCache.INSTANCE.getClaimants(new ChunkPos(x, z));

        if (claimants.isEmpty()) {
            return Component.empty();
        }

        var claimant = claimants.getFirst();

        return switch (claimant) {
            case Claimant.FactionClaimant factionClaimant ->
                Component.literal(factionClaimant.factionId().toString());
            case Claimant.EntityClaimant entityClaimant ->
                Component.literal(entityClaimant.entityId().toString());
        };
    }

    @Override
    public Component getChunkHighlightBluntTooltip(ResourceKey<Level> dimension, int x, int z) {
        return null;
    }

    @Override
    public void addMinimapBlockHighlightTooltips(
        List<Component> list,
        ResourceKey<Level> dimension,
        int x,
        int z,
        int width
    ) {}

    private static boolean sameOwner(ClientTerritoryCache cache, int x, int z, Claimant claimant) {
        var neighborClaimants = cache.getClaimants(new ChunkPos(x, z));

        if (neighborClaimants.isEmpty()) {
            return false;
        }

        return neighborClaimants.getFirst().equals(claimant);
    }

    private static int colorFromClaimant(Claimant claimant) {
        var hash = claimant.hashCode();
        var hue = (hash & 0x7FFFFFFF) % 360 / 360.0f;

        return Color.HSBtoRGB(hue, 0.7f, 0.9f);
    }

    private static int packColor(int rgb) {
        var red = (rgb >> 16) & 0xFF;
        var green = (rgb >> 8) & 0xFF;
        var blue = rgb & 0xFF;

        return (blue << 24) | (green << 16) | (red << 8);
    }
}
