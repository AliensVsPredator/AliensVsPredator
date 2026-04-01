package com.blib.internal.client.territory.compat.xaero;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;
import xaero.map.WorldMapSession;
import xaero.map.highlight.ChunkHighlighter;
import xaero.map.highlight.HighlighterRegistry;

import java.awt.Color;
import java.util.List;

import com.blib.internal.client.faction.ClientFactionCache;
import com.blib.internal.client.territory.ClientTerritoryCache;

@ApiStatus.Internal
public class BLibChunkHighlighter extends ChunkHighlighter {

    private static final int FILL_OPACITY = 100;

    private static final int BORDER_OPACITY = 200;

    private static final int CONTESTED_COLOR = packColor(0xFFFF00);

    public BLibChunkHighlighter() {
        super(true);
    }

    public static void register(HighlighterRegistry registry) {
        registry.register(new BLibChunkHighlighter());
    }

    public static void invalidateAll() {
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

            dimension.getHighlightHandler().clearCachedHashes();
        });
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
        var factionIds = cache.getFactionIds(pos);

        if (factionIds.isEmpty()) {
            return null;
        }

        if (factionIds.size() > 1) {
            var fill = (CONTESTED_COLOR & 0xFFFFFF00) | FILL_OPACITY;
            var edge = (CONTESTED_COLOR & 0xFFFFFF00) | BORDER_OPACITY;

            resultStore[0] = fill;
            resultStore[1] = edge;
            resultStore[2] = edge;
            resultStore[3] = edge;
            resultStore[4] = edge;

            return resultStore;
        }

        var primaryFaction = factionIds.getFirst();
        var rgb = colorFromFaction(primaryFaction);
        var packed = packColor(rgb);
        var fill = (packed & 0xFFFFFF00) | FILL_OPACITY;
        var edge = (packed & 0xFFFFFF00) | BORDER_OPACITY;

        resultStore[0] = fill;
        resultStore[1] = sameOwner(cache, x, z - 1, primaryFaction) ? fill : edge;
        resultStore[2] = sameOwner(cache, x + 1, z, primaryFaction) ? fill : edge;
        resultStore[3] = sameOwner(cache, x, z + 1, primaryFaction) ? fill : edge;
        resultStore[4] = sameOwner(cache, x - 1, z, primaryFaction) ? fill : edge;

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
                var factionIds = cache.getFactionIds(new ChunkPos(x, z));

                for (var factionId : factionIds) {
                    hash = hash * 37L + factionId.hashCode();
                    hash = hash * 37L + colorFromFaction(factionId);
                }

                hash = hash * 37L;
            }
        }

        return (int) (hash >> 32) * 37 + (int) (hash & 0xFFFFFFFFL);
    }

    @Override
    public Component getChunkHighlightSubtleTooltip(ResourceKey<Level> dimension, int x, int z) {
        var factionIds = ClientTerritoryCache.INSTANCE.getFactionIds(new ChunkPos(x, z));

        if (factionIds.isEmpty()) {
            return Component.empty();
        }

        if (factionIds.size() > 1) {
            return Component.literal("CONTESTED");
        }

        return Component.literal(nameFromFaction(factionIds.getFirst()));
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

    private static boolean sameOwner(ClientTerritoryCache cache, int x, int z, ResourceLocation factionId) {
        var neighborFactions = cache.getFactionIds(new ChunkPos(x, z));

        if (neighborFactions.isEmpty()) {
            return false;
        }

        return neighborFactions.getFirst().equals(factionId);
    }

    private static String nameFromFaction(ResourceLocation factionId) {
        var metadata = ClientFactionCache.INSTANCE.get(factionId);

        if (metadata != null) {
            return metadata.name();
        }

        return factionId.toString();
    }

    private static int colorFromFaction(ResourceLocation factionId) {
        var metadata = ClientFactionCache.INSTANCE.get(factionId);

        if (metadata != null) {
            return metadata.color();
        }

        var hash = factionId.hashCode();
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
