package com.blib.engine.jigsaw;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import com.blib.api.common.worldgen.v1.StructureTemplateAccessor;
import com.blib.engine.jigsaw.placement.TransformedTemplateCache;

/**
 * GPU-resident bake of a jigsaw piece's preview mesh. Each (template, rotation, mirror) combination is rendered once
 * into per-{@link RenderType} {@link VertexBuffer}s; subsequent frames bind and draw those buffers with a translated
 * model-view matrix instead of re-walking the block model for every block in the template.
 * <p>
 * For a 32³ piece this turns ~32 000 per-frame {@code renderSingleBlock} calls (each walking the block model + writing
 * ~24 vertices) into one bind + drawWithShader per render type (typically 1-3 types). The vertex generation only
 * happens on cache build — usually when the user first holds a piece, then once more on each rotation / mirror change.
 * <p>
 * GPU resource lifecycle: each {@link BakedMesh} owns its {@link VertexBuffer}s and must be {@code close()}'d to free
 * them. {@link #invalidate} closes every cached mesh; callers wire it to workspace close and datapack reload
 * ({@link JigsawPieceLibrary#invalidate}) so we don't accumulate orphaned GPU buffers over long sessions or after the
 * underlying templates have been reloaded out from under us.
 */
@ApiStatus.Internal
public final class JigsawPreviewMeshCache {

    /**
     * Initial capacity for the per-RenderType {@link ByteBufferBuilder} used during bake. Sized to fit a small-medium
     * piece without resizing; bigger pieces grow the buffer organically. The builder is closed after upload, so this is
     * only transient native memory during the build call.
     */
    private static final int INITIAL_BUILDER_CAPACITY = 256 * 1024;

    /**
     * One cached {@link BlockEntity} instance + its template-local position. Stored on the {@link BakedMesh} so the BE
     * render pass can iterate without re-instantiating BEs per frame — BE construction goes through the registry and
     * per-state component decoding, which we'd rather not pay 60×/sec for a static preview.
     */
    public record BakedBlockEntity(
        BlockPos localPos,
        BlockEntity blockEntity
    ) {}

    public static final class BakedMesh implements AutoCloseable {

        private final Map<RenderType, VertexBuffer> buffers;

        private final List<BakedBlockEntity> blockEntities;

        BakedMesh(Map<RenderType, VertexBuffer> buffers, List<BakedBlockEntity> blockEntities) {
            this.buffers = buffers;
            this.blockEntities = blockEntities;
        }

        public boolean isEmpty() {
            return buffers.isEmpty() && blockEntities.isEmpty();
        }

        /**
         * Render every cached buffer with the given combined model-view (which should already include the anchor
         * translation relative to the camera) and projection. Iterates render types in insertion order — solid first,
         * then cutout, then translucent — matching vanilla's chunk render order so depth/blending behave the same way
         * as the un-batched code path did.
         */
        public void render(Matrix4f modelView, Matrix4f projection) {
            for (var entry : buffers.entrySet()) {
                var type = entry.getKey();
                var vbo = entry.getValue();
                type.setupRenderState();
                vbo.bind();
                vbo.drawWithShader(modelView, projection, RenderSystem.getShader());
                VertexBuffer.unbind();
                type.clearRenderState();
            }
        }

        /**
         * Render every cached block entity through the
         * {@link net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher}. The caller's {@code poseStack}
         * must already be translated to the placement anchor (camera-relative); this method pushes one local-position
         * translation per BE on top. Uses {@code renderItem} to bypass the dispatch's frustum-cull check, which would
         * falsely reject our BEs because their stored {@code worldPosition} is at the template-local origin, not the
         * actual world location they're being rendered at.
         */
        public void renderBlockEntities(PoseStack poseStack, MultiBufferSource bufferSource, float partialTick) {
            if (blockEntities.isEmpty()) {
                return;
            }
            var dispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();
            for (var entry : blockEntities) {
                var pos = entry.localPos();
                poseStack.pushPose();
                poseStack.translate(pos.getX(), pos.getY(), pos.getZ());
                dispatcher.renderItem(entry.blockEntity(), poseStack, bufferSource, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
                poseStack.popPose();
            }
        }

        @Override
        public void close() {
            for (var vbo : buffers.values()) {
                vbo.close();
            }
            buffers.clear();
            // BlockEntities are plain Java objects with no GPU resources; dropping the list lets them GC. Renderer
            // implementations sometimes cache things off the BE instance (banner pattern compiled buffer, etc.); those
            // are owned by the BE so they go with it.
            blockEntities.clear();
        }
    }

