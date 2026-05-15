package com.blib.internal.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.debug.GoalSelectorDebugRenderer;
import net.minecraft.client.renderer.debug.LightSectionDebugRenderer;
import net.minecraft.client.renderer.debug.StructureRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.blib.engine.render.pipeline.EngineWorldPasses;
import com.blib.engine.render.pipeline.WorldRenderFrame;
import com.blib.engine.ui.EngineWorkspaceScreen;
import com.blib.engine.ui.panel.pathfinding.PathfindingDebugPanel;
import com.blib.mod.client.render.debug.PathfindingSearchDebugRenderer;
import com.blib.mod.common.property.BLibModProperties;
import com.blib.mod.common.property.BLibModPropertyAccess;

@Mixin(DebugRenderer.class)
public class MixinDebugRenderer {

    @Shadow
    @Final
    public DebugRenderer.SimpleDebugRenderer chunkBorderRenderer;

    @Shadow
    @Final
    public DebugRenderer.SimpleDebugRenderer chunkRenderer;

    @Shadow
    @Final
    public DebugRenderer.SimpleDebugRenderer collisionBoxRenderer;

    @Shadow
    @Final
    public GoalSelectorDebugRenderer goalSelectorRenderer;

    @Shadow
    @Final
    public DebugRenderer.SimpleDebugRenderer heightMapRenderer;

    @Shadow
    @Final
    public DebugRenderer.SimpleDebugRenderer lightDebugRenderer;

    @Shadow
    @Final
    public DebugRenderer.SimpleDebugRenderer neighborsUpdateRenderer;

    @Shadow
    @Final
    public LightSectionDebugRenderer skyLightSectionDebugRenderer;

    @Shadow
    @Final
    public DebugRenderer.SimpleDebugRenderer solidFaceRenderer;

    @Shadow
    @Final
    public StructureRenderer structureRenderer;

    @Shadow
    @Final
    public DebugRenderer.SimpleDebugRenderer waterDebugRenderer;

    @Shadow
    @Final
    public DebugRenderer.SimpleDebugRenderer worldGenAttemptRenderer;

    @Inject(method = "render", at = @At("RETURN"))
    public void render(
        PoseStack poseStack,
        MultiBufferSource.BufferSource bufferSource,
        double camX,
        double camY,
        double camZ,
        CallbackInfo ci
    ) {
        // Engine world-render pipeline. Pass order, gating, and per-pass implementations live in
        // {@link EngineWorldPasses}; this mixin entry is purely a dispatch site. Adding a new render stage is now a
        // single-line registration in {@code EngineWorldPasses.buildPipeline()} rather than search-and-edit here.
        EngineWorldPasses.pipeline().render(new WorldRenderFrame(poseStack, bufferSource, camX, camY, camZ));

        if (
            Minecraft.getInstance().screen instanceof EngineWorkspaceScreen workspace
                && workspace.layoutHasActivePanel(PathfindingDebugPanel.class)
        ) {
            PathfindingSearchDebugRenderer.INSTANCE.render(poseStack, bufferSource, camX, camY, camZ);
        }

        var access = BLibModPropertyAccess.INSTANCE;

        if (!access.get(BLibModProperties.Debug.Render.ENABLED)) {
            return;
        }

        if (access.get(BLibModProperties.Debug.Render.Goal.ENABLED)) {
            goalSelectorRenderer.render(poseStack, bufferSource, camX, camY, camZ);
        }

        if (access.get(BLibModProperties.Debug.Render.Water.ENABLED)) {
            waterDebugRenderer.render(poseStack, bufferSource, camX, camY, camZ);
        }

        if (access.get(BLibModProperties.Debug.Render.ChunkBorder.ENABLED)) {
            chunkBorderRenderer.render(poseStack, bufferSource, camX, camY, camZ);
        }

        if (access.get(BLibModProperties.Debug.Render.HeightMap.ENABLED)) {
            heightMapRenderer.render(poseStack, bufferSource, camX, camY, camZ);
        }

        if (access.get(BLibModProperties.Debug.Render.CollisionBox.ENABLED)) {
            collisionBoxRenderer.render(poseStack, bufferSource, camX, camY, camZ);
        }

        if (access.get(BLibModProperties.Debug.Render.Neighbors.ENABLED)) {
            neighborsUpdateRenderer.render(poseStack, bufferSource, camX, camY, camZ);
        }

        if (access.get(BLibModProperties.Debug.Render.Structures.ENABLED)) {
            structureRenderer.render(poseStack, bufferSource, camX, camY, camZ);
        }

        if (access.get(BLibModProperties.Debug.Render.Light.ENABLED)) {
            lightDebugRenderer.render(poseStack, bufferSource, camX, camY, camZ);
        }

        if (access.get(BLibModProperties.Debug.Render.WorldGenAttempt.ENABLED)) {
            worldGenAttemptRenderer.render(poseStack, bufferSource, camX, camY, camZ);
        }

        if (access.get(BLibModProperties.Debug.Render.SolidFace.ENABLED)) {
            solidFaceRenderer.render(poseStack, bufferSource, camX, camY, camZ);
        }

        if (access.get(BLibModProperties.Debug.Render.ChunkRender.ENABLED)) {
            chunkRenderer.render(poseStack, bufferSource, camX, camY, camZ);
        }

        if (access.get(BLibModProperties.Debug.Render.SkyLight.ENABLED)) {
            skyLightSectionDebugRenderer.render(poseStack, bufferSource, camX, camY, camZ);
        }
    }
}
