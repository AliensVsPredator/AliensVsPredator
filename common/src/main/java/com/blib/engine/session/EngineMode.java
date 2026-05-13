package com.blib.engine.session;

import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.api.BLibAPI;
import com.blib.engine.core.lifecycle.EngineSessionScope;

/**
 * Dev-only in-game editor mode. While active, the player camera is detached, player input is suppressed, the vanilla
 * HUD and first-person hand are hidden, and the screen is taken over by an editor HUD that lets you click-select
 * objects and manipulate them via 3D gizmos.
 * <p>
 * Toggled by {@code /blib engine}. Gated behind {@link BLibAPI#isDevelopmentEnvironment()} at the command-registration
 * site, so this class is never reachable from a shipped build.
 */
@ApiStatus.Internal
public final class EngineMode {

    private static final EngineMode INSTANCE = new EngineMode();

    /**
     * Volatile because the toggle command runs on the integrated-server thread while every reader (mixins, freecam
     * tick, HUD render) runs on the client thread. No races worth handling beyond visibility.
     */
    private volatile @Nullable EngineSession session;

    private volatile @Nullable EngineSessionScope sessionScope;

    /** Saved {@code Options.hideGui} value at engine entry, restored on exit. {@code hideGui} is session-only state. */
    private boolean prevHideGui;

    private EngineMode() {}

    public static EngineMode get() {
        return INSTANCE;
    }

    public boolean isActive() {
        return session != null;
    }

    public @Nullable EngineSession session() {
        return session;
    }

    /**
     * Active per-session scope. Resources that should live exactly for one engine activation register cleanup actions
     * on this scope (typically right after they're populated); on {@link #exit()} the scope unwinds them in LIFO order
     * so we don't have to maintain a parallel list of {@code .clear()} calls here. Returns {@code null} between
     * sessions.
     */
    public @Nullable EngineSessionScope sessionScope() {
        return sessionScope;
    }