    private record Key(
        Rotation rotation,
        Mirror mirror
    ) {}

    private static final Map<StructureTemplate, Map<Key, BakedMesh>> CACHE = new WeakHashMap<>();

    private JigsawPreviewMeshCache() {}

    /**
     * Returns the baked mesh for this (template, rotation, mirror), building it on first request. Must be called from
     * the render thread; bake uses GL resources.
     */
    public static BakedMesh getOrBuild(StructureTemplate template, Rotation rotation, Mirror mirror) {
        var inner = CACHE.computeIfAbsent(template, k -> new HashMap<>());
        return inner.computeIfAbsent(new Key(rotation, mirror), k -> build(template, rotation, mirror));
    }

    private static BakedMesh build(StructureTemplate template, Rotation rotation, Mirror mirror) {
        var blocks = TransformedTemplateCache.get(template, rotation, mirror);
        if (blocks.isEmpty()) {
            return new BakedMesh(new LinkedHashMap<>(), List.of());
        }

        // Build a position-keyed map of every (non-void) block in the structure so the mesher can do neighbor-aware
        // face culling. The same scan tracks the y-extent for the BlockAndTintGetter's height accessors. Interior
        // faces hidden behind solid neighbors get culled; exterior faces (neighbor outside the map = AIR) render.
        var stateMap = new HashMap<BlockPos, BlockState>(blocks.size());
        var minY = Integer.MAX_VALUE;
        var maxY = Integer.MIN_VALUE;
        for (var block : blocks) {
            stateMap.put(block.localPos(), block.state());
            var y = block.localPos().getY();
            if (y < minY) {
                minY = y;
            }
            if (y > maxY) {
                maxY = y;
            }
        }
        // Section-aligned bounds so LevelHeightAccessor's section helpers don't trip on off-by-ones. The actual
        // values only matter for tesselateBlock's isOutsideBuildHeight check — getBrightness is overridden anyway.
        var minBuildHeight = (minY >> 4) << 4;
        var height = Math.max(16, ((maxY >> 4) << 4) + 16 - minBuildHeight);
        var fakeLevel = new StructureBlockAndTintGetter(stateMap, minBuildHeight, height);

        var blockRenderer = Minecraft.getInstance().getBlockRenderer();
        var capture = new CapturingMultiBufferSource();
        var pose = new PoseStack();
        // tesselateBlock seeds its RandomSource per block via state.getSeed(pos); a single reusable source is enough.
        var random = RandomSource.create();

        for (var block : blocks) {
            // Air contributes no geometry. Skip early to avoid the pose push/pop + model lookup overhead, since
            // TransformedTemplateCache retains air blocks for the collision scanner's benefit.
            if (block.state().isAir()) {
                continue;
            }
            var renderType = ItemBlockRenderTypes.getChunkRenderType(block.state());
            var consumer = capture.getBuffer(renderType);
            pose.pushPose();
            pose.translate(block.localPos().getX(), block.localPos().getY(), block.localPos().getZ());
            // checkSides = true is what enables neighbor-aware face culling against fakeLevel.
            blockRenderer.renderBatched(block.state(), block.localPos(), fakeLevel, pose, consumer, true, random);
            pose.popPose();
        }

        var vbos = new LinkedHashMap<RenderType, VertexBuffer>();
        try {
            var meshes = capture.finishMeshes();
            for (var entry : meshes.entrySet()) {
                var vbo = new VertexBuffer(VertexBuffer.Usage.STATIC);
                vbo.bind();
                // vbo.upload(MeshData) takes ownership of the mesh data and closes it on success or failure.
                vbo.upload(entry.getValue());
                vbos.put(entry.getKey(), vbo);
            }
            VertexBuffer.unbind();
        } finally {
            capture.releaseBuilders();
        }

        var blockEntities = buildBlockEntities(template, rotation, mirror);

        return new BakedMesh(vbos, blockEntities);
    }

