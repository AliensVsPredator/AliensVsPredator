package com.blib.api.client.render.v1;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import com.blib.api.client.animation.v1.animator.AzAnimator;
import com.blib.api.client.render.v1.item.pipeline.AzItemRendererPipelineContext;
import com.blib.internal.client.model.AzBone;
import com.blib.internal.client.model.GeoCube;
import com.blib.internal.client.model.GeoQuad;
import com.blib.internal.client.render.util.RenderUtil;

public class AzModelRenderer<K, T> {

    private final Matrix4f poseStateCache = new Matrix4f();

    private final Vector3f normalScratch = new Vector3f();

    private final AzRendererPipeline<K, T> rendererPipeline;

    protected final AzLayerRenderer<K, T> layerRenderer;

    public AzModelRenderer(AzRendererPipeline<K, T> rendererPipeline, AzLayerRenderer<K, T> layerRenderer) {
        this.layerRenderer = layerRenderer;
        this.rendererPipeline = rendererPipeline;
    }

    protected void render(AzRendererPipelineContext<K, T> context, boolean isReRender) {
        var animatable = context.animatable();
        var model = context.bakedModel();

        rendererPipeline.updateAnimatedTextureFrame(animatable);

        for (var bone : model.getTopLevelBones()) {
            renderRecursively(context, bone, isReRender);
        }

        var config = rendererPipeline.config();
        config.renderEntry(context);
    }

    protected void renderRecursively(AzRendererPipelineContext<K, T> context, AzBone bone, boolean isReRender) {
        var buffer = context.vertexConsumer();
        var bufferSource = context.multiBufferSource();
        var poseStack = context.poseStack();

        poseStack.pushPose();
        RenderUtil.prepMatrixForBone(poseStack, bone);

        context.setVertexConsumer(getOrRefreshRenderBuffer(isReRender, context, bone));

        if (
            !boneRenderOverride(
                poseStack,
                bone,
                bufferSource,
                buffer,
                context.partialTick(),
                context.packedLight(),
                context.packedOverlay(),
                context.renderColor()
            )
        )
            renderCubesOfBone(context, bone);

        if (!isReRender) {
            layerRenderer.applyRenderLayersForBone(context, bone);
        }

        renderChildBones(context, bone, isReRender);
        poseStack.popPose();
    }

    protected void renderCubesOfBone(AzRendererPipelineContext<K, T> context, AzBone bone) {
        if (bone.isHidden()) {
            return;
        }

        var poseStack = context.poseStack();

        for (var cube : bone.getCubes()) {
            poseStack.pushPose();

            renderCube(context, cube);

            poseStack.popPose();
        }
    }

    protected void renderChildBones(AzRendererPipelineContext<K, T> context, AzBone bone, boolean isReRender) {
        if (bone.isHidingChildren())
            return;

        for (var childBone : bone.getChildBones()) {
            renderRecursively(context, childBone, isReRender);
        }
    }

    protected void renderCube(AzRendererPipelineContext<K, T> context, GeoCube cube) {
        var poseStack = context.poseStack();

        RenderUtil.translateToPivotPoint(poseStack, cube);
        RenderUtil.rotateMatrixAroundCube(poseStack, cube);
        RenderUtil.translateAwayFromPivotPoint(poseStack, cube);

        var normalisedPoseState = poseStack.last().normal();
        var poseState = poseStateCache.set(poseStack.last().pose());

        for (var quad : cube.quads()) {
            if (quad == null) {
                continue;
            }

            normalScratch.set(quad.normal());
            normalisedPoseState.transform(normalScratch);
            var normal = normalScratch;

            RenderUtil.fixInvertedFlatCube(cube, normal);
            createVerticesOfQuad(context, quad, poseState, normal);
        }
    }

    protected void createVerticesOfQuad(
        AzRendererPipelineContext<K, T> context,
        GeoQuad quad,
        Matrix4f poseState,
        Vector3f normal
    ) {
        var buffer = context.vertexConsumer();
        var color = context.renderColor();
        var config = rendererPipeline.config();
        var packedOverlay = context.packedOverlay();
        var packedLight = context.packedLight();
        var boneTextureSize = context.computeTextureSize(context.getTextureOverride());
        var entityTextureSize = context.computeTextureSize(
            config.textureLocation(context.currentEntity(), context.animatable())
        );

        for (var vertex : quad.vertices()) {
            var position = vertex.position();
            var vector4f = poseState.transform(new Vector4f(position.x(), position.y(), position.z(), 1.0f));
            if (context.getTextureOverride() != null && boneTextureSize != null && entityTextureSize != null) {
                var texU = (vertex.texU() * entityTextureSize.firstInt()) / boneTextureSize.firstInt();
                var texV = (vertex.texV() * entityTextureSize.secondInt()) / boneTextureSize.secondInt();
                context.vertexConsumer()
                    .addVertex(
                        vector4f.x(),
                        vector4f.y(),
                        vector4f.z(),
                        -1,
                        texU,
                        texV,
                        context.packedOverlay(),
                        context.packedLight(),
                        normal.x(),
                        normal.y(),
                        normal.z()
                    );
            } else {
                buffer.addVertex(
                    vector4f.x(),
                    vector4f.y(),
                    vector4f.z(),
                    color,
                    vertex.texU(),
                    vertex.texV(),
                    packedOverlay,
                    packedLight,
                    normal.x(),
                    normal.y(),
                    normal.z()
                );
            }
        }
    }

