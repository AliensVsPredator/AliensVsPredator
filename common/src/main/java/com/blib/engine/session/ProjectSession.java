package com.blib.engine.session;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

import com.blib.internal.common.storage.ProjectInfo;
import com.blib.mod.common.network.packet.S2CProjectOpResultPayload;

/**
 * Client-side per-engine-session state for the active BLib project: which project the workspace currently has open, and
 * the cached list of available projects most recently received from the server. Cleared when the engine workspace
 * closes (so reopening lands on the picker again — projects are not persisted across sessions per the project plan).
 * <p>
 * Single-player only — the engine is gated to integrated-server worlds, so a session-scoped global is the right
 * granularity. The {@link com.blib.engine.ui.ProjectPickerScreen} (Phase 4) writes into this on Open; the workspace
 * panels read from it for status bar display + outgoing edit packets.
 */
@ApiStatus.Internal
public final class ProjectSession {

    private static @Nullable ProjectInfo activeProject;

    private static List<ProjectInfo> availableProjects = List.of();

    /**
     * Callback the {@code ProjectPickerScreen} sets while it's the active screen, so {@code BLibClientListener} can
     * forward {@link S2CProjectOpResultPayload} instances to it without holding a hard reference. Cleared by the picker
     * on {@code removed()} so a stray late-arriving op result doesn't drive UI on a screen that no longer exists.
     */
    private static @Nullable Consumer<S2CProjectOpResultPayload> opResultCallback;

    private ProjectSession() {}

    public static @Nullable ProjectInfo activeProject() {
        return activeProject;
    }

    public static String activeProjectName() {
        return activeProject == null ? "" : activeProject.name();
    }

    public static void setActiveProject(@Nullable ProjectInfo project) {
        activeProject = project;
    }

    public static List<ProjectInfo> availableProjects() {
        return availableProjects;
    }

    public static void setAvailableProjects(List<ProjectInfo> projects) {
        availableProjects = projects == null ? List.of() : List.copyOf(projects);
    }

    public static void setOpResultCallback(@Nullable Consumer<S2CProjectOpResultPayload> cb) {
        opResultCallback = cb;
    }

    public static void deliverOpResult(S2CProjectOpResultPayload result) {
        var cb = opResultCallback;
        if (cb != null) {
            cb.accept(result);
        }
    }

    /** Reset all session state. Called by {@code EngineWorkspaceScreen.removed()}. */
    public static void clear() {
        activeProject = null;
        availableProjects = List.of();
        opResultCallback = null;
    }
}