    /**
     * Enter engine mode. Captures the player's current eye position/rotation as the freecam starting transform so the
     * camera doesn't snap when control flips over, and toggles {@code Options.hideGui} so the vanilla HUD disappears.
     * <p>
     * Every transient piece of state that lives for exactly one engine activation registers its cleanup on the session
     * scope here. The list runs LIFO on {@link #exit()}, so the workspace screen's {@code removed()} no longer needs to
     * repeat a parallel list of {@code Foo.clear()} calls — both lists used to drift out of sync whenever a new
     * singleton was added.
     */
    public void enter() {
        if (session != null) {
            return;
        }

        var mc = Minecraft.getInstance();
        var player = mc.player;

        if (player == null) {
            return;
        }

        prevHideGui = mc.options.hideGui;
        mc.options.hideGui = true;

        var eye = player.getEyePosition(1.0F);
        session = new EngineSession(eye.x, eye.y, eye.z, player.getYRot(), player.getXRot());
        var scope = new EngineSessionScope();

        // Register kernel services in the scope's ServiceContainer before anything else runs. The session holder is
        // set here too — listeners installed below call {@code EventBus.get()}, which routes through the holder, so it
        // has to be wired up first. Service instances are dropped on scope close (services.clear()), so a stale bus
        // can never leak into the next session.
        scope.services().put(com.blib.engine.runtime.EventBus.class, new com.blib.engine.runtime.EventBus());
        scope.services()
            .put(
                com.blib.engine.runtime.tool.ToolStateMachine.class,
                new com.blib.engine.runtime.tool.ToolStateMachine()
            );
        sessionScope = scope;
        com.blib.engine.runtime.EngineSessionHolder.set(scope);

        // Install the per-session tool-mutual-exclusion subscribers. Each tool's static singleton subscribes to
        // ToolChangedEvent so it can disarm itself when another tool becomes active — replacing the prior O(N²)
        // graph of {@code Foo.select() → Bar.clear()} cross-singleton calls. Subscribers belong to the session's
        // bus instance, which is dropped on close so they don't accumulate across sessions.
        com.blib.engine.jigsaw.JigsawPieceSelection.installToolListener();
        com.blib.engine.spawn.EntitySpawnSelection.installToolListener();
        com.blib.engine.domain.selection.volume.BlockSelection.installToolListener();
        com.blib.engine.territory.ClaimPaintTool.installToolListener();

        // Install the network adapters. Each adapter subscribes to a domain event type and translates it into the
        // matching C2S packet — the domain layer publishes events without importing packet types.
        com.blib.engine.net.BlockVolumeNetAdapter.install();

        // Wire the unified-gizmo adapters into GizmoRegistry. Phase-1: adapters are facades over the existing static
        // gizmo classes; the registry is now the active dispatch surface (e.g. {@code GizmoRegistry.clearAllHover()} on
        // session exit). Phase-2 moves each gizmo's static state into its adapter so the registry is the sole owner.
        com.blib.engine.tool.gizmo.GizmoRegistry
            .register(com.blib.engine.tool.gizmo.adapter.BlockSelectionScaleGizmoAdapter.INSTANCE);
        com.blib.engine.tool.gizmo.GizmoRegistry
            .register(com.blib.engine.tool.gizmo.adapter.EntityScaleGizmoAdapter.INSTANCE);
        scope.onClose(
            () -> com.blib.engine.tool.gizmo.GizmoRegistry
                .unregister(com.blib.engine.tool.gizmo.adapter.BlockSelectionScaleGizmoAdapter.INSTANCE)
        );
        scope.onClose(
            () -> com.blib.engine.tool.gizmo.GizmoRegistry
                .unregister(com.blib.engine.tool.gizmo.adapter.EntityScaleGizmoAdapter.INSTANCE)
        );

        // Session-scoped client caches that mirror server-authoritative state.
        scope.onClose(com.blib.engine.tag.TagStagingCache::clear);
        scope.onClose(com.blib.engine.jigsaw.ClientPlacedPieceRegistry::clear);
        scope.onClose(com.blib.engine.history.ClientActionHistory.INSTANCE::clear);
        scope.onClose(com.blib.engine.tag.TagDraftCache::clear);
        scope.onClose(com.blib.engine.tag.TagCatalogCache::clear);
        scope.onClose(com.blib.engine.tag.RegistryEntriesCache::clear);
        scope.onClose(com.blib.engine.jigsaw.ProjectDraftCache::clear);
        scope.onClose(com.blib.engine.projectcontents.ProjectContents::clear);
        scope.onClose(com.blib.internal.client.faction.ClientFactionDirectoryCache::clear);
        scope.onClose(com.blib.internal.client.faction.ClientFactionInspectionCache::clear);
        scope.onClose(com.blib.internal.client.faction.ClientFactionMembersCache::clear);
        scope.onClose(com.blib.internal.client.faction.ClientEntityFactionsCache::clear);

        // Selection / tool / picking state.
        scope.onClose(com.blib.engine.domain.selection.picking.SelectionManager::clear);
        scope.onClose(com.blib.engine.domain.selection.picking.EngineHoverProbe::clear);
        scope.onClose(com.blib.engine.domain.selection.volume.BlockSelection::clear);
        scope.onClose(com.blib.engine.jigsaw.JigsawPieceSelection::clear);
        scope.onClose(com.blib.engine.jigsaw.JigsawPoolSelection::clear);
        scope.onClose(com.blib.engine.spawn.EntitySpawnSelection::clear);
        scope.onClose(com.blib.engine.territory.ClaimPaintTool::deactivate);

        // Gizmo drag/hover state — a stray drag-in-progress at close shouldn't continue against fresh state on the
        // next engine open.
        scope.onClose(com.blib.engine.domain.selection.volume.BlockSelectionScaleGizmo::clear);
        scope.onClose(com.blib.engine.domain.selection.volume.BlockSelectionTranslateGizmo::clear);
        scope.onClose(com.blib.engine.domain.selection.volume.MoveBlocksGizmo::clear);
        scope.onClose(com.blib.engine.domain.selection.entity.EntityTranslateGizmo::clear);
        scope.onClose(com.blib.engine.domain.selection.entity.EntityScaleGizmo::clear);
        scope.onClose(
            () -> com.blib.engine.domain.selection.entity.EntityGizmoMode
                .set(com.blib.engine.domain.selection.entity.EntityGizmoMode.TRANSLATE)
        );
        scope.onClose(() -> com.blib.engine.gizmo.BLibGizmoState.setDrag(null));
        scope.onClose(() -> com.blib.engine.gizmo.BLibGizmoState.setLastRender(null));
        scope.onClose(() -> com.blib.engine.gizmo.BLibGizmoState.setPreviewRender(false));
        scope.onClose(() -> com.blib.engine.modeler.gizmo.ModelerGizmoState.setDrag(null));
        scope.onClose(() -> com.blib.engine.modeler.gizmo.ModelerGizmoState.setLastRender(null));

        // Jigsaw authoring caches that hold GPU vertex buffers — must release before the workspace exits.
        scope.onClose(com.blib.engine.jigsaw.JigsawPlacementCursor::clearViewportRect);
        scope.onClose(com.blib.engine.jigsaw.JigsawPieceThumbnailCache::clear);
        scope.onClose(com.blib.engine.jigsaw.placement.JigsawTemplateScanner::clear);
        scope.onClose(com.blib.engine.jigsaw.placement.JigsawPlacementFrameState::clear);
        scope.onClose(com.blib.engine.jigsaw.JigsawPieceLibrary::invalidate);
        scope.onClose(com.blib.engine.jigsaw.placement.JigsawPlacementOptions::reset);

        // Captured render-frame matrices referenced the engine's camera — drop them so the next open repopulates.
        scope.onClose(com.blib.engine.session.EngineCameraFrame::clear);

        // The tool state machine and event bus are now session-scoped instances registered above; they're dropped on
        // scope close, so no explicit reset/clear is needed here.

        // Request the server's PlacedPiece set for the current dimension so the client's hover / selection mirror is
        // populated for the very first frame of engine mode. The reply broadcasts to all engine-mode players, but in
        // practice this is singleplayer + dev so we don't care about ordering with other clients.
        com.blib.mod.BLib.MOD.networking()
            .sendToServer(
                new com.blib.mod.common.network.packet.C2SRequestPlacedPiecesPayload(player.level().dimension().location())
            );
    }

    /**
     * Leave engine mode. Force-restores all hijacked state regardless of session details — the next frame should look
     * indistinguishable from a never-entered state. If we exited from orbit mode (mouse released), re-grab so the
     * player isn't dropped back into gameplay with a free cursor.
     */
    public void exit() {
        if (session == null) {
            return;
        }

        var mc = Minecraft.getInstance();
        mc.options.hideGui = prevHideGui;

        if (mc.screen == null && !mc.mouseHandler.isMouseGrabbed()) {
            mc.mouseHandler.grabMouse();
        }

        // Tear down everything that registered on the session scope at enter() — currently the tag-staging cache, the
        // placed-piece mirror, and the action-history mirror. New session-scoped resources can register with
        // {@link EngineSessionScope#onClose} at their own initialization site and don't need to touch this method.
        var scope = sessionScope;
        if (scope != null) {
            scope.close();
        }
        sessionScope = null;
        session = null;
        com.blib.engine.runtime.EngineSessionHolder.set(null);
    }

    public void toggle() {
        if (isActive()) {
            exit();
        } else {
            enter();
        }
    }
}