    public boolean boneRenderOverride(
        PoseStack poseStack,
        AzBone bone,
        MultiBufferSource bufferSource,
        VertexConsumer buffer,
        float partialTick,
        int packedLight,
        int packedOverlay,
        int colour
    ) {
        return false;
    }

    public void handleAnimation(AzAnimator<?, T> animator, T animatable, float partialTick) {
        animator.animate(animatable, partialTick);
    }

    public VertexConsumer getOrRefreshBufferRenderType(
        AzItemRendererPipelineContext context,
        AzBone bone,
        RenderType renderType
    ) {
        var currentBuffer = context.multiBufferSource().getBuffer(renderType);
        var bufferSource = context.multiBufferSource();

        return switch (currentBuffer) {
            case BufferBuilder builder when isBufferInactive(builder) -> bufferSource.getBuffer(renderType);
            case OutlineBufferSource.EntityOutlineGenerator outline when needsBufferRefresh(outline.delegate()) ->
                new OutlineBufferSource.EntityOutlineGenerator(bufferSource.getBuffer(renderType), outline.color());
            case VertexMultiConsumer.Double pair when needsBufferRefresh(pair.first) || needsBufferRefresh(
                pair.second
            ) ->
                new VertexMultiConsumer.Double(
                    needsBufferRefresh(pair.first) ? bufferSource.getBuffer(renderType) : pair.first,
                    needsBufferRefresh(pair.second) ? bufferSource.getBuffer(renderType) : pair.second
                );
            default -> currentBuffer;
        };
    }

    public VertexConsumer getOrRefreshRenderBuffer(
        boolean isReRender,
        AzRendererPipelineContext<K, T> context,
        AzBone bone
    ) {
        var config = rendererPipeline.config();
        var currentBuffer = context.vertexConsumer();
        var bufferSource = context.multiBufferSource();
        var renderType = context.renderType();

        if (config.boneTextureOverrideProvider(bone) != null) {
            context.setTextureOverride(config.boneTextureOverrideProvider(bone));
        }

        var texture = config.boneTextureOverrideProvider(bone);

        var renderTypeOverride = config.boneRenderTypeOverrideProvider(bone);

        if (texture != null && renderTypeOverride == null) {
            renderTypeOverride = context.getDefaultRenderType(
                context.animatable(),
                texture,
                bufferSource,
                context.partialTick(),
                config.getRenderType(context.currentEntity(), context.animatable()),
                config.alpha(context.animatable())
            );
        }

        if (renderTypeOverride != null) {
            currentBuffer = context.multiBufferSource().getBuffer(renderTypeOverride);
        }

        if (isReRender) {
            return currentBuffer;
        }

        return switch (currentBuffer) {
            case BufferBuilder builder when isBufferInactive(builder) -> bufferSource.getBuffer(renderType);
            case OutlineBufferSource.EntityOutlineGenerator outline when needsBufferRefresh(outline.delegate()) ->
                new OutlineBufferSource.EntityOutlineGenerator(bufferSource.getBuffer(renderType), outline.color());
            case VertexMultiConsumer.Double pair when needsBufferRefresh(pair.first) || needsBufferRefresh(
                pair.second
            ) ->
                new VertexMultiConsumer.Double(
                    needsBufferRefresh(pair.first) ? bufferSource.getBuffer(renderType) : pair.first,
                    needsBufferRefresh(pair.second) ? bufferSource.getBuffer(renderType) : pair.second
                );
            default -> currentBuffer;
        };
    }

    protected boolean needsBufferRefresh(VertexConsumer buffer) {
        return switch (buffer) {
            case BufferBuilder builder -> isBufferInactive(builder);
            case OutlineBufferSource.EntityOutlineGenerator outline -> needsBufferRefresh(outline.delegate());
            case VertexMultiConsumer.Double pair ->
                needsBufferRefresh(pair.first) || needsBufferRefresh(pair.second);
            default -> false;
        };
    }

    protected boolean isBufferInactive(BufferBuilder builder) {
        return !builder.building;
    }
}