    /**
     * Pre-instantiate one {@link BlockEntity} per palette block with non-null NBT, transformed by the placement's
     * rotation and mirror. The chunk-mesh path can't render BE-shaped blocks (chests, signs, banners, skulls, beds,
     * etc.) because vanilla flags them as {@link net.minecraft.world.level.block.RenderShape#ENTITYBLOCK_ANIMATED} and
     * routes their visible geometry through {@link net.minecraft.client.renderer.blockentity.BlockEntityRenderer}
     * instead of {@code tesselateBlock}. We mirror what {@link StructureTemplate#placeInWorld} does internally as of
     * 1.21: rotate/mirror the {@link BlockState} (which most facing-aware BE renderers read for orientation), then load
     * the BE from its captured NBT via {@link BlockEntity#loadStatic}. Vanilla no longer calls separate
     * {@code applyRotation}/{@code applyMirror} hooks on the BE in 1.21 — state rotation does the work.
     */
    private static List<BakedBlockEntity> buildBlockEntities(StructureTemplate template, Rotation rotation, Mirror mirror) {
        var palettes = ((StructureTemplateAccessor) template).blib$getPalettes();
        if (palettes == null || palettes.isEmpty()) {
            return List.of();
        }
        var mc = Minecraft.getInstance();
        if (mc.level == null) {
            return List.of();
        }
        var registries = mc.level.registryAccess();
        var out = new ArrayList<BakedBlockEntity>();
        var pivot = BlockPos.ZERO;
        for (var info : palettes.get(0).blocks()) {
            if (info.nbt() == null) {
                continue;
            }
            var transformedPos = StructureTemplate.transform(info.pos(), mirror, rotation, pivot);
            var transformedState = info.state().mirror(mirror).rotate(rotation);
            var blockEntity = BlockEntity.loadStatic(transformedPos, transformedState, info.nbt(), registries);
            if (blockEntity == null) {
                continue;
            }
            // Some BE renderers (BeaconRenderer is the notable case) query getLevel() and skip rendering when null.
            // Point at the actual world so those renderers don't early-out; queries against template-local coords
            // through this level pointer would land in unrelated chunks, but for visual rendering that's harmless.
            // Vanilla's placeInWorld also doesn't call any applyRotation/Mirror hooks on the BE in 1.21 — the
            // rotated/mirrored BlockState carries all the orientation that visible BE renderers read.
            blockEntity.setLevel(mc.level);
            out.add(new BakedBlockEntity(transformedPos, blockEntity));
        }
        return out;
    }

    /** Close every cached buffer and clear the cache. Wire to workspace close and datapack reload. */
    public static void invalidate() {
        for (var inner : CACHE.values()) {
            for (var mesh : inner.values()) {
                mesh.close();
            }
        }
        CACHE.clear();
    }

    /**
     * Capture-only {@link MultiBufferSource} — accumulates per-RenderType vertices into our own {@link BufferBuilder}s
     * and exposes the built {@link com.mojang.blaze3d.vertex.MeshData} via {@link #finishMeshes}. Differs from
     * vanilla's {@code MultiBufferSource.BufferSource} in that {@code endBatch} on the vanilla version <em>draws</em>
     * the mesh; we want to retain it for VBO upload instead.
     */
    private static final class CapturingMultiBufferSource implements MultiBufferSource {

        private final Map<RenderType, ByteBufferBuilder> byteBuilders = new HashMap<>();

        private final Map<RenderType, BufferBuilder> builders = new HashMap<>();

        @Override
        public VertexConsumer getBuffer(RenderType type) {
            var existing = builders.get(type);
            if (existing != null) {
                return existing;
            }
            var byteBuilder = new ByteBufferBuilder(INITIAL_BUILDER_CAPACITY);
            var builder = new BufferBuilder(byteBuilder, type.mode(), type.format());
            byteBuilders.put(type, byteBuilder);
            builders.put(type, builder);
            return builder;
        }

        Map<RenderType, MeshData> finishMeshes() {
            var out = new LinkedHashMap<RenderType, MeshData>();
            for (var entry : builders.entrySet()) {
                var meshData = entry.getValue().build();
                if (meshData != null) {
                    out.put(entry.getKey(), meshData);
                }
            }
            return out;
        }

        void releaseBuilders() {
            // ByteBufferBuilders hold native memory; release explicitly. The MeshData consumed by VertexBuffer.upload
            // is already closed by the upload call itself, so no double-close concern here.
            for (var bb : byteBuilders.values()) {
                bb.close();
            }
            byteBuilders.clear();
            builders.clear();
        }
    }
}
